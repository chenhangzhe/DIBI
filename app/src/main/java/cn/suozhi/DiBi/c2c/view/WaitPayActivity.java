package cn.suozhi.DiBi.c2c.view;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.impl.customization.DefaultP2PSessionCustomization;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.OrderDetailEnity;
import cn.suozhi.DiBi.c2c.view.chat.P2PChatMessageActivity;
import cn.suozhi.DiBi.c2c.widget.TimerTextView;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.dialog.GoogleVerifyDialog;
import cn.suozhi.DiBi.common.dialog.LookPhotoDialog;
import cn.suozhi.DiBi.common.dialog.TradeKownDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.CancelOrderNumsEntity;
import cn.suozhi.DiBi.home.model.NotifyEntity;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;

/**
 * 待支付界面
 */
public class WaitPayActivity extends BaseActivity implements OkHttpUtil.OnDataListener, BaseDialog.OnItemClickListener {

    private String oderNo;

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

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_bank)
    TextView tvBank;

    @BindView(R.id.tv_branch_bank)
    TextView tvBranchBank;

    @BindView(R.id.tv_countdown)
    TimerTextView tvCountdown;

    @BindView(R.id.tv_bank_number)
    TextView tvBankNumber;//卡号


    @BindView(R.id.tv_contract)
    TextView tvContract;

    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_collect_account)
    TextView tvAcountTitle;//卡号 账号

    @BindView(R.id.lly_alipay_wechat)
    View rlyPayType;//支付方式

    @BindView(R.id.rly_branch)
    View rlyBranch;//支付方式

    @BindView(R.id.rly_bank)
    View rlyBank;//支付方式

    @BindView(R.id.lly_btn)
    View llyBtn;//支付方式

    @BindView((R.id.tv_wpTips))
    public TextView tvTips;

    private boolean isBussiness;
    private boolean isCommiting;
    private boolean isCancelOrder;
    private int clickStatus;
    //商家名称
    private String accountName;
    private String qrCode;
    //交易对象的云信id
    private String accid;
    private static DefaultP2PSessionCustomization commonP2PSessionCustomization;
    private int cancleNums;

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
    protected int getViewResId() {
        setSystemUI();
        return R.layout.activity_wait_pay;
    }

    @Override
    protected void init() {
        lang = SharedUtil.getLanguage4Url(mContext);
        initDefaultSessionCustomization();
        oderNo = getIntent().getStringExtra("oderNo");

        isBussiness = getIntent().getBooleanExtra("isBussiness", false);

    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJsonToken(Constant.URL.orderDetail, SharedUtil.getToken(mContext), this, "orderNo", oderNo);
        getInfo();
        cancelOrderNums();
    }


    /**
     * 获取用户
     */
    private void getInfo() {
        OkHttpUtil.getJsonHeader2(Constant.URL.GetInfo, SharedUtil.getToken(this),
                SharedUtil.getLanguage4Url(this), this);
    }

    /**
     * 获取onceToken
     */
    private void getOnceToken(String getCancelOrderOncetoken) {
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", getCancelOrderOncetoken);
    }

    /**
     * 取消订单
     */
    private void cancelOrder(String onceToken) {
        OkHttpUtil.putJsonToken(Constant.URL.cancelOrder, SharedUtil.getToken(mContext), this, "onceToken", onceToken, "orderNo", oderNo);
    }

    /**
     * 取消订单数量
     */
    private void cancelOrderNums() {
        OkHttpUtil.getJsonToken(Constant.URL.CancelLimit, SharedUtil.getToken(mContext), this);
    }

    /**
     * 取消订单
     */
    private void comfirPayOrder(String onceToken) {
        OkHttpUtil.putJsonToken(Constant.URL.comfirmPay, SharedUtil.getToken(mContext), this, "onceToken", onceToken, "orderNo", oderNo);
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
        } else if (url.equals(Constant.URL.onceToken)) {
            isCommiting = false;
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                if (clickStatus == 1) {
                    cancelOrder(onceToken);
                } else {
                    comfirPayOrder(onceToken);
                }

            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        onceTokenEnity.getCode(), onceTokenEnity.getMsg()));
                Util.checkLogin(mContext, onceTokenEnity.getCode());
            }
        } else if (url.equals(Constant.URL.cancelOrder)) {
            //取消订单
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                startActivity(new Intent(mContext, OrderCompleteActivity.class).putExtra("oderNo", oderNo).putExtra("orderType", 3).putExtra("isBussiness", isBussiness));
                finish();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(mContext, entity.getCode());
            }
        } else if (url.equals(Constant.URL.comfirmPay)) {
            //确认支付
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                startActivity(new Intent(mContext, WaitComfirmActivity.class).putExtra("oderNo", oderNo).putExtra("isBussiness", isBussiness));
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
        } else if (url.equals(Constant.URL.CancelLimit)) {
            CancelOrderNumsEntity entity = GsonUtil.fromJson(json, CancelOrderNumsEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                //取消订单的数量
                cancleNums = entity.getData();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(mContext, entity.getCode());
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
                    ToastUtil.initToast(WaitPayActivity.this, Util.getCodeText(mContext,
                            userEntity.getCode(), userEntity.getMsg()));
                    Util.checkLogin(mContext, userEntity.getCode());
                }
            } else {
                ToastUtil.initToast(this, Util.getCodeText(mContext,
                        userEntity.getCode(), userEntity.getMsg()));
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {
        isCommiting = false;
        Util.saveLog(this, url, error);
    }

    private void setUI(OrderDetailEnity.DataBean d) {
        if (d == null) {
            return;
        }
        tvOrderNo.setText(d.getOrderNo());
        tvTotalMoney.setText(Util.formatTinyDecimal(d.getTotalPrice(), false) + "  " + d.getLegalCurrencyCode());
        tvPrice.setText(Util.formatTinyDecimal(d.getPrice(), false) + "  " + d.getLegalCurrencyCode());
        tvTradeAmount.setText(Util.formatTinyDecimal(d.getQuantity(), false) + "  " + d.getCurrencyCode());

        accountName = d.getAccountName();
        tvOrderTime.setText(d.getTime());

        tvName.setText(d.getAccountName());

        switch (d.getPayMode()) {
            case 1:
                tvPayType.setText(getString(R.string.str_alipay));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.alipay, 0, 0, 0);
                rlyBank.setVisibility(View.GONE);
                rlyBranch.setVisibility(View.GONE);
                rlyPayType.setVisibility(View.VISIBLE);
                tvAcountTitle.setText(getString(R.string.str_c2c_account_number));

                break;
            case 2:
                tvPayType.setText(getString(R.string.str_wechat));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.wechat, 0, 0, 0);
                rlyBank.setVisibility(View.GONE);
                rlyBranch.setVisibility(View.GONE);
                rlyPayType.setVisibility(View.VISIBLE);
                tvAcountTitle.setText(getString(R.string.str_c2c_account_number));
                break;
            case 3:
                tvPayType.setText(getString(R.string.str_bank));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.bank, 0, 0, 0);
                tvBank.setText(d.getBank());
                tvBranchBank.setText(d.getBranchBank());
                rlyBank.setVisibility(View.VISIBLE);
                rlyBranch.setVisibility(View.VISIBLE);
                rlyPayType.setVisibility(View.GONE);
                tvAcountTitle.setText(getString(R.string.str_c2c_bank_number));

                tvBank.setText(d.getBank());
                tvBranchBank.setText(d.getBranchBank());
                break;
        }

        tvBankNumber.setText(d.getAccountNumber());
        if (d.getOrderType() == 1 || d.getOrderType() == 4) {
            //当前我是买家
            isBussiness = false;
        } else {
            //当前我是商家
            isBussiness = true;
        }
        qrCode = d.getQrCode();
        accid = d.getAccid();

        tvCountdown.setLastMillis(System.currentTimeMillis() + Math.abs(d.getCountdown()) * 1000);
        boolean isEn = "en".equals(SharedUtil.getLanguage(this));
        tvCountdown.setTextSize(isEn ? 11 : 12);
        if (isBussiness) {
            String pay = Util.formatTinyDecimal(d.getTotalPrice(), false) + d.getLegalCurrencyCode();
            tvCountdown.setContentBeforAfter(String.format(getString(R.string.waitPayCountSellHead), isEn ? pay : ""),
                    String.format(getString(R.string.waitPayCountSellFoot), isEn ? "" : pay));
            tvTips.setVisibility(View.GONE);
        } else {
            String pay = Util.formatTinyDecimal(d.getTotalPrice(), false) + d.getLegalCurrencyCode();
            tvCountdown.setContentBeforAfter(String.format(getString(R.string.waitPayCountBuyHead), isEn ? pay : ""),
                    String.format(getString(R.string.waitPayCountBuyFoot), isEn ? "" : pay));
            tvTips.setVisibility(View.VISIBLE);
            tvTips.setTextSize(isEn ? 11 : 12);
            tvTips.setText(Util.fromHtml(getString(R.string.waitPayBuyTips)));
        }
        tvCountdown.start();

        if (isBussiness) {
            llyBtn.setVisibility(View.GONE);
            tvInfo.setText(getString(R.string.str_buy_collect_info));
            tvContract.setText(getString(R.string.str_contract_buy));

        } else {

            llyBtn.setVisibility(View.VISIBLE);
            tvInfo.setText(getString(R.string.str_sell_collect_info));
            tvContract.setText(getString(R.string.str_contract_business));
        }
    }

    // 初始化会话定制，P2P、Team、ChatRoom
    private static void initDefaultSessionCustomization() {
        if (commonP2PSessionCustomization == null) {
            commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
        }

    }


    private void getKown() {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang, "typeCode", "tipsContent");
    }

    @OnClick({R.id.tv_cancel, R.id.tv_pay, R.id.tv_contract, R.id.iv_back, R.id.tv_record, R.id.tv_look_code,
            R.id.tv_name, R.id.tv_bank_number})
    public void waitPayClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                clickStatus = 1;
                if (!isCommiting) {
                    if (cancleNums >= 2) {
                        AuthDialog.newInstance(String.format(ResUtils.getString(mContext, R.string.str_cancel_order_hint, cancleNums)),
                                getString(R.string.str_comfir_cancel_order),
                                ResUtils.getString(mContext, R.string.thinkAgain)).setOnItemClickListener(this)
                                .show(this);
                    } else {
                        getOnceToken(Constant.Strings.getCancelOrderOncetoken);
                    }
                }
                break;
            case R.id.tv_pay: // 用户点击确认转款按钮，弹出确认框 -> 此处改成弹出验证码
                clickStatus = 2;
                if (!isCommiting) {
                    showDialog(getString(R.string.str_comfir_pay_money));
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_record:
                getKown();
                break;
            case R.id.tv_name:
                Util.copyBoard(mContext, tvName.getText().toString().trim());
                ToastUtil.initToast(this,getString(R.string.str_copyed));
                break;
            case R.id.tv_bank_number:
                Util.copyBoard(mContext, tvBankNumber.getText().toString().trim());
                ToastUtil.initToast(this,getString(R.string.str_copyed));
                break;

            case R.id.tv_look_code:
                LookPhotoDialog.newInstance(qrCode).show(this);
                break;
            case R.id.tv_contract:
//                SessionHelper.startP2PSession(mContext, accid);
                P2PChatMessageActivity.start(mContext, accid == null ? "1" : accid, accountName, commonP2PSessionCustomization, null);
                break;
        }
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

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm) {
            isCommiting = true;
            getOnceToken(Constant.Strings.getComfirmPayOncetoken);
        } else if (v.getId() == R.id.tv_auth) {
            isCommiting = true;
            getOnceToken(Constant.Strings.getCancelOrderOncetoken);
        }
    }

}
