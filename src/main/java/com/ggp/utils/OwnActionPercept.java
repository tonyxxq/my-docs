package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.IPercept;

import java.util.Objects;

public class OwnActionPercept implements IPercept {
    private int owner;
    private IAction action;


    public OwnActionPercept(int owner, IAction action) {
        this.owner = owner;
        this.action = action;
    }

    @Override
    public int getTargetPlayer() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnActionPercept that = (OwnActionPercept) o;
        return owner == that.owner &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, action);
    }

    @Override
    public String toString() {
        return "OwnActionPercept{" +
                "@" + owner +
                ":" + action +
                '}';
    }
}
