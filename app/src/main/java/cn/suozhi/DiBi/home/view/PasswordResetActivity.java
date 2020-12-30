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
import cn.suozhi.DiBi.common.dialog.GoogleVerifyDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.MD5Util;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 重置登录密码
 */
public class PasswordResetActivity extends BaseActivity implements BaseDialog.OnItemClickListener,
        OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.et_psPassword)
    public EditText etPassword;
    @BindView(R.id.et_psPasswordError)
    public TextView tvePassword;
    @BindView(R.id.et_psAgain)
    public EditText etAgain;
    @BindView(R.id.et_psAgainError)
    public TextView tveAgain;

    private int state;
    private String area, account;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_password_reset;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.resetNewPassword), v -> onBackPressed());
        Intent intent = getIntent();
        state = intent.getIntExtra("state", 0);
        area = intent.getStringExtra("area");
        account = intent.getStringExtra("account");

        Util.editListenerError(etPassword, tvePassword);
        Util.editListenerError(etAgain, tveAgain);
    }

    @OnClick(R.id.tv_psConfirm)
    public void password(View v) {
        String password = etPassword.getText().toString();
        if (etPassword.length() == 0) {
            tvePassword.setText(R.string.inputPassword);
            etPassword.requestFocus();
        } else if (!password.matches(Constant.Strings.RegexPassword)) {
            tvePassword.setText(R.string.passwordTips);
            etPassword.requestFocus();
        } else if (etAgain.length() == 0) {
            tveAgain.setText(R.string.inputPassword);
            etAgain.requestFocus();
        } else if (!password.equals(etAgain.getText().toString())) {
            tveAgain.setText(R.string.passwordNoMatch);
            etAgain.requestFocus();
        } else {
            Util.hideKeyboard(etAgain);
            if (state < 0 || state > 2) {
                ToastUtil.initToast(this, R.string.dataAbnormal);
            } else {
                if (state == 0) {
                    GoogleVerifyDialog.newInstance()
                            .setOnItemClickListener(this)
                            .show(this);
                } else {
                    CodeVerifyDialog.newInstance(state == 2, SharedUtil.getToken(this),
                                area, account, "M")
                            .setOnItemClickListener(this)
                            .show(this);
                }
            }
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode || v.getId() == R.id.et_dgGvCode) {
            try {
                showLoading();
                String code = (String) v.getTag();
                OkHttpUtil.putJsonToken(Constant.URL.ResetPassword, SharedUtil.getToken(this), this,
                        "password", MD5Util.encodeByMD5(etPassword.getText().toString()), "verifyCode", code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Reset: " + json);
        ObjectEntity reset = gson.fromJson(json, ObjectEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == reset.getCode()) {
            View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                    R.mipmap.tick_white_circle, getString(R.string.passwordResetSuc));
            ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
            finish();
        } else {
            ToastUtil.initToast(this, Util.getCodeText(this,
                    reset.getCode(), reset.getMsg()));
            Util.checkLogin(this, reset.getCode());
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
