package pt.isel.pdm.grupo17.anniversaryreminder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem;

import static android.provider.ContactsContract.CommonDataKinds.Event;
import static android.provider.ContactsContract.Contacts;
import static android.provider.ContactsContract.Data;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.d;

/**
 * Created by Kadete And Tiago on 20/11/2014.
 */
public class CursorUtils {

    private static final String TAG_UTILS_CURSOR = "TAG_UTILS_CURSOR";

    public static List<AnniversaryItem> getAnniversaryList(Context context)
    {
        List<AnniversaryItem>  anniversaryItems = new LinkedList<AnniversaryItem>();
        String contactName;
        Date contactAnniDate;
        Uri contactThumbUri;
        String contactID;

        Cursor anniCursor = context.getContentResolver().query(

                Data.CONTENT_URI,
                new String[] {Event.RAW_CONTACT_ID, Contacts.DISPLAY_NAME, Event.DATA, Contacts.PHOTO_THUMBNAIL_URI},
                Data.MIMETYPE + "= '" + Event.CONTENT_ITEM_TYPE
                        +"' AND " + Event.TYPE + "=" + Event.TYPE_ANNIVERSARY,
                null,
                Data.DISPLAY_NAME
        );
        int idCol = anniCursor.getColumnIndexOrThrow(Event.RAW_CONTACT_ID);
        int nameCol = anniCursor.getColumnIndex(Contacts.DISPLAY_NAME);
        int dateCol = anniCursor.getColumnIndex(Event.START_DATE);
        int photoCol = anniCursor.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);

        while(anniCursor.moveToNext())
        {
            contactThumbUri = null;
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            try {
                contactID = anniCursor.getString(idCol);

                contactName = anniCursor.getString(nameCol);
                contactAnniDate = df.parse(anniCursor.getString(dateCol));

                String contactThumbStr = anniCursor.getString(photoCol);
                if(contactThumbStr != null) {
                    contactThumbUri = Uri.parse(contactThumbStr);
                }
                AnniversaryItem ann = new AnniversaryItem(contactID, contactName, contactAnniDate, contactThumbUri);
                anniversaryItems.add(ann);
            } catch (ParseException e) {
                d(TAG_UTILS_CURSOR,e.getMessage());
                e.printStackTrace();
            }

        }
        anniCursor.close();
        return anniversaryItems;
    }

    public static List<AnniversaryItem> getTodayAnniversaryList(Context context) {
        List<AnniversaryItem> todayAnniversaries = new LinkedList<AnniversaryItem>();
        for (AnniversaryItem item :  getAnniversaryList(context)){
            if(isToFilter(item.getDate(), 0))
                todayAnniversaries.add(item);
        }
        return todayAnniversaries;
    }

    public static boolean isToFilter(Date anniversaryDate, int daysToFilter){
        Calendar filterDate = Calendar.getInstance(), anvDate = Calendar.getInstance();
        anvDate.setTime(anniversaryDate);

        filterDate.add(Calendar.DAY_OF_YEAR, daysToFilter);
        Calendar today = Calendar.getInstance();
        return anvDate.get(Calendar.DAY_OF_YEAR)>= today.get(Calendar.DAY_OF_YEAR) && anvDate.before(filterDate);
    }

}