package com.junit.tdd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(FizzBuzzTest.ReplaceCamelCase.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class FizzBuzzTest {

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

    @Test
    void testForDivisibleByThree() {

        String expected = "Fizz";

        assertEquals(expected, FizzBuzz.compute(3), "Should return 'Fizz'");
    }

    @Test
    void testForDivisibleByFive() {

        String expected = "Buzz";

        assertEquals(expected, FizzBuzz.compute(10), "Should return 'Buzz'");
    }

    @Test
    void testForDivisibleByThreeAndFive() {

        String expected = "FizzBuzz";

        assertEquals(expected, FizzBuzz.compute(30), "Should return 'FizzBuzz'");
    }

    @Test
    void testForNotDivisibleByThreeOrFive() {

        int number = 7;

        assertEquals(String.valueOf(number), FizzBuzz.compute(number), "Should return '7'");
    }

    @ParameterizedTest(name = "value={0}, expected={1}")
    @CsvFileSource(resources = "/medium-fizzbuzz-test-data.csv")
    void parametrizedFizzBuzzTest(int value, String expected) {

        assertEquals(expected, FizzBuzz.compute(value), "Return value must be " + expected);
    }

}