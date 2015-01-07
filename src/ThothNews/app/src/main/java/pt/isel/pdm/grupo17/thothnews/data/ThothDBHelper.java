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

    public ThothDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        d("HELPER CREATE Classes", ThothContract.Classes.CREATE_QUERY);
        db.execSQL(ThothContract.Classes.CREATE_QUERY);
        d("HELPER CREATE News",ThothContract.News.CREATE_QUERY);
        db.execSQL(ThothContract.News.CREATE_QUERY);
        d("HELPER CREATE Students", ThothContract.Students.CREATE_QUERY);
        db.execSQL(ThothContract.Students.CREATE_QUERY);
        d("HELPER CREATE Teachers", ThothContract.Teachers.CREATE_QUERY);
        db.execSQL(ThothContract.Teachers.CREATE_QUERY);
        d("HELPER CREATE Classes_Students", ThothContract.Classes_Students.CREATE_QUERY);
        db.execSQL(ThothContract.Classes_Students.CREATE_QUERY);
        d("HELPER CREATE WorkItems", ThothContract.WorkItems.CREATE_QUERY);
        db.execSQL(ThothContract.WorkItems.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}