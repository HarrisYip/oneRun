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

    public long insertProfile(String runpassid, String name, int age, double weight, double height, boolean runPass) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.RUNPASSID, runpassid);
        contentValues.put(dbHelper.NAME, name);
        contentValues.put(dbHelper.AGE, age);
        contentValues.put(dbHelper.WEIGHT, weight);
        contentValues.put(dbHelper.HEIGHT, height);
        contentValues.put(dbHelper.RUNPASSBOOL, runPass);

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
        int runPassIndex = cursor.getColumnIndexOrThrow(dbHelper.RUNPASSBOOL);

        String pRunpassid = cursor.getString(runpassidIndex);
        String pName = cursor.getString(nameIndex);
        int pAge = Integer.parseInt(cursor.getString(ageIndex));
        double pWeight = Double.parseDouble(cursor.getString(weightIndex));
        double pHeight = Double.parseDouble(cursor.getString(heightIndex));
        boolean pRunPass = false;
        String booleanString = cursor.getString(runPassIndex);
        if(booleanString.compareTo("1") == 0) {
            pRunPass = true;
        }
        Person person = new Person(id, pRunpassid, pName, pAge, pWeight, pHeight, pRunPass);

        // close cursor
        cursor.close();

        return person;
    }

    public Person getPersonByRunPass(String MAC) {
        // open cursor
        String where = dbHelper.RUNPASSID + "='" + MAC + "'";
        Cursor cursor = readableDB.query(dbHelper.PERSON, null, where, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Person person;
        try {
            // create PersonModel object
            int personidIndex = cursor.getColumnIndexOrThrow(dbHelper.KEY);
            int runpassidIndex = cursor.getColumnIndexOrThrow(dbHelper.RUNPASSID);
            int nameIndex = cursor.getColumnIndexOrThrow(dbHelper.NAME);
            int ageIndex = cursor.getColumnIndexOrThrow(dbHelper.AGE);
            int weightIndex = cursor.getColumnIndexOrThrow(dbHelper.WEIGHT);
            int heightIndex = cursor.getColumnIndexOrThrow(dbHelper.HEIGHT);
            int boolIndex = cursor.getColumnIndexOrThrow(dbHelper.RUNPASSBOOL);

            int pId = Integer.parseInt(cursor.getString(personidIndex));
            String pRunpassid = cursor.getString(runpassidIndex);
            String pName = cursor.getString(nameIndex);
            int pAge = Integer.parseInt(cursor.getString(ageIndex));
            double pWeight = Double.parseDouble(cursor.getString(weightIndex));
            double pHeight = Double.parseDouble(cursor.getString(heightIndex));
            boolean pBool = Boolean.parseBoolean(cursor.getString(boolIndex));

            person = new Person(pId, pRunpassid, pName, pAge, pWeight, pHeight, pBool);
        } catch (Exception e) {
            person = new Person(-1, "fail", "fail", 0, 0, 0, false);
        }
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
        contentValues.put(dbHelper.RUNPASSBOOL, editPerson.getRunPass());

        int status = writableDB.update(table, contentValues, where, null);
        if(status == 1) {
            ret = true;
        }
        return ret;
    }

    public int[] getAllPeople(){
        String query = ("SELECT " + dbHelper.KEY + " FROM " + dbHelper.PERSON);
        Cursor cursor = readableDB.rawQuery(query, null);
        int cursorSize = cursor.getCount();
        int personIds[] = new int[cursorSize];

        // get all runids
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursorSize; i++) {
                int idIndex = cursor.getColumnIndexOrThrow(dbHelper.KEY);
                int runid = Integer.parseInt(cursor.getString(idIndex));
                personIds[i] = runid;
                cursor.moveToNext();
            }
        }

        cursor.close();
        return personIds;
    }
}
