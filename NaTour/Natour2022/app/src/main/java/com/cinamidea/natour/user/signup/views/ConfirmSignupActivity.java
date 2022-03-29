package com.cinamidea.natour.user.signup.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.user.signup.contracts.ConfirmSignUpContract;
import com.cinamidea.natour.user.signup.models.ConfirmSignUpModel;
import com.cinamidea.natour.user.signup.presenters.ConfirmSignUpPresenter;
import com.cinamidea.natour.utilities.UserType;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ConfirmSignupActivity extends AppCompatActivity implements ConfirmSignUpContract.View {

    private ImageButton button_back;
    private com.chaos.view.PinView pin_view;
    private Button button_verify;

    private ConfirmSignUpContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmsignup);

        presenter = new ConfirmSignUpPresenter(this, new ConfirmSignUpModel());

        setupComponents();
        setListeners();

    }

    private void setupComponents() {

        button_back = findViewById(R.id.activityPin_backbutton);
        button_verify = findViewById(R.id.activityPin_button);
        pin_view = findViewById(R.id.activityPin_pin);
    }


    private void setListeners() {

        button_back.setOnClickListener(v -> finish());

        button_verify.setOnClickListener(view -> {
            String confirmation_code = pin_view.getText().toString();
            presenter.confirmSignUpButtonPressed(getIntent().getStringExtra("username"), confirmation_code);
        });
    }

    @Override
    public void confirmSignUpSuccess() {


        new UserType(this).clear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void displayError(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(ConfirmSignupActivity.this,"",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(),R.font.helvetica_regular));
        });
    }
}