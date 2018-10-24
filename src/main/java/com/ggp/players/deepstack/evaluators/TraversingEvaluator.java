package com.ggp.players.deepstack.evaluators;

import com.ggp.*;
import com.ggp.players.deepstack.DeepstackPlayer;
import com.ggp.players.deepstack.ISubgameResolver;
import com.ggp.players.deepstack.debug.StrategyAggregatorListener;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.CompleteInformationStateWrapper;
import com.ggp.utils.PlayerHelpers;
import com.ggp.utils.recall.PerfectRecallGameDescriptionWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Evaluates Deepstack configuration by traversing the game tree and computing strategy at each decision point,
 * while aggregating the resulting strategies at given time intervals.
 */
public class TraversingEvaluator implements IDeepstackEvaluator {
    private int initMs;
    private int count;
    private int timeoutMs;
    private ArrayList<Integer> logPointsMs;

    /**
     * Constructor
     * @param initMs timeout for deepstack initialization
     * @param count how many times to traverse the game tree
     * @param logPointsMs ASC ordered list of times when strategies should be aggregated
     */
    public TraversingEvaluator(int initMs, int count, List<Integer> logPointsMs) {
        this.initMs = initMs;
        this.count = count;
        this.timeoutMs = logPointsMs.get(logPointsMs.size() - 1);
        this.logPointsMs = new ArrayList<>(logPointsMs);
    }

    private void applyPercepts(DeepstackPlayer pl1, DeepstackPlayer pl2, Iterable<IPercept> percepts) {
        for (IPercept p: percepts) {
            PlayerHelpers.selectByPlayerId(p.getTargetPlayer(), pl1, pl2).receivePercepts(p);
        }
    }

    private String getActionStr(int actionIdx, int actions) {
        return " " + (actionIdx + 1) + "/" + actions;
    }

    private void printAction(int actionIdx, int actions) {
        System.out.print(getActionStr(actionIdx, actions));
    }

    private void unprintAction(int actionIdx, int actions) {
        String tmp = "";
        String actionStr = getActionStr(actionIdx, actions);
        String clearStr = "";
        for (int i = 0; i < actionStr.length(); ++i) {
            tmp += "\b";
            clearStr += " ";
        }
        System.out.print(tmp + clearStr + tmp);
    }

    private static class ActCacheEntry {
        public ISubgameResolver.ActResult actResult;
        public DeepstackPlayer playerWithComputedStrat;
        public List<EvaluatorEntry> entries;

        public ActCacheEntry(ISubgameResolver.ActResult actResult, DeepstackPlayer playerWithComputedStrat, List<EvaluatorEntry> entries) {
            this.actResult = actResult;
            this.playerWithComputedStrat = playerWithComputedStrat;
            this.entries = entries;
        }
    }

    private void aggregateStrategy(HashMap<IInformationSet, ActCacheEntry> actCache, List<EvaluatorEntry> entries, CompleteInformationStateWrapper sw, DeepstackPlayer pl1, DeepstackPlayer pl2, double reachProb1, double reachProb2, int depth) {
        ICompleteInformationState s = sw.getOrigState();
        if (s.isTerminal()) return;

        List<IAction> legalActions = s.getLegalActions();
        if (s.isRandomNode()) {
            IRandomNode rndNode = s.getRandomNode();
            int actionIdx = 0;
            for (IRandomNode.IRandomNodeAction rndAction: rndNode) {
                IAction a = rndAction.getAction();
                double actionProb = rndAction.getProb();
                DeepstackPlayer npl1 = pl1.copy(), npl2 = pl2.copy();
                applyPercepts(npl1, npl2, s.getPercepts(a));
                printAction(actionIdx, legalActions.size());
                aggregateStrategy(actCache, entries, (CompleteInformationStateWrapper) sw.next(a), npl1, npl2, reachProb1 * actionProb, reachProb2 * actionProb, depth + 1);
                unprintAction(actionIdx, legalActions.size());
                actionIdx++;
            }
            return;
        }
        double playerReachProb = PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), reachProb1, reachProb2);
        IInformationSet is = s.getInfoSetForActingPlayer();

        ActCacheEntry cacheEntry = actCache.computeIfAbsent(sw.getInfoSetForActingPlayer(), k -> {
            DeepstackPlayer currentPlayer = PlayerHelpers.selectByPlayerId(s.getActingPlayerId(), pl1, pl2);
            StrategyAggregatorListener strategyAggregatorListener = new StrategyAggregatorListener(logPointsMs);
            strategyAggregatorListener.initEnd(null);
            currentPlayer.registerResolvingListener(strategyAggregatorListener);
            ISubgameResolver.ActResult res = currentPlayer.computeStrategy(timeoutMs);
            currentPlayer.unregisterResolvingListener(strategyAggregatorListener);
            return new ActCacheEntry(res, currentPlayer, strategyAggregatorListener.getEntries());
        });

        ISubgameResolver.ActResult actResult = cacheEntry.actResult;
        for (int i = 0; i < entries.size(); ++i) {
            EvaluatorEntry cachedEntry = cacheEntry.entries.get(i);
            Strategy cachedStrat = cachedEntry.getAggregatedStrat();
            entries.get(i).addTime(cachedEntry.getEntryTimeMs(), playerReachProb);
            entries.get(i).getAggregatedStrat().addProbabilities(is, a -> playerReachProb * cachedStrat.getProbability(is, a));
        }

        int actionIdx = 0;
        for (IAction a: legalActions) {
            double actionProb = actResult.cumulativeStrategy.getProbability(is, a);
            double nrp1 = reachProb1, nrp2 = reachProb2;
            DeepstackPlayer npl1 = null, npl2 = null;
            if (s.getActingPlayerId() == 1) {
                npl1 = cacheEntry.playerWithComputedStrat.copy();
                npl2 = pl2.copy();
                npl1.act(a, cacheEntry.actResult);
                nrp1 *= actionProb;
            } else if (s.getActingPlayerId() == 2) {
                npl1 = pl1.copy();
                npl2 = cacheEntry.playerWithComputedStrat.copy();
                npl2.act(a, cacheEntry.actResult);
                nrp2 *= actionProb;
            }
            applyPercepts(npl1, npl2, s.getPercepts(a));
            printAction(actionIdx, legalActions.size());
            aggregateStrategy(actCache, entries, (CompleteInformationStateWrapper) sw.next(a), npl1, npl2, nrp1, nrp2, depth + 1);
            unprintAction(actionIdx, legalActions.size());
            actionIdx++;
        }
    }

    @Override
    public List<EvaluatorEntry> evaluate(IGameDescription gameDesc, ISubgameResolver.Factory subgameResolverFactory) {
        DeepstackPlayer.Factory playerFactory = new DeepstackPlayer.Factory(subgameResolverFactory, null);
        ICompleteInformationState initialState = gameDesc.getInitialState();
        DeepstackPlayer pl1 = playerFactory.create(gameDesc, 1), pl2 = playerFactory.create(gameDesc, 2);
        pl1.init(initMs);
        pl2.init(initMs);
        List<EvaluatorEntry> entries = new ArrayList<>(logPointsMs.size());
        for (int i = 0; i < logPointsMs.size(); ++i) {
            entries.add(new EvaluatorEntry(logPointsMs.get(i)));
        }
        for (int i  = 0; i < count; ++i) {
            HashMap<IInformationSet, ActCacheEntry> actCache = new HashMap<>();
            aggregateStrategy(actCache, entries, (CompleteInformationStateWrapper) PerfectRecallGameDescriptionWrapper.wrapInitialState(initialState), pl1.copy(), pl2.copy(), 1d, 1d, 0);
        }

        for (EvaluatorEntry entry: entries) {
            entry.getAggregatedStrat().normalize();
        }
        return entries;
    }

    @Override
    public String getConfigString() {
        return "Traversing{" +
                "i=" + initMs +
                ",c=" + count +
                '}';
    }
}
