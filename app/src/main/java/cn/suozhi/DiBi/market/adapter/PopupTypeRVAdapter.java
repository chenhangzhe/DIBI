package cn.suozhi.DiBi.market.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;

public class PopupTypeRVAdapter extends AbsRecyclerAdapter<String> {

    private int width;

    public PopupTypeRVAdapter(Context context, int width) {
        super(context, R.layout.recycler_popup_type);
        this.width = width;
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, String d, int position) {
        holder.setItemLayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT)
                .bindTextView(R.id.tv_popupType, d);
    }
}
