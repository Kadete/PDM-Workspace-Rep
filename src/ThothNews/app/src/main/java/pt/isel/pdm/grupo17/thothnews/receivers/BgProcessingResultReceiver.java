package pt.isel.pdm.grupo17.thothnews.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class BgProcessingResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public BgProcessingResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}