package com.ggp.players.deepstack.resolvers;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.ICompleteInformationStateFactory;
import com.ggp.IInformationSet;
import com.ggp.players.deepstack.*;
import com.ggp.players.deepstack.utils.GameTreeTraversalTracker;
import com.ggp.players.deepstack.utils.InformationSetRange;
import com.ggp.players.deepstack.utils.IterationTimer;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.PlayerHelpers;

import java.util.*;
import java.util.function.BiFunction;

public class CFRResolver extends BaseCFRSolver implements ISubgameResolver {
    public static class Factory implements ISubgameResolver.Factory {
        private ICFVEstimator cfvEstimator;
        private int depthLimit;

        public Factory(ICFVEstimator cfvEstimator, int depthLimit) {
            this.cfvEstimator = cfvEstimator;
            this.depthLimit = depthLimit;
        }

        @Override
        public ISubgameResolver create(int myId, InformationSetRange myRange, HashMap<IInformationSet, Double> opponentCFV,
                                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners)
        {
            return new CFRResolver(myId, myRange, opponentCFV, cisFactory, resolvingListeners, cfvEstimator, depthLimit);
        }
    }

    private ICFVEstimator cfvEstimator;
    private int depthLimit = 2;
    private Strategy strat = new Strategy();
    private Strategy nextStrat = new Strategy();

    public CFRResolver(int myId, InformationSetRange range, HashMap<IInformationSet, Double> opponentCFV,
                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners,
                       ICFVEstimator cfvEstimator, int depthLimit)
    {
        super(myId, range, opponentCFV, resolvingListeners);
        this.cfvEstimator = cfvEstimator;
        this.depthLimit = depthLimit;
    }

    private static class CFRResult {
        public double player1CFV;
        public double player2CFV;

        public CFRResult(double player1CFV, double player2CFV) {
            this.player1CFV = player1CFV;
            this.player2CFV = player2CFV;
        }
    }

    private CFRResult cfr(GameTreeTraversalTracker tracker, int player, int depth, double p1, double p2) {
        ICompleteInformationState s = tracker.getCurrentState();
        resolvingListeners.forEach(listener -> listener.stateVisited(s, resInfo));

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
            double probWithoutOpponent = rndProb * PlayerHelpers.selectByPlayerId(myId, p1, p2);
            tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), probWithoutOpponent * cfv[opponentId - 1]);
        }
        // TODO: is it ok to compute both strategies at once??
        i = 0;
        double probWithoutActingPlayer = rndProb * PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), p2, p1); // \pi_{-i}
        int pid = s.getActingPlayerId();
        for (IAction a: legalActions) {
            regretMatching.addActionRegret(is, i, probWithoutActingPlayer*(actionCFV[2*i + pid - 1] - cfv[pid - 1]));
            i++;
        }
        regretMatching.getRegretMatchedStrategy(is, nextStrat);

        CFRResult ret = new CFRResult(cfv[0], cfv[1]);

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
                double rndProb = stateProb.getValue();
                GameTreeTraversalTracker stateTracker = tracker.visitRandom(s, rndProb);
                IInformationSet os = s.getInfoSetForPlayer(opponentId);
                CFRResult res = PlayerHelpers.callWithOrderedParams(myId, subgameGadget.getFollowProb(os), 1d, (r1, r2) -> cfr(stateTracker, myId, 0, r1, r2));
                double osCFV = PlayerHelpers.selectByPlayerId(opponentId, res.player1CFV, res.player2CFV) * rndProb;
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
