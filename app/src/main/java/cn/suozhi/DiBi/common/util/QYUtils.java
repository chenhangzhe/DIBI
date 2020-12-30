package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

//import com.qiyukf.unicorn.api.Unicorn;
//import com.qiyukf.unicorn.api.UnreadCountChangeListener;
//import com.qiyukf.unicorn.api.pop.POPManager;
//import com.qiyukf.unicorn.api.pop.ShopInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by hzwangchenyan on 2015/12/29.
 */
public class QYUtils {
    private static Handler sHandler;

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
            if (allNetworkInfo != null) {
                for (NetworkInfo networkInfo : allNetworkInfo) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean inMainProcess(Context context) {
        String mainProcessName = context.getApplicationInfo().processName;
        String processName = getProcessName();
        return TextUtils.equals(mainProcessName, processName);
    }

    /**
     * 获取当前进程名
     *
     * @return 进程名
     */
    private static String getProcessName() {
        BufferedReader reader = null;
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine().trim();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            if (sHandler == null) {
                sHandler = new Handler(Looper.getMainLooper());
            }
            sHandler.post(runnable);
        }
    }


    /**
     * 初始化获取 未读消息数
     */
    public void initMsgNums() {
//        Unicorn.addUnreadCountChangeListener(mUnreadCountListener, true);
//        updateUnreadCount(Unicorn.getUnreadCount());
    }

    /**
     * 释放资源
     */
    public void releaseUnreadCountChangeListener() {
//        Unicorn.addUnreadCountChangeListener(mUnreadCountListener, false);
    }


//    private UnreadCountChangeListener mUnreadCountListener = new UnreadCountChangeListener() {
//        @Override
//        public void onUnreadCountChange(int count) {
//            updateUnreadCount(count);
//        }
//    };

    private void updateUnreadCount(int count) {
        if (count > 99) {
//            view.setText(getString(R.string.ysf_contact_service) + "(99+)");
        } else if (count > 0) {
//            view.setText(getString(R.string.ysf_contact_service) + "(" + count + ")");
        } else {
//            view.setText(R.string.ysf_contact_service);
        }
    }

    /**
     * 根据商家ID获取商家信息，如名称，logo
     *
     * @param shopId 商家ID
     * @return 如果用户联系过该商家，返回商家信息，否则返回 null
     */
//    public static ShopInfo getShopInfo(String shopId) {
//
//        ShopInfo shopInfo = POPManager.getShopInfo(shopId);
//        return shopInfo;
//    }

}