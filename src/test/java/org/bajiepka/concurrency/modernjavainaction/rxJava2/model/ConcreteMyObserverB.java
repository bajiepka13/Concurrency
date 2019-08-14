package org.bajiepka.concurrency.modernjavainaction.rxJava2.model;

public class ConcreteMyObserverB implements MyObserver<String> {

    @Override
    public void observe(String event) {
        System.out.println("Наблюдатель Б. Событие: " + event);
    }
}
