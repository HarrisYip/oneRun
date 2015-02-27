package com.onerun.onerun.onerun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/**
 * Created by Jason on 15-02-27.
 */


public class DataAccessHelper extends SQLiteOpenHelper {

    Context currContext;

    private static final String DATABASE_NAME = "oneRun";
    private static final int DATABASE_VERSION = 1; // change this whenever you modify Database Schema

    // Person Table
    public static final String PERSON = "person";
    public static final String PID = "_id";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String WEIGHT = "weight"; // KG
    public static final String HEIGHT = "height"; // CM

    private static final String CREATE_PERSON_TABLE = "CREATE TABLE " + PERSON + " (" + PID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(255), " + AGE + " INT, " + WEIGHT + " REAL, " + HEIGHT + " REAL);";
    private static final String DROP_PERSON_TABLE = "DROP TABLE IF EXISTS" + PERSON;

    // Map Table
    public static final String MAP = "map";
    public static final String MID = "_id";
    public static final String RID = "_runid";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIME = "time";

    private static final String CREATE_MAP_TABLE = "CREATE TABLE " + MAP + " (" + MID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RID + " INT, " + LATITUDE + " REAL, " + LONGITUDE + " REAL, " + TIME + " INT);";
    private static final String DROP_MAP_TABLE = "DROP TABLE IF EXISTS " + MAP;

    // Run Table
    public static final String RUN = "run";
    // RID already declared at Map Table
    // PID already declared at Person Table
    public static final String STARTTIME = "starttime";
    public static final String ENDTIME = "endtime";
    public static final String PACE = "pace";
    public static final String DISTANCE = "distance";

    private static final String CREATE_RUN_TABLE = "CREATE TABLE " + RUN + " (" + RID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PID + " INT, " + STARTTIME + " INT, " + ENDTIME + " INT, " + PACE + " REAL, " + DISTANCE + " REAL);";
    private static final String DROP_RUN_TABLE = "DROP TABLE IF EXISTS " + RUN;

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

            // create map table
            db.execSQL(CREATE_MAP_TABLE);
            ToastMessage.message(currContext, "Creating Map Table...");

            // create run table
            db.execSQL(CREATE_RUN_TABLE);
            ToastMessage.message(currContext, "Creating Run Table...");

        } catch (Exception e) {
            // TODO: catch errors
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            ToastMessage.message(currContext, "DROPPING ALL TABLES");
            db.execSQL(DROP_PERSON_TABLE);
            db.execSQL(DROP_MAP_TABLE);
            db.execSQL(DROP_RUN_TABLE);

            // recreate all tables
            onCreate(db);

        } catch (Exception e) {
            // TODO: catch errors
        }
    }
}
