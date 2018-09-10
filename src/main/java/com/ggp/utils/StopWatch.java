package com.ggp.utils;

public class StopWatch {
    private long durationNano = 0;
    private long startNano;

    private static final long msToNano = 1000000L;

    public void start() {
        startNano = System.nanoTime();
    }

    public void stop() {
        long end = System.nanoTime();
        durationNano += end - startNano;
    }

    public void reset() {
        durationNano = 0;
        startNano = 0;
    }

    public long getDurationMs() {
        return durationNano/msToNano;
    }

    public long getLiveDurationMs() {
        return (durationNano + System.nanoTime() - startNano)/msToNano;
    }
}
