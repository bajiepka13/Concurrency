package org.bajiepka.concurrency.modernjavainaction;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FunctionalStyleDataProcessingTests {

    final String testString = "Using the flatMap method has the effect of mapping each array not with a stream but " +
            "with the contents of that stream. All the separate streams that were generated when using " +
            "map(Arrays::stream) get amalgamated—flattened into a single stream. Figure 5.6 " +
            "illustrates the effect of using the flatMap method. Compare it with what map does in " +
            "figure 5.5.";
    final int width = 40;
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
    public void test_01_takewhile_example() {

        List<Integer> numbers = Arrays.asList(
                new Integer(1), new Integer(2), new Integer(3),
                new Integer(4), new Integer(5), new Integer(6),
                new Integer(7), new Integer(8), new Integer(9));

        /**
         * Методы takeWhile() и dropWhile() - фишечки JDK 9+, поэтому в текущем проекте
         * закомментированы.
         */
        List<Integer> numbersLessThanSix = numbers.stream()
//                .takeWhile(i -> i < 6)
//                .dropWhile(i -> i> 6)
                .skip(1)
                .filter(i -> i > 6)
                .limit(3)
                .collect(Collectors.toList());


    }

    @Test
    public void test_02_mapping_and_flattering_stream() {

        StringBuilder sb = new StringBuilder();
        testString.chars().forEach(c -> processLine(sb, c));

        System.out.println(sb.toString());

    }

    /**
     * Метод выполняет обработку строки - преобразует однострочную строку
     * в многострочную, с фиксированной длиной каждой подстроки.
     *
     * @param sb обычный StringBuilder, склеивающий символы в строку
     * @param c  char буква стрима char'ов
     */
    private void processLine(StringBuilder sb, int c) {

        /*
         * Если нам передали перенос строки ("\n"), то игнорируем символ переноса
         */
        if (c == 10) return;

        if (sb.length() > 1) {
            /*
             * Если мы перенесли строку и следующщий после переноса (\n) - пробел,
             * то пропускаем его, чтобы в начале строк не было лишних отступов.
             */
            if (c == 32 && sb.charAt(sb.length() - 1) == (char) 10) return;

            /*
             * Так как мы обходим стрим char, то проверяем каждый раз, когда
             * приходит новый char, текущую длина строки sb для переноса строки.
             */
            if (sb.length() % width == 0) {
                sb.append("\n");
                if (c == 32) return;
            }
        }

        sb.append((char) c);
    }

    @Test
    public void test_03_reduce_examples() {

        IntStream.rangeClosed(1, 50).reduce(Integer::sum).ifPresent(System.out::println);
        IntStream.rangeClosed(1, 50).reduce(Integer::max).ifPresent(System.out::println);

    }

    @Test
    public void test_04_intstream_test() {

        dishes.stream().mapToInt(Dish::getWeight);

    }

    @Test
    public void test_04_pythagorean_triple() {

        Stream<double[]> triples = IntStream.rangeClosed(1, 1000).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, 1000)
                        .mapToObj(b -> new double[]{a, b, Math.sqrt(a * a + b * b)}))
                .filter(t -> t[2] % 1 == 0);

        triples.forEach(t -> System.out.println(t[0] + " / " + t[1] + " / " + t[2]));

    }

    @Test
    public void test_05_stream_of_nullable() {

        /**
         * Java 9+ features, которые не дают коду работать)
         */
        /*Stream<String> strings = Stream.of("config", "home", "user")
                .flatMap(key -> Stream.ofNullable(System.getProperty(key)));*/

        int[] numbers = {1, 3, 5, 7, 11, 13};
        int sum = Arrays.stream(numbers).sum();
        System.out.println(sum);
    }

    @Test
    public void test_06_file_streams() {

        Path pathToFile = Paths.get("D:\\programs\\IntelliJ IDEA 2019.1.1\\Install-Windows-zip.txt");

        try {
            Stream<String> fileLines = Files.lines(pathToFile, Charset.defaultCharset());
            fileLines
                    .flatMap(line -> Arrays.stream(line.split("\n")))
                    .forEach(l -> System.out.println(l));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_07_stream_iterate() {

        IntStream.iterate(2, a -> a * 2).limit(10).forEach(System.out::println);

    }

    @Test
    public void test_08_fibonacci_stream_iteration() {

        int fibonacciTotal = 25;
        System.out.println(String.format("\nЧисла Фибоначчи (первые %d штук)", fibonacciTotal));

        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(fibonacciTotal)
                .map(v -> v[0])
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .ifPresent(str -> System.out.println(str));

        /**
         * Еще одна Java 9+ плюшка
         */
        /*IntStream
                .iterate(0, n -> n < 100, n -> n + 2)
                .forEach(System.out::println);*/

        MyIntSupplier supplier = new MyIntSupplier();

        System.out.println(String.format("\nЧисла Фибоначчи с помощью лямбд и метода generate (первые %d штук)", fibonacciTotal));
        IntStream.generate(supplier)
                .limit(10)
                .anyMatch(a -> a == 3);
//                .mapToObj(String::valueOf)
//                .reduce(String::join)
//                .ifPresent(str -> System.out.println(str));

    }

    @Test
    public void test_09_generate_example() {

        Stream.generate(Math::random).limit(5).forEach(System.out::println);

    }

    @Test
    public void test_10_collectors_examples() {

        Map<DishType, List<Dish>> mapByType = dishes.stream().collect(Collectors.groupingBy(Dish::getType));

        int totalWeight = dishes.stream().collect(Collectors.summingInt(Dish::getWeight));

        Double averageWeight = dishes.stream().collect(Collectors.averagingInt(Dish::getWeight));

        System.out.println(String.format("Total: %d, Average: %f ", totalWeight, averageWeight));

    }

    enum DishType {
        MEAT, FISH, VEGAN
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

        static DishBuilder newBuilder() {
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

            public DishBuilder setName(String name) {
                this.name = name;
                return this;
            }

            public DishBuilder setWeight(Integer weight) {
                this.weight = weight;
                return this;
            }

            public DishBuilder setType(DishType type) {
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
