package com.junit.demo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DemoUtilsTest.ReplaceCamelCase.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
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
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
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
//  @DisplayName("Addition test")
    void testEqualsAndNotEquals() {
        assertEquals(6, demoUtils.add(4, 2), "2+4 is 6");
        assertNotEquals(74, demoUtils.add(6, 1), "6+1 is not 74");
    }

    @Test
//  @DisplayName("Nullable return type test")
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
//  @EnabledIfSystemProperty(named = "SYS-PROP", matches = "CI_CD_DEPLOY")
    void testTrueAndFalse() {
        assertTrue(demoUtils.isGreater(1, -2), "Must be true");
        assertFalse(demoUtils.isGreater(5, 5), "Must be false");
    }

    @Test
    void testArrayEquals() {
        String[] testArray = new String[]{"A", "B", "C"};

        assertArrayEquals(testArray, demoUtils.getFirstThreeLettersOfAlphabet(), "Those are not first three letters of an alphabet");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @EnabledForJreRange(min = JRE.JAVA_10)
    void testIterableEquals() {
        List<String> testList = List.of("luv", "2", "code");

        assertIterableEquals(testList, demoUtils.getAcademyInList(), "Incorrect academy list provided");
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_17, max = JRE.JAVA_21)
    void testLinesEquals() {
        List<String> testList = List.of("luv", "2", "code");

        assertLinesMatch(testList, demoUtils.getAcademyInList(), "Incorrect academy list provided");
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testExceptionThrow() {

        assertThrows(Exception.class, () -> demoUtils.throwException(-1), "Must throw Exception.class");

        assertDoesNotThrow(() -> demoUtils.throwException(5), "Must not throw any exception");
    }

    @Test
    void testTimeout() {
        assertTimeout(Duration.ofSeconds(3), () -> demoUtils.checkTimeout(), "Should not last that long");
    }

    @Test
    @EnabledOnJre(JRE.JAVA_21)
    void testMultiplyEquals() {
        assertEquals(8, demoUtils.multiply(2, 4), "Multiplication must return 8");
    }
}
