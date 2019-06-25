package org.bajiepka.concurrency.executor;

public class RunnableInvoker implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable invoker invoked!!!");
        System.out.println(this.toString());
    }
}
