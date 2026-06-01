package com.specguard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    @Test
    public void tst() {
        Eng eng = new Eng();
        Res res = eng.run("Build a payment API. Authenticate users with JWT. Accept card details.");
        assertNotNull(res);
        assertTrue(res.rsk > 0);
        assertTrue(res.cmp > 0);
    }
}
