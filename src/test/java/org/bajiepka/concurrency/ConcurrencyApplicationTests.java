package org.bajiepka.concurrency;

import org.bajiepka.concurrency.semaphore.LoginQueueUsingSemaphore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrencyApplicationTests {

    @Test
    public void test_01_semaphore_test_after_logout() {

        final int slots = 10;

        LoginQueueUsingSemaphore semaphoreQueue = new LoginQueueUsingSemaphore(slots);
        ExecutorService executor = Executors.newFixedThreadPool(slots);
        IntStream.range(0, slots).forEach(user -> {
            executor.execute(semaphoreQueue::tryLogin);
        });
        executor.shutdown();

        assertEquals(0, semaphoreQueue.availableSlots());

        semaphoreQueue.logout();
        assertTrue(semaphoreQueue.availableSlots() > 0);
        assertTrue(semaphoreQueue.tryLogin());
    }

    @Test
    public void test_02_simple_semaphore_test() {

        final int slots = 10;

        LoginQueueUsingSemaphore semaphore = new LoginQueueUsingSemaphore(slots);
        ExecutorService executor = Executors.newFixedThreadPool(slots);
        IntStream.range(0, slots).forEach(user -> {
            executor.execute(semaphore::tryLogin);
        });
        executor.shutdown();

        System.out.println(semaphore.availableSlots());
        System.out.println(semaphore.tryLogin());
        assertEquals(0, semaphore.availableSlots());
        assertFalse(semaphore.tryLogin());

    }

    @Test
    public void test_03_semaphore_and_mutex_example() {

        final int count = 5;

        ExecutorService service = Executors.newFixedThreadPool(count);
        CounterUsingMutex mutex = new CounterUsingMutex();

        IntStream.range(0, count).forEach(value -> {
            service.execute(() -> {
                try {
                    mutex.increase();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        service.shutdown();

        assertTrue(mutex.hasQueuedThreads());

    }

    @Test
    public void test_04_consumer_producer_blockingQueue() {

        int BOUND = 10;
        int N_PRODUCERS = 1;//4;
        int N_CONSUMERS = 1;//Runtime.getRuntime().availableProcessors();
        int poisonPill = Integer.MAX_VALUE;
        int poisonPillPerProducer = N_CONSUMERS / N_PRODUCERS;
        int mod = N_CONSUMERS % N_PRODUCERS;

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(BOUND);

        for (int i = 1; i < N_PRODUCERS; i++) {
            new Thread(new BlockingNumbersProducer(queue, poisonPill, poisonPillPerProducer)).start();
        }

        for (int j = 0; j < N_CONSUMERS; j++) {
            new Thread(new BlockingNumbersConsumer(queue, poisonPill)).start();
        }

        new Thread(new BlockingNumbersProducer(queue, poisonPill, poisonPillPerProducer + mod)).start();

    }

    @Test
    public void test_05_delayQueue_example() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(2);

        BlockingQueue<DelayObject> queue = new DelayQueue<>();
        int numberOfElementsToProduce = 2;
        int delayOfEachProducedMessageMilliseconds = 500;
        DelayQueueConsumer consumer = new DelayQueueConsumer(queue, numberOfElementsToProduce);
        DelayQueueProducer producer = new DelayQueueProducer(queue, numberOfElementsToProduce, delayOfEachProducedMessageMilliseconds);

        // when
        executor.submit(producer);
        executor.submit(consumer);

        // then
        executor.awaitTermination(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(consumer.numberOfConsumedElements.get(), numberOfElementsToProduce);
    }
    /*
     * TEST 01 -03 classes
     * */

    class CounterUsingMutex {

        private Semaphore mutex;
        private int count;

        CounterUsingMutex() {
            mutex = new Semaphore(1);
            count = 0;
        }

        void increase() throws InterruptedException {
            mutex.acquire();
            this.count = this.count + 1;
            Thread.sleep(1000);
            mutex.release();

        }

        int getCount() {
            return this.count;
        }

        boolean hasQueuedThreads() {
            return mutex.hasQueuedThreads();
        }
    }

    /*
     * TEST 04 classes
     * */

    class BlockingNumbersProducer implements Runnable {

        private BlockingQueue<Integer> numbersQueue;
        private int poisonPill;
        private int poisonPillPerProducer;

        public BlockingNumbersProducer(BlockingQueue<Integer> numbersQueue, int poisonPill, int poisonPillPerProducer) {
            this.numbersQueue = numbersQueue;
            this.poisonPill = poisonPill;
            this.poisonPillPerProducer = poisonPillPerProducer;
        }

        @Override
        public void run() {
            try {
                generateNumbers();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void generateNumbers() throws InterruptedException {
            for (int i = 0; i < 100; i++) {
                numbersQueue.put(ThreadLocalRandom.current().nextInt(100));
            }
            for (int j = 0; j < poisonPillPerProducer; j++) {
                numbersQueue.put(poisonPill);
            }
        }
    }

    class BlockingNumbersConsumer implements Runnable {
        private final int poisonPill;
        private BlockingQueue<Integer> queue;

        public BlockingNumbersConsumer(BlockingQueue<Integer> queue, int poisonPill) {
            this.queue = queue;
            this.poisonPill = poisonPill;
        }

        public void run() {
            try {
                while (true) {
                    Integer number = queue.take();
                    if (number.equals(poisonPill)) {
                        return;
                    }
                    System.out.println(Thread.currentThread().getName() + " result: " + number);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /*
     * TEST 05 classes
     * */

    class DelayObject implements Delayed {

        String data;
        long startTime;

        public DelayObject(String data, long startTime) {
            this.data = data;
            this.startTime = startTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = startTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Math.toIntExact(
                    startTime - ((DelayObject) o).startTime);
        }

        @Override
        public String toString() {
            return "DelayObject{" +
                    "data='" + data + '\'' +
                    ", startTime=" + startTime +
                    '}';
        }
    }

    public class DelayQueueProducer implements Runnable {

        private BlockingQueue<DelayObject> queue;
        private Integer numberOfElementsToProduce;
        private Integer delayOfEachProducedMessageMilliseconds;

        public DelayQueueProducer(BlockingQueue<DelayObject> queue, int numberOfElementsToProduce, int delayOfEachProducedMessageMilliseconds) {
            this.queue = queue;
            this.numberOfElementsToProduce = numberOfElementsToProduce;
            this.delayOfEachProducedMessageMilliseconds = delayOfEachProducedMessageMilliseconds;
        }

        // standard constructor

        @Override
        public void run() {
            for (int i = 0; i < numberOfElementsToProduce; i++) {
                DelayObject object
                        = new DelayObject(
                        UUID.randomUUID().toString(), delayOfEachProducedMessageMilliseconds);
                System.out.println("Put object: " + object);
                try {
                    queue.put(object);
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    public class DelayQueueConsumer implements Runnable {
        public AtomicInteger numberOfConsumedElements = new AtomicInteger();
        private BlockingQueue<DelayObject> queue;
        private Integer numberOfElementsToTake;

        public DelayQueueConsumer(BlockingQueue<DelayObject> queue, int numberOfElementsToProduce) {
            this.queue = queue;
            this.numberOfElementsToTake = numberOfElementsToProduce;
        }

        // standard constructors

        @Override
        public void run() {
            for (int i = 0; i < numberOfElementsToTake; i++) {
                try {
                    DelayObject object = queue.take();
                    numberOfConsumedElements.incrementAndGet();
                    System.out.println("Consumer take: " + object);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
