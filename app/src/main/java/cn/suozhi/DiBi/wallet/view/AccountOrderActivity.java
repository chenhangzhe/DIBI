package cn.suozhi.DiBi.wallet.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.wallet.adapter.AccountOrderAdapter;
import cn.suozhi.DiBi.wallet.adapter.FlowAdapter;
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;
import cn.suozhi.DiBi.wallet.model.FlowEntity;

/**
 * 账单界面（充提记录）
 */
public class AccountOrderActivity extends BaseActivity implements AbsRecyclerAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, OkHttpUtil.OnDataListener, View.OnClickListener, OnCallbackListener {

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView rvRecord;

    @BindView(R.id.rv_get)
    public RecyclerView rvGet;

    @BindView(R.id.rv_flow)
    public RecyclerView rvFlow;

    private AccountOrderAdapter accountOrderAdapter;
    private FlowAdapter flowAdapter;
    private LinearLayout llySearchCoin;
    private TextView tvCharge;
    private View chargeDivider;
    private TextView tvGet;
    private View getDivider;
    private String coin = "";
    private boolean isCharge = true;
    private boolean isGet = false;
    private boolean isFlow = false;
    private TextView tvFlow;
    private View flowDivider;
    //标识当前是哪个类型的订单
    private int status = 1; // 1充2提3资产
    private List<AccountRecordEnity.DataBean.RecordsBean> records;
    private List<AccountRecordEnity.DataBean.RecordsBean> dataList;
    private List<AccountRecordEnity.DataBean.RecordsBean> dataList2;
    private List<FlowEntity.DataBean.RecordsBean> flowDataList;
    private AccountOrderAdapter accountOrderGetAdapter;
    private int height;
    private RelativeLayout back;
    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPageGet = START_INDEX;
    private int mPageCharge = START_INDEX;
    private int mPageFlow = START_INDEX;
    private static final int DEFAULT_PAGE_SIZE_FLOW = 10;
    private TextView tvTitle;

    @Override
    protected int getViewResId() {
        return R.layout.activity_account_order;
    }

    @Override
    protected void init() {
        status = getIntent().getIntExtra("status", 1);
//        View header = LayoutInflater.from(mContext).inflate(R.layout.acount_order_header, null);
        initHeader();
        height = Util.getPhoneHeight(mContext) / 2;

//        rvCoin.addItemDecoration(new DecoRecycler(getActivity(), R.drawable.deco_15_trans_f5f5f8,
//                DecoRecycler.Edge_Except_Left,false));

        rvRecord.setLayoutManager(new LinearLayoutManager(mContext));

        if (SharedUtil.getLanguage(mContext).equals("en")) {
            tvCharge.setTextSize(11);
            tvTitle.setTextSize(22);
            tvGet.setTextSize(11);
            tvFlow.setTextSize(11);
        } else {
            tvCharge.setTextSize(15);
            tvTitle.setTextSize(28);
            tvGet.setTextSize(15);
            tvFlow.setTextSize(15);
        }

        if (status == 1) {
            createChargeAdapter();
            showCharge();
        } else if (status == 2) {
            createGetAdapter();
            showGet();
        } else if (status == 3) {
            createFlowAdapter();
            showFlow();
        }

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);

    }

    private void createGetAdapter() {
        rvGet.setLayoutManager(new LinearLayoutManager(mContext));
        accountOrderGetAdapter = new AccountOrderAdapter(mContext, height, 2, this);
        rvGet.setAdapter(accountOrderGetAdapter
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    private void createChargeAdapter() {
        accountOrderAdapter = new AccountOrderAdapter(mContext, height, 1, this);
        rvRecord.setAdapter(accountOrderAdapter
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    private void createFlowAdapter() {
        rvFlow.setLayoutManager(new LinearLayoutManager(mContext));
        flowAdapter = new FlowAdapter(mContext, height, 1, this);
        rvFlow.setAdapter(flowAdapter
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    @Override
    protected void loadData() {
        records = new ArrayList<>();
        if (status == 1) {
            mPageCharge = START_INDEX;
            getRecords(coin);
        } else if(status == 2) {
            mPageGet = START_INDEX;
            getRecordGet(coin);
        } else if (status == 3){
            mPageFlow = START_INDEX;
            getRecordFlow(coin);
        }
    }

    /**
     * 获取充币记录
     */
    private void getRecords(String coin) {
        //类型 D 充币 W 提币
        OkHttpUtil.getJsonToken(Constant.URL.accountOrder, SharedUtil.getToken(mContext), this,
                "currency", coin, "pageNum", mPageCharge + "", "pageSize", DEFAULT_PAGE_SIZE + "", "type", "D");
    }

    /**
     * 获取提币记录
     */
    private void getRecordGet(String coin) {
        //类型 D 充币 W 提币
        OkHttpUtil.getJsonToken(Constant.URL.accountOrder, SharedUtil.getToken(mContext), this,
                "currency", coin, "pageNum", mPageGet + "", "pageSize", DEFAULT_PAGE_SIZE + "", "type", "W");
    }

    /**
     * 获取资产记录
     */
    private void getRecordFlow(String coin) {
        OkHttpUtil.getJsonToken(Constant.URL.flowList, SharedUtil.getToken(mContext), this,
                "currency", coin, "pageNum", mPageFlow + "", "pageSize", DEFAULT_PAGE_SIZE_FLOW + "");
    }

    private void initHeader() {
        back = findViewById(R.id.rly_back);
        llySearchCoin = findViewById(R.id.lly_search_coin);
        tvCharge = findViewById(R.id.tv_charge);
        tvTitle = findViewById(R.id.tv_title);
        chargeDivider = findViewById(R.id.v_charge_divider);
        tvGet = findViewById(R.id.tv_get);
        getDivider = findViewById(R.id.v_get_divider);
        EditText etCoin = findViewById(R.id.et_coin);
        // 资产记录
        tvFlow = findViewById(R.id.tv_flow);
        flowDivider = findViewById(R.id.v_flow);

        tvCharge.setOnClickListener(this);
        tvGet.setOnClickListener(this);
        tvFlow.setOnClickListener(this);
        back.setOnClickListener(this);

        etCoin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    coin = etCoin.getText().toString().trim().toUpperCase();
                    if (status == 1) {
                        mPageCharge = START_INDEX;
                        getRecords(coin);
                    } else if (status == 2) {
                        mPageGet = START_INDEX;
                        getRecordGet(coin);
                    } else if (status == 3){
                        mPageFlow = START_INDEX;
                        getRecordFlow(coin);
                    }
                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    //如果显示则隐藏，如果隐藏则显示
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (v.getId() == R.id.tv_detail) {
            if (status == 1) {
                List<AccountRecordEnity.DataBean.RecordsBean> data = accountOrderAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (position == i) {
                        if (data.get(i).isExpand()) {
                            data.get(i).setExpand(false);
                        } else {
                            data.get(i).setExpand(true);
                        }

                    }
                }
                accountOrderAdapter.notifyDataSetChanged();
            } else {
                List<AccountRecordEnity.DataBean.RecordsBean> data = accountOrderGetAdapter.getData();

                for (int i = 0; i < data.size(); i++) {
                    if (position == i) {
                        if (data.get(i).isExpand()) {
                            data.get(i).setExpand(false);
                        } else {
                            data.get(i).setExpand(true);
                        }
                    }
                }
                accountOrderGetAdapter.notifyDataSetChanged();
            }
        } else if (v.getId() == R.id.tv_look) {
            //点击查看
            if (status == 1) {
                String explorer = accountOrderAdapter.getData().get(position).getBlockchainExplorer();
                String txid = accountOrderAdapter.getData().get(position).getBlockchainTxId();

                if (!TextUtils.isEmpty(txid)) {
                    toBrower(explorer);
                }

            } else {
                String explorer = accountOrderGetAdapter.getData().get(position).getBlockchainExplorer();
                String txid = accountOrderGetAdapter.getData().get(position).getBlockchainTxId();
                if (!TextUtils.isEmpty(txid)) {
                    toBrower(explorer);
                }
            }
        }


    }

    /**
     * 打开浏览器的指定页面
     */
    private void toBrower(String url) {
        if (Util.isLink(url)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    @Override
    protected void dismissLoading() {
        super.dismissLoading();
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        dismissLoading();
        loadData();
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.accountOrder)) {
            if (status == 1) {
                //充币
                AccountRecordEnity enity = GsonUtil.fromJson(json, AccountRecordEnity.class);
                //第一次加载的时候
                if (mPageCharge == 1) {
                    dismissLoading();
                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    } else {
                        dataList.clear();
                    }
                }

                removeLoading();

                if (Constant.Int.SUC == enity.getCode()) {
                    dataList.addAll(enity.getData().getRecords());
                    if (enity.getData().getCurrent() < enity.getData().getPages()) {//还有下一页
                        dataList.add(new AccountRecordEnity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                        mPageCharge++;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                accountOrderAdapter.setData(dataList);
            } else if (status == 2) {
                //提币
                AccountRecordEnity enity = GsonUtil.fromJson(json, AccountRecordEnity.class);
                //第一次加载的时候
                if (mPageGet == 1) {
                    dismissLoading();
                    if (dataList2 == null) {
                        dataList2 = new ArrayList<>();
                    } else {
                        dataList2.clear();
                    }
                }

                removeLoadingGet();

                if (Constant.Int.SUC == enity.getCode()) {
                    dataList2.addAll(enity.getData().getRecords());
                    if (enity.getData().getCurrent() < enity.getData().getPages()) {//还有下一页
                        dataList2.add(new AccountRecordEnity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                        mPageGet++;
                    } else {
                        addBaseLineGet();
                    }
                } else {
                    addBaseLineGet();
                }

                accountOrderGetAdapter.setData(dataList2);
            }
        }
        // 资产记录
        else if (url.equals(Constant.URL.flowList)){
            if (status == 3){
                // 资产记录
                FlowEntity fe = GsonUtil.fromJson(json , FlowEntity.class);
                //第一次加载的时候
                if (mPageFlow == 1) {
                    dismissLoading();
                    if (flowDataList == null) {
                        flowDataList = new ArrayList<>();
                    } else {
                        flowDataList.clear();
                    }
                }
                removeLoadingFlow();
                if (Constant.Int.SUC == fe.getCode()) {
                    flowDataList.addAll(fe.getData().getRecords());
                    if (fe.getData().getCurrent() < fe.getData().getPages()) {//还有下一页
                        flowDataList.add(new FlowEntity.DataBean.RecordsBean(1)); //这里是把正在加载显示出来
                        mPageFlow++;
                    } else {
                        addBaseLineFlow();
                    }
                } else {
                    addBaseLineFlow();
                }
                flowAdapter.setData(flowDataList);
            }
        }

    }

    @Override
    public void onFailure(String url, String error) {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_charge:
                isGet = false;
                status = 1;
                showCharge();
                if (!isCharge) {
                    if (accountOrderAdapter == null) {
                        createChargeAdapter();
                    }
                    mPageCharge = START_INDEX;
                    getRecords(coin);
                }
                isCharge = true;
                break;
            case R.id.tv_get:
                isCharge = false;
                status = 2;
                showGet();
                //避免重复请求
                if (!isGet) {
                    if (accountOrderGetAdapter == null) {
                        createGetAdapter();
                    }
                    mPageGet = START_INDEX;
                    getRecordGet(coin);
                }
                isGet = true;
                break;
            case R.id.tv_flow:
                isFlow = false;
                status = 3;
                showFlow();
                //避免重复请求
                if (!isFlow) {
                    if (flowAdapter == null) {
                        createFlowAdapter();
                    }
                    mPageFlow = START_INDEX;
                    getRecordFlow(coin);
                }
                isFlow = true;
                break;
            case R.id.rly_back:
                finish();
                break;
        }
    }

    private void showCharge() {
//        tvCharge.setTextColor(ResUtils.getColor(R.color.purple77));
        tvCharge.setTextColor(ResUtils.getColor(R.color.color_1888FE));
        tvGet.setTextColor(ResUtils.getColor(R.color.text_color_dark));
        tvFlow.setTextColor(ResUtils.getColor(R.color.text_color_dark));

        chargeDivider.setVisibility(View.VISIBLE);
        getDivider.setVisibility(View.GONE);
        flowDivider.setVisibility(View.GONE);

        rvRecord.setVisibility(View.VISIBLE);
        rvGet.setVisibility(View.GONE);
        rvFlow.setVisibility(View.GONE);
    }

    private void showGet() {
//        tvGet.setTextColor(ResUtils.getColor(R.color.purple77));
        tvCharge.setTextColor(ResUtils.getColor(R.color.text_color_dark));
        tvGet.setTextColor(ResUtils.getColor(R.color.color_1888FE));
        tvFlow.setTextColor(ResUtils.getColor(R.color.text_color_dark));

        getDivider.setVisibility(View.VISIBLE);
        chargeDivider.setVisibility(View.GONE);
        flowDivider.setVisibility(View.GONE);

        rvRecord.setVisibility(View.GONE);
        rvGet.setVisibility(View.VISIBLE);
        rvFlow.setVisibility(View.GONE);

    }

    private void showFlow(){
        tvCharge.setTextColor(ResUtils.getColor(R.color.text_color_dark));
        tvGet.setTextColor(ResUtils.getColor(R.color.text_color_dark));
        tvFlow.setTextColor(ResUtils.getColor(R.color.color_1888FE));

        getDivider.setVisibility(View.GONE);
        chargeDivider.setVisibility(View.GONE);
        flowDivider.setVisibility(View.VISIBLE);

        rvRecord.setVisibility(View.GONE);
        rvGet.setVisibility(View.GONE);
        rvFlow.setVisibility(View.VISIBLE);
    }


    /**
     * 加载更多 充
     */
    private void removeLoading() {
        if (dataList.size() > 0) {
            if (dataList.get(dataList.size() - 1).getLoadType() == 1) {
                dataList.remove(dataList.size() - 1);
            }
        }

    }

    private void addBaseLine() {
        if (mPageCharge != 1) {
            dataList.add(new AccountRecordEnity.DataBean.RecordsBean(2));
        }
    }

    /**
     * 加载更多 提
     */
    private void removeLoadingGet() {
        if (dataList2.size() > 0) {
            if (dataList2.get(dataList2.size() - 1).getLoadType() == 1) {
                dataList2.remove(dataList2.size() - 1);
            }
        }

    }

    private void addBaseLineGet() {
        if (mPageGet != 1) {
            dataList2.add(new AccountRecordEnity.DataBean.RecordsBean(2));
        }
    }

    /**
     * 加载更多 资产
     */
    private void removeLoadingFlow() {
        if (flowDataList.size() > 0) {
            if (flowDataList.get(flowDataList.size() - 1).getLoadType() == 1) {
                flowDataList.remove(flowDataList.size() - 1);
            }
        }

    }

    private void addBaseLineFlow() {
        if (mPageFlow != 1) {
            flowDataList.add(new FlowEntity.DataBean.RecordsBean(2));
        }
    }


    @Override
    public void onCallback() {
        if (status == 1) {
            getRecords(coin);
        } else if (status == 2) {
            getRecordGet(coin);
        } else {
            getRecordFlow(coin);
        }
    }
}
