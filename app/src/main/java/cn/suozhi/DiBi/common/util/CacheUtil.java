package cn.suozhi.DiBi.common.util;

import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.IOException;

import cn.suozhi.DiBi.Constant;

/**
 * 缓存工具类
 */
public class CacheUtil {

    public static final String TEXT_CACHE_DIR = Environment.getExternalStorageDirectory() +
            Constant.Strings.Cache_Dir + "/cache";//缓存路径

    /**
     * 保存日志
     */
    public static boolean saveLog(FragmentActivity context, String fileName, String data){
        return writeText(context, fileName + ".txt", data, true);
    }

    private static boolean writeText(FragmentActivity context, String fileName, String data, boolean append) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        File cacheDir = new File(TEXT_CACHE_DIR);
        if (!cacheDir.exists() && FileUtil.isSdCardMounted()) {
            cacheDir.mkdirs();
        }
        if (cacheDir.exists()) {
            File file = new File(TEXT_CACHE_DIR, fileName);
            try {
                FileUtil.writeTextFile(file, data, append);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtil.initToast(context, "存储失败");
                return false;
            }
        }
        ToastUtil.initToast(context, "存储失败");
        return false;
    }

    /**
     * 获取缓存大小
     */
    public static long cacheSize() {
        return FileUtil.getFileSize(new File(TEXT_CACHE_DIR));
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        FileUtil.delFile(new File(TEXT_CACHE_DIR));
    }
}
