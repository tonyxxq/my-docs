package com.ggp.games.LeducPoker.percepts;

import com.ggp.IPercept;

import java.util.Objects;

public class PotUpdatePercept implements IPercept {
    private static final long serialVersionUID = 1L;
    private final int owner;
    private final int newPotSize;

    public PotUpdatePercept(int owner, int newPotSize) {
        this.owner = owner;
        this.newPotSize = newPotSize;
    }

    @Override
    public int getTargetPlayer() {
        return owner;
    }

    public int getNewPotSize() {
        return newPotSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotUpdatePercept that = (PotUpdatePercept) o;
        return owner == that.owner &&
                newPotSize == that.newPotSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), owner, newPotSize);
    }
}
