package com.ggp.players.deepstack;

import com.ggp.ICompleteInformationState;

public interface IResolvingListener {
    void resolvingStart(IResolvingInfo resInfo);
    void resolvingEnd(IResolvingInfo resInfo);
    void stateVisited(ICompleteInformationState s, IResolvingInfo resInfo);
    void resolvingIterationEnd(IResolvingInfo resInfo);
}
