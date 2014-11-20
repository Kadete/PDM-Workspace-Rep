package pt.isel.pdm.grupo17.anniversaryreminder.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by Kadete on 20/11/2014.
 */
public class StartupBootReceiver extends BroadcastReceiver {

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;
    private static final long INITIAL_ALARM_DELAY = 3 * 1000L; //3seg

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // Get the AlarmManager Service
            mAlarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            // Create an Intent to broadcast to the AlarmNotificationReceiver
            mNotificationReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
            mNotificationReceiverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Create an PendingIntent that holds the NotificationReceiverIntent
            mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(context, 0, mNotificationReceiverIntent, 0);

            // Set repeating alarm
            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                    20000L, /* TODO: alterar para AlarmManager.INTERVAL_DAY */
                    mNotificationReceiverPendingIntent);

            // Show Toast message
//            Toast.makeText(context, "Repeating Alarm Set", Toast.LENGTH_LONG).show();

        }
    }
}
