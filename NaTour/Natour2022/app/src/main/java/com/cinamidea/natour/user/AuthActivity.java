package com.cinamidea.natour.user;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cinamidea.natour.R;
import com.cinamidea.natour.user.signin.SigninFragment;
import com.cinamidea.natour.user.signup.views.SignupFragment;

public class AuthActivity extends CustomAuthActivity {

    private ImageView auth_image;
    private ImageButton button_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        setupViewComponents();
        setupFragment();

        customListeners();

    }

    private void customListeners() {

        button_back.setOnClickListener(v -> finish());

    }

    @Override
    protected void setupViewComponents() {
        auth_image = findViewById(R.id.activityAuth_image);
        button_back = findViewById(R.id.activityAuth_back);
    }


    private void setupFragment() {

        String data = getIntent().getExtras().getString("key");
        if (data.equals("signin")) {

            auth_image.setImageResource(R.drawable.image_signin);
            changeFragment(R.id.activityAuth_framelayout, new SigninFragment());

        } else {

            auth_image.setImageResource(R.drawable.image_signup);
            changeFragment(R.id.activityAuth_framelayout, new SignupFragment());

        }

    }


}
