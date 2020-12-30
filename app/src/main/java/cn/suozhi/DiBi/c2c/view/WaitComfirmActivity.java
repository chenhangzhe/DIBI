package cn.suozhi.DiBi.c2c.view;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.OrderDetailEnity;
import cn.suozhi.DiBi.c2c.view.chat.P2PChatMessageActivity;
import cn.suozhi.DiBi.c2c.widget.TimerTextView;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.dialog.GoogleVerifyDialog;
import cn.suozhi.DiBi.common.dialog.TradeKownDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.NotifyEntity;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.impl.customization.DefaultP2PSessionCustomization;

/**
 * 待确认
 */
public class WaitComfirmActivity extends BaseActivity implements BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {

    private String oderNo;

    @BindView(R.id.tv_order_number)
    TextView tvOrderNo;

    @BindView(R.id.tv_countdown)
    TimerTextView tvCountDown;
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

    @BindView(R.id.lly_bussiness_comfirm)
    LinearLayout llyBussiness;

    @BindView(R.id.lly_buyer_comfirm)
    LinearLayout llyBuyer;

    @BindView(R.id.tv_contract)
    TextView tvContract;

    @BindView(R.id.tv_compalint)
    TextView tvCompalint;

    @BindView(R.id.tv_apply_compalint)
    TextView tvBussinessCompalint;


    private boolean isBussiness;
    private boolean isCommiting;
    private OrderDetailEnity.DataBean data;

    private static DefaultP2PSessionCustomization commonP2PSessionCustomization;
    private String accid;
    private String accountName;

    private UserEntity.DataEntity.InfoEntity userData;

    private boolean isBindPhone;
    private boolean isBindEmail;
    private boolean isBindGoogle;
    private int verifiedLevel;
    private int verifiedstatus;
    private int idType;

    private String verifyCode;

    private int gs;
    private String area;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_wait_comfirm;
    }

    @Override
    protected void init() {
        super.init();
        lang = SharedUtil.getLanguage4Url(mContext);
        setSystemUI();
        initDefaultSessionCustomization();
        oderNo = getIntent().getStringExtra("oderNo");
        isBussiness = getIntent().getBooleanExtra("isBussiness", false);


    }

    // 初始化会话定制，P2P、Team、ChatRoom
    private static void initDefaultSessionCustomization() {
        if (commonP2PSessionCustomization == null) {
            commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
        }

    }


    @Override
    protected void loadData() {
        super.loadData();
        getInfo();
        OkHttpUtil.getJsonToken(Constant.URL.orderDetail, SharedUtil.getToken(mContext), this, "orderNo", oderNo);
    }


    /**
     * 获取onceToken
     *
     * @param getCancelOrderOncetoken
     */
    private void getOnceToken(String getCancelOrderOncetoken) {
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", getCancelOrderOncetoken);
    }

    /**
     * 确认收款
     *
     * @param onceToken
     */
    private void comfirCollectOrder(String onceToken) {
        OkHttpUtil.putJsonToken(Constant.URL.comfirmCollect, SharedUtil.getToken(mContext), this, "onceToken", onceToken, "orderNo", oderNo);
    }


    /**
     * 买方确认转款和卖方 确认收款
     */
    public void showDialog(String contetn) {
        ConfirmDialog.newInstance(contetn, getString(R.string.cancel),
                getString(R.string.confirm_two))
                .setOnItemClickListener(this)
                .show(this);
    }

    /**
     * 获取用户
     */
    private void getInfo() {
        OkHttpUtil.getJsonHeader2(Constant.URL.GetInfo, SharedUtil.getToken(this),
                SharedUtil.getLanguage4Url(this), this);
    }

    /**
     * 确认收款
     */
    private void orderReceive(String onceToken,String verifyCode){
        OkHttpUtil.putJsonToken(Constant.URL.orderReceive, SharedUtil.getToken(mContext), this, "onceToken", onceToken, "orderNo", oderNo,"verifyCode",verifyCode);
    }

    /**
     * 弹框的点击事件
     *
     * @param v
     */
    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm) {
            isCommiting = true;
            getOnceToken(Constant.Strings.getComfirmColletOncetoken);
        } else if (v.getId() == R.id.et_dgCvCode || v.getId() == R.id.et_dgGvCode) {
            showLoading();
            verifyCode = (String) v.getTag();
            getOnceToken(Constant.Strings.getCancelOrderOncetoken);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.orderDetail)) {
            OrderDetailEnity enity = GsonUtil.fromJson(json, OrderDetailEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                data = enity.getData();
                setUI(data);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        enity.getCode(), enity.getMsg()));
            }
        } else if (url.equals(Constant.URL.onceToken)) {
            isCommiting = false;
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
//                comfirCollectOrder(onceToken);
                orderReceive(onceToken,verifyCode);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        onceTokenEnity.getCode(), onceTokenEnity.getMsg()));
                Util.checkLogin(mContext, onceTokenEnity.getCode());
            }
        } else if (url.equals(Constant.URL.comfirmCollect)) {
            //确认收款
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                startActivity(new Intent(mContext, OrderCompleteActivity.class).putExtra("oderNo", oderNo).putExtra("orderType", 5).putExtra("isBussiness", isBussiness));
                finish();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(mContext, entity.getCode());
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
        else if (url.equals(Constant.URL.GetInfo)){
            UserEntity userEntity = GsonUtil.fromJson(json , UserEntity.class);
            if (userEntity.getCode() == Constant.Int.SUC){
                if (Constant.Int.SUC == userEntity.getCode()) {
                    userData = userEntity.getData().getInfo();
                    if (userData == null) {
                        return;
                    }
                    isBindPhone = userData.getPhoneEnabled() == 1;
                    isBindEmail = userData.getEmailEnabled() == 1;
                    isBindGoogle = userData.getGaEnabled() == 1;
                    verifiedLevel = userData.getVerifiedLevel();
                    verifiedstatus = userData.getVerifiedStatus();
                    idType = userData.getIdType();
                } else {
                    ToastUtil.initToast(WaitComfirmActivity.this, Util.getCodeText(mContext,
                            userEntity.getCode(), userEntity.getMsg()));
                    Util.checkLogin(mContext, userEntity.getCode());
                }
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        userEntity.getCode(), userEntity.getMsg()));
            }
        }
        else if(url.equals(Constant.URL.orderReceive)){
            //确认收款
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                startActivity(new Intent(mContext, OrderCompleteActivity.class).putExtra("oderNo", oderNo).putExtra("orderType", 5).putExtra("isBussiness", isBussiness));
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
        Util.saveLog(this, url, error);
    }


    private void setUI(OrderDetailEnity.DataBean data) {
        if (data == null) return;
        tvOrderNo.setText(data.getOrderNo());
        tvTotalMoney.setText(Util.formatTinyDecimal(data.getTotalPrice(), false) + "  " + data.getLegalCurrencyCode());
        tvPrice.setText(Util.formatTinyDecimal(data.getPrice(), false) + "  " + data.getLegalCurrencyCode());
        tvTradeAmount.setText(Util.formatTinyDecimal(data.getQuantity(), false) + "  " + data.getCurrencyCode());

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
        //当前时间
        long currentTimeMillis = System.currentTimeMillis();
        //当前订单生成时间
        long orderTime = Util.parseTime(data.getTime(), Util.sdf_y2s);

        //超过十五分钟才能 提起申诉
        if (currentTimeMillis - orderTime > 15 * 60 * 1000) {
            tvCompalint.setTextColor(ResUtils.getColor(R.color.white));
            tvCompalint.setEnabled(true);

            tvBussinessCompalint.setTextColor(ResUtils.getColor(R.color.white));
            tvBussinessCompalint.setEnabled(true);
        } else {
            tvCompalint.setTextColor(ResUtils.getColor(R.color.white));
            tvCompalint.setEnabled(true);

            tvBussinessCompalint.setTextColor(ResUtils.getColor(R.color.white));
            tvBussinessCompalint.setEnabled(true);
        }
        tvOrderTime.setText(data.getTime());
        accid = data.getAccid();

        if (data.getOrderType() == 1 || data.getOrderType() == 4) {
            // 当前我是买家
            isBussiness = false;
        } else {
            // 当前我是商家
            isBussiness = true;
        }
        accountName = data.getAccountName();
        if (isBussiness) {
            tvCountDown.setLastMillis(System.currentTimeMillis() + Math.abs(data.getCountdown()) * 1000);
           if (SharedUtil.getLanguage(mContext).equals("en")){
               tvCountDown.setContentBeforAfter("<font color='#FFFFFF'>"+getString(R.string.str_comfirm_hint_three)+"</font>", "");
               tvCountDown.start();
           } else {
               tvCountDown.setContentBeforAfter("<font color='#FFFFFF'>"+getString(R.string.str_comfirm_hint)+"</font>", "<font color='#FFFFFF'>"+getString(R.string.str_comfirm_hint_two)+"</font>");
               tvCountDown.start();
           }
        } else {
            tvCountDown.setText(getString(R.string.str_buy_comfirm_info));
        }

        if (isBussiness) {
            llyBussiness.setVisibility(View.VISIBLE);
            llyBuyer.setVisibility(View.GONE);
            tvContract.setText(getString(R.string.str_contract_buy));
        } else {
            llyBuyer.setVisibility(View.VISIBLE);
            llyBussiness.setVisibility(View.GONE);
            tvContract.setText(getString(R.string.str_contract_business));
        }


    }

    private void getKown() {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang, "typeCode", "tipsContent");
    }

    @OnClick({R.id.tv_apply_compalint, R.id.tv_comfirm_collect, R.id.tv_compalint, R.id.tv_contract, R.id.iv_back, R.id.tv_record})
    public void waitPayClick(View v) {
        switch (v.getId()) {
            case R.id.tv_compalint:
                startActivity(new Intent(mContext, ComplaintActivity.class).putExtra("oderNo", oderNo).putExtra("oderEnity", data));
                finish();
                break;
            case R.id.tv_apply_compalint:
                startActivity(new Intent(mContext, ComplaintActivity.class).putExtra("oderNo", oderNo).putExtra("oderEnity", data));
                finish();
                break;
            case R.id.tv_comfirm_collect:
                if (!isCommiting) {
//                    showDialog(getString(R.string.str_comfir_collect_money));
                    isCommiting = true;
                    int gs = getStatus();
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
                }
                break;
            case R.id.tv_contract:
                P2PChatMessageActivity.start(mContext, accid, accountName, commonP2PSessionCustomization, null);
                break;

            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_record:
                getKown();
                break;
        }
    }

    /**
     * 获取绑定状态
     */
    private int getStatus() {
        int gs = AppUtil.getBindState(userData.getGaEnabled(), userData.getPhoneEnabled(), userData.getEmailEnabled());
        switch (gs) {
            case 1:
                area = userData.getPhoneArea();
                account = userData.getCellPhone();
                break;
            case 2:
                area = "";
                account = userData.getEmail();
                break;
            default:
                area = "";
                account = "";
                break;
        }
        return gs;
    }

}
