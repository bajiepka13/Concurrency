package org.bajiepka.concurrency.modernjavainaction.chapter16;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class FutureExamples {

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

        String product = "Макароны по-флотски";

        List<Shop> shops = Arrays.asList(
                new Shop("Metro Cash & Carry"),
                new Shop("Пятёрочка"),
                new Shop("ПУД"),
                new Shop("iStore"),
                new Shop("Эльдорадо"),
                new Shop("Яблоко"),
                new Shop("Меганом"),
                new Shop("Novus"),
                new Shop("МТС")
        );

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

    public List<String> findPricesAsync(List<Shop> shops, String product, Executor executor) {
        List<CompletableFuture<String>> prices = shops.stream()
                .map(s -> CompletableFuture.supplyAsync(() -> format("В магазине %s цена: %.2f", s.name, s.getPrice(product)),
                        executor))
                .collect(toList());

        return prices.stream().map(CompletableFuture::join).collect(Collectors.toList());
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

    /**
     * Helper method to simulate a delay (price API request) and
     * get a random double value
     *
     * @param product - the product name
     * @return random product's price
     */
    private double calculatePrice(String product) {
        delay();
        return Math.random() * product.charAt(0) + product.charAt(1);
    }

    public class Shop {

        private String name;

        public Shop(String name) {
            this.name = name;
        }

        public double getPrice(String product) {
            return calculatePrice(product);
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
