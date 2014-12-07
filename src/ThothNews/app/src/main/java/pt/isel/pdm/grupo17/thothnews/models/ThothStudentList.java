package pt.isel.pdm.grupo17.thothnews.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kadete on 07/12/2014.
 */
public class ThothStudentList implements Serializable {

    private static List<ThothStudent> STUDENTS = new ArrayList<ThothStudent>();

    public ThothStudentList(){
    }

    public List<ThothStudent> getItems(){
        return STUDENTS;
    }

    public void add(ThothStudent thothNew) {
        STUDENTS.add(thothNew);
    }

    public void clear() {
        STUDENTS.clear();
    }

    public Object get(int position) {
        return STUDENTS.get(position);
    }
}
