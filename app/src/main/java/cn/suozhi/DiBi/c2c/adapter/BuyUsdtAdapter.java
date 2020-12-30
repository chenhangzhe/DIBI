package cn.suozhi.DiBi.c2c.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.BuyAndSellUsdtEntity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.RegexUtils;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 首页钱包的适配器
 */
public class BuyUsdtAdapter extends AbsRecyclerAdapter<BuyAndSellUsdtEntity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private int type;
    private OnCallbackListener callbackListener;

    public BuyUsdtAdapter(Context context, int height, int type, OnCallbackListener callbackListener) {
        super(context, R.layout.item_buy_usdt, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.type = type;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public int getItemType(BuyAndSellUsdtEntity.DataBean.RecordsBean d) {
        return d.getLoadType();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, BuyAndSellUsdtEntity.DataBean.RecordsBean data, int position) {

        switch (getItemType(data)) {
            case 0:
                holder.bindTextView(R.id.tv_buy, type == 1 ? getString(R.string.str_buy) : getString(R.string.str_sell));
                holder.bindTextView(R.id.tv_phone, RegexUtils.isMobileExact(data.getNickName())||RegexUtils.isEmail(data.getNickName()) ? Util.addStarInMiddle(data.getNickName()) : data.getNickName());
                if (type == 1) {
//                    holder.bindTextView(R.id.tv_nums, getString(R.string.str_amount) + " " + Util.formatTinyDecimal(data.getTotalQuantity(), false) + " " + data.getCurrencyCode());
                    holder.bindTextView(R.id.tv_nums, getString(R.string.str_amount) + " " + Util.formatTinyDecimal(data.getSurplusQuantity(), false) + " " + data.getCurrencyCode());
                    holder.bindTextView(R.id.tv_amount_limit, getString(R.string.str_c2c_limit_amount_two) + " " + Util.formatTinyDecimal(data.getLimitMin(), false) + "-" + Util.formatTinyDecimal(data.getLimitMax(), false) + " " + data.getLegalCurrencyCode());
                } else {
                    holder.bindTextView(R.id.tv_nums, getString(R.string.str_amount) + " " + Util.formatTinyDecimal(data.getSurplusQuantity(), false) + " " + data.getCurrencyCode());
//                    holder.bindTextView(R.id.tv_nums, getString(R.string.str_amount) + " " + Util.formatTinyDecimal(data.getSurplusQuantity(), false) + " " + data.getCurrencyCode());
                    holder.bindTextView(R.id.tv_amount_limit, getString(R.string.str_c2c_limit_amount_two) + " " + Util.formatTinyDecimal(data.getLimitMin(), false) + "-" + Util.formatTinyDecimal(data.getLimitMax(), false) + " " + data.getCurrencyCode());
                }

//                new BigDecimal(data.getPrice()).setScale(3, BigDecimal.ROUND_HALF_UP) // 保留小数点后3位，不足部分强制补0
                if (data.getLegalCurrencyCode().equals("USD")) {
//                    holder.bindTextView(R.id.tv_price, "$ " + Util.formatTinyDecimal(data.getPrice(), false));
                    holder.bindTextView(R.id.tv_price, "$ " + new DecimalFormat("##.###").format(data.getPrice()));
                } else {
//                    holder.bindTextView(R.id.tv_price, "¥ " + Util.formatTinyDecimal(data.getPrice(), false));
                    holder.bindTextView(R.id.tv_price, "¥ " + new DecimalFormat("##.###").format(data.getPrice()));
                }
//                holder.bindTextView(R.id.tv_suc_nums, getString(R.string.str_suc_create_nums) + " " + data.getTransOrder());

                holder.bindTextView(R.id.tv_use_time, String.format(getString(R.string.str_suc_use_time), data.getAvgDealTime()));

                LinearLayout llyPayType = (LinearLayout) holder.getView(R.id.lly_payType);


                if (!TextUtils.isEmpty(data.getPayModes())) {
                    llyPayType.removeAllViews();
                    llyPayType.setVisibility(View.VISIBLE);
                    String[] payMode = data.getPayModes().split(",");
                    List<String> stringPay = new ArrayList<>();
                    for (int i = 0; i < payMode.length; i++) {
                        stringPay.add(payMode[i]);
                    }

                    sortList(stringPay);

                    for (int i = 0; i <stringPay.size(); i++) {
//
                        ImageView imageView = new ImageView(context);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, ResUtils.getDimensionPixelSize(R.dimen.x_13dp), 0);
                        imageView.setLayoutParams(layoutParams);

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
                } else {
                    llyPayType.setVisibility(View.GONE);
                }

                holder.setOnClickListener(R.id.tv_buy);

                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }

    /**
     * 排序
     * @param stringPay
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
