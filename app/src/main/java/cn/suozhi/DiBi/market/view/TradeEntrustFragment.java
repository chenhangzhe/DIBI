package cn.suozhi.DiBi.market.view;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.market.adapter.TradeEntrustRVAdapter;
import cn.suozhi.DiBi.market.model.OrderEntity;

/**
 * 币币-当前委托
 */
public class TradeEntrustFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RadioGroup.OnCheckedChangeListener, AbsRecyclerAdapter.OnItemClickListener, OnCallbackListener,
        View.OnClickListener {

    @BindView(R.id.rg_teType)
    public RadioGroup radioGroup;

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    private int page, nextPage;//当前页 / 下一页
    private String type;
    private TradeEntrustRVAdapter recyclerAdapter;
    private List<OrderEntity> dataList;
    private boolean hasLoading = true;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_trade_entrust;
    }

    @Override
    protected void init(View v) {
        type = "";
        radioGroup.getChildAt(0).performClick();
        radioGroup.setOnCheckedChangeListener(this);

        int emptyTips = SharedUtil.isLogin(getActivity()) ? R.string.emptyData : R.string.login;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new TradeEntrustRVAdapter(getActivity(), this);
        recyclerView.setAdapter(recyclerAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips)
                .setEmptyTips(R.id.tv_emptyTips, emptyTips)
                .setEmptyClickListener(R.id.tv_emptyTips, this));
        recyclerView.addItemDecoration(new DecoRecycler(getActivity(), R.drawable.deco_5_trans,
                DecoRecycler.Edge_Top | DecoRecycler.Edge_Bottom));

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
        if (loadListener != null && recyclerAdapter != null) {
            if (SharedUtil.isLogin(getActivity())) {
                recyclerView.setTag(R.id.tag_relation, page);
                loadListener.onLoad(Constant.Code.Type_Current_Entrust, Constant.Code.Trade_Current_Entrust,
                        recyclerView, type, hasLoading ? "L" : null);
            } else {
                dismissLoading();
                recyclerAdapter.setData(new ArrayList<>());
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_teAll:
                type = "";
                refresh();
                break;
            case R.id.rb_teBuy:
                type = "B";
                refresh();
                break;
            case R.id.rb_teSell:
                type = "S";
                refresh();
                break;
        }
    }

    public void updateOrder(Messages.OrderResponse o) {
        if (page == 1) {
            dismissLoading();
            if (dataList == null) {
                dataList = new ArrayList<>();
            } else {
                dataList.clear();
            }
        }
        removeLoading();
        if (Constant.Int.SUC_S.equals(o.getCode())) {
            List<Messages.DmOrder> list = o.getOrdersList();
            Symbol symbol = loadListener == null ? null : loadListener.getSymbol();
            for (int i = 0; i < list.size(); i++) {
                dataList.add(AppUtil.getOrder(list.get(i), symbol));
            }
            if (o.getCurrentPage() < o.getTotalPage()) {//还有下一页
                dataList.add(new OrderEntity(1));
                nextPage = page + 1;
            } else {
                addBaseLine();
            }
        } else {
            addBaseLine();
        }
        recyclerAdapter.setData(dataList);
    }

    public void updateOrder() {
        dismissLoading();
        if (dataList == null) {
            dataList = new ArrayList<>();
        } else {
            removeLoading();
        }
        recyclerAdapter.setData(dataList);
    }

    public void updateCancel() {
        refresh();
    }

    @Override
    protected void dismissLoading() {
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
            dataList.add(new OrderEntity(2));
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
        if (v.getId() == R.id.tv_teItemRetreat) {//撤单
            if (loadListener != null) {
                recyclerView.setTag(R.id.tag_relation, dataList.get(position).getOrderId());
                loadListener.onLoad(Constant.Code.Type_Cancel_Current, Constant.Code.Trade_Cancel_Current,
                        recyclerView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_emptyTips && !SharedUtil.isLogin(getActivity())) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public void transmit(String info) {
        dismissLoading();
    }

    public void refresh() {
        hasLoading = true;
        loadData();
    }

    @Override
    public void onRefresh() {
        hasLoading = false;
        loadData();
    }
}
