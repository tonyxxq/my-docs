package com.ggp.games.LeducPoker.percepts;

import com.ggp.IPercept;

import java.util.Objects;

public class BettingRoundEndedPercept implements IPercept {
    private static final long serialVersionUID = 1L;
    private final int owner;

    public BettingRoundEndedPercept(int owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BettingRoundEndedPercept that = (BettingRoundEndedPercept) o;
        return owner == that.owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), owner);
    }

    @Override
    public int getTargetPlayer() {
        return owner;
    }
}
