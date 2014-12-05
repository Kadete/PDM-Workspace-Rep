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
import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
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
    private static final String ACTION_CLASSES_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASSES_UPDATE";

    private static final String ARG_CLASS_ID = "pt.isel.pdm.grupo17.thothnews.services.extra.ARG_CLASS_ID";
    private static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;

    public static final String URI_API_ROOT = "http://thoth.cc.e.ipl.pt/api/v1";
    public static final String URI_CLASSES_LIST = URI_API_ROOT + "/classes";
    public static final String URI_CLASS_NEWS = URI_API_ROOT + "/classes/%d/newsitems";
    public static final String URI_NEWS_INFO = URI_API_ROOT + "/newsitems/%d";

    private static final int COLUMN_CLASS_ID = 0;

    class JsonThothClass{
        public static final String ID = "id";
        public static final String FULLNAME = "fullName";
        public static final String COURSE_NAME = "courseUnitShortName";
        public static final String LECTIVE_SEMESTER = "lectiveSemesterShortName";
        public static final String NAME = "className";
        public static final String TEACHER = "mainTeacherShortName";
    }
    class JsonThothNews{
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String WHEN = "when";
        public static final String CONTENT = "content";
    }


    public ThothUpdateService() {
        super("ThothUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWS_UPDATE.equals(action)) {
                handleNewsUpdate();
            } else if (ACTION_CLASSES_UPDATE.equals(action)) {
                handleClassesUpdate();
            } else if (ACTION_CLASS_NEWS_UPDATE.equals(action)) {
                final long classID = intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
                handleClassNewsUpdate(classID);
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
     * Handle action ClassesUpdate in the provided background thread with the provided
     * parameters.
     */
    private void handleClassesUpdate() {
        try{
            URL url = new URL(URI_CLASSES_LIST);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                final JSONArray thothClasses = ParseUtils.parseClasses(data);
                ContentResolver resolver = getContentResolver();
                // TODO: Insert in batch instead of one by one
                for(int idx = 0; idx < thothClasses.length();++idx){
                    JSONObject jclass = thothClasses.getJSONObject(idx);
                    ContentValues currValues = new ContentValues();
                    currValues.put(ThothContract.Clazz._ID,jclass.getInt(JsonThothClass.ID));
                    currValues.put(ThothContract.Clazz.FULL_NAME,jclass.getString(JsonThothClass.FULLNAME));
                    currValues.put(ThothContract.Clazz.COURSE,jclass.getString(JsonThothClass.COURSE_NAME));
                    currValues.put(ThothContract.Clazz.SEMESTER,jclass.getString(JsonThothClass.LECTIVE_SEMESTER));
                    currValues.put(ThothContract.Clazz.SHORT_NAME,jclass.getString(JsonThothClass.NAME));
                    currValues.put(ThothContract.Clazz.TEACHER,jclass.getString(JsonThothClass.TEACHER));
                    resolver.insert(ThothContract.Clazz.CONTENT_URI,currValues);
                }
            } catch (JSONException e) {
                Log.e(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
                Log.e(SERVICE_TAG, e.getMessage());
            } finally {
                c.disconnect();
            }
        }catch (MalformedURLException e) {
            d(TAG_ACTIVITY,"An error ocurred while trying to create URL to request classes list!\nMessage: "+e.getMessage());
        } catch (IOException e) {
            d(TAG_ACTIVITY,"An error ocurred while trying to estabilish connection to Thoth API!\nMessage: "+e.getMessage());
        }
    }

    /**
     * Handle action NewsUpdate in the provided background.
     */
    private void handleNewsUpdate() {
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(ThothContract.Clazz.ENROLLED_URI,new String[]{ThothContract.Clazz._ID}
                ,String.format("%s = 1",ThothContract.Clazz.ENROLLED),null,null);

        long classID;
        while(c.moveToNext()){
            classID = c.getLong(COLUMN_CLASS_ID);
            handleClassNewsUpdate(classID);
        }
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
            String urlPath = String.format(URI_CLASS_NEWS,classID);
            URL url = new URL(urlPath);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                is.close();
                final JSONArray thothNews = ParseUtils.parseElement(data, "newsItems");
                ContentResolver resolver = getContentResolver();
                // TODO: Insert in batch instead of one by one
                // TODO: Only make request for content of the news that don't exist in the database
                Uri classNewsUri = UriUtils.Classes.parseNewsFromClasseID(classID);
                Cursor classNewsIDsCursor = resolver.query(classNewsUri, new String[]{ThothContract.News._ID},null,null,null);
                List<Long> classNewsIDs = getListFromCursor(classNewsIDsCursor);
                long currNewsID;
                for (int idx = 0; idx < thothNews.length(); ++idx) {
                    JSONObject jnews = thothNews.getJSONObject(idx), jnewsDetails;
                    currNewsID = jnews.getLong(JsonThothNews.ID);
                    if(!classNewsIDs.contains(currNewsID)){

                        jnewsDetails = getNewsDetails(currNewsID);
                        ContentValues currValues = new ContentValues();
                        currValues.put(ThothContract.News._ID,jnews.getInt(JsonThothNews.ID));
                        currValues.put(ThothContract.News.TITLE,jnews.getString(JsonThothNews.TITLE));

                        String when = jnews.getString(JsonThothNews.WHEN);
                        currValues.put(ThothContract.News.WHEN_CREATED, when);
                        String content = String.valueOf(Html.fromHtml(jnewsDetails.getString(JsonThothNews.CONTENT)));
                        currValues.put(ThothContract.News.CONTENT,content);
                        currValues.put(ThothContract.News.CLASS_ID,classID);
                        resolver.insert(ThothContract.News.CONTENT_URI, currValues);
                        ++addedNews;
                    }
                }
                if(addedNews >0){
                    Notification.Builder builder = new Notification.Builder(getApplicationContext())
                            .setContentTitle("You have news to read on!")
                            .setContentText("Click to open the application.")
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_thoth)
                            .setVibrate(mVibratePattern)
                            .setOngoing(false); //false => can drop notification on notification area, true => only dissapier if exectuted action for notification

                    Intent i = new Intent(getApplicationContext(), ClassesActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 1, i, 0);
                    builder.setContentIntent(pIntent);

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());
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
     * Receives a cursor with only one column of type long and returns a List of the column values
     * @param c Cursor with only one column composed of IDs of type long
     * @return List<Long>
     */
    private static List<Long> getListFromCursor(Cursor c){
        List<Long> ids = new ArrayList<Long>();
        while (c.moveToNext()){
            ids.add(c.getLong(0));
        }
        return ids;
    }

    private static JSONObject getNewsDetails(long newsID) throws IOException, JSONException {
        URL newsURL = new URL(String.format(URI_NEWS_INFO,newsID));
        HttpURLConnection newsConn = (HttpURLConnection) newsURL.openConnection();
        InputStream newsStream = newsConn.getInputStream();
        String newsData = readAllFrom(newsStream);
        newsStream.close();
        return new JSONObject(newsData);

    }

}
