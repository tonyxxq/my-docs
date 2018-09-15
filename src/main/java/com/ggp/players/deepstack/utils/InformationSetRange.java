package com.ggp.players.deepstack.utils;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IStrategy;
import com.ggp.players.deepstack.utils.NextRangeTree;
import com.ggp.players.deepstack.utils.PerceptSequence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InformationSetRange {
    private HashMap<ICompleteInformationState, Double> range = new HashMap<>();
    private double norm = 1d;
    private int myId;

    public InformationSetRange(int myId) {
        this.myId = myId;
    }

    public void init(ICompleteInformationState initialState) {
        range.put(initialState, 1.0);
    }

    public double getProbability(ICompleteInformationState s) {
        return range.getOrDefault(s, 0d);
    }

    public Set<ICompleteInformationState> getPossibleStates() {
        return range.keySet();
    }

    public Set<?extends Map.Entry<ICompleteInformationState, Double>> getProbabilities() {
        return range.entrySet();
    }

    public void advance(Set<PerceptSequence> possibleOpponentPerceptSequences, Map<IInformationSet, NextRangeTree> nrtMap, IStrategy topLevelStrategy) {
        HashMap<ICompleteInformationState, Double> newRange = new HashMap<>();
        double probSum = 0;
        for(HashMap.Entry<ICompleteInformationState, Double> entry: range.entrySet()) {
            NextRangeTree nrt = nrtMap.get(entry.getKey().getInfoSetForPlayer(myId));
            for (PerceptSequence ps: possibleOpponentPerceptSequences) {
                Map<ICompleteInformationState, ?extends Map<IAction, Double>> newState = nrt.getRange(ps);
                if (newState == null) continue; // this percept sequence may not be achievable from given input IS
                for (Map.Entry<ICompleteInformationState, ?extends  Map<IAction, Double>> newStateEntry: newState.entrySet()) {
                    double isProb = 0;
                    for (Map.Entry<IAction, Double> probMap: newStateEntry.getValue().entrySet()) {
                        double prob = probMap.getValue();
                        if (probMap.getKey() != null) {
                            prob *= topLevelStrategy.getProbability(newStateEntry.getKey().getInfoSetForPlayer(myId), probMap.getKey());
                        }
                        isProb += prob;
                    }
                    newRange.merge(newStateEntry.getKey(), isProb, (oldV, newV) -> oldV + newV);
                    probSum += isProb;
                }
            }
        }
        if (probSum > 0) norm = probSum;
        else norm = 1;
        range = newRange;
    }

    public InformationSetRange copy() {
        InformationSetRange ret = new InformationSetRange(myId);
        ret.range = new HashMap<>(range);
        ret.norm = norm;
        return ret;
    }

    public double getNorm() {
        return norm;
    }
}
