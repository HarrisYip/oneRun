package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

/**
 * Created by harriswarrenyip on 15-02-27.
 */
public class Person {
    DataAccessHelper helper;
    SQLiteDatabase writableDB = helper.getWritableDatabase();
    SQLiteDatabase readableDB = helper.getReadableDatabase();


    public Person(Context context) {
        helper = new DataAccessHelper(context);
    }

    public long insertProfile(String name, int age, float weight, float height) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(helper.NAME, name);
        contentValues.put(helper.AGE, age);
        contentValues.put(helper.WEIGHT, weight);
        contentValues.put(helper.HEIGHT, height);

        // returns negative id if error
        long id = writableDB.insert(helper.PERSON, null, contentValues);
        return id;
    }

    public long deleteProfile(int id) {
        String table = helper.PERSON;
        String where = helper.PID + "=" + id;
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public PersonModel getProfile(int id) {
        String where = helper.PID + "=" + id;
        Cursor cursor = readableDB.query(helper.NAME, null, where, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        PersonModel person = new PersonModel(id, cursor.getString(1), Integer.parseInt(cursor.getString(2)), Float.parseFloat(cursor.getString(3)), Float.parseFloat(cursor.getString(4)));
        return person;
    }
}
