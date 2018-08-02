package com.ggp.utils;

public class StopWatch {
    private long duration = 0;
    private long start;

    public void start() {
        start = System.nanoTime();
    }

    public void stop() {
        long end = System.nanoTime();
        duration += end - start;
    }

    public void reset() {
        duration = 0;
        start = 0;
    }

    public long getDurationMs() {
        return duration/1000000L;
    }
}
