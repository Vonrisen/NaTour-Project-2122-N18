package com.cinamidea.natour.navigation.compilation.models;

import androidx.annotation.NonNull;

import com.cinamidea.natour.entities.RoutesCompilation;
import com.cinamidea.natour.navigation.compilation.contracts.CreateCompilationContract;
import com.cinamidea.natour.utilities.http.RoutesHTTP;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateCompilationModel implements CreateCompilationContract.Model {

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void createCompilation(String username, RoutesCompilation routesCompilation, String id_token, OnFinishedListener listener) {

        Request request = RoutesHTTP.createRoutesCompilation(routesCompilation, id_token);

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
                        listener.onSuccess();
                        break;
                    case 400:
                    case 500:
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
