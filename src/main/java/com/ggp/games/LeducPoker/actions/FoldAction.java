package com.ggp.games.LeducPoker.actions;

import com.ggp.IAction;

public class FoldAction implements IAction {
    private static final long serialVersionUID = 1L;
    public static FoldAction instance = new FoldAction();
    private FoldAction() {}

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && this.getClass().equals(o.getClass());
    }

    @Override
    public String toString() {
        return "FoldAction{}";
    }
}
