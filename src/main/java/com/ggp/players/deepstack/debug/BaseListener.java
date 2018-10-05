package com.ggp.players.deepstack.debug;

import com.ggp.ICompleteInformationState;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;

public class BaseListener implements IResolvingListener {
    private boolean initEnded = false;

    protected boolean hasInitEnded() {
        return initEnded;
    }

    @Override
    public void initEnd(IResolvingInfo resInfo) {
        initEnded = true;
    }

    @Override
    public void resolvingStart(IResolvingInfo resInfo) {
    }

    @Override
    public void resolvingEnd(IResolvingInfo resInfo) {
    }

    @Override
    public void stateVisited(ICompleteInformationState s, IResolvingInfo resInfo) {
    }

    @Override
    public void resolvingIterationEnd(IResolvingInfo resInfo) {
    }

    public void reinit() {
        initEnded = false;
    }
}
