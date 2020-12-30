package cn.suozhi.DiBi.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.SelectEntity;

public class NationRVAdapter extends AbsRecyclerAdapter<SelectEntity.DataEntity> {

    private boolean showValue;
    private int stickyHeight;

    public NationRVAdapter(Context context, boolean showValue) {
        super(context, R.layout.recycler_nation);
        this.showValue = showValue;
        initStickyHeight();
    }

    private void initStickyHeight() {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_nation, null);
        View sticky = v.findViewById(R.id.tv_nationItemType);
        stickyHeight = Util.getWidgetHeight(sticky);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, SelectEntity.DataEntity d, int position) {
        boolean isFirst = position == getPositionForSection(d.getSortLetter());
        holder.bindTextViewWithVisible(R.id.tv_nationItemType, getStickyText(position), isFirst)
                .bindTextViewWithClickListener(R.id.tv_nationItemName, d.getName())
                .bindTextViewWithVisible(R.id.tv_nationItemValue, d.getValue(), showValue);
    }

    @Override
    public boolean isStickyGroupStart(int position) {
        return position == 0 || !(getStickyText(position).equals(getStickyText(position - 1)));
    }

    @Override
    public boolean isStickyGroupEnd(int position) {
        return position == data.size() - 1 ||
                !(getStickyText(position).equals(getStickyText(position + 1)));
    }

    @Override
    public int getStickyHeight() {
        return stickyHeight;
    }

    @Override
    public String getStickyText(int position) {
        return data.get(position).getSortLetter().substring(0, 1);
    }

    public int getPositionForSection(String section) {
        int position = -1;
        for (int i = 0; i < data.size(); i++) {
            String sortStr = data.get(i).getSortLetter();
            if (section.charAt(0) == sortStr.charAt(0)) {
                position = i;
                break;
            }
        }
        return position;
    }
}
