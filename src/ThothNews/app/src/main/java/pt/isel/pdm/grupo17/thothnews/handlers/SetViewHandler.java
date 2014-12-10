package pt.isel.pdm.grupo17.thothnews.handlers;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

public class SetViewHandler extends Handler {

    public SetViewHandler(Looper l){
        super(l);
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

    public final class Data {
        public final ImageView im;
        public final Bitmap bm;
        public Data(ImageView im, Bitmap bm) {
            this.im = im;
            this.bm = bm;
        }
    }
}
