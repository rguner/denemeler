package com.baeldung.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofSeconds;

public class ReactorDeneme {

    public static void main(String[] args) {
        ReactorDeneme reactorDeneme = new ReactorDeneme();
        //reactorDeneme.subscribeWithSeperateThread();
        //reactorDeneme.fluxSubscribeWithLog();
        //reactorDeneme.fluxSubscribe();
        //reactorDeneme.fluxSubscribe2();
        // reactorDeneme.fluxSubscribeBackPressure();
        // reactorDeneme.fluxMap();
        // reactorDeneme.flatMap();
        //reactorDeneme.fluxZip();
        // reactorDeneme.connectableFlux();f
        // reactorDeneme.connectableFluxSample();
        //reactorDeneme.fluxSubscribeParallel();
        // reactorDeneme.fluxSubscribe3();
        // reactorDeneme.withDelay();
        // reactorDeneme.delayAndStream();
        // reactorDeneme.firstEmitting();
        // reactorDeneme.subsOnPubOn();
        reactorDeneme.errorOnReturn();
        //reactorDeneme.errorOnResume();
        //reactorDeneme.doOnError();
        //reactorDeneme.onErrorMap();
        //reactorDeneme.collectData();
    }

    private void collectData() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 0, 5)
                .log()
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void onErrorMap() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 0, 5)
                .log()
                .map(i -> 100 / i)
                .onErrorMap(error -> {
                    System.out.println("Hata");
                    throw new RuntimeException("Bir hata oluştu");
                })
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void doOnError() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 0, 5)
                .log()
                .map(i -> 100 / i)
                .doOnError(error -> {
                    System.out.println("Hata" + error.getMessage());
                })
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void errorOnReturn() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 0, 5, 20, 25)
                .log()
                .map(i -> 100 / i)
                .onErrorReturn(Integer.MAX_VALUE)
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void errorOnResume() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 0, 5)
                .log()
                .map(i -> 100 / i)
                .onErrorResume(error -> {
                    System.out.println("Hata");
                    return Flux.range(100, 3);
                })
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void subsOnPubOn() {
        /*
        Notice that the consumer callbacks (logging "Consumed: ..") are on the publisher thread pub-1-1.
        If you take out the subscribeOn() call, you might see all of the 2nd chunk of data processed on the pub-1-1 thread as well.
         This, again, is Reactor being frugal with threads--if there’s no explicit request to switch threads it stays on the same one for the next call, whatever that is.

        Note
        We changed the code in this sample from subscribe(null, 2) to adding a prefetch=2 to the publishOn(). In this case the fetch size hint in subscribe() would have been ignored.
         */
        Flux.just("red", "white", "blue", "white", "green", "gray", "yellow", "black")
                .log()
                .map(String::toUpperCase)
                .subscribeOn(Schedulers.newParallel("sub"))
                .publishOn(Schedulers.newParallel("pub"), 2)
                .subscribe(value -> {
                    System.out.println(Thread.currentThread().getName() + " Consumed: " + value);
                });
    }

    private void firstEmitting() {

        Flux<String> b = Flux.just("let's get", "the party", "started")
                .delaySubscription(Duration.ofMillis(1000));

        Flux.first(b)
                .toIterable()
                .forEach(System.out::println);

    }

    private void delayAndStream() {

        Flux<String> helloPauseWorld =
                Mono.just("Hello")
                        .concatWith(Mono.just("world")
                                .delaySubscription(Duration.ofMillis(1500)));

        // Specifically, toIterable and toStream will both produce a blocking instance. So let's use toStream for our example:
        helloPauseWorld.toStream()
                .forEach(System.out::println);
    }

    private void withDelay() {

        Flux<String> helloPauseWorld =
                Mono.just("Hello")
                        .concatWith(Mono.just("world")
                                .delaySubscription(Duration.ofMillis(2500)));

        // helloPauseWorld.subscribe(System.out::println);
        helloPauseWorld.subscribe(s -> {
            System.out.println(s + " " + Thread.currentThread().getName());
        });

        // 500 msn delayın neticesini görelim.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fluxSubscribe3() {
        List<String> words = Arrays.asList(
                "the",
                "quick",
                "brown",
                "fox",
                "jumped",
                "over",
                "the",
                "lazy",
                "dog"
        );

        Flux<String> fewWords = Flux.just("Hello", "World");
        Flux<String> manyWords = Flux.fromIterable(words);

        fewWords.subscribe(System.out::println);
        System.out.println();
        manyWords.subscribe(System.out::println);

        Flux<String> manyLetters = Flux
                .fromIterable(words)
                .flatMap(word -> Flux.fromArray(word.split("")))
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string));

        manyLetters.subscribe(System.out::println);

        Mono<String> missing = Mono.just("s");
        Flux<String> allLetters = Flux
                .fromIterable(words)
                .flatMap(word -> Flux.fromArray(word.split("")))
                .concatWith(missing)
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string));

        allLetters.subscribe(System.out::println);


    }

    private void fluxSubscribeParallel() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .log()
                .map(i -> i * 2)
                .subscribeOn(Schedulers.newParallel("thread-parallel"))
                .subscribe(elements::add);

        System.out.println("Sleep oncesi " + elements);

        // main thread subscribe islemini beklemedigi için, sonucu görmek için sleep koydum.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Sleep sonrasi " + elements);
    }

    private void connectableFluxSample() {
        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
                    while (true) {
                        fluxSink.next(System.currentTimeMillis());
                    }
                })
                .sample(ofSeconds(2))
                .publish();
        //By calling publish() we are given a ConnectableFlux. This means that calling subscribe() won't cause it to start emitting, allowing us to add multiple subscriptions:

        //publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        //If we try running this code, nothing will happen. It's not until we call connect(), that the Flux will start emitting:

        publish.connect();
    }

    private void connectableFlux() {
        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        fluxSink.next(System.currentTimeMillis());
                    }
                })
                .publish();
        //By calling publish() we are given a ConnectableFlux. This means that calling subscribe() won't cause it to start emitting, allowing us to add multiple subscriptions:

        //publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        //If we try running this code, nothing will happen. It's not until we call connect(), that the Flux will start emitting:

        publish.connect();
    }


    private void fluxZip() {
        List<String> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .log()
                .map(i -> i * 2)
                .zipWith(Flux.range(0, Integer.MAX_VALUE).log(),
                        (one, two) -> String.format("First Flux: %d, Second Flux: %d", one, two))
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void flatMap() {
        Flux.just("red", "white", "blue")
                .log()
                .flatMap(value ->
                                Mono.just(value.toUpperCase())
                        //  .subscribeOn(Schedulers.parallel()),
                        // 2)
                )
                .subscribe(value -> {
                    System.out.println("Consumed: " + value);
                });
    }

    private void fluxMap() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .log()
                .map(i -> i * 2)
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void fluxSubscribeBackPressure() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    int onNextAmount;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                        onNextAmount++;
                        if (onNextAmount % 2 == 0) {
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        System.out.println(elements);
    }

    private void fluxSubscribe2() {

        List<Integer> elements = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;

                    @Override
                    public void onSubscribe(Subscription s) {

                        this.s = s;
                        s.request(Long.MAX_VALUE);
                        //s.request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                        //if ( integer < 3)
                        //    s.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        System.out.println(elements);
    }

    private void fluxSubscribe() {

        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(elements::add);

        System.out.println(elements);
    }

    private void fluxSubscribeWithLog() {

        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .filter(n -> n > 0)
                .map(n -> n * 2)
                .filter(n ->
                {
                    System.out.println("Filter Thread --> " + Thread.currentThread().getName() + ", value --> " + n);
                    if (n < 17) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .log()
                // .publishOn(Schedulers.newParallel("thread-publisher"), 2) ikişerli grup halinde alır.
                .subscribeOn(Schedulers.newParallel("thread-subscribe"))
                .subscribe(i -> {
                    System.out.println("Subscribe Thread --> " + Thread.currentThread().getName() + ", value --> " + i);
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    elements.add(i);
                });

        System.out.println(elements);
    }

    private void subscribeWithSeperateThread() {
        Flux<Integer> flux = Flux.range(0, 5)
                .map(i -> {
                    System.out.println("Mapping for " + i + " is done by thread " + Thread.currentThread().getName());
                    return i;
                });

        Runnable r = () -> flux.subscribe(s -> {
            System.out.println("Received " + s + " via " + Thread.currentThread().getName());
        });

        Thread t1 = new Thread(r, "t1");
        Thread t2 = new Thread(r, "t2");

        //lets start the threads. (this is when we are subscribing to the flux)
        System.out.println("Program thread :: " + Thread.currentThread().getName());
        t1.start();
        t2.start();
    }
}
