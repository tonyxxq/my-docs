package com.ggp.players.deepstack.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PerceptSequenceMap {
    private HashMap<PerceptSequence, HashSet<PerceptSequence>> perceptSequenceMap = new HashMap<>();

    public PerceptSequenceMap() {
    }

    public PerceptSequenceMap(PerceptSequence from, PerceptSequence to) {
        HashSet<PerceptSequence> hashSet = new HashSet<>();
        hashSet.add(to);
        perceptSequenceMap.put(from, hashSet);
    }

    public PerceptSequenceMap merge(PerceptSequenceMap other) {
        if (other == null) return this;
        for (Map.Entry<PerceptSequence, HashSet<PerceptSequence>> item: other.perceptSequenceMap.entrySet()) {
            perceptSequenceMap.merge(item.getKey(), item.getValue(), (oldV, newV) -> {oldV.addAll(newV); return oldV;});
        }
        return this;
    }

    public void add(PerceptSequence from, PerceptSequence to) {
        HashSet<PerceptSequence> set = perceptSequenceMap.computeIfAbsent(from, k -> new HashSet<>());
        set.add(to);
    }

    public Set<PerceptSequence> getPossibleSequences(PerceptSequence ps) {
        return perceptSequenceMap.getOrDefault(ps, null);
    }
}
