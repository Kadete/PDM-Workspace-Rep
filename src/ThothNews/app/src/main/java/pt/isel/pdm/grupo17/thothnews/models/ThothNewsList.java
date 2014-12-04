package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThothNewsList implements Serializable {

    public static List<ThothNew> NEWS = new ArrayList<ThothNew>();

    public ThothNewsList(){
    }

    public List<ThothNew> getItems(){
        return NEWS;
    }

    public void add(ThothNew thothNew) {
        NEWS.add(thothNew);
    }

    public void clear() {
        NEWS.clear();
    }

    public Object get(int position) {
        return NEWS.get(position);
    }
}
