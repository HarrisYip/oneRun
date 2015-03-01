package com.onerun.onerun.onerun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.RunDataSource;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class Running extends Activity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    TextView tView;
    TextView milliTextView;
    TextView secondsTextView;
    TextView minutesTextView;
    TextView hoursTextView;
    private Timer t;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int milli = 0;
    double latitude;
    double longitude;
    boolean close;
    int pace = 60;
    long runid;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mBestReading;

    RunDataSource rundb;
    MapDataSource mapdb;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == RESULT_OK && !mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }

    private boolean servicesAvailable() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }

    GPSTracker tracker;

    @Override
    public void onConnected(Bundle dataBundle) {
        // TODO Auto-generated method stub
        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            // Get best last location measurement meeting criteria
            mBestReading = bestLastKnownLocation(500.0f, 10000);

            if (null == mBestReading
                    || mBestReading.getAccuracy() >500.0f)
            {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                // Schedule a runnable to unregister location listeners
                Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                    @Override
                    public void run() {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, Running.this);
                    }

                }, 1000*60, TimeUnit.MILLISECONDS);
            }
        }

        new loop().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d("CURRENT LOCATION", mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude());
        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
        close = false;
        setContentView(R.layout.activity_running);
        tView = (TextView) findViewById(R.id.tView);
        milliTextView = (TextView) findViewById(R.id.timeMilliView);
        secondsTextView = (TextView) findViewById(R.id.timeSecsView);
        minutesTextView = (TextView) findViewById(R.id.timeMinsView);
        hoursTextView = (TextView) findViewById(R.id.timeHoursView);

        tracker = new GPSTracker(getApplicationContext());
        rundb = new RunDataSource(this);
        mapdb = new MapDataSource(this);

        rundb.open();
        mapdb.open();

        runid = rundb.insertRun(1,new Date(),new Date(),pace,0);

        rundb.close();




        Button start = (Button) findViewById(R.id.sumbit);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        milliTextView.setText(milli - 10 >= 0 ? milli + "" : "0" + milli);
                        secondsTextView.setText(seconds - 10 >= 0 ? seconds + "" : "0" + seconds);
                        minutesTextView.setText(minutes - 10 >= 0 ? minutes + "" : "0" + minutes);
                        hoursTextView.setText(hours - 10 >= 0 ? hours + "" : "0" + hours);
                        milli++;
                        if (milli == 100) {
                            milli = 0;
                            seconds++;
                            if (seconds == 60) {
                                seconds = 0;
                                minutes++;
                                if (minutes == 60) {
                                    minutes = 0;
                                    hours++;
                                }
                            }
                        }
                    }
                });
            }
        }, 10, 10);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_running, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close = true;
        mapdb.close();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                close = true;
                backPressed();
                dialog.dismiss();
                mapdb.close();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                close = false;
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    void backPressed(){
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy()) {
            mBestReading = location;

            if (mBestReading.getAccuracy() < 500.0f) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class loop extends AsyncTask<Void, Void, Void> {
        int count = 0;

        @Override
        protected Void doInBackground(Void... params) {
            //while(count < 10) {
            try {
                while(true) {
                    if(close){break;}
                    mBestReading = bestLastKnownLocation(500.0f, 10000);
                    latitude = mBestReading.getLatitude();
                    longitude = mBestReading.getLongitude();
                    mapdb.insertMap((int)runid,latitude,longitude,new Date());
                    // \n is for new line

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ToastMessage.message(getApplicationContext(),latitude + "," + longitude);
                            tView.setText(latitude + "," + longitude);

                        }
                    });
                    Thread.sleep(10);
                }
            } catch (Exception e) {

            }
            //}
            return null;
        }

    }

}
    