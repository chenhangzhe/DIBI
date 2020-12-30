package cn.suozhi.DiBi.login.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.GetDeviceId;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.MD5Util;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.RegexUtils;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.SpannableStringUtils;
import cn.suozhi.DiBi.common.util.TimerUtils;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.login.model.NoDataModel;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.login.model.RegisterEnity;
import cn.suozhi.DiBi.login.model.SlideEnity;
import cn.suozhi.DiBi.login.testInterface;

public class RegisterActivity extends BaseActivity implements OkHttpUtil.OnDataListener {
    @BindView(R.id.et_email_account)
    EditText etEmailAccount;

    @BindView(R.id.et_phone_account)
    EditText etPhoneAccount;

    @BindView(R.id.et_code)
    EditText etMsgCode;

    @BindView(R.id.et_psw)
    EditText etPsw;

    @BindView(R.id.et_comfirm_psw)
    EditText etComfirmPsw;

    @BindView(R.id.et_invite_code)
    EditText etInviteCode;

    @BindView(R.id.tv_login)
    TextView tvLogin;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_protocol)
    TextView tvProtocol;

    @BindView(R.id.tv_regist_type)
    TextView tvRegisterType;

    @BindView(R.id.tv_get_code)
    TextView tvGetCode;

    @BindView(R.id.tv_email_hint)
    TextView tvEmailHint;

    @BindView(R.id.tv_code_hint)
    TextView tvCodeHint;

    @BindView(R.id.tv_psw_hint)
    TextView tvPswHint;

    @BindView(R.id.tv_comfirm_psw_hint)
    TextView tvComfirmPswHint;

    @BindView(R.id.cb_protocol)
    CheckBox cbProtocol;

    @BindView(R.id.tv_register)
    TextView tvRegister;

    @BindView(R.id.tv_distrct_code)
    TextView tvDistrict;

    @BindView(R.id.tv_phone_hint)
    TextView tvPhoneHint;

    @BindView(R.id.lly_phone_register)
    LinearLayout llyPhone;

    private int activityType; //1 代表手机注册  2 代表邮箱注册
    private String phone;
    private String email;
    private String msgCode;
    private String psw;
    private String comfirmPsw;
    private String inviteCode;
    private boolean isclickRegist;
    private TimerUtils timerUtils;
    private static final int REQUEST_CODE = 1;
    private String phoneCode;
    private boolean isCheckedProtocol;
    private LoadingDialog loadingDialog;

    private String sig;
    private String nc_token;
    private String sessionid;
    private SlideEnity enity;

    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected int getViewResId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }

    @Override
    protected void init() {
        EventBusHelper.register(this);
        lang = SharedUtil.getLanguage4Url(this);
        initWebview();
        activityType = getIntent().getIntExtra("activityType", 0);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogin.setText(SpannableStringUtils.getBuilder(ResUtils.getString(mContext, R.string.str_have_account))
                .setForegroundColor(ResUtils.getColor(R.color.gy8A))
                .append(ResUtils.getString(mContext, R.string.str_go_login))
//                .setForegroundColor(ResUtils.getColor(R.color.purple77))
                .setForegroundColor(ResUtils.getColor(R.color.color_1888FE))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        //设置点击事件之后  字体颜色失效 必须要在这个方法中 设置字体颜色
//                        ds.setColor(ResUtils.getColor(R.color.purple77));
                        ds.setColor(ResUtils.getColor(R.color.color_1888FE));
                    }
                })
                .create());

        tvProtocol.setText(SpannableStringUtils.getBuilder(ResUtils.getString(mContext, R.string.str_read_and_agree))
                .setForegroundColor(ResUtils.getColor(R.color.text_color_dark))
                .append(ResUtils.getString(mContext, R.string.str_use_protocol))
//                .setForegroundColor(ResUtils.getColor(R.color.purple77))
                .setForegroundColor(ResUtils.getColor(R.color.color_1888FE))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(mContext, ProtocolActivity.class);
                        intent.putExtra("title", ResUtils.getString(mContext, R.string.str_use_protocol_two));
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        //设置点击事件之后  字体颜色失效 必须要在这个方法中 设置字体颜色
//                        ds.setColor(ResUtils.getColor(R.color.purple77));
                        ds.setColor(ResUtils.getColor(R.color.color_1888FE));
                    }
                })
                .append(ResUtils.getString(mContext, R.string.str_and))
                .setForegroundColor(ResUtils.getColor(R.color.text_color_dark))
                .append(ResUtils.getString(mContext, R.string.str_secret_protocol))
//                .setForegroundColor(ResUtils.getColor(R.color.purple77))
                .setForegroundColor(ResUtils.getColor(R.color.color_1888FE))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(mContext, ProtocolActivity.class);
                        intent.putExtra("title", ResUtils.getString(mContext, R.string.str_secret_protocol_two));
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        //设置点击事件之后  字体颜色失效 必须要在这个方法中 设置字体颜色
//                        ds.setColor(ResUtils.getColor(R.color.purple77));
                        ds.setColor(ResUtils.getColor(R.color.color_1888FE));
                    }
                })
                .create());

        if (activityType == 1) {
            etEmailAccount.setVisibility(View.GONE);
            llyPhone.setVisibility(View.VISIBLE);
            tvRegisterType.setText(ResUtils.getString(this, R.string.str_email_regist));
            tvTitle.setText(ResUtils.getString(this, R.string.str_phone_regist));
        } else {
            etEmailAccount.setVisibility(View.VISIBLE);
            llyPhone.setVisibility(View.GONE);
            tvRegisterType.setText(ResUtils.getString(this, R.string.str_phone_regist));
            tvTitle.setText(ResUtils.getString(this, R.string.str_email_regist));
        }

        timerUtils = new TimerUtils(tvGetCode, this);
        cbProtocol.setChecked(true);
        setLisenter();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebview() {
        WebSettings settings = webView.getSettings();
        // 设置屏幕自适应。
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // 建议禁止缓存加载，以确保在攻击发生时可快速获取最新的滑动验证组件进行对抗。
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置不使用默认浏览器，而直接使用WebView组件加载页面。
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 加载业务页面。
        webView.loadUrl(Constant.URL.ScrollVerify);

        // 设置WebView组件支持加载JavaScript。

        // 建立JavaScript调用Java接口的桥梁。
        webView.addJavascriptInterface(new testInterface(), "testInterface");

    }

    @OnClick({R.id.tv_regist_type, R.id.tv_register, R.id.tv_get_code, R.id.tv_distrct_code})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_regist_type:
                if (activityType == 1) {
                    toRegisterActivity(2);
                } else {
                    toRegisterActivity(1);
                }
                break;
            case R.id.tv_register:
                regist();
                break;
            case R.id.tv_get_code:
                senCode();
                break;
            case R.id.tv_distrct_code:
                Intent intent = new Intent(RegisterActivity.this, DistrictCodeActivity.class);
                intent.putExtra("title", ResUtils.getString(mContext, R.string.str_phone_distrct));
                startActivityForResult(intent, REQUEST_CODE);

                break;
        }
    }

    private void senCode() {
        if (TextUtils.isEmpty(sig)) {
            CodeStrUtil.showToastHintFail(this,getString(R.string.str_slider_verify));
            return ;
        }
        isclickRegist = true;
        phoneCode = tvDistrict.getText().toString().trim();
        phone = etPhoneAccount.getText().toString().trim();
        email = etEmailAccount.getText().toString().trim();
        if (activityType == 1) {
            //获取手机验证码
            if (TextUtils.isEmpty(phone)) {
                tvPhoneHint.setVisibility(View.VISIBLE);
                return;
            }
            //验证码类型 S 注册 L 登录 M 重置登录密码 R 忘记密码 C 设置交易操作 U 更新交易操作 I 重置交易操作 B 绑定操作 W 钱币操作 A ApiKey操作
            OkHttpUtil.postJson(Constant.URL.SendPhoneCode, this, "areaCode", phoneCode,
                    "ncToken", nc_token, "phone", phone, "sessionId", sessionid, "sig", sig,
                    "type", "S", "scene", "nc_login_h5");

        } else {
            //获取邮箱验证码
            if (TextUtils.isEmpty(email)) {
                tvEmailHint.setVisibility(View.VISIBLE);
                return;
            }
            OkHttpUtil.postJson(Constant.URL.SendEmailCode, this, "email", email,
                    "ncToken", nc_token, "sessionId", sessionid, "sig", sig, "type", "S", "scene", "nc_login_h5");
        }

        timerUtils.startSendPhoneCodeTimer();
    }

    /**
     * 注册
     */
    private void regist() {
        isclickRegist = true;
        if (checkRegist()) {
            showLoading();
            OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.oncetoken);
        }
    }


    /**
     * 检查数据
     */
    private boolean checkRegist() {
        phoneCode = tvDistrict.getText().toString().trim();
        phone = etPhoneAccount.getText().toString().trim();
        email = etEmailAccount.getText().toString().trim();
        msgCode = etMsgCode.getText().toString().trim();
        psw = etPsw.getText().toString().trim();
        comfirmPsw = etComfirmPsw.getText().toString().trim();
        inviteCode = etInviteCode.getText().toString().trim();

        if (activityType == 1) {
//            if (TextUtils.isEmpty(phone) || !RegexUtils.isMobileExact(phone)) {
//                tvPhoneHint.setVisibility(View.VISIBLE);
//                return false;
//            }
            if (TextUtils.isEmpty(phone)) {
                tvPhoneHint.setVisibility(View.VISIBLE);
                return false;
            }
        } else {
            if (TextUtils.isEmpty(email) || !RegexUtils.isEmail(email)) {
                tvEmailHint.setVisibility(View.VISIBLE);
                return false;
            }
        }
        if (TextUtils.isEmpty(msgCode)) {
            tvCodeHint.setText(activityType == 1 ? R.string.str_edit_phone_code : R.string.str_edit_email_code);
            tvCodeHint.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(psw) || !RegexUtils.isPassWordOk(psw)) {
            tvPswHint.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(comfirmPsw) || !psw.equals(comfirmPsw)) {
            tvComfirmPswHint.setVisibility(View.VISIBLE);
            return false;
        }

        if (!cbProtocol.isChecked()) {
            showToast(getString(R.string._str_agree_protocol));
            return false;
        }
        return true;
    }


    /**
     * 跳转到 注册界面
     */
    private void toRegisterActivity(int activityType) {
        Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
        intent.putExtra("activityType", activityType);
        startActivity(intent);
        finish();
    }


    private void setLisenter() {
        showHint(etEmailAccount, tvEmailHint);
        showHint(etPhoneAccount, tvPhoneHint);
        showHint(etMsgCode, tvCodeHint);
        showHint(etPsw, tvPswHint);
        showHint(etComfirmPsw, tvComfirmPswHint);

        cbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckedProtocol = isChecked;
            }
        });


    }

    /**
     * 显示提示语
     */
    private void showHint(EditText editText, TextView hint) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isclickRegist) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        hint.setVisibility(View.VISIBLE);
                    } else {
                        hint.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果结果码等于RESULT_OK
            if (requestCode == REQUEST_CODE) {
                String result = data.getExtras().getString("result");
                tvDistrict.setText(result);
            }
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                try {
                    handleRegster(onceToken);
                } catch (Exception e) {

                }
            } else {
                dismissLoading();
                ToastUtil.initToast(this, Util.getCodeText(this,
                        onceTokenEnity.getCode(), onceTokenEnity.getMsg()));
            }
        } else if (url.equals(Constant.URL.register)) {
            dismissLoading();
            RegisterEnity registerEnity = GsonUtil.fromJson(json, RegisterEnity.class);
            if (registerEnity.getCode() == Constant.Int.SUC) {
                //保存token
                SharedUtil.saveToken(mContext, registerEnity.getData().getToken());

                //登录云信
                doLogin(registerEnity.getData().getData().getUserCode(), registerEnity.getData().getData().getImToken());
                String deveiceId = SharedUtil.getString(RegisterActivity.this, Constant.Strings.SP_DEVICES_ID, "deveiceId", "");
                OkHttpUtil.postJsonToken(Constant.URL.loginLogcat, SharedUtil.getToken(mContext), this,
                        "browser", "",
                        "deviceType", deveiceId,
                        "deviceVersion", Build.MODEL + Build.VERSION.RELEASE,
                        "loginSource", "1", //登录来源 1Android 2Ios 3PC
                        "mac", GetDeviceId.getMac(mContext));

                showToast(getString(R.string._str_register_suc));
                finish();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        registerEnity.getCode(), registerEnity.getMsg()));
            }
        } else if (url.equals(Constant.URL.SendEmailCode)) {
            dismissLoading();
            NoDataModel noDataModel = GsonUtil.fromJson(json, NoDataModel.class);
            if (noDataModel.getCode() == Constant.Int.SUC) {
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string.str_edit_email_code_two));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
            } else {
                timerUtils.reSetCodeBtnStatus();
                reloadScroll();
                ToastUtil.initToast(this, Util.getCodeText(this,
                        noDataModel.getCode(), noDataModel.getMsg()));
            }
        } else if (url.equals(Constant.URL.SendPhoneCode)) {
            dismissLoading();
            NoDataModel noDataModel = GsonUtil.fromJson(json, NoDataModel.class);
            if (noDataModel.getCode() == Constant.Int.SUC) {
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string.str_edit_phone_code_two));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
            } else {
                timerUtils.reSetCodeBtnStatus();
                reloadScroll();
                ToastUtil.initToast(this, Util.getCodeText(this,
                        noDataModel.getCode(), noDataModel.getMsg()));
            }
        }
    }

    /**
     * 显示提示语
     */
    private void showToast(String hint) {
        View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                R.mipmap.tick_white_circle, hint);
        ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
    }


    private void handleRegster(String onceToken) throws Exception {
        String account = activityType == 1 ? phone : email;
        String areaCode = activityType == 1 ? phoneCode : "";
        OkHttpUtil.postJsonHeader2(Constant.URL.register, "", lang, this,
                "account", account, "areaCode", areaCode, "inviteCode", inviteCode,
                "onceToken", onceToken, "password", MD5Util.encodeByMD5(psw),
                "userSource", Constant.Strings.Android, "verifyCode", msgCode);
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        switch (event.getMsg()) {

            case Message.SLIDE_SCROLL_SUCCESS:
                String data = (String) event.getData();
                enity = GsonUtil.fromJson(data, SlideEnity.class);
                sig = enity.getSig();
                nc_token = enity.getNc_token();
                sessionid = enity.getSessionid();
                break;
            default:
                break;
        }
    }

    private void reloadScroll() {
        if (webView != null && !TextUtils.isEmpty(sig)) {
            sig = null;
            webView.reload();
        }
    }

    public void doLogin(String userCode, String imToken) {
        LoginInfo info = new LoginInfo(userCode, imToken); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
//                        Log.d("wangli", "云信登录成功");
                        NimUIKit.setAccount(TextUtils.isEmpty(userCode.toLowerCase()) ? "" : userCode.toLowerCase());

//                     NIMClient.init(mContext, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(mContext));
                        SharedUtil.saveUserAccount(userCode);
                        SharedUtil.saveUserToken(imToken);

                    }

                    @Override
                    public void onFailed(int code) {
                        //存一个空值防止空指针
                        NimUIKit.setAccount("");
                    }

                    @Override
                    public void onException(Throwable exception) {
                        //存一个空值防止空指针
                        NimUIKit.setAccount("");
                    }

                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }
}
