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
        // CIS from previous decision point are mapped to CIS in the current decision point
        // Each CIS is mapped through all paths compatible with possible opponent percept sequences
        // Each path is given by chance action probabilities along the way and top-level action at previous decision point,
        // which is converted to probability using strategy from previous decision point.
        for(HashMap.Entry<ICompleteInformationState, Double> origEntry: range.entrySet()) {
            IInformationSet myOrigIS = origEntry.getKey().getInfoSetForPlayer(myId);
            NextRangeTree nrt = nrtMap.get(myOrigIS);
            for (PerceptSequence ps: possibleOpponentPerceptSequences) {
                Map<ICompleteInformationState, ?extends Map<IAction, Double>> newState = nrt.getRange(ps);
                if (newState == null) continue; // this percept sequence may not be achievable from given input IS
                for (Map.Entry<ICompleteInformationState, ?extends  Map<IAction, Double>> newStateEntry: newState.entrySet()) {
                    ICompleteInformationState resultingState = newStateEntry.getKey();
                    Map<IAction, Double> reachProbMap = newStateEntry.getValue();
                    double reachProbForNewIS = 0;
                    for (Map.Entry<IAction, Double> reachProbTuple: reachProbMap.entrySet()) {
                        double prob = reachProbTuple.getValue();
                        if (reachProbTuple.getKey() != null) {
                            prob *= topLevelStrategy.getProbability(myOrigIS, reachProbTuple.getKey());
                        }
                        reachProbForNewIS += prob;
                    }
                    newRange.merge(resultingState, reachProbForNewIS, (oldV, newV) -> oldV + newV);
                    probSum += reachProbForNewIS;
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
