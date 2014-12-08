package pt.isel.pdm.grupo17.thothnews.models;

import android.database.Cursor;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class ThothStudent implements Serializable {

    static final String ITEM_SEP = System.getProperty("line.separator");

    long _id;
    Date _enrolledDate = new Date();
    String _fullName;
    String _email;
    int _currentGroup;
//    AVATAR_URL = "avatarUrl",

    public long getID() {
        return _id;
    }
    public void setID(long id) {
        _id = id;
    }

//    public int getNumber() {
//        return _number;
//    }
//    public void setNumber(int number) {
//        this._number = number;
//    }

    public Date getEnrolledDate() {
        return _enrolledDate;
    }
    public void setEnrolledDate(Date when){
        _enrolledDate = when;}

    public String getFormattedEnrolledDate() {
        return DateUtils.SHOW_DATE_FORMAT.format(_enrolledDate);
    }
    public String getShortEnrolledDate() {
        return DateUtils.SHOW_SHORT_DATE_FORMAT.format(_enrolledDate);
    }

    public String getFullName() {
        return _fullName;
    }
    public void setFullName(String fullName) {
        this._fullName = fullName;
    }

    public String getEmail() {
        return _email;
    }
    public void setEmail(String email) {
        this._email = email;
    }

    public int getGroup() {
        return _currentGroup;
    }
    public void setGroup(int group) {
        this._currentGroup = group;
    }

    public ThothStudent(){}

    public ThothStudent(long id, Date when, String fullName, String email, int group){
        _id = id;
        _enrolledDate = when;
        _fullName = fullName;
        _email = email;
        _currentGroup = group;
    }

    public static ThothStudent fromCursor(Cursor cursor){

        Date whenEnrolled = new Date();
        try {
            String whenStr = cursor.getString(cursor.getColumnIndex(ThothContract.Students.ENROLLED_DATE));
            whenEnrolled = DateUtils.SAVE_DATE_FORMAT.parse(whenStr);
        } catch (ParseException e) {
            d(TAG_ADAPTER, "Error on Parse Date >> ParticipantsAdapter.SwapCursor");
        }

        return new ThothStudent(
            cursor.getLong(cursor.getColumnIndex(ThothContract.Students._ID)),
            whenEnrolled,
            cursor.getString(cursor.getColumnIndex(ThothContract.Students.FULL_NAME)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Students.ACADEMIC_EMAIL)),
            cursor.getInt(cursor.getColumnIndex(ThothContract.Students.GROUP))
        );
    }

    public String toString() {
        return _id + ITEM_SEP + DateUtils.SAVE_DATE_FORMAT.format(_enrolledDate) + ITEM_SEP + _email + ITEM_SEP + _fullName + ITEM_SEP + _currentGroup;
    }

}
