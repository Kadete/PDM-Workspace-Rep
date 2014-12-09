package pt.isel.pdm.grupo17.thothnews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;

public class ThothDBHelper extends SQLiteOpenHelper {

    /** Filename for SQLite file. */
    final private static String DB_NAME = "thoth.db";
    /** Schema version. */
    public static final int DB_VERSION = 2;

//    final private Context mContext;

    public ThothDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
//        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        d("HELPER CREATE Clazz",ThothContract.Clazz.CREATE_QUERY);
        db.execSQL(ThothContract.Clazz.CREATE_QUERY);
        d("HELPER CREATE News",ThothContract.News.CREATE_QUERY);
        db.execSQL(ThothContract.News.CREATE_QUERY);
        d("HELPER CREATE Students", ThothContract.Student.CREATE_QUERY);
        db.execSQL(ThothContract.Student.CREATE_QUERY);
        d("HELPER CREATE Teachers", ThothContract.Teacher.CREATE_QUERY);
        db.execSQL(ThothContract.Teacher.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    void deleteDatabase() {
//        mContext.deleteDatabase(DB_NAME);
//    }
}