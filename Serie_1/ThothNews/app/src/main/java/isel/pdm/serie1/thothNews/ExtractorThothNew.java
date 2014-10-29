package isel.pdm.serie1.thothNews;

import android.os.AsyncTask;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static isel.pdm.serie1.thothNews.Utils.readAllFrom;

/**
 * Created by Kadete on 28/10/2014.
 */
class ThothClassNew{
    public int id;
    public String title;
    public String when;
    public String content;
    public LinksClass _links;
}

class LinksClass{
    public String self;
    public String classNewsItems;
    public String clazz;
    public String root;
}

class ExtractorThothNew extends AsyncTask<String,Void,ThothClassNew> {

    String urlString = "http://thoth.cc.e.ipl.pt/api/v1/newsitems/";

    @Override
    protected ThothClassNew doInBackground(String... arg0) {
        try{

            URL url = new URL(urlString + arg0[0]);
            HttpURLConnection c = (HttpURLConnection)url.openConnection();

            try{
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                return parseFrom(data);

            } finally{
                c.disconnect();
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ThothClassNew parseFrom(String s) throws ParseException {

        JSONObject root;
        ThothClassNew _new = new ThothClassNew();
        try {
            root = new JSONObject(s);
            _new.id = root.getInt("id");
            _new.title = root.getString("title");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            _new.when = dateFormat.parse(root.getString("when")).toString();
            _new.content = String.valueOf(Html.fromHtml(root.getString("content")));
            _new._links = new LinksClass();

            JSONObject links = root.getJSONObject("_links");
            _new._links.self = links.getString("self");
            _new._links.classNewsItems = links.getString("classNewsItems");
            _new._links.clazz = links.getString("class");
            _new._links.root = links.getString("root");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return _new;
    }

}