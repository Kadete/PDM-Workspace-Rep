package pt.isel.pdm.grupo17.anniversaryreminder.broadcastreceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import java.util.List;

import pt.isel.pdm.grupo17.anniversaryreminder.R;
import pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem;

import static android.provider.ContactsContract.Contacts;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.CursorUtils.getAnniversaryList;

public class AlarmNotificationReceiver extends BroadcastReceiver {
	// Notification ID to allow for future updates
	private static int MY_NOTIFICATION_ID;
    private static final int TODAY = 0;
    private static final int NEXT_WEEK = 7;

	// Notification Text Elements
	private final CharSequence contentText = "Click here to open his/her contact!";

	// Notification Action Elements
	private Intent mNotificationIntent;
    private NotificationManager mNotificationManager;
    private Notification.Builder notificationBuilder;
	// Notification Sound and Vibration on Arrival
	private final Uri soundURI = Uri
			.parse("android.resource://pt.isel.pdm.grupo17.anniversaryreminder/"
                    + R.raw.birthday_horn_sound_effect);
	private final long[] mVibratePattern = { 0, 200, 200, 300 };

    @Override
	public void onReceive(Context context, Intent intent) {

        List<AnniversaryItem> list = getAnniversaryList(context);

        if(list.size() == 0) return;

        // Setup Intent
        mNotificationIntent = new Intent(Intent.ACTION_VIEW);
        mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Get the NotificationManager
        mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Build the Notification
        notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_birthday_hat)
                .setAutoCancel(true)
                .setContentText(contentText)
                .setVibrate(mVibratePattern);

        String contentTitle;
        MY_NOTIFICATION_ID = 1;

        for (AnniversaryItem item : list) {
            if(item.getDaysLeft() == TODAY){
                contentTitle = "Today it's " + item.getName() + " birthday!";
                notificationBuilder.setTicker(contentTitle)
                        .setSound(soundURI).setLights(Color.GREEN, 500, 500);
                displayNotification(context, contentTitle, item.getId());
            }
            else if(item.getDaysLeft() == NEXT_WEEK){
                contentTitle = item.getName() + " birthday it's next week!";
                notificationBuilder.setTicker(contentTitle)
                        .setSound(null).setLights(Color.BLUE, 500, 500);
                displayNotification(context, contentTitle, item.getId());
            }
        }
    }

    // The Intent to be used when the user clicks on the Notification Area Bar
    private void displayNotification(Context context, String contentTitle, String contactID) {
        // Set ContactID on Uri for Intent Data Notification
        Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactID);
        mNotificationIntent.setData(uri);
        // The PendingIntent that wraps the underlying Intent
        PendingIntent mContentIntent = PendingIntent.getActivity(context, MY_NOTIFICATION_ID, mNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Build the Notification
        notificationBuilder.setContentTitle(contentTitle).setContentIntent(mContentIntent);
        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID++, notificationBuilder.build());
    }

}
