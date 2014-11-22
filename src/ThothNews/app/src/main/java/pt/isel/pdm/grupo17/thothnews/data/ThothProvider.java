package pt.isel.pdm.grupo17.thothnews.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils;

import static pt.isel.pdm.grupo17.thothnews.data.ThothContract.AUTHORITY;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;

public class ThothProvider extends ContentProvider {
    private static UriMatcher _matcher;
    private ThothDBHelper _helper;
    public static final int ROUTE_CLASSES = 0;
    public static final int ROUTE_CLASSES_ID = 1;
    public static final int ROUTE_CLASSES_ENROLLED = 2;
    public static final int ROUTE_CLASSES_ID_NEWS = 3;
    public static final int ROUTE_NEWS = 4;
    public static final int ROUTE_NEWS_ID = 5;
    /**
     * Position of the class id on URI path segments such as "classes/#", "classes/#/news"
     */
    private static final int CLASS_ID_POSITION = 1;
    /**
     * Position of the news id on the "news/#" URI path segment
     */
    private static final int NEWS_ID_POSITION = 1;
    static {
        _matcher = new UriMatcher(UriMatcher.NO_MATCH);
        _matcher.addURI(AUTHORITY, "classes", ROUTE_CLASSES);
        _matcher.addURI(AUTHORITY, "classes/#", ROUTE_CLASSES_ID);
        _matcher.addURI(AUTHORITY, "classes/enrolled", ROUTE_CLASSES_ENROLLED);
        _matcher.addURI(AUTHORITY, "classes/#/news", ROUTE_CLASSES_ID_NEWS);
        _matcher.addURI(AUTHORITY, "news", ROUTE_NEWS);
        _matcher.addURI(AUTHORITY, "news/#", ROUTE_NEWS_ID);
    }

   @Override
    public boolean onCreate() {
        _helper = new ThothDBHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                break;
            case ROUTE_NEWS:
            break;
            case ROUTE_CLASSES_ID:
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_CLASSES_ID_NEWS:
            case ROUTE_NEWS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: "+uri);
            default:
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final int match = _matcher.match(uri);

        SQLiteDatabase db =_helper.getReadableDatabase();
        Cursor c;
        switch (match){
            case ROUTE_CLASSES:
                d("Uri = %s, ROUTE_CLASSES", uri.toString());
                    c = db.query(ThothContract.Clazz.TABLE_NAME,projection
                            ,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_CLASSES_ID:{
                d("Uri = %s, ROUTE_CLASSES_ID", uri.toString());
                    long classID = getID(uri,CLASS_ID_POSITION);
                    selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Clazz._ID);
                    selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                    c = db.query(ThothContract.Clazz.TABLE_NAME, projection
                            , selection, selectionArgs ,null,null, sortOrder);
                break;
            }
            case ROUTE_CLASSES_ENROLLED:
                d("Uri = %s, ROUTE_CLASSES_ENROLLED", uri.toString());
                    selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Clazz.ENROLLED);
                    selectionArgs = SQLiteUtils.appendArgs(selectionArgs,"true");
                    c = db.query(ThothContract.Clazz.TABLE_NAME,projection,
                            selection, selectionArgs, null, null,sortOrder);
                break;
            case ROUTE_CLASSES_ID_NEWS: {
                d("Uri = %s, ROUTE_CLASSES_ID_NEWS", uri.toString());
                    long classID = getID(uri, CLASS_ID_POSITION);
                    selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News.CLASS_ID);
                    selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                    c = db.query(ThothContract.News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case ROUTE_NEWS:
                d("Uri = %s, ROUTE_NEWS", uri.toString());
                    c = db.query(ThothContract.News.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_NEWS_ID:
                d("Uri = %s, ROUTE_NEWS_ID", uri.toString());
                    long newsID = getID(uri,NEWS_ID_POSITION);
                    selection = SQLiteUtils.appendWhereCondition(ThothContract.News._ID);
                    selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newsID));
                    c = db.query(ThothContract.News.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                break;
            case ROUTE_CLASSES_ID:
                break;
            case ROUTE_CLASSES_ENROLLED:
                break;
            case ROUTE_CLASSES_ID_NEWS:
                break;
            case ROUTE_NEWS:
                break;
            case ROUTE_NEWS_ID:
                break;
        }
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                break;
            case ROUTE_CLASSES_ID:
                break;
            case ROUTE_CLASSES_ENROLLED:
                break;
            case ROUTE_CLASSES_ID_NEWS:
                break;
            case ROUTE_NEWS:
            break;
            case ROUTE_NEWS_ID:
                break;
        }
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public String getType(Uri uri) {
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
            case ROUTE_CLASSES_ENROLLED:
                return ThothContract.Clazz.CONTENT_DIR_TYPE;
            case ROUTE_CLASSES_ID:
                return ThothContract.Clazz.CONTENT_ITEM_TYPE;
            case ROUTE_NEWS:
            case ROUTE_CLASSES_ID_NEWS:
                return ThothContract.News.CONTENT_DIR_TYPE;
            case ROUTE_NEWS_ID:
                return ThothContract.News.CONTENT_ITEM_TYPE;
            default: return null;
        }
    }

    /**
     * Gets a number format ID from a URI, in a segment position
     * @return true if value is a valid long
     * @throws java.lang.NumberFormatException if there's no valid ID on the URI segment
     */
    private static long getID(Uri uri, int position){
        String segment = ParseUtils.getUriSegment(uri,position);
        long number;
        try{
            number = Long.parseLong(segment);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid content URI: "+uri.toString());
        }
        return number;
    }
}
