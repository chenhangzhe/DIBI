package cn.suozhi.DiBi.common.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.C2cCoinEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.DecoRecycler;

/**
 * 法币筛选的弹框
 */
public class C2cPickDialog extends BaseDialog implements View.OnClickListener {

    private List<C2cCoinEnity> coins;
    private int payType;
    private OnPostCoinAndPayLinsenter linsenter;
    private String currentCoin;
    private TextView tvAll;
    private TextView wechat;
    private TextView bank;
    private TextView alipay;
    private String fabi = "CNY";
    private String payMode;
    private String baseBi = "DIC";

    public static C2cPickDialog newInstance(String legalCurrencyCode, String payMode) {
        C2cPickDialog dialog = new C2cPickDialog();
        Bundle bundle = new Bundle();
        bundle.putString("legalCurrencyCode", legalCurrencyCode);
        bundle.putString("payMode", payMode);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();

//        fabi = getArguments().getString("legalCurrencyCode");
        baseBi = getArguments().getString("legalCurrencyCode");
        payMode = getArguments().getString("payMode");
        View v = inflater.inflate(R.layout.dialog_c2c_pick, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画

        coins = new ArrayList<>();
//        coins.add(new C2cCoinEnity(getString(R.string.str_all), TextUtils.isEmpty(fabi) ? true : false));

        if (TextUtils.isEmpty(baseBi)) {
            coins.add(new C2cCoinEnity("DIC", true));
            coins.add(new C2cCoinEnity("USDT", false));
        } else {
            coins.add(new C2cCoinEnity("DIC", baseBi.equals("DIC") ? true : false));
            coins.add(new C2cCoinEnity("USDT", baseBi.equals("USDT") ? true : false));
        }

        currentCoin = baseBi;
        payType = TextUtils.isEmpty(payMode) ? Integer.parseInt("0") : Integer.parseInt(payMode);

        TextView tvComfirm = v.findViewById(R.id.tv_cpmfirm);
        tvAll = v.findViewById(R.id.tv_all);
        wechat = v.findViewById(R.id.tv_wechat);
        bank = v.findViewById(R.id.tv_bank);
        alipay = v.findViewById(R.id.tv_alipay);

        if (TextUtils.isEmpty(payMode) || payMode.equals("0")) {
            setTabSelected(true, false, false, false);
        } else if (payMode.equals("1")) {
            setTabSelected(false, false, false, true);
        } else if (payMode.equals("2")) {
            setTabSelected(false, true, false, false);
        } else if (payMode.equals("3")) {
            setTabSelected(false, false, true, false);
        }


        RecyclerView gvCoin = v.findViewById(R.id.gv_coin);
        gvCoin.setLayoutManager(new GridLayoutManager(getContext(), 4));
        CoinAdapter coinAdapter = new CoinAdapter(getContext());
        gvCoin.setAdapter(coinAdapter);
        coinAdapter.setData(coins);

        if (gvCoin.getItemDecorationCount() == 0) {
            gvCoin.addItemDecoration(new DecoRecycler(getContext(), R.drawable.deco_8_trans,
                    DecoRecycler.Edge_NONE));
        }

        tvComfirm.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        wechat.setOnClickListener(this);
        bank.setOnClickListener(this);
        alipay.setOnClickListener(this);
        coinAdapter.setOnItemClickListener(new AbsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                List<C2cCoinEnity> data = coinAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (i == position) {
                        data.get(i).setSelected(true);
                    } else {
                        data.get(i).setSelected(false);
                    }
                }
                currentCoin = data.get(position).getName().equals(getString(R.string.str_all)) ? "" : data.get(position).getName();
                coinAdapter.notifyDataSetChanged();
            }
        });
//        Bundle arg = getArguments();
//        isEmail = arg.getBoolean("isEmail", true);
//        area = arg.getString("area");
//        account = arg.getString("account");


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cpmfirm:
                postData();
                dismiss();
                break;
            case R.id.tv_all:
                payType = 0;
                setTabSelected(true, false, false, false);
                break;
            case R.id.tv_wechat:
                payType = 2;
                setTabSelected(false, true, false, false);
                break;
            case R.id.tv_bank:
                payType = 3;
                setTabSelected(false, false, true, false);
                break;
            case R.id.tv_alipay:
                payType = 1;
                setTabSelected(false, false, false, true);
                break;
        }
    }

    private void setTabSelected(boolean isAll, boolean isWechat, boolean isBank, boolean isAlipay) {
        tvAll.setSelected(isAll);
        wechat.setSelected(isWechat);
        bank.setSelected(isBank);
        alipay.setSelected(isAlipay);
    }

    /**
     * 发送数据
     */
    private void postData() {
        if (linsenter != null) {
            linsenter.onClickComfirm(currentCoin, payType);
        }
    }

    public class CoinAdapter extends AbsRecyclerAdapter<C2cCoinEnity> {


        public CoinAdapter(Context context) {
            super(context, R.layout.item_coin);
        }

        @Override
        public void onBindHolder(RecyclerHolder holder, C2cCoinEnity d, int position) {
            holder.bindTextView(R.id.tv_coin, d.getName());
            TextView tvCoin = (TextView) holder.getView(R.id.tv_coin);
            tvCoin.setSelected(d.isSelected());
        }
    }

    public interface OnPostCoinAndPayLinsenter {
        void onClickComfirm(String currentCoin, int payType);
    }


    public void setOnPostCoinAndPayLinsenter(OnPostCoinAndPayLinsenter linsenter) {
        this.linsenter = linsenter;
    }


}
