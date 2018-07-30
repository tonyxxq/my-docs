package com.ggp.players.deepstack;


/**
 * Tracks current progress of resolving. Returned objects must not be modified.
 */
public interface IResolvingInfo {
    Strategy getUnnormalizedCumulativeStrategy();
}
