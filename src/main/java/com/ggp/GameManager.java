package com.ggp;

import com.ggp.utils.RandomItemSelector;

public class GameManager {
    private IPlayer player1;
    private IPlayer player2;
    private ICompleteInformationState state;
    private RandomItemSelector<IAction> randomActionSelector = new RandomItemSelector<>();
    private IStateVisualizer stateVisualizer = null;

    public GameManager(IPlayerFactory playerFactory1, IPlayerFactory playerFactory2, IGameDescription game) {
        this.player1 = playerFactory1.create(game, 1);
        this.player2 = playerFactory2.create(game, 2);
        this.state = game.getInitialState();
    }

    public void run() {
        player1.init();
        player2.init();
        if (stateVisualizer != null) stateVisualizer.visualize(state);
        while(!playOneTurn()) {}
    }

    private boolean playOneTurn() {
        if (player1 == null || player2 == null) return true;
        if (state.isTerminal()) return true;
        IAction a;
        int turn = state.getActingPlayerId();
        if (turn == 1) {
            a = player1.act();
        } else if (turn == 2) {
            a = player2.act();
        } else {
            // random player
            a = randomActionSelector.select(state.getLegalActions());
        }
        System.out.println("Action " + turn + ": " + a.toString());
        Iterable<IPercept> percepts = state.getPercepts(a);
        state = state.next(a);
        for (IPercept p: percepts) {
            if (p.getTargetPlayer() == 1) {
                player1.receivePercepts(p);
            } else if (p.getTargetPlayer() == 2) {
                player2.receivePercepts(p);
            }
        }
        if (stateVisualizer != null) {
            stateVisualizer.visualize(state);
        }
        return state.isTerminal();
    }

    public int getPayoff(int role) {
        return (int) state.getPayoff(role);
    }

    public void setStateVisualizer(IStateVisualizer visualizer) {
        this.stateVisualizer = visualizer;
    }
}
