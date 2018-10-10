package com.ggp.players.deepstack.trackers;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;

public interface IGameTraversalTracker {
    IGameTraversalTracker next(IAction a);
    ICompleteInformationState getCurrentState();
    double getRndProb();
}
