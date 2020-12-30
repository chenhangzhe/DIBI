package cn.suozhi.DiBi.login.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.MD5Util;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.RegexUtils;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.TimerUtils;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.login.model.NoDataModel;
import cn.suozhi.DiBi.login.model.SlideEnity;
import cn.suozhi.DiBi.login.testInterface;

public class FindPasswordActivity extends BaseActivity implements OkHttpUtil.OnDataListener {
    private static final int REQUEST_CODE = 1;
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


    @BindView(R.id.tv_find_way)
    TextView tvFindWay;

    @BindView(R.id.tv_get_code)
    TextView tvGetCode;

    @BindView(R.id.tv_distrct_code)
    TextView tvDistrict;

    @BindView(R.id.tv_email_hint)
    TextView tvEmailHint;

    @BindView(R.id.tv_code_hint)
    TextView tvCodeHint;

    @BindView(R.id.tv_psw_hint)
    TextView tvPswHint;

    @BindView(R.id.tv_comfirm_psw_hint)
    TextView tvComfirmPswHint;


    @BindView(R.id.tv_phone_hint)
    TextView tvPhoneHint;

    @BindView(R.id.lly_phone)
    LinearLayout llyPhone;
    private int activityType; //1 手机 2  邮箱
    private TimerUtils timerUtils;

    private String phone;
    private String email;
    private String msgCode;
    private String psw;
    private String comfirmPsw;
    private boolean isclickUpdate;
    private String phoneCode;
    private LoadingDialog loadingDialog;
    @BindView(R.id.web_view)
    WebView webView;
    private String sig;
    private String nc_token;
    private String sessionid;
    private SlideEnity enity;

    @Override
    protected int getViewResId() {
        return R.layout.activity_find_password;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }

    @Override
    protected void init() {
        EventBusHelper.register(this);
        activityType = getIntent().getIntExtra("activityType", 0);

        if (activityType == 1) {
            etEmailAccount.setVisibility(View.GONE);
            llyPhone.setVisibility(View.VISIBLE);
            tvFindWay.setText(ResUtils.getString(this, R.string.str_find_by_email));
        } else {
            etEmailAccount.setVisibility(View.VISIBLE);
            llyPhone.setVisibility(View.GONE);
            tvFindWay.setText(ResUtils.getString(this, R.string.str_find_by_phone));
        }

        initWebview();

        timerUtils = new TimerUtils(tvGetCode, this);

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

    @OnClick({R.id.tv_find_way, R.id.tv_get_code, R.id.tv_login, R.id.tv_comfirm, R.id.tv_distrct_code})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_find_way:
                showLoading();
                if (activityType == 1) {
                    toFindPasswordActivity(2);
                } else {
                    toFindPasswordActivity(1);
                }
                break;
            case R.id.tv_get_code:
                senCode();
                break;
            case R.id.tv_comfirm:
                update();
                break;
            case R.id.tv_distrct_code:
                Intent intent = new Intent(FindPasswordActivity.this, DistrictCodeActivity.class);
                intent.putExtra("title",ResUtils.getString(mContext,R.string.str_phone_distrct));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_login:
                startActivity(new Intent(FindPasswordActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }


    private void toFindPasswordActivity(int activityType) {
        Intent intent = new Intent(FindPasswordActivity.this, FindPasswordActivity.class);
        intent.putExtra("activityType", activityType);
        startActivity(intent);
        finish();
    }

    private void update() {
        isclickUpdate = true;
        if (checkRegist()) {
            try {
                String account = activityType == 1 ? phone : email;
                OkHttpUtil.postJson(Constant.URL.forgetPsw, this, "account", account,
                        "password", MD5Util.encodeByMD5(psw), "verifyCode", msgCode);
            } catch (Exception e) {}
        }
    }


    private void senCode() {
        if (TextUtils.isEmpty(sig)) {
            CodeStrUtil.showToastHintFail(this,getString(R.string.str_slider_verify));
            return ;
        }
        isclickUpdate = true;
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
            showLoading();
            OkHttpUtil.postJson(Constant.URL.SendPhoneCode, this, "areaCode", phoneCode,
                    "ncToken", nc_token, "phone", phone, "sessionId", sessionid, "sig", sig,
                    "type", "R", "scene", "nc_login_h5");

        } else {
            //获取邮箱验证码
            if (TextUtils.isEmpty(email)) {
                tvEmailHint.setVisibility(View.VISIBLE);
                return;
            }
            showLoading();
            OkHttpUtil.postJson(Constant.URL.SendEmailCode, this, "email", email,
                    "ncToken", nc_token, "sessionId", sessionid, "sig", sig, "type", "R", "scene", "nc_login_h5");
        }
        timerUtils.startSendPhoneCodeTimer();
    }

    private boolean checkRegist() {
        phone = etPhoneAccount.getText().toString().trim();
        email = etEmailAccount.getText().toString().trim();
        msgCode = etMsgCode.getText().toString().trim();
        psw = etPsw.getText().toString().trim();
        comfirmPsw = etComfirmPsw.getText().toString().trim();

        if (activityType == 1) {
            if (TextUtils.isEmpty(phone) || !RegexUtils.isMobileExact(phone)) {
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
        return true;
    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        if (url.endsWith(Constant.URL.forgetPsw)) {
            NoDataModel noDataModel = GsonUtil.fromJson(json, NoDataModel.class);
            if (noDataModel.getCode() == Constant.Int.SUC) {
//                ToastUtil.initToast(mContext,ResUtils.getString(mContext,R.string.str_find_psw_success));
                startActivity(new Intent(FindPasswordActivity.this, LoginActivity.class));
                finish();
            }else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        noDataModel.getCode(), noDataModel.getMsg()));
            }
        } else if (url.equals(Constant.URL.SendEmailCode)) {
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

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
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
}
