package com.specguard;

import java.util.*;

public class Fzy {
    private static final int THR = 2;

    public static int dst(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i-1) == b.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],
                                   Math.min(dp[i-1][j], dp[i][j-1]));
                }
            }
        }
        return dp[m][n];
    }

    public static String mtc(String tok, Set<String> voc) {
        if (tok.length() < 3) return null;
        String bst = null;
        int min = THR + 1;
        for (String v : voc) {
            int d = dst(tok, v);
            if (d < min) { min = d; bst = v; }
        }
        return min <= THR ? bst : null;
    }
}
