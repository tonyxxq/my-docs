package com.ggp.players.deepstack;

import com.ggp.ICompleteInformationState;
import com.ggp.players.deepstack.utils.Strategy;

public interface ICFVEstimator {
    class EstimatorResult {
        public double player1CFV;
        public double player2CFV;

        public EstimatorResult(double player1CFV, double player2CFV) {
            this.player1CFV = player1CFV;
            this.player2CFV = player2CFV;
        }
    }

    EstimatorResult estimate(ICompleteInformationState s, Strategy strat);
}
