package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class HomeRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    private int height;//空页面高度

    public HomeRVAdapter(Context context, int height) {
        super(context, R.layout.recycler_home);
        this.height = height;
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        int point = d.getTp();
        boolean positive = d.getRate() >= 0;
        holder.setItemTag(R.id.tag_relation, d.getSymbol())
                .bindTextView(R.id.tv_homeItemPCoin, d.getP())
                .bindTextView(R.id.tv_homeItemTCoin, d.getT())
//                .bindTextView(R.id.tv_homeItemPrice, point >= 0 ? AppUtil.roundRemoveZero(d.getPrice(), point)
//                        : d.getPrice() + "")
                .bindTextViewWithSelected(R.id.tv_homeItemPrice, point >= 0 ? AppUtil.roundRemoveZero(d.getPrice(), point)
                        : d.getPrice() + "" , positive)
                .bindTextViewWithSelected(R.id.tv_homeItemRate, (positive ? "+" : "") +
                        Util.Format_Percent.format(d.getRate()), positive);
    }
}
