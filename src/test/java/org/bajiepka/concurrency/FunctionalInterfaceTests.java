package org.bajiepka.concurrency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FunctionalInterfaceTests {

    Predicate<Integer> moreThanTen;
    BinaryOperator<Integer> multiplier;
    UnaryOperator<Integer> square;
    Function<Integer, String> dollars;
    Consumer<Integer> yeller;
    Supplier<Codeable> coder;

    @Before
    public void setUp() {
        moreThanTen = x -> x > 10;
        multiplier = (x, y) -> x * y;
        square = x -> x * x;
        dollars = x -> String.format("%s долларов", String.valueOf(x));
        yeller = x -> System.out.println(String.format("I'm just yelling and do not return anything. Only some value %s", x));
        coder = () -> new Coder("Ben Johnson", 22);
    }

    @Test
    public void test_01_predicate() {

        assertTrue(moreThanTen.test(21));
    }

    @Test
    public void test_02_binary_operator() {

        assertEquals("Перемножение двух чисел с помощью BinaryOperator<T>",
                multiplier.apply(10, 15),
                new Integer(150));
    }

    @Test
    public void test_03_unary_operator() {

        assertEquals("Возведене в квадрат с помощью UnaryOperator<T>",
                square.apply(12),
                new Integer(144));
    }

    @Test
    public void test_04_function_interface() {

        assertEquals("Возведене в квадрат с помощью UnaryOperator<T>",
                dollars.apply(100),
                "100 долларов");
    }

    @Test
    public void test_05_consumer() {

        yeller.accept(100);
    }

    @Test
    public void test_06_supplier() {

        assertEquals("Got a new coder, and let's test if everything is correct",
                new Coder("Ben Johnson", 22).getName(),
                ((Coder) coder.get()).getName());
    }

    @Test
    public void test_07_more_functional_interfaces() {

        final String TEST_KEY_JOHN = "John";

        /**
         * Сначала лямбдой, без Method reference
         */
        Map<String, Integer> nameMap = new HashMap<>();
        Integer v1 = nameMap.computeIfAbsent(TEST_KEY_JOHN, s -> s.length());
        assertEquals("Првоеряем значение по тестовому ключу",
                new Integer(TEST_KEY_JOHN.length()),
                v1);

        System.out.println(nameMap.get(TEST_KEY_JOHN));

        final String TEST_KEY_MICHAEL = "Michael";

        /**
         * Теперь используется Method reference, т.к. вторым параметром
         * computeIfPresent() является FunctionalInterface<>, то туда
         * можно передать либо лямбду, как в вышеуказанном примере, либо
         * сразу ссылку на нужным метод
         */
        Integer v2 = nameMap.computeIfAbsent(TEST_KEY_MICHAEL, String::length);
        assertEquals("Првоеряем значение по тестовому ключу",
                new Integer(TEST_KEY_MICHAEL.length()),
                v2);

        System.out.println(nameMap.get(TEST_KEY_MICHAEL));

    }

    @Test
    public void test_08_int_to_string() {

        Function<Integer, String> intToString = Object::toString;
        Function<String, String> quoteSmth = s -> String.format("\"%s\"", s);
        System.out.println(String.format("Это %s код", quoteSmth.apply(intToString.apply(10))));

    }

    /**
     * Метод предназначен для быстрого преобразования short[] -> byte[]
     *
     * @param shorts   массив short чисел, который требуется преобразовать в массив байтов
     * @param function функция преобразования short -> byte
     * @return массив байтов
     */
    byte[] shortsToBytes(short[] shorts, ShortByteFunction function) {
        byte[] bytes = new byte[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i] = function.applyAsByte(shorts[i]);
        }
        return bytes;
    }

    @Test
    public void test_09_short_byte_function() {

        ShortByteFunction f = s -> (byte) s;
        ShortByteFunction f2 = s -> (byte) (s * 3);

        short[] shorts = {
                (short) 1,
                (short) 2,
                (short) 3
        };

        byte[] bytes = shortsToBytes(shorts, f);

        byte[] expected = {
                (byte) 1,
                (byte) 2,
                (byte) 3
        };

        assertArrayEquals(expected, bytes);

        byte[] bytes2 = shortsToBytes(shorts, f2);

        byte[] expected2 = {
                (byte) 3,
                (byte) 6,
                (byte) 9
        };

        assertArrayEquals(expected2, bytes2);

    }

    @Test
    public void test_10_two_arity_functions() {

        BiFunction<Integer, Integer, Boolean> compareIntegers = (x, y) -> x > y;
        assertTrue(compareIntegers.apply(10, 2));

        ToIntBiFunction<String, String> convertorCompare = (a, b) -> Double.valueOf(a) > Double.valueOf(b) ? 1 : 0;
        assertEquals("Если число а больше чем число б, то должны получить 1",
                1, convertorCompare.applyAsInt("120", "110"));

    }

    @Test
    public void test_11_bifunctions() {

        final Integer VOVA_SALARY = 30_000;
        final String EMPLOYEE_NAME = "Вова";

        Map<String, Integer> salaries = new HashMap<>();
        salaries.put(EMPLOYEE_NAME, VOVA_SALARY);
        salaries.put("Петя", 29_000);
        salaries.put("Вадик", 28_000);
        salaries.put("Виталя", 27_000);

        salaries.replaceAll((name, value) -> name.equals(EMPLOYEE_NAME) ? value + 1_000 : value);

        assertEquals("Проверяем зарплату у Вовы. Должна быть на тысячу больше",
                new Integer(VOVA_SALARY + 1_000),
                salaries.get(EMPLOYEE_NAME));

    }

    /**
     * Метод, принимающий Supplier и рассчитывающий площадь прямоугольника
     * по значению стороны квадрата
     *
     * @param side значение стороны
     * @return плозадь квадрата
     */
    Double lazySquare(Supplier<Double> side) {
        return Math.pow(side.get(), 2);
    }

    @Test
    public void test_12_suppliers() {

        assertEquals("Проверяем значение площади квадрата",
                Double.valueOf(100d),
                lazySquare(() -> 10d));
    }

    interface Codeable {
        static void getSalary() {
            System.out.println("Got something");
        }

        String code();

        ;
    }

    @FunctionalInterface
    interface ShortByteFunction {
        byte applyAsByte(short s);
    }

    @AllArgsConstructor
    @Getter
    class Coder implements Codeable {
        String name;
        Integer age;

        @Override
        public String code() {
            return "Typing - typing and typing";
        }
    }

}
