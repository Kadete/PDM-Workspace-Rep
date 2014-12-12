package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;

/**
 * Created by Kadete on 10/12/2014.
 */
public class ThothClassStudent implements Serializable{

    private int _id;
    private long _classID;
    private long _studentID;
    private int _currentGroup;

    public int getGroup() {
        return _currentGroup;
    }
    public void setGroup(int group) {
        this._currentGroup = group;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long get_classID() {
        return _classID;
    }

    public void set_classID(long _classID) {
        this._classID = _classID;
    }

    public long get_studentID() {
        return _studentID;
    }

    public void set_studentID(long _studentID) {
        this._studentID = _studentID;
    }
}
