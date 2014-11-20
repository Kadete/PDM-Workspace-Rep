package pt.isel.pdm.grupo17.anniversaryreminder.utils;

import android.util.Log;

public class Utils {

    public static final String TAG_DEBUG = "DEBUG";
    public static final String TAG_BROADCAST = "BROADCASTRECEIVER";
    public static final String TAG_ACTIVITY = "ACTIVITY";

    public static void d(String tag, String strInfo){
        Log.d(tag, strInfo);
    }

}

