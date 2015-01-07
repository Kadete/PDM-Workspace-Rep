package pt.isel.pdm.grupo17.thothnews.utils;

import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class UriUtils {

    public static class Classes {
        public static Uri parseNewsFromClassID(long classID){
            return Uri.parse(String.format("%s/%d/news", ThothContract.Classes.CONTENT_URI, classID));
        }
        public static Uri parseWorkItemsFromClassID(long classID){
            return Uri.parse(String.format("%s/%d/workitems", ThothContract.Classes.CONTENT_URI, classID));
        }

        public static Uri parseParticipantsFromClassID(long classID){
            return Uri.parse(String.format("%s/%d/participants", ThothContract.Classes.CONTENT_URI, classID));
        }

        public static Uri parseClass(long classID){
            return Uri.parse(String.format("%s/%d", ThothContract.Classes.CONTENT_URI, classID));
        }
    }

    public static class News {
        public static Uri parseNewID(long newID){
            return Uri.parse(String.format("%s/%d", ThothContract.News.CONTENT_URI, newID));
        }
    }

//    public static class WorkItems {
//        public static Uri parseWorkItemID(long workItemID){
//            return Uri.parse(String.format("%s/%d", ThothContract.WorkItems.CONTENT_URI, workItemID));
//        }
//    }

    public static class Teachers {
        public static Uri parseTeacherID(long teacherID){
            return Uri.parse(String.format("%s/%d", ThothContract.Teachers.CONTENT_URI, teacherID));
        }
    }
    public static class Students {
        public static Uri parseStudentID(long studentID){
            return Uri.parse(String.format("%s/%d", ThothContract.Students.CONTENT_URI, studentID));
        }
    }
}
