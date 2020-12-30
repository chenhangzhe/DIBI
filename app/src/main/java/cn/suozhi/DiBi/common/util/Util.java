package cn.suozhi.DiBi.common.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.login.LoginActivity;

/**
 * 获取手机相关信息 -- 工具类
 */
public class Util {

    public static SimpleDateFormat sdf_y2Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
    public static SimpleDateFormat sdf_y2S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    public static SimpleDateFormat sdf_y2s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static SimpleDateFormat sdf_y2m = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static SimpleDateFormat sdf_y2d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat sdf_M2s = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
    public static SimpleDateFormat sdf_H2s = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static DecimalFormat Format0 = new DecimalFormat("0");
    public static DecimalFormat Format2 = new DecimalFormat("0.00");
    public static DecimalFormat Format4 = new DecimalFormat("0.0000");
    public static DecimalFormat Format8 = new DecimalFormat("0.00000000");

    public static DecimalFormat Format00 = new DecimalFormat("00");
    public static DecimalFormat Format_Percent = new DecimalFormat("#0.00%");

    public static long MIN_1 = 60 * 1000L;
    public static long HOUR_1 = 60 * MIN_1;
    public static long Day_1 = 24 * HOUR_1;

    /**
     * 获取手机屏幕宽度
     */
    public static int getPhoneWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取手机屏幕高度
     */
    public static int getPhoneHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private static int measureWidgetHeight(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, w);
        return v.getMeasuredHeight();
    }

    private static int measureWidgetHeight(Context context, int resId) {
        return measureWidgetHeight(LayoutInflater.from(context).inflate(resId, null));
    }

    /**
     * 获取控件高度 -- ConstraintLayout中可能出现测量不准确的情况
     */
    public static int getWidgetHeight(View... vs) {
        if (vs == null) {
            return 0;
        }
        int h = 0;
        for (View v : vs) {
            h += measureWidgetHeight(v);
        }
        return h;
    }

    public static int getWidgetHeight(Context context, int... ids) {
        if (ids == null) {
            return 0;
        }
        int h = 0;
        for (int id : ids) {
            h += measureWidgetHeight(context, id);
        }
        return h;
    }

    /**
     * 获取控件宽度 -- ConstraintLayout中可能出现测量不准确的情况
     */
    public static int getWidgetWidth(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, w);
        return v.getMeasuredWidth();
    }

    public static int toPx(Context context, int unit, float value) {
        return (int) TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

    /**
     * dp 转 px
     */
    public static int dp2px(Context context, float dpValue) {
        return toPx(context, TypedValue.COMPLEX_UNIT_DIP, dpValue);
    }

    /**
     * px 转 dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp 转 px
     */
    public static int sp2px(Context context, float spValue) {
        return toPx(context, TypedValue.COMPLEX_UNIT_SP, spValue);
    }

    /**
     * 获取通知栏高度
     */
    public static int getStatusHeight(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(id);
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationHeight(Context context) {
        if (hasNavigationBar(context)) {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
            return rs.getDimensionPixelSize(id);
        }
        return 0;
    }

    /**
     * 是否显示导航栏
     */
    public static boolean hasNavigationBar(Context context) {
        boolean hasNavigation = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigation = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigation = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigation = true;
            }
        } catch (Exception e) {
        }
        return hasNavigation;
    }

    public static int getContentHeight(Context context) {
        return getPhoneHeight(context) - getStatusHeight(context) - getNavigationHeight(context);
    }

    /**
     * 获取App名称
     */
    public static String getAppName(Context context) {
        String appName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
            appName = (String) packageManager.getApplicationLabel(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 获取设备唯一编号
     */
    public static String getUniqueId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId + Build.SERIAL;
    }

    /**
     * 获取App版本号
     */
    public static String getVersion(Context context) {
        String versionName = "1.0.0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取App版本数字
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 是否需要更新
     * return  0 -- 不需要更新、1 -- 需要更新、2 -- 需要强制更新
     */
    public static int isNeedUpdate(String thisVersion, String newVersion) {
        try {
            if (thisVersion.equals(newVersion)) {
                return 0;
            }
            int times = 100;//倍率
            float currTimes;//当前倍数
            float v1 = 0;//当前版本计算值
            float v2 = 0;//服务器版本计算值
            currTimes = 0.01F;
            String[] ver1 = thisVersion.split("\\.");
            for (int i = ver1.length - 1; i >= 0; i--) {
                v1 += Integer.parseInt(ver1[i]) * currTimes;
                currTimes *= times;
            }
            currTimes = 0.01F;
            String[] ver2 = newVersion.split("\\.");
            if (Integer.parseInt(ver1[0]) < Integer.parseInt(ver2[0])) {//有重大更新
                return 2;
            }
            for (int i = ver1.length - 1; i >= 0; i--) {
                v2 += Integer.parseInt(ver2[i]) * currTimes;
                currTimes *= times;
            }
            if (v2 > v1) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            if (thisVersion.equals(newVersion)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public static String getValue(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    /**
     * 弹出键盘
     */
    public static void showKeyboard(final View v) {
        v.post(() -> {
            InputMethodManager imm = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        });
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 复制到粘贴板
     */
    public static void copyBoard(Context context, String str) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("android", str));
    }

    public static int getColor(Context context, int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    /**
     * 多个颜色id转成颜色值
     */
    public static int[] getColor(Context context, int... colorId) {
        if (context == null || colorId.length == 0) {
            return new int[]{};
        }
        int len = colorId.length;
        int[] color = new int[len];
        for (int i = 0; i < len; i++) {
            color[i] = getColor(context, colorId[i]);
        }
        return color;
    }

    public static Drawable getDrawable(Context context, int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    public static int getResId(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public static int getResId(Context context, String name, String type, int zero) {
        int id = getResId(context, name, type);
        return id == 0 ? zero : id;
    }

    public static String getString(Context context, int stringId) {
        return getString(context, stringId, "");
    }

    public static String getString(Context context, int stringId, String zero) {
        return stringId == 0 ? zero : context.getString(stringId);
    }

    public static String getStringInResId(Context context, String name) {
        return getStringInResId(context, name, "");
    }

    public static String getStringInResId(Context context, String name, String zero) {
        int id = getResId(context, name, "string");
        return id == 0 ? zero : context.getString(id);
    }

    public static Class getClassByName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    /**
     * 获取相册路径
     */
    public static String getDCIMPath() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dir.isDirectory()) {
            String path = dir.getAbsolutePath();
            File[] list = dir.listFiles();
            if (list == null) {
                return path;
            }
            for (int i = 0; i < list.length; i++) {
                File d = list[i];
                if (d.isDirectory() && "Camera".equals(d.getName())) {
                    path = d.getAbsolutePath();
                }
            }
            return path;
        }
        return null;
    }

    public static String getCodeText(Context context, long code, String msg) {
        String codeName = ("code" + code).replace("-", "_");
        return getStringByCodeName(context, codeName, msg);
    }

    public static String getCodeTextString(Context context, String code, String msg) {
        String codeName = ("code" + code).replace("-", "_");
        return getStringByCodeName(context, codeName, msg);
    }

    private static String getStringByCodeName(Context context, String name, String msg) {
        String s = getStringInResId(context, name, msg);
        if (TextUtils.isEmpty(s) || s.equals(msg)) {
            return s;
        }
        if (!TextUtils.isEmpty(msg) && msg.contains(Constant.Strings.Code_Split)) {
            int i0 = msg.indexOf(Constant.Strings.Code_Split);
            int i1 = msg.lastIndexOf(Constant.Strings.Code_Split);
            if (i0 >= 0 && i1 > i0) {
                return String.format(s, msg.substring(i0 + 1, i1));
            }
        }
        return s;
    }

    public static String getCodeText(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "";
        }
        if (!msg.contains("@")) {
            return msg;
        }
        String start = "@" + SharedUtil.getLanguage4Socket(context) + ":";
        int is = msg.indexOf(start);
        if (is < 0) {
            is = msg.indexOf("@SC:");
        }
        int si = is + start.length();
        int ei = si;
        for (int i = si; i < msg.length(); i++) {
            String s = msg.substring(i, i + 1);
            if ("@".equals(s) || "]".equals(s)) {
                ei = i;
                break;
            }
        }
        return msg.substring(si, ei).trim();
    }

    public static void checkLogin(Context context, long code) {
        if (Constant.Int.Please_Login == code) {
            SharedUtil.clearData(context);
            context.startActivity(new Intent(context, LoginActivity.class)
                    .putExtra("goBack", false));
        }
    }

    public static void checkLoginSocket(Context context, String code) {
        if (Constant.Int.Socket_No_Login.equals(code) || Constant.Int.Socket_Please_Login.equals(code)) {
            SharedUtil.clearData(context);
            context.startActivity(new Intent(context, LoginActivity.class)
                    .putExtra("goBack", false));
        }
    }

    public static boolean isLink(String url) {
        return !TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * 限制EditText小数点后位数 -- 最少1位
     */
    public static void editDigitPoint(final EditText et, final int pointNum) {
        if (et == null || pointNum < 1) {
            return;
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.toString().contains(".")) {
                    int dotIndex = s.toString().indexOf(".");
                    int dotLastIndex = s.toString().lastIndexOf(".");
                    int maxIndex = dotIndex + pointNum + 1;
                    if (dotIndex != dotLastIndex) {//出现两个点
                        et.setText(s.toString().substring(0, dotLastIndex));
                        et.setSelection(et.length());
                    } else if (s.length() > maxIndex) {
                        et.setText(s.toString().substring(0, maxIndex));
                        et.setSelection(et.length());
                    }
                }
            }
        });
    }

    /**
     * 监听EditText -- 显示删除按钮
     */
    public static void editListenerWithDel(EditText et, final View del) {
        if (et == null || del == null) {
            return;
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                del.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 监听EditText -- Enter键收起键盘
     */
    public static void editListenerEnter(EditText et) {
        if (et == null) {
            return;
        }
        et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(v);
                return true;
            }
            return false;
        });
    }

    /**
     * 监听EditText -- Enter键将焦点传递给etNext
     */
    public static void editListenerEnterNext(EditText et, EditText etNext) {
        if (et == null || etNext == null) {
            return;
        }
        et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                etNext.requestFocus();
                return true;
            }
            return false;
        });
    }

    /**
     * 监听EditText -- 输入文字后清空下方错误提示
     */
    public static void editListenerError(EditText et, TextView tv) {
        if (et == null || tv == null) {
            return;
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && tv.length() > 0) {
                    tv.setText(null);
                }
            }
        });
    }

    /**
     * 移除EditText中的非法字符
     */
    public static String editRemoveIllegal(EditText et) {
        return editRemoveIllegal(et, "\"", "\\");
    }

    /**
     * 移除EditText中的非法字符 -- 传入限制字符
     */
    public static String editRemoveIllegal(EditText et, String... limit) {
        if (et == null || et.length() == 0) {
            return "";
        }
        String text = et.getText().toString();
        if (limit == null || limit.length == 0) {
            return text;
        }
        for (int i = 0; i < limit.length; i++) {
            if (text.contains(limit[i])) {
                et.setText(text.replace(limit[i], ""));
            }
        }
        return et.getText().toString();
    }

    public static String editRemoveIllegal(String text, String... limit) {
        if (limit == null || limit.length == 0 || TextUtils.isEmpty(text)) {
            return text;
        }
        for (int i = 0; i < limit.length; i++) {
            if (text.contains(limit[i])) {
                text = text.replace(limit[i], "");
            }
        }
        return text;
    }


    /**
     * 数字格式化为pointNum位小数
     */
    public static String formatDecimal(double d, int pointNum) {
        return new DecimalFormat(getDecimalFormat(pointNum)).format(d);
    }

    /**
     * 对字符串数字进行格式化
     */
    public static String formatDecimal(String decimal, int point) {
        if (point < 0 || TextUtils.isEmpty(decimal)) {
            return decimal;
        }
        if (point == 0) {
            if (decimal.contains(".")) {
                return decimal.substring(0, decimal.lastIndexOf("."));
            } else {
                return decimal;
            }
        }
        if (decimal.contains(".")) {
            int index = decimal.lastIndexOf(".");
            if (index + point >= decimal.length()) {//小数点后位数不够截取
                return formatDecimal(decimal + "0", point);
            } else {
                return decimal.substring(0, index + point + 1);
            }
        } else {
            return formatDecimal(decimal + ".0", point);
        }
    }

    /**
     * 移除小数点后多余的0
     */
    public static String removePointZero(String tiny, boolean forcePoint) {
        if (TextUtils.isEmpty(tiny) || !tiny.contains(".")) {
            return tiny;
        }
        if (tiny.startsWith(".")) {
            tiny = "0" + tiny;
        }
        if (tiny.endsWith(".")) {
            return forcePoint ? tiny + "0" : tiny.substring(0, tiny.length() - 1);
        }
        if (!tiny.endsWith("0")) {
            return tiny;
        }
        int index = tiny.lastIndexOf(".");
        if (index == tiny.length() - 2) {
            return forcePoint ? tiny : tiny.substring(0, index);
        }
        return removePointZero(tiny.substring(0, tiny.length() - 1), forcePoint);
    }

    /**
     * 返回0.0000
     */
    private static String getDecimalFormat(int pointNum) {
        StringBuffer format = new StringBuffer("0");
        for (int i = 0; i < pointNum; i++) {
            if (i == 0) {
                format.append(".");
            }
            format.append("0");
        }
        return format.toString();
    }

    /**
     * 字符串中间部分转换为*  - 用于手机号或邮箱中间加*
     */
    public static String addStarInMiddle(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        if (s.contains("@")) {
            int index = s.lastIndexOf("@");
            if (index < 4) {
                return s;
            }
            return s.substring(0, 3) + getStar(3) +
                    s.substring(index);
        }
        int len = s.length();
        if (len < 7) {
            return s;
        }
        return s.substring(0, 3) + getStar(len - 6) +
                s.substring(len - 3);
    }

    /**
     * 字符串中间部分转换为*  - 前后各留3个字符
     */
    public static String addStarLast3(String s) {
        if (TextUtils.isEmpty(s) || s.length() < 4) {
            return s;
        }
        int len = s.length();
        if (len < 7) {
            return s.substring(0, 3) + getStar(len - 3);
        }
        return s.substring(0, 3) + getStar(len - 6) +
                s.substring(len - 3);
    }

    /**
     * 返回 length 长度的*号
     */
    public static String getStar(int length) {
        StringBuffer star = new StringBuffer();
        for (int i = 0; i < length; i++) {
            star.append("*");
        }
        return star.toString();
    }

    /**
     * 数字每隔4位加空格
     */
    public static String addSpaceInNum(String cardNum) {
        if (TextUtils.isEmpty(cardNum)) {
            return cardNum;
        }
        StringBuffer number = new StringBuffer();
        for (int i = 0; i < cardNum.length(); i++) {
            number.append(cardNum.charAt(i));
            if ((i + 1) % 4 == 0) {
                number.append(" ");
            }
        }
        return number.toString();
    }

    public static String addNumSeparate(double num) {
        return addNumSeparate(num + "");
    }

    public static String addNumSeparate(double num, int decimal) {
        return addNumSeparate(formatDecimal(num, decimal));
    }

    public static String addNumSeparate(long num) {
        return addNumSeparate(num + "");
    }

    /**
     * 按英文格式给数字添加,
     */
    public static String addNumSeparate(String num) {
        if (TextUtils.isEmpty(num)) {
            return num;
        }
        int endPosition = num.length();
        if (num.contains(".")) {
            endPosition = num.indexOf(".");
        }
        if (endPosition < 4) {
            return num;
        }

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < num.length(); i++) {
            if (i < endPosition) {
                if ((endPosition - i) % 3 == 0 && i > 0) {
                    result.append(",");
                }
                result.append(num.charAt(i));
            } else {
                result.append(num.substring(i));
                break;
            }
        }
        return result.toString();
    }

    /**
     * 初始化TabLayout -- 系统默认
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB) {
        tabInit(tabLayout, TAB, 0);
    }

    /**
     * 初始化TabLayout -- 系统默认
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB, int select) {
        if (tabLayout == null || TAB == null) {
            return;
        }
        for (int i = 0; i < TAB.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(TAB[i]), i == select);
        }
    }

    /**
     * 初始化TabLayout -- 系统默认
     */
    public static void tabList(TabLayout tabLayout, List<String> list) {
        tabList(tabLayout, list, 0);
    }

    /**
     * 初始化TabLayout -- 系统默认
     */
    public static void tabList(TabLayout tabLayout, List<String> list, int select) {
        tabInit(tabLayout, list2Array(list), select);
    }

    /**
     * 初始化TabLayout -- 只添加View
     */
    public static void tabInit(TabLayout tabLayout, int size, int layoutId, int select) {
        if (tabLayout == null || size <= 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            View tab = LayoutInflater.from(tabLayout.getContext()).inflate(layoutId, null);
            tabLayout.addTab(tabLayout.newTab().setCustomView(tab), i == select);
        }
    }

    /**
     * 初始化TabLayout
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB, int layoutId, int nameId) {
        tabInit(tabLayout, TAB, layoutId, nameId, 0);
    }

    /**
     * 初始化TabLayout - 设置选中下标
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB, int layoutId, int nameId, int select) {
        tabInit(tabLayout, TAB, layoutId, nameId, 0, select, 0, 0);
    }

    /**
     * 初始化TabLayout - 给指示器设置权重
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB, int layoutId, int nameId,
                               int indId, float indWeight) {
        tabInit(tabLayout, TAB, layoutId, nameId, 0, 0, indId, indWeight);
    }

    /**
     * 初始化TabLayout - 设置文本高度
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB, int layoutId, int nameId, int nameHeight,
                               int select, int indId, float indWeight) {
        tabInit(tabLayout, TAB, layoutId, nameId, nameHeight, select, indId, indWeight, 0);
    }

    /**
     * 初始化TabLayout
     */
    public static void tabInit(TabLayout tabLayout, String[] TAB, int layoutId, int nameId, int nameHeight,
                               int select, int indId, float indWeight, int gapId) {
        if (tabLayout == null || TAB == null) {
            return;
        }
        for (int i = 0; i < TAB.length; i++) {
            View tab = LayoutInflater.from(tabLayout.getContext()).inflate(layoutId, null);
            TextView name = tab.findViewById(nameId);
            if (name != null) {
                setLayoutHeight(name, nameHeight);
                name.setText(TAB[i]);
            }
            if (indId != 0) {
                setConstraintWeight(tab.findViewById(indId), indWeight);
            }
            if (gapId != 0) {
                setLinearWeight(tab.findViewById(gapId), i == 0 ? 0.05F : 0.2F);
            }
            tabLayout.addTab(tabLayout.newTab().setCustomView(tab), i == select);
        }
    }

    public static void tabList(TabLayout tabLayout, List<String> list, int layoutId, int nameId,
                               int select, int indId, float indWeight) {
        tabList(tabLayout, list, layoutId, nameId, 0, select, indId, indWeight, 0);
    }

    /**
     * 初始化TabLayout - 数据为List
     */
    public static void tabList(TabLayout tabLayout, List<String> list, int layoutId, int nameId,
                               int nameHeight, int select, int indId, float indWeight, int gapId) {
        tabInit(tabLayout, list2Array(list), layoutId, nameId, nameHeight, select, indId, indWeight, gapId);
    }

    public static String[] list2Array(List<String> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        String[] TAB = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            TAB[i] = list.get(i);
        }
        return TAB;
    }

    public static List<String> array2List(String[] TAB) {
        if (TAB == null) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(TAB));
    }

    /**
     * 初始化TabLayout - 只有两个Tab时左右多留间隙
     */
    public static void tab2Init(TabLayout tabLayout, String[] TAB, int layoutId, int nameId, int gapId) {
        tab2Init(tabLayout, TAB, layoutId, nameId, gapId, 0, 0);
    }

    public static void tab2Init(TabLayout tabLayout, String[] TAB, int layoutId, int nameId,
                                int indId, float indWeight, int gapId) {
        tabInit(tabLayout, TAB, layoutId, nameId, 0, 0, indId, indWeight, gapId);
    }

    public static void setLayoutHeight(View v, int height) {
        if (v == null || height <= 0) {
            return;
        }
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent instanceof ConstraintLayout) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) v.getLayoutParams();
            lp.height = height;
            v.setLayoutParams(lp);
        } else if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
            lp.height = height;
            v.setLayoutParams(lp);
        } else if (parent instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
            lp.height = height;
            v.setLayoutParams(lp);
        } else if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
            lp.height = height;
            v.setLayoutParams(lp);
        }
    }

    public static void setConstraintWeight(View v, float weight) {
        if (v == null) {
            return;
        }
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) v.getLayoutParams();
        lp.horizontalWeight = weight;
        v.setLayoutParams(lp);
    }

    public static void setLinearWeight(View v, float weight) {
        if (v == null) {
            return;
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
        lp.weight = weight;
        v.setLayoutParams(lp);
    }

    /**
     * 初始化TabLayout - 设置文本高度
     */
    public static void tabC2cInit(TabLayout tabLayout, String[] TAB, int layoutId, int nameId, int nameHeight,
                                  int select, int indId, float indWeight) {
        //添加tab之前清除tablayout中的所有tab
        tabLayout.removeAllTabs();
        tabInit(tabLayout, TAB, layoutId, nameId, nameHeight, select, indId, indWeight, 0);
    }

    public static String formatTime(long time) {
        return formatTime(time, sdf_y2s);
    }

    public static String formatTime(long time, SimpleDateFormat out) {
        if (out == null || time < 0) {
            return time + "";
        }
        return out.format(new Date(time));
    }

    public static String formatTime(String time) {
        return formatTime(time, sdf_y2Z, sdf_y2s);
    }

    /**
     * 格式化时间
     */
    public static String formatTime(String time, SimpleDateFormat in, SimpleDateFormat out) {
        if (TextUtils.isEmpty(time) || in == null || out == null) {
            return time;
        }
        try {
            long t = in.parse(time).getTime();
            return formatTime(t, out);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public static long parseTime(String time) {
        return parseTime(time, sdf_y2s);
    }

    public static long parseTime(String time, SimpleDateFormat in) {
        return parseTime(time, in, -1);
    }

    /**
     * 解析时间
     */
    public static long parseTime(String time, SimpleDateFormat in, long zero) {
        if (TextUtils.isEmpty(time) || in == null) {
            return zero;
        }
        try {
            return in.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return zero;
        }
    }


    public static String formatTinyDecimal(double d) {
        return formatTinyDecimal(d, true);
    }

    public static String formatTinyDecimal(double d, boolean forcePoint) {
        return formatTinyDecimal(d, 8, forcePoint, false);
    }

    /**
     * 避免数字太小而用科学计数法显示
     *
     * @param forcePoint -- 强制显示小数点
     *                   -- true表示 0.00 格式化为0.0 -- false表示 0.00 格式化为0
     * @param isCut      -- 格式化时是否是截取
     *                   -- false表示 使用默认的四舍五入
     */
    public static String formatTinyDecimal(double d, int point, boolean forcePoint, boolean isCut) {
        String t;
        if (d <= -0.001 || d >= 0.001) {
            t = removePointZero(formatDecimal(d + "", point), forcePoint);
        } else {
            t = removePointZero(isCut ? formatFloor(d, point) : formatDecimal(d, point), forcePoint);
        }
        if (t.toUpperCase().contains("E")) {
            String t4 = removePointZero(isCut ? formatFloor(d, 4) : Format4.format(d), forcePoint);
            if (t4.toUpperCase().contains("E")) {
                return removePointZero(isCut ? formatFloor(d, 0) : Format0.format(d), forcePoint);
            }
            return t4;
        } else if (t.contains(".")) {
            return removePointNum(t);
        }
        return t;
    }

    /**
     * 对较长数字进行截取
     */
    private static String removePointNum(String tiny) {
        if (tiny.length() > 13) {
            int index = tiny.lastIndexOf(".");
            int point = tiny.length() - index - 1;
            if (point > 8) {
                return removePointNum(tiny.substring(0, index + 9));
            } else if (point > 4) {
                return removePointNum(tiny.substring(0, index + 5));
            } else if (point > 2) {
                return removePointNum(tiny.substring(0, index + 3));
            } else if (point > 1) {
                return removePointNum(tiny.substring(0, index + 2));
            }
            return tiny.substring(0, index);
        }
        return tiny;
    }

    /**
     * 格式化小数时采用截取
     */
    public static String formatFloor(double d, int point) {
        DecimalFormat format = new DecimalFormat(getDecimalFormat(point));
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(d);
    }

    /**
     * d + offset / 10 ^ point
     */
    public static String changeDecimal(String d, int offset, int point) {
        try {
            if (TextUtils.isEmpty(d) || point < 0) {
                return d;
            }
            if (d.startsWith(".")) {
                return changeDecimal("0" + d, offset, point);
            }
            double decimal = Double.parseDouble(d);
            double off = offset * Math.pow(10, -point);
            if (off < 0 && decimal < Math.abs(off)) {
                return d;
            }
            return formatDecimal(decimal + off, point);
        } catch (Exception e) {
            return d;
        }
    }

    /**
     * 字符串转数字  -- 处理"xx." / ".xx"
     */
    public static double parseDouble(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                return 0;
            }
            if (s.startsWith(".")) {
                return parseDouble("0" + s);
            }
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 字符串转数字
     */
    public static int parseInt(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                return 0;
            }
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 字符串转数字
     */
    public static long parseLong(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                return 0;
            }
            return Long.parseLong(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 反射修改参数
     */
    public static void fieldSet(String fieldName, Object obj, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射获取参数
     */
    public static Object fieldGet(String fieldName, Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String null2Default(String text) {
        return null2Default(text, "");
    }

    /**
     * text若为空 则替换为defaultText
     */
    public static String null2Default(String text, String defaultText) {
        return TextUtils.isEmpty(text) ? defaultText : text;
    }

    public static String getLargerString(String... s) {
        if (s == null || s.length == 0) {
            return null;
        }
        int length = 0, index = -1;
        for (int i = 0; i < s.length; i++) {
            if (!TextUtils.isEmpty(s[i])) {
                if (length < s[i].length()) {
                    length = s[i].length();
                    index = i;
                }
            }
        }
        if (index < 0) {
            return null;
        }
        return s[index];
    }

    /**
     * 是否有通知权限 - context最好是ApplicationContext
     */
    public static boolean permCheckNotify(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 检测并申请权限
     * 在Fragment中如需回调 请使用方法{@link #permReq(Fragment, int, String...)}
     */
    public static boolean permCheckReq(Activity activity, int code, String... perm) {
        if (perm.length == 0) {
            return true;
        }
        boolean check = true;
        for (String p : perm) {
            if (ActivityCompat.checkSelfPermission(activity, p)
                    != PackageManager.PERMISSION_GRANTED) {
                check = false;
                break;
            }
        }
        if (!check) {
            ActivityCompat.requestPermissions(activity, perm, code);
        }
        return check;
    }

    /**
     * 仅检测权限
     */
    public static boolean permCheck(Activity activity, String... perm) {
        if (perm.length == 0) {
            return true;
        }
        boolean check = true;
        for (String p : perm) {
            if (ActivityCompat.checkSelfPermission(activity, p)
                    != PackageManager.PERMISSION_GRANTED) {
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * Fragment请求权限
     */
    public static void permReq(Fragment fragment, int code, String... perm) {
        if (perm.length == 0) {
            return;
        }
        fragment.requestPermissions(perm, code);
    }

    /**
     * 检测已申请权限
     */
    public static boolean permCheck(int[] results) {
        boolean check = true;
        for (int r : results) {
            if (r != PackageManager.PERMISSION_GRANTED) {
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * WebView加载Html代码
     */
    public static void loadHtmlData(WebView wv, String data) {
        wv.loadData(data, "text/html; charset=UTF-8", null);
    }

    public static String getCurrentTime() {
        return sdf_y2S.format(new Date(System.currentTimeMillis()));
    }

    public static String getLogData(Context context, String url, String error) {
        StringBuffer log = new StringBuffer()
                .append("Version Code: ")
                .append(getVersionCode(context))
                .append("\nVersion Name: ")
                .append(getVersion(context))
                .append("\nSDK INT: ")
                .append(Build.VERSION.SDK_INT)
                .append("\nSDK RELEASE: ")
                .append(Build.VERSION.RELEASE)

                .append("\n\nBRAND: ")
                .append(Build.BRAND)
                .append("\nDEVICE: ")
                .append(Build.DEVICE)
                .append("\nHARDWARE: ")
                .append(Build.HARDWARE)

                .append("\n\nTime: ")
                .append(getCurrentTime())
                .append("\nURL: ")
                .append(url)
                .append("\nERROR: ")
                .append(error)
                .append("\n\n\n");

        return log.toString();
    }

    public static void saveLog(FragmentActivity context, String url, String error) {
        Log.e("loge", "Exception: " + error);
        if (context == null) {
            return;
        }
        try {
            CacheUtil.saveLog(context, "Server_log", getLogData(context, url, error));
        } catch (Exception e) {
            CacheUtil.saveLog(context, "Exception", e.getMessage());
        }
    }


    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean hasText(String str) {

        if (str == null || "".equals(str)) {
            return false;
        }
        return true;
    }
}
