package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

/**
 * Created by harriswarrenyip on 15-02-27.
 */
public class Person {
    DataAccessHelper helper;

    public Person(Context context) {
        helper = new DataAccessHelper(context);
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

    public long deleteProfile(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = DataAccessHelper.PERSON;
        String where = DataAccessHelper.UID + "=" + id;
        long retId = db.delete(table, where, null);
        return retId;
    }
}
