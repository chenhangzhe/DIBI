package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class TradeFilterRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    public TradeFilterRVAdapter(Context context) {
        super(context, R.layout.recycler_trade_filter);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        int tp = d.getTp();

        String p = "", t = "";
        if(d.getCurrencyPairRegion().equals("B")){
            p = d.getShowSymbol().substring(0 , d.getShowSymbol().length() - d.getT().length() + 1);
        } else {
            p = d.getP();
        }

        holder.bindTextView(R.id.tv_tfItemPCoin, p)
                .bindTextView(R.id.tv_tfItemTCoin, d.getT())
                .bindTextViewWithSelected(R.id.tv_tfItemPrice, tp >= 0 ? Util.formatDecimal(d.getPrice(), tp)
                        : d.getPrice() + "", d.getRate() >= 0);
    }
}
