package com.specguard;

import java.util.*;

public class Grp {
    private final Map<String, Set<String>> adj = new HashMap<>();
    private final Map<String, Integer> sev = new HashMap<>();
    private final Map<String, Set<String>> fwk = new HashMap<>();

    public void add(String src, String... dst) {
        adj.computeIfAbsent(src, k -> new LinkedHashSet<>());
        for (String d : dst) {
            adj.get(src).add(d);
        }
    }

    public void sev(String nod, int val) {
        sev.put(nod, val);
    }

    public Set<String> adj(String nod) {
        return adj.getOrDefault(nod, Collections.emptySet());
    }

    public int sev(String nod) {
        return sev.getOrDefault(nod, 1);
    }

    public void fwk(String nam, String... ctl) {
        fwk.computeIfAbsent(nam, k -> new LinkedHashSet<>());
        for (String c : ctl) fwk.get(nam).add(c);
    }

    public List<String> vfw(Set<String> mis) {
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Set<String>> e : fwk.entrySet()) {
            Set<String> req = e.getValue();
            long hit = mis.stream().filter(req::contains).count();
            if (hit >= 2) out.add(e.getKey());
        }
        return out;
    }

    public static Grp bld() {
        Grp g = new Grp();
        // Domain → required controls
        g.add("PAYMENT",  "AUTH", "ENCRYPT", "AUDIT", "REFUND", "IDEMPOTENCY");
        g.add("LOGIN",    "AUTH", "RATE_LIMIT", "CAPTCHA", "SESSION");
        g.add("AUTH",     "MFA", "SESSION", "LOCKOUT", "TOKEN_EXPIRY");
        g.add("API",      "AUTH", "RATE_LIMIT", "VERSIONING", "DOCS");
        g.add("DATA",     "BACKUP", "ENCRYPT", "AUDIT", "VALIDATION");
        g.add("MONITOR",  "ALERT", "DASHBOARD", "LOG_RETENTION");
        // Severity scores
        g.sev("AUTH",        10);
        g.sev("ENCRYPT",      9);
        g.sev("AUDIT",        8);
        g.sev("MFA",          8);
        g.sev("RATE_LIMIT",   7);
        g.sev("SESSION",      7);
        g.sev("LOCKOUT",      6);
        g.sev("BACKUP",       6);
        g.sev("VALIDATION",   5);
        g.sev("VERSIONING",   4);
        g.sev("DOCS",         3);
        g.sev("REFUND",       5);
        g.sev("IDEMPOTENCY",  6);
        g.sev("CAPTCHA",      5);
        g.sev("TOKEN_EXPIRY", 7);
        g.sev("ALERT",        6);
        g.sev("DASHBOARD",    3);
        g.sev("LOG_RETENTION",5);
        // Compliance framework definitions
        g.fwk("PCI_DSS",    "ENCRYPT", "AUDIT", "RATE_LIMIT", "MFA", "LOCKOUT");
        g.fwk("OWASP_TOP10","AUTH",    "LOCKOUT", "SESSION", "CAPTCHA", "VALIDATION");
        g.fwk("GDPR",       "AUDIT",   "BACKUP", "VALIDATION", "ENCRYPT");
        g.fwk("ISO_27001",  "AUDIT",   "MFA", "BACKUP", "RATE_LIMIT", "ALERT");
        g.fwk("HIPAA",      "ENCRYPT", "AUDIT", "BACKUP", "SESSION", "MFA");
        return g;
    }
}
