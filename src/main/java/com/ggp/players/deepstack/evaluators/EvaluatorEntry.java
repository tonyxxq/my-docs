package com.ggp.players.deepstack.evaluators;

import com.ggp.players.deepstack.utils.Strategy;

public class EvaluatorEntry {
    private double intendedTimeMs;
    private double avgTimeMs = 0;
    private double avgTimeWeigthNorm = 0;
    private Strategy aggregatedStrat = new Strategy();

    public EvaluatorEntry(double intendedTimeMs) {
        this.intendedTimeMs = intendedTimeMs;
    }

    public EvaluatorEntry(double intendedTimeMs, double avgTimeMs, Strategy aggregatedStrat) {
        this.intendedTimeMs = intendedTimeMs;
        this.avgTimeMs = avgTimeMs;
        this.avgTimeWeigthNorm = 1;
        this.aggregatedStrat = aggregatedStrat;
    }

    public void addTime(double time, double weight) {
        avgTimeMs += time*weight;
        avgTimeWeigthNorm += weight;
    }

    public double getIntendedTimeMs() {
        return intendedTimeMs;
    }

    public double getEntryTimeMs() {
        return avgTimeMs/avgTimeWeigthNorm;
    }

    public Strategy getAggregatedStrat() {
        return aggregatedStrat;
    }
}
