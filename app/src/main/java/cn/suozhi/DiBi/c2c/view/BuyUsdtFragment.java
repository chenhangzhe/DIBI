package cn.suozhi.DiBi.c2c.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.adapter.BuyUsdtAdapter;
import cn.suozhi.DiBi.c2c.model.BuyAndSellUsdtEntity;
import cn.suozhi.DiBi.c2c.model.PayTypeEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.dialog.TradeKownDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.NotifyEntity;
import cn.suozhi.DiBi.home.view.BindPhoneActivity;
import cn.suozhi.DiBi.home.view.C1Activity;
import cn.suozhi.DiBi.home.view.C2Activity;
import cn.suozhi.DiBi.home.view.CollectMoneyActivity;
import cn.suozhi.DiBi.home.view.IdentityResultActivity;

public class BuyUsdtFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener
        , AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener, BaseDialog.OnItemClickListener, OnCallbackListener {

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;
    private BuyUsdtAdapter buyUsdtAdapter;

    private int type;
    private boolean isBindPhone;
    private int verifiedLevel;
    private int dialogType;
    private String payMode = "";//筛选条件  支付方式
    private String legalCurrencyCode = "";//筛选条件  法币类型
    private boolean isBussiness;
    //是否点击筛选数据
    private boolean isLoadData = true;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPage = START_INDEX;
    private List<BuyAndSellUsdtEntity.DataBean.RecordsBean> dataList;
    private LoadingDialog loadingDialog;
    //出售时 用户的支付方式
    private List<PayTypeEnity.DataBean> dataPaytype;
    private int verifiedstatus;
    private int idType;

    protected boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;
    private String tradeKnow;
    private int itemPos;
    private int userId;
    private boolean isShowKonw;

    public static BuyUsdtFragment getInstance(int type, boolean isBindPhone, int verifiedLevel, String legalCurrencyCode, String payMode, boolean isBussiness, int verifiedstatus, int idType, int userId) {
        BuyUsdtFragment fragment = new BuyUsdtFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putBoolean("isBindPhone", isBindPhone);
        args.putInt("userId", userId);
        args.putInt("verifiedLevel", verifiedLevel);
        args.putInt("verifiedstatus", verifiedstatus);
        args.putInt("idType", idType);
        args.putString("legalCurrencyCode", legalCurrencyCode);
        args.putString("payMode", payMode);
        args.putBoolean("isBussiness", isBussiness);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }


    /**
     * fragment被设置为不可见时调用
     */
    protected void onInvisible() {

    }

    /**
     * 这里获取数据，刷新界面
     */
    protected void initData() {
        mPage = START_INDEX;
        if (isLoadData) {
            getSellAndBuy(type == 1 ? "2" : "1");
            if (type != 1) {
                OkHttpUtil.getJsonToken(Constant.URL.getPayType, SharedUtil.getToken(getContext()), this);
            }
            isLoadData = false;
        }

    }

    /**
     * 懒加载
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initData();
        isFirst = false;
    }


    private void getKown() {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang, "typeCode", "tipsContent");
    }


    @Override
    protected int getViewResId() {
        return R.layout.fragment_buy_and_sell;
    }

    @Override
    protected void init(View v) {
        super.init(v);
        lang = SharedUtil.getLanguage4Url(getContext());
        type = getArguments().getInt("type");
        userId = getArguments().getInt("userId");
        verifiedLevel = getArguments().getInt("verifiedLevel");
        verifiedstatus = getArguments().getInt("verifiedstatus");
        idType = getArguments().getInt("idType");
        isBindPhone = getArguments().getBoolean("isBindPhone");
        legalCurrencyCode = getArguments().getString("legalCurrencyCode", "");
        payMode = getArguments().getString("payMode", "");
        isBussiness = getArguments().getBoolean("isBussiness");

        int height = Util.getPhoneHeight(getActivity()) / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        buyUsdtAdapter = new BuyUsdtAdapter(getActivity(), height, type, this);
        recyclerView.setAdapter(buyUsdtAdapter
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
        recyclerView.addItemDecoration(new DecoRecycler(getActivity(), R.drawable.deco_5_trans,
                DecoRecycler.Edge_Top | DecoRecycler.Edge_Bottom));
    }

    /**
     * 获取买卖列表
     */
    private void getSellAndBuy(String types) {
        showLoading();
        OkHttpUtil.getJsonToken(Constant.URL.sellAndBuyList, SharedUtil.getToken(getContext()), this,
                "legalCurrencyCode","CNY", // 法币类型CNY
                "baseCurrencyCode", TextUtils.isEmpty(legalCurrencyCode) ? "USDT" : legalCurrencyCode,
                "payMode", (TextUtils.isEmpty(payMode) || payMode.equals("0")) ? "" : payMode,//支付方式,查询全部为空即可
                "pageNum", mPage + "",//当前页
                "pageSize", DEFAULT_PAGE_SIZE + "",//每页的条数
                "type", types//广告类型[1出售|2购买]
        );
    }


    @Override
    public void onRefresh() {
        mPage = START_INDEX;
        getSellAndBuy(type == 1 ? "2" : "1");
    }

    @Override
    public void onItemClick(View v, int position) {
        itemPos = position;
        switch (v.getId()) {
            case R.id.tv_buy:
                //实名
                int ls = AppUtil.getIdentify(verifiedLevel, verifiedstatus);
                if (idType == 1 && ls < 6) {
                    dialogType = 12;
                    showAuthDialog(getString(R.string.c2cTradeNeedSenior), getString(R.string.str_go_auth),
                            getString(R.string.str_my_think_argin));
                    return;
                } else if (idType != 1 && ls < 5) {
                    dialogType = 12;
                    showAuthDialog(getString(R.string.str_c2c_trade_hint_two),
                            getString(R.string.str_go_auth), getString(R.string.str_my_think_argin));
                    return;
                }

                //绑定手机
                if (!isBindPhone) {
                    dialogType = 11;
                    showAuthDialog(getString(R.string.str_c2c_trade_hint), getString(R.string.str_go_bind), getString(R.string.str_my_think_argin));
                    return;
                }

                //收款方式
                if (type != 1) {
                    if (dataPaytype == null || dataPaytype.size() == 0) {
                        dialogType = 14;
                        showDialog(getString(R.string.str_bind_pay_hint));
                        return;
                    }

                    if (!isHavePaytype(buyUsdtAdapter.getData().get(position).getPayModes())) {
                        dialogType = 13;
                        showDialog(getString(R.string.str_bind_pay_hint));
                        return;
                    }
                }

                isShowKonw = SharedUtil.getBool(getContext(), "c2c", userId + "", false);
                if (!isShowKonw) {
                    getKown();
                } else {
                    toPlaceOrderActivity();
                }
                break;
        }
    }

    private boolean isHavePaytype(String payModes) {
        List<String> payModeList = new ArrayList<>();
        String[] split = payModes.split(",");

        for (int i = 0; i < split.length; i++) {
            payModeList.add(split[i]);
        }
        for (int i = 0; i < dataPaytype.size(); i++) {
            if (payModeList.contains(dataPaytype.get(i).getAccountType() + "")) {
                dataPaytype.get(i).setUseable(true);
            } else {
                dataPaytype.get(i).setUseable(false);
            }
        }

        for (int i = 0; i < dataPaytype.size(); i++) {
            if (dataPaytype.get(i).isUseable()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 买方确认转款和卖方 确认收款
     */
    public void showDialog(String contetn) {
        ConfirmDialog.newInstance(contetn, getString(R.string.thinkAgain),
                getString(R.string.str_go_bind))
                .setOnItemClickListener(this)
                .show(getActivity());
    }

    @Override
    public void onResponse(String url, String json, String session) {
        loadingDialog.dismiss();
        if (url.equals(Constant.URL.sellAndBuyList)) {
            BuyAndSellUsdtEntity enity = GsonUtil.fromJson(json, BuyAndSellUsdtEntity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                //第一次加载的时候
                if (mPage == 1) {
                    dismissLoading();
                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    } else {
                        dataList.clear();
                    }
                }
                removeLoading();
                if (Constant.Int.SUC == enity.getCode()) {
                    List<BuyAndSellUsdtEntity.DataBean.RecordsBean> records = enity.getData().getRecords();
                    dataList.addAll(records);
                    if (enity.getData().getCurrent() < enity.getData().getPages()) {//还有下一页
                        dataList.add(new BuyAndSellUsdtEntity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                        mPage++;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                buyUsdtAdapter.setData(dataList);
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        enity.getCode(), enity.getMsg()));
            }
        } else if (url.equals(Constant.URL.getPayType)) {
            //获取所有的支付方式
            PayTypeEnity enity = GsonUtil.fromJson(json, PayTypeEnity.class);
            if (enity.getCode() == Constant.Int.SUC) {
                dataPaytype = enity.getData();

            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        enity.getCode(), enity.getMsg()));
                Util.checkLogin(getContext(), enity.getCode());
            }
        } else if (url.equals(Constant.URL.GetNotify)) {
            NotifyEntity notify = GsonUtil.fromJson(json, NotifyEntity.class);
            if (Constant.Int.SUC == notify.getCode()) {
                tradeKnow = notify.getData().getRecords().get(0).getContent();
                //保存是否显示交易须知的提示
                SharedUtil.putBool(getContext(), "c2c", userId + "", true);
                if (!TextUtils.isEmpty(tradeKnow)) {
                    TradeKownDialog.newInstance(tradeKnow).setOnItemClickListener(new BaseDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v) {
                            if (v.getId() == R.id.tv_comfirm) {
                                toPlaceOrderActivity();
                            }
                        }
                    }).show(getActivity());
                }
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(getActivity(), url, error);
    }

    private void toPlaceOrderActivity() {
        startActivity(new Intent(getActivity(), PlaceOrderActivity.class)
                .putExtra("type", type)
                .putExtra("isBussiness", isBussiness)
                .putExtra("adid", buyUsdtAdapter.getData().get(itemPos).getAdId())
                .putExtra("legalCurrencyCode", buyUsdtAdapter.getData().get(itemPos).getLegalCurrencyCode())
                .putExtra("currencyCode", buyUsdtAdapter.getData().get(itemPos).getCurrencyCode())
        );
    }

    private void showAuthDialog(String content, String comfirm, String cancel) {
        AuthDialog.newInstance(content, comfirm, cancel)
                .setOnItemClickListener(this)
                .show(getActivity());
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_auth) {
            if (dialogType == 11) {
                //去绑定手机
                startActivity(new Intent(getActivity(), BindPhoneActivity.class));
            } else {
                //去认证
                int ls = AppUtil.getIdentify(verifiedLevel, verifiedstatus);
                if (ls == 1) {
                    startActivity(new Intent(getActivity(), C1Activity.class));
                } else {
                    if (idType == 1) {
                        if (ls >= 2 && ls <= 5) {
                            startActivity(new Intent(getActivity(), IdentityResultActivity.class)
                                    .putExtra("level", verifiedLevel)
                                    .putExtra("status", verifiedstatus)
                                    .putExtra("type", idType));
                        }
                    } else {
                        if (ls == 2 || ls == 4) {
                            startActivity(new Intent(getActivity(), C2Activity.class));
                        } else if (ls == 3) {
                            startActivity(new Intent(getActivity(), IdentityResultActivity.class)
                                    .putExtra("level", verifiedLevel)
                                    .putExtra("status", verifiedstatus)
                                    .putExtra("type", idType));
                        }
                    }
                }
            }
        } else if (v.getId() == R.id.tv_dgcConfirm) {
            startActivity(new Intent(getActivity(), CollectMoneyActivity.class));
        }
    }

    /**
     * 通过筛选刷新数据
     */
    public void update(String legalCurrencyCode, String payMode, int type) {
        this.legalCurrencyCode = legalCurrencyCode;
        this.payMode = payMode;
        this.type = type;
//        getSellAndBuy(type == 1 ? "2" : "1");
        onRefresh();
    }

    public void setParam(String legalCurrencyCode, String payMode, boolean isLoadData) {
        this.legalCurrencyCode = legalCurrencyCode;
        this.payMode = payMode;
        this.isLoadData = isLoadData;
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
        loadingDialog.dismiss();
        if (mPage != 1) {
            dataList.add(new BuyAndSellUsdtEntity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public void onCallback() {
        getSellAndBuy(type == 1 ? "2" : "1");
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
}
