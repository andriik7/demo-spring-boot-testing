package com.junit.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DemoUtilsTest.ReplaceCamelCase.class)
class DemoUtilsTest {

    static class ReplaceCamelCase extends DisplayNameGenerator.Standard {
        public ReplaceCamelCase() {
        }

        public String generateDisplayNameForClass(Class<?> testClass) {
            return this.replaceCapitals(super.generateDisplayNameForClass(testClass));
        }

        public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
            return this.replaceCapitals(super.generateDisplayNameForNestedClass(nestedClass));
        }

        public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
            return this.replaceCapitals(testMethod.getName());
        }

        private String replaceCapitals(String name) {
            name = name.replaceAll("([A-Z])", " $1");
            name = name.replaceAll("([0-9]+)", " $1");
            return name;
        }
    }

    DemoUtils demoUtils;

    @BeforeEach
    void beforeEach() {
        demoUtils = new DemoUtils();
        //System.out.println("@BeforeEach is invoked");
    }

//    @AfterEach
//    void afterEach() {
//        System.out.println("@AfterEach is invoked\n");
//    }
//
//    @BeforeAll
//    static void beforeAll() {
//        System.out.println("@BeforeAll being executed\n");
//    }
//
//    @AfterAll
//    static void afterAll() {
//        System.out.println("@AfterAll being executed");
//    }

    @Test
//    @DisplayName("Addition test")
    void testEqualsAndNotEquals() {
        assertEquals(6, demoUtils.add(4, 2), "2+4 is 6");
        assertNotEquals(74, demoUtils.add(6, 1), "6+1 is not 74");
    }

    @Test
//    @DisplayName("Nullable return type test")
    void testNullAndNotNull() {
        assertNull(demoUtils.checkNull(null), "Return should be null");
        assertNotNull(demoUtils.checkNull("null"), "Return shouldn't be null");
    }

    @Test
    void testSameAndNotSame() {
        assertSame(demoUtils.getAcademy(), demoUtils.getAcademyDuplicate(), "References must be the same");
        assertNotSame(demoUtils.getAcademy(), "demoUtils.getAcademyDuplicate()", "References must not be the same");
    }

    @Test
    void testTrueAndFalse() {
        assertTrue(demoUtils.isGreater(1, -2), "Must be true");
        assertFalse(demoUtils.isGreater(5, 5), "Must be false");
    }
}
