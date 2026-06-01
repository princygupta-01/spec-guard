package com.specguard;

import java.util.*;

public class Cfl {
    private final Map<String, Set<String>> rls = new HashMap<>();

    public void add(String d1, String d2, String... ctl) {
        String k = d1.compareTo(d2) < 0 ? d1+"|"+d2 : d2+"|"+d1;
        rls.computeIfAbsent(k, x -> new LinkedHashSet<>());
        for (String c : ctl) rls.get(k).add(c);
    }

    public Set<String> chk(Set<String> dom) {
        Set<String> out = new HashSet<>();
        List<String> lst = new ArrayList<>(dom);
        for (int i = 0; i < lst.size(); i++) {
            for (int j = i+1; j < lst.size(); j++) {
                String a = lst.get(i), b = lst.get(j);
                String k = a.compareTo(b) < 0 ? a+"|"+b : b+"|"+a;
                Set<String> hit = rls.get(k);
                if (hit != null) out.addAll(hit);
            }
        }
        return out;
    }

    public static Cfl bld() {
        Cfl c = new Cfl();
        c.add("LOGIN",   "PAYMENT",  "SESSION_FIXATION", "CSRF");
        c.add("API",     "PAYMENT",  "IDEMPOTENCY", "WEBHOOK_AUTH");
        c.add("DATA",    "PAYMENT",  "PCI_SCOPE_ISOLATION");
        c.add("AUTH",    "API",      "TOKEN_ROTATION", "SCOPE_LIMIT");
        c.add("MONITOR", "PAYMENT",  "FRAUD_DETECTION");
        return c;
    }
}
