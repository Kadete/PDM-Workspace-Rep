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
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

import static pt.isel.pdm.grupo17.thothnews.data.providers.ThothProviderUris.URI_CLASS_NEWS_ITEMS;
import static pt.isel.pdm.grupo17.thothnews.services.GetDataUtils.downloadUrlStr;
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
        SyncActionsHandler handler = new SyncActionsHandler(getContext(), mContentResolver);
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
                    handler.handleClassNewsUpdate(batch, syncResult, stream, classID);
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

            handler.sendNotifications(listClassesToNotify);
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

}
