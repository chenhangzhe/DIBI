package cn.suozhi.DiBi.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.custom.GlideRoundTransform;

/**
 * Glide工具类
 * 禁止调用{@link ImageView#setTag(Object)}给{@link ImageView}设置tag
 * 如有需求 请调用{@link ImageView#setTag(int, Object)}
 */
public class GlideUtil {

    public static void glide(Activity activity, ImageView iv, int imgId) {
        Glide.with(activity).load(imgId).into(iv);
    }

    public static void glide(Activity activity, ImageView iv, int imgId, RequestOptions ro) {
        Glide.with(activity)
                .load(imgId)
                .apply(ro)
                .into(iv);
    }

    /**
     * URL
     */
    public static void glide(Activity activity, ImageView iv, String url) {
        Glide.with(activity).load(url).into(iv);
    }

    public static void glidePE(Activity activity, ImageView iv, String url) {
        glidePE(activity, iv, url, R.mipmap.img_loading, R.mipmap.img_fail);
    }

    public static void glidePE(Activity activity, ImageView iv, String url, int placeId, int errorId) {
        glidePE(activity, iv, url, placeId, errorId, 0);
    }

    public static void glidePE(Activity activity, ImageView iv, String url, int placeId, int errorId, int corner) {
        glide(activity, iv, url, getOption(placeId, errorId, corner));
    }

    public static void glide(Activity activity, ImageView iv, String url, RequestOptions ro) {
        Glide.with(activity)
                .load(url)
                .apply(ro)
                .into(iv);
    }

    public static void glideData(Activity activity, ImageView iv, String url) {
        glide(activity, iv, url, getOptionData());
    }

    public static void glideDataPE(Activity activity, ImageView iv, String url, int placeId, int errorId) {
        glide(activity, iv, url, getOptionData(placeId, errorId));
    }

    public static void glideCircle(Activity activity, ImageView iv, String url) {
        glide(activity, iv, url, getOptionCircle());
    }

    public static void glideCirclePE(Activity activity, ImageView iv, String url, int placeId, int errorId) {
        glide(activity, iv, url, getOptionCircle(placeId, errorId));
    }

    /**
     * Bitmap
     */
    public static void glide(Activity activity, ImageView iv, Bitmap bm) {
        glide(activity, iv, bm, getOption(0, 0));
    }

    public static void glide(Activity activity, ImageView iv, Bitmap bm, RequestOptions ro) {
        Glide.with(activity)
                .load(bitmap2Bytes(bm))
                .apply(ro)
                .into(iv);
    }

    public static void glidePE(Activity activity, ImageView iv, Bitmap bm) {
        glidePE(activity, iv, bm, R.mipmap.img_loading, R.mipmap.img_fail);
    }

    public static void glidePE(Activity activity, ImageView iv, Bitmap bm, int placeId, int errorId) {
        glide(activity, iv, bm, getOption(placeId, errorId));
    }

    /**
     * File
     */
    public static void glide(Activity activity, ImageView iv, File file) {
        Glide.with(activity)
                .load(Uri.fromFile(file))
                .into(iv);
    }

    public static void glidePE(Activity activity, ImageView iv, File file) {
        glidePE(activity, iv, file, R.mipmap.img_loading, R.mipmap.img_fail);
    }

    public static void glidePE(Activity activity, ImageView iv, File file, int placeId, int errorId) {
        Glide.with(activity)
                .load(Uri.fromFile(file))
                .apply(getOption(placeId, errorId))
                .into(iv);
    }

    /**
     * 获取Glide占位图、失败图Option
     */
    public static RequestOptions getOption() {
        return getOption(R.mipmap.img_loading, R.mipmap.img_fail);
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getOption(int placeId, int errorId) {
        RequestOptions options = new RequestOptions();
        if (placeId != 0) {
            options.placeholder(placeId);
        }
        if (errorId != 0) {
            options.error(errorId);
        }
        return options;
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getOption(int placeId, int errorId, int corner) {
        if (corner > 0) {
            RequestOptions options = RequestOptions.bitmapTransform(new GlideRoundTransform(corner));
            if (placeId != 0) {
                options.placeholder(placeId);
            }
            if (errorId != 0) {
                options.error(errorId);
            }
            return options;
        }
        return getOption(placeId, errorId);
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getOptionCircle(int placeId, int errorId) {
        RequestOptions options = RequestOptions.bitmapTransform(new CircleCrop());
        if (placeId != 0) {
            options.placeholder(placeId);
        }
        if (errorId != 0) {
            options.error(errorId);
        }
        return options;
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getOptionCircle() {
        return getOptionCircle(0, 0);
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getOptionData(int placeId, int errorId) {
        RequestOptions options =  new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA);
        if (placeId != 0) {
            options.placeholder(placeId);
        }
        if (errorId != 0) {
            options.error(errorId);
        }
        return options;
    }

    @SuppressLint("CheckResult")
    public static RequestOptions getOptionData() {
        return getOptionCircle(0, 0);
    }

    /**
     * Bitmap转byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bas);
        return bas.toByteArray();
    }


    /**
     * 获取缓存大小
     */
    public static long cacheSize(Context context) {
        return FileUtil.getFileSize(Glide.getPhotoCacheDir(context));
    }

    /**
     * 清空缓存
     */
    public static void clearCache(final Context context) {
        Glide.get(context).clearMemory();
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }
}
