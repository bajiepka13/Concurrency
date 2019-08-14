package org.bajiepka.concurrency.modernjavainaction.rxJava2.model;

public interface Subject<T> {

    void registerObserver(MyObserver<T> observer);

    void unregisterObserver(MyObserver<T> observer);

    void notifyObservers(T event);

}
