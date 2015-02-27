package com.onerun.onerun.onerun.Model;

import android.content.Context;

import com.onerun.onerun.onerun.DataAccessHelper;

/**
 * Created by Jason on 15-02-27.
 */
public class Map {
    DataAccessHelper helper;

    public Map(Context context) {
        helper = new DataAccessHelper(context);
    }
}
