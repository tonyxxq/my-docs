package com.ggp.utils;

import java.util.ArrayList;
import java.util.List;

public class TimedCounter {
    private List<Integer> incPointsMs;
    private int counter = 0;
    private StopWatch timer = new StopWatch();

    /**
     * Constructor
     * @param incPointsMs ASC ordered list of total time it should take for counter to reach respective index.
     */
    public TimedCounter(List<Integer> incPointsMs) {
        this.incPointsMs = new ArrayList<>(incPointsMs);
    }

    /**
     * Starts the timer.
     */
    public void start() {
        timer.start();
    }

    /**
     * Resets the timer and counter.
     */
    public void reset() {
        timer.stop();
        timer.reset();
        counter = 0;
    }

    /**
     * Tests whether counter can be increased and increases it to the maximal possible value.
     * @return New value of the counter.
     */
    public int tryIncrement() {
        while (counter < incPointsMs.size() && timer.getLiveDurationMs() > incPointsMs.get(counter)) {
            counter++;
        }
        return counter;
    }

    /**
     * Gets live duration in milliseconds.
     * @return
     */
    public long getLiveDurationMs() {
        return timer.getLiveDurationMs();
    }
}
