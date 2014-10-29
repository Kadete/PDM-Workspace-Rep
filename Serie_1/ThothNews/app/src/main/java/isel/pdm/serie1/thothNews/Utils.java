package isel.pdm.serie1.thothNews;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Kadete on 15/10/2014.
 */
public class Utils {

    private static final String TAG_DEBUG = "DEBUG";

    protected static String readAllFrom(InputStream is){
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
