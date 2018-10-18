package com.ggp.games.TicTacToe;

import com.ggp.IPercept;

import java.util.Objects;

public class ActionSuccessPercept implements IPercept {
    private static final long serialVersionUID = 1L;
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

    @Override
    public int getTargetPlayer() {
        return lastAction.getRole();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionSuccessPercept that = (ActionSuccessPercept) o;
        return successful == that.successful &&
                Objects.equals(lastAction, that.lastAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAction, successful);
    }
}
