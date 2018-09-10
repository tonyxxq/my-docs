package com.ggp.players.deepstack.debug;

import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.StopWatch;

import java.util.ArrayList;

public class StrategyAggregatorListener implements IResolvingListener {
    private final int logIntervalMs;
    private ArrayList<Strategy> strategies = new ArrayList<>();
    private StopWatch timer = new StopWatch();
    private int strategyIdx;

    public StrategyAggregatorListener(int logIntervalMs) {
        this.logIntervalMs = logIntervalMs;
    }

    @Override
    public void initEnd(IResolvingInfo resInfo) { }

    @Override
    public void resolvingStart(IResolvingInfo resInfo) {
        timer.reset();
        timer.start();
        strategyIdx = 0;
    }

    private void mergeStrategy(IResolvingInfo resInfo) {
        IStrategy strat = resInfo.getUnnormalizedCumulativeStrategy();
        if (strategies.size() <= strategyIdx) {
            strategies.add(new Strategy());
        }
        Strategy target = strategies.get(strategyIdx);
        for (IInformationSet is: strat.getDefinedInformationSets()) {
            target.addProbabilities(is, a -> strat.getProbability(is, a));
        }
    }

    @Override
    public void resolvingEnd(IResolvingInfo resInfo) {
        mergeStrategy(resInfo);
    }

    @Override
    public void stateVisited(ICompleteInformationState s, IResolvingInfo resInfo) {
    }

    @Override
    public void resolvingIterationEnd(IResolvingInfo resInfo) {
        if ((strategyIdx + 1) * logIntervalMs < timer.getLiveDurationMs()) {
            mergeStrategy(resInfo);
            strategyIdx++;
        }
    }

    public ArrayList<Strategy> getStrategies() {
        return strategies;
    }

    public int getLogIntervalMs() {
        return logIntervalMs;
    }
}
