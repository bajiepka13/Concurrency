package org.bajiepka.concurrency.modernjavainaction.rxJava2.model;

public class ConcreteMyObserverA implements MyObserver<String> {

    @Override
    public void observe(String event) {
        System.out.println("Наблюдатель А. Событие: " + event);
    }
}
