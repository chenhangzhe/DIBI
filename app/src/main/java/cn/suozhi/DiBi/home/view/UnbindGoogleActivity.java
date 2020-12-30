package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.MD5Util;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 关闭谷歌验证
 */
public class UnbindGoogleActivity extends BaseActivity implements BaseDialog.OnItemClickListener,
        OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.et_ugPassword)
    public EditText etPassword;
    @BindView(R.id.et_ugPasswordError)
    public TextView tvePassword;
    @BindView(R.id.et_ugCode)
    public EditText etCode;
    @BindView(R.id.et_ugCodeError)
    public TextView tveCode;

    private int state;
    private String area, account;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_unbind_google;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.disableGoogle), v -> onBackPressed());
        Intent intent = getIntent();
        state = intent.getIntExtra("state", 0);
        area = intent.getStringExtra("area");
        account = intent.getStringExtra("account");

        Util.editListenerError(etPassword, tvePassword);
        Util.editListenerError(etCode, tveCode);
    }

    @OnClick(R.id.tv_ugConfirm)
    public void google(View v) {
        if (etPassword.length() == 0) {
            tvePassword.setText(R.string.inputCorrectLoginPassword);
            etPassword.requestFocus();
        } else if (etCode.length() == 0) {
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
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode) {
            try {
                showLoading();
                String code = (String) v.getTag();
                OkHttpUtil.putJsonToken(Constant.URL.UnbindGoogle, SharedUtil.getToken(this), this,
                        "gaCode", etCode.getText().toString(), "loginPwd",
                        MD5Util.encodeByMD5(etPassword.getText().toString()), "verifyCode", code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Unbind: " + json);
        ObjectEntity unbind = gson.fromJson(json, ObjectEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == unbind.getCode()) {
            View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                    R.mipmap.tick_white_circle, getString(R.string.googleUnbindSuc));
            ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
            finish();
        } else {
            ToastUtil.initToast(this, Util.getCodeText(this,
                    unbind.getCode(), unbind.getMsg()));
            Util.checkLogin(this, unbind.getCode());
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
    }
}
