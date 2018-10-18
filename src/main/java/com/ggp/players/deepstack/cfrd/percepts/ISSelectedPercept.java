package com.ggp.players.deepstack.cfrd.percepts;

import com.ggp.IInformationSet;
import com.ggp.IPercept;

import java.util.Objects;

public class ISSelectedPercept implements IPercept {
    private static final long serialVersionUID = 1L;
    private int target;
    private IInformationSet is;

    public ISSelectedPercept(int target, IInformationSet is) {
        this.target = target;
        this.is = is;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ISSelectedPercept that = (ISSelectedPercept) o;
        return target == that.target &&
                Objects.equals(is, that.is);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, is);
    }

    @Override
    public int getTargetPlayer() {
        return target;
    }

    public IInformationSet getInformationSet() {
        return is;
    }
}
