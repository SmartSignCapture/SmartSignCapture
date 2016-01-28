package at.fhs.smartsigncapture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.webkit.URLUtil;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import at.fhs.smartsigncapture.model.Contact;

/**
 * Created by MartinTiefengrabner on 17/09/15.
 */
public class ImageUtils {


    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);


        final Canvas canvas = new Canvas(outputBitmap);

        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }


    public static File downloadContactImage(Contact c, Context context){
        boolean result = false;

        File imgFolder = getUserPhotoDir(context);

        String imageFile = URLUtil.guessFileName(c.getImageURL(), null, null);

        File img = new File(imgFolder.getAbsoluteFile()+"/"+imageFile);

        if(!img.exists()) {

            try {
                URL url = new URL(c.getImageURL());

                URLConnection conn = url.openConnection();

                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(img);
                fos.write(baf.toByteArray());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return img;
    }

    private static File getUserPhotoDir(Context context){
        File folder = new File(getImageDir(context).getAbsoluteFile()+"/user");

        if(!folder.exists()){
            folder.mkdirs();
        }

        return folder;
    }

    private static File getImageDir(Context context){
        File folder = new File(context.getFilesDir().getAbsoluteFile()+"/img");

        if(!folder.exists()){
            folder.mkdirs();
        }

        return folder;
    }

}
