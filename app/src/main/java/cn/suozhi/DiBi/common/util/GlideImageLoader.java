package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
//import com.qiyukf.unicorn.api.ImageLoaderListener;
//import com.qiyukf.unicorn.api.UnicornImageLoader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by zhoujianghua on 2016/12/27.
 */

public class GlideImageLoader /*implements UnicornImageLoader */{
    private Context context;

    public GlideImageLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
//    @Override
    public Bitmap loadImageSync(String uri, int width, int height) {
        return null;
    }

//    @Override
//    public void loadImage(String uri, int width, int height, final ImageLoaderListener listener) {
//        if (width <= 0 || height <= 0) {
//            width = height = Integer.MIN_VALUE;
//        }
//
//        Glide.with(context).load(uri).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                if (listener != null) {
//                    Bitmap bitmap = drawable2Bitmap(resource);
//                    if (bitmap != null) {
//                        listener.onLoadComplete(bitmap);
//                    }
//                }
//            }
//
//            @Override
//            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//
//            }
//        });
//
//    }


    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
}
