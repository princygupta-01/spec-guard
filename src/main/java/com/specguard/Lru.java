package com.specguard;

import java.util.*;

public class Lru {
    private static final int CAP = 10;

    private final Map<String, Res> cch = new LinkedHashMap<>(CAP, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Res> e) {
            return size() > CAP;
        }
    };

    public Res get(String k) {
        return cch.get(k);
    }

    public void put(String k, Res val) {
        cch.put(k, val);
    }

    public int siz() {
        return cch.size();
    }
}
