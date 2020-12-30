package cn.suozhi.DiBi.home.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.MsgListAdapter;
import cn.suozhi.DiBi.home.model.MsgListEntity;

/**
 * 站内信
 */
public class MessageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, AbsRecyclerAdapter.OnItemClickListener, OnCallbackListener {

    @BindView(R.id.iv_mineBack)
    ImageView ivMineBack;
    @BindView(R.id.tv_msg_title)
    TextView tvMsgTitle;
    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.srl_msg)
    SwipeRefreshLayout refreshLayout;
    private MsgListAdapter msgListAdapter;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPage = START_INDEX;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();
    private List<MsgListEntity.DataBean.RecordsBean> dataList;

    @Override
    protected int getViewResId() {
        return R.layout.activity_message;
    }

    @Override
    protected void init() {
        showLoading();
        lang = SharedUtil.getLanguage4Url(this);
        msgListAdapter = new MsgListAdapter(this, this);
        rvMsg.setLayoutManager(new LinearLayoutManager(this));
        rvMsg.setAdapter(msgListAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJsonHeader2(Constant.URL.MsgList, SharedUtil.getToken(this), lang,
                this, "pageNum", mPage + "", "pageSize", DEFAULT_PAGE_SIZE + "");
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e(TAG, "Message: " + json);
        switch (url) {
            case Constant.URL.UpdateRead:
                break;
            case Constant.URL.MsgList:
                if (mPage == 1) {//如果是刷新
                    dismissLoading();
                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    } else {
                        dataList.clear();
                    }
                }
                //这种只有加载更多的时候才走这个 removeLoading
                removeLoading();
                MsgListEntity entity = gson.fromJson(json, MsgListEntity.class);
                if (Constant.Int.SUC == entity.getCode()) {
                    dataList.addAll(entity.getData().getRecords());
                    if (entity.getData().getCurrent() < entity.getData().getPages()) {//还有下一页
                        dataList.add(new MsgListEntity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                        mPage++;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                msgListAdapter.setData(dataList);
                break;
        }
    }

    @Override
    public void onFailure(String url, String error) {
        switch (url) {
            case Constant.URL.UpdateRead:
                break;
            case Constant.URL.MsgList:
                dismissLoading();
                break;
        }
        Util.saveLog(this, url, error);
    }

    @OnClick(R.id.iv_mineBack)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onItemClick(View v, int position) {
        //判断是否是未读，是未读就修改为已读
        MsgListEntity.DataBean.RecordsBean recordsBean = dataList.get(position);
        if (recordsBean!=null){
            if (recordsBean.getStatus() == 1) {//已读（2）未读（1），
                OkHttpUtil.postJsonToken(Constant.URL.UpdateRead, SharedUtil.getToken(this),
                        this, "id", recordsBean.getId() + "");
            }
        }
        msgListAdapter.setExpandPosition(position);
        msgListAdapter.setRedPoint(position);
    }

    @Override
    public void onRefresh() {
        mPage = START_INDEX;
        loadData();
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
        if (mPage != 1) {
            dataList.add(new MsgListEntity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public void onCallback() {
        loadData();
    }
}
