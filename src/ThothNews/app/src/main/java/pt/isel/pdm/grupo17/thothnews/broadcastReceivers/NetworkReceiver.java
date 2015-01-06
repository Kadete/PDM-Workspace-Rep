package pt.isel.pdm.grupo17.thothnews.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_BROADCAST;

public class NetworkReceiver extends BroadcastReceiver {

    private static NetworkInfo.State previousState;
    private static boolean firstTime = true;
    private static boolean enableDataMobile = false;

    public static final String ACTION_DATA_MOBILE_CHANGE = "action_data_mobile_change";
    public static final String DATA_MOBILE_EXTRA = "data_mobile_extra";

    @Override
    public void onReceive(Context context, Intent intent) {

        d(TAG_BROADCAST, "Received intent with action = " + intent.getAction());
        String action = intent.getAction();
        if (! action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && !action.equals(ACTION_DATA_MOBILE_CHANGE))
            return;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni_wifi = cm.getActiveNetworkInfo();
        if (ni_wifi == null)
            return;

        if(action.equals(ACTION_DATA_MOBILE_CHANGE)){
            Bundle extra = intent.getExtras();
            enableDataMobile = extra.getBoolean(DATA_MOBILE_EXTRA);
        }

        if(ni_wifi.getType() == ConnectivityManager.TYPE_MOBILE && !enableDataMobile){
            Toast.makeText(context,"Application Data Mobile Disable",Toast.LENGTH_SHORT).show();
            return;
        }


        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

//            d(TAG_BROADCAST, "Network info  = " + ni_wifi);
//            d(TAG_BROADCAST, "Wifi connected  = " + ni_wifi.isConnected());
//            d(TAG_BROADCAST, "Wifi state  = " + ni_wifi.getDetailedState());

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