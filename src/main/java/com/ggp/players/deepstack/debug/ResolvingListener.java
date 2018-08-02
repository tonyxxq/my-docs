package com.ggp.players.deepstack.debug;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IGameListener;
import com.ggp.players.deepstack.IResolvingInfo;
import com.ggp.players.deepstack.IResolvingListener;
import com.ggp.utils.StopWatch;

public class ResolvingListener implements IResolvingListener, IGameListener {
    private int totalStateCounter = 0;
    private int actionStateCounter = 0;
    private StopWatch totalReasoningTime = new StopWatch();
    private StopWatch actionReasoningTime = new StopWatch();

    @Override
    public void initEnd(IResolvingInfo resInfo) {
    }

    @Override
    public void resolvingStart(IResolvingInfo resInfo) {
        actionStateCounter = 0;
        actionReasoningTime.reset();
        totalReasoningTime.start();
        actionReasoningTime.start();
    }

    @Override
    public void resolvingEnd(IResolvingInfo resInfo) {
        totalReasoningTime.stop();
        actionReasoningTime.stop();
        System.out.println(String.format("Reasoning time: %d ms, states visited: %d",
                actionReasoningTime.getDurationMs(), actionStateCounter));
    }

    @Override
    public void stateVisited(ICompleteInformationState s, IResolvingInfo resInfo) {
        totalStateCounter++;
        actionStateCounter++;
    }

    @Override
    public void resolvingIterationEnd(IResolvingInfo resInfo) {

    }

    @Override
    public void gameStart() {
    }

    @Override
    public void gameEnd(int payoff1, int payoff2) {
        System.out.println(String.format("TOTAL: Reasoning time: %d ms, states visited: %d",
                totalReasoningTime.getDurationMs(), totalStateCounter));
    }

    @Override
    public void stateReached(ICompleteInformationState s) {
    }

    @Override
    public void actionSelected(ICompleteInformationState s, IAction a) {
    }
}
