package org.bajiepka.concurrency.modernjavainaction.chapter16;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class FutureExamples {

    private final List<Shop> shops = new ArrayList<>();
    private final String product = "Макароны по-флотски";

    /**
     * Simulate delay helper method
     */
    public static void delay() {

        long ONE_SECOND = 1_000;

        try {
            Thread.sleep(ONE_SECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulate Random delay helper method
     */
    public static void randomDelay() {

        Random random = new Random();
        int delay = 500 + random.nextInt(2000);
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {

        shops.addAll(Arrays.asList(
                new Shop("Metro Cash & Carry"),
                new Shop("Пятёрочка"),
                new Shop("ПУД"),
                new Shop("iStore"),
                new Shop("Эльдорадо"),
                new Shop("Яблоко"),
                new Shop("Меганом"),
                new Shop("Novus"),
                new Shop("МТС")));
    }

    @Test
    public void test_01_someFuture() {

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Double> future10 = executorService.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                System.out.println("Future thread: " + Thread.currentThread().getName());
                System.out.println("Doing some work for 3 seconds...");
                sleep(3_000);
                return 10.0;
            }
        });

        Future<Double> future21 = executorService.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                System.out.println("Future thread: " + Thread.currentThread().getName());
                System.out.println("Doing some work for 2 seconds...");
                sleep(2_000);
                return 21.0;
            }
        });

        System.out.println("Current thread: " + Thread.currentThread().getName());
        try {
            sleep(6_000);
            System.out.println("Waiting the job is done for 6 seconds...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Double result = null;
        try {
            result = future10.get(10, SECONDS) + future21.get(10, SECONDS);
            assertEquals("Результат правильный?", result, Double.valueOf(31));

        } catch (InterruptedException i) {
            i.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException t) {
            t.printStackTrace();
        }
        System.out.println(result);
    }

    @Test
    public void test_02_completeableFuture() {

        Shop bestShop = new Shop("Best shop");
        Future<Double> futurePrice = bestShop.getPriceAsync("Краковская колбаса");

        delay();

        try {
            double price = futurePrice.get();
            System.out.printf("Цена: %.2f%n", price);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_03_multipleCompletableFutures() {

        System.out.println(Runtime.getRuntime().availableProcessors());

        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), (Runnable r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        System.out.println("\nИщем цены синхронно.");
        long start = System.nanoTime();
        findPrices(shops, product).stream().forEach(System.out::println);
        long duration = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("\nВыполнено за %d милисекунд.\n", duration);

        System.out.println("\nИщем цены параллельно.");
        start = System.nanoTime();
        findPricesParallel(shops, product).stream().forEach(System.out::println);
        duration = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("\nВыполнено за %d милисекунд.\n", duration);

        System.out.println("\nИщем цены асинхронно и не параллельно.");
        start = System.nanoTime();
        findPricesAsync(shops, product, executor).stream().forEach(System.out::println);
        duration = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("\nВыполнено за %d милисекунд.\n", duration);

    }

    @Test
    public void test_04_implementing_a_discount_service() {

        findPricesWithDiscountAsync(shops, product).stream().forEach(System.out::println);

    }

    @Test
    public void test_05_implementing_a_discount_service_async() {

        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), (Runnable r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        findPricesAsyncTop(shops, product, executor).stream().forEach(System.out::println);

    }

    @Test
    public void test_06_withTimeout_exceptions() {

        Shop pyaterochka = shops.get(1);
        Shop pud = shops.get(2);

        Future<Double> futurePriceInUSD =
                CompletableFuture.supplyAsync(() -> pyaterochka.getPrice(product)).thenCombine(
                        CompletableFuture.supplyAsync(() -> pud.getPrice(product)).completeOnTimeout(1.0, 3, SECONDS),
                        (p1, p2) -> p1 + p2)
                        .orTimeout(10, SECONDS);
        try {
            System.out.println(futurePriceInUSD.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_07_asSoonAsPossible() {
        CompletableFuture[] futures = findPricesStream(product)
                .map(f -> f.thenAccept(System.out::println))
                .toArray(size -> new CompletableFuture[size]);

        CompletableFuture.allOf(futures).join();
    }

    @Test
    public void test_08_differentTimingTes() {
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream("myPhone27S")
                .map(f -> f.thenAccept(
                        s -> System.out.println(s + " (done in " +
                                ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responded in "
                + ((System.nanoTime() - start) / 1_000_000) + " msecs");
    }

//region ~~~ 04 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Stream<CompletableFuture<String>> findPricesStream(String product) {

        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), (Runnable r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPriceWithDiscount(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
    }

    public List<String> findPricesAsyncTop(List<Shop> shops, String product, Executor executor) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPriceWithDiscount(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(q -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(q), executor)))
                .collect(Collectors.toList());

        return priceFutures.stream().map(CompletableFuture::join).collect(toList());
    }

    public List<String> findPricesWithDiscountAsync(List<Shop> shops, String product) {
        return shops.stream()
                .map(shop -> shop.getPriceWithDiscount(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(toList());
    }

//endregion  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Helper method to simulate a delay (price API request) and
     * get a random double value
     *
     * @param product - the product name
     * @return random product's price
     */
    private double calculatePrice(String product) {
        randomDelay();
        return Math.random() * product.charAt(0) + product.charAt(1);
    }

    enum DiscountCode {

        NONE(0),
        SILVER(5),
        GOLD(10),
        PLATINUM(15),
        DIAMOND(20);

        private int percentage;

        DiscountCode(int percentage) {
            this.percentage = percentage;
        }
    }

    public List<String> findPricesAsync(List<Shop> shops, String product, Executor executor) {
        List<CompletableFuture<String>> prices = shops.stream()
                .map(s -> CompletableFuture.supplyAsync(() -> format("В магазине %s цена: %.2f", s.name, s.getPrice(product)),
                        executor))
                .collect(toList());

        return prices.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public static class Discount {

        public static String applyDiscount(Quote quote) {
            return quote.shopName + " price is " + Discount.apply(quote.price, quote.discountCode);
        }

        private static double apply(double price, DiscountCode code) {
            delay();
            return price * (100 - code.percentage) / 100;
        }
    }

    public List<String> findPrices(List<Shop> shops, String product) {
        return shops.stream()
                .map(s -> format("В магазине %s цена: %.2f", s.name, s.getPrice(product)))
                .collect(toList());

    }

    public List<String> findPricesParallel(List<Shop> shops, String product) {
        return shops.parallelStream()
                .map(s -> format("В магазине %s цена: %.2f", s.name, s.getPrice(product)))
                .collect(toList());

    }

    public static class Quote {

        private final String shopName;
        private final double price;
        private final DiscountCode discountCode;

        public Quote(String shopName, double price, DiscountCode discountCode) {
            this.shopName = shopName;
            this.price = price;
            this.discountCode = discountCode;
        }

        public static Quote parse(String s) {
            String[] split = s.split(":");
            String shopName = split[0];
            double price = Double.parseDouble(split[1]);
            DiscountCode discountCode = DiscountCode.valueOf(split[2]);

            return new Quote(shopName, price, discountCode);
        }
    }

    public class Shop {

        private String name;

        public Shop(String name) {
            this.name = name;
        }

        public double getPrice(String product) {
            return calculatePrice(product);
        }

        public String getPriceWithDiscount(String product) {

            Random random = new Random();
            double price = calculatePrice(product);
            DiscountCode code = DiscountCode.values()[random.nextInt(DiscountCode.values().length)];

            return String.format("%s:%s:%s", name, Double.toString(price), code);
        }

        /*  Объявляем асинхронный метод, реализующий расчёт цены в отдельном
         *   потоке с помещением результата в фьючер.*/
        public Future<Double> getPriceAsync(String product) {

            /*  Создаём объект CompletableFuture чтобы передать туда результат
             *   выполнения операции  */
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();

            /*  Создаём отдельный поток для выполнения операции, в котором
             *   по окончании его выполнения результат будет сохранён в создан-
             *   ный экземпляр CompletableFuture  */
            new Thread(() -> {
                try {
                    double price = calculatePrice(product);
                    futurePrice.complete(price);

                } catch (Exception ex) {

                    /*  На случай возникновения ошибки при выполнении операции
                     *   в потоке, чтобы этот поток не остался заблокированным
                     *   определяем проброс исключения.  */
                    futurePrice.completeExceptionally(ex);
                }
            }).start();

            /*  Стартуем выполнение потока и возвращаем ссылку на экземпляр
             *   фьючера, в который поток вернёт результат методом complete()  */
            return futurePrice;
        }

        public Future<Double> getPriceAsyncEasy(String product) {
            return CompletableFuture.supplyAsync(() -> calculatePrice(product));
        }

    }

}
