package com.onerun.onerun.onerun;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.onerun.onerun.onerun.Model.Map;
import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.PersonDataSource;
import com.onerun.onerun.onerun.Model.Run;
import com.onerun.onerun.onerun.Model.Person;
import com.onerun.onerun.onerun.Model.PersonDataSource;
import com.onerun.onerun.onerun.Model.RunDataSource;
import com.onerun.onerun.onerun.Model.ServerUtil;
import com.onerun.onerun.onerun.Model.RunDataSource;

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

    // RunPass bluetooth connection adapter
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    // person db connection
    private PersonDataSource personDB;

    // runpass bolean
    boolean runPassBoolean = false;

    TextView mLastRunPassView;
    TextView mLocationTextView;
    TextView mMilliTextView;
    TextView mSecondsTextView;
    TextView mMinutesTextView;
    TextView mHoursTextView;
    ImageButton mPlayPauseButton;
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

        // runpass activate
        PersonDataSource personDB = new PersonDataSource(this);
        personDB.open();
        Person myPerson = personDB.getPerson(1);
        runPassBoolean = myPerson.getRunPass();
        personDB.close();
        if(runPassBoolean) {
            setupBluetooth(); // run this before setup runpass profile
            setupRunPassProfile(); // requires bluetooth to be set up first (needs to know bluetooth MAC address)
            doDiscovery();
        }
    }

    // send profile information to heroku to know that I am running
    private void setupRunPassProfile() {
        // get bluetooth mac address
        String btMACAddress = mBtAdapter.getAddress();

        // get profile
        personDB = new PersonDataSource(this);
        personDB.open();
        Person currentPerson;
        try {
            // send info heroku to acknowledge the server that current user is running
            currentPerson = personDB.getPerson(1); // get device user
            ServerUtil.startRun(currentPerson.getName(), btMACAddress);
        } catch (Exception e) {
            // problem with getting person
        }
    }

    // set Bluetooth
    private void setupBluetooth() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

//        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
//        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // make Bluetooth discoverable during run
        ensureDiscoverable();
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Intent i = intent;
            new Thread(new Runnable() {
                public void run() {
                    String action = i.getAction();

                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        // check if it exist in NewDevicesArrayAdapter
                        if(mNewDevicesArrayAdapter.getPosition(device.getAddress()) != -1) {
                            // already checked previously, ignore it
                            return;
                        }
                        mNewDevicesArrayAdapter.add(device.getAddress());

                        // check if it exist in personDB
                        if(persondb.getPersonByRunPass(device.getAddress()).getId() != -1) {
                            return;
                        }

                        // check if runPassId is actual runner from heroku
                        Person runPassPerson = null;
                        try {
                            runPassPerson = ServerUtil.getRunner(device.getAddress());
                            if(runPassPerson.getId() == -1) return; // not a oneRun user
                        } catch (Exception e) {
                            // get person from server failed
                        }

                        // add runPass person to local db
                        persondb.insertProfile(device.getAddress(), runPassPerson.getName(), runPassPerson.getAge(), runPassPerson.getWeight(), runPassPerson.getHeight(), false);
                        String speakRunPass = runPassPerson.getName() + " just passed you";
                        mTts.speak(speakRunPass, mTts.QUEUE_FLUSH, null);
                        changeRunPass(runPassPerson.getName() + " passed you!");

                        // When discovery is finished, change the Activity title
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        // keep searching for bluetooth unless run is finished
                        mBtAdapter.startDiscovery();
                    }
                }
            }).start();
        }
    };

    private void changeRunPass(final String runPassName) {
        runOnUiThread(new Runnable() {
            public void run() {
                mLastRunPassView.setText(runPassName);
            }
        });
    }

    private void setupDatabases() {
        rundb = new RunDataSource(this);
        mapdb = new MapDataSource(this);
        persondb = new PersonDataSource(this);

        rundb.open();
        mapdb.open();
        persondb.open();

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

                        if (pace > 0 && mInterval > 0 && totalMilli % mInterval == 0){
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
        mLastRunPassView = (TextView) findViewById(R.id.lastRunPassView);
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
        persondb.close();
        if(runPassBoolean) {
            ServerUtil.endRun(mBtAdapter.getAddress());
            mBtAdapter.cancelDiscovery();
            try {
                unregisterReceiver(mReceiver);
            } catch (Exception e) {
                // TODO: ignore because onDestroy runs multiple times
            }
        }
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
                persondb.close();
                if(runPassBoolean) {
                    ServerUtil.endRun(mBtAdapter.getAddress());
                    mBtAdapter.cancelDiscovery();
                    try {
                        unregisterReceiver(mReceiver);
                    } catch (Exception e) {
                        // TODO: ignore because onDestroy runs multiple times
                    }
                }
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
    