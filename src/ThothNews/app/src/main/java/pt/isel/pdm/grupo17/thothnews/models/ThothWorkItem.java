package pt.isel.pdm.grupo17.thothnews.models;

import android.database.Cursor;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class ThothWorkItem implements Serializable {

    long _id;
    String title;
    Date _startDate = new Date();
    Date _dueDate = new Date();
    String _url;

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

    public Date getStartDate() {
        return _startDate;
    }
    public void setStartDate(Date startDate){startDate = _startDate;}

    public Date getDueDate() {
        return _dueDate;
    }
    public void setDueDate(Date dueDate){dueDate = _dueDate;}

    public String getFormattedWhenStart() {
        return DateUtils.SHOW_DATE_FORMAT.format(_startDate);
    }
    public String getShortWhenStart() {
        return DateUtils.SHOW_SHORT_DATE_FORMAT.format(_startDate);
    }

    public String getFormattedWhenDue() {
        return DateUtils.SHOW_DATE_FORMAT.format(_dueDate);
    }
    public String getShortWhenDue() {
        return DateUtils.SHOW_SHORT_DATE_FORMAT.format(_dueDate);
    }

    public String getUrl() {
        return _url;
    }
    public void setUrl(String _url) {
        this._url = _url;
    }

    public ThothWorkItem(){}

    public ThothWorkItem(long id, String title, Date whenStart, Date whenDue, String url){
        _id = id;
        this.title = title;
        _startDate = whenStart;
        _dueDate = whenDue;
        _url = url;
    }

    public static ThothWorkItem fromCursor(Cursor cursor){

        Date startDate = new Date(), dueDate = new Date();
        try {
            String strStartDate = cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.START_DATE));
            startDate = DateUtils.SAVE_DATE_FORMAT.parse(strStartDate);
            String strDueDate = cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.DUE_DATE));
            dueDate = DateUtils.SAVE_DATE_FORMAT.parse(strDueDate);
        } catch (ParseException e) {
            d(TAG_ADAPTER, "Error on Parse Date >> NewsAdapter.SwapCursor");
        }

        return new ThothWorkItem(
            cursor.getLong(cursor.getColumnIndex(ThothContract.WorkItems._ID)),
            cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.TITLE)),
            startDate,
            dueDate,
            cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.URL))
        );
    }
}