package isel.pdm.serie1.thothNews;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static isel.pdm.serie1.thothNews.ThothClassNewListItem.Status.NOTREAD;
import static isel.pdm.serie1.thothNews.Utils.readAllFrom;

/**
 * Created by Kadete on 28/10/2014.
 */
public class ExtractorThothNews extends AsyncTask<String, Void, ArrayList<ThothClassNewListItem>> {

    final static String urlString = "http://thoth.cc.e.ipl.pt/api/v1/classes/";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ThothClassNewListItem> doInBackground(String ... classId) {
        try {
            ArrayList<ThothClassNewListItem> newItems = new ArrayList<ThothClassNewListItem>();

            URL url = new URL(urlString + classId[0] + "/newsitems");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);

                for(ThothClassNewListItem newItem : parseFrom(data))
                    newItems.add(newItem);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            } finally {
                c.disconnect();
            }

            return newItems;
        } catch (IOException e) {
            return null;
        }
    }

    private ArrayList<ThothClassNewListItem> parseFrom(String s) throws JSONException, ParseException {
        JSONObject root = new JSONObject(s);
        JSONArray jnews = root.getJSONArray("newsItems");
        ArrayList<ThothClassNewListItem> news = new ArrayList<ThothClassNewListItem>(jnews.length());
        for (int i = 0; i < jnews.length(); ++i) {

            JSONObject jnew = jnews.getJSONObject(i);

            int id = jnew.getInt("id");
            String title = jnew.getString("title");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date when = dateFormat.parse(jnew.getString("when"));
            String self = jnew.getJSONObject("_links").getString("self");

            news.add(new ThothClassNewListItem(id, title, when, self));
        }
        return news;
    }
}


class LinksClassNewListItem {
    public String self;
    LinksClassNewListItem(String self){
        this.self = self;
    }
}

class ThothClassNewListItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public enum Status {
        NOTREAD, READ
    };

    private int _id;
    private String _title;
    private Date _when = new Date();
    private LinksClassNewListItem _links;
    private Status _status = NOTREAD;

    ThothClassNewListItem(int id, String title, Date when, Status status){

        _id = id;
        _title = title;
        _when = when;
        _status = status;
    }

    ThothClassNewListItem(int id, String title, Date when, String self){
        _id = id;
        _title = title;
        _when = when;
        _links = new LinksClassNewListItem(self);
    }

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

    public char[] GetInfoToStore(String classId) {
        String info = String.format("%s%s%s", classId, ITEM_SEP, toString());
        char[] cInfo = info.toCharArray();
        return cInfo;
    }

    public String toString() {
        return _id + ITEM_SEP + _title + ITEM_SEP + Utils.SIMPLE_DATE_FORMAT.format(_when) + ITEM_SEP + _status;
    }

    public String toLog() {
        return "Id: " + _id + ITEM_SEP + "FullName: " + _title + ITEM_SEP + "Teacher: " + _when;
    }
}
