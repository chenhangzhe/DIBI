package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.MainActivity;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.CacheUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity implements BaseDialog.OnItemClickListener,
        OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.tv_setLanguage)
    public TextView tvLanguage;
    @BindView(R.id.tv_setCache)
    public TextView tvCache;
    @BindView(R.id.tv_setVersion)
    public TextView tvVersion;

    @BindView(R.id.tv_setPerm)
    public TextView tvPerm;
    @BindView(R.id.l_set3)
    public View lPerm;

    private ArrayList<String> list;
    private String[] LANG = {"chs", "cht", "en"};
    private int defaultIndex;

    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        openRestartInResume();
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.setting), v -> onBackPressed());

        list = new ArrayList<>();
        list.add("简体中文");
        list.add("繁體中文");
        list.add("English");
        String language = SharedUtil.getLanguage(this);
        switch (language) {
            case "chs":
                defaultIndex = 0;
                break;
            case "cht":
                defaultIndex = 1;
                break;
            case "en"://英文
                defaultIndex = 2;
                break;
            default:
                defaultIndex = 0;
                break;
        }
        tvLanguage.setText(list.get(defaultIndex));
    }

    @Override
    protected void loadData() {
        int visible = Util.permCheckNotify(getApplicationContext()) ? View.GONE : View.VISIBLE;
        tvPerm.setVisibility(visible);
        lPerm.setVisibility(visible);
        updateCache();
        tvVersion.setText("V" + Util.getVersion(this));
    }

    private void updateCache() {
        DecimalFormat decimal = new DecimalFormat("0.0");
        float cache = (CacheUtil.cacheSize() + GlideUtil.cacheSize(this)) / 1024.0F;
        String cacheSize;
        if (cache < 1024) {
            if (cache <= 0) {
                cacheSize = "0K";
            } else {
                cacheSize = decimal.format(cache) + "K";
            }
        } else {
            cacheSize = decimal.format(cache / 1024) + "M";
        }
        tvCache.setText(cacheSize);
    }

    @OnClick({R.id.tv_setLanguageName, R.id.tv_setPerm, R.id.tv_setCacheName})
    public void setting(View v) {
        switch (v.getId()) {
            case R.id.tv_setLanguageName:
                ChooseSingleDialog.newInstance(null, list, defaultIndex, false)
                        .setOnItemClickListener(vl -> {
                            int position = (int) vl.getTag();
                            SharedUtil.putLanguage(this, LANG[position]);
                            SharedUtil.onLanguageChange(getApplicationContext());

                            startActivity(new Intent(this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        })
                        .show(this);
                break;
            case R.id.tv_setPerm:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                            .putExtra(Settings.EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                    startActivity(intent);
                }
                break;
            case R.id.tv_setCacheName:
                CacheUtil.clearCache();
                GlideUtil.clearCache(this);
                tvCache.setText("0K");
                ToastUtil.initToast(this, getString(R.string.clearCacheDone));
                break;
//            case R.id.tv_setLogout:
//                ConfirmDialog.newInstance(getString(R.string.logoutMark), getString(R.string.cancel),
//                            getString(R.string.confirm))
//                        .setOnItemClickListener(this)
//                        .show(this);
//                break;
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm) {
            String token = SharedUtil.getToken(this);
            if (SharedUtil.isLogin(token)) {
                showLoading();
                OkHttpUtil.deleteJsonToken(Constant.URL.Logout, token, this);
            } else {
                logout();
            }
        }
    }

    private void logout() {
        SharedUtil.clearData(this);

        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Logout: " + json);
        dismissLoading();
        ObjectEntity logout = JsonUtil.fromJsonO(json, ObjectEntity.class);
        if (logout != null) {
            ToastUtil.initToast(this, Util.getCodeText(this, logout.getCode(), logout.getMsg()));
        }
        logout();
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(this, url, error);
        logout();
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
    }

    @Override
    protected void restart() {
        loadData();
    }
}
