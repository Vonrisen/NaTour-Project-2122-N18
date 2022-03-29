package com.cinamidea.natour.navigation.compilation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.navigation.compilation.contracts.CompilationRoutesContract;
import com.cinamidea.natour.navigation.compilation.models.CompilationRoutesModel;
import com.cinamidea.natour.navigation.compilation.presenters.CompilationRoutesPresenter;
import com.cinamidea.natour.navigation.main.recyclerview.RecyclerViewAdapter;
import com.cinamidea.natour.utilities.UserType;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class CompilationRoutesActivity extends AppCompatActivity implements CompilationRoutesContract.View {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private static ArrayList<Route> routes;
    private static ArrayList<Route> fav_routes;
    private CompilationRoutesContract.Presenter presenter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_in_compilation);

        progressBar = findViewById(R.id.activityRoutesCompilation_progress);
        presenter = new CompilationRoutesPresenter(this, new CompilationRoutesModel());

        loadRecyclerView();
    }

    private void loadRecyclerView() {

        recyclerView = findViewById(R.id.activityRoutesCompilation_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        UserType user_type = new UserType(this);
        String id_token = user_type.getUserType() + user_type.getIdToken();
        presenter.getRoutesButtonClicked(user_type.getUsername(),getIntent().getStringExtra("id"), id_token);
        //new RoutesHTTP().getUserRoutesCompilation(SigninFragment.current_username,  id_token, new GetRoutesInCompilationCallback(this, progressBar, recyclerView, recyclerViewAdapter));

    }

    @Override
    public void loadRoutes(ArrayList<Route> routes, ArrayList<Route> fav_routes) {

        this.runOnUiThread(() -> {

            progressBar.setVisibility(View.GONE);

            CompilationRoutesActivity.routes = routes;
            CompilationRoutesActivity.fav_routes = fav_routes;

            recyclerViewAdapter = new RecyclerViewAdapter(this, CompilationRoutesActivity.routes, CompilationRoutesActivity.fav_routes, false);
            recyclerView.setAdapter(recyclerViewAdapter);

        });

    }

    @Override
    public void displayError(String message) {
        runOnUiThread(()->{
            MotionToast.Companion.createColorToast(CompilationRoutesActivity.this, "",
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