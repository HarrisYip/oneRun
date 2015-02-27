package com.onerun.onerun.onerun;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jason on 15-02-27.
 */
public class DataAccessAdapter {

    DataAccessHelper helper;
    Context currContext; // used for Toast Message

    public DataAccessAdapter(Context context) {
        helper = new DataAccessHelper(context);
        this.currContext = context;
    }

    public long insertProfile(String name, int age, float weight, float height) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataAccessHelper.NAME, name);
        contentValues.put(DataAccessHelper.AGE, age);
        contentValues.put(DataAccessHelper.WEIGHT, weight);
        contentValues.put(DataAccessHelper.HEIGHT, height);

        // returns negative id if error
        long id = db.insert(DataAccessHelper.PERSON, null, contentValues);
        return id;
    }

    private class DataAccessHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "oneRun";
        private static final int DATABASE_VERSION = 1; // change this whenever you modify Database Schema

        // Person Table
        private static final String PERSON = "person";
        private static final String UID = "_id";
        private static final String NAME = "name";
        private static final String AGE = "age";
        private static final String WEIGHT = "weight"; // KG
        private static final String HEIGHT = "height"; // CM

        private static final String CREATE_PERSON_TABLE = "CREATE TABLE " + PERSON + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(255), " + AGE + " INT, " + WEIGHT + " REAL, " + HEIGHT + " REAL);";
        private static final String DROP_PERSON_TABLE = "DROP TABLE IF EXISTS" + PERSON;

        public DataAccessHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
}
