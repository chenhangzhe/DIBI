package cn.suozhi.DiBi.home.view;

import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.NotifyDetailEntity;

/**
 * 功能描述：帮助中心详情
 */
public class HelpCenterDetailActivity extends BaseActivity implements OkHttpUtil.OnDataListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.iv_mineBack)
    ImageView ivMineBack;
    @BindView(R.id.ll_yes)
    LinearLayout llYes;
    @BindView(R.id.ll_no)
    LinearLayout llNo;
    @BindView(R.id.srl_nd)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.wv_nd)
    public WebView wv;
    @BindView(R.id.tv_ndTitle)
    public TextView tvTitle;
    @BindView(R.id.tv_ndTime)
    public TextView tvTime;
    @BindView(R.id.tv_ndView)
    public TextView tvView;
    @BindView(R.id.tv_help_num)
    TextView tvHelpNum;

    private long id;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    private boolean isCanNotLike;
    private int total;
    private int like;
    private int type = -1;

    @Override
    protected int getViewResId() {
        return R.layout.activity_help_center_detail;
    }

    @Override
    protected void loadData() {
        id = getIntent().getLongExtra(Constant.Strings.Intent_Id, 0);
        OkHttpUtil.getJson(Constant.URL.GetNotifyDetail + id, this);
        isCanNotLike = SharedUtil.getBool(this, "like", id + "", false);
        if (isCanNotLike) {//设置不可点击
            llYes.setEnabled(false);
            llNo.setEnabled(false);
        }
    }


    @Override
    protected void init() {
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

    @OnClick({R.id.iv_mineBack, R.id.ll_yes, R.id.ll_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_mineBack:

                finish();

                break;
            case R.id.ll_yes:

                type = 1;
                OkHttpUtil.postJson(Constant.URL.Like + id, this, "isLike", "1");

                break;
            case R.id.ll_no:

                type = 0;
                OkHttpUtil.postJson(Constant.URL.Like + id, this, "isLike", "0");

                break;
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {

        String urlLike = Constant.URL.Like + id;
        String contentLike = Constant.URL.GetNotifyDetail + id;

        if (urlLike.equals(url)) {

            //标记已表过态，不让再次点击
            SharedUtil.putBool(this, "like", id + "", true);
            //设置成不可点击

            llNo.setEnabled(false);
            llYes.setEnabled(false);

            if (type == 1) {
                tvHelpNum.setText(getString(R.string.vvbtc_help_num, total + 1, like + 1));
            } else if (type == 0) {
                tvHelpNum.setText(getString(R.string.vvbtc_help_num, total + 1, like));
            }

        } else if (contentLike.equals(url)) {

            NotifyDetailEntity nd = gson.fromJson(json, NotifyDetailEntity.class);
            dismissLoading();
            if (Constant.Int.SUC == nd.getCode()) {
                upUI(nd.getData());
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        nd.getCode(), nd.getMsg()));
            }

        }


    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    private void upUI(NotifyDetailEntity.DataEntity d) {
        if (d == null) {
            return;
        }

        if (d == null) {
            return;
        }
        tvTitle.setText(d.getTitle());
        tvTime.setText(d.getCreateTime());
        tvView.setText(getString(R.string.browse) + " " + d.getPv());
        like = d.getLikeNum();
        total = d.getLikeNum() + d.getNotLikeNum();
        tvHelpNum.setText(getString(R.string.vvbtc_help_num, total, d.getLikeNum()));

        Util.loadHtmlData(wv, d.getContent());
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
