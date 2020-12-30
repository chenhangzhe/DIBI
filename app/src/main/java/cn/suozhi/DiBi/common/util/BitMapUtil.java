package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.suozhi.DiBi.Constant;

/**
 * Bitmap处理工具类
 */
public class BitMapUtil {

    public static final long K128 = 128 * 1024L * 8;
    public static final long K256 = K128 * 2;
    public static final long K512 = K128 * 4;
    public static final long K640 = K128 * 5;
    public static final long K768 = K128 * 6;
    public static final long M1 = K128 * 8;
    public static final long M5 = K128 * 8 * 5;
    public static final String CACHE_DIR = Environment.getExternalStorageDirectory() +
            Constant.Strings.Cache_Dir + "/cache";//缓存路径

    /**
     * 图片二次采样 -- Uri
     */
    public static Bitmap decodeBitmap(InputStream is, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap decodeBitmap(String bitmapFile, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//表示只解码图片的边缘信息，用来得到图片的宽高
        BitmapFactory.decodeFile(bitmapFile, options);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(bitmapFile, options);
    }

    public static Bitmap decodeBitmap(String bitmapFile) {
        return decodeBitmap(bitmapFile, K512);
    }

    public static Bitmap decodeBitmap(String bitmapFile, long limit) {
        if (TextUtils.isEmpty(bitmapFile)) {
            return null;
        }
        File file = new File(bitmapFile);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        long len = file.length() * 8;
        if (len > limit) {
            return decodeBitmap(bitmapFile, (int) Math.ceil(len * 1.0F / limit));
        }
        return BitmapFactory.decodeFile(bitmapFile);
    }

    public static Bitmap decodeBitmap(Bitmap bitmap, int inSampleSize) {
        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();
        if (inSampleSize < 2) {
            inSampleSize = 2;
        }
        return ThumbnailUtils.extractThumbnail(bitmap, oldWidth / inSampleSize, oldHeight / inSampleSize);
    }

    public static Bitmap decodeBitmap(Bitmap bitmap){
        return decodeBitmap(bitmap, K512);
    }

    public static Bitmap decodeBitmap4Big(Bitmap bitmap){
        return decodeBitmap(bitmap, M1);
    }


    /**
     * 获取View截图  -- 为避免操作被回收的Bitmap 此处不再调用setDrawingCacheEnabled(false)
     */
    public static Bitmap getDrawingCache(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        v.draw(new Canvas(bitmap));
        return bitmap;
    }

    /**
     * 保存View为文件
     */
    public static boolean saveView2File(View v, Bitmap bm, String filePath) {
        Bitmap draw;
        try {
            draw = getDrawingCache(v);
        } catch (Exception e) {
            draw = null;
        }
        return saveBitmap2File(draw == null ? bm : draw, filePath);
    }

    /**
     * 保存View为文件
     */
    public static boolean saveView2File(View v, String filePath) {
        Bitmap draw = getDrawingCache(v);
        return saveBitmap2File(draw, filePath);
    }

    /**
     * 保存Bitmap为文件
     */
    public static boolean saveBitmap2File(Bitmap bitmap, String filePath) {
        if (bitmap == null) {
            return false;
        }
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 图片加入到相册并通知刷新
     */
    public static void insertGallery(Context context, String filePath) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            String name;
            if (filePath.contains("/")) {
                name = filePath.substring(filePath.lastIndexOf("/") + 1);
            } else {
                name = filePath;
            }
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    filePath, name, null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + filePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片二次采样 -- 裁剪成方形
     */
    public static Bitmap cropSquare(Bitmap bitmap, int scaleWidth, int scaleHeight) {
        int min = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
        return ThumbnailUtils.extractThumbnail(bitmap, min, min);
    }

    /**
     * 图片二次采样 -- 裁剪成方形
     */
    public static Bitmap cropSquare(Bitmap bitmap) {
        return cropSquare(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 图片转Bitmap
     */
    public static Bitmap getBitmap(String bitmapFile) {
        return BitmapFactory.decodeFile(bitmapFile);
    }

    /**
     * 图片转Bitmap
     */
    public static Bitmap getBitmap(String bitmapFile, int inSampleSize) {
        return BitmapFactory.decodeFile(bitmapFile, getBitmapOption(inSampleSize));
    }

    /**
     * 获取bitmap大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
            return bitmap.getAllocationByteCount();
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount();
        }*/
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 获取圆形图片-会自动裁剪多余部分
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        int w = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap tar = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(tar);
        c.drawCircle(w / 2, w / 2, w / 2, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, 0, 0, p);
        return tar;
    }

    /**
     * Bitmap转byte[]
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bas);
        return bas.toByteArray();
    }

    /**
     * byte[]转Bitmap
     */
    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static InputStream Bitmap2IS(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = Bitmap2BAOS(bm);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static ByteArrayOutputStream Bitmap2BAOS(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos;
    }

    public static Bitmap decodeBitmap(Bitmap bm, long limit) {
        int size = getBitmapSize(bm);
        if (size > limit) {
            return decodeBitmap(bm, (int) Math.ceil(size * 1.0F / limit));
        } else {
            return bm;
        }
    }

    /**
     * Uri转Bitmap
     */
    public static Bitmap Uri2Bitmap(Context context, Uri uri) {
        return Uri2Bitmap(context, uri, K512);
    }

    public static Bitmap Uri2Bitmap4Big(Context context, Uri uri) {
        return Uri2Bitmap(context, uri, M1);
    }

    public static Bitmap Uri2Bitmap(Context context, Uri uri, long limit) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null) {
                return null;
            }
            long len = is.available() * 8L;
            if (len > limit) {
                int inSampleSize = (int) (len / limit) + 1;
                return decodeBitmap(is, inSampleSize);
            }
            return decodeBitmap(BitmapFactory.decodeStream(is), limit);
        } catch (Exception e) {
            e.printStackTrace();
            String path = getPath(context, uri);
            File file = new File(path);
            long len = limit / 8;
            if (file.exists() && file.isFile() && file.length() > len) {
                return getBitmap(path, (int) (file.length() / len + 1));
            }
            return getBitmap(path);
        }
    }

    public static Bitmap Uri2BitmapFLAC(Context context, Uri uri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return getBitmap(getPath(context, uri));
        }
    }

    /**
     * 异常情况下的备选方案
     */
    private static String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private static BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }


    /**
     * 对图片进行压缩处理
     */
    public static Bitmap compressImage(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}
