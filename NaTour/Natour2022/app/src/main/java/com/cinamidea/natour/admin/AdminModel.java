package com.cinamidea.natour.admin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour.utilities.UserType;
import com.cinamidea.natour.utilities.http.AdminHTTP;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminModel implements AdminContract.Model {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void sendMail(UserType user_type, String subject, String body, OnFinishListener listener) {

        if(isSendable(subject, body)) {

            Request request = AdminHTTP.sendMail(user_type.getUsername(), user_type.getIdToken(), subject, body);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    listener.onNetworkError("Network error");
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    int response_code = response.code();
                    String message = response.body().string();
                    Log.e("ciao",message);
                    if(response_code == 200)
                        listener.onSuccess(message);
                    else if(response_code == 400)
                        listener.onError(message);
                    else
                        listener.onUserUnauthorized(message);
                }
            });

        }

    }

    private final boolean isSendable(String subject, String body) {
        return subject.length() >= 0 && body.length() >= 0;
    }

}
