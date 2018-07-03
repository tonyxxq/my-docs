package com.ggp.players.deepstack;

import com.ggp.*;
import com.ggp.players.deepstack.estimators.RandomPlayoutCFVEstimator;
import com.ggp.utils.RandomItemSelector;

import java.util.*;

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
    RandomItemSelector<IAction> randomActionSelector = new RandomItemSelector<>();

    public DeepstackPlayer(int id, IGameDescription gameDesc, ICFVEstimator cfvEstimator) {
        this.id = id;
        this.opponentId = (id == 1) ? 2 : 1;
        range = new InformationSetRange();
        IInformationSet initialSet = gameDesc.getInitialInformationSet(id);
        IInformationSet initialOpponentSet = gameDesc.getInitialInformationSet(2 - id + 1);
        range.init(initialSet);
        hiddenInfo = initialSet;
        opponentCFV = new HashMap<>(1);
        opponentCFV.put(initialOpponentSet, 0d);
        cisFactory = gameDesc.getCISFactory();
        this.cfvEstimator = cfvEstimator;
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
            public NextTurnInfoTree ntit; // only returned in WAIT_MY_TURN state
            public HashMap<IAction, NextTurnInfoTree> actionToNTIT; // only returned at TOP state

            public CFRResult(double player1CFV, double player2CFV) {
                this.player1CFV = player1CFV;
                this.player2CFV = player2CFV;
            }
        }

        private CFRResult cfr(ICompleteInformationState s, CFRState state, int player, int depth, double p1, double p2) {
            if (s.isTerminal()) {
                return new CFRResult(s.getPayoff(1), s.getPayoff(2));
            } else if (s.isRandomNode()) {
                ICompleteInformationState ns = s.next(randomActionSelector.select(s.getLegalActions()));
                return cfr(ns, state, player, depth+1, p1, p2);
            }
            CFRState nextState = state;
            NextTurnInfoTree ntit = null;
            if (state == CFRState.WAIT_MY_TURN) {
                ntit = new NextTurnInfoTree();
            }
            if (state == CFRState.TOP) {
                nextState = CFRState.WAIT_MY_TURN;
            } else if (state == CFRState.WAIT_MY_TURN && s.getActingPlayerId() == id) {
                nextState = CFRState.END;
            }

            // cutoff can only be made once i know opponentCFV for next turn i'll play
            if (state == CFRState.END && depth > depthLimit) {
                ICFVEstimator.EstimatorResult res = cfvEstimator.estimate(s, cumulativeStrat);
                return new CFRResult(res.player1CFV, res.player2CFV);
            }

            IInformationSet is = s.getInfoSetForActingPlayer();
            double[] cfv = new double[2];
            List<IAction> legalActions = is.getLegalActions();
            double[] actionCFV = new double[2*legalActions.size()];
            int i = 0;
            HashMap<IAction, NextTurnInfoTree> actionToNTIT = null;
            if (state == CFRState.TOP) {
                actionToNTIT = new HashMap<>();
            }

            for (IAction a: legalActions) {
                double actionProb = strat.getProbability(is, a);
                CFRResult res;
                if (s.getActingPlayerId() == 1) {
                    res = cfr(s.next(a), nextState, player, depth+1,actionProb*p1, p2);
                } else {
                    res = cfr(s.next(a), nextState, player, depth+1, p1, actionProb*p2);
                }
                actionCFV[2*i] = res.player1CFV;
                actionCFV[2*i + 1] = res.player2CFV;
                for (int j = 0; j < 2; ++j) {
                    cfv[j] = cfv[j] + actionProb*actionCFV[2*i + j];
                }
                if (state == CFRState.TOP) {
                    actionToNTIT.put(a, res.ntit);
                } else if (state == CFRState.WAIT_MY_TURN && nextState == CFRState.WAIT_MY_TURN) {
                    ntit.add(s.getPercepts(a), id, res.ntit);
                }
                i++;
            }
            if (state == CFRState.WAIT_MY_TURN && nextState == CFRState.END) {
                ntit.addLeaf(s.getInfoSetForPlayer(opponentId), cfv[opponentId - 1]);
            }
            // TODO: is it ok to compute both strategies at once??
            double totalRegret = 0;
            i = 0;
            Double[] actionRegrets = regrets.getOrDefault(is, null);
            if (actionRegrets == null) {
                actionRegrets = new Double[legalActions.size()];
                Arrays.fill(actionRegrets, 0d);
            }
            double p;
            if (s.getActingPlayerId() == 1) p = p2;
            else p = p1;
            int pid = s.getActingPlayerId();
            for (IAction a: legalActions) {
                actionRegrets[i] = actionRegrets[i] + p*(actionCFV[2*i + pid - 1] - cfv[pid - 1]);
                totalRegret += Math.max(actionRegrets[i], 0);
                i++;
            }
            i = 0;
            if (totalRegret > 0) {
                for (IAction a: legalActions) {
                    strat.setProbability(is, a, Math.max(actionRegrets[i], 0)/totalRegret);
                    i++;
                }
            } else {
                strat.setProbabilities(is, (action) -> 1d/legalActions.size());
            }
            cumulativeStrat.addProbabilities(is, (action) -> p*strat.getProbability(is, action));
            CFRResult ret = new CFRResult(cfv[0], cfv[1]);
            if (state == CFRState.TOP) {
                ret.actionToNTIT = actionToNTIT;
            } else if (state == CFRState.WAIT_MY_TURN) {
                ret.ntit = ntit;
            }

            return ret;
        }

        public IAction act() {
            HashMap<IAction, NextTurnInfoTree> actionToNTIT = new HashMap<>();
            double[] prevGadgetValues = new double[opponentCFV.size()];
            for (int i = 0; i < iters; ++i) {
                int osIdx = 0;
                for (IInformationSet os: opponentCFV.keySet()) {
                    double osCFV = 0;
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
                        CFRResult res = cfr(s, CFRState.TOP, id, 0, r1, r2);
                        // I want res to contain map IAction -> (opponent IS next time its my turn, CFV for that IS)
                        if (id == 1) {
                            osCFV += r1*res.player2CFV;
                        } else {
                            osCFV += r2*res.player1CFV;
                        }
                        strat = new Strategy();
                        res.actionToNTIT.forEach((k, v) -> actionToNTIT.merge(k, v, (x, y) -> x.merge(y)));
                    }
                    double followProb = Math.max(regretsGadget[2*osIdx], 0);
                    followProb = followProb/(followProb + Math.max(regretsGadget[2*osIdx + 1], 0));
                    opponentRange[osIdx] = followProb;
                    double gadgetValue = followProb * osCFV + (1 - followProb) * opponentCFV.getOrDefault(os, 0d);
                    regretsGadget[2*osIdx + 1] += opponentCFV.getOrDefault(os, 0d)  - prevGadgetValues[osIdx];
                    regretsGadget[2*osIdx] += osCFV - gadgetValue;

                    prevGadgetValues[osIdx] = gadgetValue;
                    osIdx++;
                }
            }
            cumulativeStrat.normalize();
            IAction ret = cumulativeStrat.sampleAction(hiddenInfo);
            range.advance(ret, cumulativeStrat);
            ntit = actionToNTIT.get(ret);
            return ret;
        }
    }

    @Override
    public IAction act() {
        if (ntit != null) {
            opponentCFV = new HashMap<>(ntit.getOpponentValues().size());
            ntit.getOpponentValues().forEach((is, cfv) -> opponentCFV.put(is, cfv.getValue()));
        }
        Resolver r = new Resolver();
        return r.act();
    }

    @Override
    public void receivePercepts(IPercept percept) {
        range.advance(percept);
        hiddenInfo = hiddenInfo.applyPercept(percept);
        if (ntit != null) {
            ntit = ntit.getNext(percept); // in case player hasn't played yet when receiving percept
        }
    }
}
