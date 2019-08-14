package org.bajiepka.reactivewebappwithrxjava;

import io.reactivex.Observable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ReactiveWebAppWithRxjavaApplication implements ApplicationRunner {

    Boolean stopIt = false;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveWebAppWithRxjavaApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {


        //region RxJava from Subscriber, Observable

        Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {

            @Override
            public void onSubscribe(Flow.Subscription subscription) {

            }

            @Override
            public void onNext(String item) {
                System.out.println(item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("Done");
            }
        };

        Observable<String> observable = Observable.create(
                sub -> {
                    sub.onNext("Hello reactive world!");
                    sub.onComplete();
                }
        );

        Observable.create(
                sub -> {
                    sub.onNext("Hello reactive World!");
                    sub.onComplete();
                }
        ).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Done"));

        //endregion

        //region RxJava from Callable, Future

        Observable<String> fromChars = Observable.just("a", "b", "c", "d");
        Observable<Integer> fromNumbers = Observable.fromArray(new Integer[]{1, 2, 3, 4, 5});
        Observable<String> fromCollection = Observable.fromIterable(Arrays.asList("Hello"));
        Observable<String> fromCallable = Observable.fromCallable(() -> "World");
        Observable<Integer> fromFuture = Observable.fromFuture(Executors.newCachedThreadPool().submit(() -> 100));

        Observable.concat(
                fromCollection,
                fromCallable,
                Observable.just("!"))
                .forEach(System.out::println);

        //endregion

        //region
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (false) {
            Observable<Long> intervalObservable = Observable.interval(1, TimeUnit.SECONDS);
            intervalObservable.subscribe(System.out::println);
            Thread.sleep(5_000);
        }
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //endregion

        Observable.zip(
                Observable.just("Иван", "Пётр", "Виктор"),
                Observable.just("Иванов", "Петров", "Викторов"),
                (a, b) -> a + " " + b
        ).forEach(System.out::println);

        Observable.just("144", "9", "25", "4")
                .map(Double::parseDouble)
                .map(Math::sqrt)
                .filter(d -> d >= 3)
                .forEach(System.out::println);
    }
}
