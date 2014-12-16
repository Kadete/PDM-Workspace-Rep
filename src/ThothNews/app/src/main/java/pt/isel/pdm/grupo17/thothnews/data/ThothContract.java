package pt.isel.pdm.grupo17.thothnews.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ThothContract {
    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "pt.isel.pdm.grupo17.thothnews";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CLASSES = "classes";
    public static final String PATH_CLASSES_SEARCH = "classesSearch";
    public static final String PATH_NEWS = "news";
    public static final String PATH_TEACHERS = "teachers";
    public static final String PATH_STUDENTS = "students";
    public static final String PATH_CLASSES_STUDENTS = "classesStudents";
    public static final String PATH_CLASSES_ENROLLED = "enrolled";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_BOOLEAN = " BOOLEAN";
    private static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String COMMA_SEP = ", ";

    public static class Classes implements BaseColumns{

        /**
         * MIME type for lists of classes.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.thothprovider.classes";
        /**
         * MIME type for individual class.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.thothprovider.classes";
        /**
         * Fully qualified URI for "classes" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSES).build(); /**
         * Fully qualified URI for search "classes" resources.
         */
        public static final Uri SEARCH_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSES_SEARCH).build();
/**
         * Fully qualified URI for enrolled "classes" resources.
         */
        public static final Uri ENROLLED_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSES)
                .appendPath(PATH_CLASSES_ENROLLED).build();


        public static final String TABLE_NAME = "classes",
                FULL_NAME = "fullName",
                COURSE = "courseShortName",
                SEMESTER = "lectiveSemester",
                SHORT_NAME = "className",
                TEACHER_NAME = "mainTeacherShortName",
                ENROLLED = "enrolled",
                UNREAD_NEWS = "unreadNews",
                LINKS = "_links",
                TEACHER_ID = "teacherId";

        static final String CREATE_QUERY = "CREATE TABLE " + Classes.TABLE_NAME +
                " ("+ Classes._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP + Classes.FULL_NAME+ TYPE_TEXT +COMMA_SEP
                + Classes.COURSE + TYPE_TEXT + COMMA_SEP + Classes.SEMESTER + TYPE_TEXT + COMMA_SEP
                + Classes.SHORT_NAME + TYPE_TEXT + COMMA_SEP + Classes.TEACHER_NAME + TYPE_TEXT + COMMA_SEP
                + Classes.ENROLLED + TYPE_BOOLEAN + COMMA_SEP + Classes.UNREAD_NEWS + TYPE_BOOLEAN + COMMA_SEP
                + Classes.LINKS + TYPE_TEXT + COMMA_SEP + Classes.TEACHER_ID + TYPE_INTEGER + ")";
    }

    public static class News implements BaseColumns {
        /**
         * MIME type for lists of news.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.thothprovider.news";
        /**
         * MIME type for individual new
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.thothprovider.news";
        /**
         * Fully qualified URI for "News" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String TABLE_NAME = "news",
                TITLE = "title",
                WHEN_CREATED = "when_created",
                CONTENT = "content",
                READ = "_read",
                CLASS_ID = "classId";

        static final String CREATE_QUERY = "CREATE TABLE " + News.TABLE_NAME + " ("
                + News._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP + News.TITLE + TYPE_TEXT + COMMA_SEP
                + News.WHEN_CREATED + TYPE_TEXT + COMMA_SEP + News.CONTENT + TYPE_TEXT + COMMA_SEP
                + News.READ + TYPE_BOOLEAN + COMMA_SEP + News.CLASS_ID + TYPE_INTEGER + ")";
    }

    public static class Students implements BaseColumns {
        /**
         * MIME type for lists of students.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.thothprovider.students";
        /**
         * MIME type for individual student
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.thothprovider.students";
        /**
         * Fully qualified URI for "Students" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS).build();

        public static final String TABLE_NAME = "students",
//                NUMBER = "number", // id == number
                FULL_NAME = "studentFullName",
                ACADEMIC_EMAIL = "academicEmail",
//                AVATAR_PATH = "avatarPath", //inside location
                AVATAR_URL = "avatarUrl", //external location
                ENROLLED_DATE = "enrollmentDate",
                CLASS_ID = "classId";

        static final String CREATE_QUERY = "CREATE TABLE " + Students.TABLE_NAME + " ("
                + Students._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP
                + Students.FULL_NAME + TYPE_TEXT + COMMA_SEP + Students.ACADEMIC_EMAIL + TYPE_TEXT + COMMA_SEP
                + Students.AVATAR_URL + TYPE_TEXT + COMMA_SEP + Path_Auxiliar.AVATAR_PATH + TYPE_TEXT + COMMA_SEP
                + Students.ENROLLED_DATE + TYPE_TEXT + COMMA_SEP
                + Students.CLASS_ID + TYPE_INTEGER + ")";
    }

    public static class Teachers implements BaseColumns {
        /**
         * MIME type for lists of teachers.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.thothprovider.teachers";
        /**
         * MIME type for individual teacher
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.thothprovider.teachers";
        /**
         * Fully qualified URI for "Teachers" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEACHERS).build();

        public static final String TABLE_NAME = "teachers",
                NUMBER = "number",
                SHORT_NAME = "shortName",
                FULL_NAME = "fullName",
                ACADEMIC_EMAIL = "academicEmail",
//                AVATAR_PATH = "avatarPath", //inside location
                AVATAR_URL = "avatarUrl", //external location
                LINKS = "_linsk";

        static final String CREATE_QUERY = "CREATE TABLE " + Teachers.TABLE_NAME + " ("
                + Teachers._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP
                + Teachers.NUMBER + TYPE_INTEGER + COMMA_SEP + Teachers.SHORT_NAME + TYPE_TEXT + COMMA_SEP
                + Teachers.FULL_NAME + TYPE_TEXT + COMMA_SEP + Teachers.ACADEMIC_EMAIL + TYPE_TEXT + COMMA_SEP
                + Teachers.AVATAR_URL + TYPE_TEXT + COMMA_SEP + Path_Auxiliar.AVATAR_PATH + TYPE_TEXT + COMMA_SEP
                + Teachers.LINKS + TYPE_TEXT + ")";
    }

    public static class Classes_Students implements BaseColumns {
        public static final String
                KEY_CLASS_ID = "class_id",
                KEY_STUDENT_ID = "student_id",
                GROUP = "currentGroup";

        public static final String TABLE_NAME = "classes_students";

        /**
         * Fully qualified URI for combining "Classes" and "Students" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSES_STUDENTS).build();

        static final String CREATE_QUERY = "CREATE TABLE " + Classes_Students.TABLE_NAME + " ("
                + Classes_Students._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP
                + Classes_Students.KEY_CLASS_ID + TYPE_INTEGER + COMMA_SEP + Classes_Students.KEY_STUDENT_ID + COMMA_SEP
                + Classes_Students.GROUP + TYPE_INTEGER + ")";

    }

    public static class Path_Auxiliar {
        public static final String AVATAR_PATH = "avatarPath"; //inside location
    }

}
