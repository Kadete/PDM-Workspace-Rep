package pt.isel.pdm.grupo17.thothnews.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils;
import pt.isel.pdm.grupo17.thothnews.data.providers.ThothProvider;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.utils.CalendarUtils;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;
import pt.isel.pdm.grupo17.thothnews.utils.SettingsUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASSES_LIST;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASS_INFO;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASS_NEWS_ITEMS;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASS_PARTICIPANTS;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASS_WORK_ITEMS;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_NEW_INFO;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_TEACHER_INFO;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.downloadUrlStr;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.getJSONArrayFromData;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.getJSONObjectFromUri;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.getListFromCursor;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.readAllFrom;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothAvatar;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothClass;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothFullClass;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothNew;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothParticipant;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothTeacher;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothWorkItem;
import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;


public class ThothUpdateActionsHandler {

    private Context mContext;
    private SQLiteDatabase _dbWritable;
    private SQLiteDatabase _dbReadable;


    public ThothUpdateActionsHandler(Context context){
        this.mContext = context;
        _dbWritable = ThothProvider.getHelper().getWritableDatabase();
        _dbReadable = ThothProvider.getHelper().getReadableDatabase();
    }

    private static final String SERVICE_TAG = "ActionsHandler";

    static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;

    private static final int COLUMN_CLASS_ID = 0;
    private static final int NOTIFICATION_ID = 1;

    /**
     * Handle action SEMESTERS_UPDATE in the provided background thread with the provided
     * parameters.
     */
    void handleSemestersUpdate(){
        try {
            InputStream is = downloadUrlStr(URI_CLASSES_LIST);
            String data = readAllFrom(is);
            final JSONArray thothClasses = getJSONArrayFromData(data, JsonThothClass.ARRAY_CLASSES);

            List<String> semesters = new LinkedList<>();
            for(int idx = 0; idx < thothClasses.length();++idx) {
                JSONObject classJsonObj = thothClasses.getJSONObject(idx);
                String semester = classJsonObj.getString(JsonThothClass.LECTIVE_SEMESTER);
                if (semesters.contains(semester))
                    continue;
                semesters.add(semester);
            }
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            sharedPrefs.edit().putStringSet(TagUtils.TAG_LIST_ALL_SEMESTERS, new HashSet<>(semesters)).apply();

        } catch (JSONException e) {
            d(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
            d(SERVICE_TAG, e.getMessage());
        }catch (MalformedURLException e) {
            d(SERVICE_TAG,"An error ocurred while trying to create URL to request classes list!\nMessage: "+e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    /**
     * Handle action CLASSES_UPDATE in the provided background thread with the provided
     * parameters.
     */
    void handleClassesUpdate() {

        List<String> semestersToFilter = new LinkedList<>(); // <Semester, toFilter>
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final Set<String> semestersSet = sharedPrefs.getStringSet(TagUtils.TAG_MULTILIST_SEMESTERS_PREF_KEY, null);
        if (semestersSet != null && !semestersSet.isEmpty()){
            for(Object semester : semestersSet.toArray())
                semestersToFilter.add(String.valueOf(semester));
        }
        else { return; }

        try {

            InputStream is = downloadUrlStr(URI_CLASSES_LIST);
            String classesData = readAllFrom(is);
            is.close();
            final JSONArray thothClasses = getJSONArrayFromData(classesData, JsonThothClass.ARRAY_CLASSES);
            List<JSONObject> jsonClasses = new LinkedList<>();
            for(int idx = 0; idx < thothClasses.length();++idx) {
                JSONObject classJsonObj = thothClasses.getJSONObject(idx);
                String semester = classJsonObj.getString(JsonThothClass.LECTIVE_SEMESTER);
                if (!semestersToFilter.contains(semester)) // check if semester is to filter
                    continue;
                jsonClasses.add(classJsonObj);
            }
            if(jsonClasses.isEmpty()) { return; }
            final String[] CURSOR_COLUMNS = {ThothContract.Teachers._ID};
            Cursor classTeacherIDCursor = _dbReadable.query(ThothContract.Teachers.TABLE_NAME, CURSOR_COLUMNS, null, null, null, null, null);
            List<Long> teachersIDs = getListFromCursor(classTeacherIDCursor);

            try {
                _dbWritable.beginTransaction();
                for (JSONObject classObj : jsonClasses) {
                    long teacherID = insertClass(classObj); /** INSERT CLASS **/
                    /*check if is already the teacher on the db, if not extract that info from Thoth JSONObject teacher */
                    if (!teachersIDs.contains(teacherID)) {
                        insertTeacher(getJSONObjectFromUri(teacherID, URI_TEACHER_INFO)); /** INSERT TEACHER **/
                        teachersIDs.add(teacherID);
                    }
                }
                _dbWritable.setTransactionSuccessful();
            } finally {
                _dbWritable.endTransaction();
            }

        } catch (JSONException e) {
            d(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
            d(SERVICE_TAG, e.getMessage());

        }catch (MalformedURLException e) {
            d(SERVICE_TAG,"An error ocurred while trying to create URL to request classes list!\nMessage: "+e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    /**
     * Handle action NEWS_UPDATE in the provided background.
     */
    void handleNewsUpdate(){
        Cursor cursor = _dbReadable.query(ThothContract.Classes.TABLE_NAME, new String[]{ThothContract.Classes._ID}
                ,String.format("%s = 1", ThothContract.Classes.ENROLLED), null, null, null, null);

        Set<Long> listClassesToNotify = new TreeSet<>();

        while (cursor.moveToNext()) {
            long classID = cursor.getLong(COLUMN_CLASS_ID);
            if (handleClassNewsUpdate(classID))
                listClassesToNotify.add(classID);
        }

        sendNotifications(listClassesToNotify);
        cursor.close();
    }

    /**
     * Handle action CLASS_NEWS_UPDATE in the provided background thread with the provided
     * parameters.
     * @param classID
     */
    boolean handleClassNewsUpdate( long classID) {
        if( classID == ARG_CLASS_ID_DEFAULT_VALUE)
            return false;
        final String[] CURSOR_COLUMNS = {ThothContract.News._ID};
        try {

            InputStream is = downloadUrlStr(String.format(URI_CLASS_NEWS_ITEMS, classID));
            String newsData = readAllFrom(is);
            is.close();

            final JSONArray thothNews = getJSONArrayFromData(newsData, JsonThothNew.ARRAY_NEWS_ITEMS);
            Cursor classNewsIDsCursor = _dbReadable.query(ThothContract.News.TABLE_NAME, CURSOR_COLUMNS, ThothContract.News.CLASS_ID + " = ?",
                    new String[]{String.valueOf(classID)}, null, null, null);
//            Cursor classNewsIDsCursor = mContentResolver.query(ParseUtils.Classes.parseNewsFromClassID(classID), new String[]{ThothContract.News._ID}, null, null, null);
            List<Long> classNewsIDs = getListFromCursor(classNewsIDsCursor);

            int addedNews = 0;
            try {
                _dbWritable.beginTransaction();
                for (int idx = 0; idx < thothNews.length(); ++idx) {
                    JSONObject jNew = thothNews.getJSONObject(idx);
                    long currNewID = jNew.getLong(JsonThothNew.ID);
                    if (!classNewsIDs.contains(currNewID)) {
                        if (insertNew(jNew, classID))
                            ++addedNews;
                    }
                }
                _dbWritable.setTransactionSuccessful();
            } finally {
                _dbWritable.endTransaction();
            }

            if(addedNews > 0)
                return true;
        } catch (JSONException e) {
            d(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
            d(SERVICE_TAG, e.getMessage());
        } catch (MalformedURLException e) {
            d(SERVICE_TAG, "An error occurred while trying to create URL to request news list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
        return false;
    }

    private static final long[] mVibratePattern = { 0, 200, 200, 300 };
    private NotificationManager notificationManager = null;
    private Notification.Builder builder = null;

    void sendNotifications(Set<Long> classesID) {
        Intent intent = null;
        if(builder == null){
            builder = new Notification.Builder(mContext)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_thoth)
                    .setOngoing(false);
        }
        if(notificationManager == null)
            notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (classesID.size()) {
            case 0:
                return;
            case 1:
//                Cursor classInfo = mContentResolver.query(ParseUtils.Classes.parseClass(classesID.iterator().next()), null, null, null, null);
                Cursor classInfo = _dbReadable.query(ThothContract.Classes.TABLE_NAME, null, ThothContract.Classes._ID + " = ?",
                        new String[]{String.valueOf(classesID.iterator().next())}, null, null, null);

                if (classInfo.moveToNext()) {
                    ThothClass thothClass = ThothClass.fromCursor(classInfo);
                    intent = new Intent(mContext, ClassSectionsActivity.class);
                    intent.putExtra(TagUtils.TAG_SERIALIZABLE_CLASS, thothClass);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    builder.setContentTitle("You got news from " + thothClass.getFullName())
                            .setContentText("Click to open the new from " + thothClass.getFullName());
                }
                classInfo.close();
                break;
            default:
                intent = new Intent(mContext, ClassesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                builder.setContentTitle("You have new news from " + classesID.size() + " classes.")
                        .setContentText("Click to open ThothNews Application.");
                break;
        }

        PendingIntent pIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pIntent)
                .setVibrate((!SettingsUtils.isToVibrate) ? null : mVibratePattern);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cleanNotifications(Context context){
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    /**
     * Handle action CLASS_PARTICIPANTS_UPDATE in the provided background thread with the provided
     * parameters.
     * @param classID
     */
    void handleClassParticipantsUpdate(long classID) {
        final String[] CURSOR_COLUMNS_STUDENT = {ThothContract.Students._ID};
        final String[] CURSOR_COLUMNS_PARTICIPANT = {ThothContract.Classes_Students.KEY_STUDENT_ID};
        final String ORDER_BY = ThothContract.Students._ID + " ASC";

        if( classID == ARG_CLASS_ID_DEFAULT_VALUE){
            return;
        }
        try {
            InputStream is = downloadUrlStr(String.format(URI_CLASS_PARTICIPANTS, classID));
            String participantsData = readAllFrom(is);
            is.close();
            final JSONArray thothParticipants = getJSONArrayFromData(participantsData, JsonThothParticipant.ARRAY_STUDENTS);

            Cursor participantsIDsCursor = _dbReadable.query(ThothContract.Classes_Students.TABLE_NAME, CURSOR_COLUMNS_PARTICIPANT, ThothContract.Classes_Students.KEY_CLASS_ID + " = ?",
                    new String[]{String.valueOf(classID)}, null, null, ORDER_BY);
            Cursor studentsIDsCursor = _dbReadable.query(ThothContract.Students.TABLE_NAME, CURSOR_COLUMNS_STUDENT, null, null, null, null, ORDER_BY);

//            Cursor participantsIDsCursor = mContentResolver.query((ParseUtils.Classes.parseParticipantsFromClassID(classID)), CURSOR_COLUMNS, null, null, ORDER_BY);
//            Cursor studentsIDsCursor = mContentResolver.query(ThothContract.Students.CONTENT_URI, CURSOR_COLUMNS, null, null, ORDER_BY);
            List<Long>participantsIDs = getListFromCursor(participantsIDsCursor);
            List<Long>studentsIDs = getListFromCursor(studentsIDsCursor);

            try {
                _dbWritable.beginTransaction();

                for (int idx = 0; idx < thothParticipants.length(); ++idx) {
                    JSONObject jParticipant = thothParticipants.getJSONObject(idx);
                    long currParticipantID = jParticipant.getLong(JsonThothParticipant.ID);

                    if(participantsIDs.contains(currParticipantID))
                        continue;

                    if(!studentsIDs.contains(currParticipantID))
                        insertStudent(jParticipant, classID);

                    String group = jParticipant.getString(JsonThothParticipant.GROUP);
                    int nGroup = (!isNumeric(group) ? 0 : Integer.parseInt(group));
                    assigningStudentToClass(classID, currParticipantID, nGroup);

                }_dbWritable.setTransactionSuccessful();
            } finally {
                _dbWritable.endTransaction();
            }

        } catch (JSONException e) {
            d(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
            d(SERVICE_TAG, e.getMessage());
        } catch (MalformedURLException e) {
            d(SERVICE_TAG, "An error occurred while trying to create URL to request participants list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    /**
     * Handle action CLASS_WORK_ITEMS_UPDATE in the provided background thread with the provided
     * parameters.
     * @param classID
     */
    void handleClassWorkItemsUpdate(long classID) {
        if( classID == ARG_CLASS_ID_DEFAULT_VALUE)
            return;
        final String[] CURSOR_COLUMNS = {ThothContract.WorkItems._ID};
        try {
            InputStream is = downloadUrlStr(String.format(URI_CLASS_WORK_ITEMS, classID));
            String workItemsData = readAllFrom(is);
            is.close();
            final JSONArray thothWorkItems = getJSONArrayFromData(workItemsData, JsonThothWorkItem.ARRAY_WORK_ITEMS);

            Cursor workItemsIDsCursor = _dbReadable.query(ThothContract.WorkItems.TABLE_NAME, CURSOR_COLUMNS, ThothContract.WorkItems.CLASS_ID + " = ?",
                    new String[]{String.valueOf(classID)}, null, null, null);

//            Cursor workItemsIDsCursor = mContentResolver.query(ParseUtils.Classes.parseWorkItemsFromClassID(classID), new String[]{ThothContract.WorkItems._ID, ThothContract.WorkItems.CLASS_ID}, null, null, null);
            try {
                _dbWritable.beginTransaction();
                List<Long> classWorkItemsIDs = getListFromCursor(workItemsIDsCursor);
                long currWorkItemID;
                for (int idx = 0; idx < thothWorkItems.length(); ++idx) {
                    JSONObject jWorkItem = thothWorkItems.getJSONObject(idx);
                    currWorkItemID = jWorkItem.getLong(JsonThothWorkItem.ID);
                    if(!classWorkItemsIDs.contains(currWorkItemID))
                        insertWorkItem(jWorkItem, classID);
                }
                _dbWritable.setTransactionSuccessful();
            } finally {
                _dbWritable.endTransaction();
            }
        } catch (JSONException e) {
            d(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
            d(SERVICE_TAG, e.getMessage());
        } catch (MalformedURLException e) {
            d(SERVICE_TAG, "An error occurred while trying to create URL to request news list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    private long insertClass(JSONObject classObj) throws JSONException, IOException {
        long classID = classObj.getLong(JsonThothClass.ID);
        ContentValues currValuesClass = new ContentValues();
        currValuesClass.put(ThothContract.Classes._ID, classID);
        currValuesClass.put(ThothContract.Classes.FULL_NAME, classObj.getString(JsonThothClass.FULL_NAME));
        currValuesClass.put(ThothContract.Classes.COURSE, classObj.getString(JsonThothClass.COURSE_NAME));
        currValuesClass.put(ThothContract.Classes.SEMESTER, classObj.getString(JsonThothClass.LECTIVE_SEMESTER));
        currValuesClass.put(ThothContract.Classes.SHORT_NAME, classObj.getString(JsonThothClass.NAME));
        currValuesClass.put(ThothContract.Classes.SEMESTER, classObj.getString(JsonThothClass.LECTIVE_SEMESTER));
        currValuesClass.put(ThothContract.Classes.LINKS, classObj.getString(JsonThothClass.LINKS));
        currValuesClass.put(ThothContract.Classes.TEACHER_NAME, classObj.getString(JsonThothClass.TEACHER));
        long teacherID = getJSONObjectFromUri(classID, URI_CLASS_INFO).getLong(JsonThothFullClass.TEACHER_ID);
        currValuesClass.put(ThothContract.Classes.TEACHER_ID, teacherID);
        currValuesClass.put(ThothContract.Classes.ENROLLED, SQLiteUtils.FALSE);
        currValuesClass.put(ThothContract.Classes.UNREAD_NEWS, SQLiteUtils.FALSE);
        _dbWritable.insertWithOnConflict(ThothContract.Classes.TABLE_NAME, null, currValuesClass, SQLiteDatabase.CONFLICT_IGNORE);/** INSERT CLASS **/
        return teacherID;
    }

    private void insertTeacher(JSONObject jFullTeacher) throws JSONException, IOException {
        ContentValues currValuesTeacher = new ContentValues();
        currValuesTeacher.put(ThothContract.Teachers._ID, jFullTeacher.getLong(JsonThothTeacher.ID));
        currValuesTeacher.put(ThothContract.Teachers.NUMBER, jFullTeacher.getLong(JsonThothTeacher.NUMBER));
        currValuesTeacher.put(ThothContract.Teachers.SHORT_NAME, jFullTeacher.getString(JsonThothTeacher.SHORT_NAME));
        currValuesTeacher.put(ThothContract.Teachers.FULL_NAME, jFullTeacher.getString(JsonThothTeacher.FULL_NAME));
        currValuesTeacher.put(ThothContract.Teachers.ACADEMIC_EMAIL, jFullTeacher.getString(JsonThothTeacher.ACADEMIC_EMAIL));
        JSONObject avatarsObj = jFullTeacher.getJSONObject(JsonThothTeacher.AVATAR);
        String avatarUrl = avatarsObj.getString(JsonThothAvatar.SIZE64);
        currValuesTeacher.put(ThothContract.Avatars.AVATAR_URL, avatarUrl);
        currValuesTeacher.put(ThothContract.Teachers.LINKS, jFullTeacher.getString(JsonThothTeacher.LINKS));
        _dbWritable.insert(ThothContract.Teachers.TABLE_NAME, null, currValuesTeacher); /** INSERT TEACHER **/
    }

    private boolean insertNew(JSONObject jNew, long classID) throws JSONException, IOException {
        ContentValues currValues = new ContentValues();
        long currNewID = jNew.getLong(JsonThothNew.ID);
        currValues.put(ThothContract.News._ID, currNewID);
        currValues.put(ThothContract.News.TITLE, jNew.getString(JsonThothNew.TITLE));
        String when = jNew.getString(JsonThothNew.WHEN);
        currValues.put(ThothContract.News.WHEN_CREATED, when);
        JSONObject jNewDetails = getJSONObjectFromUri(currNewID, URI_NEW_INFO);
        String content = String.valueOf(jNewDetails.getString(JsonThothNew.CONTENT));
        currValues.put(ThothContract.News.CONTENT,content);
        currValues.put(ThothContract.News.CLASS_ID, classID);
        currValues.put(ThothContract.News.READ, SQLiteUtils.FALSE);
        return (_dbWritable.insert(ThothContract.News.TABLE_NAME, null, currValues) > 0); /** INSERT NEW **/
    }

    public void insertStudent(JSONObject jsonStudent, long classID) throws JSONException {
        ContentValues currValuesParticipants = new ContentValues();
        currValuesParticipants.put(ThothContract.Students._ID, jsonStudent.getLong(JsonThothParticipant.ID));
        currValuesParticipants.put(ThothContract.Students.FULL_NAME, jsonStudent.getString(JsonThothParticipant.FULL_NAME));
        currValuesParticipants.put(ThothContract.Students.ACADEMIC_EMAIL, jsonStudent.getString(JsonThothParticipant.ACADEMIC_EMAIL));
        JSONObject avatarsObj = jsonStudent.getJSONObject(JsonThothTeacher.AVATAR);
        currValuesParticipants.put(ThothContract.Avatars.AVATAR_URL, avatarsObj.getString(JsonThothAvatar.SIZE64));
        currValuesParticipants.put(ThothContract.Students.ENROLLED_DATE, jsonStudent.getString(JsonThothParticipant.ENROLL_DATE));
        currValuesParticipants.put(ThothContract.Students.CLASS_ID, classID);
        _dbWritable.insert(ThothContract.Students.TABLE_NAME, null, currValuesParticipants);  /** INSERT STUDENT **/
    }

    public void assigningStudentToClass(long classID, long studentID, int group) {
        ContentValues values = new ContentValues();
        values.put(ThothContract.Classes_Students.KEY_CLASS_ID, classID);
        values.put(ThothContract.Classes_Students.KEY_STUDENT_ID, studentID);
        values.put(ThothContract.Classes_Students.GROUP, group);
        _dbWritable.insert(ThothContract.Classes_Students.TABLE_NAME, null, values); /** ASSIGN STUDENT **/
    }

    public void insertWorkItem(JSONObject jsonWorkItem, long classID) throws JSONException {
        ContentValues currValues = new ContentValues();
        long workItemID = jsonWorkItem.getLong(JsonThothWorkItem.ID);
        currValues.put(ThothContract.WorkItems._ID, workItemID);
        String title = jsonWorkItem.getString(JsonThothWorkItem.TITLE);
        currValues.put(ThothContract.WorkItems.TITLE, title);
        String strStartDate = jsonWorkItem.getString(JsonThothWorkItem.START_DATE);
        String strDueDate = jsonWorkItem.getString(JsonThothWorkItem.DUE_DATE);
        currValues.put(ThothContract.WorkItems.START_DATE, strStartDate);
        currValues.put(ThothContract.WorkItems.DUE_DATE, strDueDate);


        Cursor classCursor = _dbReadable.query(ThothContract.Classes.TABLE_NAME, new String[]{ThothContract.Classes.FULL_NAME}, ThothContract.Classes._ID + " = ?",
                new String[]{String.valueOf(classID)}, null, null, null);

//        Cursor classCursor = mContentResolver.query(ParseUtils.Classes.parseClass(classID), new String[]{ThothContract.Classes.FULL_NAME}, null, null, null);
        String classFullName = null;
        if(classCursor.moveToNext()){
            classFullName = classCursor.getString(classCursor.getColumnIndex(ThothContract.Classes.FULL_NAME));
            currValues.put(ThothContract.WorkItems.URL, String.format("%s/%s/workitems/%d",
                    UriUtils.URI_CLASSES_ROOT, classFullName.replace(" ", ""), workItemID));
        }
        classCursor.close();
        currValues.put(ThothContract.WorkItems.CLASS_ID,classID);
        if(SettingsUtils.isToAutoInsertEvent) {
            try {
                final Date dueDate = DateUtils.SAVE_DATE_FORMAT.parse(strDueDate);
                long eventID = CalendarUtils.addAppointment(mContext, title, classFullName, dueDate.getTime());
                currValues.put(ThothContract.WorkItems.EVENT_ID, eventID);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        _dbWritable.insert(ThothContract.WorkItems.TABLE_NAME, null, currValues); /** INSERT WORK ITEM **/
    }

}
