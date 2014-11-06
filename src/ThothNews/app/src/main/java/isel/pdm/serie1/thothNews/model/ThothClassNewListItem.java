package isel.pdm.serie1.thothNews.model;

import java.util.Date;

import isel.pdm.serie1.thothNews.utils.Utils;

import static isel.pdm.serie1.thothNews.model.ThothClassNewListItem.Status.NOTREAD;

public class ThothClassNewListItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public enum Status {
        NOTREAD, READ
    };

    private int _id;
    private String _title;
    private Date _when = new Date();
    private LinksClassNewListItem _links;
    private Status _status = NOTREAD;

    public ThothClassNewListItem(int id, String title, Date when, Status status){

        _id = id;
        _title = title;
        _when = when;
        _status = status;
    }

    ThothClassNewListItem(int id, String title, Date when, String self){
        _id = id;
        _title = title;
        _when = when;
        _links = new LinksClassNewListItem(self);
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getTitle() {
        return _title;
    }

    public Date getWhen() {
        return _when;
    }
    public String getFormattedWhen() {
        return Utils.SHOW_DATE_FORMAT.format(_when);
    }

    public Status getStatus() {
        return _status;
    }

    public void setStatus(Status status) {
        _status = status;
    }

    public char[] GetInfoToStore(String classId) {
        String info = String.format("%s%s%s", classId, ITEM_SEP, toString());
        char[] cInfo = info.toCharArray();
        return cInfo;
    }

    public String toString() {
        return _id + ITEM_SEP + _title + ITEM_SEP + Utils.SAVE_DATE_FORMAT.format(_when) + ITEM_SEP + _status;
    }

    public String toLog() {
        return "Id: " + _id + ITEM_SEP + "FullName: " + _title + ITEM_SEP + "Teacher: " + _when;
    }
}
