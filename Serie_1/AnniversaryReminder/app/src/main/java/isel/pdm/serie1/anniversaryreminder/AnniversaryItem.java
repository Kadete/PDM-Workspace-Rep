package isel.pdm.serie1.anniversaryreminder;

import android.content.Intent;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kadete on 21/10/2014.
 */
public class AnniversaryItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String NAME = "name";
    public final static String DATE = "date";
    public final static String IMAGE = "image";

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "MMMM-dd", Locale.US);

    private String mName = new String();
    private Bitmap mImage;
    private Date mDate = new Date();


    public AnniversaryItem(String name, Date date) {
        this.mName = name;
        this.mDate = date;
    }

    AnniversaryItem(Intent intent) {

        mName = intent.getStringExtra(NAME);

        mImage = intent.getParcelableExtra(IMAGE);

        String date = intent.getStringExtra(DATE);
        mDate = getDateFromData(date);
    }

    private Date getDateFromData(String dateString){
        String[] anniversaryInfo = dateString.split(":");

        int year = Integer.valueOf(anniversaryInfo[0]);
        int month = Integer.valueOf(anniversaryInfo[1]);
        month--;
        int day = Integer.parseInt(anniversaryInfo[2],10);

        return new Date(year, month, day);
    }

    public String getName() {
        return mName;
    }

    public void setTitle(String title) {
        mName = title;
    }

    public Date getDate() {

        return mDate;

    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Bitmap getImageBitmap() {
        return mImage;
    }

    public void setImageId(Bitmap bitmap) { mImage = bitmap; }

    public static void packageIntent(Intent intent, String title, Bitmap bitmapPhoto ,String date) {
        intent.putExtra(NAME, title);
        intent.putExtra(IMAGE, bitmapPhoto);
        intent.putExtra(DATE, date);
    }

    public String toString() {
        return mName + ITEM_SEP + FORMAT.format(mDate);
    }

    public String toLog() {
        return "Title: " + mName + ITEM_SEP + "Date: " + FORMAT.format(mDate);
    }



}
