package com.cinamidea.natour.navigation.profile.favourites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.navigation.main.recyclerview.RecyclerViewAdapter;
import com.cinamidea.natour.navigation.main.views.HomeActivity;
import com.cinamidea.natour.utilities.UserType;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ProfileFavouriteRoutesFragment extends Fragment implements ProfileFavouriteRoutesContract.View {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBar;
    private View view;

    private ProfileFavouriteRoutesContract.Presenter presenter;
    private UserType userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userType = new UserType(getContext());
        presenter = new ProfileFavouriteRoutesPresenter(this, new ProfileFavouriteRoutesModel());
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_preferred_roads, container, false);
        setupViewComponents(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        if (!HomeActivity.counter_updated[1]) {
            recyclerView.setAdapter(null);
            progressBar.setVisibility(View.VISIBLE);
            presenter.getFavouriteRoutes(userType.getUsername(), userType.getUserType()+userType.getIdToken());
            HomeActivity.is_updated = false;
            HomeActivity.counter_updated[1] = true;
        }
    }

    private void setupViewComponents(View view) {

        recyclerView = view.findViewById(R.id.fragmentPreferredRoads_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.fragmentPreferredRoads_progress);
        presenter.getFavouriteRoutes(userType.getUsername(),userType.getUserType()+userType.getIdToken());

    }


    @Override
    public void loadRoutes(ArrayList<Route> fav_routes) {
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), fav_routes, true);
            recyclerView.setAdapter(recyclerViewAdapter);
            Log.d("HOME","Favourite routes loaded");
        });
    }

    @Override
    public void displayError(String message) {

        getActivity().runOnUiThread(() -> {
            MotionToast.Companion.createColorToast(getActivity(), "",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));

        });
    }

    @Override
    public void logOutUnauthorizedUser() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}