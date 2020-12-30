package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;

public class SingleTextRVAdapter extends AbsRecyclerAdapter<String> {

    private int index;

    public SingleTextRVAdapter(Context context, List<String> data, int index) {
        super(context, data, R.layout.recycler_single_text);
        this.index = index;
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, String d, int position) {
        holder.bindTextViewWithSelected(R.id.tv_stItem, d, position == index);
    }
}
