package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;

//import com.qiyukf.nimlib.sdk.NIMClient;
//import com.qiyukf.nimlib.sdk.auth.AuthService;
//import com.qiyukf.unicorn.api.Unicorn;

import java.util.Locale;

import cn.suozhi.DiBi.wychat.mixpush.DemoCache;

/**
 * 数据存储 {@link SharedPreferences} & 多语言管理 工具类
 * 用户相关信息(token、id等)保存在 User 的SharedPreferences内，退出时清空
 * 持久化信息(手机区号、语言等)保存在 Constant 的SharedPreferences内，代码内不清空
 */
public class SharedUtil {

    public static String USER_NONE = null;//未登录
    public static long DAY_1 = 24L * 60 * 60 * 1000;
    public static long DAY_30 = 30 * DAY_1;

    private static final int PRIVATE = Context.MODE_PRIVATE;

    public static String getLanguage(Context context) {
        String language = getString(context, "Constant", "language", "default");
        if ("default".equals(language)) {
            language = getDefaultLanguage(context);
        }
        return language;
    }

    public static String getLanguage4Socket(Context context) {
        String language = getLanguage(context);
        String lang;
        switch (language) {
            case "cht"://中文繁体
                lang = "TC";
                break;
            case "en"://英文
                lang = "EN";
                break;
            case "chs"://中文简体
            default://系统语言
                lang = "SC";
                break;
        }
        return lang;
    }

    public static String getLanguage4Url(Context context) {
        String language = getLanguage(context);
        String lang;
        switch (language) {
            case "cht"://中文繁体
                lang = "zh_TW";
                break;
            case "en"://英文
                lang = "en_US";
                break;
            case "chs"://中文简体
            default://系统语言
                lang = "zh_CN";
                break;
        }
        return lang;
    }

    /**
     * 获取当前语言的文字描述
     */
    public static String getLanguageInText(Context context) {
        return getTextLanguage(getLanguage(context));
    }

    private static String getTextLanguage(String language) {
        String lang;
        switch (language) {
            case "chs":
                lang = "简体中文";
                break;
            case "cht":
                lang = "繁體中文";
                break;
            case "en":
                lang = "English";
                break;
            default:
                lang = "Unknown";
                break;
        }
        return lang;
    }

    private static String getDefaultLanguage(Context context) {
        String language;
        switch (Locale.getDefault().getCountry()) {
            case "CN"://大陆地区
            case "SG"://新加坡
                language = "chs";
                break;
            case "HK"://香港
            case "MO"://澳门
            case "TW"://台湾
                language = "cht";
                break;
            case "US"://美国
            case "GB"://英国
            case "AU"://澳大利亚
            default://使用英语的国家和地区多达上百个，未匹配到的一律显示英语
                language = "en";
                break;
        }
        putLanguage(context, language);
        return language;
    }

    public static void putLanguage(Context context, String language) {
        putString(context, "Constant", "language", language);
    }

    /**
     * 获取语言Locale
     */
    public static Locale getLocale(Context context) {
        String language = getLanguage(context);
        Locale locale;
        switch (language) {
            case "cht"://中文繁体
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case "en"://英语
                locale = Locale.ENGLISH;
                break;
            case "chs"://中文简体
            default://系统语言
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
        }
        return locale;
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources resources = context.getResources();
            Locale locale = getLocale(context);
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            config.setLocales(new LocaleList(locale));
            return context.createConfigurationContext(config);
        } else {
            return context;
        }
    }

    public static void onLanguageChange(Context context) {
        Resources res = context.getResources();
        Locale locale = getLocale(context);
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList ll = new LocaleList(locale);
            LocaleList.setDefault(ll);
            config.setLocales(ll);
            context.createConfigurationContext(config);
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    /**
     * 清除本地保存数据
     */
    public static void clearData(Context context) {
        clear(context, "User");
//        NIMClient.getService(AuthService.class).logout();
//        Unicorn.logout();
        clear(context, "IMTToken");

    }


    /**
     * 保存token
     * @param context
     * @param token
     */
    public static void saveToken(Context context, String token) {
        saveToken(context, "User", "token", token);
    }


    public static void saveToken(Context context, String name, String itemName, String token) {
        context.getSharedPreferences(name, PRIVATE).edit()
                .putString(itemName, token)
                .apply();
    }

    /**
     * 获取Token
     */
    public static String getToken(Context context) {
        return getToken(context, USER_NONE);
    }

    public static String getToken(Context context, String defaultValue) {
        return getString(context, "User", "token", defaultValue);
    }

    public static long getUserId(Context context) {
        return isLogin(context) ? getLong(context, "User", "userId", -1) : -1;
    }

    public static boolean isLogin(Context context) {
        return !TextUtils.isEmpty(getToken(context));
    }

    public static boolean isLogin(String token) {
        return !TextUtils.isEmpty(token) && !token.equals(USER_NONE);
    }

    public static void putBool(Context context, String name, String itemName, boolean value) {
        context.getSharedPreferences(name, PRIVATE).edit()
                .putBoolean(itemName, value)
                .apply();
    }

    public static boolean getBool(Context context, String name, String itemName, boolean defaultValue) {
        return context.getSharedPreferences(name, PRIVATE).getBoolean(itemName, defaultValue);
    }

    public static void putString(Context context, String name, String itemName, String value) {
        context.getSharedPreferences(name, PRIVATE).edit()
                .putString(itemName, value)
                .apply();
    }

    public static String getString(Context context, String name, String itemName, String defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return context.getSharedPreferences(name, PRIVATE).getString(itemName, defaultValue);
    }

    public static void putInt(Context context, String name, String itemName, int value) {
        context.getSharedPreferences(name, PRIVATE).edit()
                .putInt(itemName, value)
                .apply();
    }

    public static int getInt(Context context, String name, String itemName, int defaultValue) {
        return context.getSharedPreferences(name, PRIVATE).getInt(itemName, defaultValue);
    }

    public static void putLong(Context context, String name, String itemName, long value) {
        context.getSharedPreferences(name, PRIVATE).edit()
                .putLong(itemName, value)
                .apply();
    }

    public static long getLong(Context context, String name, String itemName, long defaultValue) {
        return context.getSharedPreferences(name, PRIVATE).getLong(itemName, defaultValue);
    }

    public static SharedPreferences getPreferences(Context context, String name) {
        return context.getSharedPreferences(name, PRIVATE);
    }

    public static void remove(Context context, String name, String itemName) {
        context.getSharedPreferences(name, PRIVATE).edit()
                .remove(itemName)
                .apply();
    }

    public static void clear(Context context, String name) {
        context.getSharedPreferences(name, PRIVATE)
                .edit().clear().apply();
    }


    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";

    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    static SharedPreferences getSharedPreferences() {
        return DemoCache.getContext().getSharedPreferences("IMTToken", Context.MODE_PRIVATE);
    }
}
