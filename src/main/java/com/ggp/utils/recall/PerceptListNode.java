package com.ggp.utils.recall;

import com.ggp.IPercept;

import java.util.Objects;

class PerceptListNode {
    public final PerceptListNode previous;
    public final IPercept percept;

    public PerceptListNode(PerceptListNode previous, IPercept percept) {
        this.previous = previous;
        this.percept = percept;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerceptListNode that = (PerceptListNode) o;
        return Objects.equals(percept, that.percept) &&Objects.equals(previous, that.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, percept);
    }
}
