package com.ggp.players.deepstack.regret_matching;


import com.ggp.players.deepstack.IRegretMatching;

public class RegretMatchingPlus extends BaseRegretMatching {
    public static class Factory implements IRegretMatching.Factory {
        @Override
        public IRegretMatching create() {
            return new RegretMatchingPlus();
        }

        @Override
        public String getConfigString() {
            return "RM+";
        }
    }

    @Override
    protected double sumRegrets(double r1, double r2) {
        return Math.max(0, r1 + r2);
    }
}
