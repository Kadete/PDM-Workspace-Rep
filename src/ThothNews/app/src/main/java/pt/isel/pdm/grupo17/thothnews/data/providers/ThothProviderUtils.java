package pt.isel.pdm.grupo17.thothnews.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.List;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class ThothProviderUtils {

    /**
     * Gets a number format ID from a URI, in a segment position
     * @return true if value is a valid long
     * @throws java.lang.NumberFormatException if there's no valid ID on the URI segment
     */
    protected static long getID(Uri uri, int position){
        String segment = getUriSegment(uri, position);
        long number;
        try{
            number = Long.parseLong(segment);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid content URI: "+uri.toString());
        }
        return number;
    }

    protected static String getName(Uri uri, int position){
        return getUriSegment(uri, position);
    }

    public static String getUriSegment(Uri uri, int position){
        List<String> segments = uri.getPathSegments();
        return segments.get(position);
    }

    /**
     * getting all students assigned to a single classe
     */
    protected static Cursor getCursorAllStudentsByClass(long classID, SQLiteOpenHelper _helper) {
        String selectQuery = "SELECT * FROM " + ThothContract.Students.TABLE_NAME + " st, "
                + ThothContract.Classes.TABLE_NAME + " cs, " + ThothContract.Classes_Students.TABLE_NAME + " classes_students WHERE cs."
                + ThothContract.Classes._ID + " = '" + classID + "'" + " AND st." + ThothContract.Students._ID
                + " = " + "classes_students." + ThothContract.Classes_Students.KEY_STUDENT_ID + " AND cs." + ThothContract.Classes._ID + " = "
                + "classes_students." + ThothContract.Classes_Students.KEY_CLASS_ID;

        SQLiteDatabase db = _helper.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    protected static Uri insertNew(SQLiteDatabase db, ContentValues values){
        long insertResult;
        Long classID = values.getAsLong(ThothContract.News.CLASS_ID);
        if(classID == null){
            throw new IllegalArgumentException("Missing class ID associated with this new");
        }
        values.put(ThothContract.News.READ,false);
        insertResult = db.insert(ThothContract.News.TABLE_NAME, null, values);
        return Uri.parse(ThothContract.News.CONTENT_URI + "/" + insertResult);
    }

    protected static Uri insertStudent(SQLiteDatabase db, ContentValues values){
        long insertResult = db.insert(ThothContract.Students.TABLE_NAME, null, values);
        return Uri.parse(ThothContract.Students.CONTENT_URI + "/"+insertResult);
    }

    protected static Uri insertTeacher(SQLiteDatabase db, ContentValues values){
        long insertResult = db.insert(ThothContract.Teachers.TABLE_NAME, null, values);
        return Uri.parse(ThothContract.Students.CONTENT_URI + "/" + insertResult);
    }

    protected static Uri insertClassesStudent(SQLiteDatabase db, ContentValues values) {
        long insertResult;
        insertResult = db.insert(ThothContract.Classes_Students.TABLE_NAME, null, values);
        return Uri.parse(ThothContract.Classes_Students.CONTENT_URI + "/"+insertResult);
    }

    protected static Uri insertWorkItem(SQLiteDatabase db, ContentValues values){
        long insertResult = db.insert(ThothContract.WorkItems.TABLE_NAME, null, values);
        return Uri.parse(ThothContract.WorkItems.CONTENT_URI + "/" + insertResult);
    }

}
