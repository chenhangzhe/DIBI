package cn.suozhi.DiBi.c2c.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.AdvaneEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 首页钱包的适配器
 */
public class MyAdvanceAdapter extends AbsRecyclerAdapter<AdvaneEnity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public MyAdvanceAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.item_my_advance, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public int getItemType(AdvaneEnity.DataBean.RecordsBean d) {
        return d.getLoadType();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, AdvaneEnity.DataBean.RecordsBean data, int position) {

        switch (getItemType(data)) {
            case 0:
                holder.bindTextView(R.id.tv_order, data.getAdNo());  //订单编号

                if (SharedUtil.getLanguage(context).equals("cht")){
                    holder.bindImageView(R.id.iv_order_status, data.getType() == 1 ? R.mipmap.buy_tw : R.mipmap.sell_tw);  //订单买卖状态
                }else if (SharedUtil.getLanguage(context).equals("en")){
                    holder.bindImageView(R.id.iv_order_status, data.getType() == 1 ? R.mipmap.buy_en : R.mipmap.sell_en);  //订单买卖状态
                }else {
                    holder.bindImageView(R.id.iv_order_status, data.getType() == 1 ? R.mipmap.buy : R.mipmap.sell);  //订单买卖状态

                }


//                holder.bindTextView(R.id.tv_price_nums, getString(R.string.str_c2c_price) + "  " + Util.formatTinyDecimal(data.getPrice(), false) + " " + data.getLegalCurrencyCode()); //单价
                holder.bindTextView(R.id.tv_price_nums, getString(R.string.str_c2c_price) + "  " + new DecimalFormat("##.###").format(data.getPrice()) + " " + data.getLegalCurrencyCode()); //单价
                if (data.getType() == 1){
                    holder.bindTextView(R.id.tv_limit_amount, getString(R.string.str_c2c_limit_amount_two) + " " + Util.formatTinyDecimal(data.getLimitMin(), false) + "-" + Util.formatTinyDecimal(data.getLimitMax(), false) + " " + data.getCurrencyCode());
                } else {
                    holder.bindTextView(R.id.tv_limit_amount, getString(R.string.str_c2c_limit_amount_two) + " " + Util.formatTinyDecimal(data.getLimitMin(), false) + "-" + Util.formatTinyDecimal(data.getLimitMax(), false) + " " + data.getLegalCurrencyCode());
                }
                holder.bindTextView(R.id.tv_amount, getString(R.string.str_amount) + "  " + Util.formatTinyDecimal(data.getTotalQuantity(), false) + "  " + data.getCurrencyCode());  //数量
                holder.bindTextView(R.id.tv_time, getString(R.string.time) + "  " + data.getCreateTime());  //订单状态

                LinearLayout llyPayType = (LinearLayout) holder.getView(R.id.lly_payType);
                if (!TextUtils.isEmpty(data.getPayModes())) {
                    llyPayType.removeAllViews();
                    llyPayType.setVisibility(View.VISIBLE);
                    String[] payMode = data.getPayModes().split(",");
                    for (int i = 0; i < payMode.length; i++) {
//
                        ImageView imageView = new ImageView(context);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, ResUtils.getDimensionPixelSize(R.dimen.x_13dp), 0);
                        imageView.setLayoutParams(layoutParams);

                        if (payMode[i].equals("1")) {
                            imageView.setImageResource(R.mipmap.alipay);
                        }

                        if (payMode[i].equals("2")) {
                            imageView.setImageResource(R.mipmap.wechat);
                        }

                        if (payMode[i].equals("3")) {
                            imageView.setImageResource(R.mipmap.bank);
                        }

                        llyPayType.addView(imageView);
                    }
                } else {
                    llyPayType.setVisibility(View.GONE);
                }
                TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
                TextView btnOne = (TextView) holder.getView(R.id.tv_up_down);
                TextView btnTwo = (TextView) holder.getView(R.id.tv_edit);
                btnTwo.setText(getString(R.string.str_edit));
                switch (data.getStatus()) {
                    case 1:
                        tvStatus.setText(getString(R.string.str_uping));
                        tvStatus.setTextColor(ResUtils.getColor(R.color.color_e89940));
                        btnOne.setText(getString(R.string.str_down));
                        btnOne.setSelected(true);
                        btnTwo.setSelected(true);
                        break;
                    case 2:
                        tvStatus.setText(getString(R.string.str_downed));
                        tvStatus.setTextColor(ResUtils.getColor(R.color.gy8A));
                        btnOne.setText(getString(R.string.str_up));
                        btnOne.setSelected(true);
                        btnTwo.setSelected(true);
                        break;
                    case 3:
                        tvStatus.setText(getString(R.string.str_completed));
                        tvStatus.setTextColor(ResUtils.getColor(R.color.green3F));
                        btnOne.setText(getString(R.string.str_up));
                        btnOne.setSelected(false);
                        btnTwo.setSelected(false);
                        break;
                }

                holder.setOnClickListener(R.id.tv_up_down);
                holder.setOnClickListener(R.id.tv_edit);
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;

        }
    }


}
