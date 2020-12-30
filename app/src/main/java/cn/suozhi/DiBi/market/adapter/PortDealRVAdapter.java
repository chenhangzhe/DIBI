package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class PortDealRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    private int height;//空页面高度

    public PortDealRVAdapter(Context context, int height) {
        super(context, R.layout.recycler_port_deal);
        this.height = height;
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        holder.bindTextView(R.id.tv_pdItemTime, Util.sdf_H2s.format(d.getTime()))
                .bindTextViewWithSelected(R.id.tv_pdItemPrice,
                        Util.formatDecimal(d.getPrice(), d.getTp()), d.isFavor())
                .bindTextView(R.id.tv_pdItemAmount, Util.formatDecimal(d.getVolume(), d.getPp()));
    }
}
