package cn.suozhi.DiBi.login;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.MainActivity;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.custom.LollipopWebView;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.eventbus.EventBusHelper;
import cn.suozhi.DiBi.common.eventbus.Message;
import cn.suozhi.DiBi.common.eventbus.MessageEvent;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.MD5Util;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SpannableStringUtils;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.view.Thefingerprint;
import cn.suozhi.DiBi.home.view.Yestures;
import cn.suozhi.DiBi.login.model.PreLoginEnity;
import cn.suozhi.DiBi.login.model.SlideEnity;
import cn.suozhi.DiBi.login.view.FindPasswordActivity;
import cn.suozhi.DiBi.login.view.LoginCodeActivity;
import cn.suozhi.DiBi.login.view.RegisterActivity;

import static android.graphics.Color.WHITE;

public class LoginActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPsw;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;
    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;
    @BindView(R.id.wv_loginScroll)
    public LollipopWebView wv;

    private String account;
    private String psw;
    private boolean isClickLogin;
    private boolean isGoBack = true;
    boolean isLogining = false; //是否在登陆中
    private LoadingDialog loadingDialog;
    private  String SHA1 = "CB:9E:88:43:38:AE:D1:36:8A:42:FD:6E:6F:00:88:D2:63:9A:94:1F";
    private String sha2 =  "CB:9E:88:43:38:AE:D1:36:8A:42:FD:6E:6F:00:88:D2:63:9A:94:1F";

    private String sig, nc_token, sessionId;

    @Override
    protected int getViewResId() {
        openRestartInResume();
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        EventBusHelper.register(this);
        isGoBack = getIntent().getBooleanExtra("goBack", true);
        initWebView();

        tvRegister.setText(SpannableStringUtils.getBuilder(getString(R.string.str_no_accout) + "?")
                .setForegroundColor(ResUtils.getColor(R.color.gy8A))
                .append(" " + getString(R.string.str_register_now))
//                .setForegroundColor(ResUtils.getColor(R.color.purple77))
                .setForegroundColor(ResUtils.getColor(R.color.color_1888FE))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        finishActivity();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        //设置点击事件之后  字体颜色失效 必须要在这个方法中 设置字体颜色
//                        ds.setColor(ResUtils.getColor(R.color.purple77));
                        ds.setColor(ResUtils.getColor(R.color.color_1888FE));
                    }
                })
                .create());

        setLisenter();
        noScreenshots();
       // sHA1(mContext);
        compare(SHA1);
    }

    private void noScreenshots() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wv.requestFocusFromTouch();
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
            settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // 建议禁止缓存加载，以确保在攻击发生时可快速获取最新的滑动验证组件进行对抗。
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置不使用默认浏览器，而直接使用WebView组件加载页面。
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 加载业务页面。
        wv.loadUrl(Constant.URL.ScrollVerify);
        // 建立JavaScript调用Java接口的桥梁。
        wv.addJavascriptInterface(new testInterface(), "testInterface");
    }

    private void reloadScroll() {
        if (wv != null && !TextUtils.isEmpty(sig)) {
            sig = null;
            wv.reload();
        }
    }

    private void finishActivity() {
        LoginActivity.this.finish();
    }

    private void setLisenter() {
        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isClickLogin) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintOne.setVisibility(View.VISIBLE);
                    } else {
                        tvHintOne.setVisibility(View.GONE);
                    }
                }
            }
        });

        etPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isClickLogin) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintTwo.setVisibility(View.VISIBLE);
                    } else {
                        tvHintTwo.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @OnClick({R.id.tv_register, R.id.tv_forget_psw, R.id.tv_login, R.id.rly_back})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("activityType", 1);
                startActivity(intent);
                finishActivity();
                break;
            case R.id.tv_forget_psw:
                Intent intent1 = new Intent(LoginActivity.this, FindPasswordActivity.class);
                intent1.putExtra("activityType", 1);
                startActivity(intent1);
                finishActivity();
                break;
            case R.id.tv_login:
                login();
                break;
            case R.id.rly_back:
                onBackPressed();
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {

        if (checkAccountAndPsw()) {
            try {
                if (!isLogining) {
                    if (TextUtils.isEmpty(sig)) {
                        ToastUtil.initToast(this, R.string.str_slider_verify);
                        return;
                    }
                    isLogining = true;
                    showLoading();
                    OkHttpUtil.postJson(Constant.URL.preLogin, this, "account", account,
                            "ncToken", nc_token, "password", MD5Util.encodeByMD5(psw), "scene", "nc_login_h5",
                            "sessionId", sessionId, "sig", sig);
                    Log.i("TAG", "sendCode: "+MD5Util.encodeByMD5(psw)+psw);
                }
            } catch (Exception e) {
                dismissLoading();
                isLogining = false;
            }
        }
    }

    private boolean checkAccountAndPsw() {
        account = etAccount.getText().toString().trim();
        psw = etPsw.getText().toString().trim();
        isClickLogin = true;

        if (TextUtils.isEmpty(account)) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(psw)) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
//        Log.e(TAG, "PreLogin: " + json);
        if (url.equals(Constant.URL.preLogin)) {
            PreLoginEnity preLogin = GsonUtil.fromJson(json, PreLoginEnity.class);
            if (preLogin.getCode() == Constant.Int.SUC) {
                isLogining = false;
                PreLoginEnity.DataBean.BindStatusBean bind = preLogin.getData().getBindStatus();
                int type = bind.getGaEnabled() == 1 ? 0 : !account.contains("@") ? 1 : 2;
                Intent ic = new Intent(this, LoginCodeActivity.class)
                        .putExtra("loginCodeType", type)
                        .putExtra("preToken", preLogin.getData().getPreToken())
                        .putExtra("account", account);
                startActivityForResult(ic, Constant.Code.JumpCode);
            } else {
                reloadScroll();
                isLogining = false;
                ToastUtil.initToast(this, Util.getCodeText(this,
                        preLogin.getCode(), preLogin.getMsg()));
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {
        reloadScroll();
        dismissLoading();
        isLogining = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.Code.JumpCode && data != null) {
            boolean back = data.getBooleanExtra("back", false);
            if (back) {
                finish();
            }
        }
    }

    @Override
    protected void restart() {
        reloadScroll();
    }

    @Override
    public void onBackPressed() {
        if (isGoBack) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        switch (event.getMsg()) {
            case Message.LOGIN_SUCCESS:
                finish();
                break;
            case Message.SLIDE_SCROLL_SUCCESS:
                String slide = (String) event.getData();
                if (!TextUtils.isEmpty(slide)) {
                    SlideEnity se = GsonUtil.fromJson(slide, SlideEnity.class);
                    sig = se.getSig();
                    nc_token = se.getNc_token();
                    sessionId = se.getSessionid();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        wv.stopLoading();
        wv.removeAllViews();
        wv.clearCache(true);
        wv.clearHistory();
        wv.destroy();
        super.onDestroy();
        EventBusHelper.unregister(this);
    }


    //检验代码的完整性
    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            String sha1 = result.substring(0, result.length() - 1);
            Log.i("11111", "sHA1: "+sha1);
            return sha1;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 和key的签名文件对比
     * @param s
     */
    private void compare(String s){

        if (!s.equals(sHA1(mContext))){
            killAppProcess();
        }
    }
    /**
     * 杀死进程
     */
    public void killAppProcess()
    {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager)LoginActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList)
        {
            if (runningAppProcessInfo.pid != android.os.Process.myPid())
            {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    public static void start(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
