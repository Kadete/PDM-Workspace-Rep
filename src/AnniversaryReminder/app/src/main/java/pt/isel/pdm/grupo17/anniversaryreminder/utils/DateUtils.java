package pt.isel.pdm.grupo17.anniversaryreminder.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils{
    public static final SimpleDateFormat SHOW_DATE_FORMATTER = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(),"dd MMMM"));
    public static final SimpleDateFormat SAVE_DATE_FORMATTER = new SimpleDateFormat("yyyy:MM:dd");

    /**
     * Converts month between 1 and 12 to a {@link java.util.Calendar Calendar} month constant.<br>
     * {@link java.util.Calendar Calendar} month constants start at 0 instead of 1, so this auxiliary method
     * does this adjustment.
     * @param month month of a gregorian calendar
     * @throws java.lang.IllegalArgumentException if <code>month</code> not between 1 and 12
     * @return Correct  {@link java.util.Calendar Calendar} month constant
     */
    public static int getCalendarMonth(int month){
        if(month<1 || month > 12){
            throw new IllegalArgumentException("Month Value must be between 1 and 12!");
        }
        return month-1;
    }

    public static int[] parseDateFromString(String date, String regExpression){
        String[] sArr = date.split(regExpression);
        int[] dateValues = new int[sArr.length];
        for(int i=0;i<dateValues.length;++i){
            dateValues[i]= Integer.valueOf(sArr[i]);
        }
        return dateValues;
    }
}
