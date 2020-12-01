package com.example.bulletinboard.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CompressUtil {
    public static Bitmap compressBitmap(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 1, byteArrayOutputStream);
        /*
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int options = 10;
        while (byteArrayOutputStream.toByteArray().length/1024 > 100){
            byteArrayOutputStream.reset();
            options -= 1;
            image.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
        }
        */
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream, null, null);
        return bitmap;
    }
}
