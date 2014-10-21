package isel.pdm.serie1.thothNews;

import java.util.Date;

import static isel.pdm.serie1.thothNews.ThothClassNewItem.Status.NOTREAD;

/**
 * Created by Kadete on 19/10/2014.
 */

public class ThothClassNewItem{

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


    public Status getStatus() {
        return _status;
    }

    public void setStatus(Status status) {
        _status = status;
    }

    public enum Status {
        NOTREAD, READ
    };

    private int _id;
    private String _title;
    private Date _when = new Date();
    private LinksClassNewItem _links;
    private Status _status = NOTREAD;

    ThothClassNewItem(int id, String title, Date when, String self){
        _id = id;
        _title = title;
        _when = when;
        _links = new LinksClassNewItem(self);
    }
}

class LinksClassNewItem{
    public String self;
    LinksClassNewItem(String self){
        this.self = self;
    }

}