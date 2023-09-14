package com.espoir.glidedslib.transform.gray;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.espoir.glidedslib.GlideBitmapFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class GrayPicTransform extends BitmapTransformation {
    private static final String ID = "com.bumptech.glide.transformations.GrayPicTransform";
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return convertGreyImg(toTransform);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }

    public boolean equals(Object o) {
        return o instanceof GrayPicTransform;
    }

    public int hashCode() {
      return ID.hashCode();
    }

    /**
     * 彩图转换成灰色图片
     *
     * @param img
     * @return
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey;
                if (pixels[width * i + j] == 0) {
                    continue;
                } else grey = pixels[width * i + j];
                int alpha = grey & 0xFF000000;
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        //创建空的bitmap时，格式一定要选择ARGB_4444,或ARGB_8888,代表有Alpha通道，RGB_565格式的不显示灰度
        Bitmap result = GlideBitmapFactory.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }
}
