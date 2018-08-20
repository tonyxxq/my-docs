package com.ggp.players.deepstack.rerget_matching;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.players.deepstack.IRegretMatching;
import com.ggp.players.deepstack.utils.Strategy;

import java.util.HashMap;
import java.util.List;

public class RegretMatchingPlus implements IRegretMatching {
    protected HashMap<IInformationSet, double[]> regrets = new HashMap<>();

    private double[] getOrCreateActionRegrets(IInformationSet is) {
        return regrets.computeIfAbsent(is, k -> new double[is.getLegalActions().size()]);
    }

    @Override
    public void addActionRegret(IInformationSet is, int actionIdx, double regretDiff) {
        double[] actionRegrets = getOrCreateActionRegrets(is);
        actionRegrets[actionIdx] = Math.max(0, actionRegrets[actionIdx] + regretDiff);
    }

    @Override
    public boolean hasInfoSet(IInformationSet is) {
        return regrets.containsKey(is);
    }

    @Override
    public void getRegretMatchedStrategy(IInformationSet is, Strategy strat) {
        double[] actionRegrets = getOrCreateActionRegrets(is);
        double totalRegret = 0;
        for (int i = 0; i < actionRegrets.length; ++i) totalRegret += actionRegrets[i];
        int actionIdx = 0;
        List<IAction> legalActions = is.getLegalActions();
        if (totalRegret > 0) {
            for (IAction a: legalActions) {
                strat.setProbability(is, a, actionRegrets[actionIdx]/totalRegret);
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
