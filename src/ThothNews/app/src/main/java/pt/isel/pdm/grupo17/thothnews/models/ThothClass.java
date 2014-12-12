package pt.isel.pdm.grupo17.thothnews.models;

import android.database.Cursor;

import java.io.Serializable;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class ThothClass implements Serializable{

    public static final String ITEM_SEP = System.getProperty("line.separator");

    long _id;
    String _fullname;
    String _courseName;
    String _lectiveSemester;
    String _className;
    String _teacherName;
    long _teacherID;

    public long getID() {
        return _id;
    }
    public void setID(long _id) {
        this._id = _id;
    }

    public String getFullName() {
        return _fullname;
    }
    public void setFullName(String _fullname) {
        this._fullname = _fullname;
    }

    public String getCourseName() {
        return _courseName;
    }
    public void setCourseName(String courseName) {
        this._courseName = courseName;
    }

    public String getLectiveSemester() {
        return _lectiveSemester;
    }
    public void setLectiveSemester(String lectiveSemester) {
        this._lectiveSemester = lectiveSemester;
    }

    public String getClassName() {
        return _className;
    }
    public void setClassName(String className) {
        this._className = className;
    }

    public String getTeacherName() {
        return _teacherName;
    }
    public void setTeacherName(String teacherID) {
        this._teacherName = teacherID;
    }

    public long getTeacherID() {
        return _teacherID;
    }
    public void setTeacherID(long teacherID) {
        this._teacherID = teacherID;
    }

    public ThothClass(){}

    public ThothClass(long id, String name, String teacher){
        _id = id;
        _fullname = name;
        _teacherName = teacher;
    }
    public ThothClass(long id, String fullName,String courseName,String lectiveSemester
            ,String className, String teacherName, long teacherID){
        _id = id;
        _fullname = fullName;
        _courseName = courseName;
        _lectiveSemester = lectiveSemester;
        _className = className;
        _teacherName = teacherName;
        _teacherID = teacherID;
    }

    public static ThothClass fromCursor(Cursor cursor) {
        return new ThothClass(
            cursor.getLong(cursor.getColumnIndex(ThothContract.Classes._ID)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Classes.FULL_NAME)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Classes.COURSE)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Classes.SEMESTER)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Classes.SHORT_NAME)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Classes.TEACHER_NAME)),
            cursor.getLong(cursor.getColumnIndex(ThothContract.Classes.TEACHER_ID))
        );
    }

    public String toString() {
        return _id + ITEM_SEP + _fullname + ITEM_SEP + _teacherName;
    }
}