package cn.suozhi.DiBi.hide.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.C2cLCoinEntity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.hide.model.VoteEntity;
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;

/**
 *  投票-币种介绍
 */
public class VoteCIntroAdapter extends AbsRecyclerAdapter<VoteEntity.DataBean.RecordsBean.ProjectListBean> {

    public VoteCIntroAdapter(Context context) {
        super(context, R.layout.recycler_vote_cintro);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, VoteEntity.DataBean.RecordsBean.ProjectListBean d, int position) {
        holder.bindImageViewGlideCircle(R.id.iv_vote_coin_image, d.getLogo());
        holder.bindTextView(R.id.tv_vote_coin, d.getShowCode());
        holder.bindTextView(R.id.tv_helpItemTitle, getString(R.string.now_votes) + "：" + (int)d.getGetVotes());
        if(d.getIntro().isEmpty()){
            holder.bindTextView(R.id.tv_vote_content, getString(R.string.emptyData));
        } else {
            holder.bindTextView(R.id.tv_vote_content, d.getIntro());
        }

        holder.setOnClickListener(R.id.tv_vote_button1);
        holder.setOnClickListener(R.id.tv_vote_button2);
    }
}
