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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
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
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.wallet.adapter.AccountOrderAdapter;
import cn.suozhi.DiBi.wallet.adapter.TransferRecordAdapter;
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;
import cn.suozhi.DiBi.wallet.model.CurrenciesBean;
import cn.suozhi.DiBi.wallet.model.TransferRecordEntity;

/**
 * 转账记录
 */
public class TransferRecordActivity extends BaseActivity implements AbsRecyclerAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, OkHttpUtil.OnDataListener, View.OnClickListener, OnCallbackListener {

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView rvRecord;
    @BindView(R.id.tab_tv_charge)
    public TabLayout tab_charge;
    @BindView(R.id.rv_get)
    public RecyclerView rvGet;
    private TextView tvCharge;
    private View chargeDivider;
    private TextView tvGet;
    private View getDivider;
    private boolean isCharge = true;
    private boolean isGet = false;
    //标识当前是哪个类型的订单
    private int status = 1;
    private List<TransferRecordEntity.DataBean.RecordsBean> dataList;
    private List<TransferRecordEntity.DataBean.RecordsBean> dataList2;
    private TransferRecordAdapter traUsdt;
    private TransferRecordAdapter traDic;

    private int height;
    private RelativeLayout back;
    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private int mPage = START_INDEX;
    private String code;

    @Override
    protected int getViewResId() {
        return R.layout.activity_tr;
    }

    @Override
    protected void init() {
        initHeader();
        height = Util.getPhoneHeight(mContext) / 2;

        rvRecord.setLayoutManager(new LinearLayoutManager(mContext));

        if (SharedUtil.getLanguage(mContext).equals("en")) {
            tvCharge.setTextSize(11);
            tvGet.setTextSize(11);
        } else {
            tvCharge.setTextSize(15);
            tvGet.setTextSize(15);
        }
        getCoinList();
        createChargeAdapter();
        showCharge();


        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    // DIC
    private void createGetAdapter() {
        rvGet.setLayoutManager(new LinearLayoutManager(mContext));
        traDic = new TransferRecordAdapter(mContext, height, 2, this);
        rvGet.setAdapter(traDic
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    // USDT
    private void createChargeAdapter() {
        traUsdt = new TransferRecordAdapter(mContext, height, 1, this);
        rvRecord.setAdapter(traUsdt
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    @Override
    protected void loadData() {
        if (status == 1) {
            mPage = START_INDEX;
            getTRecords("DIBI");
        } else {
            mPage = START_INDEX;
            getTRecords("BITP");
        }
    }

    /**
     * 获取转账记录
     */
    private void getTRecords(String coin) {
        OkHttpUtil.getJsonToken(Constant.URL.otcTransferRecord, SharedUtil.getToken(mContext), this,
                "currencyCode",coin ,
                "pageNum", mPage + "",
                "pageSize", DEFAULT_PAGE_SIZE + "");
        Log.i("TAG", "getTRecords: "+SharedUtil.getToken(mContext));
    }

    // 获取法币
    private void getCoinList(){
        OkHttpUtil.getJson(Constant.URL.currencies, this);
    }

    private void initHeader() {
        back = findViewById(R.id.rly_back);
        tvCharge = findViewById(R.id.tv_charge);
        tvCharge.setText("USDT");
        chargeDivider = findViewById(R.id.v_charge_divider);
        tvGet = findViewById(R.id.tv_get);
        tvGet.setText("DIC");
        getDivider = findViewById(R.id.v_get_divider);

        tvCharge.setOnClickListener(this);
        tvGet.setOnClickListener(this);
        back.setOnClickListener(this);
        tab_charge.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabitem(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onItemClick(View v, int position) {
        //
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
        // Constant.URL.otcTransferRecord
        if (url.equals(Constant.URL.otcTransferRecord)) {
            if (status == 1) {
                // usdt
                TransferRecordEntity enity = GsonUtil.fromJson(json, TransferRecordEntity.class);
                Log.i("TAG", "onResponse: "+enity.toString());
                //  tab_charge.addTab(tab_charge.newTab().setText("BITP"));
//                for (int i = 0; i < enity.getData().getRecords().size(); i++) {
//                    String code = enity.getData().getRecords().get(i).getCurrencyCode();
//                    tab_charge.addTab(tab_charge.newTab().setText(code));
//                }
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
                    dataList.addAll(enity.getData().getRecords());
                    if (enity.getData().getCurrent() < enity.getData().getPages()) { // 还有下一页


                        dataList.add(new TransferRecordEntity.DataBean.RecordsBean(1)); // 这里是把正在加载显示出来
                        mPage++;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                traUsdt.setData(dataList);
            } else {
                // DIC
                TransferRecordEntity enity = GsonUtil.fromJson(json, TransferRecordEntity.class);
                //第一次加载的时候
                if (mPage == 1) {
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

                        dataList2.add(new TransferRecordEntity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                        mPage++;
                    } else {
                        addBaseLineGet();
                    }
                } else {
                    addBaseLineGet();
                }
                traDic.setData(dataList2);
            }

        }
        if (url.equals(Constant.URL.currencies)){
            Log.i("TAG", "onResponse: "+"1111111111111111111111111");
            CurrenciesBean enity = GsonUtil.fromJson(json, CurrenciesBean.class);
            Log.i("TAG", "onResponse: "+enity.toString());
            for (int i = 0; i < enity.getData().size(); i++) {
                code = enity.getData().get(i).getCode();
                tab_charge.addTab(tab_charge.newTab().setText(code));
                Log.i("TAG", "onResponse: "+ code);
            }

        }

    }


    @Override
    public void onFailure(String url, String error) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_charge:
                isGet = false;
                status = 1;
                showCharge();
                if (!isCharge) {
                    if (traUsdt == null) {
                        createChargeAdapter();
                    }
                    mPage = START_INDEX;
                    getTRecords(code);
                }
                isCharge = true;
                break;
            case R.id.tv_get:
                isCharge = false;
                status = 2;
                showGet();
                //避免重复请求
                if (!isGet) {
                    if (traDic == null) {
                        createGetAdapter();
                    }
                    mPage = START_INDEX;
                    getTRecords(code);
                }
                isGet = true;
                break;
            case R.id.rly_back:
                finish();
                break;
        }
//        for (int i = 0; i < tab_charge.getTabCount(); i++) {
//            TabLayout.Tab tabAt = tab_charge.getTabAt(i);
//            if (tabAt == null) return;
//            Class aClass = tabAt.getClass();
//            try {
//                Field view = aClass.getDeclaredField("view");
//                view.setAccessible(true);
//                View v1 = (View) view.get(tabAt);
//                v1.setTag(i);
//                v1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(TransferRecordActivity.this, "11111111111111111", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


    }

    private void tabitem(TabLayout.Tab tab) {

        if (tab.getPosition() == 0){
            //Toast.makeText(mContext, "111111111111111111", Toast.LENGTH_SHORT).show();
            isGet = false;
            status = 1;
            showCharge();
            if (!isCharge) {
                if (traUsdt == null) {
                    createChargeAdapter();
                }
                mPage = START_INDEX;
                getTRecords("DIBI");
            }
            isCharge = true;
        }else if(tab.getPosition() == 1){
            //    Toast.makeText(mContext, "222222222", Toast.LENGTH_SHORT).show();
            isCharge = false;
            status = 2;
            showGet();
            //避免重复请求
            if (!isGet) {
                if (traDic == null) {
                    createGetAdapter();
                }
                mPage = START_INDEX;
                getTRecords("BITP");
            }
            isGet = true;
        }
    }





    private void showCharge() {
//        tvCharge.setTextColor(ResUtils.getColor(R.color.purple77));
        tvCharge.setTextColor(ResUtils.getColor(R.color.color_1888FE));
        tvGet.setTextColor(ResUtils.getColor(R.color.text_color_dark));
        chargeDivider.setVisibility(View.VISIBLE);
        getDivider.setVisibility(View.GONE);

        rvRecord.setVisibility(View.VISIBLE);
        rvGet.setVisibility(View.GONE);
    }

    private void showGet() {
//        tvGet.setTextColor(ResUtils.getColor(R.color.purple77));
        tvGet.setTextColor(ResUtils.getColor(R.color.color_1888FE));
        tvCharge.setTextColor(ResUtils.getColor(R.color.text_color_dark));
        getDivider.setVisibility(View.VISIBLE);
        chargeDivider.setVisibility(View.GONE);

        rvRecord.setVisibility(View.GONE);
        rvGet.setVisibility(View.VISIBLE);
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
            dataList.add(new TransferRecordEntity.DataBean.RecordsBean(2));
        }
    }

    /**
     * 加载更多
     */
    private void removeLoadingGet() {
        if (dataList2.size() > 0) {
            if (dataList2.get(dataList2.size() - 1).getLoadType() == 1) {
                dataList2.remove(dataList2.size() - 1);
            }
        }
    }

    private void addBaseLineGet() {
        if (mPage != 1) {
            dataList2.add(new TransferRecordEntity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public void onCallback() {
        if (status == 1) {
            getTRecords("DIBI");
        } else {
            getTRecords("BITP");
        }
    }
}
