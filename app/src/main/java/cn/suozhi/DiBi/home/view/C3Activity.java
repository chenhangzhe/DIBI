package cn.suozhi.DiBi.home.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.NotifyEntity;

/**
 * C3认证
 */
public class C3Activity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    @BindView(R.id.srl_c3)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.wv_c3)
    public WebView wv;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_c3;

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.idLevel3), v -> onBackPressed());
        showLoading();
        lang = SharedUtil.getLanguage4Url(this);

        wv.requestFocusFromTouch();
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(false);
        wv.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang,
                "pageNum", "1", "pageSize", "1", "typeCode", "contactMethod");
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e("loge", "C3: " + json);
        NotifyEntity notify = gson.fromJson(json, NotifyEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == notify.getCode()) {
            if (notify.getData() != null) {
                List<NotifyEntity.DataEntity.RecordsEntity> list = notify.getData().getRecords();
                if (list != null && list.size() > 0) {
                    Util.loadHtmlData(wv, list.get(0).getContent());
                }
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    @Override
    protected void showLoading() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(this);
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onDestroy() {
        wv.stopLoading();
        wv.removeAllViews();
        wv.clearCache(true);
        wv.clearHistory();
        wv.destroy();
        super.onDestroy();
    }
}
