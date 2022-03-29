package com.cinamidea.natour.map;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cinamidea.natour.R;
import com.cinamidea.natour.map.views.AllPathsFragment;

public class MapActivity extends AppCompatActivity {

    private ImageButton button_back;
    private Button button_addpath, button_allpaths;
    private Fragment fragment_add_path, fragment_all_paths;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setupViewComponents();
        mainListeners();
        button_allpaths.setClickable(false);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map, fragment_all_paths);
        fragmentTransaction.add(R.id.map, fragment_add_path);
        fragmentTransaction.hide(fragment_add_path);
        fragmentTransaction.commit();

    }

    protected void setupViewComponents() {
        button_back = findViewById(R.id.activityMap_backbutton);
        button_addpath = findViewById(R.id.activityMap_addpath);
        button_allpaths = findViewById(R.id.activityMap_allpaths);

        fragment_add_path = new AddPathFragment();
        fragment_all_paths = new AllPathsFragment();

    }

    private void mainListeners() {

        button_back.setOnClickListener(view -> finish());

        button_addpath.setOnClickListener(view -> {

            button_addpath.setClickable(false);

            if (!button_allpaths.isClickable()) {
                button_allpaths.setClickable(true);
                button_allpaths.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.ic_globe), null, null);
            }

            button_addpath.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.ic_add_road_active), null, null);
            changeFragment(fragment_add_path);

        });

        button_allpaths.setOnClickListener(view -> {

            button_allpaths.setClickable(false);

            if (!button_addpath.isClickable()) {
                button_addpath.setClickable(true);
                button_addpath.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.ic_add_road), null, null);
            }

            button_allpaths.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.ic_globe_active), null, null);
            changeFragment(fragment_all_paths);

        });


    }

    protected void changeFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragment instanceof AllPathsFragment) {

            fragmentTransaction.hide(fragment_add_path);
            fragmentTransaction.show(fragment_all_paths);

        } else if (fragment instanceof AddPathFragment) {

            fragmentTransaction.show(fragment_add_path);
            fragmentTransaction.hide(fragment_all_paths);

        }
        fragmentTransaction.commit();


    }

}