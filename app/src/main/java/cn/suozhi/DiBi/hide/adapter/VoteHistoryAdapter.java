package cn.suozhi.DiBi.hide.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.hide.model.VoteEntity;

public class VoteHistoryAdapter extends AbsRecyclerAdapter<VoteEntity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public VoteHistoryAdapter(Context context, OnCallbackListener callbackListener) {
        this(context, 0, callbackListener);
    }

    public VoteHistoryAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_vote_history, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(VoteEntity.DataBean.RecordsBean d) {
        return d.getLoadType();
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
    public void onBindHolder(RecyclerHolder holder, VoteEntity.DataBean.RecordsBean d, int position) {
        switch (getItemType(d)) {
            case 0:
                holder.bindImageViewGlideCirclePE(R.id.iv_vote_history_coin_image , d.getWinProject().getLogo() , R.mipmap.img_loading , R.mipmap.img_fail);
                holder.bindTextView(R.id.tv_vote_history_coin , d.getWinProject().getShowCode());
                holder.bindTextView(R.id.tv_vote_history_votes ,  getString(R.string.vote_history_get_votes) + (int)d.getWinProject().getGetVotes());
                holder.bindTextView(R.id.tv_vote_history_total_part , getString(R.string.vote_history_take_part) + d.getWinProject().getTakePartNum());
                holder.bindTextView(R.id.tv_vote_history_time , getString(R.string.vote_history_end_time) + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d.getEndTime()));

                holder.setOnClickListener(R.id.ll_vote_history_item_p);

                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
