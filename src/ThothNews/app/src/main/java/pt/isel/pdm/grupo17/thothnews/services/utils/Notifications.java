package pt.isel.pdm.grupo17.thothnews.services.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.SettingsUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class Notifications {

    private static final int NOTIFICATION_ID = 1;

    private static final long[] mVibratePattern = { 0, 200, 200, 300 };
    private static NotificationManager notificationManager = null;
    private static Notification.Builder builder = null;

    public static void sendNotifications(Set<Long> classesID, Context mContext) {
        Intent intent = null;
        if(builder == null){
            builder = new Notification.Builder(mContext)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_thoth)
                    .setOngoing(false);
        }
        if(notificationManager == null)
            notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (classesID.size()) {
            case 0:
                return;
            case 1:
                try(Cursor classInfo = mContext.getContentResolver().query(ParseUtils.Classes.parseClass(classesID.iterator().next()), null, null, null, null)) {
                    if (classInfo.moveToNext()) {
                        ThothClass thothClass = ThothClass.fromCursor(classInfo);
                        intent = new Intent(mContext, ClassSectionsActivity.class);
                        intent.putExtra(TagUtils.TAG_SERIALIZABLE_CLASS, thothClass);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        builder.setContentTitle("You got news from " + thothClass.getFullName())
                                .setContentText("Click to open the new from " + thothClass.getFullName());
                    }
                }
                break;
            default:
                intent = new Intent(mContext, ClassesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                builder.setContentTitle("You have new news from " + classesID.size() + " classes.")
                        .setContentText("Click to open ThothNews Application.");
                break;
        }

        PendingIntent pIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pIntent)
                .setVibrate((!SettingsUtils.isToVibrate) ? null : mVibratePattern);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cleanNotifications(Context context){
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
}
