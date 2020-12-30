package cn.suozhi.DiBi.c2c.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.adapter.MyAdvanceAdapter;
import cn.suozhi.DiBi.c2c.model.AdvaneEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.StringEntity;

public class MyAdvanceFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener, OnCallbackListener {
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;

    @BindView(R.id.rv_advance)
    public RecyclerView rvAdvance;


    @BindView(R.id.tv_all)
    TextView tvAll;

    @BindView(R.id.tv_buy)
    TextView tvBuy;

    @BindView(R.id.tv_sell)
    TextView tvSell;

    private int height;
    private MyAdvanceAdapter advanceAdapter;
    //广告类型[1买|2卖],查询全部为空即可
    private String adType = "";
    private String adId;
    private boolean isCommiting;
    private LoadingDialog loadingDialog;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPage = START_INDEX;
    private List<AdvaneEnity.DataBean.RecordsBean> dataList;

    public static MyAdvanceFragment getInstance(boolean isBussiness) {
        MyAdvanceFragment fragment = new MyAdvanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getViewResId() {
        return R.layout.fragment_my_advance;
    }

    @Override
    protected void init(View v) {
        super.init(v);
        height = Util.getPhoneHeight(getActivity()) / 2;
        rvAdvance.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        advanceAdapter = new MyAdvanceAdapter(getActivity(), height, this);
        rvAdvance.setAdapter(advanceAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        tvAll.setSelected(true);
    }

    @Override
    protected void loadData() {
        super.loadData();
        mPage = START_INDEX;
        getMyAdvance(true);
    }

    /**
     * 广告上架
     *
     * @param advanceID
     */
    private void advanceUp(int advanceID) {
        showLoading();
        OkHttpUtil.putJsonToken(Constant.URL.advanceUp, SharedUtil.getToken(getContext()), this, "adId", advanceID + "");
    }

    /**
     * 广告下架
     *
     * @param advanceID
     */
    private void advanceDown(int advanceID) {
        showLoading();
        OkHttpUtil.putJsonToken(Constant.URL.advanceDown, SharedUtil.getToken(getContext()), this, "adId", advanceID + "");
    }


    @OnClick({R.id.tv_all, R.id.tv_buy, R.id.tv_sell})
    public void advanceClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all:
                adType = "";
                mPage = START_INDEX;
                showSelectedTag(true, false, false);
                getMyAdvance(true);
                break;
            case R.id.tv_buy:
                adType = "1";
                mPage = START_INDEX;
                showSelectedTag(false, true, false);
                getMyAdvance(true);
                break;
            case R.id.tv_sell:
                adType = "2";
                mPage = START_INDEX;
                showSelectedTag(false, false, true);
                getMyAdvance(true);
                break;
        }
    }

    private void showSelectedTag(boolean isAll, boolean isBuy, boolean isSell) {
        tvAll.setSelected(isAll);
        tvBuy.setSelected(isBuy);
        tvSell.setSelected(isSell);
    }


    @Override
    public void onRefresh() {
        mPage = START_INDEX;
        getMyAdvance(true);
    }

    @Override
    public void onItemClick(View v, int position) {
        adId = advanceAdapter.getData().get(position).getAdNo();
        int status = advanceAdapter.getData().get(position).getStatus();
        int advanceID = advanceAdapter.getData().get(position).getAdId();
        if (v.getId() == R.id.tv_up_down) {
            if (status == 1) {
                if (!isCommiting) {
                    isCommiting = true;
                    advanceDown(advanceID);
                }
            } else if (status == 2) {
                if (!isCommiting) {
                    isCommiting = true;
                    advanceUp(advanceID);
                }
            }
        } else if (v.getId() == R.id.tv_edit) {
            if (status == 1 || status == 2) {
                startActivity(new Intent(getContext(), PublishAdvanceActivity.class).putExtra("adId", advanceAdapter.getData().get(position).getAdId() + ""));
            }
        }
    }

    /**
     * 获取我的广告
     */
    private void getMyAdvance(boolean isShowLoding) {
        if (isShowLoding) {
            showLoading();
        }
        OkHttpUtil.getJsonToken(Constant.URL.myAdvance, SharedUtil.getToken(getContext()), this,
                "currencyCode ", "",//币种编号,查询全部为空即可
                "status", "",//状态[1上架|2下架|3已完成],查询全部为空即可
                "pageNum", mPage + "",//当前页
                "pageSize", DEFAULT_PAGE_SIZE + "",//每页的条数
                "type", adType//广告类型[1买|2卖],查询全部为空即可
        );
    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        if (url.equals(Constant.URL.myAdvance)) {
            AdvaneEnity enity = GsonUtil.fromJson(json, AdvaneEnity.class);

            //第一次加载的时候
            if (mPage == 1) {
                if (dataList == null) {
                    dataList = new ArrayList<>();
                } else {
                    dataList.clear();
                }
            }

            removeLoading();

            if (Constant.Int.SUC == enity.getCode()) {
                List<AdvaneEnity.DataBean.RecordsBean> records = enity.getData().getRecords();
                dataList.addAll(records);
                if (enity.getData().getCurrent() < enity.getData().getPages()) {//还有下一页
                    dataList.add(new AdvaneEnity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                    mPage++;
                } else {
                    addBaseLine();
                }
            } else {
                addBaseLine();
            }
            advanceAdapter.setData(dataList);

        } else if (url.equals(Constant.URL.advanceUp)) {
            //上架
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                mPage = START_INDEX;
                getMyAdvance(true);
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(getContext(), entity.getCode());
            }
        } else if (url.equals(Constant.URL.advanceDown)) {
            //下架
            isCommiting = false;
            StringEntity entity = GsonUtil.fromJson(json, StringEntity.class);
            if (entity.getCode() == Constant.Int.SUC) {
                mPage = START_INDEX;
                getMyAdvance(true);
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(getContext(), entity.getCode());
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {
        isCommiting = false;
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
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }


    /**
     * 加载更多
     */
    private void removeLoading() {
        if (dataList.size() > 0) {
            if (dataList.get(dataList.size() - 1).getLoadType() == 1) {
                dataList.remove(dataList.size() - 1);
            }
        }

    }

    private void addBaseLine() {
        if (mPage != 1) {
            dataList.add(new AdvaneEnity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public void onCallback() {
        getMyAdvance(false);
    }

}
