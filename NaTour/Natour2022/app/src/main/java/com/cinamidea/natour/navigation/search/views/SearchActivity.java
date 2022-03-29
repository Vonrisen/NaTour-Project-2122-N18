package com.cinamidea.natour.navigation.search.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.entities.RouteFilters;
import com.cinamidea.natour.navigation.main.recyclerview.RecyclerViewAdapter;
import com.cinamidea.natour.navigation.search.SearchContract;
import com.cinamidea.natour.navigation.search.SearchModel;
import com.cinamidea.natour.navigation.search.SearchPresenter;
import com.cinamidea.natour.utilities.UserType;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SearchActivity extends AppCompatActivity implements SearchContract.View {

    public RecyclerView recyclerView;
    public RecyclerViewAdapter recyclerViewAdapter;
    private PersistentSearchView persistentSearchView;
    private ImageButton button_back;
    private SearchContract.Presenter presenter;
    private ProgressBar progressBar;

    private boolean is_searchview_empty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        presenter = new SearchPresenter(this, new SearchModel());

        button_back = findViewById(R.id.activitySearch_backbutton);
        recyclerView = findViewById(R.id.activitySearch_recyclerview);
        persistentSearchView = findViewById(R.id.activitySearch_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        button_back.setOnClickListener(v -> {

            persistentSearchView.setVisibility(View.VISIBLE);
            button_back.setVisibility(View.GONE);

        });

        persistentSearchView.setOnSearchConfirmedListener((searchView, query) -> {

            hideSoftKeyboard(SearchActivity.this);
            persistentSearchView.setVisibility(View.GONE);
            button_back.setVisibility(View.VISIBLE);
            loadRecyclerView(query);

        });

        persistentSearchView.setOnSearchQueryChangeListener((searchView, oldQuery, newQuery) -> {
            if(persistentSearchView.isInputQueryEmpty()) {

                persistentSearchView.setLeftButtonDrawable(R.drawable.ic_back);
                is_searchview_empty = true;

            }
            else {

                persistentSearchView.setLeftButtonDrawable(R.drawable.stream_ui_ic_search);
                is_searchview_empty = false;

            }

        });

        persistentSearchView.setOnLeftBtnClickListener(view -> {

            if(is_searchview_empty)
                finish();
            else
                persistentSearchView.confirmSearchAction();

        });

    }

    private void loadRecyclerView(String query) {

        RouteFilters routeFilters = new RouteFilters();
        routeFilters.setRoute_name(query);

        progressBar = findViewById(R.id.activitySearch_progress);
        progressBar.setVisibility(View.VISIBLE);

        UserType user_type = new UserType(this);
        String id_token = user_type.getUserType() + user_type.getIdToken();
        presenter.searchButtonClicked(user_type.getUsername(),routeFilters, id_token);


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    @Override
    public void loadResults(ArrayList<Route> filtered_routes, ArrayList<Route> favourite_routes) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            recyclerViewAdapter = new RecyclerViewAdapter(SearchActivity.this, filtered_routes, favourite_routes, false);
            recyclerView.setAdapter(recyclerViewAdapter);
        });

    }

    @Override
    public void displayError(String message) {
        progressBar.setVisibility(View.GONE);
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(SearchActivity.this, "",
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