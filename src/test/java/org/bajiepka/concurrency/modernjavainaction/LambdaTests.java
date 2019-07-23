package org.bajiepka.concurrency.modernjavainaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LambdaTests {

    List<Apple> apples;
    List<Plum> plums;
    List<String> words;

    List<Apple> filterApples(List<Apple> basket, Predicate<Apple> p) {
        return basket.stream().filter(apple -> p.test(apple)).collect(Collectors.toList());
    }

    /**
     * Предопределенные коллекции для "тренировки": Яблоки и сливы
     */
    @Before
    public void setUp() {

        apples = Arrays.asList(
                new Apple(Color.GREEN, 191d),
                new Apple(Color.RED, 151d),
                new Apple(Color.GREEN, 160d),
                new Apple(Color.GREEN, 178d),
                new Apple(Color.RED, 170d),
                new Apple(Color.GREEN, 120d));

        plums = Arrays.asList(
                new Plum(Color.GREEN, 32),
                new Plum(Color.RED, 33),
                new Plum(Color.GREEN, 18),
                new Plum(Color.GREEN, 43),
                new Plum(Color.RED, 35),
                new Plum(Color.GREEN, 32));

        words = Arrays.asList(
                "Первое",
                "Второе",
                "Третье",
                "четвертое",
                "пятое"
        );
    }

    @Test
    public void test_01_filterApples() {

        System.out.println("\nВсе имеющиеся яблоки.");
        apples.forEach(apple -> System.out.println(apple));

        System.out.println("\nБерём только зелёные яблоки.");
        filterApples(apples, Apple::isGreenApple)
                .stream()
                .forEach(apple -> System.out.println(apple));

        System.out.println("\nБерем только зелёные и только большие яблоки, а также упорядочиваем их по весу.");
        apples.stream()
                .filter(Apple::isHeavy)
                .filter(Apple::isGreenApple)
                .sorted(comparing(Apple::getWeight))
                .collect(Collectors.toList()).stream()
                .forEach(apple -> System.out.println(apple));

        System.out.println("\nВыводим сгруппированные позиции.");
        apples.stream()
                .collect(Collectors.groupingBy(Apple::getColor))
                .forEach((color, apple) -> System.out.println(String.format("%s / %s", color, apple)));

        System.out.println("\nВыводим отфильтрованные с помощью предиката позиции.");
        apples.stream()
                .filter(new AppleGreenAndHeavyPredicate())
                .forEach(a -> System.out.println(a));

    }

    @Test
    public void test_02_stream_executor() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> threadName = executorService.submit(() -> Thread.currentThread().getName());
        if (threadName.isDone()) System.out.println(threadName);

    }

    /**
     * Метод выбирает все тяжёлые и зелёные яблоки. Фильтр таблицы слив
     * происходит с помощью специального предиката, сортировка с помо-
     * щью компаратора. Также вместо предиката можно использовать прос-
     * тую лямбду:
     * .filter(f -> f.isHeavy(f) && f.isGreen(f)), а также вместо
     * компаратора лямбду:
     * .sorted((Fruit a, Fruit b) -> b.getWeight() - a.getWeight())
     * .sorted(Comparator.comparingInt(Fruit::getWeight).reversed())
     */
    @Test
    public void test_03_different_functional_interfaces() {

        plums.stream()
                .filter(new GreenAndHeavyFruitsPredicate())
                .sorted(new AscWeightFruitComparator())
                .forEach(p -> log.info(p.toString(), p));
    }

    @Test
    public void test_04_target_type() {
        execute((Runnable) () -> {
            System.out.println("Выполнение Runnable завершено.");
        });
        execute((Action) () -> {
            System.out.println("Выполнение Action завершено.");
        });
    }

    void execute(Runnable runnable) {
        System.out.println("Запускаем Runnable");
        runnable.run();
    }

    void execute(Action action) {
        System.out.println("Запускаем Action");
        action.act();
    }

    @Test
    public void test_05_lambda_variables() {

        final int port = 2000;
        Runnable r = () -> System.out.println(port);
        r.run();
    }

    @Test
    public void test_06_method_reference() {

        System.out.println("\nПечатаем число, каждый разряд которого является длиной каждого слова из списка слов:" +
                "с отбором по условию, что первая буква слова - заглавная");

        words.stream()
                .filter(ValidationTools::isValidName)
                .map(String::length)
                .forEach(System.out::print);

        System.out.println("\nУпорядочиваем все слова списка и делаем их lowercase:");

        words.stream()
                .map(String::toLowerCase)
                .sorted(String::compareTo)
                .forEach(System.out::println);

        System.out.println("\nВыводим сумму количества вукв в каждом слове:");
        System.out.println(words.stream()
                .mapToInt(s -> s.length())
                .summaryStatistics().getSum());

    }

    @Test
    public void test_07_constructor_method_reference() {

        BiFunction<Color, Integer, Plum> plum = Plum::new;
        Plum p1 = plum.apply(Color.GREEN, 120);
        System.out.println(p1);

        Supplier<Plum> plumSupplier = Plum::new;
        Plum p2 = plumSupplier.get();
        System.out.println(p2);

        Function<Integer, Plum> f = Plum::new;
        Arrays.asList(89, 90, 91, 92, 93, 94, 95, 96).stream()
                .map(Plum::new)
                .collect(Collectors.toList())
                .forEach(System.out::println);

    }

    @Test
    public void test_08_triple_constructor() {
        Map<String, TriFunction<Color, Integer, String, Fruit>> map = new HashMap<>();
        map.putIfAbsent("plum", Plum::new);

        Plum plum = (Plum) map.get("plum").apply(Color.GREEN, 150, "Синеглазка");
        System.out.println(plum);

        apples.sort(comparing(Apple::getWeight)
                .reversed()
                .thenComparing(Apple::getColor));

        Predicate<Plum> isGreenPlum = p -> p.isGreen(p);
        Predicate<Plum> isRedPlum = isGreenPlum.negate();
        Predicate<Plum> isGreenOrRedPlum = isGreenPlum.or(isRedPlum);

        plums.stream().filter(isGreenOrRedPlum).forEach(System.out::println);

    }

    @Test
    public void test_09_function_pipelines() {

        Function<String, String> justify = x -> "   " + x;
        Function<String, String> border = x -> x + " |";
        Function<String, String> timestamp = x -> String.format("[%s] %s", new Date().toString(), x);
        Function<String, String> chatMessage = justify.andThen(border).andThen(timestamp);

        System.out.println(chatMessage.apply("Привет!"));

    }

    enum Color {
        RED, GREEN
    }

    @FunctionalInterface
    interface ApplePredicate extends Predicate<Apple> {
        @Override
        boolean test(Apple apple);
    }

    @FunctionalInterface
    interface Action {
        void act();
    }

    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    static class Apple {
        Color color;
        Double weight;

        public static boolean isGreenApple(Apple apple) {
            System.out.println("Проверка цвета Яблока");
            return Color.GREEN.equals(apple.getColor());
        }

        public static boolean isHeavy(Apple apple) {
            System.out.println("Проверка веса Яблока");
            return apple.getWeight() > 150d;
        }
    }

    /**
     * Статиеский класс, существующий для валидации каких-то данных
     */
    static class ValidationTools {
        static boolean isValidName(String string) {
            return Character.isUpperCase(string.charAt(0));
        }
    }

    @Getter
    abstract class Fruit {
        protected Color color;
        int weight;

        protected boolean isHeavy(Fruit fruit) {
            System.out.println("Проверка веса Фрукта");
            return fruit.getWeight() > 10;
        }

        protected boolean isGreen(Fruit fruit) {
            System.out.println("Проверка цвета Фрукта");
            return Color.GREEN.equals(fruit.getColor());
        }
    }

    @Getter
    class Plum extends Fruit {

        private String sort;

        public Plum(Color color, int weight) {
            this.color = color;
            this.weight = weight;
        }

        public Plum(Color color, int weight, String sort) {
            this.color = color;
            this.weight = weight;
            this.sort = sort;
        }

        public Plum(Color color) {
            this(color, 0);
        }

        public Plum(Integer weight) {
            this(Color.GREEN, weight);
        }

        public Plum() {
            this(Color.GREEN, 66);
        }

        @Override
        protected boolean isHeavy(Fruit fruit) {
            System.out.println("Проверка веса Сливы");
            return this.weight > 20;
        }

        @Override
        public String toString() {
            return String.format("Plum {Цвет:%s / вес:%d / сорт:%s}", color.toString(), weight, sort);
        }
    }

    class AppleGreenAndHeavyPredicate implements ApplePredicate {

        @Override
        public boolean test(Apple apple) {
            return apple.getWeight() > 150d && Color.GREEN.equals(apple.getColor());
        }
    }

    class AscWeightFruitComparator implements Comparator<Fruit> {
        @Override
        public int compare(Fruit o1, Fruit o2) {
            return o2.getWeight() - o1.getWeight();
        }
    }

    class GreenAndHeavyFruitsPredicate implements Predicate<Fruit> {
        @Override
        public boolean test(Fruit fruit) {
            return fruit.isHeavy(fruit) && fruit.isGreen(fruit);
        }
    }

}
