package cn.suozhi.DiBi.wallet.view;

import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.wallet.adapter.AddressAdapter;
import cn.suozhi.DiBi.wallet.model.CoinAddressEnity;

public class AdreessListActivity extends BaseActivity implements   SwipeRefreshLayout.OnRefreshListener,
        BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener, OnCallbackListener {


    @BindView(R.id.rv_item)
    RecyclerView rvAddress;

    @BindView(R.id.et_search_coin)
    EditText etSearch;

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    private AddressAdapter addressAdapter;
    private int gs;
    private String area;
    private String account;
    private String searchCoin = "";
    private List<CoinAddressEnity.DataBean.RecordsBean> dataList;
    private String currentIds;
    private LoadingDialog
            loadingDialog;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPage = START_INDEX;
    private String coin = "";
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_adreess_list;
    }

    @Override
    protected void init() {
        super.init();

        gs = getIntent().getIntExtra("state", -1);
        from = getIntent().getIntExtra("from", -1);
        area = getIntent().getStringExtra("area");
        account = getIntent().getStringExtra("account");
        searchCoin = getIntent().getStringExtra("coin");
        int height = Util.getPhoneHeight(mContext) / 2;
        rvAddress.setLayoutManager(new LinearLayoutManager(mContext));
        addressAdapter = new AddressAdapter(mContext, height, this);
        rvAddress.setAdapter(addressAdapter.setEmptyView(R.layout.empty_tips));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCoin = etSearch.getText().toString().trim();
                    getCoinAddress(searchCoin);
                    Util.hideKeyboard(etSearch);
                    return true;
                }
                return false;
            }
        });

        addressAdapter.setOnItemClickListener(new AbsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (from == 1) {
                    CoinAddressEnity.DataBean.RecordsBean recordsBean = addressAdapter.getData().get(position);
                    if (!searchCoin.equals(recordsBean.getCode())) {
                        ToastUtil.initToast(AdreessListActivity.this, "不能选取当前币种地址");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("address", recordsBean.getAddress());
                    intent.putExtra("addressTag", recordsBean.getTag());
                    intent.putExtra("addressExt", recordsBean.getRemark());

                    setResult(RESULT_OK, intent);//设置resultCode
                    finish();
                }

                switch (v.getId()) {
                    case R.id.tv_item_delete:
                        currentIds = addressAdapter.getData().get(position).getId() + "";
                        ConfirmDialog.newInstance(getString(R.string.str_comfir_delete_address), getString(R.string.cancel),
                                getString(R.string.confirm_two))
                                .setOnItemClickListener(AdreessListActivity.this)
                                .show(AdreessListActivity.this);
                        break;
                    case R.id.lly_item_view:

                        break;
                }

            }
        });

    }

    @Override
    protected void loadData() {
        super.loadData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getCoinAddress(searchCoin);
    }

    private void getCoinAddress(String coin) {
        OkHttpUtil.getJsonToken(Constant.URL.coinAddress, SharedUtil.getToken(mContext), this,
                "currency", TextUtils.isEmpty(coin) ? "" : coin,
                "pageNum", mPage + "",
                "pageSize", DEFAULT_PAGE_SIZE + "");
    }


    @Override
    public void onRefresh() {
        mPage = START_INDEX;
        getCoinAddress(searchCoin);

    }


    @OnClick({R.id.iv_back, R.id.tv_add_address})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_add_address:
                startActivity(new Intent(mContext, AddAddressActivity.class)
                        .putExtra("state", gs)
                        .putExtra("area", area)
                        .putExtra("account", account));
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }


    /**
     * 弹框的点击事件
     *
     * @param v
     */
    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm) {
            showLoading();
            OkHttpUtil.deleteJsonToken(Constant.URL.deleteCoinAddress + currentIds, SharedUtil.getToken(mContext), this);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        if (url.equals(Constant.URL.coinAddress)) {
            CoinAddressEnity coinAddressEnity = GsonUtil.fromJson(json, CoinAddressEnity.class);
            if (Constant.Int.SUC == coinAddressEnity.getCode()) {

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
                if (Constant.Int.SUC == coinAddressEnity.getCode()) {
                    List<CoinAddressEnity.DataBean.RecordsBean> records = coinAddressEnity.getData().getRecords();
                    dataList.addAll(records);
                    if (coinAddressEnity.getData().getCurrent() < coinAddressEnity.getData().getPages()) {//还有下一页
                        dataList.add(new CoinAddressEnity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                        mPage++;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                addressAdapter.setData(dataList);

            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        coinAddressEnity.getCode(), coinAddressEnity.getMsg()));
                Util.checkLogin(this, coinAddressEnity.getCode());
            }
        } else if (url.equals(Constant.URL.deleteCoinAddress + currentIds)) {

            StringEntity objectEntity = GsonUtil.fromJson(json, StringEntity.class);
            if (Constant.Int.SUC == objectEntity.getCode()) {
                getCoinAddress(searchCoin);
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string._str_delete_address_suc));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);

            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        objectEntity.getCode(), objectEntity.getMsg()));
                Util.checkLogin(this, objectEntity.getCode());
            }
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
            dataList.add(new CoinAddressEnity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public void onCallback() {
        loadData();
    }

}
