package pt.isel.pdm.grupo17.anniversaryreminder.models;

import android.net.Uri;

import java.util.Calendar;
import java.util.Date;

import pt.isel.pdm.grupo17.anniversaryreminder.utils.DateUtils;


public class AnniversaryItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    private static final int DAYS_PER_YEAR = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR);
    public static final int INVALID_DATE = -1;

    private String id;
    private String mName;
    private Date mDate = new Date();
    private Uri thumbnailUri;

    public AnniversaryItem(String id,String name, Date date, Uri thumbnailUri) {
        this.id = id;
        this.mName = name;
        this.mDate = date;
        this.thumbnailUri = thumbnailUri;
    }

    public String getName() {
        return mName;
    }
    public Date getDate() { return mDate; }
    public Uri getThumbnailUri() { return thumbnailUri; }

    public int getDaysLeft() {

        Calendar calCurr = Calendar.getInstance();
        Calendar calAnniv = Calendar.getInstance();
        calAnniv.setTime(mDate);

        if(calAnniv.after(calCurr)){
            int currDayOfYear = calCurr.get(Calendar.DAY_OF_YEAR);
            int annivDayOfYear = calAnniv.get(Calendar.DAY_OF_YEAR);

            switch (calAnniv.get(Calendar.YEAR) - calCurr.get(Calendar.YEAR)){
                case 0:
                    return annivDayOfYear - currDayOfYear;
                case 1:
                    return DAYS_PER_YEAR - currDayOfYear + annivDayOfYear;
            }
        }
        return INVALID_DATE;
    }

    public String getId() { return id; }
    public String toString() {
        return mName + ITEM_SEP + DateUtils.SHOW_DATE_FORMATTER.format(mDate);
    }
//    public String toLog() {
//        return "Title: " + mName + ITEM_SEP + "Date: " + DateUtils.SHOW_DATE_FORMATTER.format(mDate) + ITEM_SEP + thumbnailUri;
//    }

}
