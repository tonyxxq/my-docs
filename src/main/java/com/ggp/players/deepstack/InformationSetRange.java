package com.ggp.players.deepstack;

import com.ggp.IAction;
import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.IStrategy;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

public class InformationSetRange {
    private HashMap<IInformationSet, Double> range = new HashMap<>();

    public void init(IInformationSet initialSet) {
        range.put(initialSet, 1.0);
    }

    private void advance(Function<IInformationSet, IInformationSet> setMapper, Function<IInformationSet, Double> probMapper) {
        HashMap<IInformationSet, Double> newRange = new HashMap<>();
        double probSum = 0;
        for(HashMap.Entry<IInformationSet, Double> entry: range.entrySet()) {
            IInformationSet s = entry.getKey();
            double prob = entry.getValue();
            prob *= probMapper.apply(s);
            IInformationSet ns = setMapper.apply(s);
            if (prob == 0 || ns == null) continue;
            probSum += prob;
            double existingProb = newRange.getOrDefault(ns, 0d);
            newRange.put(ns, prob + existingProb);
        }
        if (probSum > 0) {
            for(HashMap.Entry<IInformationSet, Double> entry: newRange.entrySet()) {
                IInformationSet s = entry.getKey();
                double prob = entry.getValue();
                newRange.put(s, prob/probSum);
            }
        }
        range = newRange;
    }

    public void advance(IAction a, IStrategy strat) {
        advance((s) -> s.next(a), (s) -> strat.getProbability(s, a));
        /*HashMap<IInformationSet, Double> newRange = new HashMap<>();
        double probSum = 0;
        for(HashMap.Entry<IInformationSet, Double> entry: range.entrySet()) {
            IInformationSet s = entry.getKey();
            double prob = entry.getValue();
            if (!s.isLegal(a)) continue;
            prob *= strat.getProbability(s, a);
            IInformationSet ns = s.next(a);
            if (prob == 0 || ns == null) continue;
            probSum += prob;
            double existingProb = newRange.getOrDefault(ns, 0d);
            newRange.put(ns, prob + existingProb);
        }
        if (probSum > 0) {
            for(HashMap.Entry<IInformationSet, Double> entry: newRange.entrySet()) {
                IInformationSet s = entry.getKey();
                double prob = entry.getValue();
                newRange.put(s, prob/probSum);
            }
        }
        range = newRange;*/
    }

    public void advance(IPercept p) {
        advance((s) -> s.applyPercept(p), (s) -> 1d);
    }

    public double getProbability(IInformationSet s) {
        return range.getOrDefault(s, 0d);
    }

    public Set<IInformationSet> getInformationSets() {
        return range.keySet();
    }
}
