package pt.isel.pdm.grupo17.thothnews.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.HashSet;

import pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver;
import pt.isel.pdm.grupo17.thothnews.receivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.services.utils.Notifications;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

import static pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver.STATUS_FINISHED;
import static pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver.STATUS_RUNNING;
import static pt.isel.pdm.grupo17.thothnews.services.ThothUpdateActionsHandler.ARG_CLASS_ID_DEFAULT_VALUE;
import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class ThothUpdateService extends IntentService { // request update onDemand

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SEMESTERS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.SEMESTERS_UPDATE";
    private static final String ACTION_CLASSES_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASSES_UPDATE";
//    private static final String ACTION_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.NEWS_UPDATE";
    private static final String ACTION_CLASS_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_NEWS_UPDATE";
    private static final String ACTION_CLASS_PARTICIPANTS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_PARTICIPANTS_UPDATE";
    private static final String ACTION_CLASS_WORK_ITEMS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_WORK_ITEMS_UPDATE";

    private static final String ARG_CLASS_ID = "pt.isel.pdm.grupo17.thothnews.services.extra.ARG_CLASS_ID";

    public ThothUpdateService() {
        super(ThothUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        d(ThothUpdateService.class.getName(), "ThothUpdateService started...");
        if (intent != null) {

            ThothUpdateActionsHandler handler = new ThothUpdateActionsHandler(getApplicationContext());
            final ResultReceiver receiver = intent.getParcelableExtra(TagUtils.TAG_EXTRA_RESULT_RECEIVER);
            if(receiver != null)
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                switch (intent.getAction()) {
                    case ACTION_SEMESTERS_UPDATE:
                        handler.handleSemestersUpdate();
                        break;
                    case ACTION_CLASSES_UPDATE:
                        handler.handleClassesUpdate();
                        break;
                    /** SyncAdapter Job **/
//                    case ACTION_NEWS_UPDATE:
//                        handler.handleNewsUpdate();
//                        break;
                    case ACTION_CLASS_NEWS_UPDATE:
                        final long classID = intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
                        if (handler.handleClassNewsUpdate(classID))
                            Notifications.sendNotifications(new HashSet<Long>() {{add(classID);}}, getApplicationContext() );
                        break;
                    case ACTION_CLASS_PARTICIPANTS_UPDATE:
                        handler.handleClassParticipantsUpdate(intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE));
                        break;
                    case ACTION_CLASS_WORK_ITEMS_UPDATE:
                        handler.handleClassWorkItemsUpdate(intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE));
                }
            } finally {
                if(receiver != null)
                    receiver.send(STATUS_FINISHED, new Bundle());
            }
        }
        d(ThothUpdateService.class.getName(), "ThothUpdateService stopping...");
        this.stopSelf();
    }

    public static void startActionSemestersUpdate(Context context) {
        if(!NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_SEMESTERS_UPDATE);
        context.startService(intent);
    }

//    /**
//     * Starts this service to perform action NewsUpdate, updating the news list of all classes enrolled.
//     * If the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    public static void startActionNewsUpdate(Context context, BgProcessingResultReceiver receiver) {
//        if(!NetworkReceiver.checkConnection(context, false))
//            return;
//        Intent intent = new Intent(context, ThothUpdateService.class);
//        intent.setAction(ACTION_NEWS_UPDATE);
//        intent.putExtra(TagUtils.TAG_EXTRA_RESULT_RECEIVER, receiver);
//        context.startService(intent);
//    }

    /**
     * Starts this service to perform action ClassesUpdate, updating classes list, showing all available.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassesUpdate(Context context, BgProcessingResultReceiver receiver) {
        if(!NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASSES_UPDATE);
        intent.putExtra(TagUtils.TAG_EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ClassNewsUpdate, with the given classID.
     * This will update the news list for a given class.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassNewsUpdate(Context context, BgProcessingResultReceiver receiver,long classID) {
        if(classID == ARG_CLASS_ID_DEFAULT_VALUE || !NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_NEWS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        intent.putExtra(TagUtils.TAG_EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action ClassNewsUpdate, with the given classID.
     * This will update the news list for a given class.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassParticipantsUpdate(Context context, BgProcessingResultReceiver receiver, long classID) {
        if(classID == ARG_CLASS_ID_DEFAULT_VALUE || !NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_PARTICIPANTS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        intent.putExtra(TagUtils.TAG_EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action WorkItemUpdate, updating the workItems list of the class id provided.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionWorkItemsUpdate(Context context, BgProcessingResultReceiver receiver, long classID) {
        if(!NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_WORK_ITEMS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        intent.putExtra(TagUtils.TAG_EXTRA_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

}