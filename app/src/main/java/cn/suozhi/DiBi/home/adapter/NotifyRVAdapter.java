package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.home.model.NotifyEntity;

public class NotifyRVAdapter extends AbsRecyclerAdapter<NotifyEntity.DataEntity.RecordsEntity> {

    private OnCallbackListener callbackListener;

    public NotifyRVAdapter(Context context, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_notify, R.layout.page_more, R.layout.page_bottom);
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(NotifyEntity.DataEntity.RecordsEntity d) {
        return d.getType();
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, NotifyEntity.DataEntity.RecordsEntity d, int position) {
        switch (getItemType(d)) {
            case 0:
                holder.bindTextView(R.id.tv_notifyItemTitle, d.getTitle())
                        .bindTextView(R.id.tv_notifyItemTime, d.getCreateTime());
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
