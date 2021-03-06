package com.ggp.players.deepstack.resolvers;

import com.ggp.*;
import com.ggp.players.deepstack.IRegretMatching;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.ISubgameResolver;
import com.ggp.players.deepstack.regret_matching.RegretMatchingPlus;
import com.ggp.players.deepstack.trackers.GameTreeTraversalTracker;
import com.ggp.players.deepstack.utils.InformationSetRange;
import com.ggp.players.deepstack.utils.IterationTimer;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.random.RandomSampler;

import java.util.*;

public class MCCFRResolver extends BaseCFRResolver implements ISubgameResolver {
    public static class Factory implements ISubgameResolver.Factory {
        private double targetingProb = 0d;
        private double explorationProb = 0.2d;
        private IRegretMatching.Factory rmFactory = new RegretMatchingPlus.Factory();

        @Override
        public ISubgameResolver create(int myId, IInformationSet hiddenInfo, InformationSetRange myRange, HashMap<IInformationSet, Double> opponentCFV, ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners) {
            return new MCCFRResolver(myId, hiddenInfo, myRange, opponentCFV, resolvingListeners, rmFactory.create(), targetingProb, explorationProb);
        }

        @Override
        public String getConfigString() {
            return "MC-CFR{" +
                    ", t=" + targetingProb +
                    ", e=" + explorationProb +
                    ", rm=" + rmFactory.getConfigString() +
                    '}';
        }
    }

    private Strategy strat = new Strategy();
    private double targetingProb;
    private double explorationProb;
    private RandomSampler sampler = new RandomSampler();

    public MCCFRResolver(int myId, IInformationSet hiddenInfo, InformationSetRange range, HashMap<IInformationSet, Double> opponentCFV,
                         List<IResolvingListener> resolvingListeners, IRegretMatching regretMatching,
                         double targetingProb, double explorationProb) {
        super(myId, hiddenInfo, range, opponentCFV, resolvingListeners, regretMatching);
        this.targetingProb = targetingProb;
        this.explorationProb = explorationProb;
    }

    private static class CFRResult {
        public double suffixReachProb;
        public double sampleProb;
        public double payoff;

        public CFRResult(double suffixReachProb, double sampleProb, double payoff) {
            this.suffixReachProb = suffixReachProb;
            this.sampleProb = sampleProb;
            this.payoff = payoff;
        }
    }

    private static class SampleResult {
        public IAction action;
        public double targetedProb;
        public double untargetedProb;

        public SampleResult(IAction action, double targetedProb, double untargetedProb) {
            this.action = action;
            this.targetedProb = targetedProb;
            this.untargetedProb = untargetedProb;
        }
    }

    private SampleResult sampleRandom(ICompleteInformationState s) {
        List<IAction> legalActions = s.getLegalActions();
        IRandomNode rndNode = s.getRandomNode();
        RandomSampler.SampleResult<IAction> res = sampler.select(legalActions, a -> rndNode.getActionProb(a));
        return new SampleResult(res.getResult(), res.getSampleProb(), res.getSampleProb());
    }

    private SampleResult samplePlayerAction(ICompleteInformationState s, IInformationSet is, int player) {
        List<IAction> legalActions = is.getLegalActions();
        double unifPart = explorationProb * 1d/legalActions.size();
        int actingPlayer = s.getActingPlayerId();
        RandomSampler.SampleResult<IAction> res;
        if (actingPlayer == player) {
            res = sampler.select(legalActions, a -> unifPart + (1-explorationProb) * strat.getProbability(is, a));
        } else {
            res = sampler.select(legalActions, a -> strat.getProbability(is, a));
        }
        return new SampleResult(res.getResult(), res.getSampleProb(), res.getSampleProb());
    }

    private CFRResult playout(ICompleteInformationState s, double prefixProb, int player) {
        double suffixProb = 1;
        while (!s.isTerminal()) {
            SampleResult res;
            if (s.isRandomNode()) {
                res = sampleRandom(s);
            } else {
                List<IAction> legalActions = s.getLegalActions();
                res = new SampleResult(sampler.select(legalActions),
                        1d/legalActions.size(), 1d/legalActions.size());
            }

            suffixProb *= res.untargetedProb;
            s = s.next(res.action);
        }
        return new CFRResult(suffixProb, prefixProb * suffixProb, s.getPayoff(player));
    }

    private CFRResult cfr(GameTreeTraversalTracker tracker, double playerProb, double probWithoutPlayer,
                          double targetedSampleProb, double untargetedSampleProb, int player) {
        ICompleteInformationState s = tracker.getCurrentState();
        resolvingListeners.forEach(listener -> listener.stateVisited(s, resInfo));
        double totalSampleProb = targetingProb * targetedSampleProb + (1-targetingProb) * untargetedSampleProb;
        if (s.isTerminal()) {
            return new CFRResult(1,
                    totalSampleProb,
                    s.getPayoff(player));
        } else if (s.isRandomNode()) {
            SampleResult sample = sampleRandom(s);
            CFRResult res = cfr(tracker.next(sample.action), playerProb, sample.untargetedProb * probWithoutPlayer,
                    sample.targetedProb * targetedSampleProb, sample.untargetedProb * untargetedSampleProb, player);
            res.suffixReachProb *= sample.untargetedProb;
            return res;
        }

        IInformationSet actingPlayerInfoSet = s.getInfoSetForActingPlayer();
        List<IAction> legalActions = actingPlayerInfoSet.getLegalActions();
        if (legalActions == null || legalActions.isEmpty()) return null;
        SampleResult sampledAction = samplePlayerAction(s, actingPlayerInfoSet, player);
        CFRResult ret;
        double actionProb = strat.getProbability(actingPlayerInfoSet, sampledAction.action);
        if (regretMatching.hasInfoSet(actingPlayerInfoSet)) {
            regretMatching.getRegretMatchedStrategy(actingPlayerInfoSet, strat);
            double newPlayerProb = playerProb;
            double newProbWithoutPlayer = probWithoutPlayer;
            if (s.getActingPlayerId() == player) {
                newPlayerProb *= actionProb;
            } else {
                newProbWithoutPlayer *= actionProb;
            }
            ret = cfr(tracker.next(sampledAction.action), newPlayerProb, newProbWithoutPlayer,
                    sampledAction.targetedProb * targetedSampleProb,
                    sampledAction.untargetedProb * untargetedSampleProb, player);
        } else {
            regretMatching.initInfoSet(actingPlayerInfoSet);
            ret = playout(s, (totalSampleProb)/legalActions.size(), player);
        }

        double newSuffixReachProb = actionProb * ret.suffixReachProb;
        int actingPlayer = s.getActingPlayerId();
        double w = ret.payoff * probWithoutPlayer / ret.sampleProb;
        double cfv = w * newSuffixReachProb;
        double sampledActionCfv = w * ret.suffixReachProb;

        if (tracker.isMyNextTurnReached() && player == opponentId) {
            tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), cfv);
        }

        if (actingPlayer == player) {
            int actionIdx = 0;
            for (IAction a: legalActions) {
                double regretDiff;
                if (a.equals(sampledAction.action)) {
                    regretDiff = sampledActionCfv - cfv;
                } else {
                    regretDiff = -cfv;
                }
                regretMatching.addActionRegret(actingPlayerInfoSet, actionIdx, regretDiff);
                actionIdx++;
            }
        } else {
            if (actingPlayer == myId && range.getPossibleStates().contains(s)) {
                cumulativeStrat.addProbabilities(actingPlayerInfoSet, action ->
                        probWithoutPlayer*strat.getProbability(actingPlayerInfoSet, action)/totalSampleProb);
            }
        }
        ret.suffixReachProb = newSuffixReachProb;
        return ret;
    }

    private static class SubgameSample {
        public ICompleteInformationState state;
        public double targetedProb;
        public double untargetedProb;

        public SubgameSample(ICompleteInformationState state, double targetedProb, double untargetedProb) {
            this.state = state;
            this.targetedProb = targetedProb;
            this.untargetedProb = untargetedProb;
        }
    }

    private SubgameSample sampleSubgame() {
        double norm = range.getNorm();
        RandomSampler.SampleResult<ICompleteInformationState> res = sampler.select(range.getPossibleStates(), s -> range.getProbability(s)/norm);
        return new SubgameSample(res.getResult(), res.getSampleProb() * norm, res.getSampleProb() * norm);
    }

    @Override
    protected ActResult doAct(GameTreeTraversalTracker tracker, IterationTimer timeout) {
        double remaining = 0;
        int nextIter = 1;
        int realIters = 0;
        while (timeout.canDoAnotherIteration()) {
            timeout.startIteration();
            {
                SubgameSample subgameSample = sampleSubgame();
                ICompleteInformationState s = subgameSample.state;
                GameTreeTraversalTracker stateTracker = tracker.visit(s);
                IInformationSet os = s.getInfoSetForPlayer(opponentId);
                CFRResult res = cfr(stateTracker, 1d, subgameGadget.getFollowProb(os) * subgameSample.untargetedProb, subgameSample.targetedProb, subgameSample.untargetedProb, myId);
                remaining += res.sampleProb;
            }
            {
                SubgameSample subgameSample = sampleSubgame();
                ICompleteInformationState s = subgameSample.state;
                GameTreeTraversalTracker stateTracker = tracker.visit(s);
                IInformationSet os = s.getInfoSetForPlayer(opponentId);
                CFRResult res = cfr(stateTracker, subgameGadget.getFollowProb(os), subgameSample.untargetedProb, subgameSample.targetedProb, subgameSample.untargetedProb, opponentId);
                double followCFV = subgameSample.untargetedProb * res.payoff * res.suffixReachProb / res.sampleProb;
                subgameGadget.addFollowCFV(os, followCFV);
                remaining += res.sampleProb;
            }

            if (remaining >= nextIter) {
                resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
                nextIter++;
            }
            realIters++;
            timeout.endIteration();
        }
        resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
        cumulativeStrat.normalize();

        return new ActResult(cumulativeStrat, tracker.getActionToNtit(), tracker.getActionToPsMap(), tracker.getMyISToNRT(), realIters);
    }

    @Override
    protected InitResult doInit(GameTreeTraversalTracker tracker, IterationTimer timeout) {
        double remaining = 0;
        int nextIter = 1;
        int realIters = 0;
        while (timeout.canDoAnotherIteration()) {
            timeout.startIteration();
            CFRResult res = cfr(tracker, 1d, 1d, 1d, 1d, myId);
            remaining += res.sampleProb;
            res = cfr(tracker, 1d, 1d, 1d, 1d, opponentId);
            remaining += res.sampleProb;
            if (remaining >= nextIter) {
                resolvingListeners.forEach(listener -> listener.resolvingIterationEnd(resInfo));
                nextIter++;
            }
            realIters++;
            timeout.endIteration();
        }
        return new InitResult(tracker.getNtit(), tracker.getNrt(), tracker.getPsMap(), realIters);
    }
}
