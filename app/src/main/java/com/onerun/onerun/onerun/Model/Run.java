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
public class Run {
    DataAccessHelper helper;
    SQLiteDatabase writableDB = helper.getWritableDatabase();
    SQLiteDatabase readableDB = helper.getReadableDatabase();

    public Run(Context context) {
        helper = new DataAccessHelper(context);
    }

    public long insertRun(int personid, Date starttime, Date endtime, float pace, float distance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(helper.PID, personid);
        contentValues.put(helper.STARTTIME, starttime.getTime()); // convert to milliseconds (long)
        contentValues.put(helper.ENDTIME, endtime.getTime()); // convert to milliseconds (long)
        contentValues.put(helper.PACE, pace);
        contentValues.put(helper.DISTANCE, distance);

        // return negative
        long id = writableDB.insert(helper.RUN, null, contentValues);
        return id;
    }

    public long deleteRun(int rid) {
        String table = helper.RUN;
        String where = helper.RID + "=" + rid;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public RunModel getRun(int rid) {
        String where = helper.RID + "=" + rid;
        Cursor cursor = readableDB.query(helper.RUN, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create RunModel object
        int pidIndex = cursor.getColumnIndexOrThrow(helper.PID);
        int starttimeIndex = cursor.getColumnIndexOrThrow(helper.STARTTIME);
        int endtimeIndex = cursor.getColumnIndexOrThrow(helper.ENDTIME);
        int paceIndex = cursor.getColumnIndexOrThrow(helper.PACE);
        int distanceIndex = cursor.getColumnIndexOrThrow(helper.DISTANCE);

        int rPid = Integer.parseInt(cursor.getString(pidIndex));
        long rStarttimeMilliseconds = Long.parseLong(cursor.getString(starttimeIndex));
        Date rStarttimeDate = new Date(rStarttimeMilliseconds);
        long rEndtimeMilliseconds = Long.parseLong(cursor.getString(endtimeIndex));
        Date rEndtimeDate = new Date(rEndtimeMilliseconds);
        float rPace = Float.parseFloat(cursor.getString(paceIndex));
        float rDistance = Float.parseFloat(cursor.getString(distanceIndex));

        RunModel runModel = new RunModel(rid, rPid, rStarttimeDate, rEndtimeDate, rPace, rDistance);

        // close cursor
        cursor.close();

        return runModel;
    }
}
