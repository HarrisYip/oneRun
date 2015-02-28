package com.onerun.onerun.onerun;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsFragment extends Fragment {

    private static GoogleMap mMap;

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
            if (mMap != null)
                setUpMap();
        }
    }

    private static void setUpMap(){
        //markers zoom etc.
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