package cn.suozhi.DiBi.market.adapter;

import android.content.Context;
import android.view.View;

import java.util.Set;

import androidx.recyclerview.widget.RecyclerView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class SearchRVAdapter extends AbsRecyclerAdapter<QuoteEntity> {

    private int height;//空页面高度
    private final int span = 4;//一行个数
    private Set<String> favor;

    public SearchRVAdapter(Context context, Set<String> favor) {
        super(context, R.layout.recycler_search, R.layout.recycler_search_history,
                R.layout.recycler_text_line, R.layout.recycler_empty, R.layout.empty_tips);
        this.favor = favor;
        height = Util.getPhoneHeight(context) / 2;
    }

    @Override
    public int getItemType(QuoteEntity d) {
        return d.getType();
    }

    @Override
    protected boolean isItemFullSpan(int type) {
        return type != 1;
    }

    @Override
    protected int getItemSpanSize(int type) {
        return type == 1 ? 1 : span;
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, QuoteEntity d, int position) {
        switch (getItemType(d)) {
            case 0:
                int point = d.getTp();
                boolean positive = d.getRate() >= 0;
                holder.setViewSelectedWithClickListener(R.id.iv_searchItemStar,
                            AppUtil.isFavor(favor, d.getSymbol()))
                        .bindTextView(R.id.tv_searchItemPCoin, d.getP())
                        .bindTextView(R.id.tv_searchItemTCoin, d.getT())
                        .bindTextView(R.id.tv_searchItemPrice, point >= 0 ?
                                AppUtil.roundRemoveZero(d.getPrice(), point) : d.getPrice() + "")
                        .bindTextViewWithSelected(R.id.tv_searchItemRate, (positive ? "+" : "") +
                                Util.Format_Percent.format(d.getRate()), positive);
                break;
            case 1:
                int dp = dp(1);
                int dp7 = dp * 7, dp8 = dp * 8, dp15 = dp * 15;
                int left = position % span == 0 ? dp15 : dp7;
                int right = (position + 1) % span == 0 ? dp15 : dp8;
                holder.setItemMargins(left, dp8, right, dp8)
                        .bindTextView(R.id.tv_shItem, d.getSymbol());
                break;
            case 3:
                holder.bindTextView(R.id.tv_emptyItem, R.string.emptyHistory);
                break;
            case 4:
                holder.setViewLayoutParams(R.id.cl_empty, RecyclerView.LayoutParams.MATCH_PARENT, height)
                        .setViewVisible(R.id.cl_empty, View.VISIBLE);
                break;
        }
    }

    public void setFavor(Set<String> favor) {
        this.favor = favor;
        notifyDataSetChanged();
    }
}
