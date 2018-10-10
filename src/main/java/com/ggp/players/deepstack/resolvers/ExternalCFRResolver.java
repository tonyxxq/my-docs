package com.ggp.players.deepstack.resolvers;

import com.ggp.*;
import com.ggp.players.deepstack.IRegretMatching;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.ISubgameResolver;
import com.ggp.players.deepstack.IUtilityEstimator;
import com.ggp.players.deepstack.regret_matching.RegretMatchingPlus;
import com.ggp.players.deepstack.trackers.CFRDTracker;
import com.ggp.players.deepstack.trackers.IGameTraversalTracker;
import com.ggp.players.deepstack.utils.InformationSetRange;
import com.ggp.players.deepstack.utils.IterationTimer;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.solvers.cfr.DepthLimitedCFRSolver;
import com.ggp.utils.PlayerHelpers;

import java.util.*;

public class ExternalCFRResolver extends BaseCFRDResolver {
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
            return new ExternalCFRResolver(myId, hiddenInfo, myRange, opponentCFV, cisFactory, resolvingListeners, regretMatching, utilityEstimator, depthLimit);
        }

        @Override
        public String getConfigString() {
            return "ExCFR{" +
                    "ue=" + ((utilityEstimator == null) ? "null" : utilityEstimator.getConfigString()) +
                    ", dl=" + depthLimit +
                    ", rm=" + regretMatching.getConfigString() +
                    '}';
        }
    }

    private IUtilityEstimator utilityEstimator;
    private int depthLimit = 2;
    private Strategy strat = new Strategy();

    public ExternalCFRResolver(int myId, IInformationSet hiddenInfo, InformationSetRange range, HashMap<IInformationSet, Double> opponentCFV,
                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners,
                       IRegretMatching regretMatching, IUtilityEstimator utilityEstimator, int depthLimit)
    {
        super(myId, hiddenInfo, range, opponentCFV, resolvingListeners, regretMatching);
        this.utilityEstimator = utilityEstimator;
        this.depthLimit = depthLimit;
    }

    private DepthLimitedCFRSolver createSolver() {
        DepthLimitedCFRSolver cfrSolver = new DepthLimitedCFRSolver(regretMatching, strat);

        cfrSolver.registerListener(new DepthLimitedCFRSolver.IListener() {
            @Override
            public void enteringState(IGameTraversalTracker tracker, DepthLimitedCFRSolver.Info info) {
            }

            @Override
            public void leavingState(IGameTraversalTracker t, DepthLimitedCFRSolver.Info info, double p1Utility) {
                CFRDTracker tracker = (CFRDTracker) t;
                if (tracker.isMyNextTurnReached()) {
                    double probWithoutOpponent = info.rndProb * PlayerHelpers.selectByPlayerId(myId, info.reachProb1, info.reachProb2);
                    double playerMul = PlayerHelpers.selectByPlayerId(myId, -1, 1);
                    tracker.getNtit().addLeaf(tracker.getCurrentState().getInfoSetForPlayer(opponentId), probWithoutOpponent * playerMul * p1Utility);
                }
            }
        });
        return cfrSolver;
    }

    @Override
    protected ActResult doAct(CFRDTracker subgameTracker, IterationTimer timeout) {
        HashSet<IInformationSet> myInformationSets = new HashSet<>();
        for (ICompleteInformationState s: range.getPossibleStates()) {
            myInformationSets.add(s.getInfoSetForActingPlayer());
        }
        int iters = 0;
        DepthLimitedCFRSolver cfrSolver = createSolver();
        while (timeout.canDoAnotherIteration()) {
            timeout.startIteration();
            cfrSolver.cfr(subgameTracker, myId, 0, 1, 1);
            for (IInformationSet myIs: myInformationSets) {
                cumulativeStrat.addProbabilities(myIs, (action) -> strat.getProbability(myIs, action));
            }
            regretMatching.getRegretMatchedStrategy(strat);

            resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
            timeout.endIteration();
            iters++;
        }
        cumulativeStrat.normalize();

        return new ActResult(cumulativeStrat, subgameTracker.getActionToNtit(), subgameTracker.getActionToPsMap(), subgameTracker.getMyISToNRT(), iters);
    }

    @Override
    protected InitResult doInit(CFRDTracker tracker, IterationTimer timeout) {
        int iters = 0;
        DepthLimitedCFRSolver cfrSolver = createSolver();
        while (timeout.canDoAnotherIteration()) {
            timeout.startIteration();
            cfrSolver.cfr(tracker, myId, 0, 1, 1);
            resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
            timeout.endIteration();
            iters++;
        }
        return new InitResult(tracker.getNtit(), tracker.getNrt(), tracker.getPsMap(), iters);
    }
}
