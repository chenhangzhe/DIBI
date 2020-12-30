package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.market.model.OrderEntity;

public class TradeTradeRVAdapter extends AbsRecyclerAdapter<OrderEntity> {

    private OnCallbackListener callbackListener;

    public TradeTradeRVAdapter(Context context, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_trade_trade, R.layout.page_more, R.layout.page_bottom);
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
                holder.setViewSelected(R.id.iv_ttItemType, d.isBuy())
                        .bindTextView(R.id.tv_ttItemPCoin, d.getShowSymbol().substring(0 , d.getShowSymbol().length() - d.getT().length()))
                        .bindTextView(R.id.tv_ttItemTime, Util.sdf_y2s.format(d.getTime()))
                        .bindTextView(R.id.tv_ttItemPrice, Util.formatDecimal(d.getPrice(), d.getTp()))
                        .bindTextView(R.id.tv_ttItemPriceCoin, "(" + d.getT() + ")")
                        .bindTextView(R.id.tv_ttItemAmount, Util.formatDecimal(d.getVolume(), d.getPp()))
                        .bindTextView(R.id.tv_ttItemTotal,
                                Util.formatDecimal(d.getTotal(), d.getTp()))
                        .bindTextView(R.id.tv_ttItemFee, Util.Format8.format(d.getFee()));
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
