package isel.pdm.serie1.birthdayreminder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kadete on 21/10/2014.
 */
public class BirthdayItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String TITLE = "title";
    public final static String DATE = "date";
    public final static String IMAGE = "image";

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "MMMM-dd", Locale.US);

    private String mTitle = new String();
    private Bitmap mImage;
    private Date mDate = new Date();


    BirthdayItem(String title, Date date) {
        this.mTitle = title;
        this.mDate = date;
    }

    BirthdayItem(Intent intent) {

        mTitle = intent.getStringExtra(TITLE);

        mImage = intent.getParcelableExtra(IMAGE);

        try {
            mDate = FORMAT.parse(intent.getStringExtra(DATE));
        } catch (ParseException e) {
            mDate = new Date();
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() { return mDate; }

    public void setDate(Date date) {
        mDate = date;
    }

    public Bitmap getImageBitmap() {
        return mImage;
    }

    public void setImageId(Bitmap bitmap) { mImage = bitmap; }

    public static void packageIntent(Intent intent, String title, Bitmap bitmapPhoto ,String date) {
        intent.putExtra(TITLE, title);
        intent.putExtra(IMAGE, bitmapPhoto);
        intent.putExtra(DATE, date);
    }

    public String toString() {
        return mTitle  + ITEM_SEP + FORMAT.format(mDate);
    }

    public String toLog() {
        return "Title: " + mTitle + ITEM_SEP + "Date: " + FORMAT.format(mDate);
    }



}
