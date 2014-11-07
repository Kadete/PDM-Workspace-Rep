package pt.isel.pdm.grupo17.thothnews.utils;

import android.text.format.DateFormat;
import android.util.Log;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

public class Utils {

    public static final String TAG_DEBUG = "DEBUG";

    public static final SimpleDateFormat SHOW_DATE_FORMAT = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE dd-MM-yyyy' 'HH:mm"));
    public static final SimpleDateFormat SAVE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public static String readAllFrom(InputStream is){
        Scanner s = new Scanner(is);
        try{
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : null;
        }finally{
            s.close();
        }
    }

    public static void d(String strInfo){
        Log.d(TAG_DEBUG, strInfo);
    }




}
