package org.bajiepka.concurrency.modernjavainaction.rxJava2.model;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcreteSubject implements Subject<String> {

    private final Set<MyObserver<String>> myObservers = new CopyOnWriteArraySet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void registerObserver(MyObserver<String> myObserver) {
        myObservers.add(myObserver);
    }

    @Override
    public void unregisterObserver(MyObserver<String> myObserver) {
        myObservers.remove(myObserver);
    }

    @Override
    public void notifyObservers(String event) {
        /*myObservers.forEach(observer ->
                executorService.submit(
                        () -> observer.observe(event)
                )
        );*/

        myObservers.forEach(obs -> obs.observe(event));

    }
}
