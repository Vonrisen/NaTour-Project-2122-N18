package com.cinamidea.natour.utilities.http;

import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHTTPRequest {

    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    Request request;

    public static Request getGetRequest(String url, Headers headers) {

        return headers != null ? new Request.Builder().url(url).headers(headers).build() : new Request.Builder().url(url).build();
    }

    public static Request getDeleteRequest(String url, Headers headers) {

        return headers != null ? new Request.Builder().delete().url(url).headers(headers).build() : new Request.Builder().delete().url(url).build();
    }

    public static Request getPutRequest(String url, String request_body, Headers headers) {

        RequestBody body = RequestBody.create(request_body, MediaType.parse("application/json"));

        return headers != null ? new Request.Builder()
                .url(url)
                .put(body).headers(headers)
                .build() : new Request.Builder()
                .url(url)
                .put(body)
                .build();

    }

    public static Request getPostRequest(String url, String request_body, Headers headers) {

        RequestBody body = RequestBody.create(request_body, MediaType.parse("application/json"));

        return headers != null ? new Request.Builder()
                .url(url)
                .post(body).headers(headers)
                .build() : new Request.Builder()
                .url(url)
                .post(body)
                .build();

    }


}
