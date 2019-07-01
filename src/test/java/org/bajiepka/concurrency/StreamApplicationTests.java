package org.bajiepka.concurrency;

import lombok.Getter;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StreamApplicationTests {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Test
    public void test_01_distinct_streams() {

        Person firstMike = new Person("Mike");
        Person secondMike = new Person("Mike");

        assertEquals("Persons with same names are equal.", firstMike, secondMike);

        Arrays.asList("one", "two", "three", "four", "five", "three", "six", "two")
                .stream()
                .distinct()
                .forEach(e -> System.out.println("element: " + e));

        Arrays.asList(
                new Person("Mike"),
                new Person("John"),
                new Person("Mike"))
                .parallelStream()
                .filter(distinctByKey(p -> p.getName()))
                .forEach(e -> System.out.println("element: " + e));
    }

    @Test
    public void test_02_simpson_predicate_test() {

        Simpson bart1 = new Simpson("Bart");
        Simpson bart2 = new Simpson("Bart");

        assertEquals("Bart is still Bart", bart1, bart2);

        Simpson overridenHomer = new Simpson("Homer") {
            @Override
            public int hashCode() {
                return (43 + 777 + 1);
            }
        };

        assertNotEquals("But Homer is not as he was", new Simpson("Homer"), overridenHomer);

        Set<Simpson> set = new HashSet();
        set.add(new Simpson("Homer"));
        set.add(new Simpson("Marge"));
        set.add(new Simpson("Homer"));
        set.add(overridenHomer);

        assertEquals("Всего три симпсона", 3, set.size());

        Predicate<Simpson> startsWithHo = new Predicate<Simpson>() {
            @Override
            public boolean test(Simpson simpson) {
                return simpson.getName().startsWith("Ho");
            }
        };

        Predicate<Simpson> endWithMer = new Predicate<Simpson>() {
            @Override
            public boolean test(Simpson simpson) {
                return simpson.getName().endsWith("mer");
            }
        };

        set.stream().filter(startsWithHo.and(endWithMer)).forEach(s -> System.out.println(s.getName()));

    }

    @Test
    public void test_03_primitives_stream() {

        Simpson homerSimpson = new Simpson("Homer");

        IntStream intStream = IntStream.of(1, 2, 3);
        DoubleStream doubleStream = DoubleStream.of(4.0, 5.0, 6.0);
        LongStream longStream = LongStream.of(7, 8, 9);

        OptionalDouble value = IntStream.rangeClosed(43, 122).average();
        if (value.isPresent()) System.out.println(value.getAsDouble());

        System.out.println("1. Инициализируем нуллом опционального симпсона.");
        Optional<Simpson> nullSimpson = Optional.ofNullable(null);
        System.out.println(nullSimpson);

        System.out.println("2. Инициализируем пустого опционального симпсона.");
        Optional<Simpson> anotherNullSimpson = Optional.empty();
        System.out.println(anotherNullSimpson);

        System.out.println("3. Пробуем вернуть произвольную строку, если значение пустое.");
        Optional<String> optionalValue = Optional.empty();
        System.out.println(optionalValue.orElseGet(() -> "Nothing"));

        System.out.println("4. Пробуем вывести имя пустого симпсона.");
        Optional<Simpson> emptySimpson = Optional.empty();
        emptySimpson.ifPresent(simpson -> {
            System.out.println(simpson.getName());
        });

        System.out.println("5. Проубем вывести Симпсона в ловеркейс.");
        Optional<Optional<Simpson>> simpson = Optional.of(Optional.of(homerSimpson));
        Optional<String> lowercaseHomer = simpson.flatMap(s -> s.map(simpson1 -> simpson1.getName().toLowerCase()));
        System.out.println(lowercaseHomer.orElse("No Simpson here!"));

        System.out.println("6. Пробуем вывести имя гомера маленькими буквами.");
        Optional<String> homerName = Optional.of("HOMER");
        homerName.ifPresent(h -> System.out.println(h.toLowerCase()));

    }

    @ToString
    @Getter
    class Person {

        String name;

        public Person(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Person p = (Person) obj;
            return name == p.name;

        }
    }

    @ToString
    @Getter
    class Simpson {

        String name;

        public Simpson(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            Simpson otherSimpson = (Simpson) obj;
            return this.name.equals(otherSimpson.name) &&
                    this.hashCode() == otherSimpson.hashCode();
        }

        @Override
        public int hashCode() {
            return (43 + 777);
        }
    }
}
