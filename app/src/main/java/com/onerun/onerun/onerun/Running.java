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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.onerun.onerun.onerun.Model.Map;
import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.PersonDataSource;
import com.onerun.onerun.onerun.Model.Run;
import com.onerun.onerun.onerun.Model.RunDataSource;
import com.onerun.onerun.onerun.Model.ServerUtil;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class Running extends Activity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        TextToSpeech.OnInitListener{

    private TextToSpeech mTts;
    private TextView mLocationTextView;
    private TextView mMilliTextView;
    private TextView mSecondsTextView;
    private TextView mMinutesTextView;
    private TextView mHoursTextView;
    private ImageButton mPlayPauseButton;
    private int mHours = 0;
    private int mMinutes = 0;
    private int mSeconds = 0;
    private int mMilli = 0;
    private int totalMilli = 0;
    private int pace = 60;
    private double mAveragePace = 0;
    private int mCadence = -1;
    private int mPaceMin = -1;
    private int mPaceSec = -1;
    private int mIntervalMin = -1;
    private int mIntervalSec = -1;
    private int mInterval = -1;
    private double mDistance = 0;
    private long runid;
    private boolean mGhostRunSet = false;
    private int mGhostRunId;
    private String mExerciseType;
    private Map[] mGhostMapCoor;
    private Run mGhostRun;


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mBestReading = null;

    RunDataSource rundb;
    MapDataSource mapdb;
    PersonDataSource persondb;
    Timer mTimer;

    View.OnClickListener mPauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayPauseButton.setBackgroundResource(R.drawable.ic_action_play);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, Running.this);
            mTimer.cancel();
            mPlayPauseButton.setOnClickListener(mPlayOnClickListener);
        }
    };

    View.OnClickListener mPlayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayPauseButton.setBackgroundResource(R.drawable.ic_action_pause);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, Running.this);
            setupTimer();
            mPlayPauseButton.setOnClickListener(mPauseOnClickListener);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCadence = extras.getInt(WorkoutSetFragment.CADENCE);
            mGhostRunSet = extras.getBoolean(WorkoutSetFragment.GHOSTRUN);
            mGhostRunId = extras.getInt(WorkoutSetFragment.GHOSTRUN_ID);
            mExerciseType = extras.getString(WorkoutSetFragment.EXERCISE);
            mPaceMin = extras.getInt(WorkoutSetFragment.PACEMIN);
            mPaceSec = extras.getInt(WorkoutSetFragment.PACESEC);
            mIntervalMin = extras.getInt(WorkoutSetFragment.INTERVALMIN);
            mIntervalSec = extras.getInt(WorkoutSetFragment.INTERVALSEC);
            pace = (60*mPaceMin) + mPaceSec;
            mInterval = ((mIntervalMin * 60) + mIntervalSec) * 100;

        }
        createLocationRequest();
        setContentView(R.layout.activity_running);

        mTts = new TextToSpeech(this, this);

        setupView();

        setupTimer();

        setupDatabases();

        /*new Thread(new Runnable() {
            public void run() {
                try {
                    Person p = ServerUtil.getRunner("00:00:00:00:00:00");
                    String n = p.getName();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        ServerUtil.endRun("00:00:00:00:00:00");

    }

    private void setupDatabases() {
        rundb = new RunDataSource(this);
        mapdb = new MapDataSource(this);
        persondb = new PersonDataSource(this);

        rundb.open();
        mapdb.open();

        int exercise = -1;
        switch (mExerciseType) {
            case "Running":
                exercise = 1;
                break;
            case "Walking":
                exercise = 2;
                break;
            case "Cycling":
                exercise = 3;
                break;
        }

        runid = rundb.insertRun(exercise, new Date(), new Date(), pace, pace, 0, 0); // TODO: change _sportID depending on what sports

        if (mGhostRunSet) {
            mGhostMapCoor = mapdb.getAllCoorForRun(mGhostRunId);
            mGhostRun = rundb.getRun(mGhostRunId);
        }
        rundb.close();
    }

    private void setupTimer() {
        mTimer = new Timer();
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

                        if (pace > 0 && totalMilli % mInterval == 0){
                            checkCurrentPace();
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

    private void checkCurrentPace() {
        //TODO:Text to speech
        String speak = "";
        int averageMin = (int)mAveragePace/60;
        int averageSeconds = (int)mAveragePace%60;
        if (mAveragePace < pace - 5) {
           speak += "You are going too fast, your pace is " + averageMin + " minutes and " + averageSeconds + " seconds.";
        } else if (mAveragePace > pace + 5) {
           speak += "You are going too slow, your pace is " + averageMin + " minutes and " + averageSeconds + " seconds.";
        }

        if (mGhostRunSet) {
            if (mAveragePace > mGhostRun.getPace() + 5) {
                speak += "You are slower than your ghost";
            } else if (mAveragePace < mGhostRun.getPace() - 5) {
                speak += "You are faster than your ghost";
            } else {
                speak += "You are keeping up with your ghost";
            }
        }

        if(!speak.isEmpty()) {
            mTts.speak(speak, mTts.QUEUE_FLUSH, null);
        }
    }

    private void setupView() {
        mLocationTextView = (TextView) findViewById(R.id.tView);
        mMilliTextView = (TextView) findViewById(R.id.timeMilliView);
        mSecondsTextView = (TextView) findViewById(R.id.timeSecsView);
        mMinutesTextView = (TextView) findViewById(R.id.timeMinsView);
        mHoursTextView = (TextView) findViewById(R.id.timeHoursView);
        mPlayPauseButton = (ImageButton) findViewById(R.id.playPauseButton);

        Button start = (Button) findViewById(R.id.sumbit);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBestReading != null) {
                    handleLocationChanged();
                }
                rundb.open();
                int rid = rundb.getLastRunID();
                rundb.trackEndRunNow(rid, mDistance, mAveragePace);
                rundb.close();
                // calculate calories
                CalorieInterface calorieInterface = new CalorieInterface();
                calorieInterface.newSrategy(rundb, persondb, rid);

                onDestroy();
                finish();
            }
        });

        mPlayPauseButton.setOnClickListener(mPauseOnClickListener);
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
                rundb.open();
                rundb.deleteRun(runid);
                rundb.close();
                mapdb.deleteAllMapsWithRun(runid);
                dialog.dismiss();
                mapdb.close();
                backPressed();
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
        mapdb.insertMap((int)runid, latitude, longitude, new Date(), totalMilli);
        if (mDistance > 0) {
            mAveragePace = (totalMilli / 100) / mDistance;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocationTextView.setText(latitude + "," + longitude);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Location tempLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (tempLocation != null && (mBestReading == null || (tempLocation.getLongitude() != mBestReading.getLongitude() || tempLocation.getLatitude() != mBestReading.getLatitude())) && tempLocation.getAccuracy() <= 11.0f) {
            if (mBestReading != null) {
                mDistance += Measurements.distance(mBestReading.getLatitude(), mBestReading.getLongitude(), tempLocation.getLatitude(), tempLocation.getLongitude(), "K");
            }
            mBestReading = tempLocation;
            handleLocationChanged();
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location tempLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (tempLocation != null && tempLocation.getAccuracy() <= 11.0f) {
            mBestReading = tempLocation;
            handleLocationChanged();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            if(mTts.isLanguageAvailable(Locale.CANADA) == TextToSpeech.LANG_AVAILABLE)
                mTts.setLanguage(Locale.CANADA);
        }
    }

    private void playSound() throws IOException {
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        float vol = 1f;
        am.playSoundEffect(1, vol);
    }
}
    