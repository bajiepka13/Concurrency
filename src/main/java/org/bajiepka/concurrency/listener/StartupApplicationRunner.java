package org.bajiepka.concurrency.listener;

import lombok.extern.slf4j.Slf4j;
import org.bajiepka.concurrency.executor.CountdownLatchWorker;
import org.bajiepka.concurrency.executor.Invoker;
import org.bajiepka.concurrency.executor.RunnableInvoker;
import org.bajiepka.concurrency.executor.SemaphoreInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class StartupApplicationRunner implements ApplicationRunner {

    @Autowired
    private AnnotationConfigApplicationContext ctx;

    @Autowired
    @Qualifier("single_scheduled_executor")
    private ScheduledExecutorService singleExecutor;

    @Autowired
    @Qualifier("multi_scheduled_executor")
    private ScheduledExecutorService multiExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Приложение запущено!");

        if (false) {

            Executor executor = new Invoker();
            executor.execute(() -> {
                System.out.println("Invoker execurot implementation started!!!");
            });

            System.out.println("-------------------------------------------------");


            ExecutorService service2 = Executors.newFixedThreadPool(30);
            service2.submit(new SemaphoreInvoker());

            System.out.println("-------------------------------------------------");

            ExecutorService service = Executors.newFixedThreadPool(10);
            ExecutorService service1 = Executors.newSingleThreadExecutor();

            service.submit(new RunnableInvoker());
            service.awaitTermination(3L, TimeUnit.SECONDS);

            Future<Integer> expected = calculation(10, 12);
            Future<Integer> expected2 = calculation(11, 21);

            while (!(expected.isDone() && expected2.isDone())) {
                System.out.println(
                        String.format(
                                "expected is %s and expected2 is %s",
                                expected.isDone() ? "done" : "not done",
                                expected2.isDone() ? "done" : "not done"
                        )
                );
                Thread.sleep(3_000);
            }

            System.out.println(expected.get());
            System.out.println(expected2.get());

            System.out.println("-------------------------------------------------");

            List<String> outputScrapper = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch countDownLatch = new CountDownLatch(3);
            List<Thread> workers = Stream
                    .generate(() -> new Thread(new CountdownLatchWorker(outputScrapper, countDownLatch)))
                    .limit(10)
                    .collect(Collectors.toList());

            workers.forEach(Thread::start);
            countDownLatch.await();
            outputScrapper.add("Latch released");

            outputScrapper.forEach(scrap -> System.out.println(scrap));
        }
    }

    public Future<Integer> calculation(Integer first, Integer second) {
        return multiExecutor.submit(() -> {
            System.out.println("sleeping for 10s... ");
            Thread.sleep(10_000);
            return first * second;
        });
    }

}
