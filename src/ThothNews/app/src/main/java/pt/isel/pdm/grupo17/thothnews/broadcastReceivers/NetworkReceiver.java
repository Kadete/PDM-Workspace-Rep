package pt.isel.pdm.grupo17.thothnews.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;

import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_BROADCAST;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;

public class NetworkReceiver extends BroadcastReceiver {

    static NetworkInfo.State previousState;
    static boolean firstTime = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        d(TAG_BROADCAST, "Received intent with action = " + intent.getAction());

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni_wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

//            NetworkInfo ni_mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            d(TAG_BROADCAST, "Network info  = " + ni_wifi);
            d(TAG_BROADCAST, "Wifi connected  = " + ni_wifi.isConnected());
            d(TAG_BROADCAST, "Wifi state  = " + ni_wifi.getDetailedState());

            if(firstTime) {
                previousState = (ni_wifi.getState() == NetworkInfo.State.CONNECTING) ? NetworkInfo.State.DISCONNECTED : NetworkInfo.State.CONNECTED ;
                firstTime = false;
                ThothUpdateService.cleanNotifications(context);
            }

            if(ni_wifi.isConnected() && previousState == NetworkInfo.State.DISCONNECTED){

                ThothUpdateService.startActionNewsUpdate(context);
                d(TAG_BROADCAST, "--- register notification " + ni_wifi.getDetailedState().name());
                previousState = NetworkInfo.State.CONNECTED;
            }
            else if(previousState == NetworkInfo.State.CONNECTED){

                d(TAG_BROADCAST, "--- unregister notification " + ni_wifi.getDetailedState().name());
                previousState = NetworkInfo.State.DISCONNECTED;
            }
        }
    }
}
