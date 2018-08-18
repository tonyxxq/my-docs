package com.ggp.players.deepstack.resolvers;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.ICompleteInformationStateFactory;
import com.ggp.IInformationSet;
import com.ggp.players.deepstack.*;
import com.ggp.players.deepstack.utils.GameTreeTraversalTracker;
import com.ggp.players.deepstack.utils.InformationSetRange;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.PlayerHelpers;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CFRResolver implements ISubgameResolver {
    public static class Factory implements ISubgameResolver.Factory {
        private int iters;
        private ICFVEstimator cfvEstimator;
        private int depthLimit;

        public Factory(int iters, ICFVEstimator cfvEstimator, int depthLimit) {
            this.iters = iters;
            this.cfvEstimator = cfvEstimator;
            this.depthLimit = depthLimit;
        }

        @Override
        public ISubgameResolver create(int myId, InformationSetRange myRange, HashMap<IInformationSet, Double> opponentCFV,
                                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners)
        {
            return new CFRResolver(myId, iters, myRange, opponentCFV, cisFactory, resolvingListeners, cfvEstimator, depthLimit);
        }
    }

    private int myId;
    private int iters;
    private InformationSetRange range;
    private HashMap<IInformationSet, Double> opponentCFV;
    private ICompleteInformationStateFactory cisFactory;
    private ArrayList<IResolvingListener> resolvingListeners;
    private ICFVEstimator cfvEstimator;
    private int depthLimit = 2;
    private Strategy strat = new Strategy();
    private Strategy nextStrat = new Strategy();
    private Strategy cumulativeStrat = new Strategy();
    private HashMap<IInformationSet, double[]> regrets = new HashMap<>();
    private double[] regretsGadget;
    private HashMap<IInformationSet, Double> opponentFollowProb;
    private int opponentId;

    public CFRResolver(int myId, int iters, InformationSetRange range, HashMap<IInformationSet, Double> opponentCFV,
                       ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners,
                       ICFVEstimator cfvEstimator, int depthLimit)
    {
        this.myId = myId;
        this.iters = iters;
        this.range = range;
        this.opponentCFV = opponentCFV;
        this.cisFactory = cisFactory;
        this.resolvingListeners = resolvingListeners;
        this.cfvEstimator = cfvEstimator;
        this.depthLimit = depthLimit;
        regretsGadget = new double[2 * opponentCFV.size()];
        Arrays.fill(regretsGadget, 0d);
        opponentFollowProb = new HashMap<>(opponentCFV.size());
        for (IInformationSet os: opponentCFV.keySet()) {
            opponentFollowProb.put(os, 0.5);
        }
        opponentId = PlayerHelpers.getOpponentId(myId);
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
            double probWithoutOpponent = rndProb * PlayerHelpers.selectByPlayerId(myId, p1, p2);
            tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), probWithoutOpponent * cfv[opponentId - 1]);
        }
        // TODO: is it ok to compute both strategies at once??
        double totalRegret = 0;
        i = 0;
        double[] actionRegrets = regrets.getOrDefault(is, null);
        if (actionRegrets == null) {
            actionRegrets = new double[legalActions.size()];
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

    protected void findMyNextTurn(GameTreeTraversalTracker tracker) {
        ICompleteInformationState s = tracker.getCurrentState();
        if (s.isTerminal()) return;
        if (tracker.isMyNextTurnReached()) {
            tracker.getNtit().addLeaf(s.getInfoSetForPlayer(opponentId), 0);
            tracker.getPsMap().add(tracker.getMyPerceptSequence(), tracker.getOpponentPerceptSequence());
            tracker.getNrt().add(tracker.getOpponentPerceptSequence(), s, tracker.getMyTopAction(), tracker.getRndProb());
            return;
        }
        for (IAction a: s.getLegalActions()) {
            findMyNextTurn(tracker.next(a));
        }
    }

    private GameTreeTraversalTracker prepareDataStructures() {
        GameTreeTraversalTracker tracker = GameTreeTraversalTracker.createForAct(myId);
        for (Map.Entry<ICompleteInformationState, Double> stateProb: range.getProbabilities()) {
            GameTreeTraversalTracker stateTracker = tracker.visitRandom(stateProb.getKey(), stateProb.getValue());
            findMyNextTurn(stateTracker);
        }
        return tracker;
    }

    @Override
    public ActResult act() {
        onEvent((listener, info) -> listener.resolvingStart(info));
        GameTreeTraversalTracker tracker = prepareDataStructures();
        HashMap<IInformationSet, Double> currentOpponentCFV = new HashMap<>(opponentCFV.size());
        HashSet<IInformationSet> myInformationSets = new HashSet<>();
        for (ICompleteInformationState s: range.getPossibleStates()) {
            myInformationSets.add(s.getInfoSetForActingPlayer());
        }
        for (int i = 0; i < iters; ++i) {
            for (IInformationSet os: opponentCFV.keySet()) {
                currentOpponentCFV.put(os, 0d);
            }
            for (Map.Entry<ICompleteInformationState, Double> stateProb: range.getProbabilities()) {
                ICompleteInformationState s = stateProb.getKey();
                double rndProb = stateProb.getValue();
                GameTreeTraversalTracker stateTracker = tracker.visitRandom(s, rndProb);
                IInformationSet os = s.getInfoSetForPlayer(opponentId);
                CFRResult res = PlayerHelpers.callWithPlayerParams(myId, opponentFollowProb.get(os), 1d, (r1, r2) -> cfr(stateTracker, myId, 0, r1, r2));
                double osCFV = PlayerHelpers.selectByPlayerId(opponentId, res.player1CFV, res.player2CFV) * rndProb;
                currentOpponentCFV.merge(os, osCFV, (oldV, newV) -> oldV + newV);
            }
            for (IInformationSet os: opponentCFV.keySet()) {
                double osCFV = currentOpponentCFV.get(os);
                int osIdx = 0;

                double followProb = Math.max(regretsGadget[2*osIdx], 0);
                followProb = followProb/(followProb + Math.max(regretsGadget[2*osIdx + 1], 0));
                if (Double.isNaN(followProb)) followProb = 0.5; // during first iteration regretsGadget is 0
                opponentFollowProb.put(os, followProb);
                double gadgetValue = followProb * osCFV + (1 - followProb) * opponentCFV.getOrDefault(os, 0d);
                regretsGadget[2*osIdx + 1] += opponentCFV.getOrDefault(os, 0d)  - gadgetValue;
                regretsGadget[2*osIdx] += osCFV - gadgetValue;

                osIdx++;
            }
            strat = nextStrat;
            nextStrat = new Strategy();
            for (IInformationSet myIs: myInformationSets) {
                cumulativeStrat.addProbabilities(myIs, (action) -> strat.getProbability(myIs, action));
            }
            onEvent((listener, info) -> listener.resolvingIterationEnd(info));
        }
        cumulativeStrat.normalize();

        onEvent((listener, info) -> listener.resolvingEnd(info));
        return new ActResult(cumulativeStrat, tracker.getActionToNtit(), tracker.getActionToPsMap(), tracker.getMyISToNRT(), iters);
    }

    @Override
    public InitResult init(ICompleteInformationState initialState) {
        onEvent((listener, info) -> listener.resolvingStart(info));
        GameTreeTraversalTracker tracker = GameTreeTraversalTracker.createForInit(myId, initialState);
        findMyNextTurn(tracker);
        for (int i = 0; i < iters; ++i) {
            cfr(tracker, myId, 0, 1, 1);
            onEvent((listener, info) -> listener.resolvingIterationEnd(info));
        }
        onEvent((listener, info) -> listener.resolvingEnd(info));
        onEvent((listener, info) -> listener.initEnd(info));
        return new InitResult(tracker.getNtit(), tracker.getNrt(), tracker.getPsMap(), iters);
    }
}
