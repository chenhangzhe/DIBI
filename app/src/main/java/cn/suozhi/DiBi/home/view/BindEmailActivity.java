package cn.suozhi.DiBi.home.view;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 邮箱绑定
 */
public class BindEmailActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.et_beEmail)
    public EditText etEmail;
    @BindView(R.id.et_beEmailError)
    public TextView tveEmail;
    @BindView(R.id.et_beCode)
    public EditText etCode;
    @BindView(R.id.et_beCodeError)
    public TextView tveCode;
    @BindView(R.id.tv_beSend)
    public TextView tvSend;

    private final int retryLimit = Constant.Int.RetryTime;//重试秒数上限
    private int timeNum = retryLimit;//获取验证码倒计时

    private LoadingDialog loadingDialog;
    private Handler handler = new Handler();
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_bind_email;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.emailBind), v -> onBackPressed());

        Util.editListenerError(etCode, tveCode);
    }

    @OnTextChanged(value = R.id.et_beEmail, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etEmailChanged(CharSequence text) {
        if (text.length() > 0 && tveEmail.length() > 0) {
            tveEmail.setText(null);
        }
        if (canSend(text)) {
            canSendCode();
        } else {
            if (tvSend.isSelected()) {
                tvSend.setSelected(false);
            }
        }
    }

    private boolean canSend(CharSequence text) {
        if (text.length() > 0 && text.toString().matches(Constant.Strings.RegexEmail)) {
            return true;
        }
        return false;
    }

    private void canSendCode() {
        if (!tvSend.isSelected() && timeNum >= retryLimit) {
            tvSend.setSelected(true);
        }
    }

    @OnClick({R.id.tv_beSend, R.id.tv_beBind})
    public void bind(View v) {
        switch (v.getId()) {
            case R.id.tv_beSend:
                sendCode();
                break;
            case R.id.tv_beBind:
                String email = Util.editRemoveIllegal(etEmail);
                String code = etCode.getText().toString();
                if (etEmail.length() == 0) {
                    tveEmail.setText(R.string.inputFormatEmail);
                    etEmail.requestFocus();
                } else if (!email.matches(Constant.Strings.RegexEmail)) {
                    tveEmail.setText(R.string.inputFormatEmail);
                    etEmail.requestFocus();
                } else if (etCode.length() == 0) {
                    tveCode.setText(R.string.inputCorrectCode);
                    etCode.requestFocus();
                } else {
                    Util.hideKeyboard(etCode);
                    showLoading();
                    OkHttpUtil.putJsonToken(Constant.URL.Bind, SharedUtil.getToken(this), this,
                            "account", email, "areaCode", "", "type", "2", "verifyCode", code);
                }
                break;
        }
    }

    private void sendCode() {
        String email = Util.editRemoveIllegal(etEmail);
        if (tvSend.isSelected()) {
            if (etEmail.length() == 0) {
                tveEmail.setText(R.string.inputFormatEmail);
                etEmail.requestFocus();
            } else if (!email.matches(Constant.Strings.RegexEmail)) {
                tveEmail.setText(R.string.inputFormatEmail);
                etEmail.requestFocus();
            } else {
                tvSend.setSelected(false);
                OkHttpUtil.postJsonToken(Constant.URL.SendEmail, SharedUtil.getToken(this),
                        this, "email", email, "type", "B");
                //设置XX秒后重试
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tvSend != null && handler != null) {
                            tvSend.setText(timeNum + " S");
                            if (timeNum > 0) {
                                handler.postDelayed(this, 1000);
                                timeNum--;
                            } else {
                                tvSend.setText(R.string.resend);
                                timeNum = retryLimit;
                                if (canSend(etEmail.getText())) {
                                    tvSend.setSelected(true);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "BindEmail: " + json);
        switch (url) {
            case Constant.URL.SendEmail:
                ObjectEntity code = gson.fromJson(json, ObjectEntity.class);
                ToastUtil.initToast(this, Util.getCodeText(this,
                        code.getCode(), code.getMsg()));
                if (Constant.Int.SUC == code.getCode()) {
                    etCode.requestFocus();
                } else {
                    upTimeNum();
                }
                break;
            case Constant.URL.Bind:
                ObjectEntity bind = gson.fromJson(json, ObjectEntity.class);
                dismissLoading();
                if (Constant.Int.SUC == bind.getCode()) {
                    View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                            R.mipmap.tick_white_circle, getString(R.string.emailBindSuc));
                    ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                    upTimeNum();
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
        if (Constant.URL.SendEmail.equals(url)) {
            upTimeNum();
        }
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

    private void upTimeNum() {
        if (timeNum < retryLimit) {//倒计时中
            timeNum = 0;
        }
    }
}
