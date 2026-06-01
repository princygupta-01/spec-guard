package com.specguard;

import java.util.*;
import java.util.stream.*;

public class Eng {
    private final Tri tri;
    private final Grp grp;
    private final Cfl cfl;
    private final Syn syn;
    private final Lru lru = new Lru();
    private final Set<String> voc;

    public Eng() {
        this.tri = Tri.bld();
        this.grp = Grp.bld();
        this.cfl = Cfl.bld();
        this.syn = Syn.bld();
        this.voc = new HashSet<>(Arrays.asList(
            "auth","authentication","authenticate","login","signin",
            "jwt","token","oauth","payment","pay","billing",
            "transaction","card","stripe","encrypt","encryption",
            "tls","ssl","https","hash","audit","log","logging",
            "trail","monitor","api","endpoint","rest","graphql",
            "database","storage","persist","sql"
        ));
    }

    public Res run(String inp) {
        // Cache check — O(1)
        String k = inp.trim().toLowerCase();
        Res hit = lru.get(k);
        if (hit != null) return hit;

        // Step 1: Tokenize
        List<String> tks = Tok.run(inp);

        // Step 2: Trie match with synonym + fuzzy fallback
        Set<String> dom = new HashSet<>();
        Set<String> fnd = new HashSet<>();
        Idx idx = new Idx();
        for (int i = 0; i < tks.size(); i++) {
            String tk = tks.get(i);
            String res = syn.res(tk);
            String tag = tri.get(res);
            if (tag == null && tk.length() >= 4) {
                String fuz = Fzy.mtc(res, voc);
                if (fuz != null) tag = tri.get(fuz);
            }
            if (tag != null) {
                idx.add(tag, i);
                if (ctl(tag)) fnd.add(tag);
                else dom.add(tag);
            }
        }

        // Controls that are also expandable domains
        Set<String> exp = Set.of("AUTH","DATA","MONITOR");
        for (String f : fnd) {
            if (exp.contains(f)) dom.add(f);
        }

        // Step 3: BFS expand requirements from detected domains
        Set<String> req = Bfs.run(dom, grp);

        // Cross-domain conflict expansion
        Set<String> xtr = cfl.chk(dom);
        req.addAll(xtr);

        // Step 4: Coverage analysis via HashSet subtraction
        Cov cov = new Cov(fnd, req);

        // Step 5: Rank missing by severity via PriorityQueue
        List<String> mis = Rnk.rnk(cov.mis, grp);
        int rsk = Rnk.rsk(cov.mis, grp);
        int cmp = cov.cmp();

        // Confidence = ratio of matched tokens to total tokens
        int ttl = Math.max(tks.size(), 1);
        int mat = fnd.size() + dom.size();
        int cnf = (int) Math.min(100, Math.round((mat * 100.0) / ttl));

        // Detect ambiguous/vague terms
        Set<String> vag = Set.of("system","feature","handle","process","manage","support","ensure");
        List<String> amb = tks.stream().filter(vag::contains).distinct().collect(Collectors.toList());

        // Quality gate checks
        String wrn = null;
        if (tks.size() < 15) {
            wrn = "Spec too short for reliable analysis";
        } else if (dom.isEmpty()) {
            wrn = "No recognizable domain detected in spec";
        } else if (!amb.isEmpty() && amb.size() > tks.size() / 2) {
            wrn = "Spec too vague — replace general terms with specifics";
        }

        // Compliance framework violations
        List<String> vfw = grp.vfw(cov.mis);

        // Tier grouping using severity scores
        Map<String, List<String>> trs = new LinkedHashMap<>();
        trs.put("CRITICAL", new ArrayList<>());
        trs.put("HIGH",     new ArrayList<>());
        trs.put("LOW",      new ArrayList<>());
        for (String m : mis) {
            int s = grp.sev(m);
            if      (s >= 8) trs.get("CRITICAL").add(m);
            else if (s >= 5) trs.get("HIGH").add(m);
            else             trs.get("LOW").add(m);
        }

        // Build response
        Res r = new Res(rsk, cmp, mis, new ArrayList<>(fnd), vfw, cnf);
        r.amb = amb;
        r.trs = trs;
        r.den = idx.dns();
        r.wrn = wrn;

        // Cache store
        lru.put(k, r);
        return r;
    }

    private static final Set<String> CTL = Set.of(
        "AUTH","ENCRYPT","AUDIT","MFA","SESSION",
        "LOCKOUT","RATE_LIMIT","BACKUP","VALIDATION"
    );

    private boolean ctl(String tag) {
        return CTL.contains(tag);
    }
}
