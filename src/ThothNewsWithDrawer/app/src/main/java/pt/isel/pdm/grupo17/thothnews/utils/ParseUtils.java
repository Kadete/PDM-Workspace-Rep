package pt.isel.pdm.grupo17.thothnews.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
public class ParseUtils {

    public static String readAllFrom(InputStream is){
        Scanner s = new Scanner(is);
        try{
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : null;
        }finally{
            s.close();
        }
    }

    public static JSONArray parseClasses(String s) throws JSONException {
        JSONObject root = new JSONObject(s);
        return root.getJSONArray("classes");
    }
    public static JSONArray parseElement(String s, String elem) throws JSONException {
        JSONObject root = new JSONObject(s);
        return root.getJSONArray(elem);
    }

    public static void d(String tagActivity, String message){
        Log.d(tagActivity, message);
    }

    public static void e(String tagActivity, String message) {
        Log.e(tagActivity, message);
    }


    public static String getUriSegment(Uri uri, int position){
        List<String> segments = uri.getPathSegments();
        return segments.get(position);
    }
}

