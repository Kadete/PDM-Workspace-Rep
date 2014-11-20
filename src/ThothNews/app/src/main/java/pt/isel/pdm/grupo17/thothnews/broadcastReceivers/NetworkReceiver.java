package pt.isel.pdm.grupo17.thothnews.broadcastreceivers;

/**
 * Created by Kadete on 11/11/2014.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesActivity;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.TAG_BROADCAST;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;

public class NetworkReceiver extends BroadcastReceiver {

    static NetworkInfo.State previousState;
    static boolean firstTime = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        d(TAG_BROADCAST, "Received intent with action = " + intent.getAction());

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

            //d(TAG_BROADCAST, "Network changed");
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni_wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

//            NetworkInfo ni_mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            d(TAG_BROADCAST, "Network info  = " + ni_wifi);
            d(TAG_BROADCAST, "Wifi connected  = " + ni_wifi.isConnected());
            d(TAG_BROADCAST, "Wifi state  = " + ni_wifi.getDetailedState());

            if(firstTime) {
                previousState = (ni_wifi.getState() == NetworkInfo.State.CONNECTING) ? NetworkInfo.State.DISCONNECTED : NetworkInfo.State.CONNECTED ;
                firstTime = false;
            }

                if(ni_wifi.isConnected() && previousState == NetworkInfo.State.DISCONNECTED){

                d(TAG_BROADCAST, "--- register notification " + ni_wifi.getDetailedState().name());

                Notification.Builder builder = new Notification.Builder(context)
                        .setContentTitle("You have news to read")
                        .setContentText("Click to open the application")
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.thoth_icon)
                        .setOngoing(false); //false => can drop notification on notification area, true => only dissapier if exectuted action for notification

                intent = new Intent(context, ClassesActivity.class);
                intent.putExtra("msg", "Network is connected");

                PendingIntent pintent = PendingIntent.getActivity(context, 1, intent, 0);
                builder.setContentIntent(pintent);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
                previousState = NetworkInfo.State.CONNECTED;

            }
            else if(previousState == NetworkInfo.State.CONNECTED){

                d(TAG_BROADCAST, "--- unregister notification " + ni_wifi.getDetailedState().name());
                previousState = NetworkInfo.State.DISCONNECTED;

            }
            else
                d(TAG_BROADCAST, "--- Other States");
        }

        else{
            d(TAG_BROADCAST, "No interested to Received intent with action");
        }
    }
}
