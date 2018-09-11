package com.ggp.players.deepstack;

import com.ggp.ICompleteInformationState;
import com.ggp.players.deepstack.utils.Strategy;

public interface IUtilityEstimator {
    class EstimatorResult {
        public double player1Utility;
        public double player2Utility;

        public EstimatorResult(double player1Utility, double player2Utility) {
            this.player1Utility = player1Utility;
            this.player2Utility = player2Utility;
        }
    }

    EstimatorResult estimate(ICompleteInformationState s, Strategy strat);
}
