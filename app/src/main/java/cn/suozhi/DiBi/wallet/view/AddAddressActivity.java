package cn.suozhi.DiBi.wallet.view;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.GoogleVerifyDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

public class AddAddressActivity extends BaseActivity implements BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {

    @BindView(R.id.tv_coin)
    TextView tvCoin;

    @BindView(R.id.tv_title_memo)
    TextView tvTitleMemo;

    @BindView(R.id.et_note_name)
    EditText etNoteName;

    @BindView(R.id.et_memo_hint)
    EditText etMemoHint;

    @BindView(R.id.et_coin_address)
    EditText etAddress;

    @BindView(R.id.rly_special_coin)
    RelativeLayout rlySpecialCoin;

    private static final int REQUEST_CODE = 2;
    private String coin;
    private String note;
    private String addresss;
    private String memoHint;

    private int state;
    private String area;
    private String account;
    private LoadingDialog loadingDialog;

    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;

    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;

    @BindView(R.id.tv_hint_three)
    TextView tvHintThree;

    private boolean icClickCommit;
    private String currencyId;
    private String tagDescribe;

    @Override
    protected int getViewResId() {
        return R.layout.activity_add_address;
    }

    private void setLisenter() {
        etNoteName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        etNoteName.addTextChangedListener(new TextWatcher() {
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

        etAddress.addTextChangedListener(new TextWatcher() {
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
        etMemoHint.addTextChangedListener(new TextWatcher() {
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

    public void editRemoveEmoji(EditText editText) {
        editText.setFilters(new InputFilter[]{inputFilter});
    }


    InputFilter inputFilter = new InputFilter() {

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (!matcher.find()) {
                return null;
            } else {
                return "";
            }

        }
    };


    @Override
    protected void init() {
        state = getIntent().getIntExtra("state", -1);
        area = getIntent().getStringExtra("area");
        account = getIntent().getStringExtra("account");
        editRemoveEmoji(etNoteName);
        setLisenter();
    }

    /**
     * 获取单个币种信息
     */
    private void getSingleCoinInfo() {
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfo, SharedUtil.getToken(mContext), this, "currencyId", currencyId);
    }

    @OnClick({R.id.tv_coin, R.id.tv_submit, R.id.iv_back})
    public void onAddClick(View v) {
        switch (v.getId()) {
            case R.id.tv_coin:
                Intent intent = new Intent(mContext, SelectedCoinActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_submit:
                addAddress();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 添加币种地址
     */
    private void addAddress() {
        icClickCommit = true;
        if (CheckCommitInfo()) {

            if (state == 0) {
                GoogleVerifyDialog.newInstance()
                        .setOnItemClickListener(this)
                        .show(this);
            } else {
                CodeVerifyDialog.newInstance(state == 2, SharedUtil.getToken(this),
                            area, account, "W")
                        .setOnItemClickListener(this)
                        .show(this);
            }
        }
    }


    /**
     * 检查提交信息
     */
    private boolean CheckCommitInfo() {
        coin = tvCoin.getText().toString().trim();
        note = etNoteName.getText().toString().trim();
        addresss = etAddress.getText().toString().trim();
        memoHint = etMemoHint.getText().toString().trim();


        if (TextUtils.isEmpty(coin)) {
            CodeStrUtil.showToastHintFail(this, getString(R.string.str_not_empty_coin));
            return false;
        }

        if (TextUtils.isEmpty(note)) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(addresss)) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }



        if (!TextUtils.isEmpty(tagDescribe)) {
            //标签不为空就为 特殊币种
            if (TextUtils.isEmpty(memoHint)) {
                tvHintThree.setVisibility(View.VISIBLE);
                tvHintThree.setText(String.format(getString(R.string.str_payment_hint),tagDescribe));
                return false;
            }
        }

//        if (coin.equals("EOS")
//                || coin.equals("XLM")
//                || coin.equals("XRP")
//                || coin.equals("GDM")
//                || coin.equals("XMR")|| coin.equals("WCG")|| coin.equals("WEN")|| coin.equals("ORA")|| coin.equals("IPSO")|| coin.equals("HCC")) {
//
//
//            if (TextUtils.isEmpty(memoHint)) {
//                if (coin.equals("EOS")
//                        || coin.equals("XLM")|| coin.equals("WCG")|| coin.equals("WEN")|| coin.equals("ORA")|| coin.equals("IPSO")|| coin.equals("HCC")|| coin.equals("GDM")) {
//                    tvHintThree.setVisibility(View.VISIBLE);
//                    tvHintThree.setText(getString(R.string.str_memo_hint));
//
//                } else if (coin.equals("XRP")) {
//                    tvHintThree.setVisibility(View.VISIBLE);
//                    tvHintThree.setText(getString(R.string.str_tag_hint));
//                } else {
//                    tvHintThree.setVisibility(View.VISIBLE);
//                    tvHintThree.setText(getString(R.string.str_payment_hint));
//                }
//                return false;
//            }
//        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果结果码等于RESULT_OK
            if (requestCode == REQUEST_CODE) {
                coin = data.getExtras().getString("result");
                currencyId = data.getExtras().getString("coinId");

                tvCoin.setText(coin);
                getSingleCoinInfo();
            }
        }
    }


    /**
     * 显示特殊币种的标签输入框
     */
    private void showSpecilaNote() {
//        String selectCoin = tvCoin.getText().toString().trim();
//        if (tvCoin.getText().toString().trim().equals("EOS")
//                || tvCoin.getText().toString().trim().equals("XLM")
//                || tvCoin.getText().toString().trim().equals("XRP")
//                || tvCoin.getText().toString().trim().equals("GDM")
//                || tvCoin.getText().toString().trim().equals("XMR")|| selectCoin.equals("WCG")|| selectCoin.equals("WEN")|| selectCoin.equals("ORA")|| selectCoin.equals("IPSO")|| selectCoin.equals("HCC")) {
//            rlySpecialCoin.setVisibility(View.VISIBLE);
//
//
//            if (tvCoin.getText().toString().trim().equals("EOS")
//                    || tvCoin.getText().toString().trim().equals("XLM")|| selectCoin.equals("WCG")|| selectCoin.equals("WEN")|| selectCoin.equals("ORA")|| selectCoin.equals("IPSO")|| selectCoin.equals("HCC")|| selectCoin.equals("GDM")) {
//                tvTitleMemo.setText("MEMO");
//                etMemoHint.setHint(getString(R.string.str_memo_hint));
//            } else if (tvCoin.getText().toString().trim().equals("XRP")) {
//                tvTitleMemo.setText("TAG");
//                etMemoHint.setHint(getString(R.string.str_tag_hint));
//            } else {
//                tvTitleMemo.setText("Payment ID");
//                etMemoHint.setHint(getString(R.string.str_payment_hint));
//            }
//        } else {
//            rlySpecialCoin.setVisibility(View.GONE);
//        }


        if (TextUtils.isEmpty(tagDescribe)) {
            rlySpecialCoin.setVisibility(View.GONE);
        } else {
            rlySpecialCoin.setVisibility(View.VISIBLE);
            tvTitleMemo.setText(tagDescribe);
            etMemoHint.setHint(String.format(getString(R.string.str_payment_hint),tagDescribe));
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode || v.getId() == R.id.et_dgGvCode) {
            try {
                showLoading();
                String code = (String) v.getTag();
                OkHttpUtil.postJsonToken(Constant.URL.addCoinAddress, SharedUtil.getToken(this), this,
                        "address", addresss,
                        "currency", coin,
                        "remark", memoHint,
                        "tag", note,
                        "verifyCode", code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.addCoinAddress)){
            ObjectEntity reset = GsonUtil.fromJson(json, ObjectEntity.class);
            dismissLoading();
            if (Constant.Int.SUC == reset.getCode()) {
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string._str_add_address_suc));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                finish();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        reset.getCode(), reset.getMsg()));
                Util.checkLogin(this, reset.getCode());
            }
        }else if (url.equals(Constant.URL.singleCoinInfo)) {
            SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
            if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {

                tagDescribe = singleCoinInfoEnity.getData().getTagDescribe();
                showSpecilaNote();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                Util.checkLogin(this, singleCoinInfoEnity.getCode());
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
}
