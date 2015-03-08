package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

/**
 * Created by harriswarrenyip on 15-02-27.
 */
public class PersonDataSource {
    DataAccessHelper dbHelper;
    SQLiteDatabase writableDB;
    SQLiteDatabase readableDB;

    public PersonDataSource(Context context) {
        dbHelper = new DataAccessHelper(context);
    }

    public void open() throws SQLException{
        writableDB = dbHelper.getWritableDatabase();
        readableDB = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertProfile(int runpassid, String name, int age, double weight, double height) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.RUNPASSID, runpassid);
        contentValues.put(dbHelper.NAME, name);
        contentValues.put(dbHelper.AGE, age);
        contentValues.put(dbHelper.WEIGHT, weight);
        contentValues.put(dbHelper.HEIGHT, height);

        // returns negative id if error
        long id = writableDB.insert(dbHelper.PERSON, null, contentValues);
        return id;
    }

    public long deleteProfile(int id) {
        String table = dbHelper.PERSON;
        String where = dbHelper.KEY + "=" + id;
        long retId = writableDB.delete(table, where, null);
        return retId;
    }

    public Person getPerson(int id) {
        // open cursor
        String where = dbHelper.KEY + "=" + id;
        Cursor cursor = readableDB.query(dbHelper.PERSON, null, where, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // create PersonModel object
        int runpassidIndex = cursor.getColumnIndexOrThrow(dbHelper.RUNPASSID);
        int nameIndex = cursor.getColumnIndexOrThrow(dbHelper.NAME);
        int ageIndex = cursor.getColumnIndexOrThrow(dbHelper.AGE);
        int weightIndex = cursor.getColumnIndexOrThrow(dbHelper.WEIGHT);
        int heightIndex = cursor.getColumnIndexOrThrow(dbHelper.HEIGHT);

        int pRunpassid = Integer.parseInt(cursor.getString(runpassidIndex));
        String pName = cursor.getString(nameIndex);
        int pAge = Integer.parseInt(cursor.getString(ageIndex));
        double pWeight = Double.parseDouble(cursor.getString(weightIndex));
        double pHeight = Double.parseDouble(cursor.getString(heightIndex));

        Person person = new Person(id, pRunpassid, pName, pAge, pWeight, pHeight);

        // close cursor
        cursor.close();

        return person;
    }

    public boolean updatePerson(Person editPerson) {
        boolean ret = false;
        String table = dbHelper.PERSON;
        String where = dbHelper.KEY + "=" + editPerson.getId();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY, editPerson.getId());
        contentValues.put(dbHelper.RUNPASSID, editPerson.getRunpassid());
        contentValues.put(dbHelper.NAME, editPerson.getName());
        contentValues.put(dbHelper.AGE, editPerson.getAge());
        contentValues.put(dbHelper.WEIGHT, editPerson.getWeight());
        contentValues.put(dbHelper.HEIGHT, editPerson.getHeight());

        int status = writableDB.update(table, contentValues, where, null);
        if(status == 1) {
            ret = true;
        }
        return ret;
    }
}
