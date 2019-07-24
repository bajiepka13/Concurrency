package org.bajiepka.concurrency.modernjavainaction.chapter8;

import org.junit.Test;

import java.util.*;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CollectionApiEnhancements {

    @Test
    public void test_01_collections_java8_9_changes() {

        Set<String> nameSet = Set.of("Аня", "Наташа", "Вика");
        List<String> nameList = List.of("Аня", "Наташа", "Вика");
        Map<String, Integer> nameMap = Map.of("Аня", 27, "Наташа", 29, "Вика", 26);

        assertEquals("Проверяем общее количество элементов в созданных коллекциях",
                9,
                nameList.size() + nameSet.size() + nameMap.size());

        nameMap.forEach((key, value) -> System.out.println(String.format("key:[%s] / value:[%d]", key, value)));

        System.out.println(nameMap.getOrDefault("Катя", 99));

        System.out.println("\nПечатаем упорядоченные по ключу значения мапы:");
        nameMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(System.out::println);

    }

    @Test
    public void test_02_map_replaceMethods() {

        String testName = "Аня";

        List<String> mutableList = new ArrayList<>();
        mutableList.add(testName);
        mutableList.add("Наташа");
        mutableList.add("Вика");

        mutableList.removeIf(girl -> girl.equals("Наташа"));
        mutableList.replaceAll(girl -> girl.toUpperCase());

        assertEquals("Проверяем нашу мутабельную мапу, а именно количество её пар",
                2,
                mutableList.size());

        assertTrue("Проверяем наш мутабельный лист, а именно количество и одно из значений",
                mutableList.contains(testName.toUpperCase()));

    }

    @Test
    public void test_03_mapOfEntries() {

        Map<String, Integer> mapOfEntries = ofEntries(
                entry("Аня", 27),
                entry("Наташа", 29),
                entry("Вика", 26));

        assertEquals(3, mapOfEntries.size());

    }

    @Test
    public void test_04_mapComputeMethods() {

        Map<String, String> cache = new HashMap<>();
        cache.put("Sevastopol", null);

        cache.computeIfAbsent("Sevastopol", v -> v.toUpperCase());
        cache.computeIfAbsent("Simferopol", v -> v.toUpperCase());

        System.out.println(cache);

        cache.computeIfPresent("Simferopol", (k, v) -> v.toLowerCase());
        System.out.println(cache);

        cache.compute("Sevastopol", (k, v) -> v.substring(0, 2).toUpperCase());
        cache.compute("Simferopol", (k, v) -> v.substring(0, 2).toUpperCase());
        System.out.println(cache);

        cache.remove("Simferopol", "SI");
        System.out.println(cache);

        cache.replace("Sevastopol", "SE", "Sevastopol");
        System.out.println(cache);

    }

    @Test
    public void test_05_mapMerge() {

        Map<String, String> friends = Map.ofEntries(
                entry("Alex", "Books"),
                entry("Nick", "Films"),
                entry("Vit", "Talks"));

        Map<String, String> family = Map.ofEntries(
                entry("Alex", "Hookah"),
                entry("Nate", "Work"),
                entry("Pit", "Gym"));

        Map<String, String> all = new HashMap<>(friends);
        family.forEach((k, v) -> all.merge(k, v, (i1, i2) -> i1 + " & " + i2));

        System.out.println(all);

    }

}
