package com.github.honwhy.http;

import com.github.honwhy.ThreadPoolExecutorBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyHttpClientTest {
    private final static AtomicInteger ai = new AtomicInteger(0);
    private final int size = 5;

    private final static String[] alphabet = {
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    private final static Random random = new Random(System.currentTimeMillis());
    private String randomChar() {
        return alphabet[Math.abs(random.nextInt() % alphabet.length)];
    }

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 100);
        // jdk-4
        List<CompletableFuture<?>> fs = new ArrayList<>(size);
        List<CompletableFuture<?>> others = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HttpRequestTask task = new HttpRequestTask(ai.incrementAndGet(), randomChar());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                return task.call();
            }, executor);
            if (i % 2 == 0) {
                System.out.println("pick task-" + (i + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 2; j++) {
            HttpRequestTask task = new HttpRequestTask(ai.incrementAndGet(), randomChar());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                return task.call();
            }, executor);
            if (j % 4 == 0) {
                System.out.println("pick task-" + (j + size + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 3; j++) {
            HttpRequestTask task = new HttpRequestTask(ai.incrementAndGet(), randomChar());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                return task.call();
            }, executor);
            others.add(cf);
        }
        CompletableFuture<?>[] fa = new CompletableFuture[0];
        try {
            CompletableFuture.allOf(fs.toArray(fa)).get(2500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            if (e instanceof TimeoutException) {
                fs.forEach(f -> f.cancel(true));
            }
        }
        CompletableFuture.allOf(others.toArray(fa)).join();
        executor.shutdown();
        System.out.println("test cost time:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void test2() {
        long start = System.currentTimeMillis();
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 100);
        // jdk-4
        List<CompletableFuture<?>> fs = new ArrayList<>(size);
        List<CompletableFuture<?>> others = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            AsyncHttpRequestTask task = new AsyncHttpRequestTask(ai.incrementAndGet(), randomChar());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                return task.call();
            }, executor);
            if (i % 2 == 0) {
                System.out.println("pick task-" + (i + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 2; j++) {
            AsyncHttpRequestTask task = new AsyncHttpRequestTask(ai.incrementAndGet(), randomChar());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                return task.call();
            }, executor);
            if (j % 4 == 0) {
                System.out.println("pick task-" + (j + size + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 3; j++) {
            AsyncHttpRequestTask task = new AsyncHttpRequestTask(ai.incrementAndGet(), randomChar());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                return task.call();
            }, executor);
            others.add(cf);
        }
        CompletableFuture<?>[] fa = new CompletableFuture[0];
        try {
            CompletableFuture.allOf(fs.toArray(fa)).get(2500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            if (e instanceof TimeoutException) {
                fs.forEach(f -> f.cancel(true));
            }
        }
        CompletableFuture.allOf(others.toArray(fa)).join();
        AsyncHttpRequestTask.closeClient();
        executor.shutdown();
        System.out.println("test2 cost time:" + (System.currentTimeMillis() - start));
    }
}
