package com.github.honwhy;

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
    public void test() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 10);
        // jdk-1
        List<CompletableFuture<?>> fs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                new MyRandomSleepTask(ai.incrementAndGet()).run();
                return null;
            }, executor);
            fs.add(cf);
        }
        CompletableFuture<?>[] fa = new CompletableFuture[0];
        try {
            CompletableFuture.allOf(fs.toArray(fa)).get(500, TimeUnit.MICROSECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        // jdk-2
        List<CompletableFuture<?>> fs2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CompletableFuture<?> cf = CompletableFuture.supplyAsync(() -> {
                new MyRandomSleepTask(ai.incrementAndGet()).run();
                return null;
            }, executor);
            fs2.add(cf);
        }
        CompletableFuture<Void> allWithFailFast = CompletableFuture.allOf(fs2.toArray(fa));
        fs2.forEach(f -> f.exceptionally(e -> {
            allWithFailFast.completeExceptionally(e);
            return null;
        }));
        //allWithFailFast.join();
        try {
            allWithFailFast.get(500, TimeUnit.MICROSECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void test3() {
        // guava -- InterruptibleTask
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.build(2, 4, 10);
        ListeningExecutorService service = MoreExecutors.listeningDecorator(executor);
        List<ListenableFuture<?>> fs3 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ListenableFuture<?> cf = service.submit(new MyRandomSleepTask(ai.incrementAndGet()));
            //ListenableFutureTask<?> cf = ListenableFutureTask.create(new MyRandomSleepTask(ai.incrementAndGet()), null);
            fs3.add(cf);
        }
        ListenableFuture<?> allFutures = Futures.allAsList(fs3);
        try {
            allFutures.get(200, TimeUnit.MICROSECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }
    private class MyRandomSleepTask extends RandomSleepTask {
        private int id;

        public MyRandomSleepTask(int id) {
            this.id = id;
        }
        @Override
        public void run() {
            try {
                super.run();
            } catch (Exception e) {
            }
        }
    }
}
