package com.github.honwhy.http;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class HttpRequestTask implements Callable<String> {
    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * guava cache
     */
    private static final Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .expireAfterAccess(3, TimeUnit.SECONDS)
            .weakKeys()
            .weakValues()
            .build();

    private int id;
    private String query;
    private long score;

    public HttpRequestTask(int id, String query) {
        this.id = id;
        this.query = query;
        this.score = Math.abs(random.nextLong());
    }
    @Override
    public String call() {
        try {
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("[" + Thread.currentThread().getName() + "]" + this.getClass().getSimpleName() + "-" + this.id + " is interrupted");
                return null;
            }
            long left = 500;//score % 600;
            try {
                System.out.println("[" + Thread.currentThread().getName() + "]" + this.getClass().getSimpleName() + "-" + this.id + " will Sleep(" + left + "ms)...");
                TimeUnit.MILLISECONDS.sleep(left);
            } catch (InterruptedException e) {
                //ignore
                //Thread.currentThread().interrupt();
            }
            String result = cache.getIfPresent(query);
            if (result != null) {
                System.err.println("get result from cache: key=" + query);
                return result;
            }
            System.out.println("[" + Thread.currentThread().getName() + "]" + this.getClass().getSimpleName() + "-" + this.id + " query=" + query);
            result = doRequest();
            if (result != null) {
                cache.put(query, result);
            }
            return result;
        } catch (Exception e) {
        }
        return null;
    }

    protected String doRequest() throws Exception {
        String requestUrl = String.format("https://cn.bing.com/search?q=%s&qs=n&form=QBLH&sp=-1&pq=a&sc=9-1&sk=&cvid=493B94949A92491FB780F4BCCD085E37", query);
        return MyHttpClient.get(requestUrl);
    }

    public String getQuery() {
        return this.query;
    }
}
