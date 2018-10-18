package com.ggp.games.LeducPoker.percepts;

import com.ggp.IPercept;

import java.util.Objects;

public class ReturnedMoneyPercept implements IPercept {
    private static final long serialVersionUID = 1L;
    private final int owner;
    private final int amount;

    public ReturnedMoneyPercept(int owner, int amount) {
        this.owner = owner;
        this.amount = amount;
    }

    @Override
    public int getTargetPlayer() {
        return owner;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnedMoneyPercept that = (ReturnedMoneyPercept) o;
        return owner == that.owner &&
                amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), owner, amount);
    }
}
