package com.cinamidea.natour_2022.navigation.profile.created;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour_2022.utilities.ResponseDeserializer;
import com.cinamidea.natour_2022.utilities.http.RoutesHTTP;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileMyRoadsModel implements ProfileMyRoadsContract.Model{
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void getUserRoutes(String username, String id_token, OnFinishedListener listener) {

        Log.d("HOME","Loading user routes..");
        Request request = RoutesHTTP.getUserRoutes(username,id_token);
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
                        listener.onSuccess(ResponseDeserializer.jsonToRoutesList(response_body));
                        break;
                    case 401:
                        listener.onUserUnauthorized();
                        break;
                    default:
                        listener.onError(ResponseDeserializer.jsonToMessage(response_body));
                }
            }
        });


    }

    @Override
    public void getFavouriteRoutes(String username, String id_token, OnFavouriteRoutesFetchedListener listener) {

        Request request = RoutesHTTP.getFavouriteRoutes(username, id_token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                String response_body = response.body().string();
                        if(response_code == 200)
                            listener.onSuccess(ResponseDeserializer.jsonToRoutesList(response_body));
            }
        });
    }



}
