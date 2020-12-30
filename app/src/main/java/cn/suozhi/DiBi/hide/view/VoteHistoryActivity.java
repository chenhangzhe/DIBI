package cn.suozhi.DiBi.hide.view;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.hide.adapter.VoteHistoryAdapter;
import cn.suozhi.DiBi.hide.model.VoteEntity;
import cn.suozhi.DiBi.home.adapter.ConsensusExchangeAdapter;
import cn.suozhi.DiBi.home.model.IeoEntity;
import cn.suozhi.DiBi.home.view.CEDActivity;

/**
 * 销毁记录
 */
public class VoteHistoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, OkHttpUtil.OnDataListener, OnCallbackListener,
        AbsRecyclerAdapter.OnItemClickListener, View.OnClickListener, TabLayout.BaseOnTabSelectedListener {

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar_centerBack)
    public ImageView ivBack;

    private int page = 1; // 当前页
    private int nextPage = 0; // 下一页
    private final int pageSize = 10; // 每页数量
    private List<VoteEntity.DataBean.RecordsBean> dataList;
    private VoteHistoryAdapter voteHistoryAdapter;

    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        return R.layout.activity_vote_history;
    }

    @Override
    protected void init() {
        showLoading();
        lang = SharedUtil.getLanguage(this);

        int height = Util.getPhoneHeight(this) / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        voteHistoryAdapter = new VoteHistoryAdapter(this, height ,this);
        recyclerView.setAdapter(voteHistoryAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.color_1888FE);

        ivBack.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        getData();
    }

    private void getData(){
        OkHttpUtil.getJsonToken(Constant.URL.voteList, SharedUtil.getToken(this), this,
                "pageNum",page+"",
                "pageSize",pageSize+"",
                "status","3");
    }

    @Override
    public void onResponse(String url, String json, String session) {
        switch (url) {
            case Constant.URL.voteList:
                VoteEntity entity = GsonUtil.fromJson(json,VoteEntity.class);
                if (entity == null) {
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
                if (Constant.Int.SUC == entity.getCode()) {
                    dataList.addAll(entity.getData().getRecords());
                    if (entity.getData().getCurrent() < entity.getData().getPages()) {//还有下一页
                        dataList.add(new VoteEntity.DataBean.RecordsBean(1));
                        nextPage = page + 1;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                voteHistoryAdapter.setData(dataList);
                break;
        }
    }

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
            dataList.add(new VoteEntity.DataBean.RecordsBean(2));
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
        if (v.getId() == R.id.ll_vote_history_item_p) {
            startActivity(new Intent(this, VoteHistoryDetailsActivity.class).putExtra("round",dataList.get(position).getRound()));
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
