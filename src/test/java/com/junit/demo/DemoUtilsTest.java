package com.junit.demo;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DemoUtilsTest {

    DemoUtils demoUtils;

    @BeforeEach
    void beforeEach() {
        demoUtils = new DemoUtils();
        System.out.println("@BeforeEach is invoked");
    }

    @AfterEach
    void afterEach() {
        System.out.println("@AfterEach is invoked\n");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("@BeforeAll being executed\n");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("@AfterAll being executed");
    }

    @Test
    void testEqualsAndNotEquals() {
        assertEquals(6, demoUtils.add(4, 2), "2+4 is 6");
        assertNotEquals(74, demoUtils.add(6, 1), "6+1 is not 74");
    }

    @Test
    void testNullAndNotNull() {
        assertNull(demoUtils.checkNull(null), "Return should be null");
        assertNotNull(demoUtils.checkNull("null"), "Return shouldn't be null");
    }
}
