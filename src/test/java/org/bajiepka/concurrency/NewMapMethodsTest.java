package org.bajiepka.concurrency;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NewMapMethodsTest {

    private Map<String, String> map;

    @Before
    public void before() {
        map = new HashMap<>();
    }

    @Test
    public void test_01_methods() {

        map.putIfAbsent("sampleKey", "sample value");
        assertTrue(map.containsKey("sampleKey"));
        System.out.println(map.get("sampleKey"));

        map.computeIfPresent("sampleKey", (key, value) -> value = String.format("%s with addition", value));
        assertEquals("Проверяем текущее значение ключа мапы.", "sample value with addition", map.get("sampleKey"));
        System.out.println(map.get("sampleKey"));

        map.remove("sampleKey", "sample value with addition");
        assertTrue("Проверяем, что из мапы удалился наш ключ со значением", map.isEmpty());

        String value = map.getOrDefault("NonExistentKey", "I do not exist!");
        assertEquals("В мапе не оказалось пары с ключом 'NonExistentKey'", "I do not exist!", value);

        map.putIfAbsent("NonExistentKey", "Value of nonexistent pair");
        map.merge("NonExistentKey",
                "and something more...",
                (was, now) -> was + " " + now);
        assertEquals("Проверяем, правильно ли произошёл merge",
                "Value of nonexistent pair and something more...",
                map.get("NonExistentKey"));

    }
}
