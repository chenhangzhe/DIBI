package cn.suozhi.DiBi.home.adapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class HomeTopRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    public HomeTopRVAdapter(Context context, List<QuoteEntity> data) {
        super(context, data, R.layout.recycler_home_top);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        String p = "", t = "";
        if(d.getCurrencyPairRegion().equals("B")){
            p = d.getShowSymbol().substring(0 , d.getShowSymbol().length() - d.getT().length() + 1);
        } else {
            p = d.getP();
        }
        int point = Math.min(d.getTp(), 4);
        boolean positive = d.getRate() >= 0;
        holder.setItemTag(R.id.tag_relation, d.getSymbol())
                .bindTextView(R.id.tv_htItemName, Util.null2Default(p) +
                        Util.null2Default(d.getT()))
                .bindTextViewWithSelected(R.id.tv_htItemPrice, point >= 0 ?
                        Util.formatDecimal(d.getPrice(), point) : d.getPrice() + "", positive)
                .bindTextViewWithSelected(R.id.tv_htItemRate,
                        (positive ? "+" : "") + Util.Format_Percent.format(d.getRate()), positive);
    }
}
