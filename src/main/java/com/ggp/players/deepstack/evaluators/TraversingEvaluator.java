package com.ggp.players.deepstack.evaluators;

import com.ggp.*;
import com.ggp.players.deepstack.DeepstackPlayer;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.ISubgameResolver;
import com.ggp.players.deepstack.debug.BaseListener;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.PlayerHelpers;
import com.ggp.utils.TimedCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates Deepstack configuration by traversing the game tree and computing strategy at each decision point,
 * while aggregating the resulting strategies at given time intervals.
 */
public class TraversingEvaluator {
    private int initMs;
    private int timeoutMs;
    private ArrayList<Integer> logPointsMs;

    /**
     * Constructor
     * @param initMs timeout for deepstack initialization
     * @param logPointsMs ASC ordered list of times when strategies should be aggregated
     */
    public TraversingEvaluator(int initMs, List<Integer> logPointsMs) {
        this.initMs = initMs;
        this.timeoutMs = logPointsMs.get(logPointsMs.size() - 1);
        this.logPointsMs = new ArrayList<>(logPointsMs);
    }

    private DeepstackPlayer applyPercepts(DeepstackPlayer pl, int playerId, Iterable<IPercept> percepts) {
        for (IPercept p: percepts) {
            if (playerId == p.getTargetPlayer()) pl = pl.getNewPlayerByPercept(p);
        }
        return pl;
    }

    private void printAction(IAction a, int depth) {
        String indent = "";
        for (int i = 0; i < depth; ++i) indent += " ";
        System.out.println(indent + "Taking action " + a);
    }

    private DeepstackPlayer ensureDifference(DeepstackPlayer orig, DeepstackPlayer next) {
        if (orig == next) return orig.copy();
        return next;
    }

    private void aggregateStrategy(List<EvaluatorEntry> entries, ICompleteInformationState s, DeepstackPlayer pl1, DeepstackPlayer pl2, double reachProb1, double reachProb2, int depth) {
        if (s.isTerminal()) return;
        List<IAction> legalActions = s.getLegalActions();
        if (s.isRandomNode()) {
            double actionProb = 1d/legalActions.size();
            for (IAction a: legalActions) {
                Iterable<IPercept> percepts = s.getPercepts(a);
                DeepstackPlayer npl1 = applyPercepts(pl1, 1, percepts), npl2 = applyPercepts(pl2, 2, percepts);
                npl1 = ensureDifference(pl1, npl1);
                npl2 = ensureDifference(pl2, npl2);
                printAction(a, depth);
                aggregateStrategy(entries, s.next(a), npl1, npl2, reachProb1 * actionProb, reachProb2 * actionProb, depth + 1);
            }
            return;
        }
        double playerReachProb = PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), reachProb1, reachProb2);
        IInformationSet is = s.getInfoSetForActingPlayer();

        IResolvingListener stratAggregator = new BaseListener() {
            private TimedCounter timedCounter = new TimedCounter(logPointsMs);
            private int strategyIdx = 0;

            @Override
            public void resolvingStart(IResolvingInfo resInfo) {
                timedCounter.reset();
                timedCounter.start();
                strategyIdx = 0;
            }

            private void mergeStrategy(IResolvingInfo resInfo) {
                Strategy strat = resInfo.getUnnormalizedCumulativeStrategy();
                EvaluatorEntry entry = entries.get(strategyIdx);
                entry.addTime(timedCounter.getLiveDurationMs(), playerReachProb);
                Strategy target = entry.getAggregatedStrat();
                double norm = 0;
                for (IAction a: legalActions) {
                    norm += strat.getProbability(is, a);
                }
                if (norm > 0) {
                    final double finNorm = norm;
                    target.addProbabilities(is, a -> playerReachProb * strat.getProbability(is, a) / finNorm);
                } else {
                    target.addProbabilities(is, a -> playerReachProb * legalActions.size());
                }
            }

            @Override
            public void resolvingEnd(IResolvingInfo resInfo) {
                strategyIdx = logPointsMs.size() - 1;
                mergeStrategy(resInfo);
            }

            @Override
            public void resolvingIterationEnd(IResolvingInfo resInfo) {
                if (strategyIdx >= logPointsMs.size() - 1) return;
                int counter = timedCounter.tryIncrement();
                if (strategyIdx != counter) {
                    strategyIdx = counter - 1;
                    mergeStrategy(resInfo);
                    strategyIdx++;
                }
            }
        };

        DeepstackPlayer currentPlayer = PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), pl1, pl2);
        currentPlayer.registerResolvingListener(stratAggregator);
        ISubgameResolver.ActResult actResult = currentPlayer.computeStrategy(timeoutMs);
        currentPlayer.unregisterResolvingListener(stratAggregator);


        for (IAction a: legalActions) {
            double actionProb = actResult.cumulativeStrategy.getProbability(is, a);
            double nrp1 = reachProb1, nrp2 = reachProb2;
            DeepstackPlayer npl1 = pl1, npl2 = pl2;
            if (s.getActingPlayerId() == 1) {
                npl1 = npl1.getNewPlayerByAction(a, actResult);
                nrp1 *= actionProb;
            } else if (s.getActingPlayerId() == 2) {
                npl2 = npl2.getNewPlayerByAction(a, actResult);
                nrp2 *= actionProb;
            }
            Iterable<IPercept> percepts = s.getPercepts(a);
            npl1 = applyPercepts(npl1, 1, percepts);
            npl2 = applyPercepts(npl2, 2, percepts);
            npl1 = ensureDifference(pl1, npl1);
            npl2 = ensureDifference(pl2, npl2);
            printAction(a, depth);
            aggregateStrategy(entries, s.next(a), npl1, npl2, nrp1, nrp2, depth + 1);
        }
    }

    /**
     * Evaluates given Deepstack configuration on given game.
     * @param gameDesc
     * @param playerFactory
     * @return ASC ordered list of entries containing times and corresponding normalized aggregated strategies.
     */
    public List<EvaluatorEntry> evaluate(IGameDescription gameDesc, DeepstackPlayer.Factory playerFactory) {
        ICompleteInformationState initialState = gameDesc.getInitialState();
        DeepstackPlayer pl1 = playerFactory.create(gameDesc, 1), pl2 = playerFactory.create(gameDesc, 2);
        pl1.init(initMs);
        pl2.init(initMs);
        List<EvaluatorEntry> entries = new ArrayList<>(logPointsMs.size());
        for (int i = 0; i < logPointsMs.size(); ++i) {
            entries.add(new EvaluatorEntry(logPointsMs.get(i)));
        }
        aggregateStrategy(entries, initialState, pl1, pl2, 1d, 1d, 0);
        for (EvaluatorEntry entry: entries) {
            entry.getAggregatedStrat().normalize();
        }
        return entries;
    }
}
