package com.starling.starlingroundup.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathTest {

    @Test
    public void testRoundLongToNearest100() {
        assertEquals(200, Math.roundLongToNext100(100));
        assertEquals(200, Math.roundLongToNext100(101));
        assertEquals(200, Math.roundLongToNext100(180));
        assertEquals(1300, Math.roundLongToNext100(1298));
        assertEquals(100, Math.roundLongToNext100(0));
    }
}
