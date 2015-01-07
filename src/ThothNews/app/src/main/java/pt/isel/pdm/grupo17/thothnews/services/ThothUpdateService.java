package pt.isel.pdm.grupo17.thothnews.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;
import pt.isel.pdm.grupo17.thothnews.activities.WebViewActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.readAllFrom;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class ThothUpdateService extends IntentService {

    private static final String SERVICE_TAG = "ThothUpdateService";
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SEMESTERS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.SEMESTERS_UPDATE";
    private static final String ACTION_CLASSES_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASSES_UPDATE";
    private static final String ACTION_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.NEWS_UPDATE";
    private static final String ACTION_CLASS_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_NEWS_UPDATE";
    private static final String ACTION_CLASS_PARTICIPANTS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_PARTICIPANTS_UPDATE";
    private static final String ACTION_CLASS_WORK_ITEMS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_WORK_ITEMS_UPDATE";

    private static final String ARG_CLASS_ID = "pt.isel.pdm.grupo17.thothnews.services.extra.ARG_CLASS_ID";
    private static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;

    /** provider uri paths **/
    public static final String URI_API_ROOT = "http://thoth.cc.e.ipl.pt/api/v1";
    public static final String URI_CLASSES_LIST = URI_API_ROOT + "/classes";
    public static final String URI_CLASS_NEWS_ITEMS = URI_API_ROOT + "/classes/%d/newsitems";
    public static final String URI_NEW_INFO = URI_API_ROOT + "/newsitems/%d";
    public static final String URI_CLASS_PARTICIPANTS = URI_API_ROOT + "/classes/%d/participants";
    public static final String URI_CLASS_WORK_ITEMS = URI_API_ROOT + "/classes/%d/workitems";
    public static final String URI_CLASS_INFO = URI_API_ROOT + "/classes/%d";
    public static final String URI_TEACHER_INFO = URI_API_ROOT + "/teachers/%d";

    private static final int COLUMN_CLASS_ID = 0;
    private static final int NOTIFICATION_ID = 1;

//    class JsonThothLink{
//        public static final String SELF = "self";
//    }

    class JsonThothAvatar{
//        public static final String SIZE24 = "size24";
//        public static final String SIZE32 = "size32";
        public static final String SIZE64 = "size64";
//        public static final String SIZE128 = "size128";
    }

    class JsonThothTeacher{
        /** Props **/
        public static final String ID = "id";
        public static final String NUMBER = "number";
        public static final String FULL_NAME = "fullName";
        public static final String SHORT_NAME = "shortName";
        public static final String ACADEMIC_EMAIL = "academicEmail";
        /** Object JsonThothAvatar **/
        public static final String AVATAR = "avatarUrl";
        /** Object JsonThothLink **/
        public static final String LINKS = "_links";
    }

    class JsonThothClass{
        public static final String ID = "id";
        public static final String FULL_NAME = "fullName";
        public static final String COURSE_NAME = "courseUnitShortName";
        public static final String LECTIVE_SEMESTER = "lectiveSemesterShortName";
        public static final String NAME = "className";
        public static final String TEACHER = "mainTeacherShortName";
        /** Object JsonThothAvatar **/
        public static final String LINKS = "_links";
    }

    class JsonThothFullClass{
        public static final String TEACHER_ID = "mainTeacherId";
    }

    class JsonThothNew {
        /** Array **/
        public static final String ARRAY_NEWS_ITEMS = "newsItems";
        /** Props **/
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String WHEN = "when";
        public static final String CONTENT = "content";
    }
    class JsonThothParticipant{
        /** Array **/
        public static final String ARRAY_STUDENTS = "students";
        /** Props **/
        public static final String ID = "id";
        public static final String FULL_NAME = "fullName";
        public static final String ACADEMIC_EMAIL = "academicEmail";
        /** Object JsonThothAvatar **/
//        public static final String AVATAR_URL = "avatarUrl";
        public static final String ENROLL_DATE = "enrollmentDate";
        public static final String GROUP = "currentGroup";
    }
    class JsonThothWorkItem{
        /** Array **/
        public static final String ARRAY_WORK_ITEMS = "workItems";
        /** Props **/
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String START_DATE = "startDate";
        public static final String DUE_DATE = "dueDate";
    }

    public ThothUpdateService() {
        super("ThothUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()){
                case ACTION_SEMESTERS_UPDATE:
                    handleSemestersUpdate();
                    break;
                case ACTION_CLASSES_UPDATE:
                    handleClassesUpdate();
                    break;
                case ACTION_NEWS_UPDATE:
                    handleNewsUpdate();
                    break;
                case ACTION_CLASS_NEWS_UPDATE:
                    final long classID = intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
                    if(handleClassNewsUpdate(classID))
                        sendNotifications(new HashSet<Long>(){{add(classID);}});
                    break;
                case ACTION_CLASS_PARTICIPANTS_UPDATE:
                    handleClassParticipantsUpdate(intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE));
                    break;
                case ACTION_CLASS_WORK_ITEMS_UPDATE:
                    handleClassWorkItemsUpdate(intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE));
            }
        }
    }

    public static void startActionSemestersUpdate(Context context) {
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_SEMESTERS_UPDATE);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action NewsUpdate, updating the news list of all classes enrolled.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionNewsUpdate(Context context) {
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_NEWS_UPDATE);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ClassesUpdate, updating classes list, showing all available.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassesUpdate(Context context) {
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASSES_UPDATE);
        context.startService(intent);
    }
    /**
     * Starts this service to perform action ClassNewsUpdate, with the given classID.
     * This will update the news list for a given class.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassNewsUpdate(Context context, long classID) {
        if(classID == ARG_CLASS_ID_DEFAULT_VALUE) return;

        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_NEWS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ClassNewsUpdate, with the given classID.
     * This will update the news list for a given class.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassParticipantsUpdate(Context context, long classID) {
        if(classID == ARG_CLASS_ID_DEFAULT_VALUE) return;

        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_PARTICIPANTS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action WorkItemUpdate, updating the workItems list of the class id provided.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionWorkItemsUpdate(Context context, long classID) {
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_WORK_ITEMS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        context.startService(intent);
    }

    /**
     * Handle action SEMESTERS_UPDATE in the provided background thread with the provided
     * parameters.
     */
    private void handleSemestersUpdate(){
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(URI_CLASSES_LIST).openConnection();
            try {
                InputStream is = conn.getInputStream();
                String data = readAllFrom(is);
                final JSONArray thothClasses = ParseUtils.parseClasses(data);

                List<String> semesters = new LinkedList<>();
                for(int idx = 0; idx < thothClasses.length();++idx) {
                    JSONObject classJsonObj = thothClasses.getJSONObject(idx);
                    String semester = classJsonObj.getString(JsonThothClass.LECTIVE_SEMESTER);
                    if (semesters.contains(semester))
                        continue;
                    semesters.add(semester);
                }
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
                sharedPrefs.edit().putStringSet(TagUtils.TAG_LIST_ALL_SEMESTERS, new HashSet<>(semesters)).apply();

            } catch (JSONException e) {
                d(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
                d(SERVICE_TAG, e.getMessage());
            } finally {
                conn.disconnect();
            }
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
    private void handleClassesUpdate() {
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(URI_CLASSES_LIST).openConnection();
            ContentResolver resolver = getContentResolver();
            try {
                InputStream is = conn.getInputStream();
                String classesData = readAllFrom(is);
                is.close();
                final JSONArray thothClasses = ParseUtils.parseClasses(classesData);

                List<String> semestersToFilter = new LinkedList<>(); // <Semester, toFilter>
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
                final Set<String> semestersSet = sharedPrefs.getStringSet(TagUtils.TAG_MULTILIST_SEMESTERS_PREF_KEY, null);

                if (semestersSet != null && !semestersSet.isEmpty()){
                    for(Object semester : semestersSet.toArray()){
                        semestersToFilter.add(String.valueOf(semester));
                    }
                }

                List<JSONObject> jsonClasses = new LinkedList<>();
                for(int idx = 0; idx < thothClasses.length();++idx) {
                    JSONObject classJsonObj = thothClasses.getJSONObject(idx);
                    String semester = classJsonObj.getString(JsonThothClass.LECTIVE_SEMESTER);
                    if (!semestersToFilter.contains(semester)) // check if semester is to filter
                        continue;
                   jsonClasses.add(classJsonObj);
                }

                if(jsonClasses.isEmpty()) { return; }

                Cursor classTeacherIDCursor = resolver.query(ThothContract.Teachers.CONTENT_URI, new String[]{ThothContract.Teachers._ID}, null, null, null);
                List<Long> teachersIDs = getListFromCursor(classTeacherIDCursor);
                classTeacherIDCursor.close();

                ContentValues currValuesClass = new ContentValues();
                for(JSONObject classObj : jsonClasses) {
                    long classID = classObj.getLong(JsonThothClass.ID);
                    currValuesClass.clear();
                    currValuesClass.put(ThothContract.Classes._ID, classID);
                    currValuesClass.put(ThothContract.Classes.FULL_NAME, classObj.getString(JsonThothClass.FULL_NAME));
                    currValuesClass.put(ThothContract.Classes.COURSE, classObj.getString(JsonThothClass.COURSE_NAME));
                    currValuesClass.put(ThothContract.Classes.SEMESTER, classObj.getString(JsonThothClass.LECTIVE_SEMESTER));
                    currValuesClass.put(ThothContract.Classes.SHORT_NAME, classObj.getString(JsonThothClass.NAME));
                    currValuesClass.put(ThothContract.Classes.SEMESTER, classObj.getString(JsonThothClass.LECTIVE_SEMESTER));
                    currValuesClass.put(ThothContract.Classes.LINKS, classObj.getString(JsonThothClass.LINKS));

                    currValuesClass.put(ThothContract.Classes.TEACHER_NAME, classObj.getString(JsonThothClass.TEACHER));
                    long teacherID = getClassObject(classID).getLong(JsonThothFullClass.TEACHER_ID);
                    currValuesClass.put(ThothContract.Classes.TEACHER_ID, teacherID);

                    resolver.insert(ThothContract.Classes.CONTENT_URI, currValuesClass); /** INSERT CLASS **/

                    /*check if is already the teacher on the db, if not extract that info from Thoth JSONObject teacher */
                    if (!teachersIDs.contains(teacherID)) {
                        insertTeacher(getTeacherObject(teacherID)); /** INSERT TEACHER **/
                        teachersIDs.add(teacherID);
                    }
                }

            } catch (JSONException e) {
                d(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
                d(SERVICE_TAG, e.getMessage());
            } finally {
                conn.disconnect();
            }
        }catch (MalformedURLException e) {
            d(SERVICE_TAG,"An error ocurred while trying to create URL to request classes list!\nMessage: "+e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    /**
     * Handle action NEWS_UPDATE in the provided background.
     */
    private void handleNewsUpdate() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ThothContract.Classes.ENROLLED_URI, new String[]{ThothContract.Classes._ID}
                ,String.format("%s = 1", ThothContract.Classes.ENROLLED), null, null);

        Set<Long> listClassesToNotify = new TreeSet<>();
        while(cursor.moveToNext()) {
            long classID = cursor.getLong(COLUMN_CLASS_ID);
            if(handleClassNewsUpdate(classID))
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
    private boolean handleClassNewsUpdate(long classID) {
        if( classID == ARG_CLASS_ID_DEFAULT_VALUE)
            return false;

        int addedNews = 0;

        try {
            String urlPath = String.format(URI_CLASS_NEWS_ITEMS,classID);
            URL url = new URL(urlPath);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = httpConn.getInputStream();
                String newsData = readAllFrom(is);
                is.close();
                final JSONArray thothNews = ParseUtils.parseElement(newsData, JsonThothNew.ARRAY_NEWS_ITEMS);
                ContentResolver resolver = getContentResolver();
                // TODO: Insert in batch instead of one by one
                // TODO: Only make request for content of the news that don't exist in the database
                Cursor classNewsIDsCursor = resolver.query(UriUtils.Classes.parseNewsFromClassID(classID), new String[]{ThothContract.News._ID}, null, null, null);
                List<Long> classNewsIDs = getListFromCursor(classNewsIDsCursor);
                classNewsIDsCursor.close();
                long currNewID;
                ContentValues currValues = new ContentValues();
                for (int idx = 0; idx < thothNews.length(); ++idx) {
                    JSONObject jnews = thothNews.getJSONObject(idx), jNewDetails;
                    currNewID = jnews.getLong(JsonThothNew.ID);
                    if(!classNewsIDs.contains(currNewID)){
                        currValues.clear();
                        currValues.put(ThothContract.News._ID, currNewID);
                        currValues.put(ThothContract.News.TITLE,jnews.getString(JsonThothNew.TITLE));
                        String when = jnews.getString(JsonThothNew.WHEN);
                        currValues.put(ThothContract.News.WHEN_CREATED, when);

                        jNewDetails = getNewDetails(currNewID);
                        String content = String.valueOf(jNewDetails.getString(JsonThothNew.CONTENT));
                        currValues.put(ThothContract.News.CONTENT,content);
                        currValues.put(ThothContract.News.CLASS_ID,classID);
                        resolver.insert(ThothContract.News.CONTENT_URI, currValues);
                        ++addedNews;
                    }
                }
                if(addedNews > 0)
                    return true;
            } catch (JSONException e) {
                d(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
                d(SERVICE_TAG, e.getMessage());
            } finally {
                httpConn.disconnect();
            }
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

    private void sendNotifications(Set<Long> classesID) {
        Intent intent = null;
        if(builder == null){
            builder = new Notification.Builder(getApplicationContext())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_thoth)
                .setOngoing(false);
        }
        if(notificationManager == null)
            notificationManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        switch (classesID.size()) {
            case 0:
                return;
            case 1:
                Cursor classInfo = getContentResolver().query(UriUtils.Classes.parseClass(classesID.iterator().next()), null, null, null, null);
                if (classInfo.moveToNext()) {
                    ThothClass thothClass = ThothClass.fromCursor(classInfo);
                    intent = new Intent(this.getApplication(), ClassSectionsActivity.class);
                    intent.putExtra(TagUtils.TAG_SERIALIZABLE_CLASS, thothClass);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    builder.setContentTitle("You got news from " + thothClass.getFullName())
                        .setContentText("Click to open the new from " + thothClass.getFullName());
                }
                classInfo.close();
                break;
            default:
                intent = new Intent(this.getApplication(), ClassesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                builder.setContentTitle("You have new news from " + classesID.size() + " classes.")
                        .setContentText("Click to open ThothNews Application.");
                break;
        }

        PendingIntent pIntent = PendingIntent.getActivity(this.getApplication(), NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pIntent)
            .setVibrate((!isToVibrate) ? null : mVibratePattern);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static boolean isToVibrate = true;

    public static void cleanNotifications(Context context){
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    static final String[] CURSOR_COLUMNS = {ThothContract.Students._ID};
    static final String ORDER_BY = ThothContract.Students._ID + " ASC";

    /**
     * Handle action CLASS_PARTICIPANTS_UPDATE in the provided background thread with the provided
     * parameters.
     * @param classID
     */
    private void handleClassParticipantsUpdate(long classID) {
        if( classID == ARG_CLASS_ID_DEFAULT_VALUE){
            return;
        }
        try {
            String urlPath = String.format(URI_CLASS_PARTICIPANTS,classID);
            URL url = new URL(urlPath);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String participantsData = readAllFrom(is);
                is.close();
                final JSONArray thothParticipants = ParseUtils.parseElement(participantsData, JsonThothParticipant.ARRAY_STUDENTS);
                Cursor participantsIDsCursor = getContentResolver().query((UriUtils.Classes.parseParticipantsFromClassID(classID)), CURSOR_COLUMNS, null, null, ORDER_BY);
                Cursor studentsIDsCursor = getContentResolver().query(ThothContract.Students.CONTENT_URI, CURSOR_COLUMNS, null, null, ORDER_BY);
                List<Long>participantsIDs = getListFromCursor(participantsIDsCursor);
                List<Long>studentsIDs = getListFromCursor(studentsIDsCursor);
                participantsIDsCursor.close();
                studentsIDsCursor.close();

                long currParticipantID;

                for (int idx = 0; idx < thothParticipants.length(); ++idx) {
                    JSONObject jParticipant = thothParticipants.getJSONObject(idx);
                    currParticipantID = jParticipant.getLong(JsonThothParticipant.ID);

                    if(participantsIDs.contains(currParticipantID))
                        continue;

                    if(!studentsIDs.contains(currParticipantID))
                        insertStudent(jParticipant, classID);

                    String group = jParticipant.getString(JsonThothParticipant.GROUP);
                    int nGroup = (!isNumeric(group) ? 0 : Integer.parseInt(group));
                    assigningStudentToClass(classID, currParticipantID, nGroup);
                }
            } catch (JSONException e) {
                d(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
                d(SERVICE_TAG, e.getMessage());
            } finally {
                c.disconnect();
            }
        } catch (MalformedURLException e) {
            d(SERVICE_TAG, "An error occurred while trying to create URL to request participants list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    /**
     * Handle action CLASS_WORK_ITEMS_UPDATE in the provided background thread with the provided
     * parameters.
     * @param classID
     */
    private void handleClassWorkItemsUpdate(long classID) {
        if( classID == ARG_CLASS_ID_DEFAULT_VALUE)
            return;

        try {
            String urlPath = String.format(URI_CLASS_WORK_ITEMS,classID);
            URL url = new URL(urlPath);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = httpConn.getInputStream();
                String workItemsData = readAllFrom(is);
                is.close();
                final JSONArray thothWorkItems = ParseUtils.parseElement(workItemsData, JsonThothWorkItem.ARRAY_WORK_ITEMS);
                ContentResolver resolver = getContentResolver();
                Cursor workItemsIDsCursor = resolver.query(UriUtils.Classes.parseWorkItemsFromClassID(classID), new String[]{ThothContract.WorkItems._ID, ThothContract.WorkItems.CLASS_ID}, null, null, null);
                List<Long> classWorkItemsIDs = getListFromCursor(workItemsIDsCursor);
                workItemsIDsCursor.close();
                long currWorkItemID;
                ContentValues currValues = new ContentValues();
                // TODO: Insert in batch instead of one by one
                // TODO: Only make request for content of the news that don't exist in the database
                for (int idx = 0; idx < thothWorkItems.length(); ++idx) {
                    JSONObject jWorkItem = thothWorkItems.getJSONObject(idx);
                    currWorkItemID = jWorkItem.getLong(JsonThothWorkItem.ID);
                    if(!classWorkItemsIDs.contains(currWorkItemID)){
                        currValues.clear();
                        currValues.put(ThothContract.WorkItems._ID, currWorkItemID);
                        currValues.put(ThothContract.WorkItems.TITLE, jWorkItem.getString(JsonThothWorkItem.TITLE));
                        String strStartDate = jWorkItem.getString(JsonThothWorkItem.START_DATE);
                        String strDueDate = jWorkItem.getString(JsonThothWorkItem.DUE_DATE);
                        currValues.put(ThothContract.WorkItems.START_DATE, strStartDate);
                        currValues.put(ThothContract.WorkItems.DUE_DATE, strDueDate);
                        Cursor classCursor = resolver.query(UriUtils.Classes.parseClass(classID), new String[]{ThothContract.Classes.FULL_NAME}, null, null, null);
                        if(classCursor.moveToNext())
                            currValues.put(ThothContract.WorkItems.URL, String.format("%s/%s/workitems/%d",
                                WebViewActivity.URI_CLASSES_ROOT, classCursor.getString(classCursor.getColumnIndex(ThothContract.Classes.FULL_NAME)).replace(" ", ""), currWorkItemID));
                        classCursor.close();
                        currValues.put(ThothContract.WorkItems.CLASS_ID,classID);
                        resolver.insert(ThothContract.WorkItems.CONTENT_URI, currValues);
                    }
                }
            } catch (JSONException e) {
                d(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
                d(SERVICE_TAG, e.getMessage());
            } finally {
                httpConn.disconnect();
            }
        } catch (MalformedURLException e) {
            d(SERVICE_TAG, "An error occurred while trying to create URL to request news list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
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
        getContentResolver().insert(ThothContract.Teachers.CONTENT_URI, currValuesTeacher);
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
        getContentResolver().insert(ThothContract.Students.CONTENT_URI, currValuesParticipants);
    }

    public void assigningStudentToClass(long classID, long studentID, int group) {
        ContentValues values = new ContentValues();
        values.put(ThothContract.Classes_Students.KEY_CLASS_ID, classID);
        values.put(ThothContract.Classes_Students.KEY_STUDENT_ID, studentID);
        values.put(ThothContract.Classes_Students.GROUP, group);
        getContentResolver().insert(ThothContract.Classes_Students.CONTENT_URI, values);
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    /**
     * Receives a cursor with only one column of type long and returns a List of the column values
     * @param c Cursor with only one column composed of IDs of type long
     * @return List<Long>
     */
    private static List<Long> getListFromCursor(Cursor c){
        List<Long> ids = new ArrayList<>();
        while (c.moveToNext()){
            ids.add(c.getLong(0));
        }
        return ids;
    }

    private static JSONObject getNewDetails(long newID) throws IOException, JSONException {
        URL newsURL = new URL(String.format(URI_NEW_INFO, newID));
        HttpURLConnection connection = (HttpURLConnection) newsURL.openConnection();
        InputStream newStream = connection.getInputStream();
        String newsData = readAllFrom(newStream);
        newStream.close();
        return new JSONObject(newsData);
    }

    private static JSONObject getClassObject(long classID) throws IOException, JSONException {
        URL classURL = new URL(String.format(URI_CLASS_INFO, classID));
        HttpURLConnection connection = (HttpURLConnection) classURL.openConnection();
        InputStream classStream = connection.getInputStream();
        String classData = readAllFrom(classStream);
        classStream.close();
        return new JSONObject(classData);
    }

    private static JSONObject getTeacherObject(long teacherID) throws IOException, JSONException {
        URL teacherURL = new URL(String.format(URI_TEACHER_INFO, teacherID));
        HttpURLConnection connection = (HttpURLConnection) teacherURL.openConnection();
        InputStream teacherStream = connection.getInputStream();
        String teacherData = readAllFrom(teacherStream);
        teacherStream.close();
        return new JSONObject(teacherData);
    }

}