package com.onerun.onerun.onerun;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.onerun.onerun.onerun.Model.Map;
import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.Run;
import com.onerun.onerun.onerun.Model.RunDataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MapsFragment extends Fragment {

//    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//    Bitmap bmp = Bitmap.createBitmap(200, 50, conf);
//    Canvas canvas = new Canvas(bmp);

    private static GoogleMap mMap;
    private TextView mStartTimeTextView;
    private TextView mEndTimeTextView;
    private TextView mRunNameView;
    private ImageButton next;
    private ImageButton pervious;
    private View rootView;
    private int lastRunId;
    private int currentRun;
    private GraphView graph;
    Polyline line;
    private ArrayList<Integer> Runs;
    private MapDataSource mMapDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_maps, container, false);

        addListenerOnButton();

        mMapDB = new MapDataSource(getActivity());

        mStartTimeTextView = (TextView) rootView.findViewById(R.id.startTime);
        mEndTimeTextView = (TextView) rootView.findViewById(R.id.endTime);
        mRunNameView = (TextView) rootView.findViewById(R.id.run_name);
        graph = (GraphView) rootView.findViewById(R.id.pace_graph);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    int minutes = (int)value / 60;
                    int seconds = (int)value % 60;
                    return String.format("%d:%02d", minutes, seconds);
                } else {
                    int minutes = (int)value / 60;
                    int seconds = (int)value % 60;
                    return String.format("%d:%02d", minutes, seconds);
                }
            }
        });

        loadRunIds();

        setUpMapIfNeeded();


        return rootView;
    }

    private void addListenerOnButton() {
        next = (ImageButton) rootView.findViewById(R.id.right_button);
        pervious = (ImageButton) rootView.findViewById(R.id.left_button);

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //ToastMessage.message(getActivity(),"Next Click");
                nextRun();
            }

        });
        pervious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //ToastMessage.message(getActivity(),"Previous Click");
                previousRun();
            }

        });


    }

    private void nextRun(){
        if(currentRun < lastRunId){
            currentRun++;
            mRunNameView.setText(getRunName(currentRun));
            updateTime();
            updateCoord();
        }
    }

    private void previousRun(){
        if(currentRun > 1){
            currentRun--;
            mRunNameView.setText(getRunName(currentRun));
            updateTime();
            updateCoord();
        }
    }

    private void updateTime(){
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
        Run lastRun = runDB.getRun(currentRun);

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String formattedStartDate = df.format(lastRun.getStarttime());
        mStartTimeTextView.setText(formattedStartDate);

        String formattedEndDate = df.format(lastRun.getEndtime());
        mEndTimeTextView.setText(formattedEndDate);
        runDB.close();
    }

    private void updateCoord(){
        mMap.clear();
        double pace;
        double latArray[];
        double longArray[];
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
//        runDB.insertRun(0, new Date(10000000), new Date(200000000), 10, 10);
        int runId = currentRun;
        if (runId > 0) {
            Run lastRun = runDB.getRun(runId);
            upDateGraph(lastRun);



            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            String formattedStartDate = df.format(lastRun.getStarttime());
            mStartTimeTextView.setText(formattedStartDate);

            String formattedEndDate = df.format(lastRun.getEndtime());
            mEndTimeTextView.setText(formattedEndDate);


            mMapDB.open();
//            double tempLat[] = {43.473209, 43.471870, 43.469507, 43.468697};
//            double tempLong[] = {-80.541670, -80.540039, -80.539275, -80.540155};
//            for (int i = 0; i < 4; i++) {
//                mapDB.insertMap(0, tempLat[i], tempLong[i], new Date(10000));
//            }
            Map myMap[] = mMapDB.getAllCoorForRun(runId);
            mMapDB.close();
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));

        line = mMap.addPolyline(options);


        Marker markerStart = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latArray[0], longArray[0]))
                        .title("Start")
        );

        Marker markerFinish = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latArray[latArray.length - 1], longArray[longArray.length - 1]))
                        .title("Finish")
        );

        markerStart.showInfoWindow();
        markerFinish.showInfoWindow();
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

    private String getRunName(int runid){
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
        Run run = runDB.getRun(runid);

        String type = "Run";
        SimpleDateFormat parseFormat = new SimpleDateFormat("E MMMM dd,yyyy hh:mm a");
        String formattedStartDate = parseFormat.format(run.getStarttime());
        runDB.close();
        return type + ": " + formattedStartDate ;
    }

    private void loadRunIds(){
        int runIds[];
        Runs = new ArrayList<Integer>();
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
        int runId = runDB.getLastRunID();
        lastRunId = runId;
        runDB.close();
        if(runId  > 0){
            mRunNameView.setText(getRunName(runId));
            for(int x = 1; x <=lastRunId; x++){
                Runs.add(x);
            }
            currentRun = lastRunId;
        }else{
            mRunNameView.setText("No Runs");
        }

    }
    //return average sec/km;
    private double getSpeed(double [] longituted, double [] lattitude, double [] time){
        double speed = 0;
        int n = time.length - 1;
        int count = 0;
        for(int x = 0; x < n; x++){
            double dist = Measurements.distance(lattitude[x], longituted[x], lattitude[x+1], longituted[x+1], "K");
            double diff_time = time[x+1] - time[x];
            if(dist != 0) {
                speed += diff_time / dist;
                count++;
            }
        }
        if (count > 0) {
            speed /= count;
        }
        return speed;
    }

    private void upDateGraph(Run r){


        Date start = r.getStarttime();
        Date end = r.getEndtime();
        int s = 0;
        int e = (int) ((end.getTime()-start.getTime()));
        e = e/1000;
        //ToastMessage.message(getActivity().getApplicationContext(),new String(""+r.getPace()));
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(s, r.getPace()),
                new DataPoint(e, r.getPace()),
        });


        mMapDB.open();
        Map myMap[] = mMapDB.getAllCoorForRun(r.getId());
        mMapDB.close();
        DataPoint[] d = new DataPoint[myMap.length - 1];
        double distance = 0;


        for(int i = 0; i < myMap.length - 1; i++){
            double time = myMap[i + 1].getRunMilli()/100;
            double dist = Measurements.distance(myMap[i].getLatitude(), myMap[i].getLongitude(), myMap[i + 1].getLatitude(), myMap[i + 1].getLongitude(), "K");
            if (!Double.isNaN(dist)){
                distance += dist;
            }
            Log.d("[AYO]", time + " " + distance + " " + myMap[i].getLatitude() + " " + myMap[i].getLongitude() + " " + myMap[i + 1].getLatitude() + " " + myMap[i + 1].getLongitude());
            d[i] = new DataPoint(time, time/distance);
        }

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(e);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(r.getPace() * 3);
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(d);
        //Paint paint = new Paint();
        //paint.setStyle(Paint.Style.STROKE);
        //paint.setStrokeWidth(10);
        //paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        //series1.setCustomPaint(paint);
        series2.setColor(Color.RED);
        graph.addSeries(series1);
        graph.addSeries(series2);
    }

    private void setUpMap(){
        double latArray[];
        double longArray[];
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
//        runDB.insertRun(0, new Date(10000000), new Date(200000000), 10, 10);
        int runId = runDB.getLastRunID();
        if (runId > 0) {
            Run lastRun = runDB.getRun(runId);
            upDateGraph(lastRun);
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            String formattedStartDate = df.format(lastRun.getStarttime());
            mStartTimeTextView.setText(formattedStartDate);

            String formattedEndDate = df.format(lastRun.getEndtime());
            mEndTimeTextView.setText(formattedEndDate);


            mMapDB.open();
//            double tempLat[] = {43.473209, 43.471870, 43.469507, 43.468697};
//            double tempLong[] = {-80.541670, -80.540039, -80.539275, -80.540155};
//            for (int i = 0; i < 4; i++) {
//                mapDB.insertMap(0, tempLat[i], tempLong[i], new Date(10000));
//            }
            Map myMap[] = mMapDB.getAllCoorForRun(runId);
            mMapDB.close();
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));

        line = mMap.addPolyline(options);


        Marker markerStart = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latArray[0], longArray[0]))
                        .title("Start")
        );

        Marker markerFinish = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(latArray[latArray.length - 1], longArray[longArray.length - 1]))
            .title("Finish")
        );

        markerStart.showInfoWindow();
        markerFinish.showInfoWindow();
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