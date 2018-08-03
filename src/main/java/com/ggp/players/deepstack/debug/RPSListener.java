package com.ggp.players.deepstack.debug;

import com.ggp.ICompleteInformationState;
import com.ggp.IStrategy;
import com.ggp.games.RockPaperScissors.ExploitabilityEstimator;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.players.deepstack.Strategy;
import com.ggp.utils.Metrics;

public class RPSListener implements IResolvingListener {
    private int skipIterations = 5000;
    private int currentIteration;
    private boolean initEnded = false;
    private Strategy optimal = new Strategy();
    private ExploitabilityEstimator exploitabilityEstimator;

    public RPSListener(ExploitabilityEstimator exploitabilityEstimator) {
        this.exploitabilityEstimator = exploitabilityEstimator;
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
            IStrategy cumulStrat = resInfo.getUnnormalizedCumulativeStrategy();
            System.out.println(String.format("Optimal strategy MSE: %e, Exploitability: %e",
                    Metrics.getStrategyMSE(optimal, cumulStrat),
                    exploitabilityEstimator.estimate(cumulStrat)));
        }
    }
}
