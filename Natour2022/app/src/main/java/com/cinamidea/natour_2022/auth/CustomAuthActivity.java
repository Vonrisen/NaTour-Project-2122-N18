package com.cinamidea.natour_2022.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cinamidea.natour_2022.R;

public abstract class CustomAuthActivity extends AppCompatActivity {

    protected void changeFragment(int layout, Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(layout, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    protected abstract void setupViewComponents();

}