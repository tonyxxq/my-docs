package com.ggp.players.deepstack.utils;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class Strategy implements IStrategy {
    private static final long serialVersionUID = 1L;
    HashMap<IInformationSet, HashMap<IAction, Double>> strategy = new HashMap<>();

    private HashMap<IAction, Double> getSetStragety(IInformationSet s) {
        return strategy.getOrDefault(s, null);
    }

    private HashMap<IAction, Double> getOrCreateSetStragety(IInformationSet s, int initSize) {
        HashMap<IAction, Double> sStrategy = getSetStragety(s);
        if (sStrategy == null) {
            sStrategy = new HashMap<>(initSize);
            strategy.put(s, sStrategy);
        }
        return sStrategy;
    }

    @Override
    public double getProbability(IInformationSet s, IAction a) {
        if (!s.isLegal(a)) return 0;
        HashMap<IAction, Double> sStrategy = getSetStragety(s);
        if (sStrategy == null) {
            return 1.0/(s.getLegalActions().size());
        } else {
            return sStrategy.getOrDefault(a, 0d);
        }
    }

    /**
     * Override action probabilities for given information set. Normalizes when neccessary.
     * @param s
     * @param probMap
     */
    public void setProbabilities(IInformationSet s, Function<IAction, Double> probMap) {
        List<IAction> legalActions = s.getLegalActions();
        if (legalActions.isEmpty()) return;

        HashMap<IAction, Double> sStrategy = getOrCreateSetStragety(s, legalActions.size());
        double probSum = 0;
        for(IAction a: legalActions) {
            double p = probMap.apply(a);
            probSum += p;
            sStrategy.put(a, p);
        }
        if (probSum != 1 && probSum != 0) {
            for(IAction a: legalActions) {
                sStrategy.put(a, sStrategy.getOrDefault(a, 0d)/probSum);
            }
        }
    }

    public void setProbability(IInformationSet s, IAction a, double p) {
        HashMap<IAction, Double> sStrategy = getOrCreateSetStragety(s, s.getLegalActions().size());
        sStrategy.put(a, p);
    }

    public void addProbabilities(IInformationSet s, Function<IAction, Double> probMap) {
        List<IAction> legalActions = s.getLegalActions();
        if (legalActions.isEmpty()) return;
        HashMap<IAction, Double> sStrategy = getOrCreateSetStragety(s, legalActions.size());
        for(IAction a: legalActions) {
            double p = probMap.apply(a);
            sStrategy.put(a, p + sStrategy.getOrDefault(a,  0d));
        }
    }

    public void normalize() {
        for(Map.Entry<IInformationSet, HashMap<IAction, Double>> entry: strategy.entrySet()) {
            IInformationSet s = entry.getKey();
            HashMap<IAction, Double> sStrategy = entry.getValue();
            List<IAction> legalActions = s.getLegalActions();
            if (legalActions.isEmpty()) continue;
            double probSum = 0;
            for(IAction a: legalActions) {
                probSum += sStrategy.getOrDefault(a, 0d);
            }
            if (probSum == 0) {
                entry.setValue(null); // uniform strategy
            } else if (probSum != 1) {
                for(IAction a: legalActions) {
                    sStrategy.put(a, sStrategy.getOrDefault(a, 0d)/probSum);
                }
            }
        }
    }

    @Override
    public Iterable<IInformationSet> getDefinedInformationSets() {
        return strategy.keySet();
    }

    public int countDefinedInformationSets() {
        return strategy.size();
    }

    public Strategy copy() {
        Strategy ret = new Strategy();
        for (IInformationSet is: strategy.keySet()) {
            ret.strategy.put(is, new HashMap<>(strategy.get(is)));
        }
        return ret;
    }

    public boolean hasInformationSet(IInformationSet is) {
        return strategy.containsKey(is);
    }

}
