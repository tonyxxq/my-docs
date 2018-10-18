package com.ggp.players.deepstack.cfrd.actions;

import com.ggp.IAction;

public class TerminateAction implements IAction {
    private static final long serialVersionUID = 1L;
    public static TerminateAction instance = new TerminateAction();
    private TerminateAction() {}

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
        return "TerminateAction{}";
    }
}
