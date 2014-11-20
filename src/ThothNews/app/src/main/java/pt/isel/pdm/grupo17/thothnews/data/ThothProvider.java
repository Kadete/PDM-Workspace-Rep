package pt.isel.pdm.grupo17.thothnews.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import static pt.isel.pdm.grupo17.thothnews.data.ThothContract.AUTHORITY;

public class ThothProvider extends ContentProvider {
    private static UriMatcher _matcher;
    private DatabaseHandler _helper;

    public static final int ROUTE_CLASSES = 0;
    public static final int ROUTE_CLASSES_ID = 1;
    public static final int ROUTE_CLASSES_ENROLLED = 2;
    public static final int ROUTE_CLASSES_ID_NEWS = 3;
    public static final int ROUTE_NEWS = 4;
    public static final int ROUTE_NEWS_ID = 5;

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
        _helper = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
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
            case: ROUTE_NEWS:
            break;
            case ROUTE_NEWS_ID:
                break;
        }
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
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
            case: ROUTE_NEWS:
            break;
            case ROUTE_NEWS_ID:
                break;
        }
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
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
            case: ROUTE_NEWS:
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
            case: ROUTE_NEWS:
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
                break;
            case ROUTE_CLASSES_ID:
                break;
            case ROUTE_CLASSES_ENROLLED:
                break;
            case ROUTE_CLASSES_ID_NEWS:
                break;
            case: ROUTE_NEWS:
            break;
            case ROUTE_NEWS_ID:
                break;
        }
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
