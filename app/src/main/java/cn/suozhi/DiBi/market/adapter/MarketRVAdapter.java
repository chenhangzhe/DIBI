package cn.suozhi.DiBi.market.adapter;

import android.content.Context;
import android.util.Log;

import java.util.Set;

import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class MarketRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    private Set<String> favor;

    public MarketRVAdapter(Context context) {
        super(context, R.layout.recycler_market);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        String p = "", t = "";
        if(d.getCurrencyPairRegion().equals("B")){
            p = d.getShowSymbol().substring(0 , d.getShowSymbol().length() - d.getT().length() + 1);
        } else {
            p = d.getP();
        }

        int tp = d.getTp();
        int pp = Math.min(d.getPp(), 2);
        boolean positive = d.getRate() >= 0;
        holder.setItemTag(R.id.tag_relation, d.getSymbol())
                .setViewSelectedWithClickListener(R.id.iv_marketItemStar, AppUtil.isFavor(favor, d.getSymbol()))
                .bindTextView(R.id.tv_marketItemPCoin, p)
                .bindTextView(R.id.tv_marketItemTCoin, d.getT())
                .bindTextView(R.id.tv_marketItemVol, pp >= 0 ? AppUtil.floorRemoveZero(d.getVolume(), pp)
                        : d.getVolume() + "")
//                .bindTextView(R.id.tv_marketItemPrice, tp >= 0 ? AppUtil.roundRemoveZero(d.getPrice(), tp)
//                        : d.getPrice() + "")
                .bindTextViewWithSelected(R.id.tv_marketItemPrice , tp >= 0 ? AppUtil.roundRemoveZero(d.getPrice(), tp) : d.getPrice() + "",positive)
                .bindTextView(R.id.tv_marketItemPriceMoney,
                        Constant.Strings.Yuan + Util.formatDecimal(d.getCny(), 4))
                .bindTextViewWithSelected(R.id.tv_marketItemRate, (positive ? "+" : "") +
                        Util.Format_Percent.format(d.getRate()), positive);
    }

    public void setFavor(Set<String> favor) {
        this.favor = favor;
        notifyDataSetChanged();
    }
}
