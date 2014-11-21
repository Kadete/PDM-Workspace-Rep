package pt.isel.pdm.grupo17.anniversaryreminder.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.util.Date;

import static android.text.format.DateFormat.getTimeFormat;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.*;

/**
 * Created by Kadete on 20/11/2014.
 */
public class StartupBootReceiver extends BroadcastReceiver {

    private static final String TAG_RECEIVER_STARTUP_BOOT = "TAG_RECEIVER_STARTUP_BOOT";

    @Override
    public void onReceive(Context context, Intent intent) {

        Boolean prefsChanged = intent.getAction().equals("com.starlon.froyvisuals.PREFS_UPDATE"),
                booted = intent.ACTION_BOOT_COMPLETED.equals(intent.getAction());

        if(! prefsChanged  && !booted)
                return;

        // Get the AlarmManager Service
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
        mNotificationReceiverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Set repeating alarm
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Long notify_time_milis = sharedPreferences.getLong("schedule_notify_time", 0);
        CharSequence seq = getTimeFormat(context).format(new Date(notify_time_milis));

        d(TAG_RECEIVER_STARTUP_BOOT, "StartupBootReceiver # notify_time: " + seq);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(context, 0, mNotificationReceiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                notify_time_milis, /*TODO : Porque envia logo notificação em vez de esperar por este timer?*/
                AlarmManager.INTERVAL_DAY,
                mNotificationReceiverPendingIntent);
    }

}
