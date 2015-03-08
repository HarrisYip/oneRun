package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

/**
 * Created by Jason on 15-03-07.
 */
public class SportDataSource {
    DataAccessHelper dbHelper;
    SQLiteDatabase writableDB;
    SQLiteDatabase readableDB;

    public SportDataSource(Context context) {
        dbHelper = new DataAccessHelper(context);
    }

    public void open() throws SQLException {
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertSport(String type, double multiplier) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.TYPE, type);
        contentValues.put(dbHelper.MULTIPLIER, multiplier);

        // return neagtive id if error
        long id = writableDB.insert(dbHelper.SPORT, null, contentValues);
        return id;
    }

    public long deleteSport(int id) {
        String table = dbHelper.SPORT;
        String where = dbHelper.KEY + "=" + id;

        // return negative id on error
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    private Sport getSportObject(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(dbHelper.KEY);
        int typeIndex = cursor.getColumnIndexOrThrow(dbHelper.TYPE);
        int multiplierIndex = cursor.getColumnIndexOrThrow(dbHelper.MULTIPLIER);

        int id = Integer.parseInt(cursor.getString(idIndex));
        String type = cursor.getString(typeIndex);
        int multiplier = Integer.parseInt(cursor.getString(multiplierIndex));

        return new Sport(id, type, multiplier);
    }

    public Sport getSport(int id) {
        String where = dbHelper.KEY + "=" + id;
        Cursor cursor = readableDB.query(dbHelper.SPORT, null, where, null, null, null, null);

        // get first
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create MapModel object
        Sport sport = getSportObject(cursor);

        // close cursor
        cursor.close();

        return sport;
    }

    public Sport[] getAllSports() {
        String query = ("SELECT * FROM " + dbHelper.SPORT);
        Cursor cursor = readableDB.rawQuery(query, null);
        int cursorSize = cursor.getCount();
        Sport sports[] = new Sport[cursorSize];

        // get all sports
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursorSize; i++) {
                sports[i] = getSportObject(cursor);
                cursor.moveToNext();
            }
        }

        return sports;
    }
}
