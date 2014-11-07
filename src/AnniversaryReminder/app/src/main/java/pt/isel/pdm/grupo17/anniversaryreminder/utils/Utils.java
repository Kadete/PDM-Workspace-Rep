package pt.isel.pdm.grupo17.anniversaryreminder.utils;

import android.util.Log;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;


public class Utils {

    public static final String TAG_DEBUG = "DEBUG";

    public final static SimpleDateFormat SHOW_DATE_FORMATTER = new SimpleDateFormat("dd MMMM", Locale.US); //new Locale("pt", "PT")
    public final static SimpleDateFormat SAVE_DATE_FORMATTER = new SimpleDateFormat("yyyy:MM:dd", Locale.US);

    public static void d(String strInfo){
        Log.d(TAG_DEBUG, strInfo);
    }

}
