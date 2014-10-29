package isel.pdm.serie1.thothNews;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static isel.pdm.serie1.thothNews.Utils.readAllFrom;

/**
 * Created by Kadete on 28/10/2014.
 */

class ThothClass {
    public int _id;
    public String _fullname;
    public String _teacher;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_fullname() {
        return _fullname;
    }

    public void set_fullname(String _fullname) {
        this._fullname = _fullname;
    }

    public String get_teacher() {
        return _teacher;
    }

    public void set_teacher(String _teacher) {
        this._teacher = _teacher;
    }

    ThothClass(int id, String name, String teacher){
        _id = id;
        _fullname = name;
        _teacher = teacher;
    }

    ThothClass(){

    }

    public char[] GetInfoToStore() {

        /* TODO */
        return null;
    }
}

class ExtractorThothClasses extends AsyncTask<Set<String>, Void, List<ThothClass>> {

    @Override
    protected List<ThothClass> doInBackground(Set<String>... sets) {

        try {
            List<ThothClass> newItems = new LinkedList<ThothClass>();
            URL url;

            Iterator it = sets[0].iterator();
            while(it.hasNext()){

                url = new URL("http://thoth.cc.e.ipl.pt/api/v1/classes/" + it.next());
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                try {
                    InputStream is = c.getInputStream();
                    String data = readAllFrom(is);
                    newItems.add(parseFrom(data));

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    c.disconnect();
                }
            }
            return newItems;
        } catch (IOException e) {
            return null;
        }
    }
    private ThothClass parseFrom(String s) throws JSONException {
        JSONObject root = new JSONObject(s);

        return new ThothClass(
            root.getInt("id"),
            root.getString("fullName"),
            root.getString("mainTeacherShortName")
        );
    }


}

class QueryThothClasses extends AsyncTask<Void ,Void,ThothClass[]> {

    @Override
    protected ThothClass[] doInBackground(Void... arg0) {
        try {
            URL url = new URL("http://thoth.cc.e.ipl.pt/api/v1/classes/");

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                return parseFrom(data);
            } catch (JSONException e) {
                return null;
            } finally {
                c.disconnect();
            }
        } catch (IOException e) {
            return null;
        }
    }

    private ThothClass[] parseFrom(String s) throws JSONException {
        JSONObject root = new JSONObject(s);
        JSONArray jclasses = root.getJSONArray("classes");
        ThothClass[] classes = new ThothClass[jclasses.length()];
        for (int i = 0; i < jclasses.length(); ++i) {
            JSONObject jclass = jclasses.getJSONObject(i);
            ThothClass clazz = new ThothClass();
            clazz._id = jclass.getInt("id");
            clazz._fullname = jclass.getString("fullName");
            classes[i] = clazz;
        }
        return classes;
    }
}