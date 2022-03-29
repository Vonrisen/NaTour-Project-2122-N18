package com.cinamidea.natour.map.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.map.contracts.CreatePathActivityContract;
import com.cinamidea.natour.map.models.CreatePathActivityModel;
import com.cinamidea.natour.map.presenters.CreatePathActivityPresenter;
import com.cinamidea.natour.navigation.main.views.HomeActivity;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class CreatePathActivity extends AppCompatActivity implements CreatePathActivityContract.View {

    String image_base64;
    private ImageButton button_back;
    private TagGroup mTagGroup;
    private EditText edittext_addtag;
    private final List<String> tags = new ArrayList<>();
    private RadioButton rb_easy, rb_medium, rb_hard, rb_extreme;
    private RadioButton rb_checked;

    private Button button_continue;
    private EditText title;
    private EditText description;
    private EditText duration;
    private Button button_addimage;
    private ImageView image;
    private TextView txt_image;
    private CheckBox disability_access;
    private List<LatLng> path;
    private ProgressDialog dialog;

    CreatePathActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_path);

        setupComponents();
        setListeners();
        radioButtonListeners();
    }

    private void setupComponents() {
        presenter = new CreatePathActivityPresenter(this, new CreatePathActivityModel());
        path = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null)
            path = intent.getParcelableArrayListExtra("path");


        dialog = new ProgressDialog(this);
        button_back = findViewById(R.id.activityCreatePath_backbutton);
        mTagGroup = findViewById(R.id.tag_group);
        edittext_addtag = findViewById(R.id.activityCreatePath_addtag);
        rb_easy = findViewById(R.id.activityCreatePath_easy);
        rb_medium = findViewById(R.id.activityCreatePath_medium);
        rb_hard = findViewById(R.id.activityCreatePath_hard);
        rb_extreme = findViewById(R.id.activityCreatePath_extreme);
        rb_checked = rb_easy;
        mTagGroup.clearFocus();


        button_continue = findViewById(R.id.activityCreatePath_continue);
        title = findViewById(R.id.activityCreatePath_title);
        description = findViewById(R.id.activityCreatePath_description);
        duration = findViewById(R.id.activityCreatePath_duration);
        button_addimage = findViewById(R.id.activityCreatePath_addimage);
        image = findViewById(R.id.activityCreatePath_image);
        txt_image = findViewById(R.id.activityCreatePath_imagetext);
        disability_access = findViewById(R.id.activityCreatePath_disability);

    }

    private void setListeners() {

        button_back.setOnClickListener(view -> finish());

        edittext_addtag.setOnEditorActionListener((view, actionId, event) -> {
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_DONE) ;
                else if (actionId == EditorInfo.IME_ACTION_NEXT) ;
                else return false;
            } else if (actionId == EditorInfo.IME_NULL) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) ;
                else return true;
            } else return false;

            String tag_to_add = edittext_addtag.getText().toString();
            if (tags.size() < 10) {

                if (tag_to_add.length() != 0) {

                    if (!tags.contains(tag_to_add)) {

                        tags.add(tag_to_add);
                        mTagGroup.setTags(tags);
                        edittext_addtag.clearFocus();
                        edittext_addtag.getText().clear();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }

                }

            } else {

                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(), "Hai raggiunto il massimo numero di tag", Toast.LENGTH_SHORT).show();

            }
            return true;   // Consume the event

        });

        button_continue.setOnClickListener(view -> {

            if (image_base64 == null)

                MotionToast.Companion.createColorToast(CreatePathActivity.this, "",
                        "Image missing",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));


            if (duration.getText().toString().isEmpty())
                MotionToast.Companion.createColorToast(CreatePathActivity.this, "",
                        "Duration missing",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));

            if (description.getText().toString().isEmpty()) {
                MotionToast.Companion.createColorToast(CreatePathActivity.this, "",
                        "Description missing",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
                return;
            }

            if (image_base64 != null && !duration.getText().toString().isEmpty())
                insertRouteOnDb(path);


        });

        button_addimage.setOnClickListener(view -> {

            openFile();

        });

    }

    private void radioButtonListeners() {

        rb_easy.setOnClickListener(view -> {

            if (rb_checked != null) {

                rb_checked.setChecked(false);
                rb_checked.setTextColor(getResources().getColor(R.color.natour_black));
                rb_easy.setChecked(true);
                rb_easy.setTextColor(getResources().getColor(R.color.natour_white));
                rb_checked = rb_easy;


            }

        });

        rb_medium.setOnClickListener(view -> {

            if (rb_checked != null) {

                rb_checked.setChecked(false);
                rb_checked.setTextColor(getResources().getColor(R.color.natour_black));
                rb_medium.setChecked(true);
                rb_medium.setTextColor(getResources().getColor(R.color.natour_white));
                rb_checked = rb_medium;

            }

        });

        rb_hard.setOnClickListener(view -> {

            if (rb_checked != null) {

                rb_checked.setChecked(false);
                rb_checked.setTextColor(getResources().getColor(R.color.natour_black));
                rb_hard.setChecked(true);
                rb_hard.setTextColor(getResources().getColor(R.color.natour_white));
                rb_checked = rb_hard;

            }

        });

        rb_extreme.setOnClickListener(view -> {

            if (rb_checked != null) {

                rb_checked.setChecked(false);
                rb_checked.setTextColor(getResources().getColor(R.color.natour_black));
                rb_extreme.setChecked(true);
                rb_extreme.setTextColor(getResources().getColor(R.color.natour_white));
                rb_checked = rb_extreme;

            }

        });


    }

    private void insertRouteOnDb(List<LatLng> path) {
        String level = checkLevel();

        if (!checkLevel().equals("error")) {

            Route route = new Route(title.getText().toString(), description.getText().toString(),
                    new UserType(this).getUsername(), level, Integer.parseInt(duration.getText().toString()), 0, checkDisabilityAccess(), path, tokenizedTags(tags), image_base64, getRouteLength(path));

            UserType user_type = new UserType(this);
            presenter.continueButtonClick(user_type.getUserType() + user_type.getIdToken(), route);

        }
    }

    private String checkLevel() {
        if (rb_easy.isChecked())
            return "Easy";
        if (rb_extreme.isChecked())
            return "Extreme";
        if (rb_medium.isChecked())
            return "Medium";
        if (rb_hard.isChecked())
            return "Hard";
        else
            return "error";

    }

    private boolean checkDisabilityAccess() {
        return disability_access.isChecked();
    }

    private String tokenizedTags(List<String> tags) {

        if (tags.size() == 0) return "";

        String tokenized_tags = "";

        for (String tag : tags) {

            tokenized_tags += tag + ";";

        }

        tokenized_tags = tokenized_tags.substring(0, tokenized_tags.length() - 1);


        return tokenized_tags;

    }

    private void openFile() {
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 12);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            Uri uri = data.getData();
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(uri);
                byte[] image_as_byte_array = getBytes(iStream);
                Glide.with(this).load(image_as_byte_array).into(image);
                image_base64 = Base64.getEncoder().encodeToString(image_as_byte_array);
                button_addimage.setVisibility(View.GONE);
                txt_image.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private float getRouteLength(List<LatLng> path) {
        float[] results = new float[1];
        Location.distanceBetween(path.get(0).latitude, path.get(0).longitude,
                path.get(path.size() - 1).latitude, path.get(path.size() - 1).longitude, results);
        return results[0];
    }

    @Override
    public void showLoadingDialog() {
        dialog.setMessage("Please wait...");
        dialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        dialog.dismiss();
    }

    @Override
    public void showToastAddedRoute() {
        runOnUiThread(() -> {
            MotionToast.Companion.createColorToast(CreatePathActivity.this, "",
                    "Route added successfully",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
                    backToHomeAfterInsertedRoute();

        });
    }

    @Override
    public void displayError(String message) {
        runOnUiThread(() -> {
            MotionToast.Companion.createColorToast(CreatePathActivity.this, "",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));

        });
    }

    @Override
    public void logOutUnauthorizedUser() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void backToHomeAfterInsertedRoute() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}