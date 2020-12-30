package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 二维码工具类
 * 生成二维码是耗时操作，请在子线程中调用。
 */
public class QRCodeUtil {

    //生成
    public static final Map<DecodeHintType, Object> HINTS_D = new EnumMap<>(DecodeHintType.class);

    //扫描
    public static final Map<EncodeHintType, Object> HINTS_E = new EnumMap<>(EncodeHintType.class);

    static {
        List<BarcodeFormat> allFormats = new ArrayList<>();
        allFormats.add(BarcodeFormat.AZTEC);
        allFormats.add(BarcodeFormat.CODABAR);
        allFormats.add(BarcodeFormat.CODE_39);
        allFormats.add(BarcodeFormat.CODE_93);
        allFormats.add(BarcodeFormat.CODE_128);
        allFormats.add(BarcodeFormat.DATA_MATRIX);
        allFormats.add(BarcodeFormat.EAN_8);
        allFormats.add(BarcodeFormat.EAN_13);
        allFormats.add(BarcodeFormat.ITF);
        allFormats.add(BarcodeFormat.MAXICODE);
        allFormats.add(BarcodeFormat.PDF_417);
        allFormats.add(BarcodeFormat.QR_CODE);
        allFormats.add(BarcodeFormat.RSS_14);
        allFormats.add(BarcodeFormat.RSS_EXPANDED);
        allFormats.add(BarcodeFormat.UPC_A);
        allFormats.add(BarcodeFormat.UPC_E);
        allFormats.add(BarcodeFormat.UPC_EAN_EXTENSION);

        HINTS_D.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);
        HINTS_D.put(DecodeHintType.POSSIBLE_FORMATS, allFormats);
        HINTS_D.put(DecodeHintType.CHARACTER_SET, "utf-8");

        HINTS_E.put(EncodeHintType.CHARACTER_SET, "utf-8");
        HINTS_E.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        HINTS_E.put(EncodeHintType.MARGIN, 0);
    }

    public static String syncDecodeQRCode(Context context, Uri uri) {
        return syncDecodeQRCode(Uri2Bitmap(context, uri));
    }

    public static String syncDecodeQRCode(Bitmap bitmap) {
        Result result = null;
        RGBLuminanceSource source = null;
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            source = new RGBLuminanceSource(width, height, pixels);
            result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), HINTS_D);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            if (source != null) {
                try {
                    result = new MultiFormatReader()
                            .decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)), HINTS_D);
                    return result.getText();
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }
    }

    public static Bitmap syncEncodeQRCode(String content, int size) {
        return syncEncodeQRCode(content, size, Color.BLACK, Color.WHITE, null, false);
    }

    public static Bitmap syncEncodeQRCode(String content, int size, Bitmap logo) {
        return syncEncodeQRCode(content, size, logo, false);
    }

    public static Bitmap syncEncodeQRCode(String content, int size, Bitmap logo, boolean hasStroke) {
        return syncEncodeQRCode(content, size, Color.BLACK, Color.WHITE, logo, hasStroke);
    }

    public static Bitmap syncEncodeQRCode(String content, int size, int foregroundColor,
                                          int backgroundColor, Bitmap logo, boolean hasStroke) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, size, size, HINTS_E);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * size + x] = foregroundColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return addLogoToQRCode(bitmap, logo, hasStroke);
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap addLogoToQRCode(Bitmap src, Bitmap logo, boolean hasStroke) {
        if (src == null || logo == null) {
            return src;
        }
        logo = squareBitmap(logo);

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        float scaleFactor = srcWidth * 1.0F / 6 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            int left = (srcWidth - logoWidth) / 2;
            int top = (srcHeight - logoHeight) / 2;

            if (hasStroke) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                float stroke = srcWidth / 14.0F;
                paint.setStrokeWidth(stroke);
                RectF rect = new RectF(left - stroke / 2, top - stroke / 2,
                        (srcWidth + logoWidth + stroke) / 2, (srcHeight + logoHeight + stroke) / 2);
                canvas.drawRoundRect(rect, logoWidth / 6, logoWidth / 6, paint);
            }

            canvas.drawBitmap(logo, left, top, null);
//            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 图片裁剪成圆角方形
     */
    public static Bitmap squareBitmap(Bitmap bitmap) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        int m = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (bitmap.getWidth() != bitmap.getHeight()) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, m, m);
        }
        Bitmap tar = Bitmap.createBitmap(m, m, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(tar);
        RectF rect = new RectF(0, 0, m, m);
        c.drawRoundRect(rect, m / 6, m / 6, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, 0, 0, p);
        return tar;
    }


    public static Bitmap Uri2Bitmap(Context context, Uri uri){
        try {
            int width = context.getResources().getDisplayMetrics().widthPixels / 3;
            Bitmap bitmap = decodeBitmap(context, uri, width, width);
            if (getBitmapSize(bitmap) > 1024 * 1024L) {
                return decodeBitmap(bitmap, width, width);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            String path = getPath(context, uri);
            File file = new File(path);
            if (file.exists() && file.isFile() && file.length() > 256 * 1024L) {
                return getBitmap(path, (int) (file.length() / (256 * 1024L) + 1));
            } else {
                return getBitmap(path);
            }
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, int scaleWidth, int scaleHeight) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//表示只解码图片的边缘信息，用来得到图片的宽高
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        int oldWidth = options.outWidth;
        int oldHeight = options.outHeight;
        int bWidth = oldWidth / scaleWidth;
        int bHeight = oldHeight / scaleHeight;
        if (oldWidth > scaleWidth || oldHeight > scaleHeight) {
            int size = bWidth < bHeight ? bWidth : bHeight;
            if (size == 0) {
                size = (bWidth + bHeight) / 2;
            }
            options.inSampleSize = size;
        }
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
    }

    public static Bitmap decodeBitmap(Bitmap bitmap, int scaleWidth, int scaleHeight){
        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();
        int bWidth = oldWidth / scaleWidth;
        int bHeight = oldHeight / scaleHeight;
        int inSampleSize = bWidth < bHeight ? bWidth : bHeight;
        if (inSampleSize < 2) {
            inSampleSize = 2;
        }
        return ThumbnailUtils.extractThumbnail(bitmap, oldWidth / inSampleSize, oldHeight / inSampleSize);
    }

    public static int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static Bitmap getBitmap(String bitmapFile){
        return BitmapFactory.decodeFile(bitmapFile);
    }

    public static Bitmap getBitmap(String bitmapFile, int inSampleSize) {
        return BitmapFactory.decodeFile(bitmapFile, getBitmapOption(inSampleSize));
    }

    private static BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public static boolean isWHEqual(Context context, Uri uri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//表示只解码图片的边缘信息，用来得到图片的宽高
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            return options.outWidth == options.outHeight;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean saveBitmap2File(Bitmap bitmap, String filePath) {
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
