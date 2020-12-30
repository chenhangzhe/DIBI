package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.home.model.MsgListEntity;

/**
 * 创建时间：2019-07-24 18:28
 * 作者：Lich_Cool
 * 邮箱：licheng@ld.chainsdir.com
 * 功能描述：站内信列表适配器
 */
public class MsgListAdapter extends AbsRecyclerAdapter<MsgListEntity.DataBean.RecordsBean> {


    private int mExpandedPosition = -1;

    private OnCallbackListener callbackListener;

    public MsgListAdapter(Context context, OnCallbackListener callbackListener) {
        super(context, R.layout.item_msg, R.layout.page_more, R.layout.page_bottom);
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(MsgListEntity.DataBean.RecordsBean d) {
        return d.getType();
    }


    @Override
    public void onBindHolder(RecyclerHolder holder, MsgListEntity.DataBean.RecordsBean d, int position) {


        switch (getItemType(d)) {

            case 0:

                holder.bindTextView(R.id.tv_title, d.getTitle())
                        .bindTextView(R.id.tv_msg_content, d.getContent())
                        .bindTextView(R.id.tv_msg_time, d.getCreateTime());
                //status是否已读（2）未读（1），默认不传都查找
                if (d.getStatus() == 2) {
                    holder.setViewVisible(R.id.v_red_point, false);
                } else if (d.getStatus() == 1) {
                    holder.setViewVisible(R.id.v_red_point, true);
                }
                boolean isExpanded = d.isExpanded();
                holder.setViewSelected(R.id.tv_msg_time, isExpanded);
                holder.setViewVisible(R.id.tv_msg_content, d.isExpanded());

                break;

            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }

    }

    public void setExpandPosition(int position) {
        if (mExpandedPosition != -1) {
            populateExpand(false);
        }
        if (mExpandedPosition != position) {
            mExpandedPosition = position;
            populateExpand(true);
        } else {
            mExpandedPosition = -1;
        }
    }

    private void populateExpand(boolean expand) {
        if (mExpandedPosition < 0 || mExpandedPosition >= getData().size()) {
            return;
        }
        MsgListEntity.DataBean.RecordsBean entity = getData().get(mExpandedPosition);
        if (entity.isExpanded() != expand) {
            entity.setExpanded(expand);
            notifyItemChanged(mExpandedPosition);
        }
    }

    public void setRedPoint(int position) {
        if (position < 0 || position >= getData().size()) {
            return;
        }
        MsgListEntity.DataBean.RecordsBean entity = getData().get(position);
        if (entity.getStatus() == 1) {//status是否已读（2）未读（1），默认不传都查找
            //设置成已读状态
            entity.setStatus(2);
            //更新
            notifyItemChanged(position);
        }
    }
}
