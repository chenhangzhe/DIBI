package cn.suozhi.DiBi.c2c.adapter;

import android.content.Context;
import android.widget.TextView;

import java.text.DecimalFormat;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.OrderEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.RegexUtils;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.SpannableStringUtils;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 首页钱包的适配器
 */
public class MyOrderAdapter extends AbsRecyclerAdapter<OrderEnity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public MyOrderAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.item_my_order, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public int getItemType(OrderEnity.DataBean.RecordsBean d) {
        return d.getLoadType();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, OrderEnity.DataBean.RecordsBean data, int position) {

        switch (getItemType(data)) {
            case 0:
                holder.bindTextView(R.id.tv_order, data.getOrderNo());  //订单编号
                if (SharedUtil.getLanguage(context).equals("cht")){
                    holder.bindImageView(R.id.iv_order_status, data.getOrderType() == 1 ? R.mipmap.buy_tw : R.mipmap.sell_tw);  //订单买卖状态
                }else if (SharedUtil.getLanguage(context).equals("en")){
                    holder.bindImageView(R.id.iv_order_status, data.getOrderType() == 1 ? R.mipmap.buy_en : R.mipmap.sell_en);  //订单买卖状态
                }else {
                    holder.bindImageView(R.id.iv_order_status, data.getOrderType() == 1 ? R.mipmap.buy : R.mipmap.sell);  //订单买卖状态

                }

//                holder.bindTextView(R.id.tv_price_nums, getString(R.string.str_c2c_price) + "  " + Util.formatTinyDecimal(data.getPrice(), false) + " " + data.getLegalCurrencyCode()); //单价
                holder.bindTextView(R.id.tv_price_nums, getString(R.string.str_c2c_price) + "  " + new DecimalFormat("##.###").format(data.getPrice()) + " " + data.getLegalCurrencyCode());
                holder.bindTextView(R.id.tv_nick, getString(R.string.str_nick) + "  " + (RegexUtils.isMobileExact(data.getAnother()) ? Util.addStarInMiddle(data.getAnother()) : data.getAnother()));  //昵称
                holder.bindTextView(R.id.tv_status, getStatusText(data.getStatus(), (TextView) holder.getView(R.id.tv_status)));  //订单状态

                TextView tvPriceTotal = (TextView) holder.getView(R.id.tv_price_total);
                holder.bindTextView(R.id.tv_amount, getString(R.string.str_amount) + "  " + Util.formatTinyDecimal(data.getQuantity(), false) + "  " + data.getCurrencyCode());  //数量

                tvPriceTotal.setText(SpannableStringUtils.getBuilder(getString(R.string.total))
                        .setForegroundColor(ResUtils.getColor(R.color.gy8A))
                        .append("  ")
                        .append(Util.formatTinyDecimal(data.getTotalPrice(), false) + " " + data.getLegalCurrencyCode())
//                        .setForegroundColor(ResUtils.getColor(R.color.purple77))
                        .setForegroundColor(ResUtils.getColor(R.color.color_1888FE))
                        .create());
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }

    }


    /**
     * 获取订单状态的文字
     *
     * @param status
     * @param view
     * @return
     */
    private String getStatusText(int status, TextView view) {
        String text = "";
        switch (status) {
            case 1:
                text = getString(R.string.str_wait_pay);
                view.setTextColor(ResUtils.getColor(R.color.redE0));
                break;
            case 2:
                text = getString(R.string.str_wait_comfirm);
                view.setTextColor(ResUtils.getColor(R.color.color_e89940));
                break;
            case 3:
                text = getString(R.string.str_canceled);
                view.setTextColor(ResUtils.getColor(R.color.green3F));
                break;
            case 4:
                text = getString(R.string.str_complainting);
                view.setTextColor(ResUtils.getColor(R.color.color_e89940));
                break;
            case 5:
                text = getString(R.string.str_completed);
                view.setTextColor(ResUtils.getColor(R.color.green3F));
                break;
        }
        return text;
    }
}
