package com.ggp.players.deepstack.debug;

import com.ggp.IInformationSet;
import com.ggp.IStrategy;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.evaluators.EvaluatorEntry;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.TimedCounter;

import java.util.ArrayList;
import java.util.List;

public class StrategyAggregatorListener extends BaseListener {
    private ArrayList<Integer> logPointsMs;
    private ArrayList<EvaluatorEntry> entries;
    private TimedCounter timedCounter;
    private int strategyIdx;

    public StrategyAggregatorListener(List<Integer> logPointsMs) {
        this.logPointsMs = new ArrayList<>(logPointsMs);
        this.timedCounter = new TimedCounter(logPointsMs);
        this.entries = new ArrayList<>(logPointsMs.size());
        for (int i = 0; i < logPointsMs.size(); ++i) {
            entries.add(new EvaluatorEntry(logPointsMs.get(i)));
        }
    }

    @Override
    public void resolvingStart(IResolvingInfo resInfo) {
        timedCounter.reset();
        timedCounter.start();
        strategyIdx = 0;
    }

    private void mergeStrategy(IResolvingInfo resInfo) {
        IStrategy strat = resInfo.getUnnormalizedCumulativeStrategy();
        EvaluatorEntry entry = entries.get(strategyIdx);
        entry.addTime(timedCounter.getLiveDurationMs(), 1);
        Strategy target = entry.getAggregatedStrat();
        for (IInformationSet is: strat.getDefinedInformationSets()) {
            target.addProbabilities(is, a -> strat.getProbability(is, a));
        }
    }

    @Override
    public void resolvingEnd(IResolvingInfo resInfo) {
        if (!hasInitEnded()) return;
        strategyIdx = logPointsMs.size() - 1;
        mergeStrategy(resInfo);
    }

    @Override
    public void resolvingIterationEnd(IResolvingInfo resInfo) {
        if (!hasInitEnded()) return;
        if (strategyIdx >= logPointsMs.size() - 1) return;
        int counter = timedCounter.tryIncrement();
        if (strategyIdx != counter) {
            strategyIdx = counter - 1;
            mergeStrategy(resInfo);
            strategyIdx++;
        }
    }

    public List<EvaluatorEntry> getEntries() {
        return entries;
    }

}
