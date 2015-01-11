package pt.isel.pdm.grupo17.thothnews.services;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetDataUtils {
    protected static JSONArray getJSONArrayFromData(String streamData, String elem) throws JSONException {
        JSONObject root = new JSONObject(streamData);
        return root.getJSONArray(elem);
    }

    protected static JSONObject getJSONObjectFromUri(long id, String strUri) throws IOException, JSONException {
        InputStream inputStream = downloadUrlStr(String.format(strUri, id));
        String newsData = readAllFrom(inputStream);
        inputStream.close();
        return new JSONObject(newsData);
    }

    protected static List<Long> getListFromCursor(Cursor c){
        List<Long> ids = new ArrayList<>();
        while (c.moveToNext()){
            ids.add(c.getLong(0));
        }
        return ids;
    }

    protected static String readAllFrom(InputStream is){
        Scanner s = new Scanner(is);
        try{
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : null;
        }finally{
            s.close();
        }
    }

    protected static InputStream downloadUrlStr(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* 10sec */);
        conn.setConnectTimeout(15000 /* 15sec */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}
