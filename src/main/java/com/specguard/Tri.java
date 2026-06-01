package com.specguard;

import java.util.*;

public class Tri {
    private static class Nod {
        Map<Character, Nod> ch = new HashMap<>();
        String tag = null;
    }

    private final Nod rt = new Nod();

    public void add(String wrd, String tag) {
        Nod cur = rt;
        for (char c : wrd.toCharArray()) {
            cur.ch.putIfAbsent(c, new Nod());
            cur = cur.ch.get(c);
        }
        cur.tag = tag;
    }

    public String get(String wrd) {
        Nod cur = rt;
        for (char c : wrd.toCharArray()) {
            if (!cur.ch.containsKey(c)) return null;
            cur = cur.ch.get(c);
        }
        return cur.tag;
    }

    public static Tri bld() {
        Tri t = new Tri();
        // AUTH domain
        t.add("auth",           "AUTH");
        t.add("authentication", "AUTH");
        t.add("authenticate",   "AUTH");
        t.add("login",          "LOGIN");
        t.add("signin",         "AUTH");
        t.add("jwt",            "AUTH");
        t.add("token",          "AUTH");
        t.add("oauth",          "AUTH");
        // PAYMENT domain
        t.add("payment",        "PAYMENT");
        t.add("pay",            "PAYMENT");
        t.add("billing",        "PAYMENT");
        t.add("transaction",    "PAYMENT");
        t.add("card",           "PAYMENT");
        t.add("stripe",         "PAYMENT");
        // ENCRYPT domain
        t.add("encrypt",        "ENCRYPT");
        t.add("encryption",     "ENCRYPT");
        t.add("tls",            "ENCRYPT");
        t.add("ssl",            "ENCRYPT");
        t.add("https",          "ENCRYPT");
        t.add("hash",           "ENCRYPT");
        // AUDIT domain
        t.add("audit",          "AUDIT");
        t.add("log",            "AUDIT");
        t.add("logging",        "AUDIT");
        t.add("trail",          "AUDIT");
        t.add("monitor",        "MONITOR");
        // API domain
        t.add("api",            "API");
        t.add("endpoint",       "API");
        t.add("rest",           "API");
        t.add("graphql",        "API");
        // DATA domain
        t.add("database",       "DATA");
        t.add("storage",        "DATA");
        t.add("persist",        "DATA");
        t.add("sql",            "DATA");
        return t;
    }
}
