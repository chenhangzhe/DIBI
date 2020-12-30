package cn.suozhi.DiBi.c2c.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.C2cBuyInfoEnity;
import cn.suozhi.DiBi.c2c.model.PayTypeEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.RegexUtils;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;

/**
 * 法币 - 下单页面
 */
public class PlaceOrderActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    private int adid;
    //购买USDT 1  出售USDT
    private int type;

    @BindView(R.id.rv_pay_type)
    RecyclerView rvPayType;

    @BindView(R.id.tv_poError)
    TextView tvError;

    @BindView(R.id.tv_coin_one)
    TextView tvCoinOne;//金额的单位

    @BindView(R.id.tv_coin_two)
    TextView tvCoinTwo;//数量的单位

    @BindView(R.id.et_money)
    EditText etMoney; //金额

    @BindView(R.id.et_amount)
    EditText etAmount; //数量

    @BindView(R.id.tv_sell_or_buy)
    TextView tvSellOrBuy;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_price)
    TextView tvPrice; //单价

    @BindView(R.id.tv_rest_nums)
    TextView tvRestNums;  //剩余数量

    @BindView(R.id.tv_limit_amount)
    TextView tvLimmitAmount;  //限额

    @BindView(R.id.lly_pay_type)
    LinearLayout llyPayType; //支付方式

    @BindView(R.id.tv_note)
    TextView tvNote;  //备注
    //法币
    private String legalCurrencyCode;
    //数字货币
    private String currencyCode;
    private PayTypeAdapter coinAdapter;
    private int currentPayType;//当前支付方式
    //是否在进行中
    private boolean isCommiting;
    private double price;//单价
    private LoadingDialog loadingDialog;
    private boolean isFlag = true;

    private boolean isBussiness;
    private String payModes;
    private double limitMin;
    private double limitMax;
    //剩余的数量
    private double surplusQuantity;

    private int pm = 2, pa = 4;
    private boolean fromUser = false;//是否手动输入

    @Override
    protected int getViewResId() {
        setSystemUI();
        return R.layout.activity_place_order;
    }

    @Override
    protected void init() {
        type = getIntent().getIntExtra("type", -1);
        adid = getIntent().getIntExtra("adid", -1);
        legalCurrencyCode = getIntent().getStringExtra("legalCurrencyCode");
        currencyCode = getIntent().getStringExtra("currencyCode");
        isBussiness = getIntent().getBooleanExtra("isBussiness", false);

        tvCoinTwo.setText(currencyCode);

        rvPayType.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        coinAdapter = new PayTypeAdapter(mContext);
        rvPayType.setAdapter(coinAdapter);

        if (rvPayType.getItemDecorationCount() == 0) {
            rvPayType.addItemDecoration(new DecoRecycler(mContext, R.drawable.deco_8_trans,
                    DecoRecycler.Edge_NONE));
        }

        if (type == 1) {
            tvTitle.setText(getString(R.string.str_buy) + currencyCode);
            tvSellOrBuy.setText(getString(R.string.str_buy_info));

        } else {
            tvTitle.setText(getString(R.string.str_sell) + currencyCode);
            tvSellOrBuy.setText(getString(R.string.str_sell_info));
        }

        coinAdapter.setOnItemClickListener(new AbsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (!coinAdapter.getData().get(position).isUseable()) {
                    for (int i = 0; i < coinAdapter.getData().size(); i++) {
                        if (i == position) {
                            coinAdapter.getData().get(i).setSelected(true);
                            currentPayType = coinAdapter.getData().get(i).getAccountType();
                        } else {
                            coinAdapter.getData().get(i).setSelected(false);
                        }
                    }

                    coinAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    @OnTextChanged(value = R.id.et_money, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etMoneyChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + pm + 1;
                if (dotIndex != dotLastIndex) {
                    etMoney.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etMoney.setSelection(etMoney.length());
                } else {
                    if (text.length() > maxIndex) {
                        etMoney.setText(text.toString().substring(0, maxIndex));
                        etMoney.setSelection(etMoney.length());
                    }
                }
            }
            if (fromUser && price > 0) {
                fromUser = false;
                double m = Util.parseDouble(etMoney.getText().toString());
                etAmount.setText(AppUtil.floorRemoveZero(m / price, pa));
                fromUser = true;
            }
        } else {
            if (etAmount.length() > 0) {
                etAmount.setText(null);
            }
        }
        if (tvError.length() > 0) {
            tvError.setText(null);
        }
    }

    @OnTextChanged(value = R.id.et_amount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etAmountChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + pa + 1;
                if (dotIndex != dotLastIndex) {
                    etAmount.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etAmount.setSelection(etAmount.length());
                } else {
                    if (text.length() > maxIndex) {
                        etAmount.setText(text.toString().substring(0, maxIndex));
                        etAmount.setSelection(etAmount.length());
                    }
                }
            }
            if (fromUser && price > 0) {
                fromUser = false;
                double a = Util.parseDouble(etAmount.getText().toString());
                etMoney.setText(AppUtil.floorRemoveZero(a * price, pm));
                fromUser = true;
            }
        } else {
            if (etMoney.length() > 0) {
                etMoney.setText(null);
            }
        }
        if (tvError.length() > 0) {
            tvError.setText(null);
        }
    }

    @OnClick({R.id.tv_poConfirm, R.id.iv_repalce, R.id.iv_back})
    public void orderClick(View v) {
        switch (v.getId()) {
            case R.id.tv_poConfirm:
                if (!isCommiting) {
                    if (checkInfo()) {
                        isCommiting = true;
                        getOnceToken();
                    }
                }
                break;
            case R.id.iv_repalce:
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private boolean checkInfo() {
        double a = Util.parseDouble(etAmount.getText().toString());
        if (a > surplusQuantity) {
            tvError.setText(R.string.str_not_enough_amount);
            return false;
        }
        double m = Util.parseDouble(etMoney.getText().toString());
        if (m <= 0 || a <= 0) {
            tvError.setText(R.string.str_edit_order_hint);
            return false;
        }
        if (type == 1) {
            if (m < limitMin || m > limitMax) {
                tvError.setText(R.string.str_edit_order_hint_two);
                return false;
            }
        }
        if (type != 1) {
            if (a < limitMin || a > limitMax) {
                tvError.setText(getString(R.string.str_edit_order_hint_two));
                return false;
            }
        }
        if (currentPayType == 0) {
            CodeStrUtil.showToastHint(this, getString(R.string.str_select_one_pay));
            return false;
        }
        return true;
    }

    @Override
    protected void loadData() {
        getAdInfo();
    }

    private void getAdInfo() {
        showLoading();
        OkHttpUtil.getJsonToken(Constant.URL.adbussinessInfo, SharedUtil.getToken(mContext), this, "adId", adid + "");
    }

    /**
     * 获取onceToken
     */
    private void getOnceToken() {
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.getCoinOncetoken);
    }


    /**
     * 下单
     */
    private void order(String token) {
        String a = Util.removePointZero(etAmount.getText().toString(), false);
        String m = Util.removePointZero(etMoney.getText().toString(), false);
        OkHttpUtil.postJsonToken(Constant.URL.comfirmOrder, SharedUtil.getToken(mContext), this,
                "adId", adid + "",
                "onceToken", token,
                "payMode", currentPayType + "",
                "quantity", a,
                "totalPrice", m);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.adbussinessInfo)) {
            dismissLoading();
            C2cBuyInfoEnity enity = GsonUtil.fromJson(json, C2cBuyInfoEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                C2cBuyInfoEnity.DataBean data = enity.getData();
                setUI(data);
                if (type != 1) {
                    OkHttpUtil.getJsonToken(Constant.URL.getPayType, SharedUtil.getToken(mContext), this);
                }
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
                Util.checkLogin(mContext, enity.getCode());
            }
        } else if (url.equals(Constant.URL.getPayType)) {
            //获取所有的支付方式
            PayTypeEnity enity = GsonUtil.fromJson(json, PayTypeEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                List<PayTypeEnity.DataBean> data = enity.getData();
                List<String> payModeList = new ArrayList<>();
                String[] split = payModes.split(",");

                for (int i = 0; i < split.length; i++) {
                    payModeList.add(split[i]);
                }

                for (int i = 0; i < data.size(); i++) {
                    if (payModeList.contains(data.get(i).getAccountType() + "")) {
                        data.get(i).setUseable(false);
                    } else {
                        data.get(i).setUseable(true);
                    }
                }

                int count = 0;

                for (int i = 0; i < data.size(); i++) {
                    if (!data.get(i).isUseable()) {
                        count++;
                    }
                }
                //当支付方式中只有一个可用的支付方式时就选中
                if (count == 1) {
                    for (int i = 0; i < data.size(); i++) {
                        if (!data.get(i).isUseable()) {
                            data.get(i).setSelected(true);
                            currentPayType = data.get(i).getAccountType();
                        }
                    }
                }
                coinAdapter.setData(data);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
                Util.checkLogin(mContext, enity.getCode());
            }
        } else if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                order(onceToken);
            } else {
                isCommiting = false;
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        onceTokenEnity.getCode(), onceTokenEnity.getMsg()));
                Util.checkLogin(mContext, onceTokenEnity.getCode());
            }
        } else if (url.equals(Constant.URL.comfirmOrder)) {
            //确认下单成功
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                startActivity(new Intent(mContext, WaitPayActivity.class).putExtra("oderNo", entity.getData()).putExtra("isBussiness", isBussiness));
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
        isCommiting = false;
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    /**
     * 购买信息
     */
    private void setUI(C2cBuyInfoEnity.DataBean data) {
        if (data == null) return;
        if (!fromUser) {
            fromUser = true;
        }

        surplusQuantity = data.getSurplusQuantity();
        tvPhone.setText(RegexUtils.isMobileExact(data.getNickName()) ? Util.addStarInMiddle(data.getNickName()) : data.getNickName());
        tvPrice.setText(Util.formatTinyDecimal(data.getPrice(), false) + "CNY");
        tvRestNums.setText(Util.formatTinyDecimal(data.getSurplusQuantity(), false) + currencyCode);
        limitMin = data.getLimitMin();
        limitMax = data.getLimitMax();
        if (type == 1) {
            tvLimmitAmount.setText(Util.formatTinyDecimal(data.getLimitMin(), false) + "-" + Util.formatTinyDecimal(data.getLimitMax(), false) + " " + data.getLegalCurrencyCode());
        } else {
            tvLimmitAmount.setText(Util.formatTinyDecimal(data.getLimitMin(), false) + "-" + Util.formatTinyDecimal(data.getLimitMax(), false) + " " + data.getCurrencyCode());
        }

        price = data.getPrice();
        payModes = data.getPayModes();
        List<PayTypeEnity.DataBean> listPay = new ArrayList<>();

        if (!TextUtils.isEmpty(data.getPayModes())) {
            llyPayType.removeAllViews();
            llyPayType.setVisibility(View.VISIBLE);
            String[] payMode = data.getPayModes().split(",");
            List<String> stringPay = new ArrayList<>();
            for (int i = 0; i < payMode.length; i++) {
                stringPay.add(payMode[i]);
            }

            sortList(stringPay);
            for (int i = 0; i < stringPay.size(); i++) {
//
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(ResUtils.getDimensionPixelSize(R.dimen.x_13dp), 0, 0, 0);
                imageView.setLayoutParams(layoutParams);

                if (type == 1) {
                    PayTypeEnity.DataBean bean = new PayTypeEnity.DataBean();
                    bean.setAccountType(Integer.parseInt(stringPay.get(i)));
                    listPay.add(bean);
                }

                if (stringPay.get(i).equals("1")) {
                    imageView.setImageResource(R.mipmap.alipay);
                }

                if (stringPay.get(i).equals("2")) {
                    imageView.setImageResource(R.mipmap.wechat);
                }

                if (stringPay.get(i).equals("3")) {
                    imageView.setImageResource(R.mipmap.bank);
                }

                llyPayType.addView(imageView);
            }
            if (type == 1) {
                if (listPay.size() == 1) {
                    for (int i = 0; i < listPay.size(); i++) {
                        listPay.get(i).setSelected(true);
                        currentPayType = listPay.get(i).getAccountType();
                    }
                }


                coinAdapter.setData(listPay);
            }
        } else {
            llyPayType.setVisibility(View.GONE);
        }


        tvNote.setText(TextUtils.isEmpty(data.getRemark()) ? "---" : data.getRemark());

    }

    public class PayTypeAdapter extends AbsRecyclerAdapter<PayTypeEnity.DataBean> {


        public PayTypeAdapter(Context context) {
            super(context, R.layout.item_paytype);
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

            if (d.isUseable()) {
                tvCoin.setTextColor(ResUtils.getColor(R.color.white));
                tvCoin.setBackground(ResUtils.getDrawable(R.drawable.sp1_solid_c6c9de_2dp));
            } else {
//                tvCoin.setTextColor(ResUtils.getColor(R.color.purple77));
                tvCoin.setTextColor(ResUtils.getColor(R.color.color_1888FE));
                tvCoin.setBackground(ResUtils.getDrawable(R.drawable.slc_c2c_coin_pick_bg));
            }
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

    /**
     * 排序
     */
    private void sortList(List<String> stringPay) {
        Collections.sort(stringPay, new Comparator<String>() {
            /*
             * int compare(Person p1, Person p2) 返回一个基本类型的整型，
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(String p1, String p2) {
                //按照Person的年龄进行升序排列
                if (Integer.valueOf(p1) > Integer.valueOf(p2)) {
                    return 1;
                }
                if (Integer.valueOf(p1) == Integer.valueOf(p2)) {
                    return 0;
                }
                return -1;
            }
        });
    }
}
