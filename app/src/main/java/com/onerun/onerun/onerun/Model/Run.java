package com.onerun.onerun.onerun.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.onerun.onerun.onerun.DataAccessHelper;

/**
 * Created by Jason on 15-02-27.
 */
public class Run {
    DataAccessHelper helper;

    public Run(Context context) {
        helper = new DataAccessHelper(context);
    }
}
