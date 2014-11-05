package isel.pdm.serie1.anniversaryreminder;

import android.net.Uri;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

import static isel.pdm.serie1.anniversaryreminder.Utils.*;

/**
 * Created by Kadete on 21/10/2014.
 */
public class AnniversaryItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    private String mName = new String();
    private Date mDate = new Date();
    private Uri thumbnailUri;
    private int daysLeft;

    public AnniversaryItem(String name, Date date, Uri thumbnailUri) {
        this.mName = name;
        this.mDate = date;
        this.thumbnailUri = thumbnailUri;
        Calendar calCurr = Calendar.getInstance();
        Calendar calNext = Calendar.getInstance();
        calNext.setTime(mDate);

        if(calNext.after(calCurr)){
            daysLeft = (calNext.get(Calendar.DAY_OF_MONTH) -(calCurr.get(Calendar.DAY_OF_MONTH)));
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

    public String toString() {
        return mName + ITEM_SEP + SHOW_DATE_FORMATTER.format(mDate);
    }
    public String toLog() {
        return "Title: " + mName + ITEM_SEP + "Date: " + SHOW_DATE_FORMATTER.format(mDate) + ITEM_SEP + thumbnailUri;
    }



}
