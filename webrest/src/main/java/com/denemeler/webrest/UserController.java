package com.denemeler.webrest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class UserController {


    private static final String template = "Hello, %s!";
    ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/users")
    public List<String> getAll() {
        sleep();
        return Arrays.asList("Ramazan", "Erkan", "Rıdvan");
    }

    /**
     *
     * If you read the Javadocs for @RequestMapping you will find that a method can return a CompletableFuture "which the application uses to produce a return value in a separate thread of its own choosing
     * https://spring.io/blog/2016/07/20/notes-on-reactive-programming-part-iii-a-simple-http-server-application
     *
     * Sonuç:
     * ForkJoinPool.commonPool-worker-3 Hello ForkJoinPool.commonPool-worker-3 World
     * yani paralel işlemin sonucu gider response olarak
     */
    @RequestMapping("/parallel")
    public CompletableFuture<String> parallel() {
        System.out.println("GİRİŞ - Thread Name : " + Thread.currentThread().getName());

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            sleep();
            System.out.println("İŞLEM - Thread Name : " + Thread.currentThread().getName());
            return Thread.currentThread().getName() + " Hello ";
        });

        CompletableFuture<String> future = completableFuture
                .thenApply(s -> s + Thread.currentThread().getName()  + " World");

        System.out.println("ÇIKIŞ - Thread Name : " + Thread.currentThread().getName());
        return future;

    }


    /**
     *
     * http://localhost:8080/parallel2
     *
     * future objesi döner sonuç dönmez
     * {
     * "cancelled": false,
     * "done": false
     * }
     *
     */
    @RequestMapping("/parallel2")
    public Future<String> parallel2() {
        System.out.println("GİRİŞ - Thread Name : " + Thread.currentThread().getName());

        Future<String> future = executor.submit(task);

        System.out.println("ÇIKIŞ - Thread Name : " + Thread.currentThread().getName());
        return future;

    }

    Callable<String> task = () -> {
        try {
            System.out.println("İŞLEM - Thread Name : " + Thread.currentThread().getName());
            TimeUnit.MILLISECONDS.sleep(10000);
            System.out.println("İŞLEM sleep sonrası - Thread Name : " + Thread.currentThread().getName());
            return "RAMAZAN Future";
        }
        catch (InterruptedException e) {
            throw new IllegalStateException("task interrupted", e);
        }
    };

    private void sleep() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}