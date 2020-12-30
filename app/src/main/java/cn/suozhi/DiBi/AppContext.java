package cn.suozhi.DiBi;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.WebView;

import com.activeandroid.ActiveAndroid;
import com.alibaba.security.realidentity.RPVerify;
import com.alibaba.security.rp.RPSDK;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nim.uikit.business.session.viewholder.extension.CustomAttachment;
import com.netease.nim.uikit.business.session.viewholder.extension.MsgViewHolderDefCustom;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.qiyukf.unicorn.api.OnBotEventListener;
//import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
//import com.qiyukf.unicorn.api.UICustomization;
//import com.qiyukf.unicorn.api.Unicorn;
//import com.qiyukf.unicorn.api.YSFOptions;
//import com.qiyukf.unicorn.api.customization.title_bar.TitleBarConfig;
import com.tencent.bugly.crashreport.CrashReport;


import cn.suozhi.DiBi.c2c.view.chat.SessionHelper;
import cn.suozhi.DiBi.c2c.view.chat.qiyu.DemoPreferences;
import cn.suozhi.DiBi.common.custom.LollipopWebView;
import cn.suozhi.DiBi.common.util.GlideImageLoader;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.QYUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.wychat.event.DemoOnlineStateContentProvider;
import cn.suozhi.DiBi.wychat.mixpush.DemoCache;
import cn.suozhi.DiBi.wychat.mixpush.DemoMixPushMessageHandler;
import cn.suozhi.DiBi.wychat.mixpush.DemoPushContentProvider;
import cn.suozhi.DiBi.wychat.mixpush.NIMInitManager;
import cn.suozhi.DiBi.wychat.mixpush.NimSDKOptionConfig;
import cn.suozhi.DiBi.wychat.mixpush.UserPreferences;

public class AppContext extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            if (!getPackageName().equals(processName)){//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);}
        }
        new LollipopWebView(this).destroy();//避免WebView使多语言失效
        OkHttpUtil.initOkHttp();
        ActiveAndroid.initialize(this);
        RPVerify.init(appContext);
        CrashReport.initCrashReport(getApplicationContext(), "0a2dc80e17", false);

        initWyChat();
        initCrashReport();
    }

    public  String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(SharedUtil.attachBaseContext(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedUtil.onLanguageChange(this);
    }

    private void initCrashReport() {
//        LogReport.getInstance()
//                //定义路径为：Android/data/[PackageName]/cLog
//                .setLogDir(getApplicationContext(), Environment.getExternalStorageDirectory() +
//                        "/Android/data/" + getPackageName() + "/")
//                .setLogSaver(new CrashWriter(getApplicationContext()))
//                .init(getApplicationContext());
    }


    /**
     * 七鱼的appkey
     */
    private String ysfAppId() {
        return DemoPreferences.getYsfAppKey();
    }


    //七鱼
//    private YSFOptions ysfOptions() {
//        YSFOptions options = new YSFOptions();
//        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
//        options.statusBarNotificationConfig.notificationSmallIconId = R.mipmap.ic_launcher;
//
//        options.titleBarConfig = new TitleBarConfig();
//        options.titleBarConfig.titleBarRightImg = R.mipmap.arrow_back;
//
//        //ui
//        options.uiCustomization = new UICustomization();
//        options.uiCustomization.avatarShape = 0; //0为圆形 1 为方形
//        options.uiCustomization.titleBackgroundResId = R.drawable.sp_wihite_bg;
//        options.uiCustomization.topTipBarTextSize = 14;
//        options.onBotEventListener = new OnBotEventListener() {
//            @Override
//            public boolean onUrlClick(Context context, String url) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                context.startActivity(intent);
//                return true;
//            }
//        };
//        // 如果项目中使用了 Glide 可以通过设置 gifImageLoader 去加载 gif 图片
//        //options.gifImageLoader = new GlideGifImagerLoader(this);
//
//        DemoCache.ysfOptions = options;
//        return options;
//    }

    /**
     * app退出时调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }


    private void initWyChat() {
        DemoPreferences.init(this);
        DemoCache.setContext(this);
        //初始化网易七鱼
//        Unicorn.init(this, ysfAppId(), ysfOptions(), new GlideImageLoader(appContext));
        // 4.6.0 开始，第三方推送配置入口改为 SDKOption#mixPushConfig，旧版配置方式依旧支持。
        SDKOptions ops = NimSDKOptionConfig.getSDKOptions(this);
        ops.disableAwake = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;//为避免崩溃 8.0以上不再后台弹出消息通知
        NIMClient.init(this, getLoginInfo(), ops);

        if (QYUtils.inMainProcess(this)) {
            int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
            ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                    .memoryCacheSize(memoryCacheSize)
                    .diskCacheSize(50 * 1024 * 1024)
                    .build());
//            Unicorn.toggleNotification(true);
        }
        // 以下逻辑只在主进程初始化时执行
        if (NIMUtil.isMainProcess(this)) {

            // 注册自定义推送消息处理，这个是可选项
            NIMPushClient.registerMixPushMessageHandler(new DemoMixPushMessageHandler());
            // 初始化UIKit模块
            initUIKit();

            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
            //关闭撤回消息提醒
//            NIMClient.toggleRevokeMessageNotification(false);
            // 云信sdk相关业务初始化
            NIMInitManager.getInstance().init(true);
            // 初始化rts模块
//            initRTSKit();
        }
    }


    private LoginInfo getLoginInfo() {
        String account = SharedUtil.getUserAccount();
        String token = SharedUtil.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }


    private void initUIKit() {
        // 初始化
        NimUIKit.init(this);
        // IM 会话窗口的定制初始化。
        SessionHelper.init();
        // 聊天室聊天窗口的定制初始化。
//        ChatRoomSessionHelper.init();
        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(new DemoPushContentProvider());
        NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }

    private static void registerViewHolders() {
        NimUIKit.registerMsgItemViewHolder(CustomAttachment.class, MsgViewHolderDefCustom.class);
    }
}
