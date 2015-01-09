package pt.isel.pdm.grupo17.thothnews.utils;

import android.content.ContentValues;
import android.content.Context;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class ResolverUtils {

    public static void updateNew(Context context, long newID, boolean read){
        ContentValues values = new ContentValues();
        values.put(ThothContract.News.READ, (read) ? SQLiteUtils.TRUE : SQLiteUtils.FALSE);
        if(newID > 0)
            context.getContentResolver().update(UriUtils.News.parseNewID(newID), values, null, null );
    }

    public static void updateWorkItem(Context context, long eventId, String workItemID){
        ContentValues values = new ContentValues();
        values.put(ThothContract.WorkItems.EVENT_ID, eventId);
        String where = ThothContract.WorkItems._ID + " = ?";
        String []whereArgs = new String[]{workItemID};
        context.getContentResolver().update(UriUtils.WorkItems.parseWorkItemID(Long.parseLong(workItemID)), values, where, whereArgs);
    }
}
