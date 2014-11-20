package pt.isel.pdm.grupo17.thothnews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    final private static String DB_NAME = "thoth.db";
//    final private Context mContext;
    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, 1);
//        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ClassTable.CREATE_QUERY);
        db.execSQL(NewsTable.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        throw new UnsupportedOperationException("Method not implemented");
    }

//    void deleteDatabase() {
//        mContext.deleteDatabase(DB_NAME);
//    }
}

class ClassTable {
    static final String TABLE_NAME = "classes",
            WHEN_CREATED = "when",
            TITLE = "title",
            _ID = "id",
            READ = "read",
            HAS_NEWS = "hasNews";

    static final String CREATE_QUERY = "CREATE TABLE " + ClassTable.TABLE_NAME + "("
        + ClassTable._ID + " INTEGER PRIMARY KEY," + ClassTable.TITLE + " TEXT,"
        + ClassTable.WHEN_CREATED + " TEXT" + ClassTable.READ +" BOOLEAN," + ClassTable.HAS_NEWS +" BOOLEAN))";
}

class NewsTable {
    static final String TABLE_NAME = "news",
            TEACHER = "mainTeacherShortName",
            FULL_NAME = "fullName",
            _ID = "id";

    static final String CREATE_QUERY = "CREATE TABLE " + NewsTable.TABLE_NAME +
        "("+ NewsTable._ID + " INTEGER PRIMARY KEY," + NewsTable.FULL_NAME+ " TEXT," + NewsTable.TEACHER + " TEXT";


}