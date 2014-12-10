package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThothNewsList implements Serializable {

    private List<ThothNew> mNews = new ArrayList<>();

    public ThothNewsList(){
    }

    public List<ThothNew> getItems(){
        return mNews;
    }

    public void add(ThothNew thothNew) {
        mNews.add(thothNew);
    }

    public void clear() {
        mNews.clear();
    }

    public Object get(int position) {
        return mNews.get(position);
    }

    public int getPosition(ThothNew thothNew) {
        return mNews.indexOf(thothNew);
    }
}
