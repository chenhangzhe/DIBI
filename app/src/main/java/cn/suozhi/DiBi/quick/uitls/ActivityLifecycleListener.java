package cn.suozhi.DiBi.quick.uitls;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Describe: 监听app锁屏或切后台
 * Created by Gao Chunfa on 2020/3/19.
 * Company: Hainan DaDi(Jinan) Network Technology Co. Ltd
 */
class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
        getAppStatus(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        getAppStatus(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    /**
     * 根据activityCount,判断app状态
     */
    public  void getAppStatus(Context context) {
        if (activityCount == 0) {
            //App进入后台或者APP锁屏了
            Toast.makeText(context, "进入后台", Toast.LENGTH_SHORT).show();
        } else {
            //App进入前台

            Toast.makeText(context, "打开应用", Toast.LENGTH_SHORT).show();}
    }
}