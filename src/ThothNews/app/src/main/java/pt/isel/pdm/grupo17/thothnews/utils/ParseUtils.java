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

    public static final String TAG_ACTIVITY = "TAG_ACTIVITY";
    public static final String TAG_ADAPTER = "TAG_ADAPTER";
    public static final String TAG_ASYNC_TASK = "TAG_ASYNC_TASK";
    public static final String TAG_BROADCAST = "TAG_BROADCAST";
    public static final String TAG_FRAGMENT = "TAG_FRAGMENT";
    public static final String TAG_MODEL = "TAG_MODEL";
    public static final String TAG_UTILS = "TAG_UTILS";

    public static final String CLASS_ID = "id";
    public static final String CLASS_FULLNAME = "fullName";
    public static final String CLASS_COURSE_NAME = "courseUnitShortName";
    public static final String CLASS_LECTIVE_SEMESTER = "lectiveSemesterShortName";
    public static final String CLASS_NAME = "className";
    public static final String CLASS_TEACHER = "mainTeacherShortName";

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
            clazz._id = jclass.getInt(CLASS_ID);
            clazz._fullname = jclass.getString(CLASS_FULLNAME);
            clazz._courseName =  jclass.getString(CLASS_COURSE_NAME);
            clazz._lectiveSemester = jclass.getString(CLASS_LECTIVE_SEMESTER);
            clazz._className = jclass.getString(CLASS_NAME);
            clazz._teacher = jclass.getString(CLASS_TEACHER);
            classes[i] = clazz;
        }
        return classes;
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

