package cn.suozhi.DiBi.c2c.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.AdvanceDetailEnity;
import cn.suozhi.DiBi.c2c.model.C2cCkPriceEnity;
import cn.suozhi.DiBi.c2c.model.C2cLCoinEntity;
import cn.suozhi.DiBi.c2c.model.CoinEnity;
import cn.suozhi.DiBi.c2c.model.PayTypeEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.home.view.CollectMoneyActivity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

/**
 * 发布广告的界面
 */
public class PublishAdvanceActivity extends BaseActivity implements OkHttpUtil.OnDataListener {
    private static final int REQUEST_CODE = 1;
    @BindView(R.id.tv_sell)
    TextView tvSell;

    @BindView(R.id.tv_commit)
    TextView tvCommit;


    @BindView(R.id.tv_buy)
    TextView tvBuy;

    @BindView(R.id.rv_pay_type)
    RecyclerView rvPayType;

    @BindView(R.id.tv_single_max_coin)
    TextView tvSingleMaxCoin;

    @BindView(R.id.tv_single_coin)
    TextView tvSingleCoin;

    @BindView(R.id.tv_coin)
    TextView tvCoin;

    @BindView(R.id.tv_price_coin)
    TextView tvPriceCoin;


    @BindView(R.id.et_price)
    EditText etPrice;  //单价

    @BindView(R.id.et_amount)
    EditText etAmount;  //数量

    @BindView(R.id.et_small_amount)
    EditText etSmallAmount;  //单笔最小限额

    @BindView(R.id.et_max_amount)
    EditText etMaxAmount;  //单笔最大限额

    @BindView(R.id.et_note)
    EditText etNote;  //备注
    @BindView(R.id.tv_avalible_amount)
    TextView tvAvalible;  //可用余额
    @BindView(R.id.tv_amount_coin)
    public TextView tvAmountCoin;

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    private boolean isCommiting;
    //广告类型
    private String type = "2";
    private LoadingDialog
            loadingDialog;
    //支付方式的集合
    private String currentPayType;
    private PayTypeAdapter coinAdapter;
    private String adId;
    //是否是编辑广告
    private boolean isEditAdvance = false;
//    private CoinEnity allCoins;
    private C2cLCoinEntity allCoins;
    //当前选中的法币
    private String currentFBCoin = "CNY";
    //当前选中的基础币种
    private String baseCoin = "USDT";
    //参考价格
    private double ckprice;
    //单价
    private String price;
    //最小限额
    private String minAmount;
    //最大限额
    private String maxAmount;
    //数量
    private String amount;
    //备注
    private String remark;
    //usdt可用余额
    private String availableAmount;
    //已选择的支付方式
    private String[] payArry;

    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;

    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;
    private boolean icClickCommit;

    @BindView(R.id.rl_all_amout)
    public RelativeLayout rl_all;

    private int digits = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_publish_advance;
    }

    private void setLisenter() {
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //删除“.”后面超过3位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > digits) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + digits+1);
                        etPrice.setText(s);
                        etPrice.setSelection(s.length()); //光标移到最后
                    }
                }
                //如果"."在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etPrice.setText(s);
                    etPrice.setSelection(2);
                }

                //如果起始位置为0,且第二位跟的不是".",则无法后续输入
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etPrice.setText(s.subSequence(0, 1));
                        etPrice.setSelection(1);
                        return;
                    }
                }

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
            }
        });
    }

    @Override
    protected void init() {
        adId = getIntent().getStringExtra("adId");
        showSell();
        tvBuy.setSelected(false);
        if (TextUtils.isEmpty(adId)) {
            isEditAdvance = false;
            tvCoin.setEnabled(true);
            tvBuy.setEnabled(true);
            tvSell.setEnabled(true);
            ToolbarUtil.initToolbar(toolbar, getString(R.string.publish_advance), v -> onBackPressed());
            tvCommit.setText(getString(R.string.publish_advance));
        } else {
            isEditAdvance = true;
            //修改广告不可以修改法币类型 和 广告类型
            tvCoin.setEnabled(false);
            tvBuy.setEnabled(false);
            tvSell.setEnabled(false);
            ToolbarUtil.initToolbar(toolbar, getString(R.string.edit_advance), v -> onBackPressed());
            tvCommit.setText(getString(R.string.edit_advance));
        }
        rvPayType.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        coinAdapter = new PayTypeAdapter(mContext);
        rvPayType.setAdapter(coinAdapter);


        if (rvPayType.getItemDecorationCount() == 0) {
            rvPayType.addItemDecoration(new DecoRecycler(mContext, R.drawable.deco_8_trans,
                    DecoRecycler.Edge_NONE));
        }

        editRemoveEmoji(etNote);
        etNote.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

        coinAdapter.setOnItemClickListener(new AbsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                for (int i = 0; i < coinAdapter.getData().size(); i++) {
                    if (i == position) {
                        if (coinAdapter.getData().get(i).isSelected()) {
                            coinAdapter.getData().get(i).setSelected(false);
                        } else {
                            coinAdapter.getData().get(i).setSelected(true);
                        }

                        currentPayType = getPaytype(coinAdapter.getData());
                    }
                }

                coinAdapter.notifyDataSetChanged();
            }
        });

        setLisenter();

    }


    @Override
    protected void onResume() {
        super.onResume();
        getPayMode();
    }

    /**
     * 支付方式参数
     */
    private String getPaytype(List<PayTypeEnity.DataBean> data) {
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected()) {
                s += data.get(i).getAccountType() + ",";
            }
        }
        if (TextUtils.isEmpty(s)) {
            return "";
        } else {
            return TextUtils.isEmpty(s.substring(0, s.length() - 1)) ? "" : s.substring(0, s.length() - 1);

        }
    }

    private void showSell() {
        tvSell.setSelected(true);
        tvBuy.setSelected(false);
        rl_all.setVisibility(View.VISIBLE);
    }

    private void showBuy() {
        tvBuy.setSelected(true);
        tvSell.setSelected(false);
        rl_all.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {
        getSingleCoinInfo();
        if (isEditAdvance) {
            //是修改广告
            showLoading();
            OkHttpUtil.getJsonToken(Constant.URL.advanceDetail, SharedUtil.getToken(mContext), this, "adId", adId + "");
        } else {
            getCoin();
        }
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


    /**
     * 获取用户当前所拥有的支付方式
     */
    private void getPayMode() {
        OkHttpUtil.getJsonToken(Constant.URL.getPayType, SharedUtil.getToken(mContext), this);
    }

    /**
     * 获取单个币种信息
     */
    private void getSingleCoinInfo() {
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfoByName, SharedUtil.getToken(mContext), this, "currencyCode", baseCoin);
    }

    /**
     * 获取单个币种信息
     */
    private void getSingleCoinInfo2(String strCoin) {
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfoByName, SharedUtil.getToken(mContext), this, "currencyCode", strCoin);
    }


    /**
     * 获取参考价格
     */
    private void getCkPrice() {
        OkHttpUtil.getJsonToken(Constant.URL.getCkPrice, SharedUtil.getToken(mContext), this);
    }

    /**
     * 获取法币列表
     */
    private void getCoin() {
//        OkHttpUtil.getJsonToken(Constant.URL.coinListC2c, SharedUtil.getToken(mContext), this, "category", "FIAT");
        OkHttpUtil.getJson(Constant.URL.currencies, this);
    }

    @OnClick({R.id.tv_sell, R.id.tv_buy, R.id.tv_set_edit, R.id.tv_commit, R.id.tv_coin, R.id.tv_all})
    public void pulishClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sell:
                type = "2";
                rl_all.setVisibility(View.VISIBLE);
                showSell();
                showSellCoinType(baseCoin);
                break;
            case R.id.tv_buy:
                type = "1";
                rl_all.setVisibility(View.GONE);
                showBuy();
                showBuyCoinType(baseCoin);
                break;
            case R.id.tv_commit:
                icClickCommit = true;
                if (checkCommitInfo()) {
                    if (isEditAdvance) {
                        if (!isCommiting) {
                            showLoading();
                            isCommiting = true;
                            editAd();
                        }

                    } else {
                        if (!isCommiting) {
                            getOnceToken();
                            isCommiting = true;
                        }
                    }
                }

                break;
            case R.id.tv_set_edit:
                startActivity(new Intent(this, CollectMoneyActivity.class));
                break;
            case R.id.tv_coin:
                Intent intent = new Intent(mContext, C2CCoinActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_all:
                etAmount.setText(availableAmount);
                break;
        }
    }

    /**
     * 检查提交的信息
     */
    private boolean checkCommitInfo() {
        price = etPrice.getText().toString();
        minAmount = etSmallAmount.getText().toString();
        maxAmount = etMaxAmount.getText().toString();
        amount = etAmount.getText().toString();
        remark = etNote.getText().toString();

        double p = Util.parseDouble(price);
        double a = Util.parseDouble(amount);
        int min = Util.parseInt(minAmount);
        int max = Util.parseInt(maxAmount);
        if (p <= 0) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }
        if (a <= 0) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }
        // 判断余额
//        if (a > Util.parseDouble(availableAmount)) {
//            CodeStrUtil.showToastHintFail(this, getString(R.string.str_publish_max));
//            return false;
//        }
        if (min <= 0 || max <= 0 || min > max) {
            CodeStrUtil.showToastHintFail(this, getString(R.string.str_edit_correct_money));
            return false;
        }
        if (TextUtils.isEmpty(currentPayType)) {
            CodeStrUtil.showToastHintFail(this, getString(R.string.str_select_one_collecty));
            return false;
        }

        if (type.equals("2")) {
            //出售
            if (a * p < min) {
                CodeStrUtil.showToastHintFail(this, getString(R.string.str_edit_ad_sell_hint));
                return false;
            }
        } else {
            //购买
            if (a < min) {
                CodeStrUtil.showToastHintFail(this, getString(R.string.str_edit_buy_hint));
                return false;
            }
        }
        return true;
    }

    private void getOnceToken() {
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.getAdvanceOncetoken);
    }

    /**
     * 发布广告
     */
    private void publishAd(String onceToken) {
//        Log.e("addPub","单价price：" + price + "|数量：" + getTwoPointNums(amount,4));
        OkHttpUtil.postJsonToken(Constant.URL.publishAdvance, SharedUtil.getToken(mContext), this,
                "currencyCode", baseCoin, // 基础币种类型
                "legalCurrencyCode", currentFBCoin, //法币类型
                //"legalCurrencyCode", baseCoin, //基础币种类型
                "limitMax", maxAmount, //单笔限额最大值
                "limitMin", minAmount, //单笔限额最小值
                "onceToken", onceToken,//一次性token
                "payModes", currentPayType,//支付方式[1支付宝|2微信|3银行卡], 多种用逗号分隔
                "price", getThreePointNums(price,3),//单价
                "remark", TextUtils.isEmpty(remark) ? "" : remark,//广告备注
                "totalQuantity", getTwoPointNums(amount,4), //数量
                "type", type  //广告类型[1-购买|2-出售]
        );
    }

    /**
     * 修改广告
     */
    private void editAd() {
        OkHttpUtil.putJsonToken(Constant.URL.editAdvance, SharedUtil.getToken(mContext), this,
                "adId", adId + "", //币种
                "limitMax", maxAmount, //单笔限额最大值
                "limitMin", minAmount, //单笔限额最小值
                "payModes", currentPayType,//支付方式[1支付宝|2微信|3银行卡], 多种用逗号分隔
                "price", getThreePointNums(price,3),//单价
                "remark", TextUtils.isEmpty(remark) ? "" : remark,//广告备注
                "totalQuantity", getTwoPointNums(amount,4) //数量
        );
    }

    private String getTwoPointNums(String nums, int flag) {
        return Util.formatFloor(Double.parseDouble(nums), flag);
    }

    private String getThreePointNums(String nums, int flag) {
        return Util.formatFloor(Double.parseDouble(nums), flag);
    }



    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        if (url.equals(Constant.URL.onceToken)) {
            isCommiting = false;
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                publishAd(onceToken);
            }
        } else if (url.equals(Constant.URL.getPayType)) {
            //获取所有的支付方式
            PayTypeEnity enity = GsonUtil.fromJson(json, PayTypeEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                List<PayTypeEnity.DataBean> data = enity.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getStatus() == 0) {
                        data.remove(i);
                        i--;
                    }
                }
                if (isEditAdvance) {
                    //如果是修改广告 需要处理数据

                    for (int i = 0; i < data.size(); i++) {
                        for (int j = 0; j < payArry.length; j++) {
                            if ((data.get(i).getAccountType() + "").equals(payArry[j])) {
                                data.get(i).setSelected(true);
                            }
                        }
                    }
                }
                coinAdapter.setData(data);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
                Util.checkLogin(mContext, enity.getCode());
            }
        } else if (url.equals(Constant.URL.getCkPrice)) {
            //获取参考价格
            C2cCkPriceEnity enity = GsonUtil.fromJson(json, C2cCkPriceEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                List<C2cCkPriceEnity.DataBean> data = enity.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getSymbol().equals(currentFBCoin) && baseCoin.equals("USDT")) {
                        ckprice = data.get(i).getPrice();
                    } else if (baseCoin.equals("DIC")) {
                        ckprice = 1;
                    } else if (data.get(i).getSymbol().equals(currentFBCoin)){
                        ckprice = data.get(i).getPrice();
                    }
                }
                //显示参考价格
                etPrice.setHint(getString(R.string.str_ck_price) + Util.formatTinyDecimal(ckprice, false));
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
                Util.checkLogin(mContext, enity.getCode());
            }
        } else if (url.equals(Constant.URL.advanceDetail)) {
            //广告详情
            AdvanceDetailEnity enity = GsonUtil.fromJson(json, AdvanceDetailEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                AdvanceDetailEnity.DataBean data = enity.getData();
                getSingleCoinInfo2(data.getCurrencyCode());
                setDetailUI(data);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
                Util.checkLogin(mContext, enity.getCode());
            }

        } else if (url.equals(Constant.URL.editAdvance)) {
            //修改广告
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                CodeStrUtil.showToastHint(this, getString(R.string.str_ad_edit_suc));
                finish();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(mContext, entity.getCode());
            }
        }
//        else if (url.equals(Constant.URL.coinListC2c)) {
//            //法币列表
//            allCoins = GsonUtil.fromJson(json, CoinEnity.class);
//            if (allCoins.getCode() == Constant.Int.SUC) {
//                List<CoinEnity.DataBean> coinList = allCoins.getData();
//                currentFBCoin = coinList.get(0).getName();
//                showSellCoinType(currentFBCoin);
//                getCkPrice();
//            }
//        }
        else if (url.equals(Constant.URL.singleCoinInfoByName)) {
            //获取单个币种信息
            SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
            if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {
                availableAmount = singleCoinInfoEnity.getData().getAvailableAmount();
                tvAvalible.setText(getString(R.string.str_avlible_amount) + " " + Util.formatTinyDecimal(
                        Double.parseDouble(availableAmount), false));
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                Util.checkLogin(this, singleCoinInfoEnity.getCode());
            }
        }
        else if (url.equals(Constant.URL.currencies)) {
            //法币列表2
            allCoins = GsonUtil.fromJson(json, C2cLCoinEntity.class);
            if (allCoins.getCode() == Constant.Int.SUC) {
                List<C2cLCoinEntity.DataBean> coinList = allCoins.getData();
                //currentFBCoin = coinList.get(0).getCode();
                baseCoin = coinList.get(0).getCode();
                showSellCoinType(baseCoin);
                getCkPrice();
            }
        }
        if (url.equals(Constant.URL.publishAdvance)) {
            //确认支付
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                CodeStrUtil.showToastHint(this, getString(R.string.str_ad_publish_suc));
                finish();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(mContext, entity.getCode());
            }
        }
    }


    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        isCommiting = false;
        Util.saveLog(PublishAdvanceActivity.this, url, error);
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

    public class PayTypeAdapter extends AbsRecyclerAdapter<PayTypeEnity.DataBean> {


        public PayTypeAdapter(Context context) {
            super(context, R.layout.item_paytype_two);
        }

        @Override
        public void onBindHolder(RecyclerHolder holder, PayTypeEnity.DataBean d, int position) {

            switch (d.getAccountType()) {
                case 1:
                    holder.bindTextView(R.id.tv_wechat, getString(R.string.str_alipay));
                    break;
                case 2:
                    holder.bindTextView(R.id.tv_wechat, getString(R.string.str_wechat));
                    break;
                case 3:
                    holder.bindTextView(R.id.tv_wechat, getString(R.string.str_bank));
                    break;
            }
            TextView tvCoin = (TextView) holder.getView(R.id.tv_wechat);
            tvCoin.setSelected(d.isSelected());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果结果码等于RESULT_OK
            if (requestCode == REQUEST_CODE) {
                //currentFBCoin = data.getExtras().getString("result");
                baseCoin = data.getExtras().getString("result");
                // 获取单个
                getSingleCoinInfo();
                if(type == "1" || type.equals("1")){
                    showBuyCoinType(baseCoin);
                } else if(type == "2" || type.equals("2")){
                    showSellCoinType(baseCoin);
                } else {
                    showSellCoinType(baseCoin);
                }
                getCkPrice();
            }
        }
    }


    /**
     * 显示出售 法币币种单位
     */
    private void showSellCoinType(String coin) {
        tvPriceCoin.setText("CNY");
        tvCoin.setText(coin);
        tvSingleCoin.setText("CNY");
        tvSingleMaxCoin.setText("CNY");
        tvAmountCoin.setText(coin);
    }

    /**
     * 显示购买 法币币种单位
     */
    private void showBuyCoinType(String coin) {
        tvPriceCoin.setText("CNY");
        tvCoin.setText(coin);
        tvSingleCoin.setText(coin);
        tvSingleMaxCoin.setText(coin);
        tvAmountCoin.setText(coin);
    }


    /**
     * 广告详情信息
     */
    private void setDetailUI(AdvanceDetailEnity.DataBean data) {
        if (data == null) return;
        currentFBCoin = data.getLegalCurrencyCode();
        baseCoin = data.getCurrencyCode();
        showSellCoinType(baseCoin);

        etPrice.setText(Util.formatTinyDecimal(data.getPrice(), false));

        etSmallAmount.setText(Util.formatTinyDecimal(data.getLimitMin(), false));
        etMaxAmount.setText(Util.formatTinyDecimal(data.getLimitMax(), false));
        etNote.setText(TextUtils.isEmpty(data.getRemark()) ? "" : data.getRemark());

        if (data.getType() == 1) {
            showBuy();
        } else {
            showSell();
        }

        String payModes = data.getPayModes();
        currentPayType = payModes;
        payArry = payModes.split(",");
        getCkPrice();
    }
}
