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
import com.cinamidea.natour.entities.RoutesCompilation;
import com.cinamidea.natour.navigation.compilation.CompilationRecyclerAdapter;
import com.cinamidea.natour.navigation.compilation.contracts.CompilationContract;
import com.cinamidea.natour.navigation.compilation.models.CompilationModel;
import com.cinamidea.natour.navigation.compilation.presenters.CompilationPresenter;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class CompilationActivity extends AppCompatActivity implements CompilationContract.View{

    private RecyclerView recyclerView;
    private CompilationRecyclerAdapter compilationRecyclerViewAdapter;
    private FloatingActionButton button_add;
    private CompilationContract.Presenter presenter;
    private ProgressBar progressBar;
    private String extra = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compilation);

        button_add = findViewById(R.id.activityCompilation_add);
        progressBar = findViewById(R.id.activityCompilation_progress);
        presenter = new CompilationPresenter(this, new CompilationModel());

        extra = getIntent().getStringExtra("route_name");
        if(extra!=null) {

            button_add.setVisibility(View.GONE);

        }


        button_add.setOnClickListener(v -> {

            startActivity(new Intent(this, CreateCompilationActivity.class));

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCompilationRecyclerView();

    }

    private void loadCompilationRecyclerView() {

        recyclerView = findViewById(R.id.activityCompilation_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);

        UserType user_type = new UserType(this);
        String id_token = user_type.getUserType() + user_type.getIdToken();
        presenter.getUserCompilationsButtonClicked(user_type.getUsername(), id_token);

    }

    @Override
    public void loadCompilations(ArrayList<RoutesCompilation> compilations) {
        this.runOnUiThread(() -> {

            progressBar.setVisibility(View.GONE);

            compilationRecyclerViewAdapter = new CompilationRecyclerAdapter(this, compilations, extra);
            recyclerView.setAdapter(compilationRecyclerViewAdapter);

        });
    }

    @Override
    public void displayError(String message) {
       runOnUiThread(()->{
           MotionToast.Companion.createColorToast(CompilationActivity.this, "",
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