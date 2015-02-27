package com.onerun.onerun.onerun;

import android.content.ContentValues;
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

    // Person Table
    public static final String PERSON = "person";
    public static final String UID = "_id";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String WEIGHT = "weight"; // KG
    public static final String HEIGHT = "height"; // CM

    private static final String CREATE_PERSON_TABLE = "CREATE TABLE " + PERSON + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(255), " + AGE + " INT, " + WEIGHT + " REAL, " + HEIGHT + " REAL);";
    private static final String DROP_PERSON_TABLE = "DROP TABLE IF EXISTS" + PERSON;

    public DataAccessHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        currContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_PERSON_TABLE);
            ToastMessage.message(currContext, "running onCreate");
        } catch (Exception e) {
            // TODO: catch errors
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_PERSON_TABLE);
            onCreate(db);
            ToastMessage.message(currContext, "running onUpgrade");
        } catch (Exception e) {
            // TODO: catch errors
        }
    }
}
