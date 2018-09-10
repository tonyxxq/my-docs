package com.ggp.players.deepstack.utils;

public class IterationTimer {
    private long startNano;
    private long iterStartNano;
    private long timeoutNano;
    private long maxIterTimeNano = 0;
    private long iters = 0;
    private long totalIterTime = 0;

    private static final long msToNano = 1000000L;

    public IterationTimer(long timeoutMillis) {
        this.timeoutNano = timeoutMillis * msToNano;
    }

    public void start() {
        startNano = System.nanoTime();
    }

    public void startIteration() {
        iterStartNano = System.nanoTime();
    }

    public void endIteration() {
        long iterTime = System.nanoTime() - iterStartNano;
        if (iterTime >  maxIterTimeNano) maxIterTimeNano = iterTime;
        totalIterTime += iterTime;
        iters++;
        iterStartNano = System.nanoTime();
    }

    private long getEstimatedIterationLengthNano() {
        if (iters == 0) return 0;
        return totalIterTime/iters;
    }

    public boolean canDoAnotherIteration() {
        long remainingNano = timeoutNano - (System.nanoTime() - startNano);
        return (remainingNano > getEstimatedIterationLengthNano());
    }

    public long getTotalMs() {
        return (System.nanoTime() - startNano)/msToNano;
    }
}
