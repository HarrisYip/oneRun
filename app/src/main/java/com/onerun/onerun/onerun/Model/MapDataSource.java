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

    public long insertMap(int runid, double latitude, double longitude, Date time, long runMilli) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.RUNID, runid);
        contentValues.put(dbHelper.LATITUDE, latitude);
        contentValues.put(dbHelper.LONGITUDE, longitude);
        contentValues.put(dbHelper.TRACKTIME, time.getTime());
        contentValues.put(dbHelper.RUNTIME, runMilli);

        // return negative id if error
        long id = writableDB.insert(dbHelper.MAP, null, contentValues);
        return id;
    }

    public long deleteMap(int id) {
        String table = dbHelper.MAP;
        String where = dbHelper.KEY + "=" + id;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public Map getMap(int id) {
        String where = dbHelper.KEY + "=" + id;
        Cursor cursor = readableDB.query(dbHelper.MAP, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create MapModel object
        Map map = getMapObject(cursor);

        // close cursor
        cursor.close();

        return map;
    }

    private Map getMapObject(Cursor cursor) {
        int midIndex = cursor.getColumnIndex(dbHelper.KEY);
        int ridIndex = cursor.getColumnIndexOrThrow(dbHelper.RUNID);
        int latitudeIndex = cursor.getColumnIndexOrThrow(dbHelper.LATITUDE);
        int longitudeIndex = cursor.getColumnIndexOrThrow(dbHelper.LONGITUDE);
        int timeIndex = cursor.getColumnIndexOrThrow(dbHelper.TRACKTIME);
        int runIndex = cursor.getColumnIndexOrThrow(dbHelper.RUNTIME);

        int mId = Integer.parseInt(cursor.getString(midIndex));
        int mRid = Integer.parseInt(cursor.getString(ridIndex));
        double mLatitude = Double.parseDouble(cursor.getString(latitudeIndex));
        double mLongitude = Double.parseDouble(cursor.getString(longitudeIndex));
        long mMilliseconds = Long.parseLong(cursor.getString(timeIndex));
        long mRunMilliseconds = Long.parseLong(cursor.getString(runIndex));
        Date mDate = new Date(mMilliseconds);

        return new Map(mId, mRid, mLatitude, mLongitude, mDate, mRunMilliseconds);
    }

    public Map[] getAllCoorForRun(int rId) {
        String where = dbHelper.RUNID + "=" + rId;
        Cursor cursor = readableDB.query(dbHelper.MAP, null, where, null, null, null, null);

        int cursorSize = cursor.getCount();
        Map coordinates[] = new Map[cursorSize];

        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursorSize; i++) {
                coordinates[i] = getMapObject(cursor);
                cursor.moveToNext();
            }
        }

        cursor.close();


        return coordinates;
    }
}
