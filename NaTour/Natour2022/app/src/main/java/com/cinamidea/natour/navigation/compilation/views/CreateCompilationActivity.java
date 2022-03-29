package com.cinamidea.natour.navigation.compilation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.RoutesCompilation;
import com.cinamidea.natour.navigation.compilation.contracts.CreateCompilationContract;
import com.cinamidea.natour.navigation.compilation.models.CreateCompilationModel;
import com.cinamidea.natour.navigation.compilation.presenters.CreateCompilationPresenter;
import com.cinamidea.natour.utilities.UserType;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class CreateCompilationActivity extends AppCompatActivity implements CreateCompilationContract.View {

    private Button button_create;
    private ImageButton button_back;
    private ProgressBar progressBar;
    private CreateCompilationContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_compilation);

        button_back = findViewById(R.id.activityCreateCompilation_backbutton);
        button_create = findViewById(R.id.activityCreateCompilation_create);
        progressBar = findViewById(R.id.activityCreateCompilation_progress);
        presenter = new CreateCompilationPresenter(this, new CreateCompilationModel());

        listeners();

    }


    private void listeners() {

        button_back.setOnClickListener(view -> finish());

        button_create.setOnClickListener(view -> {

            String title = ((EditText)findViewById(R.id.activityCreateCompilation_title)).getText().toString();
            String description = ((EditText)findViewById(R.id.activityCreateCompilation_description)).getText().toString();

            if(!title.isEmpty()&&!description.isEmpty()) {
                UserType user_type = new UserType(this);
                RoutesCompilation routesCompilation = new RoutesCompilation("", user_type.getUsername(), title, description, null);


                String id_token = user_type.getUserType() + user_type.getIdToken();

                progressBar.setVisibility(View.VISIBLE);
                presenter.createCompilationButtonClicked(user_type.getUsername(), routesCompilation, id_token);
            }else
            {
                MotionToast.Companion.createColorToast(CreateCompilationActivity.this, "",
                        getResources().getString(R.string.error_EmptyFields),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
            }

        });

    }

    @Override
    public void compilationCreated() {
        this.runOnUiThread(() -> {

            progressBar.setVisibility(View.GONE);
            finish();

        });
    }

    @Override
    public void displayError(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(CreateCompilationActivity.this, "",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
        });

    }

    @Override
    public void logOutUnauthorizedUser() {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(CreateCompilationActivity.this, "",
                    "Invalid session, please sign in again",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
        });
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}