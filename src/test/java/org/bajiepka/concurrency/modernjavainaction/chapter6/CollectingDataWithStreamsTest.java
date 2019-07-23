package org.bajiepka.concurrency.modernjavainaction.chapter6;

import lombok.Getter;
import org.bajiepka.concurrency.modernjavainaction.model.CaloricLevel;
import org.bajiepka.concurrency.modernjavainaction.model.Dish;
import org.bajiepka.concurrency.modernjavainaction.model.DishType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

import static java.util.stream.Collectors.*;

public class CollectingDataWithStreamsTest {

    /**
     * Список блюд, над которым идёт постоянное издевательство во время
     * написания тестов.
     */
    List<Dish> dishes;

    @Before
    public void setUp() {
        dishes = Arrays.asList(
                new Dish("Анчоусы с огурцами", 100, DishType.MEAT),
                new Dish("Сыр пармезан с квасом", 300, DishType.VEGAN),
                new Dish("Селёдка в жирном кефире", 125, DishType.FISH),
                new Dish("Картошка с пеплом", 350, DishType.VEGAN));
    }

    @Test
    public void test_06_03_Grouping() {

        Map<DishType, List<Dish>> dishesByType = dishes.stream().collect(groupingBy(Dish::getType));
        pl(dishesByType);

        pl(dishes.stream().collect(groupingBy(dish -> {
            if (dish.getWeight() <= 100) {
                return CaloricLevel.DIET;
            } else if (dish.getWeight() <= 150) {
                return CaloricLevel.NORMAL;
            } else return CaloricLevel.FAT;
        })));

        pl(dishes.stream()
                .collect(groupingBy(dish -> {
                            if (dish.getWeight() <= 100) {
                                return CaloricLevel.DIET;
                            } else if (dish.getWeight() <= 150) {
                                return CaloricLevel.NORMAL;
                            } else return CaloricLevel.FAT;
                        }
                )));

        pl(dishes.stream()
                .collect(groupingBy(Dish::getType,
                        mapping(Dish::getName, toList()))));

//        pl(dishes.stream()
//                .collect(groupingBy(Dish::getType, flatMapping()
//                )));

    }

    public void pl(Object o) {
        System.out.println("\n" + o.toString());
    }

    interface Soldable {
    }

    class MyIntSupplier implements IntSupplier {

        @Getter
        private int previous = 0;
        private int current = 1;

        @Override
        public int getAsInt() {

            int oldPrevious = this.previous;
            int next = this.previous + this.current;
            this.previous = this.current;
            this.current = next;

            return oldPrevious;
        }
    }
}
