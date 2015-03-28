package com.onerun.onerun.onerun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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

import java.text.SimpleDateFormat;

/**
 * Created by Terrence on 2/27/2015.
 */
public class WorkoutSetFragment extends Fragment {

    private static GoogleMap mMap;
    public static final String CADENCE = "CADENCE";
    public static final String GHOSTRUN = "GHOSTRUN";
    public static final String GHOSTRUN_ID = "GHOSTRUN_ID";
    public static final String PACEMIN = "PACEMIN";
    public static final String PACESEC = "PACESEC";
    public static final String INTERVALMIN = "INTERVALMIN";
    public static final String INTERVALSEC = "INTERVALSEC";
    public static final String EXERCISE = "EXERCISE";
    private Spinner mIntervalMinute;
    private Spinner mIntervalSeconds;
    private Spinner mPaceMinute;
    private Spinner mPaceSeconds;
    private Spinner mExercise;
    private TextView mBpmTextView;
    private TextView mRunNameView;
    private ToggleButton mCadenceToggle;
    private ToggleButton mGhostRunToggle;
    private LinearLayout mGhostRunLinearLayout;
    private ImageButton mGhostLeftButton;
    private ImageButton mGhostRightButton;
    private SupportMapFragment mMapFragment;
    private EditText mBpmEditText;
    private RunDataSource mRunDB;
    private int mRouteShown;
    private int mMaxRoute;

    public static WorkoutSetFragment newInstance(int sectionNumber) {
        WorkoutSetFragment frag = new WorkoutSetFragment();
        Bundle args = new Bundle();
        args.putInt("SPEECH_SECTION_NUMBER", sectionNumber);
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.workout_set_fragment,
                container, false);

        mRunDB = new RunDataSource(getActivity());
        //EditText pace = (EditText) view.findViewById(R.id.paceSet);
        mBpmTextView = (TextView) view.findViewById(R.id.bpm);
        mCadenceToggle = (ToggleButton) view.findViewById(R.id.cadenceSet);
        mIntervalMinute = (Spinner) view.findViewById(R.id.intervalSetMin);
        mIntervalSeconds = (Spinner) view.findViewById(R.id.intervalSetSec);
        mPaceMinute = (Spinner) view.findViewById(R.id.paceSetMin);
        mPaceSeconds = (Spinner) view.findViewById(R.id.paceSetSec);
        mExercise = (Spinner) view.findViewById(R.id.exerciseType);
        mBpmEditText = (EditText) view.findViewById(R.id.bpmSet);

        mGhostRunToggle = (ToggleButton) view.findViewById(R.id.ghostRunSet);
        mRunNameView = (TextView) view.findViewById(R.id.ghost_run_name);
        mGhostRunLinearLayout = (LinearLayout) view.findViewById(R.id.ghostRunSelector);
        mGhostRightButton = (ImageButton) view.findViewById(R.id.right_ghost_button);
        mGhostLeftButton = (ImageButton) view.findViewById(R.id.left_ghost_button);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ghostMap);
        mMapFragment.getView().setVisibility(View.GONE);

        Button start = (Button) view.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Running.class);
                if (mCadenceToggle.isChecked()) {
                    intent.putExtra(CADENCE, Integer.parseInt(mBpmEditText.getText().toString()));
                }
                if (mGhostRunToggle.isChecked()) {
                    //TODO: Ghost Run stuff
                    intent.putExtra(GHOSTRUN, true);
                    intent.putExtra(GHOSTRUN_ID, mRouteShown);
                }
                intent.putExtra(PACEMIN, Integer.parseInt(mPaceMinute.getSelectedItem().toString()));
                intent.putExtra(PACESEC, Integer.parseInt(mPaceSeconds.getSelectedItem().toString()));
                intent.putExtra(INTERVALMIN, Integer.parseInt(mIntervalMinute.getSelectedItem().toString()));
                intent.putExtra(INTERVALSEC, Integer.parseInt(mIntervalSeconds.getSelectedItem().toString()));
                intent.putExtra(EXERCISE, mExercise.getSelectedItem().toString());
                getActivity().startActivity(intent);
            }
        });

        mCadenceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBpmTextView.setVisibility(View.VISIBLE);
                    mBpmEditText.setVisibility(View.VISIBLE);
                    mBpmEditText.setText("60");
                    mBpmEditText.setCursorVisible(false);
                } else {
                    TextView bpmText = (TextView) view.findViewById(R.id.bpm);
                    bpmText.setVisibility(View.GONE);

                    EditText bpmSet = (EditText) view.findViewById(R.id.bpmSet);
                    bpmSet.setVisibility(View.GONE);
                    bpmSet.setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bpmSet.getWindowToken(), 0);
                }
            }
        });

        mGhostRunToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mMapFragment.getView().setVisibility(View.VISIBLE);
                    mGhostRunLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mMapFragment.getView().setVisibility(View.GONE);
                    mGhostRunLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        mGhostLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteShown > 1) {
                    mRouteShown--;
                    loadRunId(mRouteShown);
                    setUpMap(mRouteShown);
                }
            }
        });

        mGhostRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteShown >= 1 && mRouteShown < mMaxRoute) {
                    mRouteShown++;
                    loadRunId(mRouteShown);
                    setUpMap(mRouteShown);
                }
            }
        });

        setUpMapIfNeeded();
        mRunDB.open();
        int id = mRunDB.getLastRunID();
        mRouteShown = id;
        mMaxRoute = id;
        mRunDB.close();
        if (id < 0) {
            mRunNameView.setText("No Runs");
        } else {
            loadRunId(id);
        }
        return view;
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback(){

                @Override
                public void onMapLoaded() {
                    setUpMap(mRouteShown);
                }
            });
        }
    }

    private void setUpMap(int runId){
        mMap.clear();
        double latArray[] = null;
        double longArray[] = null;
        RunDataSource runDB = new RunDataSource(getActivity());
        runDB.open();
        if (runId > 0) {
            Run lastRun = runDB.getRun(runId);

            MapDataSource mapDB = new MapDataSource(getActivity());
            mapDB.open();
            Map myMap[] = mapDB.getAllCoorForRun(runId);
            mapDB.close();
            latArray = new double[myMap.length];
            longArray = new double[myMap.length];
            for (int i = 0; i < myMap.length; i++) {
                latArray[i] = myMap[i].getLatitude();
                longArray[i] = myMap[i].getLongitude();
            }
        }
        if(latArray == null || latArray.length == 0) {
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

        Polyline line = mMap.addPolyline(options);


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

    private void loadRunId(int id){
        mRunDB.open();
        Run run = mRunDB.getRun(id);
        mRunDB.close();
        String type = "Run";
        SimpleDateFormat parseFormat = new SimpleDateFormat("E MMMM dd,yyyy hh:mm a");
        String formattedStartDate = parseFormat.format(run.getStarttime());
        mRunNameView.setText(type + ":" + formattedStartDate);
    }
}
