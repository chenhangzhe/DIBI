package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.market.model.OrderEntity;

public class TradeEntrustRVAdapter extends AbsRecyclerAdapter<OrderEntity> {

    private OnCallbackListener callbackListener;

    public TradeEntrustRVAdapter(Context context, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_trade_entrust, R.layout.page_more, R.layout.page_bottom);
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
                boolean buy_market = d.isBuy() && isMarket;
                String tc = "(" + d.getT() + ")";
                boolean isTarget = "SL".equals(d.getOrderType());
                double total = isMarket ? d.getTotal() : d.getPrice() * d.getVolume();

                holder.setViewSelected(R.id.iv_teItemType, d.isBuy())
                        .bindTextView(R.id.tv_teItemPCoin, d.getShowSymbol().substring(0 , d.getShowSymbol().length() - d.getT().length()))
                        .bindTextView(R.id.tv_teItemTime, Util.sdf_M2s.format(d.getTime()))
                        .setOnClickListener(R.id.tv_teItemRetreat)
                        .bindTextView(R.id.tv_teItemPrice, isMarket ? "--" :
                                Util.formatDecimal(d.getPrice(), d.getTp()))
                        .bindTextView(R.id.tv_teItemPriceCoin, tc)
                        .bindTextView(R.id.tv_teItemAmount, buy_market ? "--" :
                                Util.formatDecimal(d.getVolume(), d.getPp()))
                        .bindTextView(R.id.tv_teItemTotal, isMarket && !d.isBuy() ? "--" :
                                Util.formatDecimal(total, d.getTp()))
                        .bindTextView(R.id.tv_teItemTotalCoin, tc)
                        .bindTextView(R.id.tv_teItemDeal, Util.formatDecimal(d.getVolumeDeal(), d.getPp()))
                        .setViewVisible(R.id.ll_teItemTarget, isTarget)
                        .bindTextView(R.id.tv_teItemTarget, !isTarget ? "--" :
                                Util.formatDecimal(d.getPriceTarget(), d.getTp()));
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
