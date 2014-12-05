package pt.isel.pdm.grupo17.thothnews.models;

import android.database.Cursor;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class ThothClass {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    long _id;
    String _fullname;
    String _courseName;
    String _lectiveSemester;
    String _className;
    String _teacher;

    public long getID() {
        return _id;
    }
    public void setID(int _id) {
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

    public String getTeacher() {
        return _teacher;
    }
    public void setTeacher(String _teacher) {
        this._teacher = _teacher;
    }

    public ThothClass(){}

    public ThothClass(int id, String name, String teacher){
        _id = id;
        _fullname = name;
        _teacher = teacher;
    }
    public ThothClass(int id, String fullName,String courseName,String lectiveSemester
            ,String className, String teacher){
        _id = id;
        _fullname = fullName;
        _courseName = courseName;
        _lectiveSemester = lectiveSemester;
        _className = className;
        _teacher = teacher;
    }

    public static ThothClass fromCursor(Cursor cursor) {
        return new ThothClass(
            cursor.getInt(cursor.getColumnIndex(ThothContract.Clazz._ID)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.FULL_NAME)),
            cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.TEACHER))
        );
    }

    public String toString() {
        return _id + ITEM_SEP + _fullname + ITEM_SEP + _teacher;
    }
}