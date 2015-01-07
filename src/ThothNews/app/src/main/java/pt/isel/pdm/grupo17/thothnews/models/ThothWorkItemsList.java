package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThothWorkItemsList implements Serializable {

    private List<ThothWorkItem> mWorkItems = new ArrayList<>();

    public ThothWorkItemsList(){
    }

    public List<ThothWorkItem> getItems(){
        return mWorkItems;
    }

    public void add(ThothWorkItem workItem) {
        mWorkItems.add(workItem);
    }

    public void clear() {
        mWorkItems.clear();
    }

    public Object get(int position) {
        return mWorkItems.get(position);
    }
}
