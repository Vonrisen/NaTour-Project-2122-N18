package com.cinamidea.natour.navigation.search.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.entities.RouteFilters;
import com.cinamidea.natour.navigation.search.SearchContract;
import com.cinamidea.natour.navigation.search.SearchModel;
import com.cinamidea.natour.navigation.search.SearchPresenter;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.gujun.android.taggroup.TagGroup;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class GeoSearchActivity extends AppCompatActivity implements SearchContract.View {

    private Fragment map_fragment;
    private List<String> tags = new ArrayList<>();
    private List<String> difficulties = new ArrayList<>();
    private int range = 100000;
    private int min_duration = 0;
    private boolean is_disability;
    private Dialog dialog;
    private boolean is_searchview_empty = true;

    private Geocoder geocoder;
    private List<Address> addresses;
    private static PersistentSearchView persistentSearchView;
    private static LatLng latLng;
    private SearchContract.Presenter presenter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_search);

        geocoder = new Geocoder(this, Locale.getDefault());
        map_fragment = new SearchMapFragment();
        persistentSearchView = findViewById(R.id.activityGeoSearch_search);
        persistentSearchView.showRightButton();

        presenter = new SearchPresenter(this, new SearchModel());


        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activityGeoSearch_map, map_fragment);
        fragmentTransaction.commit();

        dialog = new Dialog(GeoSearchActivity.this);
        dialog.setContentView(R.layout.dialog_search_filters);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        listeners();

    }

    private void listeners() {

        persistentSearchView.setOnSearchQueryChangeListener((searchView, oldQuery, newQuery) -> {
            if (persistentSearchView.isInputQueryEmpty()) {

                persistentSearchView.setLeftButtonDrawable(R.drawable.ic_back);
                is_searchview_empty = true;

            } else {

                persistentSearchView.setLeftButtonDrawable(R.drawable.stream_ui_ic_search);
                is_searchview_empty = false;

            }

        });

        persistentSearchView.setOnSearchConfirmedListener((searchView, query) -> {

            if (!persistentSearchView.isInputQueryEmpty()) {
                try {

                    addresses = geocoder.getFromLocationName(persistentSearchView.getInputQuery(), 1);

                    if (!addresses.isEmpty()) {
                        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        setLatLng(latLng);

                        Bundle coordparams = new Bundle();
                        coordparams.putDouble("latitude", latLng.latitude);
                        coordparams.putDouble("longitude", latLng.longitude);

                        RouteFilters routeFilters = new RouteFilters("", tokenizedList(difficulties),
                                min_duration, is_disability, latLng, range, tokenizedList(tags));

                        Bundle range_param = new Bundle();
                        range_param.putInt("range", range);

                        Bundle mindur_param = new Bundle();
                        mindur_param.putInt("min_duration", min_duration);

                        Bundle tags_param = new Bundle();
                        tags_param.putString("tags", tokenizedList(tags));

                        UserType user_type = new UserType(GeoSearchActivity.this);
                        presenter.searchButtonClicked(user_type.getUsername(), routeFilters, user_type.getUserType() + user_type.getIdToken());

                        progressBar = findViewById(R.id.activityGeoSearch_progressbar);
                        progressBar.setVisibility(View.VISIBLE);


                    } else {
                        //Nasconde la tastiera
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                        Toast.makeText(GeoSearchActivity.this, "Luogo inesistente", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        });

        persistentSearchView.setOnLeftBtnClickListener(view -> {

            if (is_searchview_empty)
                finish();
            else
                persistentSearchView.confirmSearchAction();

        });


        persistentSearchView.setOnRightBtnClickListener(view -> {

            dialog.show();

            EditText addtag = dialog.findViewById(R.id.activitySearch_addtag);
            TagGroup tagGroup = dialog.findViewById(R.id.activitySearch_taggroup);
            RadioButton easy = dialog.findViewById(R.id.activitySearch_easy);
            RadioButton medium = dialog.findViewById(R.id.activitySearch_medium);
            RadioButton hard = dialog.findViewById(R.id.activitySearch_hard);
            RadioButton extreme = dialog.findViewById(R.id.activitySearch_extreme);

            addTag(addtag, tagGroup);
            difficultyListener(easy, medium, hard, extreme);

            dialog.findViewById(R.id.activitySearch_cancel).setOnClickListener(v -> {

                dialog.dismiss();
                tags.clear();
                difficulties.clear();
                easy.setChecked(false);
                medium.setChecked(false);
                hard.setChecked(false);
                extreme.setChecked(false);
                tagGroup.setTags(tags);

                ((Slider) dialog.findViewById(R.id.activitySearch_range)).setValue(0);
                ((Slider) dialog.findViewById(R.id.activitySearch_duration)).setValue(0);
                ((CheckBox) dialog.findViewById(R.id.activitySearch_disability)).setChecked(false);

            });

            dialog.findViewById(R.id.activitySearch_ok).setOnClickListener(v -> {

                int range_slider = (int) ((Slider) dialog.findViewById(R.id.activitySearch_range)).getValue();
                int min_duration_slider = (int) ((Slider) dialog.findViewById(R.id.activitySearch_duration)).getValue();

                try {
                    if (range_slider != 0) range = getMeters(range_slider);
                } catch (ArithmeticException exception) {
                    range = Integer.MAX_VALUE;
                }

                try {
                    if (min_duration_slider != 0)
                        min_duration = min_duration_slider;
                } catch (ArithmeticException exception) {
                    min_duration = Integer.MAX_VALUE;
                }
                is_disability = ((CheckBox) dialog.findViewById(R.id.activitySearch_disability)).isChecked();

                dialog.dismiss();

            });

        });

    }

    private void addTag(EditText addtag, TagGroup tagGroup) {

        addtag.setOnEditorActionListener((view, actionId, event) -> {
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_DONE) ;
                else if (actionId == EditorInfo.IME_ACTION_NEXT) ;
                else return false;
            } else if (actionId == EditorInfo.IME_NULL) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) ;
                else return true;
            } else return false;

            String tag_to_add = addtag.getText().toString();
            if (tags.size() < 10) {

                if (tag_to_add.length() != 0) {

                    if (!tags.contains(tag_to_add)) {

                        tags.add(tag_to_add);
                        tagGroup.setTags(tags);
                        addtag.clearFocus();
                        addtag.getText().clear();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }

                }

            } else {

                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(), "Hai raggiunto il massimo numero di tag", Toast.LENGTH_SHORT).show();

            }
            return true;
        });

    }

    private void difficultyListener(RadioButton easy, RadioButton medium, RadioButton hard, RadioButton extreme) {

        easy.setOnClickListener(v -> {
            if (!difficulties.contains("Easy")) difficulties.add("Easy");

        });

        medium.setOnClickListener(v -> {

            if (!difficulties.contains("Medium")) difficulties.add("Medium");

        });

        hard.setOnClickListener(v -> {

            if (!difficulties.contains("Hard")) difficulties.add("Hard");

        });

        extreme.setOnClickListener(v -> {

            if (!difficulties.contains("Extreme")) difficulties.add("Extreme");

        });

    }

    private String tokenizedList(List<String> value) {

        if (value.size() == 0) return "";

        String tokenized_tags = "";

        for (String tag : value) {

            tokenized_tags += tag + ";";

        }

        tokenized_tags = tokenized_tags.substring(0, tokenized_tags.length() - 1);


        return tokenized_tags;

    }

    public static void setLatLng(LatLng latLng) {
        GeoSearchActivity.latLng = latLng;
    }

    public static PersistentSearchView getPersistentSearchView() {
        return persistentSearchView;
    }

    private int getMeters(int km) {

        return km * 1000;

    }

    @Override
    public void loadResults(ArrayList<Route> filtered_routes, ArrayList<Route> favourite_routes) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            CompletedGeoSearchActivity.setRoutes(filtered_routes);
            CompletedGeoSearchActivity.setFav_routes(favourite_routes);
            startActivity(new Intent(GeoSearchActivity.this, CompletedGeoSearchActivity.class));

            Log.d("SEARCH", "Results loaded");

        });

    }

    @Override
    public void displayError(String message) {
        progressBar.setVisibility(View.GONE);
        runOnUiThread(() -> {
            MotionToast.Companion.createColorToast(GeoSearchActivity.this, "",
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
}