package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;

import java.util.HashMap;

/**
 * Normalizing wrapper for unnormalized strategy.
 *
 * This class assumes the underlying strategy doesn't change while the wrapper is in use.
 * If the underlying strategy changes new wrapper must be created to reflect those changes.
 */
public class NormalizingStrategyWrapper implements IStrategy {
    private static final long serialVersionUID = 1L;
    private IStrategy unnormalizedStrategy;
    private HashMap<IInformationSet, Double> norms = new HashMap<>();

    /**
     * Constructor
     * @param unnormalizedStrategy
     */
    public NormalizingStrategyWrapper(IStrategy unnormalizedStrategy) {
        this.unnormalizedStrategy = unnormalizedStrategy;
    }

    private double getNorm(IInformationSet is) {
        return norms.computeIfAbsent(is, k -> {
            double norm = 0;
            for (IAction a: k.getLegalActions()) {
                norm += unnormalizedStrategy.getProbability(k, a);
            }
            if (norm == 0) norm = 1;
            return norm;
        });
    }

    @Override
    public double getProbability(IInformationSet s, IAction a) {
        return unnormalizedStrategy.getProbability(s, a) / getNorm(s);
    }

    @Override
    public Iterable<IInformationSet> getDefinedInformationSets() {
        return unnormalizedStrategy.getDefinedInformationSets();
    }
}
