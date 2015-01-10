package pt.isel.pdm.grupo17.thothnews.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.grupo17.thothnews.broadcastreceivers.NetworkReceiver;

import static pt.isel.pdm.grupo17.thothnews.services.ThothUpdateActionsHandler.ARG_CLASS_ID_DEFAULT_VALUE;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class ThothUpdateService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SEMESTERS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.SEMESTERS_UPDATE";
    private static final String ACTION_CLASSES_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASSES_UPDATE";
    private static final String ACTION_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.NEWS_UPDATE";
//    private static final String ACTION_CLASS_NEWS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_NEWS_UPDATE";
    private static final String ACTION_CLASS_PARTICIPANTS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_PARTICIPANTS_UPDATE";
    private static final String ACTION_CLASS_WORK_ITEMS_UPDATE = "pt.isel.pdm.grupo17.thothnews.services.action.CLASS_WORK_ITEMS_UPDATE";

    private static final String ARG_CLASS_ID = "pt.isel.pdm.grupo17.thothnews.services.extra.ARG_CLASS_ID";

    public ThothUpdateService() {
        super("ThothUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ThothUpdateActionsHandler handler = new ThothUpdateActionsHandler(getApplicationContext(),getContentResolver());
        if (intent != null) {
            switch (intent.getAction()){
                case ACTION_SEMESTERS_UPDATE:
                    handler.handleSemestersUpdate();
                    break;
                case ACTION_CLASSES_UPDATE:
                    handler.handleClassesUpdate();
                    break;
                case ACTION_NEWS_UPDATE:
                    handler.handleNewsUpdate();
                    break;
//                case ACTION_CLASS_NEWS_UPDATE:
//                    final long classID = intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
//                    if(handler.handleClassNewsUpdate(classID))
//                        handler.sendNotifications(new HashSet<Long>(){{add(classID);}});
//                    break;
                case ACTION_CLASS_PARTICIPANTS_UPDATE:
                    handler.handleClassParticipantsUpdate(intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE));
                    break;
                case ACTION_CLASS_WORK_ITEMS_UPDATE:
                    handler.handleClassWorkItemsUpdate(intent.getLongExtra(ARG_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE));
            }
        }
    }

    public static void startActionSemestersUpdate(Context context) {
        if(!NetworkReceiver.checkConnection(context, false))
            return;
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
        if(!NetworkReceiver.checkConnection(context, false))
            return;
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
        if(!NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASSES_UPDATE);
        context.startService(intent);
    }

//    /**
//     * Starts this service to perform action ClassNewsUpdate, with the given classID.
//     * This will update the news list for a given class.
//     * If the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    public static void startActionClassNewsUpdate(Context context, long classID) {
//        if(classID == ARG_CLASS_ID_DEFAULT_VALUE || !NetworkReceiver.checkConnection(context, false))
//            return;
//        Intent intent = new Intent(context, ThothUpdateService.class);
//        intent.setAction(ACTION_CLASS_NEWS_UPDATE);
//        intent.putExtra(ARG_CLASS_ID, classID);
//        context.startService(intent);
//    }

    /**
     * Starts this service to perform action ClassNewsUpdate, with the given classID.
     * This will update the news list for a given class.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassParticipantsUpdate(Context context, long classID) {
        if(classID == ARG_CLASS_ID_DEFAULT_VALUE || !NetworkReceiver.checkConnection(context, false))
            return;
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
        if(!NetworkReceiver.checkConnection(context, false))
            return;
        Intent intent = new Intent(context, ThothUpdateService.class);
        intent.setAction(ACTION_CLASS_WORK_ITEMS_UPDATE);
        intent.putExtra(ARG_CLASS_ID, classID);
        context.startService(intent);
    }

}