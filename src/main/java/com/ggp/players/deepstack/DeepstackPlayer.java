package com.ggp.players.deepstack;

import com.ggp.*;
import com.ggp.players.deepstack.estimators.RandomPlayoutCFVEstimator;

import java.util.*;
import java.util.function.BiFunction;

public class DeepstackPlayer implements IPlayer {
    public static class Factory implements IPlayerFactory {
        @Override
        public IPlayer create(IGameDescription game, int role) {
            return new DeepstackPlayer(role, game, new RandomPlayoutCFVEstimator());
        }
    }

    private int id;
    private int opponentId;
    private InformationSetRange range;
    private IInformationSet hiddenInfo;
    private HashMap<IInformationSet, Double> opponentCFV;
    private ICompleteInformationStateFactory cisFactory;
    private int iters = 5;
    private int depthLimit = 2;
    private ICFVEstimator cfvEstimator;
    private NextTurnInfoTree ntit;
    private HashMap<IInformationSet, NextRangeTree> myISToNRT = new HashMap<>();
    private IGameDescription gameDesc;
    private PerceptSequence.Builder myPSBuilder = new PerceptSequence.Builder();
    private IAction myLastAction;
    private PerceptSequenceMap psMap;
    private Strategy lastCummulativeStrategy;

    public DeepstackPlayer(int id, IGameDescription gameDesc, ICFVEstimator cfvEstimator) {
        this.id = id;
        this.opponentId = (id == 1) ? 2 : 1;
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

    @Override
    public void init() {
        Resolver r = new Resolver();
        NextRangeTree nrt = myISToNRT.get(gameDesc.getInitialInformationSet(id));
        ntit = new NextTurnInfoTree();
        for (int i = 0; i < iters; ++i) {
            Resolver.CFRResult res = r.cfr(gameDesc.getInitialState(), CFRState.WAIT_MY_TURN, id, 0, 1, 1, new PerceptSequence(), new PerceptSequence(), nrt, null, 1d);
            psMap = res.perceptSequenceMap;
            ntit.merge(res.ntit);
        }
    }

    @Override
    public int getRole() {
        return id;
    }

    private enum CFRState {
        TOP, WAIT_MY_TURN, END;
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
            public NextTurnInfoTree ntit; // only returned in non-terminal WAIT_MY_TURN state
            public HashMap<IAction, NextTurnInfoTree> actionToNTIT; // only returned in non-terminal TOP state
            public PerceptSequenceMap perceptSequenceMap; // only returned in non-terminal WAIT_MY_TURN state
            public HashMap<IAction, PerceptSequenceMap> actionToPerceptSequenceMap; // only returned in non-terminal TOP state

            public CFRResult(double player1CFV, double player2CFV) {
                this.player1CFV = player1CFV;
                this.player2CFV = player2CFV;
            }
        }

        private PerceptSequence getNextPerceptSequence(CFRState cfrState, PerceptSequence current, int player, Iterable<IPercept> percepts) {
            if (cfrState != CFRState.END) {
                for (IPercept p: percepts) {
                    if (p.getTargetPlayer() == player) return new PerceptSequence(current, percepts, player);
                }
                return current;
            }
            return null;
        }

        public CFRResult cfr(ICompleteInformationState s, CFRState state, int player, int depth, double p1, double p2, PerceptSequence myPerceptSequence, PerceptSequence opponentPerceptSequence, NextRangeTree nrt, IAction myTopAction, double rndProb) {
            if (s.isTerminal()) {
                return new CFRResult(s.getPayoff(1), s.getPayoff(2));
            }
            CFRState nextState = state;
            NextTurnInfoTree ntit = null;
            PerceptSequenceMap perceptSequenceMap = null;
            if (state == CFRState.TOP) {
                nextState = CFRState.WAIT_MY_TURN;
            } else if (state == CFRState.WAIT_MY_TURN && s.getActingPlayerId() == id) {
                nextState = CFRState.END;
            }
            if (state != CFRState.END) {
                ntit = new NextTurnInfoTree();
                perceptSequenceMap = new PerceptSequenceMap();
            }

            // cutoff can only be made once i know opponentCFV for next turn i'll play
            if (state == CFRState.END && depth > depthLimit) {
                ICFVEstimator.EstimatorResult res = cfvEstimator.estimate(s, cumulativeStrat);
                return new CFRResult(res.player1CFV, res.player2CFV);
            }
            List<IAction> legalActions = s.getLegalActions();

            final CFRState finalNextState = nextState;
            int opponentId = 2 - id + 1;
            BiFunction<ICompleteInformationState, IAction, CFRResult> callCfr = (x, a) -> {
                Iterable<IPercept> percepts = x.getPercepts(a);
                double np1 = p1, np2 = p2;
                double newRndProb = rndProb;
                if (s.getActingPlayerId() == 1) {
                    np1 *= strat.getProbability(s.getInfoSetForActingPlayer(), a);
                } else if (s.getActingPlayerId() == 2) {
                    np2 *= strat.getProbability(s.getInfoSetForActingPlayer(), a);
                } else {
                    newRndProb *= 1d/legalActions.size();
                }
                IAction newMyTopAction = myTopAction == null && s.getActingPlayerId() == id ? a : myTopAction;
                PerceptSequence wipOpponentPerceptSequence = opponentPerceptSequence;
                if (s.getActingPlayerId() == opponentId) {
                    wipOpponentPerceptSequence = new PerceptSequence(opponentPerceptSequence, Collections.singleton(new OwnActionPercept(opponentId, a)), opponentId);
                }

                return cfr(s.next(a), finalNextState, player, depth+1, np1, np2, getNextPerceptSequence(state, myPerceptSequence, id, percepts), getNextPerceptSequence(state, wipOpponentPerceptSequence, opponentId, percepts), finalNextState == CFRState.WAIT_MY_TURN ? nrt : null, newMyTopAction, newRndProb);
            };

            if (s.isRandomNode()) {
                CFRResult ret = new CFRResult(0,0);
                if (state == CFRState.WAIT_MY_TURN) {
                    ret.ntit = new NextTurnInfoTree();
                    ret.perceptSequenceMap = new PerceptSequenceMap();
                }
                // random node can't be TOP state so no need to handle that
                for (IAction a: legalActions) {
                    CFRResult res =  callCfr.apply(s, a);
                    ret.player1CFV += res.player1CFV;
                    ret.player2CFV += res.player2CFV;
                    if (state == CFRState.WAIT_MY_TURN) {
                        ret.ntit.add(s.getPercepts(a), id, res.ntit);
                        ret.perceptSequenceMap.merge(res.perceptSequenceMap);
                    }
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
            HashMap<IAction, NextTurnInfoTree> actionToNTIT = null;
            HashMap<IAction, PerceptSequenceMap> actionToPerceptSequenceMap = null;
            if (state == CFRState.TOP) {
                actionToNTIT = new HashMap<>();
                actionToPerceptSequenceMap = new HashMap<>();
            }

            for (IAction a: legalActions) {
                double actionProb = strat.getProbability(is, a);
                CFRResult res = callCfr.apply(s, a);
                actionCFV[2*i] = res.player1CFV;
                actionCFV[2*i + 1] = res.player2CFV;
                for (int j = 0; j < 2; ++j) {
                    cfv[j] = cfv[j] + actionProb*actionCFV[2*i + j];
                }
                if (nextState == CFRState.WAIT_MY_TURN) {
                    ntit.add(s.getPercepts(a), id, res.ntit);
                    perceptSequenceMap.merge(res.perceptSequenceMap);
                }
                if (state == CFRState.TOP) {
                    actionToNTIT.put(a, ntit);
                    actionToPerceptSequenceMap.put(a, perceptSequenceMap);
                }
                i++;
            }
            if (state == CFRState.WAIT_MY_TURN && nextState == CFRState.END) {
                ntit.addLeaf(s.getInfoSetForPlayer(opponentId), cfv[opponentId - 1]);
                perceptSequenceMap = new PerceptSequenceMap(myPerceptSequence, opponentPerceptSequence);
                if (nrt != null) {
                    nrt.add(opponentPerceptSequence, s.getInfoSetForPlayer(id), myTopAction, rndProb);
                }
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
            double probWithoutActingPlayer; // \pi_{-i}
            if (s.getActingPlayerId() == 1) probWithoutActingPlayer = p2*rndProb;
            else probWithoutActingPlayer = p1*rndProb;
            int pid = s.getActingPlayerId();
            for (IAction a: legalActions) {
                actionRegrets[i] = actionRegrets[i] + probWithoutActingPlayer*(actionCFV[2*i + pid - 1] - cfv[pid - 1]);
                totalRegret += Math.max(actionRegrets[i], 0);
                i++;
            }
            i = 0;
            if (totalRegret > 0) {
                for (IAction a: legalActions) {
                    nextStrat.setProbability(is, a, Math.max(actionRegrets[i], 0)/totalRegret);
                    i++;
                }
            } else {
                nextStrat.setProbabilities(is, (action) -> 1d/legalActions.size());
            }
            double rangeProb = range.getProbability(is);
            if (rangeProb > 0) {
                // cummulative strategy is only used for selecting action and updating range -> only in range information sets
                cumulativeStrat.addProbabilities(is, (action) -> rangeProb*strat.getProbability(is, action));
            }

            CFRResult ret = new CFRResult(cfv[0], cfv[1]);
            if (state == CFRState.TOP) {
                ret.actionToNTIT = actionToNTIT;
                ret.actionToPerceptSequenceMap = actionToPerceptSequenceMap;
            } else if (state == CFRState.WAIT_MY_TURN) {
                ret.ntit = ntit;
                ret.perceptSequenceMap = perceptSequenceMap;
            }

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
            HashMap<IAction, NextTurnInfoTree> actionToNTIT = new HashMap<>();
            HashMap<IAction, PerceptSequenceMap> actionToPerceptSequenceMap = new HashMap<>();
            myISToNRT = new HashMap<>();
            for (int i = 0; i < iters; ++i) {
                int osIdx = 0;
                for (IInformationSet os: opponentCFV.keySet()) {
                    double osCFV = 0;
                    boolean isOsValid = false;
                    for (IInformationSet ms: range.getInformationSets()) {
                        IInformationSet player1IS, player2IS;
                        NextRangeTree nrt = myISToNRT.getOrDefault(ms, null);
                        if (nrt == null) {
                            nrt = new NextRangeTree();
                            myISToNRT.put(ms, nrt);
                        }
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
                        isOsValid = true;
                        CFRResult res = cfr(s, CFRState.TOP, id, 0, r1, r2, new PerceptSequence(), new PerceptSequence(), nrt, null, 1d);
                        // I want res to contain map IAction -> (opponent IS next time its my turn, CFV for that IS)
                        if (id == 1) {
                            osCFV += r1*res.player2CFV;
                        } else {
                            osCFV += r2*res.player1CFV;
                        }
                        if (res.actionToNTIT != null) {
                            res.actionToNTIT.forEach((k, v) -> {if (v != null) actionToNTIT.merge(k, v, (oldV, newV) -> oldV == null ? newV : oldV.merge(newV));});
                        }
                        // only opponent sequences originating from hiddenInfo are actually possible
                        if (res.actionToPerceptSequenceMap != null && ms.equals(hiddenInfo)) {
                            res.actionToPerceptSequenceMap.forEach((k, v) -> {if (v != null) actionToPerceptSequenceMap.merge(k, v, (oldV, newV) -> oldV.merge(newV));});
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
            }
            cumulativeStrat.normalize();
            lastCummulativeStrategy = cumulativeStrat;
            IAction ret = cumulativeStrat.sampleAction(hiddenInfo);
            myLastAction = ret;
            ntit = actionToNTIT.get(ret);
            psMap = actionToPerceptSequenceMap.get(ret);
            hiddenInfo = hiddenInfo.next(ret);
            return ret;
        }
    }

    @Override
    public IAction act() {
        opponentCFV = new HashMap<>(ntit.getOpponentValues().size());
        ntit.getOpponentValues().forEach((is, cfv) -> opponentCFV.put(is, cfv.getValue()));
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
