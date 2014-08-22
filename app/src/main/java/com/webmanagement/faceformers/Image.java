package com.webmanagement.faceformers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Banpot.S on 8/21/14 AD.
 */
public class Image {

    // decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f) {
        Bitmap result = null;
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 150;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            result = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return result;
        } catch (FileNotFoundException e) {
            //Log.e(TAG, "ERROR", e);
        }
        return result;
    }
    public static Bitmap scaleCropToFit(Bitmap original, int targetWidth, int targetHeight){
        //Need to scale the image, keeping the aspect ration first
        int width = original.getWidth();
        int height = original.getHeight();

        float widthScale = (float) targetWidth / (float) width;
        float heightScale = (float) targetHeight / (float) height;
        float scaledWidth;
        float scaledHeight;

        int startY = 0;
        int startX = 0;

        if (widthScale > heightScale) {
            scaledWidth = targetWidth;
            scaledHeight = height * widthScale;
            //crop height by...
            startY = (int) ((scaledHeight - targetHeight) / 2);
        } else {
            scaledHeight = targetHeight;
            scaledWidth = width * heightScale;
            //crop width by..
            startX = (int) ((scaledWidth - targetWidth) / 2);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, (int) scaledWidth, (int) scaledHeight, true);

        Bitmap resizedBitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);
        return resizedBitmap;
    }
}
