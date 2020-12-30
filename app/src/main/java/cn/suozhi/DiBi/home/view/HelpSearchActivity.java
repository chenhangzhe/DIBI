package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.HelpCenterAdapter;
import cn.suozhi.DiBi.home.model.NotifyEntity;

/**
 * 帮助中心搜索
 */
public class HelpSearchActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, OnCallbackListener, AbsRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.et_hsSearch)
    public EditText etSearch;
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    private int page, nextPage;//当前页 / 下一页
    private final int pageSize = 15;//每页数量
    private HelpCenterAdapter recyclerAdapter;
    private List<NotifyEntity.DataEntity.RecordsEntity> dataList;

    private boolean fromHelp;
    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        return R.layout.activity_help_search;
    }

    @Override
    protected void init() {
        showLoading();
        lang = SharedUtil.getLanguage4Url(this);
        fromHelp = getIntent().getBooleanExtra("fromHelp", false);
        String key = getIntent().getStringExtra(Constant.Strings.Intent_Key);
        etSearch.setText(key);
        if (TextUtils.isEmpty(key)) {
            etSearch.requestFocus();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new HelpCenterAdapter(this, this);
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
        if (etSearch.length() == 0) {
            dismissLoading();
            return;
        }
        Util.hideKeyboard(etSearch);
        etSearch.clearFocus();
        OkHttpUtil.getJsonHeader2(Constant.URL.SearchHelp, "", lang, this,
                "keyWords", etSearch.getText().toString(), "pageNum", page + "",
                "pageSize", pageSize + "", "typeCode", "helpCenter");
    }

    @Override
    public void onResponse(String url, String json, String session) {
        switch (url) {
            case Constant.URL.SearchHelp://搜索结果
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

    @OnClick({R.id.iv_hsBack, R.id.tv_hsSearch})
    public void search(View v) {
        switch (v.getId()) {
            case R.id.iv_hsBack:
                if (fromHelp) {
                    onBackPressed();
                } else {
                    startActivity(new Intent(this, HelpCenterActivity.class));
                    finish();
                }
                break;
            case R.id.tv_hsSearch:
                Util.editRemoveIllegal(etSearch);
                if (etSearch.length() > 0) {
                    showLoading();
                    loadData();
                } else {
                    etSearch.requestFocus();
                }
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

    @OnEditorAction(R.id.et_hsSearch)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            Util.editRemoveIllegal(etSearch);
            Util.hideKeyboard(etSearch);
            if (etSearch.length() > 0) {
                showLoading();
                loadData();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
