package com.ggp.players.deepstack.utils;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;

import java.util.HashMap;
import java.util.Map;

public class NextRangeTree {
    private HashMap<PerceptSequence, HashMap<ICompleteInformationState, HashMap<IAction, Double>>> range = new HashMap<>();

    public void add(PerceptSequence opponentPerceptSequence, ICompleteInformationState cis, IAction myAction, double rndProb) {
        HashMap<ICompleteInformationState, HashMap<IAction, Double>> psSubtree = range.getOrDefault(opponentPerceptSequence, null);
        if (psSubtree == null) {
            psSubtree = new HashMap<>();
            range.put(opponentPerceptSequence, psSubtree);
        }
        HashMap<IAction, Double> probMap = psSubtree.getOrDefault(cis, null);
        if (probMap == null) {
            probMap = new HashMap<>();
            psSubtree.put(cis, probMap);
        }
        probMap.merge(myAction, rndProb, (oldV, newV) -> oldV + newV);
    }

    public NextRangeTree merge(NextRangeTree tree) {
        if (tree != null) {
            tree.range.forEach((ps, psSubtree)
                    -> psSubtree.forEach((cis, probMap)
                        -> probMap.forEach((myAction, rndProb)
                            -> add(ps, cis, myAction, rndProb))));
        }
        return this;
    }

    public Map<ICompleteInformationState, ?extends Map<IAction, Double>> getRange(PerceptSequence ps) {
        return range.getOrDefault(ps, null);
    }
}
