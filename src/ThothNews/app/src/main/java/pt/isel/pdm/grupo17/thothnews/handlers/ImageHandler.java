package pt.isel.pdm.grupo17.thothnews.handlers;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.BitmapUtils;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;

public class ImageHandler extends Handler {

    private SetViewAndUpdateHandler _h;

    public ImageHandler(SetViewAndUpdateHandler h, Looper l){
        super(l);
        _h = h;
    }

    public void handleMessage (Message msg){
        Data data = (Data)msg.obj;
        URL url;
        try {
            url = new URL(data.avatarUri);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            InputStream s = c.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(s);

            long id = Long.parseLong(ParseUtils.getUriSegment(data.routeUri, 1));
            File photoFile = BitmapUtils.createImageFile(id, data.path);
            String mCurrentPhotoPath = photoFile.getAbsolutePath();
            BitmapUtils.storeBitmapToFile(bm, mCurrentPhotoPath);

            ContentValues values = new ContentValues();
            /** WARNING: Teachers.AVATAR_PATH == Students.AVATAR_PATH **/
            values.put(ThothContract.Path_Auxiliar.AVATAR_PATH, mCurrentPhotoPath);

            if(data.routeUri !=null)
                _h.update(data.routeUri, values, null, null);

            _h.publishImage(data.im, bm);

        } catch (IOException ignored) {
            Log.d("IMAGE_HANDLER", "handleMessage ignored: " + ignored.getMessage());
        }
    }

    public void fetchImage(ImageView im, String avatarUri, Uri routeUri, String path){
        Message m = obtainMessage();
        m.obj = new Data(avatarUri, im, routeUri, path);
        sendMessage(m);
    }

    static class Data{
        public final String avatarUri;
        public final ImageView im;
        private Uri routeUri;
        public String path;

        public Data(String avatarUri, ImageView im, Uri routeUri, String path){
            this.avatarUri = avatarUri;
            this.im = im;
            this.routeUri = routeUri;
            this.path = path;
        }
    }
}