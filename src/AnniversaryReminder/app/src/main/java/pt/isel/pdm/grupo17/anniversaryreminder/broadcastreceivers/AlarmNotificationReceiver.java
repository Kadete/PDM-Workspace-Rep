package pt.isel.pdm.grupo17.anniversaryreminder.broadcastreceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import pt.isel.pdm.grupo17.anniversaryreminder.R;
import pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem;
import pt.isel.pdm.grupo17.anniversaryreminder.utils.CursorUtils;

import static android.provider.ContactsContract.Contacts;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.CursorUtils.*;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.d;

public class AlarmNotificationReceiver extends BroadcastReceiver {
	// Notification ID to allow for future updates
	private static int MY_NOTIFICATION_ID = 1;
    private static final int TODAY = 0;
    private static final int NEXT_WEEK = 7;

	// Notification Text Elements
	private final CharSequence tickerText = "Today is someone birthday!!",
                               contentText = "Click here to open his/her contact!";

	// Notification Action Elements
	private Intent mNotificationIntent;
    private NotificationManager mNotificationManager;
    private Notification.Builder notificationBuilder;
	// Notification Sound and Vibration on Arrival
	private final Uri soundURI = Uri
			.parse("android.resource://pt.isel.pdm.grupo17.anniversaryreminder/"
                    + R.raw.alarm_rooster);
	private final long[] mVibratePattern = { 0, 200, 200, 300 };
    private static final String TAG_RECEIVER_ALARM_NOTIFICATION = "TAG_RECEIVER_ALARM_NOTIFICATION";

    @Override
	public void onReceive(Context context, Intent intent) {



        String contentTitle;

        List<AnniversaryItem> list = getAnniversaryList(context);

        if(list.size() > 0){
            // Setup Intent
            mNotificationIntent = new Intent(Intent.ACTION_VIEW);
            mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Get the NotificationManager
            mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Build the Notification
            notificationBuilder = new Notification.Builder(context)
                    .setTicker(tickerText)
                    .setSmallIcon(R.drawable.ic_birthday_hat)
                    .setAutoCancel(true)
                    .setVibrate(mVibratePattern);
        }

        for (AnniversaryItem item : list) {
            if(isToFilter(item.getDate(), TODAY)){ /*TODO: filtrar na query */
                contentTitle = "Today it's " + item.getName() + " birthday!";
                notificationBuilder.setSound(soundURI).setLights(Color.GREEN, 500, 500);
                displayNotification(context, contentTitle, item.getId());

                // Log occurence of notify() call
                d(TAG_RECEIVER_ALARM_NOTIFICATION, "Sending notification for TODAY at:" + DateFormat.getDateTimeInstance().format(new Date()));
            }
            else if(isToFilter(item.getDate(), NEXT_WEEK)){ /*TODO: filtrar na query */
                contentTitle = item.getName() + " birthday it's next week!";
                notificationBuilder.setSound(null).setLights(Color.BLUE, 500, 500);
                displayNotification(context, contentTitle, item.getId());

                // Log occurence of notify() call
                d(TAG_RECEIVER_ALARM_NOTIFICATION, "Sending notification for NEXT WEEK at:" + DateFormat.getDateTimeInstance().format(new Date()));
            }
        }
        MY_NOTIFICATION_ID = 1; //Reset ID of Notifications
    }

    // The Intent to be used when the user clicks on the Notification Area Bar
    private void displayNotification(Context context, String contentTitle, String contactID) {
        // Set ContactID on Uri for Intent Data Notification
        Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactID);
        mNotificationIntent.setData(uri);

        // The PendingIntent that wraps the underlying Intent
        PendingIntent mContentIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Build the Notification
        notificationBuilder.setContentTitle(contentTitle).setContentText(contentText).setContentIntent(mContentIntent);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID++, notificationBuilder.build());
    }

}