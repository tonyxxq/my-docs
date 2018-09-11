package com.ggp.players.deepstack.regret_matching;


public class RegretMatchingPlus extends BaseRegretMatching {
    @Override
    protected double sumRegrets(double r1, double r2) {
        return Math.max(0, r1 + r2);
    }
}
