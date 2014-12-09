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
    public static final int ROUTE_CLASSES_ID_NEWSITEMS = 3;
    public static final int ROUTE_CLASSES_ID_PARTICIPANTS = 4;
    public static final int ROUTE_NEWS = 5;
    public static final int ROUTE_NEWS_ID = 6;
    public static final int ROUTE_STUDENTS = 7;
    public static final int ROUTE_STUDENTS_ID = 8;
    public static final int ROUTE_TEACHERS = 9;
    public static final int ROUTE_TEACHERS_ID = 10;
    /**
     * Position of the class _id on URI path segments such as "classes/#", "classes/#/newsitems"
     */
    private static final int CLASS_ID_POSITION = 1;
    private static final int TEACHER_ID_POSITION = 1;

    /**
     * Position of the news _id on the "newsitems/#" URI path segment
     */
    private static final int NEWS_ID_POSITION =  1;
    private static final int STUDENTS_ID_POSITION = 1;

    static {
        _matcher = new UriMatcher(UriMatcher.NO_MATCH);
        _matcher.addURI(CONTENT_AUTHORITY, "classes", ROUTE_CLASSES);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#", ROUTE_CLASSES_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/enrolled", ROUTE_CLASSES_ENROLLED);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/newsitems", ROUTE_CLASSES_ID_NEWSITEMS);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/participants", ROUTE_CLASSES_ID_PARTICIPANTS);
        _matcher.addURI(CONTENT_AUTHORITY, "newsitems", ROUTE_NEWS);
        _matcher.addURI(CONTENT_AUTHORITY, "newsitems/#", ROUTE_NEWS_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "students", ROUTE_STUDENTS);
        _matcher.addURI(CONTENT_AUTHORITY, "students/#", ROUTE_STUDENTS_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "teachers", ROUTE_TEACHERS);
        _matcher.addURI(CONTENT_AUTHORITY, "teachers/#", ROUTE_TEACHERS_ID);
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
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                d("Uri = %s, ROUTE_CLASSES", uri.toString());
                values.put(ThothContract.Clazz.ENROLLED,false);
                values.put(ThothContract.Clazz.UNREAD_NEWS,false);
                insertResult = db.insertWithOnConflict(ThothContract.Clazz.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);
                break;
            case ROUTE_CLASSES_ID_NEWSITEMS:
                d("Uri = %s, ROUTE_CLASSES_ID_NEWSITEMS", uri.toString());
                long classID = getID(uri, CLASS_ID_POSITION);
                values.put(ThothContract.News.CLASS_ID, classID);
                return insertNew(db, values);
            case ROUTE_CLASSES_ID_PARTICIPANTS:
                d("Uri = %s, ROUTE_CLASSES_ID_PARTICIPANTS", uri.toString());
                classID = getID(uri, CLASS_ID_POSITION);
                values.put(ThothContract.Student.CLASS_ID, classID);
                return insertStudent(db, values);
            case ROUTE_NEWS:
                d("Uri = %s, ROUTE_NEWS", uri.toString());
                return insertNew(db, values);
            case ROUTE_STUDENTS:
                d("Uri = %s, ROUTE_STUDENTS", uri.toString());
                return insertStudent(db, values);
            case ROUTE_TEACHERS:
                d("Uri = %s, ROUTE_TEACHERS", uri.toString());
                return insertTeacher(db, values);
            case ROUTE_TEACHERS_ID:
                d("Uri = %s, ROUTE_TEACHERS_ID", uri.toString());
                long teacherID = getID(uri, TEACHER_ID_POSITION);
                values.put(ThothContract.Teacher._ID, teacherID);
                return insertTeacher(db, values);
            case ROUTE_CLASSES_ID:
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_NEWS_ID:
            case ROUTE_STUDENTS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: "+uri);
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(insertResult));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db =_helper.getReadableDatabase();
        Cursor cursor;

        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                d("Uri = %s, ROUTE_CLASSES", uri.toString());
                cursor = db.query(ThothContract.Clazz.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_CLASSES_ID:{
                d("Uri = %s, ROUTE_CLASSES_ID", uri.toString());
                long classID = getID(uri,CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Clazz._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                cursor = db.query(ThothContract.Clazz.TABLE_NAME, projection
                        , selection, selectionArgs ,null,null, sortOrder);
                break;
            }
            case ROUTE_CLASSES_ENROLLED:
                d("Uri = %s, ROUTE_CLASSES_ENROLLED", uri.toString());
                selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Clazz.ENROLLED);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, SQLiteUtils.TRUE);
                cursor = db.query(ThothContract.Clazz.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_ID_NEWSITEMS: {
                d("Uri = %s, ROUTE_CLASSES_ID_NEWSITEMS", uri.toString());
                long classID = getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                cursor = db.query(ThothContract.News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case ROUTE_CLASSES_ID_PARTICIPANTS: {
                d("Uri = %s, ROUTE_CLASSES_ID_PARTICIPANTS", uri.toString());
                long classID = getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Student.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                cursor = db.query(ThothContract.Student.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case ROUTE_NEWS:
                d("Uri = %s, ROUTE_NEWS", uri.toString());
                cursor = db.query(ThothContract.News.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_NEWS_ID:
                d("Uri = %s, ROUTE_NEWS_ID", uri.toString());
                long newsID = getID(uri,NEWS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(ThothContract.News._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newsID));
                cursor = db.query(ThothContract.News.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_STUDENTS:
                d("Uri = %s, ROUTE_STUDENTS", uri.toString());
                cursor = db.query(ThothContract.Student.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_STUDENTS_ID:
                d("Uri = %s, ROUTE_NEWS_ID", uri.toString());
                long studentID = getID(uri,STUDENTS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(ThothContract.Student._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(studentID));
                cursor = db.query(ThothContract.Student.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ROUTE_TEACHERS:
                d("Uri = %s, ROUTE_TEACHERS", uri.toString());
                cursor = db.query(ThothContract.Teacher.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_TEACHERS_ID:{
                d("Uri = %s, ROUTE_TEACHERS_ID", uri.toString());
                long teacherID = getID(uri,TEACHER_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Teacher._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(teacherID));
                cursor = db.query(ThothContract.Teacher.TABLE_NAME, projection
                        , selection, selectionArgs ,null,null, sortOrder);
                break;
            }
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db =_helper.getWritableDatabase();
        int updateResult;
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
                updateResult =  db.update(ThothContract.News.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ROUTE_CLASSES_ID_NEWSITEMS:
                classID = getID(uri,CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                updateResult =  db.update(ThothContract.News.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ROUTE_STUDENTS_ID:
                long studentID = getID(uri,STUDENTS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Student._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(studentID));
                updateResult =  db.update(ThothContract.Student.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ROUTE_TEACHERS:
                updateResult = db.update(ThothContract.Teacher.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ROUTE_TEACHERS_ID:
                long teacherID = getID(uri,TEACHER_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Teacher._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(teacherID));
                updateResult =  db.update(ThothContract.Teacher.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_CLASSES_ID_PARTICIPANTS:
            case ROUTE_NEWS:
            case ROUTE_STUDENTS:
                throw new UnsupportedOperationException("Update not supported on URI: "+uri);
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return updateResult;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = _helper.getWritableDatabase();
        int deleteResult;
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
            case ROUTE_STUDENTS:
                deleteResult=db.delete(ThothContract.Student.TABLE_NAME,selection,selectionArgs);
                break;
            case ROUTE_STUDENTS_ID:
                long studentID = getID(uri,STUDENTS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Student._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(studentID));
                deleteResult=db.delete(ThothContract.Student.TABLE_NAME,selection,selectionArgs);
                uri = ThothContract.Student.CONTENT_URI;
                break;
            case ROUTE_TEACHERS:
                deleteResult=db.delete(ThothContract.Teacher.TABLE_NAME,selection,selectionArgs);
                break;
            case ROUTE_TEACHERS_ID:
                long TeacherID = getID(uri,TEACHER_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.Teacher._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(TeacherID));
                deleteResult=db.delete(ThothContract.Teacher.TABLE_NAME,selection,selectionArgs);
                uri = ThothContract.Teacher.CONTENT_URI;
                break;
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_CLASSES_ID_NEWSITEMS:
            case ROUTE_CLASSES_ID_PARTICIPANTS:
                throw new UnsupportedOperationException("Delete not supported on URI: "+uri);
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
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
            case ROUTE_CLASSES_ID_PARTICIPANTS:
                return ThothContract.Clazz.CONTENT_DIR_TYPE;
            case ROUTE_CLASSES_ID:
                return ThothContract.Clazz.CONTENT_ITEM_TYPE;
            case ROUTE_NEWS:
            case ROUTE_CLASSES_ID_NEWSITEMS:
                return ThothContract.News.CONTENT_DIR_TYPE;
            case ROUTE_NEWS_ID:
                return ThothContract.News.CONTENT_ITEM_TYPE;
            case ROUTE_STUDENTS:
                return ThothContract.Student.CONTENT_DIR_TYPE;
            case ROUTE_STUDENTS_ID:
                return ThothContract.Student.CONTENT_ITEM_TYPE;
            case ROUTE_TEACHERS:
                return ThothContract.Teacher.CONTENT_DIR_TYPE;
            case ROUTE_TEACHERS_ID:
                return ThothContract.Teacher.CONTENT_ITEM_TYPE;
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

    private Uri insertNew(SQLiteDatabase db, ContentValues values){
        long insertResult;
        Long classID = values.getAsLong(ThothContract.News.CLASS_ID);
        if(classID == null){
            throw new IllegalArgumentException("Missing class ID associated with this new");
        }
        values.put(ThothContract.News.READ,false);
        insertResult = db.insert(ThothContract.News.TABLE_NAME,null,values);
        ContentValues updateInfo = new ContentValues();
        updateInfo.put(ThothContract.Clazz.UNREAD_NEWS,true);
        update(Uri.parse(ThothContract.Clazz.CONTENT_URI+"/"+classID),updateInfo,null,null);
        return Uri.parse(ThothContract.News.CONTENT_URI + "/"+insertResult);
    }

    /* TODO: insert on table N-M */
    private Uri insertStudent(SQLiteDatabase db, ContentValues values){
        long insertResult;
//        Long classID = values.getAsLong(ThothContract.Student.CLASS_ID);
//        if(classID == null){
//            throw new IllegalArgumentException("Missing class ID associated with this participant");
//        }
        insertResult = db.insert(ThothContract.Student.TABLE_NAME,null,values);
//        ContentValues updateInfo = new ContentValues();
//        updateInfo.put(ThothContract.Clazz.UNREAD_NEWS,true);
//        update(Uri.parse(ThothContract.Clazz.CONTENT_URI+"/"+classID),updateInfo,null,null);
        return Uri.parse(ThothContract.Student.CONTENT_URI + "/"+insertResult);
    }

    /* TODO: passar informação da classe a fazer update, invés de inserir o Docente antes da Turma */
    private Uri insertTeacher(SQLiteDatabase db, ContentValues values){
        long insertResult;
//        Long teacherID = values.getAsLong(ThothContract.Teacher._ID);
//        if(teacherID == null){
//            throw new IllegalArgumentException("Missing class ID associated with this participant");
//        }
        insertResult = db.insert(ThothContract.Teacher.TABLE_NAME, null, values);
//        ContentValues updateInfo = new ContentValues();
//        updateInfo.put(ThothContract.Clazz.TEACHER_ID, teacherID);
//
//        update(Uri.parse(ThothContract.Clazz.CONTENT_URI+"/"+ classID),updateInfo,null,null);
        return Uri.parse(ThothContract.Student.CONTENT_URI + "/"+insertResult);
    }
}