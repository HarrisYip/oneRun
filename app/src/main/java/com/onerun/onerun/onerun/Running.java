package com.onerun.onerun.onerun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.RunDataSource;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Running extends Activity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    TextView mLocationTextView;
    TextView mMilliTextView;
    TextView mSecondsTextView;
    TextView mMinutesTextView;
    TextView mHoursTextView;
    private int mHours = 0;
    private int mMinutes = 0;
    private int mSeconds = 0;
    private int mMilli = 0;
    private int totalMilli = 0;
    int pace = 60;
    int mCadence = -1;
    long runid;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mBestReading;

    RunDataSource rundb;
    MapDataSource mapdb;
    private final Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCadence = extras.getInt(WorkoutSetFragment.CADENCE);
        }
        createLocationRequest();
        setContentView(R.layout.activity_running);
        setupView();

        setupTimer();

        setupDatabases();

    }

    private void setupDatabases() {
        rundb = new RunDataSource(this);
        mapdb = new MapDataSource(this);

        rundb.open();
        mapdb.open();

        runid = rundb.insertRun(1,new Date(),new Date(),pace,0);

        rundb.close();
    }

    private void setupTimer() {
        final double timePerBeep = mCadence > 0 ? 6000 / mCadence : 0;

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMilliTextView.setText(mMilli - 10 >= 0 ? mMilli + "" : "0" + mMilli);
                        mSecondsTextView.setText(mSeconds - 10 >= 0 ? mSeconds + "" : "0" + mSeconds);
                        mMinutesTextView.setText(mMinutes - 10 >= 0 ? mMinutes + "" : "0" + mMinutes);
                        mHoursTextView.setText(mHours - 10 >= 0 ? mHours + "" : "0" + mHours);
                        mMilli++;
                        totalMilli++;
                        if (mCadence > 0 && totalMilli % timePerBeep == 0) {
                            try {
                                playSound();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (mMilli == 100) {
                            mMilli = 0;
                            mSeconds++;
                            if (mSeconds == 60) {
                                mSeconds = 0;
                                mMinutes++;
                                if (mMinutes == 60) {
                                    mMinutes = 0;
                                    mHours++;
                                }
                            }
                        }
                    }
                });
            }
        }, 10, 10);
    }

    private void setupView() {
        mLocationTextView = (TextView) findViewById(R.id.tView);
        mMilliTextView = (TextView) findViewById(R.id.timeMilliView);
        mSecondsTextView = (TextView) findViewById(R.id.timeSecsView);
        mMinutesTextView = (TextView) findViewById(R.id.timeMinsView);
        mHoursTextView = (TextView) findViewById(R.id.timeHoursView);

        Button start = (Button) findViewById(R.id.sumbit);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rundb.open();
                int rid = rundb.getLastRun();
                rundb.trackEndRun(rid);
                rundb.close();
                onDestroy();
                finish();
            }
        });
    }

    protected void createLocationRequest() {
        this.getSystemService(Context.LOCATION_SERVICE);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == RESULT_OK && !mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
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
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mTimer.cancel();
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
                mTimer.cancel();
                backPressed();
                dialog.dismiss();
                mapdb.close();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    void backPressed(){
        super.onBackPressed();
    }

    private void handleLocationChanged(){
        final double latitude = mBestReading.getLatitude();
        final double longitude = mBestReading.getLongitude();
        mapdb.insertMap((int)runid,latitude,longitude,new Date());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocationTextView.setText(latitude + "," + longitude);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mBestReading = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mBestReading.getAccuracy() <= 10.f) {
            handleLocationChanged();
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mBestReading = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mBestReading != null) {
            handleLocationChanged();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("SUSPEND", "WHY");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("ERROR", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void playSound() throws IOException {
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        float vol = 1f;
        am.playSoundEffect(1, vol);
    }
}
    