package com.onerun.onerun.onerun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.onerun.onerun.onerun.Model.Map;
import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.Run;
import com.onerun.onerun.onerun.Model.RunDataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MapsFragment extends Fragment {

    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Bitmap bmp = Bitmap.createBitmap(200, 50, conf);
    Canvas canvas = new Canvas(bmp);

    private static GoogleMap mMap;
    private TextView mStartTimeTextView;
    private TextView mEndTimeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        mStartTimeTextView = (TextView) rootView.findViewById(R.id.startTime);
        mEndTimeTextView = (TextView) rootView.findViewById(R.id.endTime);

        setUpMapIfNeeded();
        return rootView;
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback(){

                @Override
                public void onMapLoaded() {
                    setUpMap();
                }
            });
        }
    }

    private void setUpMap(){
        double latArray[];
        double longArray[];
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
//        runDB.insertRun(0, new Date(10000000), new Date(200000000), 10, 10);
        int runId = runDB.getLastRun();
        if (runId > 0) {
            Run lastRun = runDB.getRun(runId);

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            String formattedStartDate = df.format(lastRun.getStarttime());
            mStartTimeTextView.setText(formattedStartDate);

            String formattedEndDate = df.format(lastRun.getEndtime());
            mEndTimeTextView.setText(formattedEndDate);

            MapDataSource mapDB = new MapDataSource(getActivity());
            mapDB.open();
//            double tempLat[] = {43.473209, 43.471870, 43.469507, 43.468697};
//            double tempLong[] = {-80.541670, -80.540039, -80.539275, -80.540155};
//            for (int i = 0; i < 4; i++) {
//                mapDB.insertMap(0, tempLat[i], tempLong[i], new Date(10000));
//            }
            Map myMap[] = mapDB.getAllCoorForRun(runId);
            mapDB.close();
            latArray = new double[myMap.length];
            longArray = new double[myMap.length];
            for (int i = 0; i < myMap.length; i++) {
                latArray[i] = myMap[i].getLatitude();
                longArray[i] = myMap[i].getLongitude();
            }
        } else {
            latArray = new double[]{43.473209, 43.471870, 43.469507, 43.468697, 43.467911, 43.466603, 43.467335, 43.469173};
            longArray = new double[]{-80.541670, -80.540039, -80.539275, -80.540155, -80.540455, -80.541171, -80.543489, -80.543961};
        }

        runDB.close();

        //markers zoom etc.
        double latMin = latArray[0];
        double longMin = longArray[0];

        double latMax = latArray[0];
        double longMax = longArray[0];
        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        for (int i = 0; i < longArray.length; i++){
            LatLng point = new LatLng(latArray[i], longArray[i]);
            if (latArray[i] < latMin) {
                latMin = latArray[i];
            } else if (latArray[i] > latMax) {
                latMax = latArray[i];
            }

            if (longArray[i] < longMin) {
                longMin = longArray[i];
            } else if (longArray[i] > longMax) {
                longMax = longArray[i];
            }
            options.add(point);
        }
        LatLngBounds bounds = new LatLngBounds(new LatLng(latMin, longMin), new LatLng(latMax, longMax));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));

        Polyline line = mMap.addPolyline(options);

        Marker markerStart = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latArray[0], longArray[0]))
                        .title("Start")
        );

        Marker markerFinish = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(latArray[latArray.length - 1], longArray[longArray.length - 1]))
            .title("Finish")
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            getFragmentManager().beginTransaction()
                    .remove(getChildFragmentManager().findFragmentById(R.id.map)).commit();
            mMap = null;
        }
    }
}