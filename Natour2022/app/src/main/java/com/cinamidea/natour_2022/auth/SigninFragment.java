package com.cinamidea.natour_2022.auth;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cinamidea.natour_2022.HomeActivity;
import com.cinamidea.natour_2022.R;
import com.cinamidea.natour_2022.auth_util.AWSCognitoAuthentication;
import com.cinamidea.natour_2022.auth_util.Tokens;
import com.cinamidea.natour_2022.auth_util.AuthenticationCallback;
import com.cinamidea.natour_2022.auth_util.GetTokensCallback;
import com.cinamidea.natour_2022.auth_util.GoogleAuthentication;
import com.cinamidea.natour_2022.auth_util.TokenLoginCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SigninFragment extends CustomAuthFragment {

    private Button button_signin;
    private Intent home_intent, forgotpwd_intent;
    private TextView text_forgotpwd;
    private EditText edit_user;
    private EditText edit_password;
    private Button button_googlesignin;
    private Intent googlesignin_intent;
    public static String chat_username;

    private GoogleAuthentication google_auth = new GoogleAuthentication(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewComponents(view);
        setListeners();

    }

    @Override
    protected void setupViewComponents(View view) {

        button_signin = view.findViewById(R.id.fragmentSignin_signin);
        text_forgotpwd = view.findViewById(R.id.fragmentSignin_forgotpassword);
        edit_user = view.findViewById(R.id.fragmentSignin_username);
        edit_password = view.findViewById(R.id.fragmentSignin_password);
        button_googlesignin = view.findViewById(R.id.fragmentSignin_signinwithgoogle);

        home_intent = new Intent(getActivity(), HomeActivity.class);
        forgotpwd_intent = new Intent(getActivity(), ResetCRActivity.class);
        googlesignin_intent = new Intent(getActivity(), HomeActivity.class);

        setupAnimation();

    }

    private void setListeners() {

        button_signin.setOnClickListener(v -> {

            runAnimation(button_signin);

            String username = edit_user.getText().toString();
            String password = edit_password.getText().toString();

            home_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            AWSCognitoAuthentication auth = new AWSCognitoAuthentication();

            auth.getIdNRefreshTokens(username, password, new GetTokensCallback(getActivity(), username));
            //Set username per l'utente della chat
            chat_username = username;
        });

        text_forgotpwd.setOnClickListener(view -> runHandledIntent(forgotpwd_intent));

        button_googlesignin.setOnClickListener(view -> {
            google_auth.signIn(googlesignin_intent);
        });

    }




}