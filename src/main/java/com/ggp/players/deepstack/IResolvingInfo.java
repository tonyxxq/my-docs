package com.ggp.players.deepstack;


import com.ggp.IInformationSet;
import com.ggp.players.deepstack.utils.Strategy;

/**
 * Tracks current progress of resolving. Returned objects must not be modified.
 */
public interface IResolvingInfo {
    Strategy getUnnormalizedCumulativeStrategy();
    IInformationSet getHiddenInfo();
}
