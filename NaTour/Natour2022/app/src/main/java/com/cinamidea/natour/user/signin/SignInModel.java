package com.cinamidea.natour.user.signin;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour.entities.Tokens;
import com.cinamidea.natour.utilities.ResponseDeserializer;
import com.cinamidea.natour.utilities.http.AuthenticationHTTP;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignInModel implements SignInContract.Model {

    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void googleSignUp(String username, String email, String id_token, SharedPreferences google_preferences, OnFinishListenerGoogle listener) {

        Request request = AuthenticationHTTP.googleSignUp(username, email, id_token);

        Log.e("Google","signin up");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int response_code = response.code();
                String message = response.body().string();
                Log.e("Google",message);
                if (response_code == 200) {
                    google_preferences.edit().putString("username", username.replace(" ","")).commit();
                    google_preferences.edit().putString("id_token", id_token).commit();
                    listener.onSuccess();
                } else {
                    listener.onError(message);
                    google_preferences.edit().clear().commit();
                }
            }
        });
    }


    @Override
    public void cognitoSignIn(String username, String password, SharedPreferences cognito_preferences, OnFinishListenerCognito listener) {

        Request request = AuthenticationHTTP.signInAndGetTokensRequest(username, password);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int response_code = response.code();
                SharedPreferences.Editor editor = cognito_preferences.edit();
                String message = response.body().string();
                if (response_code == 200) {
                    Tokens tokens = new Gson().fromJson(ResponseDeserializer.removeQuotesAndUnescape(message), Tokens.class);
                    editor.putString("username", username.toLowerCase());
                    editor.putString("id_token", tokens.getId_token());
                    editor.putString("access_token", tokens.getAccess_token());
                    editor.putString("refresh_token", tokens.getRefresh_token());
                    editor.commit();
                    listener.onSuccess();
                } else {
                    editor.clear().commit();
                    listener.onError(ResponseDeserializer.jsonToMessage(message));
                }
            }
        });
    }
}
