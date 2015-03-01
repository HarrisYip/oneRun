package com.onerun.onerun.onerun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.onerun.onerun.onerun.Model.MapDataSource;
import com.onerun.onerun.onerun.Model.RunDataSource;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Running extends Activity {
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
    RunDataSource rundb;
    MapDataSource mapdb;


    GPSTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        new loop().execute();
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

    private class loop extends AsyncTask<Void, Void, Void> {
        int count = 0;

        @Override
        protected Void doInBackground(Void... params) {
            //while(count < 10) {
                try {
                    while(true) {
                        if(close){break;}
                        Location l = tracker.getLocation();
                        if(tracker.canGetLocation()){

                            latitude = l.getLatitude();
                            longitude = l.getLongitude();
                            mapdb.insertMap((int)runid,longitude,latitude,new Date());
                            // \n is for new line

                        }else{
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            tracker.showSettingsAlert();
                            //ToastMessage.message(getApplicationContext(), "I've got nothing");
                        }
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
