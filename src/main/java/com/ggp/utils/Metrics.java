package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;

import java.util.List;

public class Metrics {
    public static double getStrategyMSE(IStrategy normalizedTarget, IStrategy unnormalizedCurrent, IInformationSet is) {
        double errSum = 0;
        List<IAction> legalActions = is.getLegalActions();
        if (legalActions == null || legalActions.isEmpty()) return errSum;
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
        return errSum;
    }

    public static double getStrategyMSE(IStrategy normalizedTarget, IStrategy unnormalizedCurrent) {
        int isCount = 0;
        double errSum = 0;
        for (IInformationSet is: unnormalizedCurrent.getDefinedInformationSets()) {
            isCount++;
            errSum += getStrategyMSE(normalizedTarget, unnormalizedCurrent, is);
        }
        if (isCount > 0) return errSum/isCount;
        return 0;
    }
}
