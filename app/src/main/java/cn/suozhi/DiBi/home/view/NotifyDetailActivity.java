package cn.suozhi.DiBi.home.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.NotifyDetailEntity;

/**
 * 公告详情
 */
public class NotifyDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.tv_ndTitle)
    public TextView tvTitle;
    @BindView(R.id.tv_ndTime)
    public TextView tvTime;
    @BindView(R.id.tv_ndView)
    public TextView tvView;

    @BindView(R.id.srl_nd)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.wv_nd)
    public WebView wv;

    private long id;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_notify_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.notifyDetail), v -> onBackPressed());
        showLoading();
        id = getIntent().getLongExtra("id", 0);

        wv.requestFocusFromTouch();
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJson(Constant.URL.GetNotifyDetail + id, this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e("loge", "Detail: " + json);
        NotifyDetailEntity nd = gson.fromJson(json, NotifyDetailEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == nd.getCode()) {
            upUI(nd.getData());
        } else {
            ToastUtil.initToast(this, Util.getCodeText(this,
                    nd.getCode(), nd.getMsg()));
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

    private void upUI(NotifyDetailEntity.DataEntity d) {
        if (d == null) {
            return;
        }
        tvTitle.setText(d.getTitle());
        tvTime.setText(d.getCreateTime());
        tvView.setText(getString(R.string.browse) + " " + d.getPv());
        Util.loadHtmlData(wv, d.getContent());
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
