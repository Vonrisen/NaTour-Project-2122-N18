package com.cinamidea.natour_2022.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cinamidea.natour_2022.R;
import com.cinamidea.natour_2022.databinding.ActivityGpxBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GpxFragment extends Fragment {

    private GoogleMap gpx_map;
    public int FILE_REQUEST_CODE = 1;
    public String gpx_content;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gpx_map=googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gpx2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        openFile();
    }



    //Metodi gpx

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            Uri uri = data.getData();

            try {
                Log.e("GPX","1");
                gpx_content = readTextFromUri(uri);
                List<LatLng> punti_gpx = new ArrayList<>();
                XmlPullParser xpp = getParser();
                InputStream gpxIn = convertStringToInputStream(gpx_content);
                addMarkerFromGpx(gpxIn, punti_gpx, xpp);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }


    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }


    public  List<LatLng> GpxParser(XmlPullParser parser, InputStream gpxIn) throws XmlPullParserException, IOException {
        // We use a List<> as we need subList for paging later
        List<LatLng> latLngs = new ArrayList<>();
        parser.setInput(gpxIn, null);
        parser.nextTag();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("trkpt")) {
                // Save the discovered latitude/longitude attributes in each <trkpt>.
                latLngs.add(new LatLng(
                        Double.valueOf(parser.getAttributeValue(null, "lat")),
                        Double.valueOf(parser.getAttributeValue(null, "lon"))));
            }
            // Otherwise, skip irrelevant data
        }

        return latLngs;

    }

    public String readTextFromUri(Uri uri) throws IOException {
        Log.e("GPX","2");
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getActivity().getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    public InputStream convertStringToInputStream(String string) {
        Log.e("GPX","4");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        return inputStream;

    }

    public XmlPullParser getParser() {
        Log.e("GPX","3");
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        factory.setNamespaceAware(true);
        XmlPullParser xpp = null;
        try {
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return xpp;
    }

    public void addMarkerFromGpx(InputStream inputStream, List<LatLng> latLngs, XmlPullParser parser) {
        Log.e("GPX","5");
        try {
            latLngs = GpxParser(parser, inputStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (LatLng punto : latLngs) {
            //MarkerOptions options = new MarkerOptions().position(punto).icon(BitmapDescriptorFactory.defaultMarker());
            //gpx_map.addMarker(options);

        }
    }


}