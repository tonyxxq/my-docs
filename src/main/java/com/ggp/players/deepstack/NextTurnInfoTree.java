package com.ggp.players.deepstack;

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
        NextTurnInfoTree current = this;
        for (IPercept p: perceptPath) {
            if (p.getTargetPlayer() != perceptsForPlayer) continue;
            NextTurnInfoTree next = current.children.getOrDefault(p, null);
            if (next == null) {
                next = new NextTurnInfoTree();
                current.children.put(p, next);
            }
            current = next;
        }
        current.merge(subtree);

    }

    public NextTurnInfoTree merge(NextTurnInfoTree tree) {
        if (tree != null) {
            tree.opponentValues.forEach((k, v) -> opponentValues.merge(k, v, (a, b) -> a.add(b)));
            tree.children.forEach((k, v) -> children.merge(k, v, (a, b) -> a.merge(b)));
        }
        return this;
    }

    public void addLeaf(IInformationSet is, double opponentCFV) {
        opponentValues.merge(is, new AveragedDouble(opponentCFV), (a, b) -> a.add(b));
    }

    public Map<IInformationSet, AveragedDouble> getOpponentValues() {
        return opponentValues;
    }

    public NextTurnInfoTree getNext(IPercept p) {
        return children.getOrDefault(p, null);
    }
}
