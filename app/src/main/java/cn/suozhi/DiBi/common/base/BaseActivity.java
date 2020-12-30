package cn.suozhi.DiBi.common.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import cn.suozhi.DiBi.common.util.SharedUtil;

/**
 * Activity的基类 /mipmap
 */
public abstract class BaseActivity extends AppCompatActivity {

    private boolean clickable = true;
    private boolean restart = false;
    public Context  mContext;
    /**
     * 开启后把 {@link #onRestart()} 中方法移至 {@link #onResume()} 中执行
     * 先调用 {@link #openRestartInResume()} 再重写 {@link #restart()}
     */
    private boolean restartInResume = false;

    protected String lang;//请求接口时的语言，免去多次定义
    public final String TAG = "loge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle(savedInstanceState);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//手机竖屏
        SharedUtil.onLanguageChange(this);//设置语言
        setContentView(getViewResId());
        ButterKnife.bind(this);
        try {
            init();
            loadData();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    protected void getBundle(Bundle state){}

    protected abstract int getViewResId();

    protected void init(){}

    protected void loadData(){}

    protected void showLoading(){}

    protected void dismissLoading(){}

    /**
     * 设置顶部通知栏透明 需要时在 {@link #getViewResId()} 中调用
     */
    protected void setSystemUI() {
        View decorView = getWindow().getDecorView();
        int systemUI = decorView.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        systemUI |= flags;
        decorView.setSystemUiVisibility(systemUI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置顶部通知栏颜色
     */
    protected void setStatusColor(int colorId) {
        if (colorId == 0) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, colorId));
            }
        } catch (Exception e){}
    }

    /**
     * 设置底部导航栏颜色
     */
    protected void setNavigationColor(int colorId) {
        if (colorId == 0) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, colorId));
            }
        } catch (Exception e){}
    }

    public void clickOnce() {
        if (!clickable) {
            return;
        }
        clickable = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clickable = true;
            }
        }, 1000);
    }

    public boolean canClick() {
        return clickable;
    }


    public void openRestartInResume() {
        restartInResume = true;
    }

    public void closeRestartInResume() {
        restartInResume = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
        if (restartInResume && restart) {
            restart = false;
            restart();
        }
    }
    /**
     * 设置 app 不随着系统字体的调整而变化
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (restartInResume) {
            restart = true;
        }
    }

    protected void resume(){}

    protected void restart(){}
}
