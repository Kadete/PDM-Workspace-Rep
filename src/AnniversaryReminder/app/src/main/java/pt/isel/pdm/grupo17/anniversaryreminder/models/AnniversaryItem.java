package pt.isel.pdm.grupo17.anniversaryreminder.models;

import android.net.Uri;

import java.util.Calendar;
import java.util.Date;

import pt.isel.pdm.grupo17.anniversaryreminder.utils.DateUtils;

import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.*;


public class AnniversaryItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    private String id;
    private String mName = new String();
    private Date mDate = new Date();
    private Uri thumbnailUri;
    private int daysLeft;

    public AnniversaryItem(String id,String name, Date date, Uri thumbnailUri) {
        this.id = id;
        this.mName = name;
        this.mDate = date;
        this.thumbnailUri = thumbnailUri;
        Calendar calCurr = Calendar.getInstance();
        Calendar calNext = Calendar.getInstance();
        calNext.setTime(mDate);

        if(calNext.after(calCurr)){
            daysLeft = (calNext.get(Calendar.DAY_OF_YEAR) -(calCurr.get(Calendar.DAY_OF_YEAR)));
        }
    }

    public String getName() {
        return mName;
    }
    public Date getDate() { return mDate; }
    public Uri getThumbnailUri() { return thumbnailUri; }
    public int getDaysLeft() {
        return daysLeft;
    }
    public String getId() { return id; }
    public String toString() {
        return mName + ITEM_SEP + DateUtils.SHOW_DATE_FORMATTER.format(mDate);
    }
//    public String toLog() {
//        return "Title: " + mName + ITEM_SEP + "Date: " + DateUtils.SHOW_DATE_FORMATTER.format(mDate) + ITEM_SEP + thumbnailUri;
//    }

}
