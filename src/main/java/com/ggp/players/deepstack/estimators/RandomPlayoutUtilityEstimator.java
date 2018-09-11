package com.ggp.players.deepstack.estimators;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.players.deepstack.IUtilityEstimator;
import com.ggp.players.deepstack.utils.Strategy;
import com.ggp.utils.RandomItemSelector;

import java.util.List;

public class RandomPlayoutUtilityEstimator implements IUtilityEstimator {
    private int iters = 5;
    private RandomItemSelector<IAction> rnd = new RandomItemSelector<>();
    @Override
    public EstimatorResult estimate(ICompleteInformationState s, Strategy strat) {
        if (s.isTerminal()) {
            return new EstimatorResult(s.getPayoff(1), s.getPayoff(2));
        }
        double u1 = 0, u2 = 0;
        for (int i = 0; i < iters; ++i) {
            ICompleteInformationState ws = s;
            double prob = 1;
            while (!ws.isTerminal()) {
                List<IAction> legalActions = ws.getLegalActions();
                prob *= 1/legalActions.size();
                IAction a = rnd.select(legalActions);
                ws = ws.next(a);
            }
            u1 += prob*ws.getPayoff(1);
            u2 += prob*ws.getPayoff(2);
        }
        return new EstimatorResult(u1/iters, u2/iters);
    }
}
