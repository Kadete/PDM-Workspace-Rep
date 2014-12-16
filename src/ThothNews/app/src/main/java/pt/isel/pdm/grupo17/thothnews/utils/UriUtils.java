package pt.isel.pdm.grupo17.thothnews.utils;

import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class UriUtils {

    public static class Classes {
        public static Uri parseNewsFromClassID(long classID){
            Uri uri = Uri.parse(String.format("%s/%d/news", ThothContract.Classes.CONTENT_URI, classID));
            return uri;
        }

        public static Uri parseParticipantsFromClassID(long classID){
            Uri uri = Uri.parse(String.format("%s/%d/participants", ThothContract.Classes.CONTENT_URI, classID));
            return uri;
        }

        public static Uri parseClass(long classID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.Classes.CONTENT_URI, classID));
            return uri;
        }
    }

    public static class News {
        public static Uri parseNewID(long newID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.News.CONTENT_URI, newID));
            return uri;
        }
    }

    public static class Teachers {
        public static Uri parseTeacherID(long teacherID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.Teachers.CONTENT_URI, teacherID));
            return uri;
        }
    }
    public static class Students {
        public static Uri parseStudentID(long studentID){
            Uri uri = Uri.parse(String.format("%s/%d", ThothContract.Students.CONTENT_URI, studentID));
            return uri;
        }
    }
}
