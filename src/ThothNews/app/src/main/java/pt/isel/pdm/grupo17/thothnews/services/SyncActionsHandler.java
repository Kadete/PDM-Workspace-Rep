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
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.getJSONArrayFromData;
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.getJSONObjectFromUri;
import static pt.isel.pdm.grupo17.thothnews.services.utils.GetData.readAllFrom;
import static pt.isel.pdm.grupo17.thothnews.services.utils.JsonModels.JsonThothNew;
import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_SERIALIZABLE_CLASS;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_SYNC_ADAPTER;

public class SyncActionsHandler {



}
