package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThothStudentsList implements Serializable {

    private static List<ThothStudent> PARTICIPANTS = new ArrayList<ThothStudent>();

    public ThothStudentsList(){
    }

    public List<ThothStudent> getItems(){
        return PARTICIPANTS;
    }

    public void add(ThothStudent participant) {
        PARTICIPANTS.add(participant);
    }

    public void clear() {
        PARTICIPANTS.clear();
    }

    public Object get(int position) {
        return PARTICIPANTS.get(position);
    }
}
