package com.ggp;

public class GamePlayVisualizer implements IGameListener {
    private IStateVisualizer stateVisualizer;

    public GamePlayVisualizer(IStateVisualizer stateVisualizer) {
        this.stateVisualizer = stateVisualizer;
    }

    @Override
    public void gameStart() {

    }

    @Override
    public void gameEnd(int payoff1, int payoff2) {

    }

    @Override
    public void stateReached(ICompleteInformationState s) {
        if (stateVisualizer != null) {
            stateVisualizer.visualize(s);
        }
    }

    @Override
    public void actionSelected(ICompleteInformationState s, IAction a) {
        System.out.println("Action " + s.getActingPlayerId() + ": " + a.toString());
    }
}
