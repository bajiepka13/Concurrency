package org.bajiepka.concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferenceMethodApplicationTests {

    private final String CHAR_SEQUENCE_PREFIX = "[";
    private final String CHAR_SEQUENCE_SUFFIX = "]";
    private final String CHAR_SEQUENCE_DIVIDER = ",";

    @Test
    public void test_01_referenceMerhodFactories() {

        /*
         *   Простая фабрика, реализованная на функциональном интерфейсе
         *   с помощью Method reference (ссылка на метод)
         */
        JavaApplicationFactory factory = JavaApplication::new;
        JavaApplication app = factory.create("Bajiepka test app", 100);

        assertTrue("Проверяем свойства созданной программы",
                app.getCost().equals(100) &&
                        app.getName().equals("Bajiepka test app"));

        /**
         *  Пример сортировки пользователей с помощью Method reference
         */
        Stream.of(
                factory.create("2. Калькулятор", 1),
                factory.create("3. Чат", 2),
                factory.create("1. Блокнот", 3))
                .sorted(Comparator.comparing(JavaApplication::getCost))
                .forEach(System.out::println);

        PersonFactory<Person> factory2 = Person::new;
        Person p = factory2.create("John", "Doe");
        System.out.println(p);

        Predicate<String> predicate = (s) -> s.length() > 0;
        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> notEmpty = isEmpty.negate();

    }

    @Test
    public void test_02_referenceMethodChain() {

        /*
         *   Цепочка обращений (Function composition),
         *   реализованная на функциональном интерфейсе
         *   с помощью Method reference (ссылка на метод)
         */
        Function<String, String> trim = String::trim;
        String result = trim
                .andThen(String::toLowerCase)
                .andThen(StringBuilder::new)
                .andThen(StringBuilder::reverse)
                .andThen(StringBuilder::toString)
                .apply(" ABCDEFG ");

        assertTrue("Проверка цепочки обращений через Method Reference", result.equals("gfedcba"));

    }

    @Test
    public void test_03_setream_GroupingBy() {

        FilmHeroFactory heroFactory = FilmHero::new;

        Map<String, Set<String>> grouppedHeroes = Stream.of(
                heroFactory.create("Ned", "Stark", 33),
                heroFactory.create("Robb", "Stark", 42),
                heroFactory.create("Arya", "Stark", 38),
                heroFactory.create("Aegon", "Targaryen", 55),
                heroFactory.create("Daenerys", "Targaryen", 27),
                heroFactory.create("Jaime", "Lannister", 31),
                heroFactory.create("Tyrion", "Lannister", 36))
                .sorted(Comparator.comparing(FilmHero::getAge))
                .collect(Collectors.groupingBy(FilmHero::getSurname, //группируем по фамилии
                        Collectors.mapping(FilmHero::getName, Collectors.toSet())));

        Map<String, Integer> countedHeroes = Stream.of(
                heroFactory.create("Ned", "Stark", 33),
                heroFactory.create("Robb", "Stark", 42),
                heroFactory.create("Arya", "Stark", 38),
                heroFactory.create("Aegon", "Targaryen", 55),
                heroFactory.create("Daenerys", "Targaryen", 27),
                heroFactory.create("Jaime", "Lannister", 31),
                heroFactory.create("Tyrion", "Lannister", 36))
                .sorted(Comparator.comparing(FilmHero::getAge, Comparator.reverseOrder()))
                .collect(Collectors.groupingBy(FilmHero::getSurname, //группируем по фамилии
                        Collectors.summingInt(FilmHero::getAge)));

        assertEquals("Сверяем размер полученной сгруппированной коллекций № 1", 3, grouppedHeroes.size());
        assertEquals("Сверяем общую сумму лет актёров, в полученной коллекции № 2",
                Integer.valueOf(33 + 42 + 38 + 55 + 27 + 31 + 36),
                countedHeroes
                        .values()
                        .stream()
                        .reduce(Integer::sum).get());

    }

    @Test
    public void test_04_stream_joining() {

        assertEquals("Проверяем объединение значений в строку",
                "[1,2,3,4,5,6,7,8,9,10]",
                Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                        .stream()
                        .collect(Collectors.joining(CHAR_SEQUENCE_DIVIDER, CHAR_SEQUENCE_PREFIX, CHAR_SEQUENCE_SUFFIX)));
    }

    @FunctionalInterface
    interface JavaApplicationFactory {
        /**
         * <p>Простая фабрика, использующая ссылку на метод (Method reference)
         * <a href="https://www.google.ru/search?q=javadoc">Лучшее руководство</a> как основу
         * </p>
         * Это часть тестов {@link ConcurrencyApplicationTests}
         *
         * @param name наименование java приложения
         * @param cost стоимость java приложения         *
         * @return {@link JavaApplication} новое java-приложение
         * @author Captain America
         * @see <a href="http://www.link_to_jira/HERO-402">HERO-402</a>
         * @since v1
         */
        JavaApplication create(String name, Integer cost);
    }

    @FunctionalInterface
    interface RandomFactory<Q> {
        Q nextInt();
    }

    @FunctionalInterface
    interface PersonFactory<P extends Person> {
        P create(String firstName, String lastName);
    }

    @FunctionalInterface
    interface FilmHeroFactory {
        /**
         * <p>Простая фабрика, использующая ссылку на метод (Method reference)
         * Это часть тестов {@link ConcurrencyApplicationTests}
         *
         * @param name    имя главного героя
         * @param surname фамилия главного героя (нужна для группировки по семьям)
         * @param age     полное количество лет
         * @return {@link FilmHero} новый главный герой
         * @since v1
         */
        FilmHero create(String name, String surname, Integer age);
    }

    @ToString
    @AllArgsConstructor
    @Getter
    class JavaApplication {
        String name;
        Integer cost;
    }

    class Person {

        String firstname;
        String lastName;

        Person() {
        }

        public Person(String firstname, String lastName) {
            this.firstname = firstname;
            this.lastName = lastName;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstname='" + firstname + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }

    @Getter
    @AllArgsConstructor
    class FilmHero {
        String name, surname;
        Integer age;
    }

}
