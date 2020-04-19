package com.github.honwhy.guava;

import java.util.concurrent.*;

public class GuavaCacheExecutorBuilder {

    private static int keepAliveSeconds = 60;

    public static ThreadPoolExecutor build(Integer corePoolSize, Integer maxPoolSize, Integer queueSize) {
        BlockingQueue<Runnable> blockingQueue = createQueue(queueSize);
        GuavaCacheTaskExecutor executor = new GuavaCacheTaskExecutor(
                corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS,
                blockingQueue);
        return executor;
    }

    private static BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<>(queueCapacity);
        }
        else {
            return new SynchronousQueue<>();
        }
    }
}
