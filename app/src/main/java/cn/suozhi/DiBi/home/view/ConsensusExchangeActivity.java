package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.hide.model.TimerTextView;
import cn.suozhi.DiBi.home.adapter.ConsensusExchangeAdapter;
import cn.suozhi.DiBi.home.adapter.ConsensusExchangeEnAdapter;
import cn.suozhi.DiBi.home.adapter.HelpCenterAdapter;
import cn.suozhi.DiBi.home.adapter.IeoListAdapter;
import cn.suozhi.DiBi.home.model.BannerEntity;
import cn.suozhi.DiBi.home.model.BannerEntity2;
import cn.suozhi.DiBi.home.model.IeoEntity;
import cn.suozhi.DiBi.home.model.NotifyEntity;
import cn.suozhi.DiBi.home.model.TypeListEntity;

/**
 * 共识兑换列表页
 */
public class ConsensusExchangeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, OkHttpUtil.OnDataListener, OnCallbackListener,
        AbsRecyclerAdapter.OnItemClickListener, View.OnClickListener, TabLayout.BaseOnTabSelectedListener {

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar_centerBack)
    public ImageView ivBack;

    @BindView(R.id.iv_ce_banner)
    public ImageView ivBanner;

    private String bannerUrl;

    private int page = 1; // 当前页
    private int nextPage = 0; // 下一页
    private final int pageSize = 15; // 每页数量
    private ConsensusExchangeAdapter recyclerAdapter;
    private ConsensusExchangeEnAdapter recyclerAdapterE;
    private List<IeoEntity.DataBean.RecordsBean> dataList;

    private LoadingDialog loadingDialog;

    private Timer timer;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // (1) 使用handler发送消息
                    Message message=new Message();
                    message.what=0;
                    mHandler.sendMessage(message);
                }
            },0,1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        }
    };

    @Override
    protected int getViewResId() {
        return R.layout.activity_consensus_exchange;
    }

    @Override
    protected void init() {
        showLoading();
        lang = SharedUtil.getLanguage(this);

        int height = Util.getPhoneHeight(this) / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(lang.equals("en")){
            recyclerAdapterE = new ConsensusExchangeEnAdapter(this,height,this);
            recyclerView.setAdapter(recyclerAdapterE.setOnItemClickListener(this)
                    .setEmptyView(R.layout.empty_tips));
        } else {
            recyclerAdapter = new ConsensusExchangeAdapter(this, height,this);
            recyclerView.setAdapter(recyclerAdapter.setOnItemClickListener(this)
                    .setEmptyView(R.layout.empty_tips));
        }
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.color_1888FE);

        ivBack.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        getBanner();
        getIeo();
        mHandler.post(runnable);
    }

    private void getIeo(){
        OkHttpUtil.getJsonToken(Constant.URL.ieoList , SharedUtil.getToken(this) , this , "pageNum",page+"","pageSize",pageSize+"");
    }

    private void getBanner(){
        OkHttpUtil.getJsonToken(Constant.URL.GetBanner , SharedUtil.getToken(this) , this , "platform","2","category","6");
    }

    @Override
    public void onResponse(String url, String json, String session) {
        switch (url) {
            case Constant.URL.ieoList:
                IeoEntity ieoData = GsonUtil.fromJson(json,IeoEntity.class);
                if (ieoData == null) {
                    dismissLoading();
                    break;
                }
                if (page == 1) {
                    dismissLoading();
                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    } else {
                        dataList.clear();
                    }
                }
                removeLoading();
                if (Constant.Int.SUC == ieoData.getCode()) {
                    dataList.addAll(ieoData.getData().getRecords());
                    if (ieoData.getData().getCurrent() < ieoData.getData().getPages()) {//还有下一页
                        dataList.add(new IeoEntity.DataBean.RecordsBean(1));
                        nextPage = page + 1;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                if(lang.equals("en")){
                    recyclerAdapterE.setData(dataList);
                } else {
                    recyclerAdapter.setData(dataList);
                }
                break;
            case Constant.URL.GetBanner:
                BannerEntity2 bannerData = GsonUtil.fromJson(json,BannerEntity2.class);
                if (Constant.Int.SUC == bannerData.getCode()) {
                    if(bannerData.getData().size()>0){
                        bannerUrl = bannerData.getData().get(0).getBannerImgUrl();
                        Glide.with(this)
                                .load(bannerUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.mipmap.img_fail)
                                .placeholder(R.mipmap.img_loading)
                                .fallback(R.mipmap.img_fail)
                                .into(ivBanner);
                    } else {
                        Glide.with(this).load(getResources().getDrawable(R.mipmap.img_fail)).into(ivBanner);
                    }
                }
                break;
        }
    }

    // (2) 使用handler处理接收到的消息
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                if(lang.equals("en")){
                    recyclerAdapterE.notifyDataSetChanged();
                } else {
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    @Override
    protected void showLoading() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(this);
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void removeLoading() {
        if (dataList.size() > 0) {
            if (dataList.get(dataList.size() - 1).getLoadType() == 1) {
                dataList.remove(dataList.size() - 1);
            }
        }
    }

    private void addBaseLine() {
        if (page != 1) {
            dataList.add(new IeoEntity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public synchronized void onCallback() {
        if (nextPage == page + 1) {
            page++;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        showLoading();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_centerBack:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (v.getId() == R.id.tv_rce_button) {
            startActivity(new Intent(this, CEDActivity.class).putExtra("ieoID",dataList.get(position).getIeoId()));
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onDestroy() {
        // 将线程销毁掉
        mHandler.removeCallbacks(runnable);
        timer.cancel();
        super.onDestroy();
    }


}
