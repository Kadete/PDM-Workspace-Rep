package pt.isel.pdm.grupo17.thothnews.handlers;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

public class SetViewAndUpdateHandler extends Handler {

    ContentResolver resolver;

    public SetViewAndUpdateHandler(Looper mainLooper, ContentResolver resolver) {
        super(mainLooper);
        this.resolver = resolver;
    }

    public void handleMessage (Message msg){
        Data data = (Data)msg.obj;
        data.im.setImageBitmap(data.bm);
    }

    public void publishImage(ImageView im, Bitmap bm){
        Message m = obtainMessage();
        m.obj = new Data(im,bm);
        sendMessage(m);
    }

    public void update(Uri uri, ContentValues values, String where, String []whereArgs) {
        resolver.update(uri,values, where, whereArgs);
    }

    final class Data {
        public final ImageView im;
        public final Bitmap bm;
        public Data(ImageView im, Bitmap bm) {
            this.im = im;
            this.bm = bm;
        }
    }
}
