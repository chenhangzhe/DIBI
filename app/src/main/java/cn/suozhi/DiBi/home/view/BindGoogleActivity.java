package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.GoogleKeyEntity;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 谷歌验证
 */
public class BindGoogleActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, BaseDialog.OnItemClickListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    @BindView(R.id.srl_bg)
    public SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tv_bg1Download)
    public TextView tvDownload;
    @BindView(R.id.tv_bg2Info)
    public TextView tv2Info;
    @BindView(R.id.tv_bgCipher)
    public TextView tvCipher;
    @BindView(R.id.et_bgCode)
    public EditText etCode;
    @BindView(R.id.et_bgCodeError)
    public TextView tveCode;

    private String pn = Constant.Strings.Google_Auth_Package;
    private int state;
    private String area, account;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_bind_google;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.googleVerify), v -> onBackPressed());
        showLoading();
        Intent intent = getIntent();
        state = intent.getIntExtra("state", 0);
        area = intent.getStringExtra("area");
        account = intent.getStringExtra("account");

        tvDownload.setText(Util.isAppInstalled(this, pn) ? R.string.open : R.string.download);
        tv2Info.setText(Util.fromHtml(getString(R.string.bindGoogleInfo2)));
        Util.editListenerError(etCode, tveCode);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        Util.hideKeyboard(etCode);
        OkHttpUtil.getJsonToken(Constant.URL.GetGoogleKey, SharedUtil.getToken(this), this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Google: " + json);
        switch (url) {
            case Constant.URL.GetGoogleKey:
                GoogleKeyEntity google = gson.fromJson(json, GoogleKeyEntity.class);
                dismissLoading();
                if (Constant.Int.SUC == google.getCode() && google.getData() != null) {
                    tvCipher.setText(google.getData().getGaKey());
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            google.getCode(), google.getMsg()));
                    Util.checkLogin(this, google.getCode());
                }
                break;
            case Constant.URL.Bind:
                ObjectEntity bind = gson.fromJson(json, ObjectEntity.class);
                dismissLoading();
                if (Constant.Int.SUC == bind.getCode()) {
                    View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                            R.mipmap.tick_white_circle, getString(R.string.googleBindSuc));
                    ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                    finish();
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            bind.getCode(), bind.getMsg()));
                    Util.checkLogin(this, bind.getCode());
                }
                break;
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

    @OnClick({R.id.tv_bg1Download, R.id.tv_bgCipher, R.id.tv_bgBind})
    public void bind(View v) {
        switch (v.getId()) {
            case R.id.tv_bg1Download:
                try {
                    PackageManager pm = getPackageManager();
                    if (Util.isAppInstalled(this, pn)) {
                        startActivity(pm.getLaunchIntentForPackage(pn)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pn))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.Strings.Google_Auth_Link)));
                }
                break;
            case R.id.tv_bgCipher:
                if (tvCipher.length() > 0) {
                    Util.copyBoard(this, tvCipher.getText().toString());
                    ToastUtil.initToast(this, R.string.copyDone);
                }
                break;
            case R.id.tv_bgBind:
                if (etCode.length() == 0) {
                    tveCode.setText(R.string.inputCorrectCode);
                    etCode.requestFocus();
                } else {
                    Util.hideKeyboard(etCode);
                    if (state < 0 || state > 1) {
                        ToastUtil.initToast(this, R.string.dataAbnormal);
                    } else {
                        CodeVerifyDialog.newInstance(state == 1, SharedUtil.getToken(this),
                                    area, account, "B")
                                .setOnItemClickListener(this)
                                .show(this);
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode) {
            showLoading();
            String code = (String) v.getTag();
            OkHttpUtil.putJsonToken(Constant.URL.Bind, SharedUtil.getToken(this), this,
                    "account", etCode.getText().toString(), "areaCode", "", "type", "3", "verifyCode", code);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
