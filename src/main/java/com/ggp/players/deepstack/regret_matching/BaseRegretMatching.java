package com.ggp.players.deepstack.regret_matching;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.players.deepstack.IRegretMatching;
import com.ggp.players.deepstack.utils.Strategy;

import java.util.HashMap;
import java.util.List;

abstract class BaseRegretMatching implements IRegretMatching {
    protected HashMap<IInformationSet, double[]> regrets = new HashMap<>();

    private double[] getOrCreateActionRegrets(IInformationSet is) {
        return regrets.computeIfAbsent(is, k -> new double[is.getLegalActions().size()]);
    }

    protected abstract double sumRegrets(double r1, double r2);

    @Override
    public void addActionRegret(IInformationSet is, int actionIdx, double regretDiff) {
        double[] actionRegrets = getOrCreateActionRegrets(is);
        actionRegrets[actionIdx] = sumRegrets(actionRegrets[actionIdx], regretDiff);
    }

    @Override
    public boolean hasInfoSet(IInformationSet is) {
        return regrets.containsKey(is);
    }

    @Override
    public void getRegretMatchedStrategy(IInformationSet is, Strategy strat) {
        double[] actionRegrets = getOrCreateActionRegrets(is);
        double totalRegret = 0;
        for (int i = 0; i < actionRegrets.length; ++i) totalRegret += Math.max(0, actionRegrets[i]);
        int actionIdx = 0;
        List<IAction> legalActions = is.getLegalActions();
        if (totalRegret > 0) {
            for (IAction a: legalActions) {
                strat.setProbability(is, a, Math.max(0, actionRegrets[actionIdx])/totalRegret);
                actionIdx++;
            }
        } else {
            strat.setProbabilities(is, (action) -> 1d/legalActions.size());
        }
    }

    @Override
    public void initInfoSet(IInformationSet is) {
        getOrCreateActionRegrets(is);
    }
}
