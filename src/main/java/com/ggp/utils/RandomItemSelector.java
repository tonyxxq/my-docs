package com.ggp.utils;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class RandomItemSelector<T> {
    private Random rng = new Random();

    /**
     * Sample option with uniform probability
     * @param options
     * @return
     */
    public T select(List<T> options) {
        if (options == null || options.isEmpty()) return null;
        T a = options.get(rng.nextInt(options.size()));
        return a;
    }

    /**
     * Sample option with given probability map
     * @param options
     * @param probMap option -> probability (must sum to 1 over all options)
     * @return
     */
    public T select(Iterable<T> options, Function<T, Double> probMap) {
        if (options == null) return null;
        double sample = rng.nextDouble();
        T lastItem = null;
        for (T item: options) {
            double p = probMap.apply(item);
            if (sample < p) return item;
            sample -= p;
            lastItem = item;
        }
        return lastItem;
    }
}
