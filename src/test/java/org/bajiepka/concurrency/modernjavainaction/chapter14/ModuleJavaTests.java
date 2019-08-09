package org.bajiepka.concurrency.modernjavainaction.chapter14;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntConsumer;

public class ModuleJavaTests {

    @Test
    public void test_01_reactiveStyle() {

        int x = 1337;
        Result result = new Result();

        a(x, (int y) -> result.left = y);
        a(x, (int y) -> result.right = y);

        System.out.println(result.left + result.right);

    }

    //region Reactive

    void a(int value, IntConsumer consumer) {
        consumer.accept(value);
    }

    void b(int value, IntConsumer consumer) {
        consumer.accept(value);
    }

    //endregion

    @Test
    public void test_02_futureStyle() {

        int x = 1337;
        Result result = new Result();

        System.out.println(result.left + result.right);

    }

    @Test
    public void test_03_threadStyle() {

        int x = 1337;
        Result result = new Result();

        Thread t1 = new Thread(() -> {
            result.left = f(x);
        });
        Thread t2 = new Thread(() -> {
            result.right = g(x);
        });

        t1.start();
        t2.start();

        try {

            t1.join();
            t2.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(result.sum());

    }

    //region Thread

    int f(int value) {
        System.out.println("Присваивание левой части результата обработано: " + Thread.currentThread().getName());
        return value;
    }

    int g(int value) {
        System.out.println("Присваивание правой части результата обработано: " + Thread.currentThread().getName());
        return value;
    }

    //endregion

    @Test
    public void test_04_executorServiceExample() {

        int x = 1337;
        Result result = new Result();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> y = executorService.submit(() -> f(x));
        Future<Integer> z = executorService.submit(() -> g(x));

        try {

            System.out.println(y.get() + z.get());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    @Test
    public void test_05_combiningCompleteableFutures() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();

        CompletableFuture<Integer> c = a.thenCombine(b, (v1, v2) -> v1 + v2);

        executorService.submit(() -> a.complete(f(x)));
        executorService.submit(() -> b.complete(g(x)));

        System.out.println(c.get());
        executorService.shutdown();

    }

    @Test
    public void test_06_flowsExample() {

        SimpleCell c1 = new SimpleCell("c1");
        SimpleCell c2 = new SimpleCell("c2");

        ArithmeticCell c3 = new ArithmeticCell("c3");

        c1.subscribe(c3);
        c2.subscribe(c3);

        c1.onNext(10);
        c2.onNext(20);

    }

    interface Publisher<T> {
        void subscribe(Flow.Subscriber<? super T> subscriber);
    }

    public class Result {
        private int left;
        private int right;

        public int sum() {
            return left + right;
        }
    }

    private class ArithmeticCell extends SimpleCell {

        private int left;
        private int right;

        public ArithmeticCell(String name) {
            super(name);
        }

        @Override
        public void onNext(Integer item) {
            this.value += item;
            System.out.println(name + ":" + value);
        }
    }

    private class SimpleCell implements Publisher<Integer>, Flow.Subscriber<Integer> {

        protected int value = 0;
        protected String name;
        private List<Flow.Subscriber> subscribers = new ArrayList<>();

        public SimpleCell(String name) {
            this.name = name;
        }

        @Override
        public void onNext(Integer item) {
            this.value = item;
            System.out.println(name + ":" + value);
            notifyAllSubscribers();
        }

        private void notifyAllSubscribers() {
            subscribers.forEach(subscriber -> subscriber.onNext(this.value));
        }

        @Override
        public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
            subscribers.add(subscriber);
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {

        }
    }


}