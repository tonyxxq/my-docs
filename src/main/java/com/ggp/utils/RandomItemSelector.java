package com.ggp.utils;

import java.util.List;
import java.util.Random;

public class RandomItemSelector<T> {
    private Random rng = new Random();

    public T select(List<T> options) {
        if (options == null || options.isEmpty()) return null;
        T a = options.get(rng.nextInt(options.size()));
        return a;
    }
}
