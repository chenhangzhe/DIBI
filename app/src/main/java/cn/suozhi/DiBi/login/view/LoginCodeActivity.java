package cn.suozhi.DiBi.login.view;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.eventbus.EventBusHelper;
import cn.suozhi.DiBi.common.eventbus.Message;
import cn.suozhi.DiBi.common.eventbus.MessageEvent;
import cn.suozhi.DiBi.common.util.GetDeviceId;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.home.view.HelpSearchActivity;
import cn.suozhi.DiBi.login.model.LoginEnity;
import cn.suozhi.DiBi.login.widget.SeparatedEditText;

public class LoginCodeActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.tv_edit_type)
    TextView tvEditType;
    @BindView(R.id.tv_lcTips)
    TextView tvTips;

    /*@BindView(R.id.tv_miss_two_validation)
    TextView tvMissTwoValidation;
    @BindView(R.id.tv_two_validation_errow)
    TextView tvTwoValidationFail;*/

    @BindView(R.id.tv_lcLostQ)
    public TextView tvqLost;
    @BindView(R.id.edit_code)
    public SeparatedEditText etCode;
    @BindView(R.id.tv_lcCodeError)
    public TextView tveCode;
    @BindView(R.id.tv_lcSend)
    public TextView tvSend;

    private final int retryLimit = Constant.Int.RetryTime;//重试秒数上限
    private int timeNum = retryLimit;//获取验证码倒计时

    private int codeType; //0 谷歌 1 手机 2 邮箱
    private String preToken, account;

    private LoadingDialog loadingDialog;
    private Handler handler = new Handler();

    @Override
    protected int getViewResId() {
        return R.layout.activity_login_code;
    }

    @Override
    protected void init() {
        EventBusHelper.register(this);
        codeType = getIntent().getIntExtra("loginCodeType", -1);
        preToken = getIntent().getStringExtra("preToken");
        account = getIntent().getStringExtra("account");

        tvSend.setSelected(!TextUtils.isEmpty(account));
        if (SharedUtil.getLanguage(mContext).equals("en")) {
//            tvMissTwoValidation.setTextSize(12);
//            tvTwoValidationFail.setTextSize(12);
            tvSend.setTextSize(12);
        } else {
//            tvTwoValidationFail.setTextSize(14);
//            tvMissTwoValidation.setTextSize(14);
            tvSend.setTextSize(14);
        }


        if (codeType == 0) {
            tvEditType.setText(ResUtils.getString(this, R.string.str_edit_two_validation));
            tvTips.setText(ResUtils.getString(this, R.string.str_edit_two_validation_hint));
            tvqLost.setVisibility(View.VISIBLE);
            tvSend.setVisibility(View.GONE);
        } else if (codeType == 1) {
            tvEditType.setText(ResUtils.getString(this, R.string.str_edit_phone_code));
            tvTips.setText(null);
            tvqLost.setVisibility(View.GONE);
            tvSend.setVisibility(View.VISIBLE);
        } else {
            tvEditType.setText(ResUtils.getString(this, R.string.str_edit_email_code));
            tvTips.setText(null);
            tvqLost.setVisibility(View.GONE);
            tvSend.setVisibility(View.VISIBLE);
        }

        etCode.setBorderColor(ResUtils.getColor(R.color.color_f8f9fd));
        etCode.requestFocus();
        etCode.setTextChangedListener(new SeparatedEditText.TextChangedListener() {
            @Override
            public void textChanged(CharSequence changeText) {
                tveCode.setText(null);
                etCode.setBorderColor(ResUtils.getColor(R.color.color_f8f9fd));
            }

            @Override
            public void textCompleted(CharSequence text) {
                //当输完验证码的时候
                showLoading();
                OkHttpUtil.postJson(Constant.URL.login, LoginCodeActivity.this,
                        "preToken", preToken, "verifyCode", text.toString());
            }
        });

        sendCode();
    }

    @OnClick({R.id.iv_lcBack, R.id.tv_lcLostQ, R.id.tv_lcSend})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.iv_lcBack:
                onBackPressed();
                break;
            case R.id.tv_lcLostQ:
                if (tvqLost.length() > 0) {
                    startActivity(new Intent(this, HelpSearchActivity.class)
                            .putExtra(Constant.Strings.Intent_Key, tvqLost.getText().toString()));
                }
                break;
            case R.id.tv_lcSend:
                sendCode();
                break;
        }
    }

    private void sendCode() {
        if (codeType == 0 || !tvSend.isSelected()) {
            return;
        }
        tvSend.setSelected(false);
        if (codeType == 1) {
            //手机验证
            //验证码类型 S 注册 L 登录 M 重置登录密码 R 忘记密码 C 设置交易操作 U 更新交易操作 I 重置交易操作 B 绑定操作 W 钱币操作 A ApiKey操作
            OkHttpUtil.postJson(Constant.URL.SendLoginPhone, this, "areaCode", "",
                    "phone", account, "preToken", preToken, "type", "L");
        } else {
            //邮箱验证
            OkHttpUtil.postJson(Constant.URL.SendLoginEmail, this, "email", account,
                    "preToken", preToken, "type", "L");
        }
        tveCode.setText(null);
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
                        tvSend.setSelected(true);
                    }
                }
            }
        });
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e(TAG, "Login: " + url + json);
        switch (url) {
            case Constant.URL.SendLoginPhone:
                ObjectEntity codePhone = JsonUtil.fromJsonO(json, ObjectEntity.class);
                if (codePhone == null) {
                    break;
                }
                if (Constant.Int.SUC == codePhone.getCode()) {
                    tvTips.setText(R.string.str_edit_phone_code_two);
                    etCode.requestFocus();
                } else if (codePhone.getCode() == 1010013L) {
                    onBackPressed();
                } else {
                    tveCode.setText(Util.getCodeText(this, codePhone.getCode(), codePhone.getMsg()));
                    upTimeNum();
                }
                break;
            case Constant.URL.SendLoginEmail:
                ObjectEntity codeEmail = JsonUtil.fromJsonO(json, ObjectEntity.class);
                if (codeEmail == null) {
                    break;
                }
                if (Constant.Int.SUC == codeEmail.getCode()) {
                    tvTips.setText(R.string.str_edit_email_code_two);
                    etCode.requestFocus();
                } else if (codeEmail.getCode() == 1010013L) {
                    onBackPressed();
                } else {
                    tveCode.setText(Util.getCodeText(this, codeEmail.getCode(), codeEmail.getMsg()));
                    upTimeNum();
                }
                break;
            case Constant.URL.login:
                LoginEnity login = JsonUtil.fromJsonO(json, LoginEnity.class);
                if (login == null) {
                    break;
                }
                if (Constant.Int.SUC == login.getCode()) {
                    Util.hideKeyboard(etCode);
                    //保存token
                    SharedUtil.saveToken(mContext, login.getData().getToken());
                    SharedUtil.putInt(mContext,"Login","LoginType",login.getData().getData().getLoginType());
                    String deveiceId = SharedUtil.getString(LoginCodeActivity.this, Constant.Strings.SP_DEVICES_ID, "deveiceId", "");
                    OkHttpUtil.postJsonToken(Constant.URL.loginLogcat, SharedUtil.getToken(mContext), null,
                            "browser", "",
                            "deviceType", deveiceId,
                            "deviceVersion", Build.MODEL + Build.VERSION.RELEASE,
                            "loginSource", "1", //登录来源 1Android 2Ios 3PC
                            "mac", GetDeviceId.getMac(mContext));
                    //登录云信
                    doLogin(login.getData().getData().getUserCode(), login.getData().getData().getImToken());
                    goBack();

//                    setResult(RESULT_OK, new Intent().putExtra("back", true));
//                    finish();
                } else if (login.getCode() == 1010013L) {
                    onBackPressed();
                } else {
                    dismissLoading();
                    if (login.getCode() == 12002) {
                        etCode.setBorderColor(ResUtils.getColor(R.color.redE0));
                        tveCode.setText(R.string.code12002);
                    } else {
                        tveCode.setText(Util.getCodeText(this, login.getCode(), login.getMsg()));
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, String error) {
        if (Constant.URL.SendPhoneCode.equals(url) || Constant.URL.SendEmailCode.equals(url)) {
            upTimeNum();
        }
    }

    private void upTimeNum() {
        if (timeNum < retryLimit) {//倒计时中
            timeNum = 0;
        }
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

    public void doLogin(String userCode, String imToken) {
//        Log.e(TAG, "doLogin: code " + userCode + "| imToken" + imToken);
        LoginInfo info = new LoginInfo(userCode, imToken); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        Log.e(TAG, "onSuccess: " + param.getAccount());
                        // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                        NimUIKit.setAccount(TextUtils.isEmpty(userCode.toLowerCase()) ? "" : userCode.toLowerCase());
//                     NIMClient.init(mContext, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(mContext));
                        SharedUtil.saveUserAccount(userCode);
                        SharedUtil.saveUserToken(imToken);
                        goBack();
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.e(TAG, "onFailed: " + code);
                        //存一个空值防止空指针
                        NimUIKit.setAccount("");
                        goBack();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.e(TAG, "onException: " + exception.getMessage());
                        //存一个空值防止空指针
                        NimUIKit.setAccount("");
                        goBack();
                    }
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }

    private void goBack() {
        dismissLoading();
        setResult(RESULT_OK, new Intent().putExtra("back", true));
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        switch (event.getMsg()) {
            case Message.LOGIN_SUCCESS:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }
}
