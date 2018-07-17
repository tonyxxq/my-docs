package com.ggp.players.deepstack;

import com.ggp.IPercept;

import java.util.ArrayList;
import java.util.Objects;

public class PerceptSequence {
    private ArrayList<IPercept> percepts;

    public static class Builder {
        private ArrayList<IPercept> percepts = new ArrayList<>();

        public void add(IPercept p) {
            percepts.add(p);
        }

        public PerceptSequence close() {
            PerceptSequence ret = new PerceptSequence(percepts);
            percepts = new ArrayList<>();
            return ret;
        }
    }

    private PerceptSequence(ArrayList<IPercept> percepts) {
        this.percepts = percepts;
    }

    public PerceptSequence() {
        this.percepts = new ArrayList<>();
    }

    public PerceptSequence(Iterable<IPercept> percepts, int forPlayer) {
        this.percepts = new ArrayList<>();
        addPercepts(percepts, forPlayer);
    }

    public PerceptSequence(PerceptSequence prefix, Iterable<IPercept> percepts, int forPlayer) {
        this.percepts = new ArrayList<>(prefix.percepts);
        addPercepts(percepts, forPlayer);
    }

    private void addPercepts(Iterable<IPercept> percepts, int forPlayer) {
        for (IPercept p: percepts) {
            if (p.getTargetPlayer() != forPlayer) continue;
            this.percepts.add(p);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerceptSequence that = (PerceptSequence) o;
        return Objects.equals(percepts, that.percepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(percepts);
    }
}
