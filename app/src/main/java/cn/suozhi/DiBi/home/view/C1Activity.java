package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.OnClick;
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
 * C1认证
 */
public class C1Activity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.tv_c1Nation)
    public TextView tvNation;
    @BindView(R.id.cl_c1China)
    public ConstraintLayout clChina;
    @BindView(R.id.cl_c1Foreign)
    public ConstraintLayout clForeign;

    @BindView(R.id.et_c1Name)
    public EditText etName;
    @BindView(R.id.et_c1NameError)
    public TextView tveName;
    @BindView(R.id.et_c1Last)
    public EditText etLast;
    @BindView(R.id.et_c1LastError)
    public TextView tveLast;
    @BindView(R.id.et_c1First)
    public EditText etFirst;
    @BindView(R.id.et_c1FirstError)
    public TextView tveFirst;

    @BindView(R.id.tv_c1Type)
    public TextView tvType;
    @BindView(R.id.et_c1Num)
    public EditText etNum;
    @BindView(R.id.et_c1NumError)
    public TextView tveNum;

    private int type;//1 身份证 2 护照
    private String value;
    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_c1;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.juniorIdentify), v -> onBackPressed());

        Util.editListenerError(etName, tveName);
        Util.editListenerError(etLast, tveLast);
        Util.editListenerError(etFirst, tveFirst);
        Util.editListenerError(etNum, tveNum);

        type = 1;
        value = Constant.Strings.China_Nation;
        tvNation.setText(R.string.china);
        initUI();
    }

    private void initUI() {
        clChina.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        clForeign.setVisibility(type == 1 ? View.GONE : View.VISIBLE);
        tvType.setText(type == 1 ? R.string.idCard : R.string.passport);
    }

    @OnClick({R.id.tv_c1Nation, R.id.tv_c1Submit})
    public void c1(View v) {
        switch (v.getId()) {
            case R.id.tv_c1Nation:
                Util.hideKeyboard(etNum);
                startActivityForResult(new Intent(this, NationActivity.class)
                        .putExtra("title", getString(R.string.selectNation))
                        .putExtra("cate", "NATION")
                        .putExtra("showValue", false), Constant.Code.SelectCode);
                break;
            case R.id.tv_c1Submit:
                String name;
                if (type == 1) {
                    name = Util.editRemoveIllegal(etName);
                    if (etName.length() == 0) {
                        tveName.setText(R.string.inputName);
                        etName.requestFocus();
                        break;
                    }
                } else {
                    String last = Util.editRemoveIllegal(etLast);
                    String first = Util.editRemoveIllegal(etFirst);
                    if (etLast.length() == 0) {
                        tveLast.setText(R.string.inputLastName);
                        etLast.requestFocus();
                        break;
                    }
                    if (etFirst.length() == 0) {
                        tveFirst.setText(R.string.inputFirstName);
                        etFirst.requestFocus();
                        break;
                    }
                    name = last + " " + first;
                }
                String num = Util.editRemoveIllegal(etNum);
                if (etNum.length() == 0) {
                    tveNum.setText(R.string.inputIdNumber);
                    etNum.requestFocus();
                    break;
                }
                if (type == 1 && !num.matches(Constant.Strings.RegexIdNum)) {
                    tveNum.setText(R.string.idNumberError);
                    etNum.requestFocus();
                    break;
                }
                showLoading();
                boolean china = type == 1;
                OkHttpUtil.putJsonToken(Constant.URL.C1, SharedUtil.getToken(this), this,
                        "country", value, "fullName", name, "idNumber", num,
                        "idType", china ? "1" : "2", "type", china ? "1" : "2");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.Code.SelectCode && data != null) {
            tvNation.setText(data.getStringExtra("name"));
            value = data.getStringExtra("value");
            type = Constant.Strings.China_Nation.equals(value) ? 1 : 2;
            initUI();
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e("loge", "C1: " + json);
        ObjectEntity c1 = gson.fromJson(json, ObjectEntity.class);
        dismissLoading();
        ToastUtil.initToast(this, Util.getCodeText(this,
                c1.getCode(), c1.getMsg()));
        if (Constant.Int.SUC == c1.getCode()) {
            startActivity(new Intent(this, IdentityResultActivity.class)
                    .putExtra("level", 1)
                    .putExtra("type", type));
            finish();
        } else {
            Util.checkLogin(this, c1.getCode());
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
