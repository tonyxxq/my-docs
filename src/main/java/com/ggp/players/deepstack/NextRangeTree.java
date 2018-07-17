package com.ggp.players.deepstack;

import com.ggp.IAction;
import com.ggp.IInformationSet;

import java.util.HashMap;
import java.util.Map;

public class NextRangeTree {
    private HashMap<PerceptSequence, HashMap<IInformationSet, HashMap<IAction, Double>>> range = new HashMap<>();

    public void add(PerceptSequence opponentPerceptSequence, IInformationSet myIs, IAction myAction, double rndProb) {
        HashMap<IInformationSet, HashMap<IAction, Double>> psSubtree = range.getOrDefault(opponentPerceptSequence, null);
        if (psSubtree == null) {
            psSubtree = new HashMap<>();
            range.put(opponentPerceptSequence, psSubtree);
        }
        HashMap<IAction, Double> probMap = psSubtree.getOrDefault(myIs, null);
        if (probMap == null) {
            probMap = new HashMap<>();
            psSubtree.put(myIs, probMap);
        }
        probMap.merge(myAction, rndProb, (oldV, newV) -> oldV + newV);
    }

    public NextRangeTree merge(NextRangeTree tree) {
        if (tree != null) {
            tree.range.forEach((ps, psSubtree)
                    -> psSubtree.forEach((myIs, probMap)
                        -> probMap.forEach((myAction, rndProb)
                            -> add(ps, myIs, myAction, rndProb))));
        }
        return this;
    }

    Map<IInformationSet, ?extends Map<IAction, Double>> getRange(PerceptSequence ps) {
        return range.getOrDefault(ps, null);
    }
}
