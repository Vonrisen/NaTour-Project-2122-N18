package com.cinamidea.natour;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cinamidea.natour.entities.Tokens;
import com.cinamidea.natour.utilities.ResponseDeserializer;
import com.cinamidea.natour.utilities.UserType;
import com.cinamidea.natour.utilities.http.AuthenticationHTTP;
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

public class MainModel implements MainContract.Model{

    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void cognitoSilentSignIn(UserType user_type, MainContract.Model.OnFinishListener listener) {
        Request request = AuthenticationHTTP.tokenLogin(user_type.getIdToken());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int response_code = response.code();
                String message = response.body().string();
                if (response_code == 200) {
                    //Token is valid
                    listener.onSuccess();
                } else { //Token is expired
                    if(message.contains("expired"))
                        refreshCognitoIdAndAccessTokens(user_type, listener);
                    else //Invalid token
                    {
                        user_type.clear();
                        listener.onUserUnauthorized("Invalid session, please sign in again");
                    }
                }
            }
        });
    }

    public void refreshCognitoIdAndAccessTokens(UserType user_type, MainContract.Model.OnFinishListener listener) {

        Request request = AuthenticationHTTP.refreshToken(user_type.getUsername(), user_type.getRefreshToken());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onError("Network error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int response_code = response.code();
                String message = response.body().string();
                if (response_code == 200) {
                    //Saving refreshed id and access tokens
                    Tokens tokens = new Gson().fromJson(ResponseDeserializer.removeQuotesAndUnescape(message), Tokens.class);
                    user_type.setIdToken(tokens.getId_token());
                    user_type.setAccessToken(tokens.getAccess_token());
                    //Trying to sign in again with refreshed id_token
                    cognitoSilentSignIn(user_type, listener);
                } else {
                    Log.e("COGNITO", "Invalid refresh token");
                    //Invalid or expired refresh token
                    user_type.clear();
                    listener.onUserUnauthorized("Invalid session, please sign in again");
                }
            }
        });

    }

    @Override
    public void googleSilentSignIn(GoogleSignInClient client, SharedPreferences google_preferences, MainContract.Model.OnFinishListener listener) {

        Log.d("GOOGLE", "Silent signing in..");
        Task<GoogleSignInAccount> task = client.silentSignIn();
        if (task.isSuccessful()) {
            listener.onSuccess(); //If cached id_token is still valid, then log user in
            Log.i("GOOGLE", "Silent sign in");
        }
            else {      //If cached id_token is present, yet expired, use refresh token to fetch new id_token and log user in
            task.addOnCompleteListener(task1 -> {
                try {

                    GoogleSignInAccount signInAccount = task1.getResult(ApiException.class);

                    //Salvo username e token nelle shared

                    google_preferences.edit().putString("username", signInAccount.getDisplayName().replace(" ", "")).commit();
                    google_preferences.edit().putString("id_token", signInAccount.getIdToken()).commit();

                    listener.onSuccess();

                } catch (ApiException apiException) { //If something goes wrong
                    //Clearing shared preferences so user wont silent sign in again
                    google_preferences.edit().clear().commit();
                    //If id_token is not cached or something goes bad
                    int status_code = apiException.getStatusCode();
                    switch (status_code) {
                        case CommonStatusCodes.SIGN_IN_REQUIRED:
                            listener.onUserUnauthorized("Sign in needed");
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            listener.onUserUnauthorized("Network error");
                            break;
                        case CommonStatusCodes.INVALID_ACCOUNT:
                            listener.onUserUnauthorized("Invalid Account");
                            break;
                        case CommonStatusCodes.INTERNAL_ERROR:
                            listener.onUserUnauthorized("Internal error");
                            break;
                        case 12501: //Sign in cancelled
                            listener.onUserUnauthorized("Sign in cancelled");
                            break;
                        case 12502:
                            listener.onUserUnauthorized("Signin already processing");
                        default:
                            listener.onUserUnauthorized("Google generic error");
                            break;
                    }
                }
            });
        }

    }


}
