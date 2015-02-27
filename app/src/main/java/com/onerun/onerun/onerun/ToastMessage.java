package com.onerun.onerun.onerun;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Jason on 15-02-27.
 */
public class ToastMessage {
    public static void message(Context context, String messageString) {
        Toast.makeText(context, messageString, Toast.LENGTH_LONG).show();
    }
}
