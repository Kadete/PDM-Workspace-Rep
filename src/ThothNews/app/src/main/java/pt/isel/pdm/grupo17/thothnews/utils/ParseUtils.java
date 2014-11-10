package pt.isel.pdm.grupo17.thothnews.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

import pt.isel.pdm.grupo17.thothnews.models.ThothClass;

public class ParseUtils {

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
    public static ThothClass[] parseThothClasses(String s) throws JSONException {
        JSONObject root = new JSONObject(s);
        JSONArray jclasses = root.getJSONArray("classes");
        ThothClass[] classes = new ThothClass[jclasses.length()];
        for (int i = 0; i < jclasses.length(); ++i) {
            JSONObject jclass = jclasses.getJSONObject(i);
            ThothClass clazz = new ThothClass();
            clazz._id = jclass.getInt("id");
            clazz._fullname = jclass.getString("fullName");
            classes[i] = clazz;
        }
        return classes;
    }

    public static void d(String strInfo){
        Log.d(TAG_DEBUG, strInfo);
    }




}

