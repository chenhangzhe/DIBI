package cn.suozhi.DiBi.c2c.view.chat.qiyu;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by zhoujianghua on 2015/8/25.
 */
public class DemoPreferences {
    private static Context context;
    private final static String KEY_YX_USER_ID = "YSF_FOREIGN_ID";
    private final static String KEY_YX_USER_NAME = "YSF_FOREIGN_NAME";

    private final static String KEY_YSF_DEV_TAG = "YSF_DEV_TAG";
    private final static String KEY_YSF_APP_KEY = "YSF_APP_KEY";
    private final static String KEY_YSF_APP_KEY_REL = "YSF_APP_KEY_REL";

    private final static String KEY_YSF_PRODUCT_ID = "YSF_PRODUCT_ID";

    public static void init(Context context) {
        DemoPreferences.context = context.getApplicationContext();
    }

    public static String getYsfUserId() {
        return getString(KEY_YX_USER_ID);
    }

    public static void saveYsfUserId(String userId) {
        saveString(KEY_YX_USER_ID, userId);
    }

    public static String getYsfUserName() {
        return getString(KEY_YX_USER_NAME);
    }

    public static void saveYsfUserName(String userId) {
        saveString(KEY_YX_USER_NAME, userId);
    }

    public static String getYsfAppKey() {
        String key = devTag() ? KEY_YSF_APP_KEY : KEY_YSF_APP_KEY_REL;
        String appKey = getString(key);
        if (TextUtils.isEmpty(appKey)) {
            // 七鱼SDK KEY
            appKey = devTag() ? "9481a5d55c13d2a3ea1c0f94dc7f9a7b" : "9481a5d55c13d2a3ea1c0f94dc7f9a7b";
        }
        return appKey;
    }

    public static void saveYsfAppKey(String userId) {
        String key = devTag() ? KEY_YSF_APP_KEY : KEY_YSF_APP_KEY_REL;
        saveString(key, userId);
    }

    public static boolean devTag() {
        return getBoolean(KEY_YSF_DEV_TAG, false);
    }

    public static void saveDevTag(boolean dev) {
        saveBoolean(KEY_YSF_DEV_TAG, dev);
    }

    public static String getProductId() {
        return getString(KEY_YSF_PRODUCT_ID);
    }

    public static void saveProductId(String produceId) {
        saveString(KEY_YSF_PRODUCT_ID, produceId);
    }

    private static boolean getBoolean(String key, boolean value) {
        return getSharedPreferences().getBoolean(key, value);
    }

    private static void saveBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    private static void saveString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("Unicorn.Demo", Context.MODE_PRIVATE);
    }
}
