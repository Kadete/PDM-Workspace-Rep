package pt.isel.pdm.grupo17.thothnews.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils{
    public static final SimpleDateFormat SHOW_DATE_FORMAT = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE dd-MM-yyyy' 'HH:mm"));
    public static final SimpleDateFormat SHOW_SHORT_DATE_FORMAT = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "dd-MM-yyyy"));
    public static final SimpleDateFormat SAVE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");


}
