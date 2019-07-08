package org.bajiepka.concurrency.modernjavainaction.chapter6;

import lombok.Getter;
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
                Dish.newBuilder().setName("Анчоусы с огурцами").setWeight(100).setType(DishType.MEAT).build(),
                Dish.newBuilder().setName("Сыр пармезан с квасом").setWeight(300).setType(DishType.VEGAN).build(),
                Dish.newBuilder().setName("Селёдка в жирном кефире").setWeight(125).setType(DishType.FISH).build(),
                Dish.newBuilder().setName("Картошка с пеплом").setWeight(350).setType(DishType.VEGAN).build());
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

    enum DishType {
        MEAT, FISH, VEGETABLE, VEGAN, OTHER
    }

    enum CaloricLevel {
        DIET, NORMAL, FAT
    }

    interface Soldable {
    }

    @Getter
    static class Dish implements Soldable {

        String name;
        Integer weight;
        DishType type;

        private Dish() {
        }

        private Dish(String name, Integer weight, DishType type) {
            this.name = name;
            this.weight = weight;
            this.type = type;
        }

        static Dish.DishBuilder newBuilder() {
            return new Dish().new DishBuilder();
        }

        /**
         * простая ерализация Builder с помощью внутреннего класса
         */
        class DishBuilder {

            private String name;
            private Integer weight;
            private DishType type;

            private DishBuilder() {
            }

            public Dish build() {
                return new Dish(this.name, this.weight, this.type);
            }

            public Dish.DishBuilder setName(String name) {
                this.name = name;
                return this;
            }

            public Dish.DishBuilder setWeight(Integer weight) {
                this.weight = weight;
                return this;
            }

            public Dish.DishBuilder setType(DishType type) {
                this.type = type;
                return this;
            }
        }
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
