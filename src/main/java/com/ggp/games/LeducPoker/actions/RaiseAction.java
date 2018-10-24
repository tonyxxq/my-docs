package com.ggp.games.LeducPoker.actions;

import com.ggp.IAction;

public class RaiseAction implements IAction {
    public static RaiseAction instance = new RaiseAction();
    private RaiseAction() {}

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
        return "RaiseAction{}";
    }
}
