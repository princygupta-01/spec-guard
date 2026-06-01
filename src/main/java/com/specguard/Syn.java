package com.specguard;

import java.util.*;

public class Syn {
    private final Map<String, String> map = new HashMap<>();

    public void add(String can, String... syn) {
        for (String s : syn) map.put(s, can);
    }

    public String res(String tok) {
        return map.getOrDefault(tok, tok);
    }

    public static Syn bld() {
        Syn s = new Syn();
        s.add("auth",
            "authenticate", "verify", "credential",
            "signin", "logon", "identity", "sso");
        s.add("encrypt",
            "secure", "cipher", "protected",
            "encoded", "hashed", "cryptography");
        s.add("audit",
            "track", "record", "history",
            "journal", "trace", "report");
        s.add("payment",
            "billing", "invoice", "checkout",
            "purchase", "transaction", "charge");
        s.add("login",
            "signin", "logon", "access",
            "entrypoint", "portal");
        s.add("monitor",
            "watch", "observe", "health",
            "uptime", "metrics", "telemetry");
        return s;
    }
}
