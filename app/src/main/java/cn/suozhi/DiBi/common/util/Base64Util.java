package cn.suozhi.DiBi.common.util;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;

/**
 * Base64编码（加密、解密）
 */
public class Base64Util {

    public static final String Regex_Feed = "[\\s*\t\n\r]";

    /**
     * 加密
     */
    public static String encode(String str) {
        try {
            String result = Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
            return result.replaceAll(Regex_Feed, "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = new String(Base64.encode(str.getBytes(), Base64.DEFAULT));
        return result.replaceAll(Regex_Feed, "");
    }

    public static String encodeByte(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        String result = Base64.encodeToString(bytes, Base64.DEFAULT);
        return result.replaceAll(Regex_Feed, "");
    }

    /**
     * 对象转JSON后加密
     */
    public static String encodeObject(Object object) {
        Gson gson = new GsonBuilder().create();
        return encode(gson.toJson(object));
    }

    /**
     * 解密
     */
    public static String decode(String strBase64) {
        byte[] bytes = Base64.decode(strBase64, Base64.DEFAULT);
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }

    /**
     * 解密返回byte[]
     */
    public static byte[] decodeToBytes(String strBase64) {
        return Base64.decode(strBase64, Base64.DEFAULT);
    }

    public static byte[] decodeToBytes(byte[] strBase64) {
        return Base64.decode(strBase64, Base64.DEFAULT);
    }
}
