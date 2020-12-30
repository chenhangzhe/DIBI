package cn.suozhi.DiBi.hide.adapter;

import android.content.Context;
import android.util.Log;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.hide.model.VoteEntity;

/**
 *  投票-币种进度
 */
public class VoteCRankAdapter extends AbsRecyclerAdapter<VoteEntity.DataBean.RecordsBean.ProjectListBean> {

    private double votes;

    public VoteCRankAdapter(Context context,double votes) {
        super(context, R.layout.recycler_vote_crank);
        this.votes = votes;
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, VoteEntity.DataBean.RecordsBean.ProjectListBean d, int position) {
        holder.bindTextView(R.id.tv_vote_coin_item_text,d.getShowCode());
        if(d.getGetVotes() > 0){
            double percent = d.getGetVotes() / votes * 100; // 计算百分比进度
            // 百分比小于1时，进度条设置为1
            if( percent < 1){
                holder.bindProgressBar(R.id.progress_vote_item_pro, 1); // 进度条
            } else {
                holder.bindProgressBar(R.id.progress_vote_item_pro, (int) percent); // 进度条
            }
        } else {
            holder.bindProgressBar(R.id.progress_vote_item_pro, 0); // 进度条
        }
    }
}
