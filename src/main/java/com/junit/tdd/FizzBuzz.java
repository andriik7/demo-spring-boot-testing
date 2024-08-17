package com.junit.tdd;

public class FizzBuzz {

    public static String compute(int number) {
        StringBuilder returnLine = new StringBuilder();
        if (number % 3 == 0) {
            returnLine.append("Fizz");
        } if (number % 5 == 0) {
            returnLine.append("Buzz");
        } if (returnLine.isEmpty())
            returnLine.append(number);
        return returnLine.toString();
    }
}
