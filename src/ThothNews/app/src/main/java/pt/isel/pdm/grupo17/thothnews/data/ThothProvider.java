package pt.isel.pdm.grupo17.thothnews.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils;

import static pt.isel.pdm.grupo17.thothnews.data.ThothContract.CONTENT_AUTHORITY;
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
        _matcher.addURI(CONTENT_AUTHORITY, "classes", ROUTE_CLASSES);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#", ROUTE_CLASSES_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/enrolled", ROUTE_CLASSES_ENROLLED);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/news", ROUTE_CLASSES_ID_NEWS);
        _matcher.addURI(CONTENT_AUTHORITY, "news", ROUTE_NEWS);
        _matcher.addURI(CONTENT_AUTHORITY, "news/#", ROUTE_NEWS_ID);
    }

   @Override
    public boolean onCreate() {
        _helper = new ThothDBHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db =_helper.getWritableDatabase();
        long insertResult;
        try {
            final int match = _matcher.match(uri);
            switch (match){
                case ROUTE_CLASSES:
                    d("Uri = %s, ROUTE_CLASSES", uri.toString());
                    values.put(ThothContract.Clazz.ENROLLED,false);
                    values.put(ThothContract.Clazz.UNREAD_NEWS,false);
                    insertResult = db.insert(ThothContract.Clazz.TABLE_NAME, null, values);
                    break;
                case ROUTE_CLASSES_ID_NEWS:
                    d("Uri = %s, ROUTE_CLASSES_ID_NEWS", uri.toString());
                    long classID = getID(uri, CLASS_ID_POSITION);
                    values.put(ThothContract.News.CLASS_ID,classID);
                    return insertNews(db,uri,values);
                case ROUTE_NEWS:
                    d("Uri = %s, ROUTE_NEWS", uri.toString());
                    return insertNews(db,uri,values);
                case ROUTE_CLASSES_ID:
                case ROUTE_CLASSES_ENROLLED:
                case ROUTE_NEWS_ID:
                    throw new UnsupportedOperationException("Insert not supported on URI: "+uri);
                default:
                    d("Uri = %s, Unmatched URI", uri.toString());
                    // The URI given doesn't match any table of the database
                    throw new UnsupportedOperationException("Unknown URI: "+uri);
            }
        }finally {
            db.close();
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(insertResult));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db =_helper.getReadableDatabase();
        Cursor c;
        try {

            final int match = _matcher.match(uri);
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
        }finally {
            db.close();
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db =_helper.getWritableDatabase();
        int updateResult = 0;
        try {
            final int match = _matcher.match(uri);
            switch (match){
                case ROUTE_CLASSES:
                        updateResult = db.update(ThothContract.Clazz.TABLE_NAME,values,selection,selectionArgs);
                    break;
                case ROUTE_CLASSES_ID:
                        long classID = getID(uri,CLASS_ID_POSITION);
                        selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Clazz._ID);
                        selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                        updateResult =  db.update(ThothContract.Clazz.TABLE_NAME,values,selection,selectionArgs);
                    break;
                case ROUTE_NEWS_ID:
                        long newsID = getID(uri,NEWS_ID_POSITION);
                        selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News._ID);
                        selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newsID));
                        updateResult =  db.update(ThothContract.Clazz.TABLE_NAME,values,selection,selectionArgs);
                    break;
                case ROUTE_CLASSES_ENROLLED:
                case ROUTE_CLASSES_ID_NEWS:
                case ROUTE_NEWS:
                    throw new UnsupportedOperationException("Update not supported on URI: "+uri);
                default:
                    d("Uri = %s, Unmatched URI", uri.toString());
                    // The URI given doesn't match any table of the database
                    throw new UnsupportedOperationException("Unknown URI: "+uri);
            }
        }finally {
            db.close();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return updateResult;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = _helper.getWritableDatabase();
        int deleteResult=0;
        try {
            final int match = _matcher.match(uri);
            switch (match){
                case ROUTE_CLASSES:
                    deleteResult=db.delete(ThothContract.Clazz.TABLE_NAME,selection,selectionArgs);
                    break;
                case ROUTE_CLASSES_ID:
                    long classID = getID(uri,CLASS_ID_POSITION);
                    selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Clazz._ID);
                    selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                    deleteResult=db.delete(ThothContract.Clazz.TABLE_NAME,selection,selectionArgs);
                    uri = ThothContract.Clazz.CONTENT_URI;
                    break;
                case ROUTE_NEWS:
                    deleteResult=db.delete(ThothContract.News.TABLE_NAME,selection,selectionArgs);
                    break;
                case ROUTE_NEWS_ID:
                    long newsID = getID(uri,NEWS_ID_POSITION);
                    selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.News._ID);
                    selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newsID));
                    deleteResult=db.delete(ThothContract.News.TABLE_NAME,selection,selectionArgs);
                    uri = ThothContract.News.CONTENT_URI;
                    break;
                //impossible
                case ROUTE_CLASSES_ENROLLED:
                case ROUTE_CLASSES_ID_NEWS:
                    throw new UnsupportedOperationException("Delete not supported on URI: "+uri);
                default:
                    d("Uri = %s, Unmatched URI", uri.toString());
                    // The URI given doesn't match any table of the database
                    throw new UnsupportedOperationException("Unknown URI: "+uri);
            }
        }finally {
            db.close();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return deleteResult;
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

    private Uri insertNews(SQLiteDatabase db,Uri uri,ContentValues values){
        long insertResult;
        Long classID = values.getAsLong(ThothContract.News.CLASS_ID);
        if(classID == null){
            throw new IllegalArgumentException("Missing class ID associated with this news");
        }
        values.put(ThothContract.News.READ,false);
        insertResult = db.insert(ThothContract.News.TABLE_NAME,null,values);
        ContentValues updateInfo = new ContentValues();
        updateInfo.put(ThothContract.Clazz.UNREAD_NEWS,true);
        update(Uri.parse(ThothContract.Clazz.CONTENT_URI+"/"+classID),updateInfo,null,null);
        return Uri.parse(ThothContract.News.CONTENT_URI + "/"+insertResult);
    }
}