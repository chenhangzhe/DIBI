package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;
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
import cn.suozhi.DiBi.login.view.DistrictCodeActivity;

/**
 * 手机绑定
 */
public class BindPhoneActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.tv_bpArea)
    public TextView tvArea;
    @BindView(R.id.et_bpPhone)
    public EditText etPhone;
    @BindView(R.id.et_bpPhoneError)
    public TextView tvePhone;
    @BindView(R.id.et_bpCode)
    public EditText etCode;
    @BindView(R.id.et_bpCodeError)
    public TextView tveCode;
    @BindView(R.id.tv_bpSend)
    public TextView tvSend;

    private String area;
    private final int retryLimit = Constant.Int.RetryTime;//重试秒数上限
    private int timeNum = retryLimit;//获取验证码倒计时

    private LoadingDialog loadingDialog;
    private Handler handler = new Handler();
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.phoneBind), v -> onBackPressed());
        area = Constant.Strings.China_Phone;
        tvArea.setText(area);

        Util.editListenerError(etCode, tveCode);
    }

    @OnTextChanged(value = R.id.et_bpPhone, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etPhoneChanged(CharSequence text) {
        if (text.length() > 0 && tvePhone.length() > 0) {
            tvePhone.setText(null);
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
        if (Constant.Strings.China_Phone.equals(area) && text.length() == 11) {
            return true;
        }
        if (!Constant.Strings.China_Phone.equals(area) && text.length() > 0) {
            return true;
        }
        return false;
    }

    private void canSendCode() {
        if (!tvSend.isSelected() && timeNum >= retryLimit) {
            tvSend.setSelected(true);
        }
    }

    @OnClick({R.id.tv_bpArea, R.id.tv_bpSend, R.id.tv_bpBind})
    public void bind(View v) {
        switch (v.getId()) {
            case R.id.tv_bpArea:
                Util.hideKeyboard(etPhone);
                startActivityForResult(new Intent(this, DistrictCodeActivity.class)
                        .putExtra("title", getString(R.string.str_phone_distrct)),
                        Constant.Code.SelectCode);
                break;
            case R.id.tv_bpSend:
                sendCode();
                break;
            case R.id.tv_bpBind:
                String phone = Util.editRemoveIllegal(etPhone);
                String code = etCode.getText().toString();
                if (etPhone.length() == 0) {
                    tvePhone.setText(R.string.inputPhoneNumber);
                    etPhone.requestFocus();
                } else if (Constant.Strings.China_Phone.equals(area) && !phone.matches(Constant.Strings.RegexMobile)) {
                    tvePhone.setText(R.string.inputFormatPhone);
                    etPhone.requestFocus();
                } else if (etCode.length() == 0) {
                    tveCode.setText(R.string.inputCorrectCode);
                    etCode.requestFocus();
                } else {
                    Util.hideKeyboard(etCode);
                    showLoading();
                    OkHttpUtil.putJsonToken(Constant.URL.Bind, SharedUtil.getToken(this), this,
                            "account", phone, "areaCode", area, "type", "1", "verifyCode", code);
                }
                break;
        }
    }

    private void sendCode() {
        String phone = Util.editRemoveIllegal(etPhone);
        if (tvSend.isSelected()) {
            if (etPhone.length() == 0) {
                tvePhone.setText(R.string.inputPhoneNumber);
                etPhone.requestFocus();
            } else if (Constant.Strings.China_Phone.equals(area) && !phone.matches(Constant.Strings.RegexMobile)) {
                tvePhone.setText(R.string.inputFormatPhone);
                etPhone.requestFocus();
            } else {
                tvSend.setSelected(false);
                OkHttpUtil.postJsonToken(Constant.URL.SendPhone, SharedUtil.getToken(this),
                        this, "areaCode", area, "phone", phone, "type", "B");
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
                                if (canSend(etPhone.getText())) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.Code.SelectCode && data != null) {
            area = data.getStringExtra("result");
            tvArea.setText(area);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "BindPhone: " + json);
        switch (url) {
            case Constant.URL.SendPhone:
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
                            R.mipmap.tick_white_circle, getString(R.string.phoneBindSuc));
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
        if (Constant.URL.SendPhone.equals(url)) {
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
