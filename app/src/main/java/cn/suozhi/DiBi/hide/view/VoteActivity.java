package cn.suozhi.DiBi.hide.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.widget.TimerTextView;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.DoVoteDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.hide.adapter.VoteCIntroAdapter;
import cn.suozhi.DiBi.hide.adapter.VoteCRankAdapter;
import cn.suozhi.DiBi.hide.model.VoteEntity;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.market.view.CoinIntroActivity;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

public class VoteActivity extends BaseActivity implements AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener,BaseDialog.OnItemClickListener {

    @BindView(R.id.tv_countdown)
    public TextView tvCountdown;

    @BindView(R.id.rv_vote_crank)
    public RecyclerView rv1;

    @BindView(R.id.rv_vote_cintro)
    public RecyclerView rv2;

    @BindView(R.id.tv_vote_title)
    public TextView tvVoteTitle;
    @BindView(R.id.tv_vote_type)
    public TextView tvVoteType;
    @BindView(R.id.tv_msg)
    public TextView tvMSG;
    @BindView(R.id.tv_winner_coin)
    public TextView tvWinnerCoin;
    @BindView(R.id.tv_votes)
    public TextView tvWinnerVotes;

    private VoteCRankAdapter voteCRankAdapter;
    private VoteCIntroAdapter voteCIntroAdapter;

    private List<VoteEntity.DataBean.RecordsBean.ProjectListBean> pList = new ArrayList<>();

    private int clickP = -1;
    private String clickV;

    private long sDate,eDate;
    private int respStatus = 0;

    private Timer timer;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
            },0,1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        }
    };

    private String availAmout;

    @Override
    protected int getViewResId() {
        return R.layout.activity_vote;
    }

    @Override
    protected void init() {
        rv1.setLayoutManager(new LinearLayoutManager(mContext));

        rv2.setLayoutManager(new LinearLayoutManager(mContext));
        voteCIntroAdapter = new VoteCIntroAdapter(mContext);
        rv2.setAdapter(voteCIntroAdapter.setOnItemClickListener(this));
    }

    @Override
    protected void loadData(){
        super.loadData();
        getData();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                setUI();
            }
        }
    };

    @OnClick({R.id.toolbar_centerBack,R.id.toolbar_rightTitle})
    public void vote(View v) {
        switch (v.getId()) {
            case R.id.toolbar_centerBack:
                finish();
                break;
            case R.id.toolbar_rightTitle:
                startActivity(new Intent(VoteActivity.this, VoteHistoryActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.tv_vote_button1:
                Intent intentIntro = new Intent(VoteActivity.this, CoinIntroActivity.class);
                intentIntro.putExtra("coinCode", pList.get(position).getCurrencyCode());
                intentIntro.putExtra("coinShowCode", pList.get(position).getShowCode());
                startActivity(intentIntro);
                break;
            case R.id.tv_vote_button2:
                clickP = pList.get(position).getProjectId();
                DoVoteDialog.newInstance(pList.get(position).getShowCode() , pList.get(position).getProjectId() , availAmout)
                        .setOnItemClickListener(this)
                        .show(this);
                break;
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        switch (url){
            case Constant.URL.voteList:
                tvMSG.setText(getString(R.string.vote_status1));
                tvWinnerCoin.setVisibility(View.GONE);
                tvWinnerVotes.setVisibility(View.GONE);
                VoteEntity entity = GsonUtil.fromJson(json, VoteEntity.class);
                if (Constant.Int.SUC == entity.getCode()){
                    pList.clear();
                    pList = entity.getData().getRecords().get(0).getProjectList();
                    voteCRankAdapter = new VoteCRankAdapter(mContext,entity.getData().getRecords().get(0).getTotalVotes());
                    rv1.setAdapter(voteCRankAdapter.setOnItemClickListener(this));
                    voteCRankAdapter.setData(pList);
                    voteCIntroAdapter.setData(pList);

                    sDate = entity.getData().getRecords().get(0).getBeginTime();
                    eDate = entity.getData().getRecords().get(0).getEndTime();
                    respStatus = entity.getData().getRecords().get(0).getStatus();

                    setUI(entity.getData());

                    mHandler.post(runnable);
                } else {
                    ToastUtil.initToast(VoteActivity.this, Util.getCodeText(VoteActivity.this, entity.getCode(), entity.getMsg()));
                    Util.checkLogin(VoteActivity.this, entity.getCode());
                }
                break;
            case Constant.URL.onceToken:
                OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
                if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                    String onceToken = onceTokenEnity.getData().getResultToken();
                    OkHttpUtil.postJsonToken(Constant.URL.doVote, SharedUtil.getToken(this), this,
                            "projectId", "" + clickP,
                            "onceToken", onceToken,
                            "votes",clickV
                    );
                }
                break;
            case Constant.URL.doVote:
                ObjectEntity obj = GsonUtil.fromJson(json, ObjectEntity.class);
                if(obj.getCode() == Constant.Int.SUC){
                    ToastUtil.initToast(this, getString(R.string.str_suc));
                    getData();
                } else {
                    ToastUtil.initToast(this, obj.getMsg());
                }
                break;
            case Constant.URL.singleCoinInfoByName:
                SingleCoinInfoEnity singleCoinInfoEnity = GsonUtil.fromJson(json, SingleCoinInfoEnity.class);
                if (singleCoinInfoEnity.getCode() == Constant.Int.SUC) {
                    availAmout = singleCoinInfoEnity.getData().getAvailableAmount();
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            singleCoinInfoEnity.getCode(), singleCoinInfoEnity.getMsg()));
                    Util.checkLogin(this, singleCoinInfoEnity.getCode());
                }
                break;
        }
    }

    private void getData(){
        getSingleCoinInfo();
        OkHttpUtil.getJsonToken(Constant.URL.voteList, SharedUtil.getToken(this), this);
    }

    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(VoteActivity.this, url, error);
    }

    @Override
    protected void dismissLoading() {
        super.dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    private void setUI(){
        String a = getFormatTime(sDate - System.currentTimeMillis());
        String b = getFormatTime(eDate - System.currentTimeMillis());

        if(respStatus == 0){
            tvVoteType.setText(getString(R.string.vote_status1));
            tvCountdown.setVisibility(View.GONE);
        } else if (respStatus == 1){
            tvVoteType.setText(getString(R.string.vote_status2));
            tvCountdown.setVisibility(View.VISIBLE);
            tvCountdown.setText(getString(R.string.cel_14) + b);
        } else {
            tvVoteType.setText(getString(R.string.vote_status4));
            tvCountdown.setText(getString(R.string.cel_6));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUI(VoteEntity.DataBean d){
        if(d == null){
            tvMSG.setText(getString(R.string.vote_status1));
            tvWinnerCoin.setVisibility(View.GONE);
            tvWinnerVotes.setVisibility(View.GONE);
            return;
        }
        tvVoteTitle.setText(getString(R.string.every_week_vote) + getString(R.string.vote_num) +" " + d.getRecords().get(0).getRound() + " " + getString(R.string.vote_end));
        int status = d.getRecords().get(0).getStatus(); // 0:未开始,1:进行中,2:已结束,3:已结算
        int has = d.getRecords().get(0).getHas();
        int isWin = d.getRecords().get(0).getIsWin();
        // 未参与
        if(has == 0){
            tvMSG.setText(getString(R.string.vote_show_1));
        } else {
            if(isWin == 0){
                tvMSG.setText(getString(R.string.vote_show_2));
            } else {
                tvMSG.setText(getString(R.string.vote_show_3));
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

    @Override
    public void onItemClick(View v) {
        switch (v.getId()){
            case R.id.tv_dgEaConfirm:
                String alias = (String) v.getTag();
                if(alias.equals("-1")){
                    ToastUtil.initToast(this,getString(R.string.dg_vote_dialog_edit_hint));
                } else {
                    clickV = alias;
                    getOnceToken();
                }
                break;
        }
    }

    private void getOnceToken(){
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.oncetoken);
    }

    /**
     * 获取单个币种信息
     */
    private void getSingleCoinInfo() {
        OkHttpUtil.getJsonToken(Constant.URL.singleCoinInfoByName, SharedUtil.getToken(mContext), this, "currencyCode", "DIBI");
    }

    private String getFormatTime(long distanceMillis) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = distanceMillis / dd;
        Long hour = (distanceMillis - day * dd) / hh;
        Long minute = (distanceMillis - day * dd - hour * hh) / mi;
        Long second = (distanceMillis - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }

        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        // 将线程销毁掉
        mHandler.removeCallbacks(runnable);
        timer.cancel();
        super.onDestroy();
    }

}
