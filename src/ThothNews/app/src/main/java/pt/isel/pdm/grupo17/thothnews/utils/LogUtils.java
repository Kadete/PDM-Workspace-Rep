package pt.isel.pdm.grupo17.thothnews.utils;

import android.util.Log;

public class LogUtils {

    public static void d(String tagActivity, String message){
        Log.d(tagActivity, message);
    }
    public static void e(String tagActivity, String message) {
        Log.e(tagActivity, message);
    }

}
