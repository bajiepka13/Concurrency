package org.bajiepka.concurrency.modernjavainaction.chapter6;

import org.bajiepka.concurrency.modernjavainaction.model.Dish;
import org.bajiepka.concurrency.modernjavainaction.model.DishType;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

public class UsingCustomCollector {

    private List<Dish> dishes;

    public static Boolean isPrime(List<Integer> primes, int candidate) {
        final int candidateRoot = (int) Math.sqrt((double) candidate);
        return takeWhile(primes, i -> i <= candidateRoot)
                .stream()
                .noneMatch(i -> candidate % i == 0);
    }

    public static <A> List<A> takeWhile(List<A> list, Predicate<A> predicate) {
        int i = 0;
        for (A item : list) {
            if (!predicate.test(item)) {
                return list.subList(0, i);
            }
            i++;
        }
        return list;
    }

    @Before
    public void setUp() {

        dishes = asList(
                new Dish("Анчоусы с огурцами", 100, DishType.VEGAN),
                new Dish("Ананасы с сыром", 320, DishType.VEGAN),
                new Dish("Сливочное масло в оливковом масле", 470, DishType.VEGAN),

                new Dish("Колбаса Краковская", 220, DishType.MEAT),
                new Dish("Сало засоленное", 350, DishType.MEAT),
                new Dish("Буженина с салом", 430, DishType.MEAT),

                new Dish("Бычки морские", 80, DishType.FISH),
                new Dish("Ставрида черноморская", 125, DishType.FISH),
                new Dish("Зубатка", 210, DishType.FISH),

                new Dish("Лапша с кетчупом", 110, DishType.VEGAN),
                new Dish("Сыр без плесени", 145, DishType.VEGAN),
                new Dish("Капуста со шпинатом", 170, DishType.VEGAN));
    }

    @Test
    public void test_01_usingMyCollector() {
        System.out.println("Собираем стрим существующей коллекции собственной реализацией коллектора");
        List<Dish> collectedDishes = dishes.stream().collect(new MyCollector<Dish>());
        System.out.println(collectedDishes);
    }

    @Test
    public void test_02_usingCustomCollector() {
        System.out.println("Собираем стрим существующей коллекции с помощью одной из" +
                "перегрузок метода collect()");
        List<Dish> collectedDishes = dishes.stream().collect(
                ArrayList::new,         //   Supplier
                List::add,              //   Accumulator
                List::addAll);          //   Combiner
        System.out.println(collectedDishes);
    }

    @Test
    public void test_03_detectingWhetherNumberIsPrime() {

        /*  Тест не работает. Не смотря на то, что проверка синтаксиса не ругается на ошибки
          при компиляции происходит ошибка "java: cannot find symbol" */

    }

    public Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
        return IntStream.rangeClosed(2, n).boxed().collect(new PrimeNumbersCollector());
    }

    class PrimeNumbersCollector implements
            Collector<Integer,
                    Map<Boolean, List<Integer>>,
                    Map<Boolean, List<Integer>>> {

        @Override
        public Supplier supplier() {
            return () -> new HashMap<Boolean, List<Integer>>() {{
                put(true, new ArrayList<>());
                put(false, new ArrayList<>());
            }};
        }

        @Override
        public BiConsumer accumulator() {
            return null;
            /*return (Map<Boolean, List<Integer>> acc,
                    Integer candidate) -> {
                Boolean candidateIsPrime = isPrime(acc.get(true), candidate);
                List<Integer> found = acc.get(candidateIsPrime);
                found.add(candidate);
            };*/
        }

        @Override
        public BinaryOperator combiner() {
            return null;
            /*return (Map<Boolean, List<Integer>> map1,
                    Map<Boolean, List<Integer>> map2) -> {
                map1.get(true).addAll(map2.get(true));
                map1.get(false).addAll(map2.get(false));
                return map1;
            };*/
        }

        @Override
        public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
        }
    }
}
