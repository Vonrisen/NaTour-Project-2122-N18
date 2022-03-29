package com.cinamidea.natour.navigation.search.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cinamidea.natour.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SearchMapFragment extends Fragment {

    private GoogleMap map;
    private ArrayList<Marker> markerArrayList;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(41.9028,12.4964))
                    .zoom(6)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            map.setOnMapClickListener(latLng -> {
                if (markerArrayList.size() == 0) {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    markerArrayList.add(marker);
                    LatLng point = marker.getPosition();
                    GeoSearchActivity.getPersistentSearchView().setInputQuery(point.latitude + "," + point.longitude);
                    GeoSearchActivity.setLatLng(point);
                }else{
                    markerArrayList.clear();
                    map.clear();
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    markerArrayList.add(marker);
                    LatLng point = marker.getPosition();
                    GeoSearchActivity.getPersistentSearchView().setInputQuery(point.latitude + "," + point.longitude);
                    GeoSearchActivity.setLatLng(point);

                }

            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        markerArrayList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_search_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}