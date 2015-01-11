package pt.isel.pdm.grupo17.thothnews.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import java.util.TimeZone;

import static android.provider.CalendarContract.Events;

public class CalendarUtils {

    public static final long INVALID_EVENT_ID = -1;

    public static void editAppointment(Context context, long eventId)
    {
        Uri CALENDAR_URI = Events.CONTENT_URI;
        Uri workItemUri = ContentUris.withAppendedId(CALENDAR_URI, eventId);
        context.startActivity(new Intent(Intent.ACTION_VIEW, workItemUri));
    }

    public static int deleteAppointment(Context context, long eventId)
    {
        Uri CALENDAR_URI = Events.CONTENT_URI;
        Uri uri = ContentUris.withAppendedId(CALENDAR_URI, eventId);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static long addAppointment(Context context, String title, String clazz, long endDate) {

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[] { CalendarContract.Calendars._ID };
        int calendarID = -1;
        try(Cursor calendarCursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (calendarCursor.moveToNext())
                calendarID = calendarCursor.getInt(calendarCursor.getColumnIndex(CalendarContract.Calendars._ID));
        }
        if (calendarID == -1)
            return INVALID_EVENT_ID;

        ContentValues eventValues = new ContentValues();
        eventValues.put(Events.CALENDAR_ID, calendarID);
        eventValues.put(Events.TITLE, clazz  + " - " +  title);
        eventValues.put(Events.STATUS, Events.STATUS_CONFIRMED);
        eventValues.put(Events.EVENT_LOCATION, "Thoth Platform");
        eventValues.put(Events.DTSTART, endDate);
        eventValues.put(Events.DTEND, endDate);
        eventValues.put(Events.ALL_DAY, 1);
        eventValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Uri l_eventUri;
        if (Build.VERSION.SDK_INT >= 8) {
            l_eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            l_eventUri = Uri.parse("content://calendar/events");
        }
        Uri eventUri = context.getContentResolver().insert(l_eventUri, eventValues);
        if(eventUri == null)
            return INVALID_EVENT_ID;

        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        /***************** Event: Reminder(with alert) Adding reminder to event *******************/
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 5);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

        return eventID;

    }
}
