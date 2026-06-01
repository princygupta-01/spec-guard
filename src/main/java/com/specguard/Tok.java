package com.specguard;

import java.util.*;

public class Tok {
    public static List<String> run(String inp) {
        if (inp == null || inp.isBlank()) return Collections.emptyList();
        LinkedList<String> out = new LinkedList<>();
        String[] raw = inp.toLowerCase()
                          .replaceAll("[^a-z0-9\\s]", " ")
                          .split("\\s+");
        for (String w : raw) {
            if (w.length() >= 2) out.add(w);
        }
        return out;
    }
}
