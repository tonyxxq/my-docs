package com.ggp.players.deepstack.debug;

import com.ggp.ICompleteInformationState;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.Metrics;

public class OptimalStrategyMSEListener implements IResolvingListener {
    private Strategy optimal;
    private int skipIterations = 5000;
    private int currentIteration;
    private boolean initEnded = false;

    public OptimalStrategyMSEListener(Strategy optimal) {
        this.optimal = optimal;
    }

    @Override
    public void initEnd(IResolvingInfo resInfo) {
        initEnded = true;
    }

    @Override
    public void resolvingStart(IResolvingInfo resInfo) {
        currentIteration = 0;
    }

    @Override
    public void resolvingEnd(IResolvingInfo resInfo) {

    }

    @Override
    public void stateVisited(ICompleteInformationState s, IResolvingInfo resInfo) {

    }

    @Override
    public void resolvingIterationEnd(IResolvingInfo resInfo) {
        if (!initEnded) return;
        currentIteration++;
        if (currentIteration % skipIterations == 1) {
            System.out.println(String.format("Optimal strategy MSE: %e", Metrics.getStrategyMSE(optimal, resInfo.getUnnormalizedCumulativeStrategy())));
        }
    }
}
