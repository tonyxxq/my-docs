package com.ggp.games.RockPaperScissors;

import com.ggp.IAction;

import java.util.Objects;

public class ChooseAction implements IAction {
    private int chosen;

    public ChooseAction(int chosen) {
        this.chosen = chosen;
    }

    public int getChosen() {
        return chosen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChooseAction that = (ChooseAction) o;
        return chosen == that.chosen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chosen);
    }

    @Override
    public String toString() {
        return "ChooseAction{" + chosen + '}';
    }
}
