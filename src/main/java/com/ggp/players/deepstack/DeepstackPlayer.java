package com.ggp.players.deepstack;

import com.ggp.*;
import com.ggp.players.deepstack.debug.RPSListener;
import com.ggp.players.deepstack.utils.*;
import com.ggp.utils.PlayerHelpers;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class DeepstackPlayer implements IPlayer {
    public static class Factory implements IPlayerFactory {
        private int iterations;
        private IResolvingListener listener;

        public Factory(int iterations, IResolvingListener listener) {
            this.iterations = iterations;
            this.listener = listener;
        }

        @Override
        public IPlayer create(IGameDescription game, int role) {
            DeepstackPlayer ret = new DeepstackPlayer(role, game, iterations, null);
            if (listener != null) ret.registerResolvingListener(listener);
            if (game.getClass() == com.ggp.games.RockPaperScissors.GameDescription.class) {
                ret.registerResolvingListener(new RPSListener(((com.ggp.games.RockPaperScissors.GameDescription) game).getExploitabilityEstimator()));
            }
            return ret;
        }
    }

    private int id;
    private int opponentId;
    private InformationSetRange range;
    private IInformationSet hiddenInfo;
    private HashMap<IInformationSet, Double> opponentCFV;
    private ICompleteInformationStateFactory cisFactory;
    private int iters;
    private int depthLimit = 2;
    private ICFVEstimator cfvEstimator;
    private NextTurnInfoTree ntit;
    private HashMap<IInformationSet, NextRangeTree> myISToNRT = new HashMap<>();
    private IGameDescription gameDesc;
    private PerceptSequence.Builder myPSBuilder = new PerceptSequence.Builder();
    private IAction myLastAction;
    private PerceptSequenceMap psMap;
    private Strategy lastCummulativeStrategy;
    private ArrayList<IResolvingListener> resolvingListeners = new ArrayList<>();

    public DeepstackPlayer(int id, IGameDescription gameDesc, int iterations, ICFVEstimator cfvEstimator) {
        this.id = id;
        this.opponentId = PlayerHelpers.getOpponentId(id);
        this.iters = iterations;
        range = new InformationSetRange();
        IInformationSet initialSet = gameDesc.getInitialInformationSet(id);
        hiddenInfo = initialSet;
        cisFactory = gameDesc.getCISFactory();
        this.cfvEstimator = cfvEstimator;
        IInformationSet initialOpponentSet = gameDesc.getInitialInformationSet(2 - id + 1);
        range.init(initialSet);
        myISToNRT.put(initialSet, new NextRangeTree());
        opponentCFV = new HashMap<>(1);
        opponentCFV.put(initialOpponentSet, 0d);
        this.gameDesc = gameDesc;
    }

    public void registerResolvingListener(IResolvingListener listener) {
        if (listener != null && !resolvingListeners.contains(listener))
            resolvingListeners.add(listener);
    }

    @Override
    public void init() {
        Resolver r = new Resolver();
        r.onEvent((listener, info) -> listener.resolvingStart(info));
        GameTreeTraversalTracker tracker = GameTreeTraversalTracker.createForInit(id, gameDesc.getInitialState());
        for (int i = 0; i < iters; ++i) {
            r.cfr(tracker, id, 0, 1, 1);
            r.onEvent((listener, info) -> listener.resolvingIterationEnd(info));
        }
        ntit = tracker.getNtit();
        myISToNRT.put(gameDesc.getInitialInformationSet(id), tracker.getNrt());
        psMap = tracker.getPsMap();
        r.onEvent((listener, info) -> listener.resolvingEnd(info));
        r.onEvent((listener, info) -> listener.initEnd(info));
    }

    @Override
    public int getRole() {
        return id;
    }

    private class Resolver {
        private Strategy strat = new Strategy();
        private Strategy nextStrat = new Strategy();
        private Strategy cumulativeStrat = new Strategy();
        private HashMap<IInformationSet, Double[]> regrets = new HashMap<>();
        private double[] regretsGadget = new double[opponentCFV.size() * 2];
        private double[] opponentRange = new double[opponentCFV.size()];

        public Resolver() {
            Arrays.fill(opponentRange, 1d/opponentRange.length);
        }

        private class CFRResult {
            public double player1CFV;
            public double player2CFV;

            public CFRResult(double player1CFV, double player2CFV) {
                this.player1CFV = player1CFV;
                this.player2CFV = player2CFV;
            }
        }

        private class ResolvingInfo implements IResolvingInfo {
            @Override
            public Strategy getUnnormalizedCumulativeStrategy() {
                return cumulativeStrat;
            }
        }

        protected void onEvent(BiConsumer<IResolvingListener, IResolvingInfo> call) {
            ResolvingInfo info = new ResolvingInfo();
            for (IResolvingListener listener: resolvingListeners) {
                call.accept(listener, info);
            }
        }

        public CFRResult cfr(GameTreeTraversalTracker tracker, int player, int depth, double p1, double p2) {
            ICompleteInformationState s = tracker.getCurrentState();
            onEvent((listener, info) -> listener.stateVisited(s, info));

            if (s.isTerminal()) {
                return new CFRResult(s.getPayoff(1), s.getPayoff(2));
            }

            // cutoff can only be made once i know opponentCFV for next turn i'll play
            if (tracker.wasMyNextTurnReached() && depth > depthLimit && cfvEstimator != null) {
                ICFVEstimator.EstimatorResult res = cfvEstimator.estimate(s, cumulativeStrat);
                return new CFRResult(res.player1CFV, res.player2CFV);
            }
            List<IAction> legalActions = s.getLegalActions();
            double rndProb = tracker.getRndProb();

            BiFunction<ICompleteInformationState, IAction, CFRResult> callCfr = (x, a) -> {
                double np1 = p1, np2 = p2;
                if (s.getActingPlayerId() == 1) {
                    np1 *= strat.getProbability(s.getInfoSetForActingPlayer(), a);
                } else if (s.getActingPlayerId() == 2) {
                    np2 *= strat.getProbability(s.getInfoSetForActingPlayer(), a);
                }
                return cfr(tracker.next(a), player, depth+1, np1, np2);
            };

            if (s.isRandomNode()) {
                CFRResult ret = new CFRResult(0,0);
                for (IAction a: legalActions) {
                    CFRResult res =  callCfr.apply(s, a);
                    ret.player1CFV += res.player1CFV;
                    ret.player2CFV += res.player2CFV;
                }
                // uniform probability for each action -> average
                ret.player1CFV /= legalActions.size();
                ret.player2CFV /= legalActions.size();
                return ret;
            }

            IInformationSet is = s.getInfoSetForActingPlayer();
            double[] cfv = new double[2];
            double[] actionCFV = new double[2*legalActions.size()];
            int i = 0;

            for (IAction a: legalActions) {
                double actionProb = strat.getProbability(is, a);
                CFRResult res = callCfr.apply(s, a);
                actionCFV[2*i] = res.player1CFV;
                actionCFV[2*i + 1] = res.player2CFV;
                for (int j = 0; j < 2; ++j) {
                    cfv[j] = cfv[j] + actionProb*actionCFV[2*i + j];
                }
                i++;
            }
            if (tracker.isMyNextTurnReached()) {
                double probWithoutOpponent = rndProb * PlayerHelpers.selectByPlayerId(id, p1, p2);
                tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), probWithoutOpponent * cfv[opponentId - 1]);
                tracker.getPsMap().add(tracker.getMyPerceptSequence(), tracker.getOpponentPerceptSequence());
                tracker.getNrt().add(tracker.getOpponentPerceptSequence(), s.getInfoSetForPlayer(id), tracker.getMyTopAction(), rndProb);
            }
            // TODO: is it ok to compute both strategies at once??
            double totalRegret = 0;
            i = 0;
            Double[] actionRegrets = regrets.getOrDefault(is, null);
            if (actionRegrets == null) {
                actionRegrets = new Double[legalActions.size()];
                Arrays.fill(actionRegrets, 0d);
                regrets.put(is, actionRegrets);
            }
            double probWithoutActingPlayer = rndProb * PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), p2, p1); // \pi_{-i}
            int pid = s.getActingPlayerId();
            for (IAction a: legalActions) {
                actionRegrets[i] = Math.max(actionRegrets[i] + probWithoutActingPlayer*(actionCFV[2*i + pid - 1] - cfv[pid - 1]), 0);
                totalRegret += actionRegrets[i];
                i++;
            }
            i = 0;
            if (totalRegret > 0) {
                for (IAction a: legalActions) {
                    nextStrat.setProbability(is, a, actionRegrets[i]/totalRegret);
                    i++;
                }
            } else {
                nextStrat.setProbabilities(is, (action) -> 1d/legalActions.size());
            }

            CFRResult ret = new CFRResult(cfv[0], cfv[1]);

            return ret;
        }

        private void normalizeOpponentRange() {
            double total = 0;
            for (int i = 0; i < opponentRange.length; ++i) {
                total += opponentRange[i];
            }
            if (total > 0) {
                if (total != 0) {
                    for (int i = 0; i < opponentRange.length; ++i) {
                        opponentRange[i] /= total;
                    }
                }
            } else {
                Arrays.fill(opponentRange, 1d/opponentRange.length);
            }
        }

        public IAction act() {
            onEvent((listener, info) -> listener.resolvingStart(info));
            GameTreeTraversalTracker tracker = GameTreeTraversalTracker.createForAct(id);
            for (int i = 0; i < iters; ++i) {
                int osIdx = 0;
                for (IInformationSet os: opponentCFV.keySet()) {
                    double osCFV = 0;
                    boolean isOsValid = false;
                    for (IInformationSet ms: range.getInformationSets()) {
                        IInformationSet player1IS, player2IS;
                        double r1, r2;
                        if (id == 1) {
                            player1IS = ms;
                            player2IS = os;
                            r1 = range.getProbability(ms);
                            r2 = opponentRange[osIdx];
                        } else {
                            player1IS = os;
                            player2IS = ms;
                            r1 = opponentRange[osIdx];
                            r2 = range.getProbability(ms);
                        }
                        ICompleteInformationState s = cisFactory.make(player1IS, player2IS, id);
                        if (s == null) continue;
                        GameTreeTraversalTracker stateTracker = tracker.visit(s);
                        isOsValid = true;
                        CFRResult res = cfr(stateTracker, id, 0, r1, r2);
                        if (id == 1) {
                            osCFV += r1*res.player2CFV;
                        } else {
                            osCFV += r2*res.player1CFV;
                        }
                    }
                    if (isOsValid) {
                        double followProb = Math.max(regretsGadget[2*osIdx], 0);
                        followProb = followProb/(followProb + Math.max(regretsGadget[2*osIdx + 1], 0));
                        if (Double.isNaN(followProb)) followProb = 0.5; // during first iteration regretsGadget is 0
                        opponentRange[osIdx] = followProb;
                        double gadgetValue = followProb * osCFV + (1 - followProb) * opponentCFV.getOrDefault(os, 0d);
                        regretsGadget[2*osIdx + 1] += opponentCFV.getOrDefault(os, 0d)  - gadgetValue;
                        regretsGadget[2*osIdx] += osCFV - gadgetValue;
                    }

                    osIdx++;
                }
                normalizeOpponentRange();
                strat = nextStrat;
                nextStrat = new Strategy();
                for (IInformationSet myIs: range.getInformationSets()) {
                    cumulativeStrat.addProbabilities(myIs, (action) -> strat.getProbability(myIs, action));
                }
                onEvent((listener, info) -> listener.resolvingIterationEnd(info));
            }
            cumulativeStrat.normalize();
            lastCummulativeStrategy = cumulativeStrat;
            IAction ret = cumulativeStrat.sampleAction(hiddenInfo);
            myLastAction = ret;
            ntit = tracker.getActionToNtit().get(ret);
            psMap = tracker.getActionToPsMap().get(ret);
            myISToNRT = tracker.getMyISToNRT();
            hiddenInfo = hiddenInfo.next(ret);
            onEvent((listener, info) -> listener.resolvingEnd(info));
            return ret;
        }
    }

    @Override
    public IAction act() {
        opponentCFV = new HashMap<>(ntit.getOpponentValues().size());
        ntit.getOpponentValues().forEach((is, cfv) -> opponentCFV.put(is, cfv.getValue() / iters));
        range.advance(psMap.getPossibleSequences(myPSBuilder.close()), myISToNRT, lastCummulativeStrategy);
        Resolver r = new Resolver();
        return r.act();
    }

    @Override
    public void receivePercepts(IPercept percept) {
        myPSBuilder.add(percept);
        hiddenInfo = hiddenInfo.applyPercept(percept);
        ntit = ntit.getNext(percept);
    }
}
