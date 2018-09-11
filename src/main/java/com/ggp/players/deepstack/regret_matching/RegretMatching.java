package com.ggp.players.deepstack.regret_matching;

public class RegretMatching extends BaseRegretMatching {
    @Override
    protected double sumRegrets(double r1, double r2) {
        return r1 + r2;
    }
}
