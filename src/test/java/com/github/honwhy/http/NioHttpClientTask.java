package com.github.honwhy.http;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class NioHttpClientTask {

    private static final Random random = new Random(System.currentTimeMillis());
    private int id;
    private String query;
    private long score;
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

    public NioHttpClientTask(int id, String query) {
        this.id = id;
        this.query = query;
        this.score = Math.abs(random.nextLong());
    }

    private static AsyncHttpClient asyncHttpClient = asyncHttpClient(Dsl.config().setConnectTimeout(2000).setReadTimeout(2000));

    public CompletableFuture<String> doRequest() {
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
            return CompletableFuture.completedFuture(result);
        }
        System.out.println("[" + Thread.currentThread().getName() + "]" + this.getClass().getSimpleName() + "-" + this.id + " query=" + query);
        String requestUrl = String.format("https://cn.bing.com/search?q=%s&qs=n&form=QBLH&sp=-1&pq=a&sc=9-1&sk=&cvid=493B94949A92491FB780F4BCCD085E37", query);
        Future<Response> whenResponse = asyncHttpClient.prepareGet(requestUrl).execute();
        try {
            Response response = whenResponse.get();
            if (response != null) {
                result = response.getResponseBody(StandardCharsets.UTF_8);
                cache.put(query, result);
                return CompletableFuture.completedFuture(result);
            }
        } catch (InterruptedException e) {
            //
        } catch (ExecutionException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static void closeClient() {
        try {
            asyncHttpClient.close();
        } catch (IOException e) {
            //
        }
    }
}
