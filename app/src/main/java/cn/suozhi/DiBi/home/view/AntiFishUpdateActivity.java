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
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 修改防钓鱼码
 */
public class AntiFishUpdateActivity extends BaseActivity implements BaseDialog.OnItemClickListener,
        OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.tv_atuCode)
    public TextView tvCode;
    @BindView(R.id.et_atuNew)
    public EditText etNew;
    @BindView(R.id.et_atuNewError)
    public TextView tveNew;

    private int state;
    private String area, account;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_anti_fish_update;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.antiFish), v -> onBackPressed());
        Intent intent = getIntent();
        state = intent.getIntExtra("state", 0);
        area = intent.getStringExtra("area");
        account = intent.getStringExtra("account");

        String code = intent.getStringExtra("code");
        tvCode.setText(Util.addStarLast3(code));

        Util.editListenerError(etNew, tveNew);
    }

    @OnClick(R.id.tv_atuConfirm)
    public void password(View v) {
        Util.editRemoveIllegal(etNew);
        if (etNew.length() == 0) {
            tveNew.setText(R.string.inputYourAntiFish);
            etNew.requestFocus();
        } else if (etNew.length() < 4) {
            tveNew.setText(R.string.antiFishTips);
            etNew.requestFocus();
        } else {
            Util.hideKeyboard(etNew);
            if (state < 0 || state > 2) {
                ToastUtil.initToast(this, R.string.dataAbnormal);
            } else {
                if (state == 0) {
                    GoogleVerifyDialog.newInstance()
                            .setOnItemClickListener(this)
                            .show(this);
                } else {
                    CodeVerifyDialog.newInstance(state == 2, SharedUtil.getToken(this),
                                area, account, "B")
                            .setOnItemClickListener(this)
                            .show(this);
                }
            }
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode || v.getId() == R.id.et_dgGvCode) {
            showLoading();
            String code = (String) v.getTag();
            OkHttpUtil.putJsonToken(Constant.URL.Bind, SharedUtil.getToken(this), this,
                    "account", etNew.getText().toString(), "areaCode", "", "type", "4", "verifyCode", code);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "AntiFish: " + json);
        ObjectEntity reset = gson.fromJson(json, ObjectEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == reset.getCode()) {
            View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                    R.mipmap.tick_white_circle, getString(R.string.antiFishUpdateSuc));
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
