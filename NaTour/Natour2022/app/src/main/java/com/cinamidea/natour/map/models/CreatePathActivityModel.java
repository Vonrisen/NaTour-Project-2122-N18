package com.cinamidea.natour.map.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.map.contracts.CreatePathActivityContract;
import com.cinamidea.natour.utilities.ResponseDeserializer;
import com.cinamidea.natour.utilities.http.RoutesHTTP;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreatePathActivityModel implements CreatePathActivityContract.Model {

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();


    @Override
    public void insertRoute(OnFinishedListener listener ,String id_token ,Route route) {
        Request request = RoutesHTTP.insertRoute(route, id_token);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                String response_body = response.body().string();
                Log.e("ciao",response_body);
                switch (response_code) {
                    case 200:
                        listener.onSuccess(response_body);
                        break;
                    case 400:
                    case 500:
                        Log.e("MAP","Couldn't insert route: "+ ResponseDeserializer.jsonToMessage(response_body));
                        listener.onError(response_body);
                        break;
                    case 401:
                        listener.onUserUnauthorized();
                        break;
                    default:
                        return;
                }
            }
        });
    }
}
