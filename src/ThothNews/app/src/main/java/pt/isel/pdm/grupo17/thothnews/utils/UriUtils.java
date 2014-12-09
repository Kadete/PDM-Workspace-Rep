package pt.isel.pdm.grupo17.thothnews.utils;

import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class UriUtils {

    public static class Classes {
        public static Uri parseNewsFromClassID(long classID){
            Uri uri = Uri.parse(String.format("%s/%d/newsitems", ThothContract.Clazz.CONTENT_URI, classID));
            return uri;
        }

        public static Uri parseParticipantsFromClassID(long classID){
            Uri uri = Uri.parse(String.format("%s/%d/participants", ThothContract.Clazz.CONTENT_URI, classID));
            return uri;
        }

        public static Uri parseClass(long classID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.Clazz.CONTENT_URI, classID));
            return uri;
        }
    }

    public static class News {
        public static Uri parseFromNewID(long newID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.News.CONTENT_URI, newID));
            return uri;
        }
    }

    public static class Teachers {
        public static Uri parseFromTeacherID(long teacherID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.Teacher.CONTENT_URI, teacherID));
            return uri;
        }
    }
}
