package isel.pdm.serie1.thothNews.model;

import java.util.Date;

import isel.pdm.serie1.thothNews.utils.Utils;

public class ThothClassNew{
    public int id;
    public String title;
    public Date when;
    public String content;
    public LinksClass _links;

    public String getFormattedWhen() {
        return Utils.SHOW_DATE_FORMAT.format(when);
    }
}
