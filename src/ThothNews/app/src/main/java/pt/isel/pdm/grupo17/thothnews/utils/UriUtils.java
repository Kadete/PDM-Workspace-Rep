package pt.isel.pdm.grupo17.thothnews.utils;

import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

/**
 * Created by Kadete on 29/11/2014.
 */
public class UriUtils {

    public static class Classes {
        public static Uri parseNewsFromClasseID(long classeID){
            return Uri.parse(String.format("%s/%d/news", ThothContract.Clazz.CONTENT_URI, classeID));
        }

        public static Uri parseClasseID(long classeID){
            return Uri.parse(String.format("%s/%d", ThothContract.Clazz.CONTENT_URI, classeID));
        }

    }

    public static class News {
        public static Uri parseFromNewID(long newID){
            return Uri.parse(String.format("%s/%d", ThothContract.News.CONTENT_URI, newID));
        }
    }

}
