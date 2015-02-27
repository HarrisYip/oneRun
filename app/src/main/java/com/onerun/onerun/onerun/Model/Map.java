package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class Map {
    DataAccessHelper helper;
    SQLiteDatabase writableDB = helper.getWritableDatabase();
    SQLiteDatabase readableDB = helper.getReadableDatabase();

    public Map(Context context) {
        helper = new DataAccessHelper(context);
    }

    public long insertMap(int runid, float latitude, float longitude, Date time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(helper.RID, runid);
        contentValues.put(helper.LATITUDE, latitude);
        contentValues.put(helper.LONGITUDE, longitude);
        contentValues.put(helper.TIME, time.getTime());

        // return negative id if error
        long id = writableDB.insert(helper.MAP, null, contentValues);
        return id;
    }

    public long deleteMap(int mid) {
        String table = helper.MAP;
        String where = helper.MID + "=" + mid;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public MapModel getMap(int mid) {
        String where = helper.MID + "=" + mid;
        Cursor cursor = readableDB.query(helper.MAP, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create MapModel object
        int runid = cursor.getColumnIndexOrThrow(helper.RID);
        float latitude = cursor.getColumnIndexOrThrow(helper.LATITUDE);
        float longitude = cursor.getColumnIndexOrThrow(helper.LONGITUDE);
        long timeInMil = cursor.getColumnIndexOrThrow(helper.TIME);
        Date date = new Date(timeInMil);

        MapModel mapModel = new MapModel(mid, runid, latitude, longitude, date);
        return mapModel;
    }
}
