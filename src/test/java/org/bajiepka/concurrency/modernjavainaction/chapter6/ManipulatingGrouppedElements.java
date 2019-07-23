package org.bajiepka.concurrency.modernjavainaction.chapter6;

import org.bajiepka.concurrency.modernjavainaction.model.CaloricLevel;
import org.bajiepka.concurrency.modernjavainaction.model.Dish;
import org.bajiepka.concurrency.modernjavainaction.model.DishType;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

public class ManipulatingGrouppedElements {

    private List<Dish> dishes;
    private Map<String, List<String>> dishTags = new HashMap<>();

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

        dishTags.put("Анчоусы с огурцами", asList("Постно", "Белок"));
        dishTags.put("Ананасы с сыром", asList("Подогрев", "Постно"));
        dishTags.put("Сливочное масло в оливковом масле", asList("Жирно", "Очень жирно"));
        dishTags.put("Колбаса Краковская", asList("Жирно", "Копчености"));
        dishTags.put("Сало засоленное", asList("Белок", "Очень жирно"));
        dishTags.put("Буженина с салом", asList("Копчености", "Жирно"));
        dishTags.put("Бычки морские", asList("Подогрев", "Постно"));
        dishTags.put("Ставрида черноморская", asList("Подогрев", "Постно"));
        dishTags.put("Зубатка", asList("Белок", "Жирно"));

        dishTags.put("Лапша с кетчупом", asList("Белок", "Жирно"));
        dishTags.put("Сыр без плесени", asList("Подогрев", "Постно"));
        dishTags.put("Капуста со шпинатом", asList("Белок", "Жирно"));

    }

    protected void pl(String msg, Object o) {
        System.out.println("\n" + msg + "\n" + o.toString());
    }

    @Test
    public void test_01_weird_java12_static_collector_methods() {

       /* System.out.println(dishes.stream()
                .collect(
                        groupingBy(Dish::getType,
                                flatMapping(
                                        dish -> dishTags.get(dish.getName()).stream(),
                                        toSet()))
                )
        );*/
    }

    @Test
    public void test_02_weird_java12_static_collector_methods() {

        System.out.println(dishes.stream()
                .collect(groupingBy(
                        Dish::getType,
                        groupingBy(
                                dish -> {
                                    if (dish.getWeight() < 110) {
                                        return CaloricLevel.DIET;
                                    } else if (dish.getWeight() >= 110 && dish.getWeight() <= 200) {
                                        return CaloricLevel.NORMAL;
                                    } else {
                                        return CaloricLevel.FAT;
                                    }
                                },
                                mapping(
                                        dish -> String.format("%s (%d)", dish.getName(), dish.getWeight()),
                                        toList()))
                        )
                )
        );
    }

    @Test
    public void test_03_weird_java12_static_collector_methods() {

        System.out.println(dishes.stream()
                .collect(groupingBy(Dish::getType, counting())));

    }

    @Test
    public void test_04_weird_java12_static_collector_methods() {

        System.out.println(dishes.stream()
                .collect(groupingBy(
                        Dish::getType,
                        collectingAndThen(maxBy(comparingInt(Dish::getWeight)), Optional::get)
                        )
                )
        );

    }

    @Test
    public void test_05_weird_java12_static_collector_methods() {

        System.out.println(dishes.stream()
                .collect(groupingBy(
                        Dish::getType,
                        summingInt(Dish::getWeight)
                        )
                )
        );

    }

    @Test
    public void test_06_weird_java12_static_collector_methods() {

        System.out.println(dishes.stream()
                .collect(groupingBy(Dish::getType, mapping(dish -> {
                    if (dish.getWeight() < 120) return CaloricLevel.DIET;
                    else if (dish.getWeight() > 120 && dish.getWeight() < 200) {
                        return CaloricLevel.NORMAL;
                    } else {
                        return CaloricLevel.FAT;
                    }
                }, toSet())))
        );

        System.out.println(dishes.stream()
                .collect(groupingBy(Dish::getType, mapping(dish -> {
                    if (dish.getWeight() < 120) return CaloricLevel.DIET;
                    else if (dish.getWeight() > 120 && dish.getWeight() < 200) {
                        return CaloricLevel.NORMAL;
                    } else {
                        return CaloricLevel.FAT;
                    }
                }, toCollection(HashSet::new))))
        );

    }

    @Test
    public void test_07_weird_java12_partitioning_methods() {

        Map<Boolean, List<Dish>> vegNonvegDishes = dishes.stream()
                .collect(partitioningBy(Dish::isVegeterian));
        pl("Собираем мапу вегетарианских продуктов.", vegNonvegDishes.get(true));

        Map<Boolean, Map<DishType, List<Dish>>> vegDishesByType = dishes.stream().collect(
                partitioningBy(Dish::isVegeterian,
                        groupingBy(Dish::getType)));
        pl("Собираем двухуровневую мапу вегетарианских продуктов.", vegDishesByType.get(true));

        Map<Boolean, Dish> mostCaloricPartitionedByVegeterian =
                dishes.stream().collect(
                        partitioningBy(Dish::isVegeterian,
                                collectingAndThen(
                                        maxBy(comparingInt(Dish::getWeight)),
                                        Optional::get)
                        )
                );
        pl("Собираем мапу продуктов с максимальным количеством веса по отбору вегетарианский / невегетарианский.",
                mostCaloricPartitionedByVegeterian);


    }

    @Test
    public void test_08_weird_java12_partitioning_pain() {

        int value = 10;
        Map<Boolean, List<Integer>> partitionPrimes =
                IntStream.rangeClosed(2, value)
                        .boxed()
                        .collect(partitioningBy(c -> isPrime(c)));

        System.out.println(partitionPrimes);

    }

    protected Boolean isPrime(int candidate) {
        int candidateRoot = (int) Math.sqrt(candidate);
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate % 1 == 0);
    }

}
