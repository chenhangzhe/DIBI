package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.NotifyRVAdapter;
import cn.suozhi.DiBi.home.model.NotifyEntity;

/**
 * 系统公告
 */
public class NotifyActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, OnCallbackListener, AbsRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    private int page, nextPage;//当前页 / 下一页
    private final int pageSize = 15;//每页数量
    private NotifyRVAdapter recyclerAdapter;
    private List<NotifyEntity.DataEntity.RecordsEntity> dataList;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_recycler;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.systemNotify), v -> onBackPressed());
        showLoading();
        lang = SharedUtil.getLanguage4Url(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new NotifyRVAdapter(this, this);
        recyclerView.setAdapter(recyclerAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        page = 1;
        nextPage = 1;
        getData();
    }

    private void getData() {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang,
                "pageNum", page + "", "pageSize", pageSize + "", "typeCode", "announcementCenter");
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Notify: " + json);
        NotifyEntity notify = gson.fromJson(json, NotifyEntity.class);
        if (page == 1) {
            dismissLoading();
            if (dataList == null) {
                dataList = new ArrayList<>();
            } else {
                dataList.clear();
            }
        }
        removeLoading();
        if (Constant.Int.SUC == notify.getCode()) {
            dataList.addAll(notify.getData().getRecords());
            if (notify.getData().getCurrent() < notify.getData().getPages()) {//还有下一页
                dataList.add(new NotifyEntity.DataEntity.RecordsEntity(1));
                nextPage = page + 1;
            } else {
                addBaseLine();
            }
        } else {
            addBaseLine();
        }
        recyclerAdapter.setData(dataList);
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
            if (dataList.get(dataList.size() - 1).getType() == 1) {
                dataList.remove(dataList.size() - 1);
            }
        }
    }

    private void addBaseLine() {
        if (page != 1) {
            dataList.add(new NotifyEntity.DataEntity.RecordsEntity(2));
        }
    }

    @Override
    public synchronized void onCallback() {
        if (nextPage == page + 1) {
            page++;
            getData();
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (v.getId() == R.id.cl_notifyItem) {
            startActivity(new Intent(this, NotifyDetailActivity.class)
                    .putExtra("id", dataList.get(position).getId()));
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
