package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.CollectMoneyEnity;
import cn.suozhi.DiBi.home.model.StringEntity;

/**
 * 银行收款
 */
public class BankActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.et_name)
    EditText etName;//姓名

    @BindView(R.id.et_account_bank)
    EditText etBank; //开户行

    @BindView(R.id.et_account_branch_bank)
    EditText etBranchBank; //开户支行

    @BindView(R.id.et_bank_number)
    EditText etBnakNumber; //银行卡号

    @BindView(R.id.tv_commit)
    TextView tvCommit;

    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;

    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;

    @BindView(R.id.tv_hint_three)
    TextView tvHintThree;

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    private String branchBank;
    private String bankNumber;
    private String bank;
    private String name;
    private boolean isBind;
    private CollectMoneyEnity.DataBean.RecordsBean data;
    private boolean icClickCommit;
    private boolean icCommitting;
    private LoadingDialog loadingDialog;
    private String fullName;

    @Override
    protected int getViewResId() {
        return R.layout.activity_bank;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        data = (CollectMoneyEnity.DataBean.RecordsBean) intent.getSerializableExtra("data");
        isBind = intent.getBooleanExtra("isBind", false);
        fullName = intent.getStringExtra("fullName");

        ToolbarUtil.initToolbar(toolbar, getString(R.string.str_bank_bind), v -> onBackPressed());

        etName.setText(fullName);
        etName.setEnabled(intent.getBooleanExtra("isSeller", false));

        if (isBind) {
            if (data != null) {
                etName.setText(TextUtils.isEmpty(data.getAccountName()) ? "" : data.getAccountName());
                etBank.setText(data.getBank());
                etBranchBank.setText(TextUtils.isEmpty(data.getBranchBank()) ? "" : data.getBranchBank());
                etBnakNumber.setText(data.getAccountNumber());
                tvCommit.setText(getString(R.string.str_update));
            }
        } else {
            tvCommit.setText(getString(R.string.submit));
        }
    }

    @OnClick({R.id.tv_commit})
    public void bankClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                if (CheckCommitInfo()) {
                    if (!icCommitting) {
                        icCommitting = true;
                        icClickCommit = true;
                        commit(name, bankNumber, bank, branchBank);
                    }
                }
                break;
        }
    }

    private void setLisenter() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (icClickCommit) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintOne.setVisibility(View.VISIBLE);
                    } else {
                        tvHintOne.setVisibility(View.GONE);
                    }
                }
            }
        });

        etBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (icClickCommit) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintTwo.setVisibility(View.VISIBLE);
                    } else {
                        tvHintTwo.setVisibility(View.GONE);
                    }
                }
            }
        });

        etBnakNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (icClickCommit) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintThree.setVisibility(View.VISIBLE);
                    } else {
                        tvHintThree.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * 检查提交信息
     */
    private boolean CheckCommitInfo() {
        name = etName.getText().toString().trim();
        bank = etBank.getText().toString().trim();
        branchBank = etBranchBank.getText().toString().trim();
        bankNumber = etBnakNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(bank)) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(bankNumber)) {
            tvHintThree.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }


    /**
     * 提交收款信息
     */
    private void commit(String accountName, String accountNumber, String bank, String branchBank) {
        showLoading();
        OkHttpUtil.postJsonToken(Constant.URL.addCollectMoneyType, SharedUtil.getToken(mContext), this
                , "accountName", accountName,//账户名
                "accountNumber", accountNumber,//收款账号
                "accountType", "3", //账号类型 1支付宝 2微信 3银行卡
                "bank", bank, //开户银行
                "branchBank", branchBank, //开户支行
                "qrCode", "");//二維碼

    }

    private void showAuthDialog() {
        AuthDialog.newInstance(ResUtils.getString(mContext, R.string.str_enable_edit_hint),
                ResUtils.getString(mContext, R.string.str_go_handle),
                ResUtils.getString(mContext, R.string.cancel))
                .show(this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        StringEntity upload = GsonUtil.fromJson(json, StringEntity.class);
        icCommitting = false;
        if (Constant.Int.SUC == upload.getCode()) {
            if (isBind) {
                CodeStrUtil.showToastHint(this, getString(R.string.str_bank_update_suc));
            } else {
                CodeStrUtil.showToastHint(this, getString(R.string.str_bank_bind_suc));

            }
            finish();
        } else {
            ToastUtil.initToast(BankActivity.this, Util.getCodeText(BankActivity.this,
                    upload.getCode(), upload.getMsg()));
            Util.checkLogin(mContext, upload.getCode());
        }
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        icCommitting = false;
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
