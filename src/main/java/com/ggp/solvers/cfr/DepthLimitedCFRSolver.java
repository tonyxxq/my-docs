package com.ggp.solvers.cfr;

import com.ggp.*;
import com.ggp.players.deepstack.IRegretMatching;
import com.ggp.players.deepstack.IUtilityEstimator;
import com.ggp.players.deepstack.trackers.IGameTraversalTracker;
import com.ggp.utils.PlayerHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class DepthLimitedCFRSolver {
    public static class Info {
        public final double reachProb1, reachProb2, rndProb;

        public Info(double reachProb1, double reachProb2, double rndProb) {
            this.reachProb1 = reachProb1;
            this.reachProb2 = reachProb2;
            this.rndProb = rndProb;
        }
    }

    public interface IListener {
        void enteringState(IGameTraversalTracker tracker, Info info);
        void leavingState(IGameTraversalTracker tracker, Info info, double utility);
    }

    private IRegretMatching regretMatching;
    private IStrategy strat;
    private int depthLimit = 0;
    private IUtilityEstimator utilityEstimator;
    private List<IListener> listeners = new ArrayList<>();

    public DepthLimitedCFRSolver(IRegretMatching regretMatching, IStrategy strat) {
        this.regretMatching = regretMatching;
        this.strat = strat;
    }

    public DepthLimitedCFRSolver(IRegretMatching regretMatching, IStrategy strat, int depthLimit, IUtilityEstimator utilityEstimator) {
        this.regretMatching = regretMatching;
        this.strat = strat;
        this.depthLimit = depthLimit;
        this.utilityEstimator = utilityEstimator;
    }

    public double cfr(IGameTraversalTracker tracker, int player, int depth, double reachProb1, double reachProb2) {
        // CVF_i(h) = reachProb_{-i}(h) * utility_i(H)
        // this method passes reachProb from top and returns player 1's utility
        ICompleteInformationState s = tracker.getCurrentState();
        Info info = new Info(reachProb1, reachProb2, tracker.getRndProb());
        listeners.forEach(listener -> listener.enteringState(tracker, info));

        if (s.isTerminal()) {
            return s.getPayoff(1);
        }

        // TODO: generalize condition for utilityEstimator
        // cutoff can only be made once i know opponentCFV for next turn i'll play
        /*if (tracker.wasMyNextTurnReached() && depth > depthLimit && utilityEstimator != null) {
            IUtilityEstimator.EstimatorResult res = utilityEstimator.estimate(s);
            return res.player1Utility;
        }*/
        List<IAction> legalActions = s.getLegalActions();
        double rndProb = tracker.getRndProb();

        BiFunction<ICompleteInformationState, IAction, Double> callCfr = (x, a) -> {
            double np1 = reachProb1, np2 = reachProb2;
            if (s.getActingPlayerId() == 1) {
                np1 *= strat.getProbability(s.getInfoSetForActingPlayer(), a);
            } else if (s.getActingPlayerId() == 2) {
                np2 *= strat.getProbability(s.getInfoSetForActingPlayer(), a);
            }
            return cfr(tracker.next(a), player, depth+1, np1, np2);
        };

        if (s.isRandomNode()) {
            IRandomNode rndNode = s.getRandomNode();
            double ret = 0;
            for (IRandomNode.IRandomNodeAction rndAction: rndNode) {
                IAction a = rndAction.getAction();
                double actionProb = rndAction.getProb();
                ret += actionProb * callCfr.apply(s, a);
            }
            return ret;
        }

        IInformationSet is = s.getInfoSetForActingPlayer();
        double utility = 0;
        double[] actionUtility = new double[legalActions.size()];
        int i = 0;

        for (IAction a: legalActions) {
            double actionProb = strat.getProbability(is, a);
            double res = callCfr.apply(s, a);
            actionUtility[i] = res;
            utility = utility + actionProb*actionUtility[i];
            i++;
        }
        final double finUtility =  utility;
        listeners.forEach(listener -> listener.leavingState(tracker, info, finUtility));

        i = 0;
        double probWithoutActingPlayer = rndProb * PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), reachProb2, reachProb1); // reachProb_{-i}
        int pid = s.getActingPlayerId();
        for (IAction a: legalActions) {
            double playerMul = PlayerHelpers.selectByPlayerId(pid, 1, -1);
            regretMatching.addActionRegret(is, i, probWithoutActingPlayer * playerMul * (actionUtility[i] - utility));
            i++;
        }

        return utility;
    }

    public void registerListener(IListener listener) {
        if (listener != null) listeners.add(listener);
    }

}
