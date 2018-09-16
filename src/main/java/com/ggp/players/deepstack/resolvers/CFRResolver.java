package com.ggp.players.deepstack.resolvers;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.ICompleteInformationStateFactory;
import com.ggp.IInformationSet;
import com.ggp.players.deepstack.*;
import com.ggp.players.deepstack.regret_matching.RegretMatching;
import com.ggp.players.deepstack.regret_matching.RegretMatchingPlus;
import com.ggp.players.deepstack.utils.GameTreeTraversalTracker;
import com.ggp.players.deepstack.utils.InformationSetRange;
import com.ggp.players.deepstack.utils.IterationTimer;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.PlayerHelpers;

import java.util.*;
import java.util.function.BiFunction;

public class CFRResolver extends BaseCFRResolver implements ISubgameResolver {
    public static class Factory implements ISubgameResolver.Factory {
        private IUtilityEstimator utilityEstimator;
        private int depthLimit;
        private IRegretMatching regretMatching = new RegretMatchingPlus();

        public Factory(IUtilityEstimator utilityEstimator, int depthLimit) {
            this.utilityEstimator = utilityEstimator;
            this.depthLimit = depthLimit;
        }

        @Override
        public ISubgameResolver create(int myId, IInformationSet hiddenInfo, InformationSetRange myRange, HashMap<IInformationSet, Double> opponentCFV,
                                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners)
        {
            return new CFRResolver(myId, hiddenInfo, myRange, opponentCFV, cisFactory, resolvingListeners, regretMatching, utilityEstimator, depthLimit);
        }

        @Override
        public String getConfigString() {
            return "DepthLimitedCFR{" +
                    "utilityEstimator=" + ((utilityEstimator == null) ? "null" : utilityEstimator.getConfigString()) +
                    ", depthLimit=" + depthLimit +
                    ", regretMatching=" + regretMatching.getConfigString() +
                    '}';
        }
    }

    private IUtilityEstimator utilityEstimator;
    private int depthLimit = 2;
    private Strategy strat = new Strategy();
    private Strategy nextStrat = new Strategy();

    public CFRResolver(int myId, IInformationSet hiddenInfo, InformationSetRange range, HashMap<IInformationSet, Double> opponentCFV,
                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners,
                       IRegretMatching regretMatching, IUtilityEstimator utilityEstimator, int depthLimit)
    {
        super(myId, hiddenInfo, range, opponentCFV, resolvingListeners, regretMatching);
        this.utilityEstimator = utilityEstimator;
        this.depthLimit = depthLimit;
    }

    private static class CFRResult {
        public double player1Utility;
        public double player2Utility;

        public CFRResult(double player1Utility, double player2Utility) {
            this.player1Utility = player1Utility;
            this.player2Utility = player2Utility;
        }
    }

    private CFRResult cfr(GameTreeTraversalTracker tracker, int player, int depth, double reachProb1, double reachProb2) {
        // CVF_i(h) = reachProb_{-i}(h) * utility_i(H)
        // this method passes reachProb from top and returns utility
        ICompleteInformationState s = tracker.getCurrentState();
        resolvingListeners.forEach(listener -> listener.stateVisited(s, resInfo));

        if (s.isTerminal()) {
            return new CFRResult(s.getPayoff(1), s.getPayoff(2));
        }

        // cutoff can only be made once i know opponentCFV for next turn i'll play
        if (tracker.wasMyNextTurnReached() && depth > depthLimit && utilityEstimator != null) {
            IUtilityEstimator.EstimatorResult res = utilityEstimator.estimate(s);
            return new CFRResult(res.player1Utility, res.player2Utility);
        }
        List<IAction> legalActions = s.getLegalActions();
        double rndProb = tracker.getRndProb();

        BiFunction<ICompleteInformationState, IAction, CFRResult> callCfr = (x, a) -> {
            double np1 = reachProb1, np2 = reachProb2;
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
                ret.player1Utility += res.player1Utility;
                ret.player2Utility += res.player2Utility;
            }
            // uniform probability for each action -> average
            ret.player1Utility /= legalActions.size();
            ret.player2Utility /= legalActions.size();
            return ret;
        }

        IInformationSet is = s.getInfoSetForActingPlayer();
        double[] utility = new double[2];
        double[] actionUtility = new double[2*legalActions.size()];
        int i = 0;

        for (IAction a: legalActions) {
            double actionProb = strat.getProbability(is, a);
            CFRResult res = callCfr.apply(s, a);
            actionUtility[2*i] = res.player1Utility;
            actionUtility[2*i + 1] = res.player2Utility;
            for (int j = 0; j < 2; ++j) {
                utility[j] = utility[j] + actionProb*actionUtility[2*i + j];
            }
            i++;
        }
        if (tracker.isMyNextTurnReached()) {
            double probWithoutOpponent = rndProb * PlayerHelpers.selectByPlayerId(myId, reachProb1, reachProb2);
            tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), probWithoutOpponent * utility[opponentId - 1]);
        }
        // TODO: is it ok to compute both strategies at once??
        i = 0;
        double probWithoutActingPlayer = rndProb * PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), reachProb2, reachProb1); // reachProb_{-i}
        int pid = s.getActingPlayerId();
        for (IAction a: legalActions) {
            regretMatching.addActionRegret(is, i, probWithoutActingPlayer*(actionUtility[2*i + pid - 1] - utility[pid - 1]));
            i++;
        }
        regretMatching.getRegretMatchedStrategy(is, nextStrat);

        CFRResult ret = new CFRResult(utility[0], utility[1]);

        return ret;
    }

    @Override
    protected ActResult doAct(GameTreeTraversalTracker tracker, IterationTimer timeout) {
        HashMap<IInformationSet, Double> currentOpponentCFV = new HashMap<>(opponentCFV.size());
        HashSet<IInformationSet> myInformationSets = new HashSet<>();
        for (ICompleteInformationState s: range.getPossibleStates()) {
            myInformationSets.add(s.getInfoSetForActingPlayer());
        }
        int iters = 0;
        while (timeout.canDoAnotherIteration()) {
            timeout.startIteration();
            for (IInformationSet os: opponentCFV.keySet()) {
                currentOpponentCFV.put(os, 0d);
            }
            for (Map.Entry<ICompleteInformationState, Double> stateProb: range.getProbabilities()) {
                ICompleteInformationState s = stateProb.getKey();
                // unnormalized reachProb_{-opponentId}
                double rndProb = stateProb.getValue();
                GameTreeTraversalTracker stateTracker = tracker.visitRandom(s, rndProb);
                IInformationSet os = s.getInfoSetForPlayer(opponentId);
                CFRResult res = PlayerHelpers.callWithOrderedParams(myId, 1d, subgameGadget.getFollowProb(os), (r1, r2) -> cfr(stateTracker, myId, 0, r1, r2));
                double osCFV = PlayerHelpers.selectByPlayerId(opponentId, res.player1Utility, res.player2Utility) * rndProb;
                currentOpponentCFV.merge(os, osCFV, (oldV, newV) -> oldV + newV);
            }
            for (IInformationSet os: opponentCFV.keySet()) {
                subgameGadget.addFollowCFV(os, currentOpponentCFV.get(os));
            }
            strat = nextStrat;
            nextStrat = new Strategy();
            for (IInformationSet myIs: myInformationSets) {
                cumulativeStrat.addProbabilities(myIs, (action) -> strat.getProbability(myIs, action));
            }
            resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
            timeout.endIteration();
            iters++;
        }
        cumulativeStrat.normalize();

        return new ActResult(cumulativeStrat, tracker.getActionToNtit(), tracker.getActionToPsMap(), tracker.getMyISToNRT(), iters);
    }

    @Override
    protected InitResult doInit(GameTreeTraversalTracker tracker, IterationTimer timeout) {
        int iters = 0;
        while (timeout.canDoAnotherIteration()) {
            timeout.startIteration();
            cfr(tracker, myId, 0, 1, 1);
            resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
            timeout.endIteration();
            iters++;
        }
        return new InitResult(tracker.getNtit(), tracker.getNrt(), tracker.getPsMap(), iters);
    }
}
