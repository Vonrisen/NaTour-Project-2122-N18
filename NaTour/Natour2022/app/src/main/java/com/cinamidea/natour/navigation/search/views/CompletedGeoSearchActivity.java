package com.cinamidea.natour.navigation.search.views;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.navigation.main.recyclerview.RecyclerViewAdapter;

import java.util.ArrayList;

public class CompletedGeoSearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ImageButton button_back;

    private static ArrayList<Route> routes;
    private static ArrayList<Route> fav_routes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_search);

        button_back = findViewById(R.id.activityCompletedGeoSearch_backbutton);

        loadRecyclerView();

        button_back.setOnClickListener(v -> finish());

    }

    private void loadRecyclerView() {

        recyclerView = findViewById(R.id.activityCompletedGeoSearch_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdapter = new RecyclerViewAdapter(this, routes, fav_routes, false);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public static void setRoutes(ArrayList<Route> routes) {
        CompletedGeoSearchActivity.routes = routes;
    }

    public static void setFav_routes(ArrayList<Route> fav_routes) {
        CompletedGeoSearchActivity.fav_routes = fav_routes;
    }

}