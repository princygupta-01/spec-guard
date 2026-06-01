package com.specguard;

import java.util.List;
import java.util.Map;

public class Res {
    public List<String> amb;
    public int rsk;
    public int cmp;
    public List<String> mis;
    public List<String> fnd;
    public String lbl;
    public List<String> vfw;
    public int cnf;
    public Map<String, List<String>> trs;
    public Map<String, Integer> den;
    public String wrn;

    public Res(int rsk, int cmp, List<String> mis, List<String> fnd, List<String> vfw, int cnf) {
        this.rsk = rsk;
        this.cmp = cmp;
        this.mis = mis;
        this.fnd = fnd;
        this.vfw = vfw;
        this.cnf = cnf;
        this.lbl = rsk > 70 ? "CRITICAL" : rsk > 40 ? "MODERATE" : "LOW";
        this.amb = List.of();
    }
}
