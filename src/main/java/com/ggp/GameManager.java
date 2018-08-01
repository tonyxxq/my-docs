package com.ggp;

import com.ggp.utils.RandomItemSelector;

import java.util.ArrayList;

public class GameManager {
    private IPlayer player1;
    private IPlayer player2;
    private ICompleteInformationState state;
    private RandomItemSelector<IAction> randomActionSelector = new RandomItemSelector<>();
    private ArrayList<IGameListener> gameListeners = new ArrayList<>();

    public GameManager(IPlayerFactory playerFactory1, IPlayerFactory playerFactory2, IGameDescription game) {
        this.player1 = playerFactory1.create(game, 1);
        this.player2 = playerFactory2.create(game, 2);
        this.state = game.getInitialState();
    }

    public void run() {
        gameListeners.forEach((listener) -> listener.gameStart());
        player1.init();
        player2.init();

        while(!playOneTurn()) {}
        gameListeners.forEach((listener) -> listener.gameEnd(getPayoff(1), getPayoff(2)));
    }

    private boolean playOneTurn() {
        if (player1 == null || player2 == null) return true;
        gameListeners.forEach((listener) -> listener.stateReached(state));
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
        gameListeners.forEach((listener) -> listener.actionSelected(state, a));
        Iterable<IPercept> percepts = state.getPercepts(a);
        state = state.next(a);
        for (IPercept p: percepts) {
            if (p.getTargetPlayer() == 1) {
                player1.receivePercepts(p);
            } else if (p.getTargetPlayer() == 2) {
                player2.receivePercepts(p);
            }
        }
        return false;
    }

    public int getPayoff(int role) {
        return (int) state.getPayoff(role);
    }

    public void registerGameListener(IGameListener listener) {
        if (listener != null) gameListeners.add(listener);
    }
}
