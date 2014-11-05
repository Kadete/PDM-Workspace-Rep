package isel.pdm.serie1.anniversaryreminder;

import android.util.Log;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Kadete on 15/10/2014.
 */
public class Utils {

    protected static final String TAG_DEBUG = "DEBUG";

    protected final static SimpleDateFormat SHOW_DATE_FORMATTER = new SimpleDateFormat("dd MMMM", Locale.US); //new Locale("pt", "PT")
    protected final static SimpleDateFormat SAVE_DATE_FORMATTER = new SimpleDateFormat("yyyy:MM:dd", Locale.US);

    protected static void d(String strInfo){
        Log.d(TAG_DEBUG, strInfo);
    }

}
