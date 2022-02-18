package com.cinamidea.natour_2022.prova;

import androidx.annotation.NonNull;

import com.cinamidea.natour_2022.entities.Route;
import com.cinamidea.natour_2022.utilities.ResponseDeserializer;
import com.cinamidea.natour_2022.utilities.http.RoutesHTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeModel implements HomeContract.Model{

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();
    @Override
    public void getAllRoutes(String username, String id_token, OnFinishedListener listener) {

        Request request = RoutesHTTP.getAllRoutes(id_token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                String response_body = response.body().string();
                switch (response_code) {
                    case 200:
                        ArrayList<Route> routes = ResponseDeserializer.jsonToRoutesList(response_body);
                        listener.onSuccess(routes);
                        break;
                    case 401:
                        listener.onUserUnauthorized(response_body);
                        break;
                    default:
                        listener.onError(response_body);
                        break;
                }
            }
        });

    }

    @Override
    public void getFavouriteRoutes(String username, String id_token, OnFinishedListener listener) {
        Request request = RoutesHTTP.getFavouriteRoutes(username, id_token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onNetworkError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                String response_body = response.body().string();
                switch (response_code) {
                    case 200:
                        listener.onSuccess(ResponseDeserializer.jsonToRoutesList(response_body));
                        break;
                    case 401:
                        listener.onUserUnauthorized("Invalid session, please sign in again");
                        break;
                    default:
                    listener.onError(response_body);
                    break;
                }
            }
        });
    }

    @Override
    public void getRoutesByDifficulty(String username, String id_token, String difficulty, OnFinishedListener listener) {

        Request request = RoutesHTTP.getRoutesByLevel(id_token, difficulty);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                String response_body = response.body().string();
                switch (response_code) {
                    case 200:
                        ArrayList<Route> routes = ResponseDeserializer.jsonToRoutesList(response_body);
                        listener.onSuccess(routes);
                        break;
                    case 400:
                        listener.onError(response_body);
                        break;
                    case 401:
                        listener.onUserUnauthorized(response_body);
                        break;
                    case 500:
                        listener.onNetworkError(response_body);
                        break;
                    default:
                        return;
                }
            }
        });
    }


}
