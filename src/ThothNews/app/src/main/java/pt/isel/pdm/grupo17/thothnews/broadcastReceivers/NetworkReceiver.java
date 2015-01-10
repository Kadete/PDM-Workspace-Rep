package pt.isel.pdm.grupo17.thothnews.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateActionsHandler;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.SettingsUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_BROADCAST;

public class NetworkReceiver extends BroadcastReceiver {

    private static NetworkInfo.State previousState;
    private static boolean firstTime = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        d(TAG_BROADCAST, "Received intent with action = " + intent.getAction());
        String action = intent.getAction();
        if (! action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            return;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni_wifi = cm.getActiveNetworkInfo();
        if (ni_wifi == null)
            return;

        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            if(firstTime) {
                previousState = (ni_wifi.getState() == NetworkInfo.State.CONNECTING) ? NetworkInfo.State.DISCONNECTED : NetworkInfo.State.CONNECTED ;
                firstTime = false;
                ThothUpdateActionsHandler.cleanNotifications(context);
            }

            if(ni_wifi.isConnected() && previousState == NetworkInfo.State.DISCONNECTED){
                ThothUpdateService.startActionNewsUpdate(context);
                previousState = NetworkInfo.State.CONNECTED;
            }
            else if(previousState == NetworkInfo.State.CONNECTED){
                previousState = NetworkInfo.State.DISCONNECTED;
            }
        }
    }

    public static boolean checkConnection(Context context, boolean toastOnFail){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean connected = (networkInfo != null && networkInfo.isConnected());
        if(toastOnFail && !connected)
            Toast.makeText(context, context.getString(R.string.toast_no_connectivity), Toast.LENGTH_SHORT).show();
        else if((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && !SettingsUtils.enableDataMobile))
            return false;
        return connected;
    }

}