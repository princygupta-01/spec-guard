package com.specguard;

import java.util.*;

public class Rnk {
    public static List<String> rnk(Set<String> mis, Grp grp) {
        PriorityQueue<Req> pq = new PriorityQueue<>();
        for (String m : mis) {
            pq.add(new Req(m, grp.sev(m)));
        }
        List<String> out = new ArrayList<>();
        while (!pq.isEmpty()) {
            out.add(pq.poll().nam);
        }
        return out;
    }

    public static int rsk(Set<String> mis, Grp grp) {
        if (mis.isEmpty()) return 0;
        int tot = 0;
        int max = 0;
        for (String m : mis) {
            int s = grp.sev(m);
            tot += s;
            max += 10;
        }
        return (int) Math.min(100, Math.round((tot * 100.0) / Math.max(max, 1)));
    }
}
