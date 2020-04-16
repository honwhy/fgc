package com.github.honwhy;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RandomSleepTask implements Runnable {
    private static final Random random = new Random(System.currentTimeMillis());
    private static final AtomicLong nexId = new AtomicLong(1);
    private long id;
    private long score;

    public RandomSleepTask() {
        id = nexId.getAndIncrement();
        score = Math.abs(random.nextLong());
    }

    @Override
    public void run() {
        long left = score % 1000;
        try {
            System.out.println("[" + Thread.currentThread().getName() + "]" + this.getClass().getSimpleName() + "-" + this.id + " will Sleep(" + left + "ms)...");
            // Mock create big array
            int[] bigArray = new int[10000];
            TimeUnit.MICROSECONDS.sleep(left);
        } catch (InterruptedException e) {
            //ignore
            //Thread.currentThread().interrupt();
        }
    }
}
