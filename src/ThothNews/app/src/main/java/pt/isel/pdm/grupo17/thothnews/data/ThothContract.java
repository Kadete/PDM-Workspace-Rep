package pt.isel.pdm.grupo17.thothnews.data;

import android.provider.BaseColumns;

public class ThothContract {
    public static final String AUTHORITY = "pt.isel.pdm.grupo17.thothnews";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_BOOLEAN = " BOOLEAN";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ", ";

    public static class Clazz implements BaseColumns{
        static final String TABLE_NAME = "classes",
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
                + Clazz.SHORT_NAME + TYPE_TEXT + COMMA_SEP + Clazz.TEACHER + TYPE_TEXT
                + Clazz.ENROLLED + TYPE_BOOLEAN + COMMA_SEP + Clazz.UNREAD_NEWS + TYPE_BOOLEAN + ")";
    }

    public static class News implements BaseColumns {
        static final String TABLE_NAME = "news",
                TITLE = "title",
                WHEN_CREATED = "when",
                CONTENT = "content",
                READ = "read",
                CLASS_ID = "classID";

        static final String CREATE_QUERY = "CREATE TABLE " + News.TABLE_NAME + "("
                + News._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP + News.TITLE + TYPE_TEXT + COMMA_SEP
                + News.WHEN_CREATED + TYPE_TEXT + COMMA_SEP + News.CONTENT + COMMA_SEP
                + News.READ + TYPE_BOOLEAN + News.CLASS_ID + TYPE_INTEGER + ")";
    }
}
