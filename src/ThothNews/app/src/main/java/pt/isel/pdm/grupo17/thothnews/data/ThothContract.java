package pt.isel.pdm.grupo17.thothnews.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ThothContract {
    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "pt.isel.pdm.grupo17.thothnews";
    /**
     * Base URI. (content://com.example.android.basicsyncadapter)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CLASSES = "classes";
    public static final String PATH_NEWS = "news";
    public static final String PATH_CLASSES_ENROLLED = "enrolled";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_BOOLEAN = " BOOLEAN";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ", ";

    public static class Clazz implements BaseColumns{

        /**
         * MIME type for lists of classes.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.thothprovider.clazz";
        /**
         * MIME type for individual class.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.thothprovider.clazz";
        /**
         * Fully qualified URI for "classes" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSES).build();
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
            TEACHER = "mainTeacherShortName",
            ENROLLED = "enrolled",
            UNREAD_NEWS = "unreadNews";
        static final String CREATE_QUERY = "CREATE TABLE " + Clazz.TABLE_NAME +
                " ("+ Clazz._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP + Clazz.FULL_NAME+ TYPE_TEXT +COMMA_SEP
                + Clazz.COURSE + TYPE_TEXT + COMMA_SEP + Clazz.SEMESTER + TYPE_TEXT + COMMA_SEP
                + Clazz.SHORT_NAME + TYPE_TEXT + COMMA_SEP + Clazz.TEACHER + TYPE_TEXT + COMMA_SEP
                + Clazz.ENROLLED + TYPE_BOOLEAN + COMMA_SEP + Clazz.UNREAD_NEWS + TYPE_BOOLEAN + ")";
    }

    public static class News implements BaseColumns {
        /**
         * MIME type for lists of news.
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.thothprovider.news";
        /**
         * MIME type for individual news
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
                CLASS_ID = "classID";

        static final String CREATE_QUERY = "CREATE TABLE " + News.TABLE_NAME + " ("
                + News._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP + News.TITLE + TYPE_TEXT + COMMA_SEP
                + News.WHEN_CREATED + TYPE_TEXT + COMMA_SEP + News.CONTENT + TYPE_TEXT + COMMA_SEP
                + News.READ + TYPE_BOOLEAN + COMMA_SEP + News.CLASS_ID + TYPE_INTEGER + ")";
    }
}
