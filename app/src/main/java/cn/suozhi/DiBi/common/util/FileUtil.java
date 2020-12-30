package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件处理工具类
 */
public class FileUtil {

    /**
     * 从流中读取文本
     */
    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return sb.toString();
    }

    /**
     * 读取文件
     */
    public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    /**
     * 读取assets
     */
    public static String readTextAssets(Context context, String name) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(name);
            text = readTextInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    /**
     * 读取raw
     */
    public static String readTextRaw(Context context, int rawId) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(rawId);
            text = readTextInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    /**
     * 将文本内容写入文件
     */
    public static void writeTextFile(File file, String str, boolean append) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            fos.write(str.getBytes("UTF-8"));
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static void delFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        delFile(new File(path));
    }

    /**
     * 删除文件
     */
    public static void delFile(File file) {
        if (file == null) {
            return;
        }
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    delFile(files[i]);
                }
            }
            file.delete();
        }
    }


    /**
     * 获取文件或文件夹的大小
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        if (file.exists()) {//文件存在
            if (file.isFile()) {//是文件
                return file.length();
            } else if (file.isDirectory()) {//是目录
                long size = 0;
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) { //遍历所有文件
                    size += getFileSize(files[i]);//迭代
                }
                return size;
            }
            return 0;
        } else {
            return 0;
        }
    }

    /**
     * sd卡挂载且可用
     */
    public static boolean isSdCardMounted() {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    // 创建一个临时目录，用于复制临时文件，如assets目录下的离线资源文件
    public static String createTmpDir(Context context, String dirName) {
        String tmpDir = Environment.getExternalStorageDirectory().toString() + "/" + dirName;
        if (!FileUtil.makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(dirName).getAbsolutePath();
            if (!FileUtil.makeDir(dirName)) {
                throw new RuntimeException("create model resources dir failed :" + tmpDir);
            }
        }
        return tmpDir;
    }

    public static boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    public static void copyFromAssets(AssetManager assets, String source, String dest, boolean isCover)
            throws IOException {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }

    public static String copyAssetsFile(Context context, String tmpDir, String sourceName)
            throws IOException {
        AssetManager assets = context.getApplicationContext().getAssets();
        String destPath = createTmpDir(context, tmpDir);
        String destName = destPath + "/" + sourceName;
        copyFromAssets(assets, sourceName, destName, false);
        return destName;
    }
}
