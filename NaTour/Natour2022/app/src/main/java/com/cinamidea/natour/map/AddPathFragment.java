package com.cinamidea.natour.map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.cinamidea.natour.R;
import com.cinamidea.natour.map.views.CreatePathActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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


//TODO:Controllare se l'user type e l'action siano validi.
public class AddPathFragment extends Fragment {

    private GoogleMap add_path_map;
    private final ArrayList<Marker> markers = new ArrayList<Marker>(2);
    private final ArrayList<Marker> AllMarkers = new ArrayList<Marker>();
    private List<LatLng> path = new ArrayList<>();
    private ImageButton button_success, button_cancel, button_add_gpx;
    private Button button_help;
    private int check_long_press_map_click = 0;


    private final int FILE_REQUEST_CODE = 1;
    private String gpx_content;
    int i = 0;
    private List<LatLng> punti_gpx;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            add_path_map = googleMap;
            add_path_map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            add_path_map.getUiSettings().setCompassEnabled(false);
            zoomOnMap();

            add_path_map.setOnMapClickListener(point -> {

                if(point != null)
                Log.e("ciao","Map click "+String.valueOf(i++));
                if (markers.size() == 0 && check_long_press_map_click == 0) {
                    Marker start_marker = add_path_map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    markers.add(start_marker);
                    path.add(start_marker.getPosition());
                    button_cancel.setVisibility(View.VISIBLE);
                    button_add_gpx.setVisibility(View.GONE);
                    check_long_press_map_click = 1;

                } else if (check_long_press_map_click == 1) {
                    //IL secondo marker il colore rosso
                    Marker marker = add_path_map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    markers.add(marker);
                    AllMarkers.add(marker);
                    button_add_gpx.setVisibility(View.GONE);
                    path.add(marker.getPosition());
                }

                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(10);
                add_path_map.addPolyline(opts);

            });


            add_path_map.setOnMapLongClickListener(latLng -> {

                if(latLng != null)
                Log.e("ciao","Map click "+String.valueOf(i++));

                if (check_long_press_map_click == 1) {

                    Bundle bundle = new Bundle();
                    bundle.putString("opt_param","param");

                    Marker end_marker = add_path_map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    path.add(end_marker.getPosition());
                    //Aggiungiamo una linea rossa tra i marker
                    PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(10);
                    add_path_map.addPolyline(opts);

                    removeAllMarkers();

                    check_long_press_map_click = 2;
                    button_success.setVisibility(View.VISIBLE);

                }

            });


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_path, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        button_cancel = view.findViewById(R.id.activityMap_cancel);
        button_success = view.findViewById(R.id.activityMap_success);
        button_add_gpx = view.findViewById(R.id.activityMap_gpx);
        button_help = view.findViewById(R.id.activityMap_help);
        setListeners();

    }

    private void removeAllMarkers() {
        for (Marker mLocationMarker : AllMarkers) {
            mLocationMarker.remove();
        }
        AllMarkers.clear();

    }

    private void setListeners() {

        button_cancel.setOnClickListener(view -> {

            button_cancel.setVisibility(View.GONE);
            button_add_gpx.setVisibility(View.VISIBLE);
            if (button_success.getVisibility() == View.VISIBLE)
                button_success.setVisibility(View.GONE);

            add_path_map.clear();
            markers.clear();
            button_add_gpx.setVisibility(View.VISIBLE);
            path.clear();
            check_long_press_map_click = 0;

        });

        button_add_gpx.setOnClickListener(view -> {

            openFile();

        });


        button_success.setOnClickListener(view -> {
            Log.d("MAP","Route confirmed");
            Log.d("MAP","Opening Create Route Form..");
            markers.clear();
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("path", (ArrayList<? extends Parcelable>) path);
            intent.setClass(getContext(), CreatePathActivity.class);
            startActivity(intent);
        });

        button_help.setOnClickListener(view -> {

            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_help);
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.background_alert_dialog));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            dialog.findViewById(R.id.dialogHelp_button).setOnClickListener(v -> dialog.dismiss());

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            Uri uri = data.getData();
            if (!checkGPX(uri)) {

                Toast.makeText(getContext(), "GPX Error", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                gpx_content = readTextFromUri(uri);
                punti_gpx = new ArrayList<>();
                XmlPullParser xpp = getParser();
                InputStream gpxIn = convertStringToInputStream(gpx_content);
                path = new ArrayList<>();
                try {
                    punti_gpx = GpxParser(xpp, gpxIn);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
                drawFromGpx(punti_gpx);
                button_add_gpx.setVisibility(View.GONE);
                button_cancel.setVisibility(View.VISIBLE);
                button_success.setVisibility(View.VISIBLE);
                moveCameraOnGpxRoute();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }


    private List<LatLng> GpxParser(XmlPullParser parser, InputStream gpxIn) throws XmlPullParserException, IOException {
        // We use a List<> as we need subList for paging later
        List<LatLng> latLngs = new ArrayList<>();
        parser.setInput(gpxIn, null);
        parser.nextTag();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("trkpt") || parser.getName().equals("rtept")) {
                // Save the discovered latitude/longitude attributes in each <trkpt>.
                latLngs.add(new LatLng(
                        Double.valueOf(parser.getAttributeValue(null, "lat")),
                        Double.valueOf(parser.getAttributeValue(null, "lon"))));
            }
            // Otherwise, skip irrelevant data
        }

        return latLngs;

    }

    private String readTextFromUri(Uri uri) throws IOException {
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

    private InputStream convertStringToInputStream(String string) {
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        return inputStream;

    }

    private XmlPullParser getParser() {
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

    private void drawFromGpx(List<LatLng> latLngs) {


        for (LatLng punto : latLngs) {
            //MarkerOptions options = new MarkerOptions().position(punto).icon(BitmapDescriptorFactory.defaultMarker());
            //gpx_map.addMarker(options);
            path.add(punto);
            if (punto == latLngs.get(0)) {
                MarkerOptions options = new MarkerOptions().position(punto).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                add_path_map.addMarker(options);

            } else if (punto == latLngs.get(latLngs.size() - 1)) {
                MarkerOptions options = new MarkerOptions().position(punto).icon(BitmapDescriptorFactory.defaultMarker());
                add_path_map.addMarker(options);

            } else {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(10);
                add_path_map.addPolyline(opts);
            }

        }
        return;

    }

    private boolean checkGPX(Uri uri) {
        String file_name = DocumentFile.fromSingleUri(getContext(), uri).getName();
        String extension = file_name.substring(file_name.lastIndexOf("."));

        return extension.equals(".gpx");

    }

    private void moveCameraOnGpxRoute() {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(punti_gpx.get(5).latitude, punti_gpx.get(5).longitude))
                .zoom(12)
                .tilt(40)
                .build();
        add_path_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    private void zoomOnMap(){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(41.891922, 12.484828))
                .zoom(6)
                .build();
        add_path_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


}