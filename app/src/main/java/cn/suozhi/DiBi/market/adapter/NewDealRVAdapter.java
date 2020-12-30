package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class NewDealRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    public NewDealRVAdapter(Context context) {
        super(context, R.layout.recycler_new_deal);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        holder.bindTextView(R.id.tv_ndItemTime, Util.sdf_H2s.format(d.getTime()))
                .bindTextViewWithSelected(R.id.tv_ndItemPrice,
                        Util.formatDecimal(d.getPrice(), d.getTp()), d.isFavor())
                .bindTextView(R.id.tv_ndItemAmount, Util.formatDecimal(d.getVolume(), d.getPp()));
    }
}
