package com.espoir.glidedslib.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.espoir.glidedslib.GlideDsl;

import java.security.MessageDigest;


/**
 * glide自定义图片遮罩
 */
public class PhotoMaskTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID =
            "jp.wasabeef.glide.transformations.PhotoMaskTransformation." + VERSION;

    private static Paint paint = new Paint();
    private int maskResId;

    static {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
    }

    /**
     * @param maskId If you change the mask file, please also rename the mask file, or Glide will get
     *               the cache with the old mask. Because key() return the same values if using the
     *               same make file name. If you have a good idea please tell us, thanks.
     */
    public PhotoMaskTransformation(int maskId) {
        this.maskResId = maskId;
    }


    @Override
    public String toString() {
        return "MaskTransformation(maskId=" + maskResId + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PhotoMaskTransformation &&
                ((PhotoMaskTransformation) o).maskResId == maskResId;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + maskResId * 10;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + maskResId).getBytes(CHARSET));
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        Drawable mask = ContextCompat.getDrawable(GlideDsl.sAppContext, maskResId);
        bitmap.setDensity(toTransform.getDensity());
        Canvas canvas = new Canvas(bitmap);
        mask.setBounds(0, 0, width, height);
        mask.draw(canvas);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        return bitmap;
    }
}