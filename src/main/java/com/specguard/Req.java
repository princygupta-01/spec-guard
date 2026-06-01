package com.specguard;

public class Req implements Comparable<Req> {
    String nam;
    int sev;

    public Req(String nam, int sev) {
        this.nam = nam;
        this.sev = sev;
    }

    @Override
    public int compareTo(Req o) {
        return Integer.compare(o.sev, this.sev);
    }

    @Override
    public String toString() {
        return nam;
    }
}
