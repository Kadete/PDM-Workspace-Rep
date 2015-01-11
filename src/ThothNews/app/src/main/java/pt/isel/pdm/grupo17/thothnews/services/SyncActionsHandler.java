package pt.isel.pdm.grupo17.thothnews.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;

import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_NEW_INFO;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.getJSONArrayFromData;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.getJSONObjectFromUri;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.readAllFrom;
import static pt.isel.pdm.grupo17.thothnews.services.JsonModels.JsonThothNew;
import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_SERIALIZABLE_CLASS;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_SYNC_ADAPTER;

public class SyncActionsHandler {


    private Context mContext;
    private ContentResolver mContentResolver;

    public SyncActionsHandler(Context context, ContentResolver contentResolver){
        this.mContext = context;
        this.mContentResolver = contentResolver;
    }

    static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;

    private static final int NOTIFICATION_ID = 1;

//    /**
//     * Handle action CLASSES_UPDATE in the provided background thread with the provided
//     * parameters.
//     */
//    void handleClassesUpdate(ArrayList<ContentProviderOperation> batch, SyncResult syncResult, InputStream stream, long classIDt) {
//
//        List<String> semestersToFilter = new LinkedList<>(); // <Semester, toFilter>
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//        final Set<String> semestersSet = sharedPrefs.getStringSet(TAG_MULTILIST_SEMESTERS_PREF_KEY, null);
//        if (semestersSet != null && !semestersSet.isEmpty()){
//            for(Object semester : semestersSet.toArray())
//                semestersToFilter.add(String.valueOf(semester));
//        }
//        else { return; }
//
//        try {
//            InputStream is = downloadUrlStr(URI_CLASSES_LIST);
//            String classesData = readAllFrom(is);
//            is.close();
//            final JSONArray thothClasses = getJSONArrayFromData(classesData, JsonThothClass.ARRAY_CLASSES);
//            List<JSONObject> jsonClasses = new LinkedList<>();
//            for(int idx = 0; idx < thothClasses.length();++idx) {
//                JSONObject classJsonObj = thothClasses.getJSONObject(idx);
//                String semester = classJsonObj.getString(JsonThothClass.LECTIVE_SEMESTER);
//                if (!semestersToFilter.contains(semester)) // check if semester is to filter
//                    continue;
//                jsonClasses.add(classJsonObj);
//            }
//            if(jsonClasses.isEmpty()) { return; }
//
//            Cursor classTeacherIDCursor = mContentResolver.query(ThothContract.Teachers.CONTENT_URI, new String[]{ThothContract.Teachers._ID}, null, null, null);
//            List<Long> teachersIDs = getListFromCursor(classTeacherIDCursor);
//
//            for(JSONObject classObj : jsonClasses) {
//                long classID = classObj.getLong(JsonModels.JsonThothClass.ID);
//                ContentValues currValuesClass = new ContentValues();
//                currValuesClass.put(ThothContract.Classes._ID, classID);
//                currValuesClass.put(ThothContract.Classes.FULL_NAME, classObj.getString(JsonModels.JsonThothClass.FULL_NAME));
//                currValuesClass.put(ThothContract.Classes.COURSE, classObj.getString(JsonModels.JsonThothClass.COURSE_NAME));
//                currValuesClass.put(ThothContract.Classes.SEMESTER, classObj.getString(JsonModels.JsonThothClass.LECTIVE_SEMESTER));
//                currValuesClass.put(ThothContract.Classes.SHORT_NAME, classObj.getString(JsonModels.JsonThothClass.NAME));
//                currValuesClass.put(ThothContract.Classes.SEMESTER, classObj.getString(JsonModels.JsonThothClass.LECTIVE_SEMESTER));
//                currValuesClass.put(ThothContract.Classes.LINKS, classObj.getString(JsonModels.JsonThothClass.LINKS));
//                currValuesClass.put(ThothContract.Classes.TEACHER_NAME, classObj.getString(JsonModels.JsonThothClass.TEACHER));
//                long teacherID = getJSONObjectFromUri(classID, URI_CLASS_INFO).getLong(JsonModels.JsonThothFullClass.TEACHER_ID);
//                currValuesClass.put(ThothContract.Classes.TEACHER_ID, teacherID);
//                mContentResolver.insert(ThothContract.Classes.CONTENT_URI, currValuesClass); /** INSERT CLASS **/
//
//                /*check if is already the teacher on the db, if not extract that info from Thoth JSONObject teacher */
//                if (!teachersIDs.contains(teacherID)) {
//                    JSONObject jFullTeacher = getJSONObjectFromUri(teacherID, URI_TEACHER_INFO);
//                    ContentValues currValuesTeacher = new ContentValues();
//                    currValuesTeacher.put(ThothContract.Teachers._ID, jFullTeacher.getLong(JsonModels.JsonThothTeacher.ID));
//                    currValuesTeacher.put(ThothContract.Teachers.NUMBER, jFullTeacher.getLong(JsonModels.JsonThothTeacher.NUMBER));
//                    currValuesTeacher.put(ThothContract.Teachers.SHORT_NAME, jFullTeacher.getString(JsonModels.JsonThothTeacher.SHORT_NAME));
//                    currValuesTeacher.put(ThothContract.Teachers.FULL_NAME, jFullTeacher.getString(JsonModels.JsonThothTeacher.FULL_NAME));
//                    currValuesTeacher.put(ThothContract.Teachers.ACADEMIC_EMAIL, jFullTeacher.getString(JsonModels.JsonThothTeacher.ACADEMIC_EMAIL));
//                    JSONObject avatarsObj = jFullTeacher.getJSONObject(JsonModels.JsonThothTeacher.AVATAR);
//                    String avatarUrl = avatarsObj.getString(JsonModels.JsonThothAvatar.SIZE64);
//                    currValuesTeacher.put(ThothContract.Avatars.AVATAR_URL, avatarUrl);
//                    currValuesTeacher.put(ThothContract.Teachers.LINKS, jFullTeacher.getString(JsonModels.JsonThothTeacher.LINKS));
//                    mContentResolver.insert(ThothContract.Teachers.CONTENT_URI, currValuesTeacher);/** INSERT TEACHER **/
//                    teachersIDs.add(teacherID);
//                }
//            }
//        } catch (JSONException e) {
//            d(SERVICE_TAG, "ERROR: handleClassesUpdate(..) while parsing JSON response");
//            d(SERVICE_TAG, e.getMessage());
//        }catch (MalformedURLException e) {
//            d(SERVICE_TAG,"An error ocurred while trying to create URL to request classes list!\nMessage: "+e.getMessage());
//        } catch (IOException e) {
//            d(SERVICE_TAG, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
//        }
//    }

    /**
     * Handle SyncAdapter action in the provided background thread
     * parameters.
     * @param batch
     * @param syncResult
     * @param stream
     * @param classID
     */
    void handleClassNewsUpdate(ArrayList<ContentProviderOperation> batch, SyncResult syncResult, InputStream stream, long classID) throws JSONException, IOException, ParseException {

        if(classID == ARG_CLASS_ID_DEFAULT_VALUE)
            return;

        String newsData = readAllFrom(stream);
        final JSONArray thothNews = getJSONArrayFromData(newsData, JsonThothNew.ARRAY_NEWS_ITEMS);

        HashMap<Long, ThothNew> newsToSyncMap = new HashMap<>();
        for (int idx = 0; idx < thothNews.length(); ++idx) {
            syncResult.stats.numEntries++;
            JSONObject jNew = thothNews.getJSONObject(idx);

            long currNewID = jNew.getLong(JsonThothNew.ID);
            String title = jNew.getString(JsonThothNew.TITLE);
            String date = jNew.getString(JsonThothNew.WHEN);
            JSONObject jNewDetails = getJSONObjectFromUri(currNewID, URI_NEW_INFO);
            String content = String.valueOf(jNewDetails.getString(JsonThothNew.CONTENT));
            newsToSyncMap.put(currNewID, new ThothNew(currNewID, title, date, false, content));
        }

        String [] projection = new String[]{ThothContract.News._ID,
                ThothContract.News.TITLE, ThothContract.News.CONTENT,
                ThothContract.News.WHEN_CREATED, ThothContract.News.READ,
                ThothContract.News.CLASS_ID};

        Cursor classNewsIDsCursorEx = mContentResolver.query(ParseUtils.Classes.parseNewsFromClassID(classID), projection , null, null, null);

        while(classNewsIDsCursorEx.moveToNext()){
            ThothNew localThothNew = ThothNew.fromCursor(classNewsIDsCursorEx);
            long localNewID = localThothNew.getID();
            if(newsToSyncMap.containsKey(localNewID)) { // update
                // Entry exists. Remove from entry map to prevent insert later.
                newsToSyncMap.remove(localNewID);
                // Check to see if the entry needs to be updated
                Uri existingUri = ThothContract.News.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(localNewID)).build();
                ThothNew syncNew = newsToSyncMap.get(localNewID);
                if(checkNewDifferences(localThothNew, syncNew)){
                    // Update existing record
                    d(TAG_SYNC_ADAPTER, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ThothContract.News.TITLE, syncNew.getTitle())
                            .withValue(ThothContract.News.WHEN_CREATED, syncNew.getWhenToSave())
                            .withValue(ThothContract.News.CONTENT, syncNew.getContent())
                            .build());
                    syncResult.stats.numUpdates++;
                }
                else {
                    d(TAG_SYNC_ADAPTER, "No action: " + existingUri);
                }
            }else {  // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = ThothContract.News.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(localNewID)).build();
                d(TAG_SYNC_ADAPTER, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        // Add new items
        for (ThothNew thothNew : newsToSyncMap.values()) {
            d(TAG_SYNC_ADAPTER, "Scheduling insert: new_id=" + thothNew.getID());
            batch.add(ContentProviderOperation.newInsert(ThothContract.News.CONTENT_URI)
                    .withValue(ThothContract.News._ID, thothNew.getID())
                    .withValue(ThothContract.News.TITLE, thothNew.getTitle())
                    .withValue(ThothContract.News.WHEN_CREATED, thothNew.getWhenToSave())
                    .withValue(ThothContract.News.CONTENT, thothNew.getContent())
                    .withValue(ThothContract.News.READ, SQLiteUtils.FALSE)
                    .withValue(ThothContract.News.CLASS_ID, classID)
                    .build());
            syncResult.stats.numInserts++;
        }
    }

    private boolean checkNewDifferences(ThothNew local, ThothNew sync) {
        return local.getWhen() != null && local.getWhen().equals(sync) &&
                local.getTitle() != null && local.getTitle().equals(sync.getTitle()) &&
                 local.getContent() != null && local.getContent().equals(sync.getContent());
    }

    private static final long[] mVibratePattern = { 0, 200, 200, 300 };
    private NotificationManager notificationManager = null;
    private Notification.Builder builder = null;

    public static boolean isToVibrate = false;

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
                try(Cursor classInfo = mContentResolver.query(ParseUtils.Classes.parseClass(classesID.iterator().next()), null, null, null, null)){
                    if (classInfo.moveToNext()) {
                        ThothClass thothClass = ThothClass.fromCursor(classInfo);
                        intent = new Intent(mContext, ClassSectionsActivity.class);
                        intent.putExtra(TAG_SERIALIZABLE_CLASS, thothClass);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        builder.setContentTitle("You got news from " + thothClass.getFullName())
                                .setContentText("Click to open the new from " + thothClass.getFullName());
                    }
                }
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
                .setVibrate((!isToVibrate) ? null : mVibratePattern);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
