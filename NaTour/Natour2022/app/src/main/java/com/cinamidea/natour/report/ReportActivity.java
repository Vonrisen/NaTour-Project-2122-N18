package com.cinamidea.natour.report;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Report;
import com.cinamidea.natour.utilities.UserType;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ReportActivity extends AppCompatActivity implements ReportContract.View {

    private ImageButton button_back;
    private Button button_send;
    private RadioGroup radio_group;
    private String report_type;
    private String route_name;
    private ReportContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        presenter = new ReportPresenter(this, new ReportModel());

        route_name = getIntent().getStringExtra("route_name");
        button_back = findViewById(R.id.activityReport_backbutton);
        radio_group = findViewById(R.id.activityReport_radiogroup);
        button_send = findViewById(R.id.activityReport_send);
        report_type = "Wrong";
        listeners();

    }

    private void listeners() {

        button_back.setOnClickListener(view -> finish());

        button_send.setOnClickListener(view -> sendReport());

        radio_group.setOnCheckedChangeListener((radioGroup, i) -> {

            if (i == R.id.activityReport_inaccurate)
                report_type = "Wrong";
            else
                report_type = "Obsolete";

        });

    }

    private void sendReport() {

        String title = ((EditText) findViewById(R.id.activityReport_title)).getText().toString();
        String description = ((EditText) findViewById(R.id.activityReport_description)).getText().toString();

        UserType user_type = new UserType(this);
        presenter.sendReportButtonClicked(user_type.getUserType()+user_type.getIdToken(), new Report(route_name, title,description, user_type.getUsername(), report_type));

    }


    @Override
    public void reportDone(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(this,"",
                    message,
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular));
            finish();
        });

    }

    @Override
    public void displayError(String message) {

        runOnUiThread(() -> {
                MotionToast.Companion.createColorToast(this, "",
                        "you have already reported this route",
                        MotionToastStyle.ERROR,
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
}