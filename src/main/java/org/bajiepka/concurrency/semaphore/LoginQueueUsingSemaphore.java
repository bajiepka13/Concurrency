package org.bajiepka.concurrency.semaphore;

import java.util.concurrent.Semaphore;

public class LoginQueueUsingSemaphore {

    private Semaphore semaphore;

    public LoginQueueUsingSemaphore(int permits) {
        this.semaphore = new Semaphore(permits);
    }

    public void logout() {
        semaphore.release();
    }

    public boolean tryLogin() {
        System.out.println("Trying to acquire a permit..." + Thread.currentThread().getId());
        return semaphore.tryAcquire();
    }

    public int availableSlots() {
        return semaphore.availablePermits();
    }

}
