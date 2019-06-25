package org.bajiepka.concurrency.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
public class SemaphoreInvoker implements Runnable {

    @Autowired
    Semaphore semaphore;

    @Override
    public void run() {
        try {
            Thread.sleep(3_000);
            System.out.println("Сплю три секунды... ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
