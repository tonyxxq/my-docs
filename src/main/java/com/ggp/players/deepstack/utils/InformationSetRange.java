package com.ggp.players.deepstack.utils;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;
import com.ggp.players.deepstack.utils.NextRangeTree;
import com.ggp.players.deepstack.utils.PerceptSequence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InformationSetRange {
    private HashMap<IInformationSet, Double> range = new HashMap<>();

    public void init(IInformationSet initialSet) {
        range.put(initialSet, 1.0);
    }

    public double getProbability(IInformationSet s) {
        return range.getOrDefault(s, 0d);
    }

    public Set<IInformationSet> getInformationSets() {
        return range.keySet();
    }

    public void advance(Set<PerceptSequence> possibleOpponentPerceptSequences, Map<IInformationSet, NextRangeTree> nrtMap, IStrategy topLevelStrategy) {
        HashMap<IInformationSet, Double> newRange = new HashMap<>();
        double probSum = 0;
        for(HashMap.Entry<IInformationSet, Double> entry: range.entrySet()) {
            NextRangeTree nrt = nrtMap.get(entry.getKey());
            for (PerceptSequence ps: possibleOpponentPerceptSequences) {
                Map<IInformationSet, ?extends Map<IAction, Double>> newIS = nrt.getRange(ps);
                if (newIS == null) continue; // this percept sequence may not be achievable from given input IS
                for (Map.Entry<IInformationSet, ?extends  Map<IAction, Double>> newIsEntry: newIS.entrySet()) {
                    double isProb = 0;
                    for (Map.Entry<IAction, Double> probMap: newIsEntry.getValue().entrySet()) {
                        double prob = probMap.getValue();
                        if (probMap.getKey() != null) {
                            prob *= topLevelStrategy.getProbability(newIsEntry.getKey(), probMap.getKey());
                        }
                        isProb += prob;
                    }
                    newRange.merge(newIsEntry.getKey(), isProb, (oldV, newV) -> oldV + newV);
                    probSum += isProb;
                }
            }
        }
        if (probSum > 0 && probSum != 1) {
            for(HashMap.Entry<IInformationSet, Double> entry: newRange.entrySet()) {
                IInformationSet s = entry.getKey();
                double prob = entry.getValue();
                newRange.put(s, prob/probSum);
            }
        }
        range = newRange;
    }
}
