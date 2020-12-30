package cn.suozhi.DiBi.home.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

import androidx.appcompat.app.AppCompatActivity;



import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.MainActivity;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.util.GetDeviceId;
import cn.suozhi.DiBi.common.util.SharedUtil;


/**
 * 启动页
 */
public class LogoActivity extends AppCompatActivity implements  Utils.OnAppStatusChangedListener{

    private LottieAnimationView lav;
    private SharedPreferences time;
    private long usetime;
    private long time1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        AppUtils.registerAppStatusChangedListener(this);
        setContentView(R.layout.activity_logo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//手机竖屏
        SharedUtil.onLanguageChange(this);//设置语言
        time =  getSharedPreferences("time", Context.MODE_PRIVATE);
        long l = 1;
        usetime = time.getLong("usetime", l);

        init();
    }


    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(true);
    }

    protected void init() {
        lav = findViewById(R.id.lav_logo);
        lav.setImageAssetsFolder("images");
        lav.setAnimation("data.json");
        lav.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                intoNextPage();
            }
        });
        lav.playAnimation();

        SharedUtil.putBool(this, "User", "isVerFirst", true);
        getSign();
    }


    /**
     * 获取设备唯一标识
     */
    private void getSign() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取保存在sd中的 设备唯一标识符
                    String readDeviceID = GetDeviceId.readDeviceID(LogoActivity.this);
                    //获取缓存在  sharepreference 里面的 设备唯一标识
                    String string = SharedUtil.getString(LogoActivity.this,Constant.Strings.SP_DEVICES_ID,"deveiceId","");
                    //判断 app 内部是否已经缓存,  若已经缓存则使用app 缓存的 设备id
                    if (string != null) {
                        //app 缓存的和SD卡中保存的不相同 以app 保存的为准, 同时更新SD卡中保存的 唯一标识符
                        if (!TextUtils.isEmpty(readDeviceID) && !string.equals(readDeviceID)) {
                            // 取有效地 app缓存 进行更新操作
                            if (!TextUtils.isEmpty(readDeviceID) && !TextUtils.isEmpty(string)) {
                                readDeviceID = string;
                                GetDeviceId.saveDeviceID(readDeviceID, LogoActivity.this);
                            }
                        }
                    }
                    // app 没有缓存 (这种情况只会发生在第一次启动的时候)
                    if (TextUtils.isEmpty(readDeviceID)) {
                        //保存设备id
                        readDeviceID = GetDeviceId.getDeviceId(LogoActivity.this);
                    }
                    //左后再次更新app 的缓存
                    SharedUtil.putString(LogoActivity.this,Constant.Strings.SP_DEVICES_ID,"deveiceId",readDeviceID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void intoNextPage() {
//        Log.i("TAG", "intoNextPage: "+usetime);
//        Log.i("TAG", "intoNextPage: "+time1);
//        long l = System.currentTimeMillis();
//        Log.i("TAG", "intoNextPage: "+l);
//        long l1 = l - usetime;
//        Log.i("TAG", "intoNextPage: "+l1);
//        if (l1 > 10000  ){
//            Log.i("TAG", "intoNextPage: "+"1111111111111");
//            SharedPreferences password = getSharedPreferences("password", Context.MODE_PRIVATE);
//            String zhiwen = password.getString("zhiwen", "");
//            String shoushi = password.getString("shoushi", "");
//            Log.i("TAG", "intoNextPage: "+"1111111111111"+zhiwen);
//            if (zhiwen.equals("zhiwen")){
//                Log.i("TAG", "intoNextPage: "+"1111111111111");
//                Thefingerprint.start(this);
//                finish();
//                return;
//            }else {
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//            }
//            if (shoushi.equals("shoushi")){
//                Yestures.start(this);
//                finish();
//            }else {
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//            }
//        }else {
            Log.i("TAG", "intoNextPage: "+"11111111111");
            startActivity(new Intent(this, MainActivity.class));
            finish();
      //  }



//        final Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(LogoActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        },1500);
    }

    @Override
    public void onForeground(Activity activity) {
        time1 = System.currentTimeMillis();
    }

    @Override
    public void onBackground(Activity activity) {

    }
}
