package cn.suozhi.DiBi.wallet.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.QRCodeUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.wallet.adapter.ChargeCoinHintAdapter;
import cn.suozhi.DiBi.wallet.model.ChargeCoinEnity;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

public class ChargeCoinActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    private static final int REQUEST_CODE = 2;
    @BindView(R.id.rv_hint)
    RecyclerView rvHint;

    @BindView(R.id.lly_special_coin)
    RelativeLayout llySpecialCoin;

    @BindView(R.id.tv_copy_memo)
    TextView tvCopyNote;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.iv_address)
    ImageView ivQrCode;

    @BindView(R.id.tv_address)
    TextView tvAddress;

    @BindView(R.id.tv_memo)
    TextView tvMemo;

    @BindView(R.id.tv_selected_coin)
    TextView tvSlectCoin;

    @BindView(R.id.tv_rechargeType)
    public TextView tvType;
    @BindView(R.id.tv_rechargeType1)
    public TextView tvType1;

    private ChargeCoinHintAdapter chargeCoinHintAdapter;
    private String coinId;
    private String coin;
    private String currentCode;
    private String currencyId;
    private String address;
    private String memo;
    private String token;
    //提币的提示
    private List<SingleCoinInfoEnity.DataBean.DepositTipsBean> depositTips;
    private String tagDescribe;
    private Bitmap qrCode;

    @BindView(R.id.tv_chain_type_text)
    public TextView chtt;

    private int selectedUsdt = 1; // 用户选择的usdt链类型 0-omni    1-erc20
    private int usdtIdOMNI = 4;
    private int usdtIdERC20 = 18;

    @Override
    protected int getViewResId() {
        return R.layout.activity_charge_coin;
    }

    @Override
    protected void init() {
        setSystemUI();
        token = null;
        coin = getIntent().getStringExtra("result");
        currencyId = getIntent().getStringExtra("coinId"); // 接收到的coinID
        token = getIntent().getStringExtra("oToken"); // 接收到的token

        // 选中USDT时获取ERC20类型的数据
        if(coin == "USDT" || coin.equals("USDT")){
            // 进来时默认选中ERC20
            selectedUsdt = 1;
            currencyId = String.valueOf(usdtIdERC20);
            tvType.setTextColor(getResources().getColor(R.color.gy4D));
            tvType.setTextSize(13);
            tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
            tvType1.setTextSize(14);
        } else {
            currencyId = getIntent().getStringExtra("coinId");
        }

        rvHint.setLayoutManager(new LinearLayoutManager(mContext));
        chargeCoinHintAdapter = new ChargeCoinHintAdapter(mContext);
        rvHint.setAdapter(chargeCoinHintAdapter);
    }

    @Override
    protected void loadData() {
        tvSlectCoin.setText(coin);
        tvTitle.setText(coin + getString(R.string.str_charge));

        if(coin == "USDT" || coin.equals("USDT")){
            getOnceToken();
        } else {
            getChargeCoin(); // 获取充币地址
            getSingleCoinInfo(); // 获取币种信息
        }
    }

    /**
     * 显示特殊币种的标签复制
     */
    private void showSpecilaNote() {
        if (TextUtils.isEmpty(tagDescribe)) {
            llySpecialCoin.setVisibility(View.GONE);
        } else {
            llySpecialCoin.setVisibility(View.VISIBLE);
            tvCopyNote.setText(getString(R.string.str_copy) + tagDescribe);
        }
    }


    @OnClick({R.id.tv_selected_coin, R.id.tv_record, R.id.tv_save, R.id.tv_copy_address, R.id.tv_copy_memo, R.id.iv_back ,
                R.id.tv_rechargeType , R.id.tv_rechargeType1})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_selected_coin:
                Intent intent = new Intent(mContext, SelectedCoinActivity.class)
                        .putExtra("type", Constant.Strings.Intent_Back_Recharge);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_record:
                Intent intent1 = new Intent(mContext, AccountOrderActivity.class);
                intent1.putExtra("status", 1);
                startActivity(intent1);
                break;
            case R.id.tv_save:
                save();
                break;
            case R.id.tv_copy_address:
                Util.copyBoard(mContext, tvAddress.getText().toString().trim());
                ToastUtil.initToast(this, getString(R.string.str_copyed));
                break;
            case R.id.tv_copy_memo:
                Util.copyBoard(mContext, tvMemo.getText().toString().trim());
                ToastUtil.initToast(this, getString(R.string.str_copyed));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_rechargeType: // ommi
                selectedUsdt = 0;
                tvType.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType.setTextSize(14);
                tvType1.setTextColor(getResources().getColor(R.color.gy4D));
                tvType1.setTextSize(13);
                // 调用更新
                currencyId = String.valueOf(usdtIdOMNI);
                getOnceToken();
                break;
            case R.id.tv_rechargeType1: // erc20
                selectedUsdt = 1;
                tvType.setTextColor(getResources().getColor(R.color.gy4D));
                tvType.setTextSize(13);
                tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType1.setTextSize(14);
                // 调用更新
                currencyId = String.valueOf(usdtIdERC20);
                getOnceToken();
                break;
        }
    }

    /**
     * 获取单个币种信息
     */
    private void getSingleCoinInfo() {
        showHide();
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfo, SharedUtil.getToken(mContext), this, "currencyId", currencyId);
    }

    private void showHide(){
        if(coin == "USDT" || coin.equals("USDT")){
            chtt.setText(getString(R.string.chainTypeInfo));
            chtt.setVisibility(View.VISIBLE);
            tvType.setVisibility(View.VISIBLE);
            tvType1.setVisibility(View.VISIBLE);
            if (currencyId == "18" || currencyId.equals(18) || currencyId.equals("18")) {
                // 默认选择erc20
                selectedUsdt = 1;
                tvType.setText("OMNI");
                tvType1.setText("ERC20");
                tvType.setTextColor(getResources().getColor(R.color.gy4D));
                tvType.setTextSize(13);
                tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType1.setTextSize(14);
            } else if (currencyId == "4" || currencyId.equals(4) || currencyId.equals("4")){
                // ommi
                selectedUsdt = 0;
                tvType.setText("OMNI");
                tvType1.setText("ERC20");
                tvType.setTextColor(getResources().getColor(R.color.color_1888FE));
                tvType.setTextSize(14);
                tvType1.setTextColor(getResources().getColor(R.color.gy4D));
                tvType1.setTextSize(13);
            }
        } else {
            chtt.setVisibility(View.GONE);
            tvType.setVisibility(View.GONE);
            tvType1.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e(TAG, "Recharge: "+url + json);
        // 获取充币二维码
        if (url.equals(Constant.URL.chargeCoin)) {
            ChargeCoinEnity chargeCoinEnity = GsonUtil.fromJson(json, ChargeCoinEnity.class);
            if (chargeCoinEnity.getCode() == Constant.Int.SUC) {
                setUI(chargeCoinEnity.getData());
            } else {
                tagDescribe = "";
                tvAddress.setText("");
                tvMemo.setText("");
                ivQrCode.setImageResource(R.color.white);
                showSpecilaNote();
                ToastUtil.initToast(this, Util.getCodeText(this,
                        chargeCoinEnity.getCode(), chargeCoinEnity.getMsg()));
                Util.checkLogin(this, chargeCoinEnity.getCode());

            }
        }
        else if (url.equals(Constant.URL.singleCoinInfo)) {
            SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
            if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {
                upDateUi(singleCoinInfoEnity.getData());
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                Util.checkLogin(this, singleCoinInfoEnity.getCode());
            }
        }
        // 此处只针对两种不同链类型的USDT判断进行特殊请求
        else if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                // 获取充币地址
                OkHttpUtil.getJsonToken(Constant.URL.chargeCoin, SharedUtil.getToken(mContext), this, "currencyId", currencyId , "onceToken", onceToken);
                getSingleCoinInfo();
            } else {
                ToastUtil.initToast(ChargeCoinActivity.this,onceTokenEnity.getMsg());
            }
        }
    }

    private void upDateUi(SingleCoinInfoEnity.DataBean data) {
        if (data == null) return;
        depositTips = data.getDepositTips();
        chargeCoinHintAdapter.setData(depositTips);
    }

    private void setUI(ChargeCoinEnity.DataBean data) {
        if (data == null) return;
        tagDescribe = data.getTagDescribe();

        showSpecilaNote();
        address = data.getAddress();
        memo = data.getTag();
        tvAddress.setText(data.getAddress());
        tvMemo.setText(data.getTag());

        ivQrCode.post(() -> {
            qrCode = QRCodeUtil.syncEncodeQRCode(data.getAddress(), Util.getPhoneWidth(this) / 3);
            ivQrCode.setImageBitmap(qrCode);
        });
    }

    @Override
    public void onFailure(String url, String error) {
        if (url.equals(Constant.URL.chargeCoin)){
            tagDescribe = "";
            tvAddress.setText("");
            tvMemo.setText("");
            ivQrCode.setImageResource(R.color.white);
            showSpecilaNote();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果结果码等于RESULT_OK
            if (requestCode == REQUEST_CODE) {
                token = null;
                coinId = data.getExtras().getString("coinId");
                coin = data.getExtras().getString("result");
                token = data.getExtras().getString("oToken"); // 接收到的token

                tvSlectCoin.setText(coin);
                tvTitle.setText(coin + getString(R.string.str_charge));


                tagDescribe = "";
                tvAddress.setText("");
                tvMemo.setText("");
                ivQrCode.setImageResource(R.color.white);

                // 选择USDT时获取ERC20类型的数据
                if(coin == "USDT" || coin.equals("USDT")){
                    // 进来时默认选中ERC20
                    selectedUsdt = 1;
                    currencyId = String.valueOf(usdtIdERC20);
                    tvType.setTextColor(getResources().getColor(R.color.gy4D));
                    tvType.setTextSize(13);
                    tvType1.setTextColor(getResources().getColor(R.color.color_1888FE));
                    tvType1.setTextSize(14);
                    getOnceToken();
                } else {
                    currencyId = coinId;
                    getChargeCoin();
                    getSingleCoinInfo();
                }
            }
        }
    }

    /**
     * 保存二维码到手机
     */
    private void save() {
        if (qrCode == null) {
            return;
        }
        try {
            String filePath = Util.getDCIMPath() + "/recharge" + ".jpg";
            if (BitMapUtil.saveBitmap2File(qrCode, filePath)) {
                BitMapUtil.insertGallery(this, filePath);
                ToastUtil.initToast(this, R.string.saveSuc);
            } else {
                ToastUtil.initToast(this, R.string.saveFail);
            }
        } catch (Exception e) {}
    }

    // 获取充币地址
    private void getChargeCoin(){
        OkHttpUtil.getJsonToken(Constant.URL.chargeCoin, SharedUtil.getToken(mContext), this, "currencyId", currencyId , "onceToken", token);
    }

    // 请求onceToken 只针对不同链类型的USDT
    private void getOnceToken(){
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.oncetoken);
    }

}
