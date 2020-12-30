package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.home.model.WorkOrderListEntity;

/**
 * 功能描述：工单适配器
 */
public class WorkOrderAdapter extends AbsRecyclerAdapter<WorkOrderListEntity.DataBean.RecordsBean> {


    private OnCallbackListener callbackListener;

    public WorkOrderAdapter(Context context, OnCallbackListener callbackListener) {
        super(context, R.layout.item_work_order, R.layout.page_more, R.layout.page_bottom);
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(WorkOrderListEntity.DataBean.RecordsBean d) {
        return d.getViewType();
    }


    @Override
    public void onBindHolder(RecyclerHolder holder, WorkOrderListEntity.DataBean.RecordsBean d, int position) {


        switch (getItemType(d)) {

            case 0:

              holder.bindTextView(R.id.tv_work_order_title,d.getTitle())
                      .bindTextView(R.id.tv_work_order_time,d.getCreateTime());
                break;

            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }

    }
}
