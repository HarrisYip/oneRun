package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class MapDataSource {
    DataAccessHelper dbHelper;
    SQLiteDatabase writableDB;
    SQLiteDatabase readableDB;

    public MapDataSource(Context context) {
        dbHelper = new DataAccessHelper(context);
    }

    public void open() throws SQLException {
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertMap(int runid, double latitude, double longitude, Date time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.RID, runid);
        contentValues.put(dbHelper.LATITUDE, latitude);
        contentValues.put(dbHelper.LONGITUDE, longitude);
        contentValues.put(dbHelper.TIME, time.getTime());

        // return negative id if error
        long id = writableDB.insert(dbHelper.MAP, null, contentValues);
        return id;
    }

    public long deleteMap(int mid) {
        String table = dbHelper.MAP;
        String where = dbHelper.MID + "=" + mid;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public Map getMap(int mid) {
        String where = dbHelper.MID + "=" + mid;
        Cursor cursor = readableDB.query(dbHelper.MAP, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create MapModel object
        int ridIndex = cursor.getColumnIndexOrThrow(dbHelper.RID);
        int latitudeIndex = cursor.getColumnIndexOrThrow(dbHelper.LATITUDE);
        int longitudeIndex = cursor.getColumnIndexOrThrow(dbHelper.LONGITUDE);
        int timeIndex = cursor.getColumnIndexOrThrow(dbHelper.TIME);

        int mRid = Integer.parseInt(cursor.getString(ridIndex));
        double mLatitude = Double.parseDouble(cursor.getString(latitudeIndex));
        double mLongitude = Double.parseDouble(cursor.getString(longitudeIndex));
        long mMilliseconds = Long.parseLong(cursor.getString(timeIndex));
        Date mDate = new Date(mMilliseconds);

        Map map = new Map(mid, mRid, mLatitude, mLongitude, mDate);

        // close cursor
        cursor.close();

        return map;
    }
}
