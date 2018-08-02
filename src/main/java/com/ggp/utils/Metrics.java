package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;

import java.util.List;

public class Metrics {
    public static double getStrategyMSE(IStrategy normalizedTarget, IStrategy unnormalizedCurrent) {
        int isCount = 0;
        double errSum = 0;
        for (IInformationSet is: unnormalizedCurrent.getDefinedInformationSets()) {
            List<IAction> legalActions = is.getLegalActions();
            if (legalActions == null || legalActions.isEmpty()) continue;
            isCount++;
            double total = 0;
            for (IAction a: legalActions) {
                total += unnormalizedCurrent.getProbability(is, a);
            }
            for (IAction a: legalActions) {
                double diff = normalizedTarget.getProbability(is, a);
                if (total > 0) {
                    diff -= unnormalizedCurrent.getProbability(is, a)/total;
                } else {
                    diff -= 1d/legalActions.size();
                }
                errSum += diff*diff;
            }
        }
        if (isCount > 0) return errSum/isCount;
        return 0;
    }
}
