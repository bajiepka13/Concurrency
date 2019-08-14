package org.bajiepka.concurrency.modernjavainaction.rxJava2.model;

public interface MyObserver<T> {
    void observe(T event);
}
