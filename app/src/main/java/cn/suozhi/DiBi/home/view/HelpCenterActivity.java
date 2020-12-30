package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

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
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.HelpCenterAdapter;
import cn.suozhi.DiBi.home.model.NotifyEntity;
import cn.suozhi.DiBi.home.model.TypeListEntity;

/**
 * 帮助中心
 */
public class HelpCenterActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, OnCallbackListener, AbsRecyclerAdapter.OnItemClickListener,
        View.OnClickListener, TabLayout.BaseOnTabSelectedListener {

    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    private TabLayout tabLayout;

    private int page, nextPage;//当前页 / 下一页
    private final int pageSize = 15;//每页数量
    private HelpCenterAdapter recyclerAdapter;
    private List<NotifyEntity.DataEntity.RecordsEntity> dataList;
    private List<TypeListEntity.DataEntity> typeList;

    private int indexType = 0;
    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        return R.layout.activity_recycler_center;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.helpCenter), v -> onBackPressed());
        showLoading();
        lang = SharedUtil.getLanguage4Url(this);

        View head = LayoutInflater.from(this).inflate(R.layout.head_help, null);
        View foot = LayoutInflater.from(this).inflate(R.layout.foot_help, null);
        findWidget(head, foot);

        int height = Util.getPhoneHeight(this) / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new HelpCenterAdapter(this, height, this);
        recyclerView.setAdapter(recyclerAdapter.addHeaderView(head)
                .addFooterView(foot)
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    private void findWidget(View head, View foot) {
        ImageView ivBanner = head.findViewById(R.id.iv_helpHeadBanner);
        tabLayout = head.findViewById(R.id.tl_helpHeadType);
        head.findViewById(R.id.tv_helpHeadSearch).setOnClickListener(this);
        foot.findViewById(R.id.tv_helpFootSubmit).setOnClickListener(this);

        GlideUtil.glide(this, ivBanner, R.mipmap.head_help, GlideUtil.getOption(0,
                0, Util.dp2px(this, 5)));
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJsonHeader2(Constant.URL.GetTypeList, "", lang, this,
                "typeCode", "helpCenter");
    }

    private void loadHelp() {
        page = 1;
        nextPage = 1;
        getData();
    }

    private void getData() {
        String code;
        if (typeList == null || indexType < 0 || indexType >= typeList.size()) {
            code = "helpCenter";
        } else {
            code = typeList.get(indexType).getTypeCode();
        }
        OkHttpUtil.getJsonHeader2(Constant.URL.GetNotify, "", lang, this,
                "pageNum", page + "", "pageSize", pageSize + "", "typeCode", code);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e(TAG, "Help：" + url + json);
        switch (url) {
            case Constant.URL.GetTypeList:
                TypeListEntity type = JsonUtil.fromJsonO(json, TypeListEntity.class);
                if (type == null) {
                    dismissLoading();
                    break;
                }
                if (Constant.Int.SUC == type.getCode()) {
                    typeList = type.getData();
                    initTab();
                    loadHelp();
                } else {
                    dismissLoading();
                }
                break;
            case Constant.URL.GetNotify://问题列表
                NotifyEntity help = JsonUtil.fromJsonO(json, NotifyEntity.class);
                if (help == null) {
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
                if (Constant.Int.SUC == help.getCode()) {
                    dataList.addAll(help.getData().getRecords());
                    if (help.getData().getCurrent() < help.getData().getPages()) {//还有下一页
                        dataList.add(new NotifyEntity.DataEntity.RecordsEntity(1));
                        nextPage = page + 1;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                recyclerAdapter.setData(dataList);
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

    private void initTab() {
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.removeAllTabs();

        if (typeList == null) {
            return;
        }
        int w = (Util.getPhoneWidth(this) - Util.dp2px(this, 60)) / 4;
        for (int i = 0; i < typeList.size(); i++) {
            View tab = LayoutInflater.from(this).inflate(R.layout.tab_12_solid_white_gy_fd, null);
            TextView name = tab.findViewById(R.id.tv_tab12SolidWgName);
            name.setText(typeList.get(i).getTypeName());
            name.setMinWidth(w);
            tab.findViewById(R.id.v_tab12SolidWg0).setVisibility(i == 0 ? View.VISIBLE : View.GONE);
            tab.findViewById(R.id.v_tab12SolidWg1).setVisibility(i == typeList.size() - 1 ? View.VISIBLE : View.GONE);
            tabLayout.addTab(tabLayout.newTab().setCustomView(tab), i == indexType);
        }
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        indexType = tab.getPosition();
        showLoading();
        loadHelp();
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
            case R.id.tv_helpHeadSearch:
                startActivity(new Intent(this, HelpSearchActivity.class)
                        .putExtra("fromHelp", true));
                break;
            case R.id.tv_helpFootSubmit:
                startActivity(new Intent(this, SubmitOrderWorkActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (v.getId() == R.id.cl_helpItem) {
            startActivity(new Intent(this, HelpCenterDetailActivity.class)
                    .putExtra(Constant.Strings.Intent_Id, dataList.get(position).getId()));
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
