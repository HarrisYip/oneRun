package com.onerun.onerun.onerun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jason on 15-02-27.
 */


public class DataAccessHelper extends SQLiteOpenHelper {

    Context currContext;

    private static final String DATABASE_NAME = "oneRun";
    private static final int DATABASE_VERSION = 1; // change this whenever you modify Database Schema

    // Key
    public static final String KEY = "_ID";

    // Person Table
    public static final String PERSON = "PERSON";
    public static final String RUNPASSID = "_runpassID";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String WEIGHT = "weight"; // KG
    public static final String HEIGHT = "height"; // CM
    public static final String RUNPASSBOOL = "runpass";
    private static final String CREATE_PERSON_TABLE = "CREATE TABLE " + PERSON + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RUNPASSID + " INT, " + NAME + " VARCHAR(255), " + AGE + " INT, " + WEIGHT + " REAL, " + HEIGHT + " REAL, " + RUNPASSBOOL + " BOOLEAN);";
    private static final String DROP_PERSON_TABLE = "DROP TABLE IF EXISTS" + PERSON;

    // Run Table
    public static final String RUN = "RUN";
    public static final String SPORTID = "_sportID";
    public static final String STARTTIME = "starttime"; // long milliseconds
    public static final String ENDTIME = "endtime"; // long milliseconds
    public static final String PACE = "pace";
    public static final String AVERAGEPACE = "averagePace";
    public static final String DISTANCE = "distance";
    public static final String CALORIES = "calories";

    private static final String CREATE_RUN_TABLE = "CREATE TABLE " + RUN + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SPORTID + " INT, " + STARTTIME + " INT, " + ENDTIME + " INT, " + PACE + " REAL, " + AVERAGEPACE + " REAL, " + DISTANCE + " REAL, " + CALORIES + " REAL);";
    private static final String DROP_RUN_TABLE = "DROP TABLE IF EXISTS " + RUN;

    // Map Table
    public static final String MAP = "MAP";
    public static final String RUNID = "_runID";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TRACKTIME = "time"; // long milliseconds
    public static final String RUNTIME = "runTime"; // long milliseconds

    private static final String CREATE_MAP_TABLE = "CREATE TABLE " + MAP + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RUNID + " INT, " + LATITUDE + " REAL, " + LONGITUDE + " REAL, " + TRACKTIME + " INT, " + RUNTIME + " INT);";
    private static final String DROP_MAP_TABLE = "DROP TABLE IF EXISTS " + MAP;

    // Sport Table
    public static final String SPORT = "SPORT";
    public static final String TYPE = "type";
    public static final String MULTIPLIER = "multiplier";

    private static final String CREATE_SPORT_TABLE = "CREATE TABLE " + SPORT + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE + " VARCHAR(255), " + MULTIPLIER + " REAL);";
    private static final String DROP_SPORT_TABLE = "DROP TABLE IF EXISTS " + SPORT;

    public DataAccessHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        currContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // create person table
            db.execSQL(CREATE_PERSON_TABLE);
            ToastMessage.message(currContext, "Creating Person Table...");

            // create run table
            db.execSQL(CREATE_RUN_TABLE);
            ToastMessage.message(currContext, "Creating Run Table...");

            // create map table
            db.execSQL(CREATE_MAP_TABLE);
            ToastMessage.message(currContext, "Creating Map Table...");

            // create sport table
            db.execSQL(CREATE_SPORT_TABLE);
            ToastMessage.message(currContext, "Creating Sport Table...");
        } catch (Exception e) {
            // TODO: catch errors
            ToastMessage.message(currContext, "FAILED: Creating Tables!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            ToastMessage.message(currContext, "WARNING: DROPPING ALL TABLES.  NO DATA IS CONSERVED.");
            db.execSQL(DROP_PERSON_TABLE);
            db.execSQL(DROP_RUN_TABLE);
            db.execSQL(DROP_MAP_TABLE);
            db.execSQL(DROP_SPORT_TABLE);

            // recreate all tables after dropping all data
            onCreate(db);

        } catch (Exception e) {
            // TODO: catch errors
            ToastMessage.message(currContext, "FAILED: Updating Tables!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
}
