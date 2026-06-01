package com.specguard;

import java.util.*;

public class Bfs {
    public static Set<String> run(Set<String> dom, Grp grp) {
        Set<String> vis = new HashSet<>();
        Set<String> req = new LinkedHashSet<>();
        Queue<String> que = new LinkedList<>(dom);
        vis.addAll(dom);
        while (!que.isEmpty()) {
            String cur = que.poll();
            Set<String> nxt = grp.adj(cur);
            for (String n : nxt) {
                req.add(n);
                if (!vis.contains(n)) {
                    vis.add(n);
                    que.add(n);
                }
            }
        }
        return req;
    }
}
