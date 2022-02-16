package com.cinamidea.natour_2022.auth.signin;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour_2022.entities.Tokens;
import com.cinamidea.natour_2022.utilities.ResponseDeserializer;
import com.cinamidea.natour_2022.utilities.http.AuthenticationHTTP;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
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
    public void googleSilentSignIn(GoogleSignInClient client, SharedPreferences google_preferences, SignInContract.Model.OnFinishListenerGoogle listener) {

        Task<GoogleSignInAccount> task = client.silentSignIn();
        if (task.isSuccessful()) {

            //If cached id_token is still valid, then log user in
            SigninFragment.current_username = task.getResult().getDisplayName().replace(" ", "");
            listener.onSuccess();

        } else {

            task.addOnCompleteListener(task1 -> {
                try {
                    //If cached id_token is present, but expired then use refresh token to fetch new id_token and log user in
                    GoogleSignInAccount signInAccount = task1.getResult(ApiException.class);

                    //Salvo username e token nelle shared
                    SigninFragment.current_username = signInAccount.getDisplayName().replace(" ", "");
                    google_preferences.edit().putString("username", signInAccount.getDisplayName().replace(" ", "")).commit();
                    google_preferences.edit().putString("id_token", signInAccount.getIdToken()).commit();

                    listener.onSuccess();

                } catch (ApiException apiException) {
                    //If id_token is not cached or something goes bad
                    int status_code = apiException.getStatusCode();
                    switch (status_code) {
                        case CommonStatusCodes.SIGN_IN_REQUIRED:
                            Log.e("Google SignIn", "Google sign in needed");
                            listener.onSignUpNeeded();
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            listener.onFailure("Network error");
                            break;
                        case CommonStatusCodes.INVALID_ACCOUNT:
                            listener.onFailure("Invalid Account");
                            break;
                        case CommonStatusCodes.INTERNAL_ERROR:
                            listener.onFailure("Internal error");
                            break;
                        case 12501: //Sign in cancelled
                            listener.onFailure("Sign in cancelled");
                            break;
                        case 12502:
                            listener.onFailure("Signin already processing");
                        default:
                            listener.onFailure("Google generic error");
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void googleSignUp(String username, String email, String id_token, SharedPreferences google_preferences, OnFinishListenerGoogle listener) {

        Request request = AuthenticationHTTP.googleSignUp(username, email, id_token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int response_code = response.code();
                String message = response.body().string();
                if (response_code == 200) {
                    google_preferences.edit().putString("username", username).commit();
                    google_preferences.edit().putString("id_token", id_token).commit();
                    listener.onSuccess();
                } else {
                    listener.onFailure(ResponseDeserializer.jsonToMessage(message));
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
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int response_code = response.code();
                String message = response.body().string();
                if (response_code == 200) {
                    Tokens tokens = new Gson().fromJson(ResponseDeserializer.removeQuotesAndUnescape(message), Tokens.class);
                    cognito_preferences.edit().putString("username", username).commit();
                    cognito_preferences.edit().putString("id_token", tokens.getId_token()).commit();
                    cognito_preferences.edit().putString("access_token", tokens.getAccess_token()).commit();
                    cognito_preferences.edit().putString("refresh_token", tokens.getRefresh_token()).commit();
                    listener.onSuccess();
                } else
                    listener.onFailure(ResponseDeserializer.jsonToMessage(message));
            }
        });
    }

}