package pt.isel.pdm.grupo17.thothnews.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    private static final String APP_DIR_TEACHERS = "ThothNews/Teachers";
    private static final String APP_DIR_STUDENTS = "ThothNews/Students";

    public static final String IMAGE_EXTENSION = ".jpg";

    public static enum EnumModel {
        DIR_PATH_TEACHER, DIR_PATH_STUDENT
    }

    public static String initStoragePath(Context context, EnumModel enumPath) {
        String mBitmapStoragePath = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                String root = context.getExternalFilesDir(null).getCanonicalPath();
                File bitmapStorageDir;
                switch (enumPath){
                    case DIR_PATH_TEACHER:
                        bitmapStorageDir = new File(root, BitmapUtils.APP_DIR_TEACHERS);
                        break;
                    case DIR_PATH_STUDENT:
                        bitmapStorageDir = new File(root, BitmapUtils.APP_DIR_STUDENTS);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                bitmapStorageDir.mkdirs();
                mBitmapStoragePath = bitmapStorageDir.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mBitmapStoragePath;
    }

    public static Bitmap getBitmapFromFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    public static boolean storeBitmapToFile(Bitmap bitmap, String filePath) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static File createImageFile(long id, String path) throws IOException {
        File image = new File(path+"/"+id + IMAGE_EXTENSION);
        image.createNewFile();
        return image;
    }

    public static class LoadBitmapTask extends AsyncTask<String, String, Bitmap> {

        private ImageView mImageView;

        public LoadBitmapTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String selfiePath = params[0];

            return BitmapUtils.getBitmapFromFile(selfiePath);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
            super.onPostExecute(result);
        }
    }

}
