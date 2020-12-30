package cn.suozhi.DiBi.wallet.view;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.wallet.adapter.GetCoinHintAdapter;
import cn.suozhi.DiBi.wallet.model.GetCoinREntity;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

/**
 * 提币界面
 */
public class GetCoinActivity extends BaseActivity implements BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {

    private static final int REQUEST_CODE = 2;
    private static final int REQUEST_SCAN = 3;
    private static final int GET_ADDRESS = 4;
    @BindView(R.id.rv_hint)
    RecyclerView rvHint;

    @BindView(R.id.lly_special_coin)
    LinearLayout llySpecialCoin;

    @BindView(R.id.tv_selected_coin)
    TextView tvSlectCoin;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    //手续费
    @BindView(R.id.tv_miners)
    TextView tvFee;
    //实际到账
    @BindView(R.id.tv_actul_account)
    TextView tvActual;

    @BindView(R.id.tv_get_coin_name)
    TextView tvGetCoinNmae;
    //矿工费的币种
    @BindView(R.id.tv_miner_coin_type)
    TextView tvFeeCoin;
    //实际到账的币种
    @BindView(R.id.tv_actul_account_coin)
    TextView tvActualCoin;

    @BindView(R.id.tv_special_note)
    TextView tvSpecilaNote;
    //可用余额
    @BindView(R.id.tv_avalible_amount)
    TextView tvAvalible;

    @BindView(R.id.et_address)
    EditText etAddress;

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
    private double availableAmountStr = 0;
    private String tagDescribe;

    @BindView(R.id.tv_chain_type_text)
    public TextView chtt;

    @BindView(R.id.tv_rechargeType)
    public TextView tvType;
    @BindView(R.id.tv_rechargeType1)
    public TextView tvType1;
    @BindView(R.id.ll_get_coin_chain)
    public LinearLayout llChain;

    private int selectedUsdt = 1; // 用户选择的usdt链类型 0-omni    1-erc20
    private int usdtIdOMNI = 4;
    private int usdtIdERC20 = 18;

    private double usdtOMNIAmout = 0;
    private double usdtERC20Amout = 0;

    private double kgf = 0;

    @Override
    protected int getViewResId() {
        return R.layout.activity_get_coin;
    }

    private void setLisenter() {
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

        gs = getIntent().getIntExtra("state", -1);
        area = getIntent().getStringExtra("area");
        account = getIntent().getStringExtra("account");

        rvHint.setLayoutManager(new LinearLayoutManager(mContext));
        getCoinHintAdapter = new GetCoinHintAdapter(mContext);
        rvHint.setAdapter(getCoinHintAdapter);


        currentCode = getIntent().getStringExtra("result");
        currencyId = getIntent().getStringExtra("coinId");

//        Log.e("提币","接收id = " + currencyId);
//        Log.e("提币" , "接收code = " + currentCode);

        preci = getIntent().getIntExtra("precis", -1);

        setPreicNums();
        tvSlectCoin.setText(currentCode);
        showSpecilaNote();

        tvTitle.setText(currentCode + getString(R.string.str_get_coin));
        tvGetCoinNmae.setText(currentCode);
        tvFeeCoin.setText(currentCode);
        tvActualCoin.setText(currentCode);

        // 进来时默认选中ERC20
        if(currentCode == "USDT" || currentCode.equals("USDT")){
            selectedUsdt = 1;
            currencyId = String.valueOf(usdtIdERC20);
            tvType.setTextColor(getResources().getColor(R.color.gy4D));
            tvType.setTextSize(13);
            tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
            tvType1.setTextSize(14);
        } else {
            selectedUsdt = 0;
            currentCode = getIntent().getStringExtra("result");
            currencyId = getIntent().getStringExtra("coinId");
            tvType.setTextColor(getResources().getColor(R.color.gy4D));
            tvType.setTextSize(13);
            tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
            tvType1.setTextSize(14);
        }

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
                        tvFee.setText(gdFee2 + "");
                    } else {

                        double fee = editAmount * dynamicFee > withdrawalMinFee ? editAmount * dynamicFee : withdrawalMinFee;
                        tvFee.setText(Util.formatTinyDecimal(fee, true));
                        actual = editAmount - fee;
                    }

                    if (actual <= 0) {
                        tvActual.setText(getTwoPointNums(0 + "", preci));
                    } else {
                        tvActual.setText(getTwoPointNums(actual + "", preci));
                    }

//                    restAvailableAmount = availableAmount - editAmount;
//                    tvAvalible.setText(getString(R.string.str_avlible_amount) + restAvailableAmount);
                } else {
                    tvActual.setText("");
                    tvFee.setText("");
                }
            }
        });
    }

    private void showHide(){
        if(currentCode == "USDT" || currentCode.equals("USDT")){
            chtt.setText(getString(R.string.chainTypeInfo));
            llChain.setVisibility(View.VISIBLE);
            if (currencyId == "18" || currencyId.equals(18) || currencyId.equals("18")) {
                // 默认选择erc20
                selectedUsdt = 1;
                tvType.setText("OMNI");
                tvType.setTextColor(getResources().getColor(R.color.gy4D));
                tvType.setTextSize(13);
                tvType1.setText("ERC20");
                tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType1.setTextSize(14);
            } else if(currencyId == "4" || currencyId.equals(4) || currencyId.equals("4")){
                // ommi
                selectedUsdt = 0;
                tvType.setText("OMNI");
                tvType.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType.setTextSize(14);
                tvType1.setText("ERC20");
                tvType1.setTextColor(getResources().getColor(R.color.gy4D));
                tvType1.setTextSize(13);
            }
        } else {
            llChain.setVisibility(View.GONE);
        }
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
//        String selectCoin = tvSlectCoin.getText().toString().trim();
//        if (tvSlectCoin.getText().toString().trim().equals("EOS")
//                || tvSlectCoin.getText().toString().trim().equals("XLM")
//                || tvSlectCoin.getText().toString().trim().equals("XRP")
//                || tvSlectCoin.getText().toString().trim().equals("GDM")
//                || tvSlectCoin.getText().toString().trim().equals("XMR") || selectCoin.equals("WCG") || selectCoin.equals("WEN") || selectCoin.equals("ORA") || selectCoin.equals("IPSO") || selectCoin.equals("HCC")) {
//            llySpecialCoin.setVisibility(View.VISIBLE);
//
//
//            if (tvSlectCoin.getText().toString().trim().equals("EOS")
//                    || tvSlectCoin.getText().toString().trim().equals("XLM") || selectCoin.equals("WCG") || selectCoin.equals("WEN") || selectCoin.equals("ORA") || selectCoin.equals("IPSO") || selectCoin.equals("HCC") || selectCoin.equals("GDM")) {
//                tvSpecilaNote.setText("MEMO");
//                etSpecialNote.setHint(getString(R.string.str_memo_hint));
//
//            } else if (tvSlectCoin.getText().toString().trim().equals("XRP")) {
//                tvSpecilaNote.setText("TAG");
//                etSpecialNote.setHint(getString(R.string.str_tag_hint));
//            } else {
//                tvSpecilaNote.setText("Payment ID");
//                etSpecialNote.setHint(getString(R.string.str_payment_hint));
//            }
//        } else {
//            llySpecialCoin.setVisibility(View.GONE);
//        }


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
        getSingleCoinInfo();
    }

    @OnClick({R.id.iv_back, R.id.iv_scan, R.id.iv_address, R.id.tv_selected_coin, R.id.tv_record, R.id.tv_commit, R.id.tv_all ,
                R.id.tv_rechargeType , R.id.tv_rechargeType1})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.iv_scan:
                if (Util.permCheckReq(this, Constant.Code.CameraCode,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                    Intent intentScan = new Intent(mContext, ScanActivity.class);
                    startActivityForResult(intentScan, REQUEST_SCAN);
                }
                break;
            case R.id.iv_address:
                startActivityForResult(new Intent(mContext, AdreessListActivity.class)
                        .putExtra("state", gs)
                        .putExtra("area", area)
                        .putExtra("from", 1)
                        .putExtra("coin", tvSlectCoin.getText().toString())
                        .putExtra("account", account), GET_ADDRESS);
                break;
            case R.id.tv_commit:
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

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_selected_coin:
                Intent intent = new Intent(mContext, SelectedCoinActivity.class)
                        .putExtra("type", Constant.Strings.Intent_Back_Withdraw);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_record:
                Intent intent1 = new Intent(mContext, AccountOrderActivity.class);
                intent1.putExtra("status", 2);
                startActivity(intent1);
                break;
            case R.id.tv_all:
                etAmount.setText(""+availableAmountStr);
                if (isF) {
                    actual = availableAmount - gdFee2;
                    tvFee.setText(gdFee2 + "");
                } else {

                    double fee = availableAmount * dynamicFee > withdrawalMinFee ? availableAmount * dynamicFee : withdrawalMinFee;
                    tvFee.setText(Util.formatTinyDecimal(fee, true));

//                    if (availableAmount * dynamicFee > withdrawalMinFee){
//                        actual = availableAmount * (1 - dynamicFee);
//                    }else {
//
//                    }
                    actual = availableAmount - fee;
                }

                if (actual <= 0) {
                    tvActual.setText(getTwoPointNums(0 + "", preci));
                } else {
                    tvActual.setText(getTwoPointNums(actual + "", preci));
                }

                break;
            case R.id.tv_rechargeType: // ommi
                selectedUsdt = 0;
                tvType.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType.setTextSize(14);
                tvType1.setTextColor(getResources().getColor(R.color.gy4D));
                tvType1.setTextSize(13);
                currencyId = String.valueOf(usdtIdOMNI);
                // 调用更新
                etAddress.setText(null);
                etAmount.setText(null);
                tvFee.setText(null);
                tvActual.setText(null);
                loadData();
                break;
            case R.id.tv_rechargeType1: // erc20
                selectedUsdt = 1;
                tvType.setTextColor(getResources().getColor(R.color.gy4D));
                tvType.setTextSize(13);
                tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType1.setTextSize(14);
                // 调用更新
                currencyId = String.valueOf(usdtIdERC20);
                etAddress.setText(null);
                etAmount.setText(null);
                tvFee.setText(null);
                tvActual.setText(null);
                loadData();
                break;
        }
    }

    private boolean checkInfo() {
        addresss = etAddress.getText().toString().trim();
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

//        if (currentCode.equals("EOS")
//                || currentCode.equals("XLM")
//                || currentCode.equals("XRP")
//                || currentCode.equals("GDM")
//                || currentCode.equals("XMR") || currentCode.equals("WCG") || currentCode.equals("WEN") || currentCode.equals("ORA") || currentCode.equals("IPSO") || currentCode.equals("HCC")) {
//
//            if (TextUtils.isEmpty(addressTag)) {
//                return false;
//            }
//            if (currentCode.equals("EOS")
//                    || currentCode.equals("XLM")) {
//
//            } else if (currentCode.equals("XRP")) {
//
//            } else {
//
//            }
//        }

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
                tvSlectCoin.setText(coin);
                showSpecilaNote();
                currencyId = coinId;
                currentCode = coin;
                tvTitle.setText(coin + getString(R.string.str_get_coin));
                tvGetCoinNmae.setText(coin);
                tvFeeCoin.setText(coin);
                tvActualCoin.setText(coin);

                //清空数据
                etAddress.setText("");
                etAmount.setText("");
                etSpecialNote.setText("");
                setPreicNums();

                // 选择USDT时获取ERC20类型的数据
                if(coin == "USDT" || coin.equals("USDT")){
                    // 进来时默认选中ERC20
                    selectedUsdt = 1;
                    currencyId = String.valueOf(usdtIdERC20);
                    tvType.setTextColor(getResources().getColor(R.color.gy4D));
                    tvType.setTextSize(13);
                    tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
                    tvType1.setTextSize(14);
                } else {
                    currencyId = coinId;
                }

                getSingleCoinInfo();
            } else if (requestCode == REQUEST_SCAN) {
                tvHintOne.setVisibility(View.GONE);
                String address = data.getExtras().getString("result");
                etAddress.setText(address);
            } else if (requestCode == GET_ADDRESS) {
                tvHintOne.setVisibility(View.GONE);
                addresss = data.getExtras().getString("address");
                addressTag = data.getExtras().getString("addressTag");
                addressExt = data.getExtras().getString("addressExt");
                etSpecialNote.setText(TextUtils.isEmpty(addressExt) ? "" : addressExt);
                etAddress.setText(addresss);


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
        /*if (url.equals(Constant.URL.chargeGetCoin)) {

            AllCoinEnity allCoins = GsonUtil.fromJson(json, AllCoinEnity.class);
            if (allCoins.getCode() == Constant.Int.SUC) {
                List<AllCoinEnity.DataBean.CurrencyDtoBean> coinList = allCoins.getData().getCurrencyDto();
                currentCode = coinList.get(0).getCode();
                currencyId = coinList.get(0).getCurrencyId() + "";
                tvSlectCoin.setText(currentCode);
                tvTitle.setText(currentCode + getString(R.string.str_get_coin));
                tvGetCoinNmae.setText(currentCode);
                tvFeeCoin.setText(currentCode);
                tvActualCoin.setText(currentCode);
                preci = coinList.get(0).getPrecis();
                getSingleCoinInfo();

            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        allCoins.getCode(), allCoins.getMsg()));
                Util.checkLogin(this, allCoins.getCode());
            }
        } else */if (url.equals(Constant.URL.singleCoinInfo)) {
            SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
            if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {
                if(singleCoinInfoEnity.getData().getCurrencyId()==4){
                    availableAmountStr = Double.parseDouble(singleCoinInfoEnity.getData().getAvailableAmount());
                }
                upDateUi(singleCoinInfoEnity.getData());
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                Util.checkLogin(this, singleCoinInfoEnity.getCode());
            }
        } else if (url.equals(Constant.URL.singleCoinInfo)) {
            SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
            if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {
                if(singleCoinInfoEnity.getData().getCurrencyId()==4){
                    availableAmountStr = Double.parseDouble(singleCoinInfoEnity.getData().getAvailableAmount());
                }
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                Util.checkLogin(this, singleCoinInfoEnity.getCode());
            }
        }

        else if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                OkHttpUtil.postJsonToken(Constant.URL.getCoin, SharedUtil.getToken(mContext), this,
                        "address", addresss, //提现地址
                        "addressExt", TextUtils.isEmpty(addressExt) ? "" : addressExt, //特殊币种地址扩展信息
                        "addressTag", TextUtils.isEmpty(addressTag) ? "" : addressTag,  //提现地址标签
                        "amount", getTwoPointNums(amount, preci),  //提现数量
                        "currencyId", currencyId,//体现币种
                        "deviceType", deveiceId, //设备类型 PC端传空即可,APP端传设备唯一识别号(与登录日志接口的设备类型传同一个值)
                        "onceToken", onceToken, //一次性token
                        "verifyCode", verifyCode//验证码
                );
            } else {
                isCommiting = false;
            }
        } else if (url.equals(Constant.URL.getCoin)) {
            isCommiting = false;
            GetCoinREntity gcre = GsonUtil.fromJson(json, GetCoinREntity.class);
            if (gcre.getCode() == Constant.Int.SUC) {
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string._str_get_coin_suc));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                finish();

            } else {
                //1210012, "提币数量超过今日提币限额,建议提币数量:"
                if (gcre.getCode() == 1210012) {
                    ToastUtil.initToast(this, getString(R.string.str_get_coin_1210012_hint) + gcre.getMsg() );
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this, gcre.getCode(), gcre.getMsg()));
                    Util.checkLogin(this, gcre.getCode());
                }

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

        // 显示余额
        // OMNI
        if(data.getCurrencyId()==4){
            availableAmount = Double.parseDouble(data.getAvailableAmount());
            availableAmountStr = Double.parseDouble(data.getAvailableAmount());
            tvAvalible.setText(getString(R.string.str_avlible_amount) + data.getAvailableAmount());
        }
        // ERC20
        else if (data.getCurrencyId()==18){
            tvAvalible.setText(getString(R.string.str_avlible_amount) + availableAmountStr);
        }
        // 其他币
        else {
            availableAmount = Double.parseDouble(data.getAvailableAmount());
//            availableAmountStr = Double.parseDouble(data.getAvailableAmount());
            tvAvalible.setText(getString(R.string.str_avlible_amount) + data.getAvailableAmount());
        }

        minAmount = Double.parseDouble(data.getMinWithdrawAmount());

//        tvAvalible.setText(getString(R.string.str_avlible_amount) + data.getAvailableAmount());

        //最小提币数量
        minWithdrawAmount = data.getMinWithdrawAmount();
        //最小费用
        withdrawalMinFee = data.getWithdrawalMinFee();

        // 矿工费
        String withdrawalFeeType = data.getWithdrawalFeeType();
        isF = withdrawalFeeType.equals("F");
        if (withdrawalFeeType.equals("F")) {
            gdFee = data.getWithdrawalFee();
            gdFee2 = gdFee > withdrawalMinFee ? gdFee : withdrawalMinFee;
            tvFee.setText(gdFee2 + "");
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
        showHide();
        if(currentCode=="USDT" || currentCode.equals("USDT")){
            OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfoU, SharedUtil.getToken(mContext), this, "currencyId", "4");
        }
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfo, SharedUtil.getToken(mContext), this, "currencyId", currencyId);
    }

    @Override
    public void onFailure(String url, String error) {
        isCommiting = false;
    }
}
