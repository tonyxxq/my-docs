package com.ggp.games.TicTacToe;

import com.ggp.IPercept;

public class Percept implements IPercept {
    private Action lastAction;
    private boolean successful;

    public Percept(Action lastAction, boolean successful) {
        this.lastAction = lastAction;
        this.successful = successful;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
