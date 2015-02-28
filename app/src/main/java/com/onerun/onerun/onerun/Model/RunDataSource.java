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
public class RunDataSource {
    DataAccessHelper dbHelper;
    SQLiteDatabase writableDB;
    SQLiteDatabase readableDB;

    public RunDataSource(Context context) {
        dbHelper = new DataAccessHelper(context);
    }

    public void open() throws SQLException {
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertRun(int personid, Date starttime, Date endtime, double pace, double distance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.PID, personid);
        contentValues.put(dbHelper.STARTTIME, starttime.getTime()); // convert to milliseconds (long)
        contentValues.put(dbHelper.ENDTIME, endtime.getTime()); // convert to milliseconds (long)
        contentValues.put(dbHelper.PACE, pace);
        contentValues.put(dbHelper.DISTANCE, distance);

        // return negative
        long id = writableDB.insert(dbHelper.RUN, null, contentValues);
        return id;
    }

    public long deleteRun(int rid) {
        String table = dbHelper.RUN;
        String where = dbHelper.RID + "=" + rid;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public Run getRun(int rid) {
        String where = dbHelper.RID + "=" + rid;
        Cursor cursor = readableDB.query(dbHelper.RUN, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create RunModel object
        int pidIndex = cursor.getColumnIndexOrThrow(dbHelper.PID);
        int starttimeIndex = cursor.getColumnIndexOrThrow(dbHelper.STARTTIME);
        int endtimeIndex = cursor.getColumnIndexOrThrow(dbHelper.ENDTIME);
        int paceIndex = cursor.getColumnIndexOrThrow(dbHelper.PACE);
        int distanceIndex = cursor.getColumnIndexOrThrow(dbHelper.DISTANCE);

        int rPid = Integer.parseInt(cursor.getString(pidIndex));
        long rStarttimeMilliseconds = Long.parseLong(cursor.getString(starttimeIndex));
        Date rStarttimeDate = new Date(rStarttimeMilliseconds);
        long rEndtimeMilliseconds = Long.parseLong(cursor.getString(endtimeIndex));
        Date rEndtimeDate = new Date(rEndtimeMilliseconds);
        double rPace = Double.parseDouble(cursor.getString(paceIndex));
        double rDistance = Double.parseDouble(cursor.getString(distanceIndex));

        Run run = new Run(rid, rPid, rStarttimeDate, rEndtimeDate, rPace, rDistance);

        // close cursor
        cursor.close();

        return run;
    }
}
