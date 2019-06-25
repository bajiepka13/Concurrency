package org.bajiepka.concurrency.executor;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CountdownLatchWorker implements Runnable {

    private List<String> outputScraper;
    private CountDownLatch countDownLatch;

    public CountdownLatchWorker(List<String> outputScraper, CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        doSomeWork();
        outputScraper.add("counted down!");
        countDownLatch.countDown();
    }

    private void doSomeWork() {

        try {
            Thread.sleep(1_000);
            System.out.println("some job is done...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
