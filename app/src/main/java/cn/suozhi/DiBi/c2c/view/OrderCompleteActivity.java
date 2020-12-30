package cn.suozhi.DiBi.c2c.view;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.OrderDetailEnity;
import cn.suozhi.DiBi.c2c.view.chat.P2PChatMessageActivity;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.TradeKownDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.NotifyEntity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.impl.customization.DefaultP2PSessionCustomization;

/**
 * 订单已完成 和已取消
 */
public class OrderCompleteActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    private int status;
    private String oderNo;

    @BindView(R.id.tv_pay_status)
    TextView tvStatus;

    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;

    @BindView(R.id.tv_order_number)
    TextView tvOrderNo;

    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;   //订单总额

    @BindView(R.id.tv_amount)
    TextView tvTradeAmount;   //交易数量

    @BindView(R.id.tv_trade_price)
    TextView tvPrice;   //交易单价

    @BindView(R.id.tv_pay_type)
    TextView tvPayType;   //支付方式

    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;   //订单时间
    private boolean isBussiness;
    private String accid;

    @BindView(R.id.tv_contract)
    TextView tvContract;


    private static DefaultP2PSessionCustomization commonP2PSessionCustomization;
    private String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_order_complete;
    }

    @Override
    protected void init() {
        super.init();
        lang = SharedUtil.getLanguage4Url(mContext);
        oderNo = getIntent().getStringExtra("oderNo");
        status = getIntent().getIntExtra("orderType", -1);
        isBussiness = getIntent().getBooleanExtra("isBussiness", false);
        initDefaultSessionCustomization();

        setSystemUI();
        if (status == 3) {
            tvStatus.setText(getString(R.string.str_canceled));
            tvOrderStatus.setText(getString(R.string.str_order_canceled));
        } else {
            tvStatus.setText(getString(R.string.str_completed));
            tvOrderStatus.setText(getString(R.string.str_order_completed));
        }
    }


    // 初始化会话定制，P2P、Team、ChatRoom
    private static void initDefaultSessionCustomization() {
        if (commonP2PSessionCustomization == null) {
            commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
        }

    }


    @OnClick({R.id.rly_header, R.id.tv_record, R.id.tv_contract, R.id.iv_back})
    public void orderFinishClick(View v) {
        switch (v.getId()) {
            case R.id.rly_back:
                finish();
                break;
            case R.id.tv_record:
                getKown();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_contract:
                P2PChatMessageActivity.start(mContext, accid, accountName, commonP2PSessionCustomization, null);
                break;
        }
    }

    private void getKown() {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang, "typeCode", "tipsContent");
    }

    @Override
    protected void loadData() {
        super.loadData();

        OkHttpUtil.getJsonToken(Constant.URL.orderDetail, SharedUtil.getToken(mContext), this, "orderNo", oderNo);
    }


    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.orderDetail)) {
            OrderDetailEnity enity = GsonUtil.fromJson(json, OrderDetailEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                OrderDetailEnity.DataBean data = enity.getData();
                setUI(data);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
            }
        } else if (url.equals(Constant.URL.GetNotify)) {
            NotifyEntity notify = GsonUtil.fromJson(json, NotifyEntity.class);
            if (Constant.Int.SUC == notify.getCode()) {
                String tradeKnow = notify.getData().getRecords().get(0).getContent();
                if (!TextUtils.isEmpty(tradeKnow)) {
                    TradeKownDialog.newInstance(tradeKnow).show(this);
                }
            }
        }
    }


    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(this, url, error);
    }


    private void setUI(OrderDetailEnity.DataBean data) {
        if (data == null) return;
        tvOrderNo.setText(data.getOrderNo());
        tvTotalMoney.setText(Util.formatTinyDecimal(data.getTotalPrice(), false) + "  " + data.getLegalCurrencyCode());
        tvPrice.setText(Util.formatTinyDecimal(data.getPrice(), false) + "  " + data.getLegalCurrencyCode());
        tvTradeAmount.setText(Util.formatTinyDecimal(data.getQuantity(), false) + "  " + data.getCurrencyCode());
        accountName = data.getAccountName();

        if (data.getOrderType() == 1 || data.getOrderType() == 4) {
            //当前我是买家
            isBussiness = false;
        } else {
            //当前我是商家
            isBussiness = true;
        }

        if (isBussiness) {

            tvContract.setText(getString(R.string.str_contract_buy));
        } else {
            tvContract.setText(getString(R.string.str_contract_business));
        }

        switch (data.getPayMode()) {
            case 1:
                tvPayType.setText(getString(R.string.str_alipay));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.alipay, 0, 0, 0);
                break;
            case 2:
                tvPayType.setText(getString(R.string.str_wechat));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.wechat, 0, 0, 0);
                break;
            case 3:
                tvPayType.setText(getString(R.string.str_bank));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.bank, 0, 0, 0);
                break;
        }

        tvOrderTime.setText(data.getTime());
        accid = data.getAccid();
    }


}
