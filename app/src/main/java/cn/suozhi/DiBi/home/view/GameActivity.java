package cn.suozhi.DiBi.home.view;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.CacheUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.RebateEntity;

/**
 * 横屏游戏
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener,
        BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {

    private WebView wv;
    private TextView tvReturn;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        String url = getIntent().getStringExtra("url");
        url = url.replace("fullscreen=1", "fullscreen=0");
        wv = findViewById(R.id.wv_game);
        tvReturn = findViewById(R.id.tv_gameReturn);
        findViewById(R.id.iv_gameClose).setOnClickListener(this);

        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        settings.setLoadsImagesAutomatically(true);//支持自动加载图片
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);//开启dom storage AOI功能
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(CacheUtil.TEXT_CACHE_DIR);
        settings.setNeedInitialFocus(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url) {
                if (Util.isLink(url)) {
                    wv.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(v, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        wv.requestFocusFromTouch();
        wv.loadUrl(url);

        handler.post(run);
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            getReturn();
            handler.postDelayed(this, 10000);
        }
    };

    private void getReturn() {
        OkHttpUtil.getJsonToken(Constant.URL.GetRebate, SharedUtil.getToken(this), this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Game: " + json);
        RebateEntity rebate = JsonUtil.fromJsonO(json, RebateEntity.class);
        if (rebate == null) {
            return;
        }
        if (Constant.Int.SUC == rebate.getCode()) {
            tvReturn.setText(AppUtil.roundRemoveZero(rebate.getData().getBalance(), 2));
        }
    }

    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(this, url, error);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_gameClose) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (wv != null && wv.canGoBack()) {
            wv.goBack();
        } else {
            ConfirmDialog.newInstance(getString(R.string.logoutGameQ), getString(R.string.cancel),
                        getString(R.string.confirm))
                    .setOnItemClickListener(this)
                    .show(this);
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(run);
        }
        wv.stopLoading();
        wv.removeAllViews();
        wv.clearCache(true);
        wv.clearHistory();
        wv.destroy();
        super.onDestroy();
    }
}
