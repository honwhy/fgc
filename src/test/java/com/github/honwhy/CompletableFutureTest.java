package com.github.honwhy;

import com.github.honwhy.RandomSleepTask;
import com.github.honwhy.ThreadPoolExecutorBuilder;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * http://arganzheng.life/writing-asynchronous-code-with-completablefuture.html
 * 默认情况下，allOf 会等待所有的任务都完成，即使其中有一个失败了，也不会影响其他任务继续执行。
 * 但是大部分情况下，一个任务的失败，往往意味着整个任务的失败，继续执行完剩余的任务意义并不大。
 * 在 谷歌的 Guava 的 allAsList 如果其中某个任务失败整个任务就会取消执行:
 */
public class CompletableFutureTest {
    private static AtomicInteger ai = new AtomicInteger(0);
    private static int size = 5;
    @Test
    public void test() throws InterruptedException {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 100);
        // jdk-1
        List<CompletableFuture<?>> fs = new ArrayList<>(size);
        List<CompletableFuture<?>> others = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MyRandomSleepTask task = new MyRandomSleepTask(ai.incrementAndGet());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                task.run();
                return null;
            }, executor);
            if (i % 2 == 0) {
                System.out.println("pick task-" + (i + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 2; j++) {
            MyRandomSleepTask task = new MyRandomSleepTask(ai.incrementAndGet());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                task.run();
                return null;
            }, executor);
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
        }
        CompletableFuture.allOf(others.toArray(fa)).join();
    }
    @Test
    public void test2() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 10);
        // jdk-2
        List<CompletableFuture<?>> fs2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                new MyRandomSleepTask(ai.incrementAndGet()).run();
                return null;
            }, executor);
            fs2.add(cf);
        }
        CompletableFuture<?>[] fa = new CompletableFuture[0];
        CompletableFuture<Void> allWithFailFast = CompletableFuture.allOf(fs2.toArray(fa));
        fs2.forEach(f -> f.exceptionally(e -> {
            allWithFailFast.completeExceptionally(e);
            return null;
        }));
        //allWithFailFast.join();
        try {
            allWithFailFast.get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws InterruptedException {
        // guava -- InterruptibleTask
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 100);
        ListeningExecutorService service = MoreExecutors.listeningDecorator(executor);
        List<ListenableFuture<?>> fs3 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ListenableFuture<?> cf = service.submit(new MyRandomSleepTask(ai.incrementAndGet()));
            //ListenableFutureTask<?> cf = ListenableFutureTask.create(new MyRandomSleepTask(ai.incrementAndGet()), null);
            if (i % 2 == 0) {
                System.out.println("pick task-" + (i + 1));
                fs3.add(cf);
            }
        }
        for (int j = 0; j < size * 2; j++) {
            ListenableFuture<?> cf = service.submit(new MyRandomSleepTask(ai.incrementAndGet()));
            //ListenableFutureTask<?> cf = ListenableFutureTask.create(new MyRandomSleepTask(ai.incrementAndGet()), null);
            if (j % 4 == 0) {
                System.out.println("pick task-" + (j + size + 1));
                fs3.add(cf);
            }
        }
        ListenableFuture<?> allFutures = Futures.allAsList(fs3);
        try {
            allFutures.get(2500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void test4() throws InterruptedException {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 100);
        // jdk-4
        List<CompletableFuture<?>> fs = new ArrayList<>(size);
        List<CompletableFuture<?>> others = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MyRandomSleepTask task = new MyRandomSleepTask(ai.incrementAndGet());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                task.run();
                return null;
            }, executor);
            if (i % 2 == 0) {
                System.out.println("pick task-" + (i + 1));
                fs.add(cf);
            } else {
                others.add(cf);
            }
        }
        for (int j = 0; j < size * 2; j++) {
            MyRandomSleepTask task = new MyRandomSleepTask(ai.incrementAndGet());
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                task.run();
                return null;
            }, executor);
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
        //CompletableFuture<?>[] fa2 = new CompletableFuture[0];
        CompletableFuture.allOf(others.toArray(fa)).join();
        //executor.awaitTermination(10, TimeUnit.SECONDS);
    }
    private class MyRandomSleepTask extends RandomSleepTask {
        private int id;

        public MyRandomSleepTask(int id) {
            this.id = id;
        }
        @Override
        public void run() {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.printf("MyRandomSleepTask-" + this.id + " is interrupted");
                    return;
                }
                super.run();
            } catch (Exception e) {
            }
        }
    }
}