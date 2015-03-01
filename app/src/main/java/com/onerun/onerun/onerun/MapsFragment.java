package com.onerun.onerun.onerun;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsFragment extends Fragment {

    private static GoogleMap mMap;
    private double latArray[] = {43.473209, 43.471870, 43.469507, 43.468697, 43.467911, 43.466603, 43.467335, 43.469173};
    private double longArray[] = {-80.541670, -80.540039, -80.539275, -80.540155, -80.540455, -80.541171, -80.543489, -80.543961};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

//        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

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