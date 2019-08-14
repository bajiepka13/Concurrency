package org.bajiepka.concurrency.modernjavainaction.chapter17;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReactiveApplicationTests {

    String result = "";

    public static Observable<TemperatureInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                .map(t -> getCelsiusTemperature(t))
                .collect(Collectors.toList()));
    }

    public static Observable<TemperatureInfo> getCelsiusTemperature(String town) {
        return getTemperature(town)
                .map(temp -> new TemperatureInfo(temp.getTown(), temp.getTemp() * 100));
    }

    public static Observable<TemperatureInfo> getTemperature(String town) {
        return Observable.create(emitter ->
                Observable.interval(1, TimeUnit.SECONDS)
                        .subscribe(i -> {
                            if (!emitter.isDisposed()) {
                                if (i >= 5) {
                                    emitter.onComplete();
                                } else {
                                    try {
                                        emitter.onNext(TemperatureInfo.fetch(town));
                                    } catch (Exception ex) {
                                        emitter.onError(ex);
                                    }
                                }
                            }
                        }));
    }

    @Test
    public void test_01_reactiveApp() {

        Flow.Publisher cPublisher = getCelsiumTemperature("New York");
        cPublisher.subscribe(new TemperatureSubscriber());

        Flow.Publisher fPublisher = getFahrenheitTemperature("Lugansk");
        fPublisher.subscribe(new TemperatureSubscriber());

    }

    @Test
    public void test_02_rxJava() {

        Observable<String> strings = Observable.just("first", "second");

        Observable<Long> onePerSec = Observable.interval(1, TimeUnit.SECONDS);
        onePerSec.blockingSubscribe(
                i -> System.out.println(TemperatureInfo.fetch("Luhansk"))
        );

    }

//region  ~ 01 Flow API ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    public void test_03_rxJavaHarder() {

        Observable<TemperatureInfo> observable = getCelsiusTemperature("Luhansk");
        observable.blockingSubscribe(new TemperatureObserver());
    }

    @Test
    public void test_04_rxJavaMultipleObservrs() {

        Observable<TemperatureInfo> observable = getCelsiusTemperatures("Luhansk", "Donetsk", "Kerch", "Sevastopol");
        observable.blockingSubscribe(new TemperatureObserver());

    }

    @Test
    public void test_05_reactiveApp2() {

        Observable<String> observer = Observable.just("Hello world!");
        observer.subscribe(s -> result = s);

        System.out.println(result);
    }

    private Flow.Publisher<TemperatureInfo> getCelsiumTemperature(String town) {
        return subscriber -> subscriber.onSubscribe(new TemperatureSubscription(subscriber, town));
    }

    private Flow.Publisher<TemperatureInfo> getFahrenheitTemperature(String town) {
        return subscriber -> {
            TemperatureProcessor processor = new TemperatureProcessor();
            processor.subscribe(subscriber);
            processor.onSubscribe(new TemperatureSubscription(processor, town));
        };
    }

    public static class TemperatureInfo {
        private static final Random random = new Random();
        private final String town;
        private final int temp;

        public TemperatureInfo(String town, int temp) {
            this.town = town;
            this.temp = temp;
        }

        public static TemperatureInfo fetch(String town) {
            if (random.nextInt(10) == 0) throw new RuntimeException("Error!");
            return new TemperatureInfo(town, random.nextInt(100));
        }

        @Override
        public String toString() {
            return town + ":" + temp;
        }

        public String getTown() {
            return town;
        }

        public int getTemp() {
            return temp;
        }
    }

//endregion  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//region  ~ 02 / 03 rxJava framework ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public class TemperatureSubscription implements Flow.Subscription {

        private final Flow.Subscriber<? super TemperatureInfo> subscriber;
        private final String town;

        public TemperatureSubscription(Flow.Subscriber<? super TemperatureInfo> subscriber, String town) {
            this.subscriber = subscriber;
            this.town = town;
        }

        @Override
        public void request(long n) {
            for (long i = 0L; i < n; i++) {
                try {
                    subscriber.onNext(TemperatureInfo.fetch(town));
                } catch (Exception e) {
                    subscriber.onError(e);
                    break;
                }
            }
        }

        @Override
        public void cancel() {
            subscriber.onComplete();
        }
    }

    public class TemperatureSubscriber implements Flow.Subscriber<TemperatureInfo> {

        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(TemperatureInfo item) {
            System.out.println(item);
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            System.err.println(throwable);
        }

        @Override
        public void onComplete() {
            System.out.println("Done!");
        }
    }

    public class TemperatureProcessor implements Flow.Processor<TemperatureInfo, TemperatureInfo> {

        private Flow.Subscriber<? super TemperatureInfo> subscriber;

        @Override
        public void subscribe(Flow.Subscriber<? super TemperatureInfo> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(TemperatureInfo item) {
            subscriber.onNext(new TemperatureInfo(item.getTown(), item.getTemp() * 100));
        }

        @Override
        public void onError(Throwable throwable) {
            subscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            subscriber.onComplete();
        }
    }

    public class TemperatureObserver implements Observer<TemperatureInfo> {

        @Override
        public void onSubscribe(Disposable disposable) {
            System.out.println("Done");
        }

        @Override
        public void onNext(TemperatureInfo temperatureInfo) {
            System.out.println(temperatureInfo);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Error : " + throwable.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("Finished.");
        }
    }

//endregion  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
