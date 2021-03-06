package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

import java.util.ArrayList;
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

    public long insertRun(int sportid, Date starttime, Date endtime, double pace, double averagePace, double distance, double calories) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.SPORTID, sportid);
        contentValues.put(dbHelper.STARTTIME, starttime.getTime()); // convert to milliseconds (long)
        contentValues.put(dbHelper.ENDTIME, endtime.getTime()); // convert to milliseconds (long)
        contentValues.put(dbHelper.PACE, pace);
        contentValues.put(dbHelper.AVERAGEPACE, averagePace);
        contentValues.put(dbHelper.DISTANCE, distance);
        contentValues.put(dbHelper.CALORIES, calories);

        // return negative
        long id = writableDB.insert(dbHelper.RUN, null, contentValues);
        return id;
    }

    public boolean updateRunCalories(Run myRun) {
        boolean ret = false;
        String table = dbHelper.RUN;
        String where = dbHelper.KEY + "=" + myRun.getId();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.CALORIES, myRun.getCalories());

        int status = writableDB.update(table, contentValues, where, null);
        if(status == 1) {
            ret = true;
        }
        return ret;
    }

    public long deleteRun(long id) {
        String table = dbHelper.RUN;
        String where = dbHelper.KEY + "=" + id;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public Run getRunObject(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(dbHelper.KEY);
        int sportidIndex = cursor.getColumnIndexOrThrow(dbHelper.SPORTID);
        int starttimeIndex = cursor.getColumnIndexOrThrow(dbHelper.STARTTIME);
        int endtimeIndex = cursor.getColumnIndexOrThrow(dbHelper.ENDTIME);
        int paceIndex = cursor.getColumnIndexOrThrow(dbHelper.PACE);
        int averageIndex = cursor.getColumnIndexOrThrow(dbHelper.AVERAGEPACE);
        int distanceIndex = cursor.getColumnIndexOrThrow(dbHelper.DISTANCE);
        int caloriesIndex = cursor.getColumnIndexOrThrow(dbHelper.CALORIES);

        int rId = Integer.parseInt(cursor.getString(idIndex));
        int rSportid = Integer.parseInt(cursor.getString(sportidIndex));
        long rStarttimeMilliseconds = Long.parseLong(cursor.getString(starttimeIndex));
        Date rStarttimeDate = new Date(rStarttimeMilliseconds);
        long rEndtimeMilliseconds = Long.parseLong(cursor.getString(endtimeIndex));
        Date rEndtimeDate = new Date(rEndtimeMilliseconds);
        double rPace = Double.parseDouble(cursor.getString(paceIndex));
        double rAveragePace = Double.parseDouble(cursor.getString(averageIndex));
        double rDistance = Double.parseDouble(cursor.getString(distanceIndex));
        double rCalories = Double.parseDouble(cursor.getString(caloriesIndex));

        return new Run(rId, rSportid, rStarttimeDate, rEndtimeDate, rPace, rAveragePace, rDistance, rCalories);
    }

    public Run getRun(int id) {
        String where = dbHelper.KEY + "=" + id;
        Cursor cursor = readableDB.query(dbHelper.RUN, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Run run = getRunObject(cursor);

        // close cursor
        cursor.close();

        return run;
    }

    public int getLastRunID(){
        String query = ("SELECT MAX(" + dbHelper.KEY + ") FROM " + dbHelper.RUN);
        Cursor cursor = readableDB.rawQuery(query, null);
        int lastRun = -1;

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // get
//        int idIndex = cursor.getColumnIndexOrThrow(dbHelper.KEY); // this doesn't work because column name is MAX(_ID)
        try {
            lastRun = Integer.parseInt(cursor.getString(0));
        } catch (Exception e) {
            lastRun = -1;
        }

        // close cursor
        cursor.close();
        return lastRun;
    }

    public Integer[] getAllRunIDs() {
        String query = ("SELECT " + dbHelper.KEY + " FROM " + dbHelper.RUN);
        Cursor cursor = readableDB.rawQuery(query, null);
        int cursorSize = cursor.getCount();
        Integer runids[] = new Integer[cursorSize];

        // get all runids
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursorSize; i++) {
                int idIndex = cursor.getColumnIndexOrThrow(dbHelper.KEY);
                int runid = Integer.parseInt(cursor.getString(idIndex));
                runids[i] = runid;
                cursor.moveToNext();
            }
        }

        cursor.close();
        return runids;
    }

    public Run[] getAllRuns() {
        String query = ("SELECT * FROM " + dbHelper.RUN);
        Cursor cursor = readableDB.rawQuery(query, null);
        int cursorSize = cursor.getCount();
        Run runs[] = new Run[cursorSize];

        // get all runids
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursorSize; i++) {
                runs[i] = getRunObject(cursor);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return runs;
    }

    public void trackEndRunNow(int id, double distance, double pace){
        String where = dbHelper.KEY + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(dbHelper.ENDTIME,
                new Date().getTime());
        cv.put(dbHelper.DISTANCE, distance);
        cv.put(dbHelper.AVERAGEPACE, pace);
        writableDB.update(dbHelper.RUN, cv, where, null);
    }
}
