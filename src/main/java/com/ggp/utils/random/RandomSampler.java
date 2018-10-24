package com.ggp.utils.random;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class RandomSampler {

    public static class SampleResult<T> {
        private T result;
        private double sampleProb;

        public SampleResult(T result, double sampleProb) {
            this.result = result;
            this.sampleProb = sampleProb;
        }

        public T getResult() {
            return result;
        }

        public double getSampleProb() {
            return sampleProb;
        }
    }

    private Random rng = new Random();

    /**
     * Sample option with uniform probability
     * @param options
     * @return
     */
    public <T> T select(List<T> options) {
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
    public <T> SampleResult<T> select(Iterable<T> options, Function<T, Double> probMap) {
        if (options == null) return null;
        double sample = rng.nextDouble();
        T item = null;
        double p = 0d;
        for (T it: options) {
            item = it;
            p = probMap.apply(item);
            if (sample < p) break;
            sample -= p;
        }
        return new SampleResult<>(item, p);
    }
}
