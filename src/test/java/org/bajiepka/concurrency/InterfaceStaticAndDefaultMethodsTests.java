package org.bajiepka.concurrency;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;

public class InterfaceStaticAndDefaultMethodsTests {

    SapienceFactory factory;

    @Before
    public void before() {
        System.out.println("Инициализируем фабрику гомосапиенсов!");
        factory = HomoSapienceFactory::new;

        System.out.println("------------------------------------!");
    }

    @Test
    public void test_01_Default_method() {

        HomoSapienceFactory h = factory.build("Java programmer", 25);
        System.out.println(h);
        h.walk();
        Walkable.walking();
    }

    interface Human {
        default void walk() {
            System.out.println("Топ-топ");
        }
    }

    interface Walkable {
        static void walking() {
            System.out.println("Шаг за шагом");
        }
    }

    @FunctionalInterface
    interface SapienceFactory {
        HomoSapienceFactory build(String name, Integer age);
    }

    @Setter
    @AllArgsConstructor
    @ToString
    class HomoSapienceFactory implements Human, Walkable {
        String name;
        Integer age;
    }
}
