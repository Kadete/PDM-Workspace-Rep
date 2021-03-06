package pt.isel.pdm.grupo17.anniversaryreminder.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

import static android.text.format.DateFormat.getTimeFormat;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.d;

public class AlarmStartupReceiver extends BroadcastReceiver {

    private static final String TAG_DEBUG = "TAG_RECEIVER_STARTUP_BOOT";

    public static final String SCHEDULE_NOTIFY_TIME_PREF_KEY = "schedule_notify_time";
    private static final String NOTIFY_ON_BOOT_PREF_KEY = "notify_on_boot";

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean prefsChanged = intent.getAction().equals("com.starlon.froyvisuals.PREFS_UPDATE"),
                booted = Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && !sharedPreferences.getBoolean(NOTIFY_ON_BOOT_PREF_KEY, true);

        if(! prefsChanged  && !booted)
                return;

        // Get the AlarmManager Service
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
        mNotificationReceiverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(context, 0, mNotificationReceiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set repeating alarm
        Long notify_time_millis = sharedPreferences.getLong(SCHEDULE_NOTIFY_TIME_PREF_KEY, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                notify_time_millis,
                AlarmManager.INTERVAL_DAY,
                mNotificationReceiverPendingIntent);

        d(TAG_DEBUG, "StartupBootReceiver # notify_time: " + getTimeFormat(context).format(new Date(notify_time_millis)));
    }

}
