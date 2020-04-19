package com.github.honwhy.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.*;

/**
 * 使用Guava缓存减少实际的执行
 */
public class GuavaCacheTaskExecutor extends ThreadPoolExecutor {

    private final Cache<String, String> cache;

    public GuavaCacheTaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        // Construct cache
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .weakKeys()
                .weakValues()
                .build();
        this.cache = cache;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task instanceof GuavaCacheCallable) {
            GuavaCacheCallable gtask = (GuavaCacheCallable) task;
            String key = gtask.getKey();
            String value = cache.getIfPresent(key);
            if (value != null) {
                System.out.println("hit cache: " + key);
                return (Future<T>) CompletableFuture.completedFuture(value);
            }
        }
        Future<T> f = super.submit(task);
        //if (f.isDone()) {
            cache.put("3", "A");
        //}
        return f;
    }

}
