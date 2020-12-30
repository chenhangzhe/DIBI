package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;


import cn.suozhi.DiBi.AppContext;

/**
 * 获取定义尺寸 颜色 字符串.工具类
 */

public class ResUtils {

    /**
     * 获取定义的尺寸
     *
     * @param id
     * @return
     */
    public static float getDimension(@DimenRes int id) {
        return AppContext.appContext.getResources().getDimension(id);
    }

    /**
     * 获取定义的尺寸像素值
     *
     * @param id
     * @return
     */
    public static int getDimensionPixelSize(@DimenRes int id) {
        return AppContext.appContext.getResources().getDimensionPixelSize(id);
    }

    /**
     * 获取定义的颜色值
     *
     * @param id
     * @return
     */
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(AppContext.appContext, id);
    }

    /**
     * 获取定义的颜色值
     *
     * @param id
     * @return
     */
    public static ColorStateList getColorStateList(@ColorRes int id) {
        return AppContext.appContext.getResources().getColorStateList(id);
    }

    /**
     * 获取定义的可绘制对象
     *
     * @param id
     * @return
     */
    public static Drawable getDrawable(@DrawableRes int id) {
        return AppContext.appContext.getResources().getDrawable(id);
    }


    /**
     * 获取定义的字符串
     *
     * @param id
     * @return
     */
    public static String getString(Context context,@StringRes int id) {
        return context.getResources().getString(id);
    }

    /**
     * 获取定义的字符串
     *
     * @param id
     * @return
     */
    public static String getString(Context context,@StringRes int id, Object... formatArgs) {
        return context.getResources().getString(id, formatArgs);
    }

    /**
     * 获取定义的字符串数组
     *
     * @param id
     * @return
     */
    public static String[] getStringArray(@ArrayRes int id) {
        return AppContext.appContext.getResources().getStringArray(id);
    }

    /**
     * 获取自定义的int数组
     *
     * @param id
     * @return
     */
    public static int[] getIntArray(@ArrayRes int id) {
        return AppContext.appContext.getResources().getIntArray(id);
    }

    /**
     * 获取屏幕相关参数
     *
     * @return
     */
    public static DisplayMetrics getDisplayMetrics() {
        return AppContext.appContext.getResources().getDisplayMetrics();
    }

}
