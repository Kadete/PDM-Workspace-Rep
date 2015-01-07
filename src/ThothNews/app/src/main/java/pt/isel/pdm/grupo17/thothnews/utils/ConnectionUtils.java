package pt.isel.pdm.grupo17.thothnews.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;

public class ConnectionUtils {
    public static boolean checkConnection(Context context, boolean toastOnFail){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean connected = (networkInfo != null && networkInfo.isConnected());
        if(toastOnFail && !connected)
            Toast.makeText(context, context.getString(R.string.toast_no_connectivity), Toast.LENGTH_SHORT).show();
        return connected;
    }
}
