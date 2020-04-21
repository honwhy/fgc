package com.github.honwhy.http;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AsyncHttpRequestTask extends HttpRequestTask {

    private static AsyncHttpClient asyncHttpClient = asyncHttpClient(Dsl.config().setConnectTimeout(2000).setReadTimeout(2000));

    public AsyncHttpRequestTask(int id, String query) {
        super(id, query);
    }

    @Override
    protected String doRequest() throws Exception {
        String requestUrl = String.format("https://cn.bing.com/search?q=%s&qs=n&form=QBLH&sp=-1&pq=a&sc=9-1&sk=&cvid=493B94949A92491FB780F4BCCD085E37", getQuery());
        Future<Response> whenResponse = asyncHttpClient.prepareGet(requestUrl).execute();
        Response response = whenResponse.get();
        if (response != null) {
            return response.getResponseBody(StandardCharsets.UTF_8);
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
