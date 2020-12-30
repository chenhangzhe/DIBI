package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.home.model.NotifyEntity;

public class HelpCenterAdapter extends AbsRecyclerAdapter<NotifyEntity.DataEntity.RecordsEntity> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public HelpCenterAdapter(Context context, OnCallbackListener callbackListener) {
        this(context, 0, callbackListener);
    }

    public HelpCenterAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_help, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(NotifyEntity.DataEntity.RecordsEntity d) {
        return d.getType();
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
    public void onBindHolder(RecyclerHolder holder, NotifyEntity.DataEntity.RecordsEntity d, int position) {
        switch (getItemType(d)) {
            case 0:
                holder.bindTextView(R.id.tv_helpItemTitle, d.getTitle());
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
