package com.cinamidea.natour.map.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.map.contracts.AllPathFragmentContract;
import com.cinamidea.natour.map.models.AllPathFragmentModel;
import com.cinamidea.natour.map.presenters.AllPathFragmentPresenter;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class AllPathsFragment extends Fragment implements AllPathFragmentContract.View {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    public static boolean locationPermissionGranted;
    private GoogleMap map;
    private ProgressDialog dialog;
    private List<LatLng> path;
    private LocationManager locationManager;
    private Location current_location;

    private AllPathFragmentContract.Presenter presenter;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            map.getUiSettings().setCompassEnabled(false);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(41.891922, 12.484828))
                    .zoom(6)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            UserType type = new UserType(getContext());
            presenter.getAllRoutesOnCreate(type.getUserType()+type.getIdToken());

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // instantiating object of AllPathFragmentPresenter Interface
        presenter = new AllPathFragmentPresenter(this, new AllPathFragmentModel());
        return inflater.inflate(R.layout.fragment_all_paths, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getContext());

        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        dialog.setOnDismissListener(dialogInterface -> {

            if (!gpsIsEnabled()) {
                showGPSDisabledDialog();
            }

        });

    }


    private boolean gpsIsEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showGPSDisabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("GPS Disabled");
        builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device");

        builder.setPositiveButton("Enable GPS", (dialog, which) ->
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)).setNegativeButton("No, Just Exit", (dialog, which) -> {
        });
        AlertDialog mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    @Override
    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        }
    }

    @Override
    public void showLoadingDialog() {
        dialog.setMessage("Loading all routes, please wait.....");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        dialog.dismiss();
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
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void drawRoutes(Route[] routes) {
        getActivity().runOnUiThread(() -> {
            for (int i = 0; i < routes.length; i++) {
                path = routes[i].getCoordinates();
                map.addMarker(new MarkerOptions().position(path.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                map.addMarker(new MarkerOptions().position(path.get(path.size() - 1)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(10);
                map.addPolyline(opts);
                Log.d("MAP","Routes loaded");
            }
        });

    }

    @Override
    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        }
        current_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return current_location;
    }

    @Override
    public void updateLocationUI() {
        getActivity().runOnUiThread(() -> {
            if (map == null) {
                return;
            }
            try {
                if (locationPermissionGranted) {

                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);

                    if (current_location != null) {
                        zoomBasedOnCurrentLocation();
                    } else {
                    }
                } else {
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);

                }
            } catch (SecurityException e) {

            }
        });
    }

    private void zoomBasedOnCurrentLocation() {
        Location location = getCurrentLocation();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(8)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}