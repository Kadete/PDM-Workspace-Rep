package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kadete on 02/12/2014.
 */
public class ThothNewList implements Serializable {
    List<ThothNew> _list = new ArrayList<ThothNew>();

    public ThothNewList(){
    }

    public List<ThothNew> getItems(){
        return _list;
    }

    public void add(ThothNew list) {
        _list.add(list);
    }

    public void clear() {
        _list.clear();
    }

    public Object get(int position) {
        return _list.get(position);
    }
}
