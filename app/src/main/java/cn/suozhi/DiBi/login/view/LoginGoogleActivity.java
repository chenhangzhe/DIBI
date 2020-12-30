package cn.suozhi.DiBi.login.view;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GetDeviceId;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.view.HelpSearchActivity;
import cn.suozhi.DiBi.login.model.LoginEnity;
import cn.suozhi.DiBi.login.widget.SeparatedEditText;

/**
 * 谷歌验证登录
 */
public class LoginGoogleActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.et_lgCode)
    public SeparatedEditText etCode;
    @BindView(R.id.tv_lgCodeError)
    public TextView tveCode;
    @BindView(R.id.tv_lgLostQ)
    public TextView tvqLost;

    private String preToken;
    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        return R.layout.activity_login_google;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.str_login_vvbtc), v -> onBackPressed());
        preToken = getIntent().getStringExtra("preToken");

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
                showLoading();
                OkHttpUtil.postJson(Constant.URL.login, LoginGoogleActivity.this,
                        "preToken", preToken, "verifyCode", text.toString());
            }
        });
    }

    @Override
    public void onResponse(String url, String json, String session) {
        LoginEnity login = JsonUtil.fromJsonO(json, LoginEnity.class);
        if (login == null) {
            return;
        }
        if (Constant.Int.SUC == login.getCode()) {
            Util.hideKeyboard(etCode);
            //保存token
            SharedUtil.saveToken(mContext, login.getData().getToken());
            String deveiceId = SharedUtil.getString(this, Constant.Strings.SP_DEVICES_ID, "deveiceId", "");
            OkHttpUtil.postJsonToken(Constant.URL.loginLogcat, SharedUtil.getToken(mContext), null,
                    "browser", "",
                    "deviceType", deveiceId,
                    "deviceVersion", Build.MODEL + Build.VERSION.RELEASE,
                    "loginSource", "1", //登录来源 1Android 2Ios 3PC
                    "mac", GetDeviceId.getMac(mContext));
            //登录云信
            doLogin(login.getData().getData().getUserCode(), login.getData().getData().getImToken());

//                    setResult(RESULT_OK, new Intent().putExtra("back", true));
//                    finish();
        } else {
            dismissLoading();
            if (login.getCode() == 12002) {
                etCode.setBorderColor(ResUtils.getColor(R.color.redE0));
                tveCode.setText(R.string.code12002);
            } else {
                tveCode.setText(Util.getCodeText(this, login.getCode(), login.getMsg()));
            }
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

    public void doLogin(String userCode, String imToken) {
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

    @OnClick(R.id.tv_lgLostQ)
    public void google(View v) {
        if (tvqLost.length() > 0) {
            startActivity(new Intent(this, HelpSearchActivity.class)
                    .putExtra(Constant.Strings.Intent_Key, tvqLost.getText().toString()));
        }
    }
}
