package pt.isel.pdm.grupo17.thothnews.data.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.data.ThothDBHelper;

import static pt.isel.pdm.grupo17.thothnews.data.ThothContract.CONTENT_AUTHORITY;
import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;

public class ThothProvider extends ContentProvider {
    private static UriMatcher _matcher;
    private ThothDBHelper _helper;
    private static final int ROUTE_CLASSES = 0;
    private static final int ROUTE_CLASSES_ID = 1;
    private static final int ROUTE_CLASSES_ENROLLED = 2;
    private static final int ROUTE_CLASSES_ID_NEWS = 3;
    private static final int ROUTE_CLASSES_ID_PARTICIPANTS = 4;
    private static final int ROUTE_CLASSES_ID_PARTICIPANTS_ID = 5;
    private static final int ROUTE_CLASSES_ID_WORK_ITEMS = 6;

    private static final int ROUTE_NEWS = 7;
    private static final int ROUTE_NEWS_ID = 8;
    private static final int ROUTE_STUDENTS = 9;
    private static final int ROUTE_STUDENTS_ID = 10;
    private static final int ROUTE_TEACHERS = 11;
    private static final int ROUTE_TEACHERS_ID = 12;
    private static final int ROUTE_CLASSES_STUDENTS = 13;
    private static final int ROUTE_CLASSES_SEARCH = 14;
    private static final int ROUTE_WORK_ITEMS = 15;
    private static final int ROUTE_WORK_ITEMS_ID = 16;

    /**
     * Position of the class _id on URI path segments such as "classes/#", "classes/#/news"
     */
    private static final int CLASS_ID_POSITION = 1;
    private static final int CLASS_NAME_POSITION = 1;
    private static final int NEWS_ID_POSITION =  1;
    private static final int TEACHER_ID_POSITION = 1;
    private static final int STUDENTS_ID_POSITION = 1;
    private static final int WORK_ITEMS_ID_POSITION = 1;
//    private static final int PARTICIPANTS_ID_POSITION = 3;

    static {
        _matcher = new UriMatcher(UriMatcher.NO_MATCH);
        _matcher.addURI(CONTENT_AUTHORITY, "classes", ROUTE_CLASSES);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#", ROUTE_CLASSES_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/enrolled", ROUTE_CLASSES_ENROLLED);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/news", ROUTE_CLASSES_ID_NEWS);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/participants", ROUTE_CLASSES_ID_PARTICIPANTS);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/participants/#", ROUTE_CLASSES_ID_PARTICIPANTS_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "classes/#/workitems/", ROUTE_CLASSES_ID_WORK_ITEMS);
        _matcher.addURI(CONTENT_AUTHORITY, "news", ROUTE_NEWS);
        _matcher.addURI(CONTENT_AUTHORITY, "news/#", ROUTE_NEWS_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "students", ROUTE_STUDENTS);
        _matcher.addURI(CONTENT_AUTHORITY, "students/#", ROUTE_STUDENTS_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "teachers", ROUTE_TEACHERS);
        _matcher.addURI(CONTENT_AUTHORITY, "teachers/#", ROUTE_TEACHERS_ID);
        _matcher.addURI(CONTENT_AUTHORITY, "classesStudents", ROUTE_CLASSES_STUDENTS);
        _matcher.addURI(CONTENT_AUTHORITY, "classesSearch/*", ROUTE_CLASSES_SEARCH);
        _matcher.addURI(CONTENT_AUTHORITY, "workItems/", ROUTE_WORK_ITEMS);
        _matcher.addURI(CONTENT_AUTHORITY, "workItems/#", ROUTE_WORK_ITEMS_ID);
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
                values.put(ThothContract.Classes.ENROLLED,false);
                values.put(ThothContract.Classes.UNREAD_NEWS,false);
                insertResult = db.insertWithOnConflict(ThothContract.Classes.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);
                break;
            case ROUTE_CLASSES_ID_NEWS:
                d("Uri = %s, ROUTE_CLASSES_ID_NEWS", uri.toString());
                long classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                values.put(ThothContract.News.CLASS_ID, classID);
                Uri newUri = ThothProviderUtils.insertNew(db, values);
                if(newUri != null)
                    updateClass(classID);
                return newUri;
            case ROUTE_CLASSES_ID_PARTICIPANTS:
                d("Uri = %s, ROUTE_CLASSES_ID_PARTICIPANTS", uri.toString());
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                values.put(ThothContract.Students.CLASS_ID, classID);
                return ThothProviderUtils.insertClassesStudent(db, values);
            case ROUTE_CLASSES_ID_WORK_ITEMS:
                d("Uri = %s, ROUTE_CLASSES_ID_WORK_ITEMS", uri.toString());
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                values.put(ThothContract.WorkItems.CLASS_ID, classID);
                return ThothProviderUtils.insertWorkItem(db, values);
            case ROUTE_NEWS:
                d("Uri = %s, ROUTE_NEWS", uri.toString());
                newUri = ThothProviderUtils.insertNew(db, values);
                if(newUri != null)
                    updateClass(values.getAsLong(ThothContract.News.CLASS_ID));
                return newUri;
            case ROUTE_STUDENTS:
                d("Uri = %s, ROUTE_STUDENTS", uri.toString());
                return ThothProviderUtils.insertStudent(db, values);
            case ROUTE_TEACHERS:
                d("Uri = %s, ROUTE_TEACHERS", uri.toString());
                return ThothProviderUtils.insertTeacher(db, values);
            case ROUTE_TEACHERS_ID:
                d("Uri = %s, ROUTE_TEACHERS_ID", uri.toString());
                long teacherID = ThothProviderUtils.getID(uri, TEACHER_ID_POSITION);
                values.put(ThothContract.Teachers._ID, teacherID);
                return ThothProviderUtils.insertTeacher(db, values);
            case ROUTE_CLASSES_STUDENTS:
                d("Uri = %s, ROUTE_CLASSES_STUDENTS", uri.toString());
                return ThothProviderUtils.insertClassesStudent(db, values);
            case ROUTE_WORK_ITEMS:
                d("Uri = %s, ROUTE_WORK_ITEMS", uri.toString());
                return ThothProviderUtils.insertWorkItem(db, values);
            case ROUTE_CLASSES_ID:
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_NEWS_ID:
            case ROUTE_WORK_ITEMS_ID:
            case ROUTE_STUDENTS_ID:
            case ROUTE_CLASSES_SEARCH:
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
                cursor = db.query(ThothContract.Classes.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_ID:
                d("Uri = %s, ROUTE_CLASSES_ID", uri.toString());
                long classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Classes._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                cursor = db.query(ThothContract.Classes.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_ENROLLED:
                d("Uri = %s, ROUTE_CLASSES_ENROLLED", uri.toString());
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Classes.ENROLLED);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, SQLiteUtils.TRUE);
                cursor = db.query(ThothContract.Classes.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_ID_NEWS:
                d("Uri = %s, ROUTE_CLASSES_ID_NEWS", uri.toString());
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                cursor = db.query(ThothContract.News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_ID_WORK_ITEMS:
                d("Uri = %s, ROUTE_CLASSES_ID_WORK_ITEMS", uri.toString());
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.WorkItems.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                cursor = db.query(ThothContract.WorkItems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_ID_PARTICIPANTS:
                d("Uri = %s, ROUTE_CLASSES_ID_PARTICIPANTS", uri.toString());
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                cursor = ThothProviderUtils.getCursorAllStudentsByClass(classID, _helper);
                break;
            case ROUTE_NEWS:
                d("Uri = %s, ROUTE_NEWS", uri.toString());
                cursor = db.query(ThothContract.News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_NEWS_ID:
                d("Uri = %s, ROUTE_NEWS_ID", uri.toString());
                long newID = ThothProviderUtils.getID(uri, NEWS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newID));
                cursor = db.query(ThothContract.News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_STUDENTS:
                d("Uri = %s, ROUTE_STUDENTS", uri.toString());
                cursor = db.query(ThothContract.Students.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_STUDENTS_ID:
                d("Uri = %s, ROUTE_STUDENTS_ID", uri.toString());
                long studentID = ThothProviderUtils.getID(uri, STUDENTS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Students._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(studentID));
                cursor = db.query(ThothContract.Students.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_TEACHERS:
                d("Uri = %s, ROUTE_TEACHERS", uri.toString());
                cursor = db.query(ThothContract.Teachers.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_TEACHERS_ID:
                d("Uri = %s, ROUTE_TEACHERS_ID", uri.toString());
                long teacherID = ThothProviderUtils.getID(uri, TEACHER_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Teachers._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(teacherID));
                cursor = db.query(ThothContract.Teachers.TABLE_NAME, projection
                        , selection, selectionArgs , null, null, sortOrder);
                break;
            case ROUTE_WORK_ITEMS:
                d("Uri = %s, ROUTE_WORK_ITEMS", uri.toString());
                cursor = db.query(ThothContract.WorkItems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_WORK_ITEMS_ID:
                d("Uri = %s, v", uri.toString());
                long workItemID = ThothProviderUtils.getID(uri, WORK_ITEMS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.WorkItems._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(workItemID));
                cursor = db.query(ThothContract.WorkItems.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ROUTE_CLASSES_SEARCH:
                d("Uri = %s, ROUTE_CLASSES_SEARCH", uri.toString());
                String classeName = ThothProviderUtils.getName(uri, CLASS_NAME_POSITION);
                String courseName =  ThothContract.Classes.COURSE;
                String[] semesters = selection.split("OR"), args = new String[selectionArgs.length*2];
                selection = "";
                for (int i = 0, argCount = 0; i < semesters.length; i++){
                    selection += SQLiteUtils.appendWhereCondition(semesters[i], courseName ) + ((i+1 < semesters.length) ? " OR " : "");
                    args[argCount++] = selectionArgs[i];
                    args[argCount++] = String.valueOf(classeName) + "%";
                }
                cursor = db.query(ThothContract.Classes.TABLE_NAME, projection
                        , selection, args , null, null, sortOrder);
                break;
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
        int rowsAffected;
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                rowsAffected = db.update(ThothContract.Classes.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_CLASSES_ID:
                long classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Classes._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                rowsAffected =  db.update(ThothContract.Classes.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_CLASSES_ID_NEWS:
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                rowsAffected =  db.update(ThothContract.News.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_CLASSES_ID_WORK_ITEMS:
                classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.WorkItems.CLASS_ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                rowsAffected =  db.update(ThothContract.WorkItems.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_NEWS:
                rowsAffected = db.update(ThothContract.News.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_NEWS_ID:
                long newID = ThothProviderUtils.getID(uri, NEWS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.News._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newID));
                rowsAffected =  db.update(ThothContract.News.TABLE_NAME,values, selection, selectionArgs);
                break;
            case ROUTE_TEACHERS:
                rowsAffected = db.update(ThothContract.Teachers.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_TEACHERS_ID:
                long teacherID = ThothProviderUtils.getID(uri, TEACHER_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Teachers._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(teacherID));
                rowsAffected =  db.update(ThothContract.Teachers.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_STUDENTS:
                rowsAffected = db.update(ThothContract.Students.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_STUDENTS_ID:
                long studentID = ThothProviderUtils.getID(uri, STUDENTS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Students._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(studentID));
                rowsAffected =  db.update(ThothContract.Students.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_WORK_ITEMS:
                rowsAffected = db.update(ThothContract.WorkItems.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_WORK_ITEMS_ID:
                long workItemID = ThothProviderUtils.getID(uri, WORK_ITEMS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.WorkItems._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(workItemID));
                rowsAffected =  db.update(ThothContract.WorkItems.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_CLASSES_ID_PARTICIPANTS:
            case ROUTE_CLASSES_SEARCH:
                throw new UnsupportedOperationException("Update not supported on URI: "+uri);
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = _helper.getWritableDatabase();
        int rowsAffected;
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
                rowsAffected=db.delete(ThothContract.Classes.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUTE_CLASSES_ID:
                long classID = ThothProviderUtils.getID(uri, CLASS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Classes._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(classID));
                rowsAffected=db.delete(ThothContract.Classes.TABLE_NAME, selection, selectionArgs);
                uri = ThothContract.Classes.CONTENT_URI;
                break;
            case ROUTE_NEWS:
                rowsAffected=db.delete(ThothContract.News.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUTE_NEWS_ID:
                long newsID = ThothProviderUtils.getID(uri, NEWS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection,ThothContract.News._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(newsID));
                rowsAffected=db.delete(ThothContract.News.TABLE_NAME, selection, selectionArgs);
                uri = ThothContract.News.CONTENT_URI;
                break;
            case ROUTE_STUDENTS:
                rowsAffected=db.delete(ThothContract.Students.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUTE_STUDENTS_ID:
                long studentID = ThothProviderUtils.getID(uri, STUDENTS_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Students._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(studentID));
                rowsAffected=db.delete(ThothContract.Students.TABLE_NAME, selection, selectionArgs);
                uri = ThothContract.Students.CONTENT_URI;
                break;
            case ROUTE_TEACHERS:
                rowsAffected=db.delete(ThothContract.Teachers.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUTE_TEACHERS_ID:
                long TeacherID = ThothProviderUtils.getID(uri, TEACHER_ID_POSITION);
                selection = SQLiteUtils.appendWhereCondition(selection, ThothContract.Teachers._ID);
                selectionArgs = SQLiteUtils.appendArgs(selectionArgs, String.valueOf(TeacherID));
                rowsAffected=db.delete(ThothContract.Teachers.TABLE_NAME, selection, selectionArgs);
                uri = ThothContract.Teachers.CONTENT_URI;
                break;
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_CLASSES_ID_NEWS:
            case ROUTE_CLASSES_ID_PARTICIPANTS:
            case ROUTE_CLASSES_SEARCH:
                throw new UnsupportedOperationException("Delete not supported on URI: "+uri);
            default:
                d("Uri = %s, Unmatched URI", uri.toString());
                // The URI given doesn't match any table of the database
                throw new UnsupportedOperationException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
    @Override
    public String getType(Uri uri) {
        final int match = _matcher.match(uri);
        switch (match){
            case ROUTE_CLASSES:
            case ROUTE_CLASSES_ENROLLED:
            case ROUTE_CLASSES_ID_PARTICIPANTS:
                return ThothContract.Classes.CONTENT_DIR_TYPE;
            case ROUTE_CLASSES_ID:
                return ThothContract.Classes.CONTENT_ITEM_TYPE;
            case ROUTE_NEWS:
            case ROUTE_CLASSES_ID_NEWS:
                return ThothContract.News.CONTENT_DIR_TYPE;
            case ROUTE_NEWS_ID:
                return ThothContract.News.CONTENT_ITEM_TYPE;
            case ROUTE_STUDENTS:
                return ThothContract.Students.CONTENT_DIR_TYPE;
            case ROUTE_STUDENTS_ID:
                return ThothContract.Students.CONTENT_ITEM_TYPE;
            case ROUTE_TEACHERS:
                return ThothContract.Teachers.CONTENT_DIR_TYPE;
            case ROUTE_TEACHERS_ID:
                return ThothContract.Teachers.CONTENT_ITEM_TYPE;
            case ROUTE_WORK_ITEMS:
            case ROUTE_CLASSES_ID_WORK_ITEMS:
                return ThothContract.WorkItems.CONTENT_DIR_TYPE;
            case ROUTE_WORK_ITEMS_ID:
                return ThothContract.WorkItems.CONTENT_ITEM_TYPE;
            default: return null;
        }
    }

    private void updateClass(long classID){
        ContentValues updateInfo = new ContentValues();
        updateInfo.put(ThothContract.Classes.UNREAD_NEWS,true);
        update(Uri.parse(ThothContract.Classes.CONTENT_URI + "/" + classID), updateInfo, null, null);
    }

}