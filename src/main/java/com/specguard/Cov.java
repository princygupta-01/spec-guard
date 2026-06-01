package com.specguard;

import java.util.*;

public class Cov {
    Set<String> fnd;
    Set<String> req;
    Set<String> mis;

    public Cov(Set<String> fnd, Set<String> req) {
        this.fnd = fnd;
        this.req = req;
        this.mis = new HashSet<>(req);
        this.mis.removeAll(fnd);
    }

    public int cmp() {
        if (req.isEmpty()) return 100;
        int met = req.size() - mis.size();
        return (int) Math.round((met * 100.0) / req.size());
    }
}
