package pt.isel.pdm.grupo17.thothnews.models;

import android.database.Cursor;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class ThothNew implements Serializable {

     static final String ITEM_SEP = System.getProperty("line.separator");
    static final int READ = 1;

    long _id;
    String title;
    Date _when = new Date();
    String _content;
    Boolean _read = false;

    public long getID() {
        return _id;
    }
    public void setID(long id) {
        _id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Date getWhen() {
        return _when;
    }
    public void setWhen(Date when){_when = when;}
    public String getFormattedWhen() {
        return DateUtils.SHOW_DATE_FORMAT.format(_when);
    }
    public String getShortWhen() {
        return DateUtils.SHOW_SHORT_DATE_FORMAT.format(_when);
    }

    public Boolean getRead() {
        return _read;
    }
    public void setRead(Boolean read) {
        _read = read;
    }

    public String getContent() {
        return _content;
    }
    public void setContent(String _content) {
        this._content = _content;
    }

    public ThothNew(){}

    public ThothNew(long id, String title, Date when, Boolean read, String content){
        _id = id;
        this.title = title;
        _when = when;
        _read = read;
        _content = content;
    }

    public static ThothNew fromCursor(Cursor cursor){

        Date when = new Date();
        try {
            String whenStr = cursor.getString(cursor.getColumnIndex(ThothContract.News.WHEN_CREATED));
            when = DateUtils.SAVE_DATE_FORMAT.parse(whenStr);
        } catch (ParseException e) {
            d(TAG_ADAPTER, "Error on Parse Date >> NewsAdapter.SwapCursor");
        }

        return new ThothNew(
                cursor.getLong(cursor.getColumnIndex(ThothContract.News._ID)),
                cursor.getString(cursor.getColumnIndex(ThothContract.News.TITLE)),
                when,
                (cursor.getString(cursor.getColumnIndex(ThothContract.News.READ)).equals(READ)),
                cursor.getString(cursor.getColumnIndex(ThothContract.News.CONTENT))
        );
    }

    public String toString() {
        return _id + ITEM_SEP + title + ITEM_SEP + DateUtils.SAVE_DATE_FORMAT.format(_when) + ITEM_SEP + _read+ ITEM_SEP + _content;
    }
}