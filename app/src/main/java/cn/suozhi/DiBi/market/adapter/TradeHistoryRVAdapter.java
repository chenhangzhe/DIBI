package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.market.model.OrderEntity;

public class TradeHistoryRVAdapter extends AbsRecyclerAdapter<OrderEntity> {

    private OnCallbackListener callbackListener;

    public TradeHistoryRVAdapter(Context context, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_trade_history, R.layout.page_more, R.layout.page_bottom);
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(OrderEntity d) {
        return d.getType();
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, OrderEntity d, int position) {
        switch (getItemType(d)) {
            case 0:
                boolean isMarket = "M".equals(d.getOrderType());
                boolean isTarget = "SL".equals(d.getOrderType());
                boolean buy_market = d.isBuy() && isMarket;
                double total = isMarket ? d.getTotal() : d.getPrice() * d.getVolume();
                String sp = d.getShowSymbol().substring(0 , d.getShowSymbol().length() - d.getT().length());
                String tc = "(" + d.getT() + ")";
                String percent = buy_market ? "--" :
                        Util.Format_Percent.format(d.getVolumeDeal() / d.getVolume());
                holder.setViewSelected(R.id.iv_thItemType, d.isBuy())
                        .bindTextView(R.id.tv_thItemSymbol,  sp + "/" + d.getT())
                        .bindTextViewWithColorId(R.id.tv_thItemStatus,
                                getStringInResId("orderStatus" + d.getStatus()),
                                AppUtil.getStatusColor(d.getStatus()))
                        .bindTextView(R.id.tv_thItemPrice, isMarket ? "--" :
                                Util.formatDecimal(d.getPrice(), d.getTp()))
                        .bindTextView(R.id.tv_thItemPriceCoin, tc)
                        .bindTextView(R.id.tv_thItemDeal, Util.formatDecimal(d.getVolumeDeal(), d.getPp()))
                        .bindTextView(R.id.tv_thItemDealCoin, sp)
                        .bindTextView(R.id.tv_thItemTotal, isMarket && !d.isBuy() ? "--" :
                                Util.formatDecimal(total, d.getTp()))
                        .bindTextView(R.id.tv_thItemTotalCoin, tc)
                        .bindTextView(R.id.tv_thItemVol, buy_market ? "--" :
                                Util.formatDecimal(d.getVolume(), d.getPp()))
                        .bindTextView(R.id.tv_thItemVolCoin, sp)
                        .bindTextView(R.id.tv_thItemAverage, d.getPriceAverage() < 0 ? "--" :
                                Util.formatDecimal(d.getPriceAverage(), d.getTp()))
                        .bindTextView(R.id.tv_thItemAverageCoin, tc)
                        .bindTextView(R.id.tv_thItemPercent, percent)
                        .bindTextView(R.id.tv_thItemTarget, !isTarget ? "--" :
                                Util.formatDecimal(d.getPriceTarget(), d.getTp()))
                        .bindTextView(R.id.tv_thItemTargetCoin, tc)
                        .bindTextView(R.id.tv_thItemTime, Util.sdf_M2s.format(d.getTime()));
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
