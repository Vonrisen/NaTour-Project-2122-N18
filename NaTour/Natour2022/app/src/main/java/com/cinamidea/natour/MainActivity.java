package com.cinamidea.natour;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.cinamidea.natour.navigation.main.views.HomeActivity;
import com.cinamidea.natour.user.AuthActivity;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


public class MainActivity extends AppCompatActivity implements MainContract.View{

    private Button button_signin, button_signup;
    private Animation anim_scale_up, anim_scale_down;
    private final Handler handler = new Handler();
    private GoogleSignInClient googlesignin_client;
    private GoogleSignInOptions gso;
    private MainContract.Presenter presenter;
    private UserType user_type;

    @Override
    protected void onResume() {


        super.onResume();

        if (user_type.getUserType().equals("Cognito"))
            cognitoSilentLogin();

        else if (GoogleSignIn.getLastSignedInAccount(this) != null)
            googleSilentLogin();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_type = new UserType(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(("556927589955-pahgt8na4l8de0985mvlc9gugfltbkef.apps.googleusercontent.com"))
                .build();


        this.googlesignin_client = GoogleSignIn.getClient(this, gso);

        presenter = new MainPresenter(this, new MainModel());

        //Controllo se l utente puo loggare in automatico(o con cognito o con google). Se no, allora alloca le componenti dell' activity (Non richiamo direttamente il metodo (Ho fatto i controlli per evitare di ripetere due volte
        //il silent sign in visto che l onresume parte in ogni caso dopo l oncreate

        if (user_type.getUserType().equals("Cognito")  || GoogleSignIn.getLastSignedInAccount(this) != null)
            return;

        startup();

        setupViewComponents();
        runButtonListeners();
        //getSharedPreferences("natour_tokens", MODE_PRIVATE).edit().clear().commit();

    }

    private void setupViewComponents() {

        button_signin = findViewById(R.id.signin);
        button_signup = findViewById(R.id.signup);
        anim_scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        anim_scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);


    }

    private void startup() {

        MotionLayout ml = findViewById(R.id.motionlayout);
        Handler handler = new Handler();
        handler.postDelayed(() -> ml.transitionToEnd(), 1000);

    }

    private void runAnimation(Button button) {

        button.startAnimation(anim_scale_up);
        button.startAnimation(anim_scale_down);

    }

    private void runIntent(Intent intent) {

        handler.postDelayed(() -> startActivity(intent), 170);

    }

    private void runButtonListeners() {

        button_signin.setOnClickListener(v -> {

            //otherwise signin with username and pwd
            Intent intent = new Intent(this, AuthActivity.class);
            intent.putExtra("key", "signin");
            runAnimation(button_signin);
            runIntent(intent);

        });

        button_signup.setOnClickListener(v -> {

            Intent intent = new Intent(this, AuthActivity.class);
            intent.putExtra("key", "signup");
            runAnimation(button_signup);
            runIntent(intent);


        });

    }


    private void cognitoSilentLogin() {

        presenter.cognitoSilentSignIn(new UserType(this));


    }

    private void googleSilentLogin() {
        presenter.googleSilentSignIn(googlesignin_client, getSharedPreferences("Google", MODE_PRIVATE));
    }

    @Override
    public void silentSignIn() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void displayError(String message) {
        Log.e("SignInUserUnauthorized", message);
    }
}