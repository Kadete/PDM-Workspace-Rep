package pt.isel.pdm.grupo17.thothnews.models;

import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

public class ThothClassNew{
    public int id;
    public String title;
    public Date when;
    public String content;
    public LinksClass _links;

    public String getFormattedWhen() {
        return DateUtils.SHOW_DATE_FORMAT.format(when);
    }
}
