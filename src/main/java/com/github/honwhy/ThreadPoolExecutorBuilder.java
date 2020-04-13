package com.github.honwhy;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolExecutorBuilder {

    private static int keepAliveSeconds = 60;

    public static ThreadPoolExecutor build(Integer corePoolSize, Integer maxPoolSize, Integer queueSize) {
        BlockingQueue<Runnable> blockingQueue = createQueue(queueSize);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
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

//    private static final class CustomThreadFactory implements ThreadFactory {
//        private final ThreadGroup group;
//        private final AtomicInteger index = new AtomicInteger(1);
//
//        private CustomThreadFactory() {
//            SecurityManager sm = System.getSecurityManager();
//            group = (sm != null) ? sm.getThreadGroup()
//                    : Thread.currentThread().getThreadGroup();
//        }
//
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread t = new Thread(group, r, "ThreadPoolExecutor-"
//                    + index.getAndIncrement());
//            t.setDaemon(true);
//            if (t.getPriority() != Thread.NORM_PRIORITY) {
//                t.setPriority(Thread.NORM_PRIORITY);
//            }
//            return t;
//        }
//    }
}
