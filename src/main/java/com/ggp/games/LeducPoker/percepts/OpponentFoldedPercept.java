package com.ggp.games.LeducPoker.percepts;

import com.ggp.IPercept;

import java.util.Objects;

public class OpponentFoldedPercept implements IPercept {
    private final int owner;

    public OpponentFoldedPercept(int owner) {
        this.owner = owner;
    }

    @Override
    public int getTargetPlayer() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpponentFoldedPercept that = (OpponentFoldedPercept) o;
        return owner == that.owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), owner);
    }
}
