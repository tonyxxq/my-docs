package com.ggp.players.deepstack.estimators;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.players.deepstack.ICFVEstimator;
import com.ggp.players.deepstack.Strategy;
import com.ggp.utils.RandomItemSelector;

import java.util.List;

public class RandomPlayoutCFVEstimator implements ICFVEstimator {
    private int iters = 5;
    private RandomItemSelector<IAction> rnd = new RandomItemSelector<>();
    @Override
    public EstimatorResult estimate(ICompleteInformationState s, Strategy strat) {
        if (s.isTerminal()) {
            return new EstimatorResult(s.getPayoff(1), s.getPayoff(2));
        }
        double cfv1 = 0, cfv2 = 0;
        for (int i = 0; i < iters; ++i) {
            ICompleteInformationState ws = s;
            while (!ws.isTerminal()) {
                List<IAction> legalActions = ws.getLegalActions();
                IAction a = rnd.select(legalActions);
                ws = ws.next(a);
            }
            cfv1 += ws.getPayoff(1);
            cfv2 += ws.getPayoff(2);
        }
        return new EstimatorResult(cfv1/iters, cfv2/iters);
    }
}
