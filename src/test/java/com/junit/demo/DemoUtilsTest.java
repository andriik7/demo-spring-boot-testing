package com.junit.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DemoUtilsTest {

    @Test
    void testEqualsAndNotEquals() {
        DemoUtils demoUtils = new DemoUtils();

        assertEquals(6, demoUtils.add(4, 2), "2+4 is 6");
        assertNotEquals(74, demoUtils.add(6, 1), "6+1 is not 74");
    }

    @Test
    void testNullAndNotNull() {
        DemoUtils demoUtils = new DemoUtils();

        assertNull(demoUtils.checkNull(null), "Return should be null");
        assertNotNull(demoUtils.checkNull("null"), "Return shouldn't be null");
    }
}
