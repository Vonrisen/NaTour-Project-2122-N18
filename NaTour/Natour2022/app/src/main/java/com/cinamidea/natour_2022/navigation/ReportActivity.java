package com.cinamidea.natour_2022.navigation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cinamidea.natour_2022.R;
import com.cinamidea.natour_2022.auth.SigninFragment;
import com.cinamidea.natour_2022.callbacks.report.InsertReportCallback;
import com.cinamidea.natour_2022.callbacks.user.insertReportCallback;
import com.cinamidea.natour_2022.entities.Report;
import com.cinamidea.natour_2022.utilities.auth.UserType;
import com.cinamidea.natour_2022.utilities.report.ReportHTTP;

public class ReportActivity extends AppCompatActivity {

    private ImageButton button_back;
    private Button button_send;
    private RadioGroup radio_group;
    private String report_type;
    private final int MIN_TITLE_LENGTH = 3;
    private final int MIN_DESCRIPTION_LENGTH = 3;
    private String route_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        route_name = getIntent().getStringExtra("route_name");
        setupXMLComponents();
        listeners();

    }

    private void setupXMLComponents() {

        button_back = findViewById(R.id.activityReport_backbutton);
        radio_group = findViewById(R.id.activityReport_radiogroup);
        button_send = findViewById(R.id.activityReport_send);
        report_type = "Wrong";

    }

    private void listeners() {

        button_back.setOnClickListener(view -> finish());

        button_send.setOnClickListener(view -> sendReport());

        radio_group.setOnCheckedChangeListener((radioGroup, i) -> {

            if(i == R.id.activityReport_inaccurate)
                report_type = "Wrong";
            else
                report_type = "Obsolete";

        });

    }

    private void sendReport() {

        String title = ((EditText)findViewById(R.id.activityReport_title)).getText().toString();
        String description = ((EditText)findViewById(R.id.activityReport_description)).getText().toString();

        if(isReportable(title, description)) {

            //TODO: Effettuare il report

            Report report = new Report(route_name,title, description, SigninFragment.current_username, report_type);
            UserType userType = new UserType(this);
            ReportHTTP.insertReport(report, userType.getUser_type()+userType.getId_token(), new InsertReportCallback(this));

        }else Toast.makeText(this, R.string.activityReport_notitleordescription, Toast.LENGTH_SHORT).show();

    }

    private boolean isReportable(String title, String description) {
        return (title.length()>=MIN_TITLE_LENGTH && description.length()>=MIN_DESCRIPTION_LENGTH) ? true : false;
    }

    private void openSuccessDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.success_message_layout);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_alert_dialog));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((TextView) dialog.findViewById(R.id.messageSuccess_message)).setText(R.string.activityReport_success);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.findViewById(R.id.messageSuccess_button).setOnClickListener(view -> {
            dialog.hide();
            finish();
        });

        dialog.setOnCancelListener(dialogInterface -> {
            dialog.hide();
            finish();
        });

    }

    private void openErrorDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.error_message_layout);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_alert_dialog));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((TextView) dialog.findViewById(R.id.messageError_message)).setText(R.string.activityReport_error);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.findViewById(R.id.messageError_button).setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        dialog.setOnCancelListener(dialogInterface -> {
            dialog.dismiss();
            finish();
        });

    }


}