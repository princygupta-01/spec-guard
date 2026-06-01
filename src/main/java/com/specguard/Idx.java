package com.specguard;

import java.util.*;

public class Idx {
    private final Map<String, List<Integer>> idx = new HashMap<>();

    public void add(String tok, int pos) {
        idx.computeIfAbsent(tok, k -> new ArrayList<>()).add(pos);
    }

    public List<Integer> get(String tok) {
        return idx.getOrDefault(tok, Collections.emptyList());
    }

    public int frq(String tok) {
        return idx.getOrDefault(tok, Collections.emptyList()).size();
    }

    public Map<String, List<Integer>> all() {
        return Collections.unmodifiableMap(idx);
    }

    public Map<String, Integer> dns() {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> e : idx.entrySet()) {
            out.put(e.getKey(), e.getValue().size());
        }
        return out;
    }
}
