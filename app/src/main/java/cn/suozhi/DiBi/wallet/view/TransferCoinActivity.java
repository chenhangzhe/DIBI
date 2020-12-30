package cn.suozhi.DiBi.wallet.view;

import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.GoogleVerifyDialog;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.EditInputFilter;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.wallet.adapter.GetCoinHintAdapter;
import cn.suozhi.DiBi.wallet.model.CheckUserExistEntity;
import cn.suozhi.DiBi.wallet.model.CoinListEntity;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

/**
 * 转账界面
 */
public class TransferCoinActivity extends BaseActivity implements BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {

    private static final int REQUEST_CODE = 2;
    private static final int REQUEST_SCAN = 3;
    private static final int GET_ADDRESS = 4;
//    @BindView(R.id.rv_hint)
//    RecyclerView rvHint;

    @BindView(R.id.lly_special_coin)
    LinearLayout llySpecialCoin;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_get_coin_name)
    TextView tvGetCoinNmae;

    @BindView(R.id.tv_special_note)
    TextView tvSpecilaNote;
    //可用余额
    @BindView(R.id.tv_avalible_amount)
    TextView tvAvalible;

    @BindView(R.id.et_target_account)
//    EditText etAddress;
    EditText etTargetAccount;

    @BindView(R.id.et_amount)
    EditText etAmount;

    @BindView(R.id.et_note)
    TextView etSpecialNote;
    private GetCoinHintAdapter getCoinHintAdapter;
    //当前币种
    private String currentCode;
    //当前币种id
    private String currencyId;

    private int gs;
    private String area;
    private String account;
    //固定手续费
    private double gdFee;
    //动态手续费
    private double dynamicFee;
    //是否是动态手续费
    private boolean isF;
    //可用余额
    private double availableAmount;
    private double fee;
    private double actual;
    //输入的数量
    private double editAmount;
    //输入数量的最小数量  小于这个数量就不让提币
    private double minAmount;
    //剩下的可用余额
    private double restAvailableAmount;
    private String deveiceId;
    //是否正在提交
    private boolean isCommiting = false;
    private String verifyCode;
    private String addresss;
    private String amount;
    private String addressExt;
    private String addressTag;
    private boolean isSpecialCoin;

    // 转账页面账号
    private String userAccount;

    //提币提示
    private List<SingleCoinInfoEnity.DataBean.WithdrawTipsBean> withdrawTips;
    //最小费用
    private double withdrawalMinFee;
    //最小提币数量
    private String minWithdrawAmount;
    //小数位
    private int preci;
    //固定手续费
    private double gdFee2;

    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;

    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;
    private boolean icClickCommit;
    private String availableAmountStr;
    private String tagDescribe;

    @BindView(R.id.sp_choose_coin_type)
    public Spinner spinner;
    @BindView(R.id.sp_choose_target)
    public Spinner spinner1;

    private String accountType;

    private List<CoinListEntity.DataEntity> coinList;

    @BindView(R.id.tv_target_name)
    public TextView tvTargetName;

    @BindView(R.id.to_account_name)
    public TextView toAccountName;

    private int CheckUserExistCode = 0;
    private String currencyCode = null;
    private String toUID = null;

    @Override
    protected int getViewResId() {
        return R.layout.activity_transfer_coin;
    }

    private void setLisenter() {
        etTargetAccount.addTextChangedListener(new TextWatcher() {
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
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void init() {
        setSystemUI();
        deveiceId = SharedUtil.getString(this, Constant.Strings.SP_DEVICES_ID, "deveiceId", "");

        toAccountName.setText(R.string.name);

        // 下拉选择用户类型
        String[] accountTypeList = {"UID","Phone","Email"};
        ArrayAdapter<String> arrAda = new ArrayAdapter<String>(this,R.layout.spinner_display_style,R.id.spinner_item_text,accountTypeList);
        arrAda.setDropDownViewResource(R.layout.spinner_dropdown_style);
        spinner1.setAdapter(arrAda);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String content = parent.getItemAtPosition(position).toString();
                accountType = Integer.toString(position+1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        gs = getIntent().getIntExtra("state", -1);
        area = getIntent().getStringExtra("area"); // +86
        account = getIntent().getStringExtra("account"); // 注册的账号
//        rvHint.setLayoutManager(new LinearLayoutManager(mContext));
        getCoinHintAdapter = new GetCoinHintAdapter(mContext);
//        rvHint.setAdapter(getCoinHintAdapter);

//        currentCode = getIntent().getStringExtra("result");
//        currencyId = getIntent().getStringExtra("coinId");
//        coinId = 2 | result = ETH

        preci = getIntent().getIntExtra("precis", -1);

        //setPreicNums();
        showSpecilaNote();

        tvTitle.setText(getString(R.string.str_transfer_c2c));
//        tvGetCoinNmae.setText(currentCode);

        setLisenter();
        etAmount.addTextChangedListener(new TextWatcher() {
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

                if (!TextUtils.isEmpty(s.toString())) {
                    editAmount = Double.parseDouble(s.toString());
                    //输入数量小于提币数量
//                    if (editAmount < minAmount) {
//                        return;
//                    }
                    if (isF) {
                        actual = editAmount - gdFee2;
                    } else {

                        double fee = editAmount * dynamicFee > withdrawalMinFee ? editAmount * dynamicFee : withdrawalMinFee;
                        actual = editAmount - fee;
                    }

//                    restAvailableAmount = availableAmount - editAmount;
//                    tvAvalible.setText(getString(R.string.str_avlible_amount) + restAvailableAmount);
                }
            }
        });
    }


    /**
     * 设置限制输入的小数位
     */
    private void setPreicNums() {
        if (preci > 0) {
            //限制editText输入的小数位
            InputFilter[] filters2 = {new EditInputFilter(preci)};
            etAmount.setFilters(filters2);
        }
    }

    private String getTwoPointNums(String nums, int flag) {
        return Util.formatFloor(Double.parseDouble(nums), flag);
    }


    /**
     * 显示特殊币种的标签输入框
     */
    private void showSpecilaNote() {
        if (TextUtils.isEmpty(tagDescribe)) {
            llySpecialCoin.setVisibility(View.GONE);
        } else {
            llySpecialCoin.setVisibility(View.VISIBLE);
            tvSpecilaNote.setText(tagDescribe);
            etSpecialNote.setHint(String.format(getString(R.string.str_payment_hint),tagDescribe));
        }
    }

    @Override
    protected void loadData() {
        getCoinList();
    }

    @OnClick({R.id.iv_back, R.id.tv_record, R.id.tv_commit, R.id.tv_all, R.id.btn_find_target_account})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                if(CheckUserExistCode == 1){
                    if (!isCommiting) {
                        isCommiting = true;
                        icClickCommit = true;
                        if (checkInfo()) {
                            if (gs == 0) {
                                GoogleVerifyDialog.newInstance()
                                        .setOnItemClickListener(this)
                                        .setOnDismissListener(() -> isCommiting = false)
                                        .show(this);
                            } else {
                                CodeVerifyDialog.newInstance(gs == 2, SharedUtil.getToken(this),
                                        area, account, "W")
                                        .setOnItemClickListener(this)
                                        .setOnDismissListener(() -> isCommiting = false)
                                        .show(this);
                            }
                        } else {
                            isCommiting = false;
                        }
                    }
                } else {
                    CodeStrUtil.showToastHintFail(this, getString(R.string.code1020001));
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_record:
                Intent intent1 = new Intent(mContext, TransferRecordActivity.class);
                intent1.putExtra("status", 2);
                startActivity(intent1);
                break;
            case R.id.tv_all:
                etAmount.setText(availableAmountStr);
                if (isF) {
                    actual = availableAmount - gdFee2;
                } else {
                    double fee = availableAmount * dynamicFee > withdrawalMinFee ? availableAmount * dynamicFee : withdrawalMinFee;
                    actual = availableAmount - fee;
                }
                break;
            case R.id.btn_find_target_account:
                userAccount = etTargetAccount.getText().toString().trim();
                OkHttpUtil.getJsonToken(Constant.URL.checkUserExist, SharedUtil.getToken(mContext), this,
                        "userAccount", userAccount ,"accountType" ,accountType);
                break;
        }
    }

    private boolean checkInfo() {
        addresss = etTargetAccount.getText().toString().trim();
        amount = etAmount.getText().toString().trim();
        addressExt = etSpecialNote.getText().toString().trim();

        if (TextUtils.isEmpty(addresss)) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(amount)) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }

        if (Double.parseDouble(amount) < minAmount) {
            //提币数量不得低于币种最小提币数量
            CodeStrUtil.showToastHintFail(this, getString(R.string.str_amount_less_than_minAmount));
            return false;
        }

        if (Double.parseDouble(amount) > availableAmount) {
            CodeStrUtil.showToastHintFail(this, getString(R.string.str_not_enough_money));
            return false;
        }


        if (!TextUtils.isEmpty(tagDescribe)) {
            //标签不为空就为 特殊币种
            if (TextUtils.isEmpty(addressExt)) {
                CodeStrUtil.showToastHintFail(this, String.format(getString(R.string.str_payment_hint),tagDescribe));
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果结果码等于RESULT_OK
            if (requestCode == REQUEST_CODE) {
                String coinId = data.getExtras().getString("coinId");
                String coin = data.getExtras().getString("result");
                preci = data.getExtras().getInt("precis");
                showSpecilaNote();
                currencyId = coinId;
                currentCode = coin;
                tvTitle.setText(coin + getString(R.string.str_transfer_accounts));
                tvGetCoinNmae.setText(coin);

                //清空数据
                etTargetAccount.setText("");
                etAmount.setText("");
                etSpecialNote.setText("");
                //setPreicNums();

                getSingleCoinInfo();
            } else if (requestCode == REQUEST_SCAN) {
                tvHintOne.setVisibility(View.GONE);
                String address = data.getExtras().getString("result");
                etTargetAccount.setText(address);
            } else if (requestCode == GET_ADDRESS) {
                tvHintOne.setVisibility(View.GONE);
                addresss = data.getExtras().getString("address");
                addressTag = data.getExtras().getString("addressTag");
                addressExt = data.getExtras().getString("addressExt");
                etSpecialNote.setText(TextUtils.isEmpty(addressExt) ? "" : addressExt);
                etTargetAccount.setText(addresss);
            }
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode || v.getId() == R.id.et_dgGvCode) {
            showLoading();
            verifyCode = (String) v.getTag();
            OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.getCoinOncetoken);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.singleCoinInfo)) {
            SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
            if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {
                upDateUi(singleCoinInfoEnity.getData());
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                Util.checkLogin(this, singleCoinInfoEnity.getCode());
            }
        } else if (url.equals(Constant.URL.onceToken)) {
//            Log.e("LogWT:","onceToken");
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                OkHttpUtil.postJsonToken(Constant.URL.otcTransfer, SharedUtil.getToken(mContext), this,
                        "toUID",toUID, // 目标uid
                        "currencyCode",currentCode, // 币种code
                        "amount", amount,  //转账数量
                        "onceToken", onceToken, //一次性token
                        "verifyCode", verifyCode//验证码
                );
            } else {
                isCommiting = false;
            }
        } else if (url.equals(Constant.URL.otcTransfer)) {
//            Log.e("LogWT:","转账");
            isCommiting = false;
            ObjectEntity objectEntity = GsonUtil.fromJson(json, ObjectEntity.class);
            if (objectEntity.getCode() == Constant.Int.SUC) {
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string.transfer_success));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                finish();
            } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            objectEntity.getCode(), objectEntity.getMsg()));
                    Util.checkLogin(this, objectEntity.getCode());
            }
        } else if (url.equals(Constant.URL.currencies)){
            // 绑定下拉
            CoinListEntity cle = JsonUtil.fromJsonO(json, CoinListEntity.class);
            if (Constant.Int.SUC == cle.getCode()) {
                coinList = cle.getData();
                List listC = new ArrayList<>();
                for(int i=0;i<coinList.size();i++){
                    listC.add(coinList.get(i).getCode());
                }
                List listI = new ArrayList<>();
                for(int i=0;i<coinList.size();i++){
                    listI.add(coinList.get(i).getId());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_display_style,R.id.spinner_item_text,listC);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String content = parent.getItemAtPosition(position).toString();
                        currencyId = (listI.get(Integer.valueOf(Long.toString(id)).intValue())).toString();
                        currentCode = content;
                        tvGetCoinNmae.setText(currentCode);
                        getSingleCoinInfo();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //
                    }
                });
            }
        }  else if (url.equals(Constant.URL.checkUserExist)){
            CheckUserExistEntity cue = GsonUtil.fromJson(json,CheckUserExistEntity.class);
            if (cue.getCode() == Constant.Int.SUC) {
                tvTargetName.setText((String) cue.getData().getFullName());
                toUID = cue.getData().getUID();
                CheckUserExistCode = 1;
            } else {
                tvTargetName.setText(R.string.code1020001);
                tvTargetName.setTextColor(getResources().getColor(R.color.redE0));
                CheckUserExistCode = 0;
                toUID = null;
            }
        }
    }

    /**
     * 更新UI
     */
    private void upDateUi(SingleCoinInfoEnity.DataBean data) {
        if (data == null) return;

        tagDescribe = data.getTagDescribe();
        showSpecilaNote();

        availableAmount = Double.parseDouble(data.getAvailableAmount());
        availableAmountStr = data.getAvailableAmount();
        minAmount = Double.parseDouble(data.getMinWithdrawAmount());
        tvAvalible.setText(getString(R.string.str_avlible_amount) + data.getAvailableAmount());
        //最小提币数量
        minWithdrawAmount = data.getMinWithdrawAmount();
        //最小费用
        withdrawalMinFee = data.getWithdrawalMinFee();


        String withdrawalFeeType = data.getWithdrawalFeeType();
        isF = withdrawalFeeType.equals("F");
        if (withdrawalFeeType.equals("F")) {
            gdFee = data.getWithdrawalFee();
            gdFee2 = gdFee > withdrawalMinFee ? gdFee : withdrawalMinFee;
        } else {
            dynamicFee = data.getWithdrawalFee();
        }

        withdrawTips = data.getWithdrawTips();
        getCoinHintAdapter.setData(withdrawTips);

    }


    /**
     * 获取单个币种信息
     */
    private void getSingleCoinInfo() {
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfo, SharedUtil.getToken(mContext), this, "currencyId", currencyId);
    }

    @Override
    public void onFailure(String url, String error) {
        isCommiting = false;
    }

    // 获取法币币种
    private void getCoinList(){
        OkHttpUtil.getJson(Constant.URL.currencies, this);
    }

}
