package pt.isel.pdm.grupo17.thothnews.utils;

import android.util.Log;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    public static final String TAG_DEBUG = "DEBUG";


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

