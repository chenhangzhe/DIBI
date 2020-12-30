package cn.suozhi.DiBi.hide.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.widget.TimerTextView;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.hide.adapter.VoteCIntroAdapter;
import cn.suozhi.DiBi.hide.adapter.VoteCRankAdapter;
import cn.suozhi.DiBi.hide.model.VoteEntity;
import cn.suozhi.DiBi.market.view.CoinIntroActivity;

public class VoteHistoryDetailsActivity extends BaseActivity implements AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener {

    @BindView(R.id.tv_countdown)
    public TimerTextView tvCountdown;

    @BindView(R.id.rv_vote_crank)
    public RecyclerView rv1;

    @BindView(R.id.rv_vote_cintro)
    public RecyclerView rv2;

    @BindView(R.id.tv_vote_title)
    public TextView tvVoteTitle;
    @BindView(R.id.tv_vote_type)
    public TextView tvVoteType;
    @BindView(R.id.tv_msg)
    public TextView tv_msg;
    @BindView(R.id.tv_winner_coin)
    public TextView tvWinnerCoin;
    @BindView(R.id.tv_votes)
    public TextView tvWinnerVotes;

    private VoteCRankAdapter voteCRankAdapter;
    private VoteCIntroAdapter voteCIntroAdapter;

    private List<VoteEntity.DataBean.RecordsBean.ProjectListBean> pList = new ArrayList<>();

    private int round;

    @Override
    protected int getViewResId() {
        return R.layout.activity_vote_history_details;
    }

    @Override
    protected void init() {
        round = getIntent().getIntExtra("round",0);

        getData();

        rv1.setLayoutManager(new LinearLayoutManager(mContext));

        rv2.setLayoutManager(new LinearLayoutManager(mContext));
        voteCIntroAdapter = new VoteCIntroAdapter(mContext);
        rv2.setAdapter(voteCIntroAdapter.setOnItemClickListener(this));
    }

    @Override
    protected void loadData(){}

    @OnClick({R.id.toolbar_centerBack})
    public void vote(View v) {
        switch (v.getId()) {
            case R.id.toolbar_centerBack:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.tv_vote_button1:
                Intent intentIntro = new Intent(this, CoinIntroActivity.class);
                intentIntro.putExtra("coinCode", pList.get(position).getCurrencyCode());
                intentIntro.putExtra("coinShowCode", pList.get(position).getShowCode());
                startActivity(intentIntro);
                break;
            case R.id.tv_vote_button2:
                Toast.makeText(this, getString(R.string.vote_status3), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        switch (url){
            case Constant.URL.voteList:
                VoteEntity entity = GsonUtil.fromJson(json, VoteEntity.class);
                if (Constant.Int.SUC == entity.getCode()){
                    pList.clear();
                    pList = entity.getData().getRecords().get(0).getProjectList();
                    voteCRankAdapter = new VoteCRankAdapter(mContext,entity.getData().getRecords().get(0).getTotalVotes());
                    rv1.setAdapter(voteCRankAdapter.setOnItemClickListener(this));
                    voteCRankAdapter.setData(pList);
                    voteCIntroAdapter.setData(pList);
                    setUI(entity.getData());
                } else {
                    ToastUtil.initToast(VoteHistoryDetailsActivity.this, Util.getCodeText(VoteHistoryDetailsActivity.this, entity.getCode(), entity.getMsg()));
                    Util.checkLogin(VoteHistoryDetailsActivity.this, entity.getCode());
                }
                break;
        }
    }

    private void getData(){
        OkHttpUtil.getJsonToken(Constant.URL.voteList, SharedUtil.getToken(this), this ,
                "round" , "" + round,
                "status" , "3");
    }

    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(VoteHistoryDetailsActivity.this, url, error);
    }

    @Override
    protected void dismissLoading() {
        super.dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    private void setUI(VoteEntity.DataBean d){
        tvVoteTitle.setText(getString(R.string.every_week_vote) + getString(R.string.vote_num) +" " + d.getRecords().get(0).getRound() + " " + getString(R.string.vote_end));
        int status = d.getRecords().get(0).getStatus(); // 0:未开始,1:进行中,2:已结束,3:已结算
        int has = d.getRecords().get(0).getHas();
        int isWin = d.getRecords().get(0).getIsWin();
        long st = d.getRecords().get(0).getBeginTime();
        long et = d.getRecords().get(0).getEndTime();
        if(status == 0){
            tvVoteType.setText(getString(R.string.vote_status1));
            tvCountdown.setLastMillis(Math.abs(st));
            tvCountdown.setContentBeforAfter("<font color='#FFFFFF'>" + getString(R.string.cel_13) + "：</font><br>", "");
            tvCountdown.start();
        } else if (status == 1){
            tvVoteType.setText(getString(R.string.vote_status2));
            tvCountdown.setLastMillis(Math.abs(et));
            tvCountdown.setContentBeforAfter("<font color='#FFFFFF'>" + getString(R.string.cel_14) + "：</font><br>", "");
            tvCountdown.start();
        } else if (status == 2){
            tvVoteType.setText(getString(R.string.vote_status3));
            tvCountdown.setText(getString(R.string.cel_9));
        } else {
            tvVoteType.setText(getString(R.string.vote_status4));
            tvCountdown.setText(getString(R.string.cel_6));
        }
        // 未参与
        if(has == 0){
            tv_msg.setText(getString(R.string.vote_show_1));
        } else {
            if(isWin == 0){
                tv_msg.setText(getString(R.string.vote_show_2));
            } else {
                tv_msg.setText(getString(R.string.vote_show_3));
            }
        }

        if(d.getRecords().get(0).getWinProject() == null){
            tvWinnerCoin.setVisibility(View.GONE);
            tvWinnerVotes.setVisibility(View.GONE);
        } else {
            tvWinnerCoin.setVisibility(View.VISIBLE);
            tvWinnerVotes.setVisibility(View.VISIBLE);
            String winCode = d.getRecords().get(0).getWinProject().getShowCode();
            String showWinCode = winCode.isEmpty()?" ":winCode;
            tvWinnerCoin.setText(getString(R.string.vote_show_winner) + showWinCode);
            double winVotes = d.getRecords().get(0).getWinProject().getGetVotes();
            if(winVotes > 0){
                tvWinnerVotes.setText(getString(R.string.vote_show_winner_votes) + winVotes);
            } else {
                tvWinnerVotes.setText(getString(R.string.vote_show_winner_votes) + "0");
            }
        }

    }

}
