package pt.isel.pdm.grupo17.thothnews.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.util.Log;

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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.readAllFrom;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ACTIVITY;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class ThothUpdateService extends IntentService {

    private static final String SERVICE_TAG = "ThothUpdateService";
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.NEWS_UPDATE";
    private static final String ACTION_CLASS_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_NEWS_UPDATE";
    private static final String ACTION_CLASS_PARTICIPANTS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_PARTICIPANTS_UPDATE";
    private static final String ACTION_CLASSES_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASSES_UPDATE";

    private static final String ARG_CLASS_ID = "pt.isel.pdm.grupo17.thothnews.services.extra.ARG_CLASS_ID";
    private static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;

    public static final String URI_API_ROOT = "http://thoth.cc.e.ipl.pt/api/v1";
    public static final String URI_CLASSES_LIST = URI_API_ROOT + "/classes";
    public static final String URI_CLASS_NEWS_ITEMS = URI_API_ROOT + "/classes/%d/newsitems";
    public static final String URI_NEWS_ITEMS_INFO = URI_API_ROOT + "/newsitems/%d";
    public static final String URI_CLASS_PARTICIPANTS = URI_API_ROOT + "/classes/%d/participants";
    public static final String URI_CLASS_INFO = URI_API_ROOT + "/classes/%d";
    public static final String URI_TEACHER_INFO = URI_API_ROOT + "/teachers/%d";

    private static final int COLUMN_CLASS_ID = 0;

    class JsonThothLink{
        public static final String SELF = "self";
    }

    class JsonThothAvatar{
        public static final String SIZE24 = "size24";
        public static final String SIZE32 = "size32";
        public static final String SIZE64 = "size64";
        public static final String SIZE128 = "size128";
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
        public static final String AVATAR_URL = "avatarUrl";
        public static final String ENROLL_DATE = "enrollmentDate";
        public static final String GROUP = "currentGroup";
    }


    public ThothUpdateService() {
        super("ThothUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()){
                case ACTION_NEWS_UPDATE:
                    handleNewsUpdate();
                    break;
                case ACTION_CLASSES_UPDATE:
                    handleClassesUpdate();
                    break;
                case ACTION_CLASS_NEWS_UPDATE:
                    long classID = intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
                    handleClassNewsUpdate(classID);
                    break;
                case ACTION_CLASS_PARTICIPANTS_UPDATE:
                    classID = intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
                    handleClassParticipantsUpdate(classID);
            }
        }
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

    static final List<String> SemestersToFilter = new LinkedList<>(Arrays.asList("1314v", "1415i"));

    /*TODO: make this a preference or generic*/
    public boolean isSemesterToFilter(String semester){
        return SemestersToFilter.contains(semester);
    }

    /**
     * Handle action ClassesUpdate in the provided background thread with the provided
     * parameters.
     */
    private void handleClassesUpdate() {
        try{
            URL url = new URL(URI_CLASSES_LIST);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            Cursor classTeacherIDCursor = null;
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                final JSONArray thothClasses = ParseUtils.parseClasses(data);
                ContentResolver resolver = getContentResolver();
                ContentValues currValuesClass = new ContentValues(), currValuesTeacher = new ContentValues();
                // TODO: Insert in batch instead of one by one

                for(int idx = 0; idx < thothClasses.length();++idx){
                    JSONObject jClass = thothClasses.getJSONObject(idx);

                    String semester = jClass.getString(JsonThothClass.LECTIVE_SEMESTER);
                    if(!isSemesterToFilter(semester))
                        continue;

                    currValuesClass.clear();
                    long classID = jClass.getLong(JsonThothClass.ID);
                    currValuesClass.put(ThothContract.Classes._ID, classID);
                    currValuesClass.put(ThothContract.Classes.SEMESTER, semester);
                    currValuesClass.put(ThothContract.Classes.FULL_NAME, jClass.getString(JsonThothClass.FULL_NAME));
                    currValuesClass.put(ThothContract.Classes.COURSE, jClass.getString(JsonThothClass.COURSE_NAME));
                    currValuesClass.put(ThothContract.Classes.SEMESTER, jClass.getString(JsonThothClass.LECTIVE_SEMESTER));
                    currValuesClass.put(ThothContract.Classes.SHORT_NAME, jClass.getString(JsonThothClass.NAME));
                    currValuesClass.put(ThothContract.Classes.TEACHER_NAME, jClass.getString(JsonThothClass.TEACHER));
                    currValuesClass.put(ThothContract.Classes.LINKS, jClass.getString(JsonThothClass.LINKS));

                    JSONObject jFullClass = getClassObject(classID);
                    long teacherID = jFullClass.getLong(JsonThothFullClass.TEACHER_ID);

                    Uri classTeacherUri = UriUtils.Teachers.parseFromTeacherID(teacherID);

                    classTeacherIDCursor = resolver.query(classTeacherUri, new String[]{ThothContract.Teachers._ID},null,null,null);
                    if(!classTeacherIDCursor.moveToNext()){
                        currValuesTeacher.clear();
                        JSONObject jFullTeacher = getTeacherObject(teacherID);
                        currValuesTeacher.put(ThothContract.Teachers._ID, jFullTeacher.getLong(JsonThothTeacher.ID));
                        currValuesTeacher.put(ThothContract.Teachers.NUMBER, jFullTeacher.getLong(JsonThothTeacher.NUMBER));
                        currValuesTeacher.put(ThothContract.Teachers.SHORT_NAME, jFullTeacher.getString(JsonThothTeacher.SHORT_NAME));
                        currValuesTeacher.put(ThothContract.Teachers.FULL_NAME, jFullTeacher.getString(JsonThothTeacher.FULL_NAME));
                        currValuesTeacher.put(ThothContract.Teachers.ACADEMIC_EMAIL, jFullTeacher.getString(JsonThothTeacher.ACADEMIC_EMAIL));

//                        ImageView img = (ImageView)findViewById(R.id.imageView1);
//                        SetViewHandler svh = new SetViewHandler(Looper.getMainLooper());
//                        ImageHandlerThread th = new ImageHandlerThread();
//                        th.start();
//                        ImageHandler ih = new ImageHandler(svh, th.getLooper());
//                        ih.fetchImage(img, "http://www.gravatar.com/avatar/e3a7f4454cf8bc6781c3bf7adcae366a?s=24&r=g&d=mm");
                        /* TODO save on rom db_rom_photo_location_url instead always request db_avatar_url*/
                        JSONObject avatarsObj = jFullTeacher.getJSONObject(JsonThothTeacher.AVATAR);
                        currValuesTeacher.put(ThothContract.Teachers.AVATAR_URL, avatarsObj.getString(JsonThothAvatar.SIZE128));

                        currValuesTeacher.put(ThothContract.Teachers.LINKS, jFullTeacher.getString(JsonThothTeacher.LINKS));
                        resolver.insert(ThothContract.Teachers.CONTENT_URI, currValuesTeacher); /** INSERT TEACHER_NAME **/
                    }

                    currValuesClass.put(ThothContract.Classes.TEACHER_ID, jFullClass.getLong(JsonThothFullClass.TEACHER_ID));
                    resolver.insert(ThothContract.Classes.CONTENT_URI, currValuesClass); /** INSERT CLASS **/

                }
            } catch (JSONException e) {
                Log.e(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
                Log.e(SERVICE_TAG, e.getMessage());
            } finally {
                c.disconnect();
                if(classTeacherIDCursor != null)
                    classTeacherIDCursor.close();
            }
        }catch (MalformedURLException e) {
            d(TAG_ACTIVITY,"An error ocurred while trying to create URL to request classes list!\nMessage: "+e.getMessage());
        } catch (IOException e) {
            d(TAG_ACTIVITY, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    /**
     * Handle action NewsUpdate in the provided background.
     */
    private void handleNewsUpdate() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ThothContract.Classes.ENROLLED_URI, new String[]{ThothContract.Classes._ID}
                ,String.format("%s = 1", ThothContract.Classes.ENROLLED), null, null);

        long classID;
        while(cursor.moveToNext()){
            classID = cursor.getLong(COLUMN_CLASS_ID);
            handleClassNewsUpdate(classID);
        }
        cursor.close();
    }

    private final long[] mVibratePattern = { 0, 200, 200, 300 };

    /**
     * Handle action ClassNewsUpdate in the provided background thread with the provided
     * parameters.
     * @param classID
     */
    private void handleClassNewsUpdate(long classID) {
        if( classID == ARG_CLASS_ID_DEFAULT_VALUE){
            return;
        }
        int addedNews = 0;
        try {
            String urlPath = String.format(URI_CLASS_NEWS_ITEMS,classID);
            URL url = new URL(urlPath);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                is.close();
                final JSONArray thothNews = ParseUtils.parseElement(data, JsonThothNew.ARRAY_NEWS_ITEMS);
                ContentResolver resolver = getContentResolver();
                // TODO: Insert in batch instead of one by one
                // TODO: Only make request for content of the news that don't exist in the database
                Uri classNewsUri = UriUtils.Classes.parseNewsFromClassID(classID);
                Cursor classNewsIDsCursor = resolver.query(classNewsUri, new String[]{ThothContract.News._ID},null,null,null);
                List<Long> classNewsIDs = getListFromCursor(classNewsIDsCursor);
                classNewsIDsCursor.close();
                long currNewID;
                ContentValues currValues = new ContentValues();
                for (int idx = 0; idx < thothNews.length(); ++idx) {
                    JSONObject jnews = thothNews.getJSONObject(idx), jnewsDetails;
                    currNewID = jnews.getLong(JsonThothNew.ID);
                    if(!classNewsIDs.contains(currNewID)){
                        currValues.clear();
                        currValues.put(ThothContract.News._ID, currNewID);
                        currValues.put(ThothContract.News.TITLE,jnews.getString(JsonThothNew.TITLE));
                        String when = jnews.getString(JsonThothNew.WHEN);
                        currValues.put(ThothContract.News.WHEN_CREATED, when);

                        jnewsDetails = getNewDetails(currNewID);
                        String content = String.valueOf(Html.fromHtml(jnewsDetails.getString(JsonThothNew.CONTENT)));
                        currValues.put(ThothContract.News.CONTENT,content);
                        currValues.put(ThothContract.News.CLASS_ID,classID);
                        resolver.insert(ThothContract.News.CONTENT_URI, currValues);
                        ++addedNews;
                    }
                }
                if(addedNews >0){
                    Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
                            .setContentTitle("You have news to read on!")
                            .setContentText("Click to open the application.")
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_thoth)
                            .setVibrate(mVibratePattern)
                            .setOngoing(false);

                    Intent i = new Intent(getApplicationContext(), ClassesActivity.class);
                    i.putExtra(TagUtils.TAG_NOTIFICATION_ID, 0);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 1, i, 0);
                    mBuilder.setContentIntent(pIntent);
                    mBuilder.setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, mBuilder.build());
                }
            } catch (JSONException e) {
                Log.e(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
                Log.e(SERVICE_TAG, e.getMessage());
            } finally {
                c.disconnect();
            }
        } catch (MalformedURLException e) {
            d(TAG_ACTIVITY, "An error occurred while trying to create URL to request news list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(TAG_ACTIVITY, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    /**
     * Handle action ClassParticipantsUpdate in the provided background thread with the provided
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
                String data = readAllFrom(is);
                is.close();
                final JSONArray thothParticipants = ParseUtils.parseElement(data, JsonThothParticipant.ARRAY_STUDENTS);
                ContentResolver resolver = getContentResolver();

                Uri classNewsUri = UriUtils.Classes.parseParticipantsFromClassID(classID);
                Cursor classParticipantsIDsCursor = resolver.query(classNewsUri, new String[]{ThothContract.Students._ID},null,null,null);
                List<Long>classParticipantsIDs = getListFromCursor(classParticipantsIDsCursor);
                classParticipantsIDsCursor.close();
                long currParticipantID;

                for (int idx = 0; idx < thothParticipants.length(); ++idx) {
                    JSONObject jParticipant = thothParticipants.getJSONObject(idx);
                    currParticipantID = jParticipant.getLong(JsonThothParticipant.ID);

                    if(!classParticipantsIDs.contains(currParticipantID))
                        insertStudent(jParticipant, classID);

                    String group = jParticipant.getString(JsonThothParticipant.GROUP);
                    int nGroup = (!isNumeric(group) ? 0 : Integer.parseInt(group));
                    assigningStudentToClass(classID, currParticipantID, nGroup);
                }
            } catch (JSONException e) {
                Log.e(SERVICE_TAG, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
                Log.e(SERVICE_TAG, e.getMessage());
            } finally {
                c.disconnect();
            }
        } catch (MalformedURLException e) {
            d(TAG_ACTIVITY, "An error occurred while trying to create URL to request participants list given classID:" + classID + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            d(TAG_ACTIVITY, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
        }
    }

    public void insertStudent(JSONObject jsonStudent, long classID) throws JSONException {
        ContentValues currValuesParticipants = new ContentValues();

        currValuesParticipants.put(ThothContract.Students._ID, jsonStudent.getLong(JsonThothParticipant.ID));
        currValuesParticipants.put(ThothContract.Students.FULL_NAME, jsonStudent.getString(JsonThothParticipant.FULL_NAME));
        currValuesParticipants.put(ThothContract.Students.ACADEMIC_EMAIL, jsonStudent.getString(JsonThothParticipant.ACADEMIC_EMAIL));

        JSONObject avatarsObj = jsonStudent.getJSONObject(JsonThothTeacher.AVATAR);
        currValuesParticipants.put(ThothContract.Teachers.AVATAR_URL, avatarsObj.getString(JsonThothAvatar.SIZE128));/* TODO */
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


    public static boolean isNumeric(String str)
    {
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
        URL newsURL = new URL(String.format(URI_NEWS_ITEMS_INFO, newID));
        HttpURLConnection newsConn = (HttpURLConnection) newsURL.openConnection();
        InputStream newsStream = newsConn.getInputStream();
        String newsData = readAllFrom(newsStream);
        newsStream.close();
        return new JSONObject(newsData);
    }

    private static JSONObject getClassObject(long classID) throws IOException, JSONException {
        URL classURL = new URL(String.format(URI_CLASS_INFO, classID));
        HttpURLConnection teacherConn = (HttpURLConnection) classURL.openConnection();
        InputStream classStream = teacherConn.getInputStream();
        String classData = readAllFrom(classStream);
        classStream.close();
        return new JSONObject(classData);
    }

    private static JSONObject getTeacherObject(long teacherID) throws IOException, JSONException {
        URL teacherURL = new URL(String.format(URI_TEACHER_INFO, teacherID));
        HttpURLConnection teacherConn = (HttpURLConnection) teacherURL.openConnection();
        InputStream teacherStream = teacherConn.getInputStream();
        String teacherData = readAllFrom(teacherStream);
        teacherStream.close();
        return new JSONObject(teacherData);
    }

}
