package cn.suozhi.DiBi.home.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.UserEntity;

/**
 * 安全中心
 */
public class SecurityActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener ,View.OnClickListener{

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    @BindView(R.id.srl_sec)
    public SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tv_secPhone)
    public TextView tvPhone;
    @BindView(R.id.tv_secEmail)
    public TextView tvEmail;
    @BindView(R.id.tv_secGoogle)
    public TextView tvGoogle;
    @BindView(R.id.tv_secLogin)
    public TextView tvLogin;
    @BindView(R.id.tv_secFish)
    public TextView tvFish;
    @BindView(R.id.img_shoushi)
    public ImageView shoushi;
    @BindView(R.id.img_shoushi1)
    public ImageView shoushi1;
    @BindView(R.id.img_zhiwen)
    public ImageView zhiwen;
    @BindView(R.id.img_zhiwen1)
    public ImageView zhiwen1;
    @BindView(R.id.lin_zhiwen)
    public RelativeLayout lin_zhiwen;

    private boolean isDone;
    private UserEntity.DataEntity.InfoEntity d;
    private FingerprintManagerCompat managerCompat;
    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();
    private SharedPreferences preferences;
    private SharedPreferences spref;

    @Override
    protected int getViewResId() {
        openRestartInResume();
        return R.layout.activity_security;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.securityCenter), v -> onBackPressed());
        showLoading();
        managerCompat = FingerprintManagerCompat.from(this);
        checkIsFinger();
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        preferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        spref = getSharedPreferences("spref", Context.MODE_PRIVATE);
        String shoushisp = preferences.getString("shoushi","");
        String zhiwensp = preferences.getString("zhiwen", "");
        if (shoushisp.equals("shoushi")){
            shoushi.setVisibility(View.GONE);
            shoushi1.setVisibility(View.VISIBLE);
        }else {
            shoushi.setVisibility(View.VISIBLE);
            shoushi1.setVisibility(View.GONE);
        }
        if (zhiwensp.equals("zhiwen")){
            zhiwen.setVisibility(View.GONE);
            zhiwen1.setVisibility(View.VISIBLE);
        }else {
            zhiwen.setVisibility(View.VISIBLE);
            zhiwen1.setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadData() {
        isDone = false;
        OkHttpUtil.getJsonToken(Constant.URL.GetInfo, SharedUtil.getToken(this), this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Security: " + json);
        UserEntity mine = gson.fromJson(json, UserEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == mine.getCode()) {
            isDone = true;
            d = mine.getData().getInfo();
            updateUI();
        } else {
            ToastUtil.initToast(this, Util.getCodeText(this,
                    mine.getCode(), mine.getMsg()));
            Util.checkLogin(this, mine.getCode());
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
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void updateUI() {
        if (d == null) {
            return;
        }
        tvPhone.setText(d.getPhoneEnabled() == 1 ? R.string.bind : R.string.unbind);
        tvEmail.setText(d.getEmailEnabled() == 1 ? R.string.bind : R.string.unbind);
        tvGoogle.setText(d.getGaEnabled() == 1 ? R.string.bind : R.string.unbind);
        tvFish.setText(TextUtils.isEmpty(d.getFishingCode()) ? R.string.set : R.string.update);
    }

    @OnClick({R.id.tv_secPhoneName, R.id.tv_secEmailName, R.id.tv_secGoogleName,
            R.id.tv_secLoginName, R.id.tv_secFishName,R.id.img_zhiwen,R.id.img_shoushi,R.id.img_zhiwen1,R.id.img_shoushi1})
    public void setting(View v) {
        if (!isDone) {
            ToastUtil.initToast(this, R.string.retryAfterRefresh);
            return;
        }
        String area, account;
        switch (v.getId()) {
            case R.id.tv_secPhoneName:
                if (d.getPhoneEnabled() == 0) {
                    startActivity(new Intent(this, BindPhoneActivity.class));
                }
                break;
            case R.id.tv_secEmailName:
                if (d.getEmailEnabled() == 0) {
                    startActivity(new Intent(this, BindEmailActivity.class));
                }
                break;
            case R.id.tv_secGoogleName:
                int gs = AppUtil.getBindState(d.getPhoneEnabled(), d.getEmailEnabled());
                switch (gs) {
                    case 0:
                        area = d.getPhoneArea();
                        account = d.getCellPhone();
                        break;
                    case 1:
                        area = "";
                        account = d.getEmail();
                        break;
                    default:
                        area = "";
                        account = "";
                        break;
                }
                if (d.getGaEnabled() == 0) {
                    startActivity(new Intent(this, BindGoogleActivity.class)
                            .putExtra("state", gs)
                            .putExtra("area", area)
                            .putExtra("account", account));
                } else if (d.getGaEnabled() == 1) {
                    startActivity(new Intent(this, UnbindGoogleActivity.class)
                            .putExtra("state", gs)
                            .putExtra("area", area)
                            .putExtra("account", account));
                }
                break;
            case R.id.tv_secLoginName:
                int ps = AppUtil.getBindState(d.getGaEnabled(), d.getPhoneEnabled(), d.getEmailEnabled());
                switch (ps) {
                    case 1:
                        area = d.getPhoneArea();
                        account = d.getCellPhone();
                        break;
                    case 2:
                        area = "";
                        account = d.getEmail();
                        break;
                    default:
                        area = "";
                        account = "";
                        break;
                }
                startActivity(new Intent(this, PasswordResetActivity.class)
                        .putExtra("state", ps)
                        .putExtra("area", area)
                        .putExtra("account", account));
                break;
            case R.id.tv_secFishName:
                int fs = AppUtil.getBindState(d.getGaEnabled(), d.getPhoneEnabled(), d.getEmailEnabled());
                switch (fs) {
                    case 1:
                        area = d.getPhoneArea();
                        account = d.getCellPhone();
                        break;
                    case 2:
                        area = "";
                        account = d.getEmail();
                        break;
                    default:
                        area = "";
                        account = "";
                        break;
                }
                String at = d.getFishingCode();
                if (TextUtils.isEmpty(at)) {
                    startActivity(new Intent(this, AntiFishActivity.class)
                            .putExtra("state", fs)
                            .putExtra("area", area)
                            .putExtra("account", account));
                } else {
                    startActivity(new Intent(this, AntiFishUpdateActivity.class)
                            .putExtra("code", at)
                            .putExtra("state", fs)
                            .putExtra("area", area)
                            .putExtra("account", account));
                }
               case  R.id.img_shoushi:
                   showDialogcode("shoushi");

                break;
            case R.id.img_zhiwen:
                //getzhiwen();
                showDialogcode("zhiwen");
                break;
            case R.id.img_shoushi1:
               // shoushidata();
                showDialogcode("shoushi1");
                break;
            case R.id.img_zhiwen1:
               // zhiwendata();
                showDialogcode("zhiwen1");
                break;
        }
    }

    private void zhiwendata() {
        preferences.edit().putString("zhiwen","").commit();
        zhiwen1.setVisibility(View.GONE);
        zhiwen.setVisibility(View.VISIBLE);
    }

    private void shoushidata() {
        preferences.edit().putString("shoushi","").commit();
        spref.edit().putString("password", "").commit();
        spref.edit().putString("tmp_password", "").commit();
        spref.edit().putInt("error_count", 5).commit();
        shoushi.setVisibility(View.VISIBLE);
        shoushi1.setVisibility(View.GONE);
}

    private void getzhiwen() {
        preferences.edit().putString("zhiwen","zhiwen").commit();
        zhiwen.setVisibility(View.GONE);
        zhiwen1.setVisibility(View.VISIBLE);
    }

    private void initdata() {
      //  Toast.makeText(mContext, "111111111", Toast.LENGTH_SHORT).show();
        preferences.edit().putString("shoushi","shoushi").commit();
        shoushi.setVisibility(View.GONE);
        shoushi1.setVisibility(View.VISIBLE);
       // showDialogcode();

//        Yestures.start(this);
//        finish();
    }

    private void showDialogcode(String bottom) {
        Log.i(TAG, "showDialogcode: "+SharedUtil.getToken(this));
        QuickDiaLog.newInstance(SharedUtil.getToken(this),bottom).setOnItemClickListener(this::onClick).show(this);


    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void restart() {
        loadData();
    }
    public boolean checkIsFinger() {
        //判断当前手机版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(MyApplication.appContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//                ToastUtils.getInstance().showToast("没有指纹识别权限");
//                return false;
//            }
            Log.e("TAG", "有指纹权限");
            //判断硬件是否支持指纹识别
            if (!managerCompat.isHardwareDetected()) {
                //ToastUtils.getInstance().showToast("没有指纹识别模块");
              //  Toast.makeText(this, "11111111111", Toast.LENGTH_SHORT).show();
                lin_zhiwen.setVisibility(View.GONE);
                return false;
            }
            Log.e("TAG", "有指纹模块");
            //判断 是否开启锁屏密码
//            if (!keyguardManager.isKeyguardSecure()) {
//               // ToastUtils.getInstance().showToast("没有开启锁屏密码");
//                return false;
//            }
            //判断是否有指纹录入
            if (!managerCompat.hasEnrolledFingerprints()) {
                // ToastUtils.getInstance().showToast("没有录入指纹");
                return false;
            }
            return true;
        } else {
            //ToastUtils.getInstance().showToast("设备系统版本太低不支持指纹识别");
            return false;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            initdata();
            Toast.makeText(this, R.string.yestures, Toast.LENGTH_SHORT).show();
        }
    }
}
