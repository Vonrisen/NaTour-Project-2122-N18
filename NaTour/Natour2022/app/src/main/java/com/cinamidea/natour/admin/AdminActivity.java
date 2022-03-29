package com.cinamidea.natour.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.utilities.UserType;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class AdminActivity extends AppCompatActivity implements AdminContract.View {

    private ImageButton button_back;
    private Button button_send;
    private AdminContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        presenter = new AdminPresenter(this, new AdminModel());

        button_back = findViewById(R.id.activityAdmin_backbutton);
        button_send = findViewById(R.id.activityAdmin_send);

        button_back.setOnClickListener(view -> finish());

        button_send.setOnClickListener(view -> {

            String subject = ((EditText) findViewById(R.id.activityAdmin_title)).getText().toString();
            String body = ((EditText) findViewById(R.id.activityAdmin_description)).getText().toString();

            UserType user_type = new UserType(this);
            presenter.onSendButtonClicked(user_type, subject, body);

        });

    }

    @Override
    public void mailSent(String message) {

        runOnUiThread(() -> {
            MotionToast.Companion.createColorToast(this, "",
                    message,
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));

        });


    }

    @Override
    public void logOutUnauthorizedUser() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void displayError(String message) {

        runOnUiThread(() -> {
            MotionToast.Companion.createColorToast(this, "",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));

        });
    }

}