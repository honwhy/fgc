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
    protected String doRequest(String requestUrl) throws Exception {
        Future<Response> whenResponse = asyncHttpClient.prepareGet("http://www.example.com/").execute();
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
