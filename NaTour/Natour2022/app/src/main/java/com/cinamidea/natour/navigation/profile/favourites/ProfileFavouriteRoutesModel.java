package com.cinamidea.natour.navigation.profile.favourites;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.utilities.ResponseDeserializer;
import com.cinamidea.natour.utilities.http.RoutesHTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileFavouriteRoutesModel implements ProfileFavouriteRoutesContract.Model {


    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();


    @Override
    public void getFavRoutes(String username, String id_token, OnFinishedListener listener) {
        Request request = RoutesHTTP.getFavouriteRoutes(username, id_token);
        Log.d("HOME","Loading favourite routes..");
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
                        ArrayList<Route> fav_routes = ResponseDeserializer.jsonToRoutesList(response_body);
                        listener.onSuccess(fav_routes);
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



}
