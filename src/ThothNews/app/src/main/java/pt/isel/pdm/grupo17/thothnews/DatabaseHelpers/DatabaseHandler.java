package pt.isel.pdm.grupo17.thothnews.DatabaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kadete on 19/11/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    class Clazz{
        final static String TABLE_NAME = "classesDB";

        final static String WHEN_CREATED = "when";
        final static String TITLE = "title";
        final static String _ID = "id";

        final static String READ = "read";
        final static String HAS_NEWS = "hasNews";
    }

    class New{
        final static String TABLE_NAME = "newsDB";
        final static String TEACHER = "mainTeacherShortName";
        final static String FULL_NAME = "fullName";
        final static String _ID = "id";
    }

    final private static String DB_NAME = "ThothDB.db";
    final private Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String CREATE_TABLE = "CREATE TABLE " + Clazz.TABLE_NAME + "("
                + Clazz._ID + " INTEGER PRIMARY KEY," + Clazz.TITLE
                + " TEXT," + Clazz.WHEN_CREATED + " TEXT" + Clazz.READ +" BOOLEAN," + Clazz.HAS_NEWS +" BOOLEAN))";

        sqLiteDatabase.execSQL(CREATE_TABLE);


        CREATE_TABLE = "CREATE TABLE " + New.TABLE_NAME + "("
                + New._ID + " INTEGER PRIMARY KEY," + New.FULL_NAME
                + " TEXT," + New.TEACHER + " TEXT";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }
}
