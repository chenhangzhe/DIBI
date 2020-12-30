package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.eventbus.EventBusHelper;
import cn.suozhi.DiBi.common.eventbus.MessageEvent;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.WorkOrderAdapter;
import cn.suozhi.DiBi.home.model.WorkOrderListEntity;

/**
 * 功能描述：已回复
 */
public class RepliyFragment extends BaseFragment implements
        OnCallbackListener, AbsRecyclerAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, OkHttpUtil.OnDataListener {


    @BindView(R.id.rv_item)
    RecyclerView rvItem;
    @BindView(R.id.srl_item)
    SwipeRefreshLayout srlItem;
    private int type = 1;
    private WorkOrderAdapter workOrderAdapter;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPage = START_INDEX;
    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();
    private List<WorkOrderListEntity.DataBean.RecordsBean> dataList;


    public static RepliyFragment create(int type) {
        RepliyFragment fragment = new RepliyFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.Strings.WORK_ORDER_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(View v) {
        showLoading();
        type = getArguments().getInt(Constant.Strings.WORK_ORDER_TYPE);
        workOrderAdapter = new WorkOrderAdapter(getContext(), this);
        rvItem.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItem.setAdapter(workOrderAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
        srlItem.setOnRefreshListener(this);
        srlItem.setColorSchemeResources(R.color.colorAccent);

    }

    @Override
    protected void loadData() {
        //加载
        OkHttpUtil.getJsonToken(Constant.URL.WorkOrderList, SharedUtil.getToken(getContext()), this,
                "pageNum", mPage + "", "pageSize", DEFAULT_PAGE_SIZE + "", "status", type + "");

    }

    @Override
    protected int getViewResId() {
        return R.layout.fragment_reply;
    }

    @Override
    public void onCallback() {
        showLoading();
        loadData();
    }

    @Override
    public void onItemClick(View v, int position) {
        WorkOrderListEntity.DataBean.RecordsBean bean = (WorkOrderListEntity.DataBean.RecordsBean) workOrderAdapter.getData().get(position);
        Intent intent = new Intent(getContext(), WorkOrderDetailActivity.class);
        intent.putExtra("workOrderId", bean.getWorkSheetId());
        startActivity(intent);


    }

    @Override
    public void onRefresh() {
        showLoading();
        mPage = START_INDEX;
        loadData();

    }

    @Override
    public void onResponse(String url, String json, String session) {

        Log.e("TAG", "返回数据：" + json);

        dismissLoading();
        if (mPage == 1) {//如果是刷新
            if (dataList == null) {
                dataList = new ArrayList<>();
            } else {
                dataList.clear();
            }
        }
        //这种只有加载更多的时候才走这个 removeLoading
        removeLoading();
        WorkOrderListEntity entity = gson.fromJson(json, WorkOrderListEntity.class);
        if (Constant.Int.SUC == entity.getCode()) {
            dataList.addAll(entity.getData().getRecords());
            if (entity.getData().getCurrent() < entity.getData().getPages()) {//还有下一页
                dataList.add(new WorkOrderListEntity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                mPage++;
            } else {
                addBaseLine();
            }
        } else {
            addBaseLine();
        }
        workOrderAdapter.setData(dataList);


    }

    @Override
    public void onFailure(String url, String error) {

        dismissLoading();
        Util.saveLog(getActivity(), url, error);
    }


    @Override
    protected void showLoading() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getActivity());
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (srlItem != null && srlItem.isRefreshing()) {
            srlItem.setRefreshing(false);
        }
    }

    private void removeLoading() {
        if (dataList.size() > 0) {
            if (dataList.get(dataList.size() - 1).getViewType() == 1) {
                dataList.remove(dataList.size() - 1);
            }
        }

    }

    private void addBaseLine() {
        if (mPage != 1) {
            dataList.add(new WorkOrderListEntity.DataBean.RecordsBean(2));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        Log.w("TAG", getClass().getSimpleName() + " receive msg : " + event.getMsg());
        switch (event.getMsg()) {
            case Constant.Strings.REFRESH_WORK_REDER:
                //刷新
                mPage = START_INDEX;
                loadData();

                break;
            default:
                break;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusHelper.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }

}
