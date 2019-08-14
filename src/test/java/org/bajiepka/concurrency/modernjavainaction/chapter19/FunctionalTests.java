package org.bajiepka.concurrency.modernjavainaction.chapter19;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class FunctionalTests {

    static DoubleUnaryOperator currencyConverter(double from, double to) {
        return (double x) -> x * to / from;
    }

    static String moneyFormat(Double sum) {
        return String.format("%.2f $", sum);
    }

    @Test
    public void test_01_functionalProgremming() {

        Function<String, Integer> strToInt = Integer::parseInt;
        System.out.println(strToInt.apply("1000"));

        DoubleUnaryOperator convRubToUsd = currencyConverter(64, 1);
        System.out.printf("%.2f $", convRubToUsd.applyAsDouble(550));

        System.out.println("\nСитомиость номинальных купюр:");

        Arrays.asList(100, 200, 500, 1000, 2000, 5000)
                .stream()
                .map(convRubToUsd::applyAsDouble)
                .map(x -> moneyFormat(x))
                .forEach(System.out::println);

    }

    @Test
    public void test_02_curryingFunctions() {

        Function<Integer, Function<Integer, Function<Integer, Function<Integer, Integer>>>> curryAdder = first -> second -> third -> fourth ->
                first + second + third + fourth;

        System.out.println(curryAdder
                .apply(1)
                .apply(2)
                .apply(3).andThen(x -> x * x)
                .apply(14)
        );

    }

}
