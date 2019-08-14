package org.bajiepka.concurrency.modernjavainaction.rxJava2;

import org.bajiepka.concurrency.modernjavainaction.rxJava2.model.*;
import org.junit.Test;
import org.mockito.Mockito;

public class ReactiveTests {

    @Test
    public void test_01_reactiveTest() {

        Subject<String> subject = new ConcreteSubject();
        MyObserver<String> observerA = Mockito.spy(new ConcreteMyObserverA());
        MyObserver<String> observerB = Mockito.spy(new ConcreteMyObserverB());

        subject.notifyObservers("No listeners");

        subject.registerObserver(observerA);
        subject.registerObserver(observerB);

        subject.notifyObservers("Both listeners");

    }

    @Test
    public void test_02_secondReactiveTest() {

        Subject<String> subject = new ConcreteSubject();

        subject.registerObserver(e -> System.out.println("A: " + e + " / " + Thread.currentThread().getName()));
        subject.registerObserver(e -> System.out.println("B: " + e + " / " + Thread.currentThread().getName()));
        subject.registerObserver(e -> System.out.println("C: " + e + " / " + Thread.currentThread().getName()));

        subject.notifyObservers("Message");

    }
}
