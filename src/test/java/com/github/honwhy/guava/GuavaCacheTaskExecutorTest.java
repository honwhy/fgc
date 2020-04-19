package com.github.honwhy.guava;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GuavaCacheTaskExecutorTest {

    private static AtomicInteger ai = new AtomicInteger(0);
    private static int size = 5;

    @Test
    public void test() throws InterruptedException {
        ThreadPoolExecutor executor = GuavaCacheExecutorBuilder.build(2, 4, 100);
        // jdk-1
        List<CompletableFuture<?>> fs = new ArrayList<>(size);
        List<CompletableFuture<?>> others = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MyRandomCharacterTask task = new MyRandomCharacterTask(ai.incrementAndGet(), randomHash());
            Future<?> f = executor.submit(task);
            CompletableFuture<?> cf = makeCompletedFuture(f);
            if (i % 2 == 0) {
                System.out.println("pick task-" + (i + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 2; j++) {
            MyRandomCharacterTask task = new MyRandomCharacterTask(ai.incrementAndGet(), randomHash());
            Future<?> f = executor.submit(task);
            CompletableFuture<?> cf = makeCompletedFuture(f);
            if (j % 4 == 0) {
                System.out.println("pick task-" + (j + size + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
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
    }

    private int randomHash() {
        //return Objects.hashCode(UUID.randomUUID());
        // trick
        return Math.abs(Objects.hashCode(UUID.randomUUID()) % 5);
    }

    private CompletableFuture<?> makeCompletedFuture(Future<?> future) {
        try {
            return CompletableFuture.completedFuture(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class MyRandomCharacterTask implements GuavaCacheCallable {
        private static final Random random = new Random(System.currentTimeMillis());
        private static final AtomicLong nexId = new AtomicLong(1);

        private long score;
        private int id;
        private int hash;

        private final static String[] alphabet = {
                "A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z"
        };

        public MyRandomCharacterTask(int id, int hash) {
            this.id = id;
            this.hash = hash;
            this.score = Math.abs(random.nextLong());
        }
        @Override
        public String call() {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.printf("MyRandomCharacterTask-" + this.id + " is interrupted");
                    return null;
                }
                long left = score % 2000;
                try {
                    System.out.println("[" + Thread.currentThread().getName() + "]" + this.getClass().getSimpleName() + "-" + this.id + " will Sleep(" + left + "ms)...");
                    TimeUnit.MILLISECONDS.sleep(left);
                } catch (InterruptedException e) {
                    //ignore
                    //Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
            }
            return alphabet[Math.abs(id % alphabet.length)];
        }

        @Override
        public String getKey() {
            return String.valueOf(hash);
        }
    }

}
