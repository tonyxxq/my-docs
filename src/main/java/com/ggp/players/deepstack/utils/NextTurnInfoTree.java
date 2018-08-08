package com.ggp.players.deepstack.utils;

import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.utils.AveragedDouble;

import java.util.HashMap;
import java.util.Map;

public class NextTurnInfoTree {
    private HashMap<IInformationSet, AveragedDouble> opponentValues = new HashMap<>();
    private HashMap<IPercept, NextTurnInfoTree> children = new HashMap<>();

    public NextTurnInfoTree() {}

    public NextTurnInfoTree(IInformationSet is, double opponentCFV) {
        opponentValues.put(is, new AveragedDouble(opponentCFV));
    }

    public void add(Iterable<IPercept> perceptPath, int perceptsForPlayer, NextTurnInfoTree subtree) {
        NextTurnInfoTree current = getOrCreatePath(perceptPath, perceptsForPlayer);
        current.sumMerge(subtree);

    }

    public NextTurnInfoTree avgMerge(NextTurnInfoTree tree) {
        if (tree != null) {
            tree.opponentValues.forEach((k, v) -> opponentValues.merge(k, v, (a, b) -> a.add(b)));
            tree.children.forEach((k, v) -> children.merge(k, v, (a, b) -> a.avgMerge(b)));
        }
        return this;
    }

    public NextTurnInfoTree sumMerge(NextTurnInfoTree tree) {
        if (tree != null) {
            tree.opponentValues.forEach((k, v) -> opponentValues.merge(k, v, (a, b) -> a.sum(b)));
            tree.children.forEach((k, v) -> children.merge(k, v, (a, b) -> a.sumMerge(b)));
        }
        return this;
    }

    public void addLeaf(IInformationSet is, double opponentCFV) {
        opponentValues.merge(is, new AveragedDouble(opponentCFV), (a, b) -> a.sum(b));
    }

    public Map<IInformationSet, AveragedDouble> getOpponentValues() {
        return opponentValues;
    }

    public NextTurnInfoTree getNext(IPercept p) {
        return children.getOrDefault(p, null);
    }

    public NextTurnInfoTree getOrCreatePath(Iterable<IPercept> percepts, int forPlayer) {
        NextTurnInfoTree current = this;
        for (IPercept p: percepts) {
            if (p.getTargetPlayer() != forPlayer) continue;
            current = current.children.computeIfAbsent(p, k -> new NextTurnInfoTree());
        }
        return current;
    }
}
