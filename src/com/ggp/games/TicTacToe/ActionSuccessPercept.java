package com.ggp.games.TicTacToe;

import com.ggp.IPercept;

public class ActionSuccessPercept implements IPercept {
    private MarkFieldAction lastAction;
    private boolean successful;

    public ActionSuccessPercept(MarkFieldAction lastAction, boolean successful) {
        this.lastAction = lastAction;
        this.successful = successful;
    }

    public MarkFieldAction getLastAction() {
        return lastAction;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
