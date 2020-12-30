package cn.suozhi.DiBi.market.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.market.adapter.MarketRVAdapter;

/**
 * 行情 - Fragment
 */
public class MarketFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RadioGroup.OnCheckedChangeListener, TabLayout.OnTabSelectedListener, AbsRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.rg_marketCoin)
    public RadioGroup radioGroup;
    @BindView(R.id.tl_marketZone)
    public TabLayout tabLayout;
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.iv_tmPriceTri)
    public ImageView ivPrice;
    @BindView(R.id.iv_tmRateTri)
    public ImageView ivRate;

    private String[] TAB_ZONE, ZONE = {"", "P", "B"};//市场Tab / 分区
    private List<QuoteEntity> quoteList;
    private MarketRVAdapter recyclerAdapter;

    private int[] SORT = {R.mipmap.triangle_double_gray, R.mipmap.triangle_double_gray_blue,
            R.mipmap.triangle_double_blue_gray};//新币榜排序方式
    private int sort_index = 0;//排序方式  0 - 默认 、 1 - 价格降 、 2 - 价格升 、 3 - 涨跌降 、 4 - 涨跌升
    private int indexCoin;
    private boolean hasLoading = true;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_market;
    }

    @Override
    protected void init(View v) {
        indexCoin = 1;
        int position = radioGroup.indexOfChild(radioGroup.findViewById(R.id.rb_mcUSD));
        radioGroup.getChildAt(position).performClick();
        radioGroup.setOnCheckedChangeListener(this);

        TAB_ZONE = new String[]{getString(R.string.all), getString(R.string.zoneMain), getString(R.string.zoneSuperMine)};
        Util.tabInit(tabLayout, TAB_ZONE, R.layout.tab_b_purple_77_bk_2c, R.id.tv_tabBPb2Name,
                R.id.v_tipIndicator, 0.33F);
        tabLayout.addOnTabSelectedListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new MarketRVAdapter(getActivity());
        recyclerView.setAdapter(recyclerAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        if (loadListener == null || tabLayout == null || indexCoin < 0) {
            return;
        }
        int position = tabLayout.getSelectedTabPosition();
        if (position < 0 || position >= TAB_ZONE.length) {
            return;
        }
        loadListener.onLoad(Constant.Code.Type_Favor_All, Constant.Code.Watch_All, null);
        int type;
        long id;
        switch (indexCoin) {
            case 0:
                type = Constant.Code.Type_Favor;
                id = Constant.Code.Watch;
                break;
            case 2:
                type = Constant.Code.Type_BTC;
                id = Constant.Code.Market_BTC;
                break;
            case 3:
                type = Constant.Code.Type_ETH;
                id = Constant.Code.Market_ETH;
                break;
            case 4:
                type = Constant.Code.Type_DIC;
                id = Constant.Code.Market_DIC;
                break;
            case 1:
            default:
                type = Constant.Code.Type_USDT;
                id = Constant.Code.Market_USDT;
                break;
        }
        String[] order = getOrder();
        loadListener.onLoad(type, id, hasLoading ? refreshLayout : null, ZONE[position], order[0], order[1]);
    }

    /**
     * 获取排序方式
     */
    private String[] getOrder() {
        if (sort_index == 1) {
            return new String[]{"PRICE", "DESC"};
        }
        if (sort_index == 2) {
            return new String[]{"PRICE", "ASC"};
        }
        if (sort_index == 3) {
            return new String[]{"RATE", "DESC"};
        }
        if (sort_index == 4) {
            return new String[]{"RATE", "ASC"};
        }
        return new String[]{null, null};
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        boolean refresh;
        switch (checkedId) {
            case R.id.rb_mcFavor:
                indexCoin = 0;
                refresh = true;
                break;
            case R.id.rb_mcUSD:
                indexCoin = 1;
                refresh = true;
                break;
            case R.id.rb_mcBTC:
                indexCoin = 2;
                refresh = true;
                break;
            case R.id.rb_mcETH:
                indexCoin = 3;
                refresh = true;
                break;
            case R.id.rb_mcHKD:
                indexCoin = 4;
                refresh = true;
                break;
            default:
                refresh = false;
                break;
        }
        if (refresh) {
            refresh();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        refresh();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    @Override
    protected void dismissLoading() {
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    public void updateData(long id, List<Messages.DmQuote> list, Map<String, Symbol> map) {
        if (id == Constant.Code.Watch) {
            if (indexCoin == 0) {
                setData(list, map);
            }
        } else if (id == Constant.Code.Market_USDT) {
            if (indexCoin == 1) {
                setData(list, map);
            }
        } else if (id == Constant.Code.Market_BTC) {
            if (indexCoin == 2) {
                setData(list, map);
            }
        } else if (id == Constant.Code.Market_ETH) {
            if (indexCoin == 3) {
                setData(list, map);
            }
        } else if (id == Constant.Code.Market_DIC) {
            if (indexCoin == 4) {
                setData(list, map);
            }
        } else if (id == Constant.Code.Market_USDTE) {
            if (indexCoin == 1) {
                setData(list, map);
            }
        }
    }

    private void setData(List<Messages.DmQuote> list, Map<String, Symbol> map) {
        dismissLoading();
        if (quoteList == null) {
            quoteList = new ArrayList<>();
        } else {
            quoteList.clear();
        }
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            quoteList.add(AppUtil.getQuote(list.get(i), map));
        }
        recyclerAdapter.setData(quoteList);
    }

    public void updateQuote(Messages.DmQuote q) {
        if (q == null || quoteList == null || quoteList.size() == 0) {
            return;
        }
        int index = -1;
        for (int i = 0; i < quoteList.size(); i++) {
            if (quoteList.get(i).getSymbol().equals(q.getSymbol())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            quoteList.get(index).setValue(q.getPrice(), AppUtil.getQuoteRate(q), q.getVolume(), q.getCloseCny());
            recyclerAdapter.notifyItemChange2(index);
        }
    }

    @OnClick({R.id.iv_marketSearch, R.id.tv_tmCoin, R.id.tv_tmPrice, R.id.tv_tmRate})
    public void market(View v) {
        switch (v.getId()) {
            case R.id.iv_marketSearch:
                if (loadListener != null) {
                    loadListener.onLoad(1, 0);
                }
                break;
            case R.id.tv_tmCoin:
                if (sort_index == 0) {
                    break;
                }
                sort_index = 0;
                ivPrice.setImageResource(SORT[0]);
                ivRate.setImageResource(SORT[0]);
                hasLoading = true;
                loadData();
                break;
            case R.id.tv_tmPrice:
                if (sort_index == 1 || sort_index == 2) {
                    sort_index = 3 - sort_index;
                } else {
                    sort_index = 1;
                }
                ivPrice.setImageResource(SORT[sort_index]);
                ivRate.setImageResource(SORT[0]);
                hasLoading = true;
                loadData();
                break;
            case R.id.tv_tmRate:
                if (sort_index == 3 || sort_index == 4) {
                    sort_index = 7 - sort_index;
                } else {
                    sort_index = 3;
                }
                ivPrice.setImageResource(SORT[0]);
                ivRate.setImageResource(SORT[sort_index - 2]);
                hasLoading = true;
                loadData();
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.iv_marketItemStar:
                if (loadListener != null) {
                    v.setTag(quoteList.get(position).getSymbol());
                    loadListener.onLoad(v.isSelected() ? Constant.Code.Type_Favor_Del :
                            Constant.Code.Type_Favor_Add, Constant.Code.Watch_Edit, v);
                }
                break;
            case R.id.cl_marketItem:
                if (loadListener != null) {
                    loadListener.onLoad(Constant.Code.Type_Trade, 0, v);
                }
                break;
        }
    }

    public void upFavor(Set<String> set) {
        recyclerAdapter.setFavor(set);
    }

    @Override
    public void transmit(String info) {
        dismissLoading();
    }

    @Override
    public void onRefresh() {
        hasLoading = false;
        loadData();
    }

    public void refresh() {
        hasLoading = true;
        loadData();
    }
}
