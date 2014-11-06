package isel.pdm.serie1.thothNews.model;

public class ThothClass {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public int _id;
    public String _fullname;
    public String _teacher;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_fullname() {
        return _fullname;
    }

    public void set_fullname(String _fullname) {
        this._fullname = _fullname;
    }

    public String get_teacher() {
        return _teacher;
    }

    public void set_teacher(String _teacher) {
        this._teacher = _teacher;
    }

    ThothClass(int id, String name, String teacher){
        _id = id;
        _fullname = name;
        _teacher = teacher;
    }

    public String toString() {
        return _id + ITEM_SEP + _fullname + ITEM_SEP + _teacher;
    }

    public String toLog() {
        return "Id: " + _id + ITEM_SEP + "FullName: " + _fullname + ITEM_SEP + "Teacher: " + _teacher;
    }

    public ThothClass(){

    }
}
