/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.isel.pdm.grupo17.thothnews.services;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.services.utils.JsonModels;
import pt.isel.pdm.grupo17.thothnews.services.utils.Notifications;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;

import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASS_NEWS_ITEMS;
import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_NEW_INFO;
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.downloadUrlStr;
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.getJSONArrayFromData;
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.getJSONObjectFromUri;
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.readAllFrom;
import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_SYNC_ADAPTER;

class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int COLUMN_CLASS_ID = 0;
    private final ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        d(ThothUpdateService.class.getName(), "SyncAdapter.onPerformSync started...");
        d(TAG_SYNC_ADAPTER, "Beginning network synchronization for news");

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        Set<Long> listClassesToNotify = new HashSet<>();
        try (Cursor cursor = mContentResolver.query(ThothContract.Classes.ENROLLED_URI,
                new String[]{ThothContract.Classes._ID}, String.format("%s = 1", ThothContract.Classes.ENROLLED), null, null)) {
            while (cursor.moveToNext()) {
                long classID = cursor.getLong(COLUMN_CLASS_ID);
                final String location = String.format(URI_CLASS_NEWS_ITEMS, classID);
                InputStream stream = null;
                d(TAG_SYNC_ADAPTER, "Streaming data from network: " + location);
                try {
                    stream = downloadUrlStr(location);
                    handleClassNewsUpdate(batch, syncResult, stream, classID);
                    if (syncResult.stats.numInserts > 0)
                        listClassesToNotify.add(classID);

                } catch (JSONException e) {
                    d(TAG_SYNC_ADAPTER, "ERROR: handleClassNewsUpdate(..) while parsing JSON response");
                    d(TAG_SYNC_ADAPTER, e.getMessage());
                } catch (MalformedURLException | ParseException e) {
                    d(TAG_SYNC_ADAPTER, "An error occurred while trying to create URL to request participants list given classID:" + classID + "\nMessage: " + e.getMessage());
                    syncResult.stats.numParseExceptions++;
                } catch (IOException e) {
                    d(TAG_SYNC_ADAPTER, "An error ocurred while trying to estabilish connection to Thoth API!\nMessage: " + e.getMessage());
                    syncResult.stats.numIoExceptions++;
                } finally {
                    if (stream != null)
                        stream.close();
                }
            }
            if (syncResult != null) {
                mContentResolver.applyBatch(ThothContract.CONTENT_AUTHORITY, batch);
                mContentResolver.notifyChange(
                        ThothContract.News.CONTENT_URI, // URI where data was modified
                        null,                           // No local observer
                        false);                         // IMPORTANT: Do not sync to network
            }

            Notifications.sendNotifications(listClassesToNotify, getContext());
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (MalformedURLException e) {
            d(TAG_SYNC_ADAPTER, "Feed URL is malformed" + e.toString());
            syncResult.stats.numParseExceptions++;
        } catch (IOException e) {
            d(TAG_SYNC_ADAPTER, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
        } catch (RemoteException | OperationApplicationException e) {
            d(TAG_SYNC_ADAPTER, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
        }
        Log.i(TAG_SYNC_ADAPTER, "Network synchronization complete");

        d(ThothUpdateService.class.getName(), "SyncAdapter.onPerformSync started...");
    }

    static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;

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
        final JSONArray thothNews = getJSONArrayFromData(newsData, JsonModels.JsonThothNew.ARRAY_NEWS_ITEMS);

        HashMap<Long, ThothNew> newsToSyncMap = new HashMap<>();
        for (int idx = 0; idx < thothNews.length(); ++idx) {
            syncResult.stats.numEntries++;
            JSONObject jNew = thothNews.getJSONObject(idx);

            long currNewID = jNew.getLong(JsonModels.JsonThothNew.ID);
            String title = jNew.getString(JsonModels.JsonThothNew.TITLE);
            String date = jNew.getString(JsonModels.JsonThothNew.WHEN);
            JSONObject jNewDetails = getJSONObjectFromUri(currNewID, URI_NEW_INFO);
            String content = String.valueOf(jNewDetails.getString(JsonModels.JsonThothNew.CONTENT));
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

}
