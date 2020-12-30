package cn.suozhi.DiBi.market.view;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.wallet.view.TransferCoinActivity;

/**
 * 币币 - Fragment
 */
public class TradeFragment extends BaseFragment implements TabLayout.OnTabSelectedListener,
        BaseFragment.OnLoadListener {

    @BindView(R.id.tv_tradeCoin)
    public TextView tvCoin;
    @BindView(R.id.tl_tradeTab)
    public TabLayout tabLayout;
    @BindView(R.id.iv_ieoTransfer)
    public ImageView ivIeoTransfer;

    private FragmentManager fragmentManager;
    private TradeBuyFragment buyFragment;
    private TradeSellFragment sellFragment;
    private TradeEntrustFragment entrustFragment; // 当前委托
    private TradeHistoryFragment historyFragment; // 历史委托
    private TradeTradeFragment tradeFragment; // 历史成交

    private String[] TAB;//Tab文字

    public static TradeFragment newInstance(int page) {
        TradeFragment fragment = new TradeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getViewResId() {
        return R.layout.fragment_trade;
    }

    @Override
    protected void init(View v) {
        TAB = new String[]{getString(R.string.buying), getString(R.string.selling),
                getString(R.string.currentEntrust), getString(R.string.historyEntrust), getString(R.string.historyTrade)};
        if (loadListener != null) {
            updateSymbol(loadListener.getSymbol());
        }

        int page = getArguments().getInt("page", 0);
        tabLayout.addOnTabSelectedListener(this);
        Util.tabInit(tabLayout, TAB, R.layout.tab_b_purple_77_bk_2c, R.id.tv_tabBPb2Name,
                0, page, R.id.v_tipIndicator, 0.2F);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (fragmentManager == null) {
            fragmentManager = getChildFragmentManager();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (tab.getPosition()) {
            case 0://买入
                if (buyFragment == null) {
                    buyFragment = new TradeBuyFragment();
                    buyFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_trade, buyFragment, "trade0");
                } else {
                    transaction.show(buyFragment);
                    buyFragment.refresh();
                }
                break;
            case 1://卖出
                if (sellFragment == null) {
                    sellFragment = new TradeSellFragment();
                    sellFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_trade, sellFragment, "trade1");
                } else {
                    transaction.show(sellFragment);
                    sellFragment.refresh();
                }
                break;
            case 2://当前委托
                if (entrustFragment == null) {
                    entrustFragment = new TradeEntrustFragment();
                    entrustFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_trade, entrustFragment, "trade2");
                } else {
                    transaction.show(entrustFragment);
                    entrustFragment.refresh();
                }
                break;
            case 3://历史委托
                if (historyFragment == null) {
                    historyFragment = new TradeHistoryFragment();
                    historyFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_trade, historyFragment, "trade3");
                } else {
                    transaction.show(historyFragment);
                    historyFragment.refresh();
                }
                break;
            case 4://历史成交
                if (tradeFragment == null) {
                    tradeFragment = new TradeTradeFragment();
                    tradeFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_trade, tradeFragment, "trade4");
                } else {
                    transaction.show(tradeFragment);
                    tradeFragment.refresh();
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (buyFragment != null) {
            transaction.hide(buyFragment);
        }
        if (sellFragment != null) {
            transaction.hide(sellFragment);
        }
        if (entrustFragment != null) {
            transaction.hide(entrustFragment);
        }
        if (historyFragment != null) {
            transaction.hide(historyFragment);
        }
        if (tradeFragment != null) {
            transaction.hide(tradeFragment);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    @Override
    public void onLoad(int type, long id) {
        if (loadListener != null) {
            loadListener.onLoad(type, id);
        }
    }

    @Override
    public void onLoad(int type, long id, View v) {
        if (loadListener != null) {
            loadListener.onLoad(type, id, v);
        }
    }

    @Override
    public void onLoad(int type, long id, View v, String orderBy, String order) {
        if (loadListener != null) {
            loadListener.onLoad(type, id, v, orderBy, order);
        }
    }

    @Override
    public void onLoad(int type, long id, View v, String zone, String orderBy, String order) {
        if (loadListener != null) {
            loadListener.onLoad(type, id, v, zone, orderBy, order);
        }
    }

    @Override
    public Symbol getSymbol() {
        if (loadListener != null) {
            return loadListener.getSymbol();
        }
        return null;
    }

    public void updateSymbol(Symbol s) {
        if (s == null) {
            return;
        }

        if (tvCoin != null) {
            String lp = s.getPCoin(),rt = s.getTCoin(), r = s.getCurrencyPairRegion(), la = "", rb = "";

            if(r.equals("B")){
                ivIeoTransfer.setVisibility(View.VISIBLE);
            } else {
                ivIeoTransfer.setVisibility(View.GONE);
            }

            if(lp.contains("-")){
                la = lp.substring(0, lp.indexOf("-"));
            } else {
                la = lp;
            }

            if(rt.contains("-")){
                rb = rt.substring(0, rt.indexOf("-"));
            } else {
                rb = rt;
            }

            tvCoin.setText(la + "/" + rb);
        }
        if (buyFragment != null) {
            buyFragment.updateSymbol(s);
        }
        if (sellFragment != null) {
            sellFragment.updateSymbol(s);
        }
    }

    public void updateAccount(Messages.DmUserAccount a, boolean isP) throws Exception {
        if (isP) {
            if (sellFragment != null) {
                sellFragment.updateAccount(a);
            }
        } else {
            if (buyFragment != null) {
                buyFragment.updateAccount(a);
            }
        }
    }

    public void updateQuote(Messages.DmQuote q, boolean first) throws Exception {
        if (buyFragment != null) {
            buyFragment.updateQuote(q, first);
        }
        if (sellFragment != null) {
            sellFragment.updateQuote(q, first);
        }
    }

    public void updateDepth(List<Messages.DmDepthItem> bids, List<Messages.DmDepthItem> asks) throws Exception {
        if (buyFragment != null) {
            buyFragment.updateDepth(bids, asks);
        }
        if (sellFragment != null) {
            sellFragment.updateDepth(bids, asks);
        }
    }

    public void updatePlace(long id) throws Exception {
        if (buyFragment != null) {
            buyFragment.updatePlace(id);
        }
        if (sellFragment != null) {
            sellFragment.updatePlace(id);
        }
    }

    public void updateRecord(List<Messages.DmTrade> list) throws Exception {
        if (buyFragment != null) {
            buyFragment.updateRecord(list);
        }
        if (sellFragment != null) {
            sellFragment.updateRecord(list);
        }
    }

    public void updateRecord() throws Exception {
        if (buyFragment != null) {
            buyFragment.updateRecord();
        }
        if (sellFragment != null) {
            sellFragment.updateRecord();
        }
    }

    public void updateOrder(long id, Messages.OrderResponse o) throws Exception {
        if (id == Constant.Code.Trade_Current_Entrust) {
            if (entrustFragment != null) {
                entrustFragment.updateOrder(o);
            }
        } else if (id == Constant.Code.Trade_History_Entrust) {
            if (historyFragment != null) {
                historyFragment.updateOrder(o);
            }
        }
    }

    public void updateOrder(long id) {
        if (id == Constant.Code.Trade_Current_Entrust) {
            if (entrustFragment != null) {
                entrustFragment.updateOrder();
            }
        } else if (id == Constant.Code.Trade_History_Entrust) {
            if (historyFragment != null) {
                historyFragment.updateOrder();
            }
        }
    }

    public void updateOrder(Messages.DmOrder order) throws Exception {
        if (historyFragment != null) {
            historyFragment.updateOrder(order);
        }
    }

    public void updateCancel() {
        if (entrustFragment != null) {
            entrustFragment.updateCancel();
        }
    }

    public void updateExecution(Messages.ExecutionResponse e) throws Exception {
        if (tradeFragment != null) {
            tradeFragment.updateExecution(e);
        }
    }

    public void updateExecution() {
        if (tradeFragment != null) {
            tradeFragment.updateExecution();
        }
    }

    // 此处将参数传递至MainActivity的OnLoad里面
    @OnClick({R.id.tv_tradeCoin, R.id.iv_tradeKline, R.id.iv_ieoTransfer})
    public void trade(View v) {
        switch (v.getId()) {
            case R.id.tv_tradeCoin://切换交易对
                Util.hideKeyboard(v);
                if (loadListener != null) {
                    loadListener.onLoad(2, 0);
                }
                break;
            case R.id.iv_tradeKline://K线
                Util.hideKeyboard(v);
                if (loadListener != null) {
                    loadListener.onLoad(3, 0);
                }
                break;
            case R.id.iv_ieoTransfer://ieo转账
                startActivity(new Intent(getContext(), IeoTransferActivity.class));
                break;
        }
    }

    @Override
    public void transmit(String info) {
        if (buyFragment != null) {
            buyFragment.transmit(info);
        }
        if (sellFragment != null) {
            sellFragment.transmit(info);
        }
        if (entrustFragment != null) {
            entrustFragment.transmit(info);
        }
        if (historyFragment != null) {
            historyFragment.transmit(info);
        }
        if (tradeFragment != null) {
            tradeFragment.transmit(info);
        }
    }

    public void onRefresh() {
        onRefresh(tabLayout == null ? 0 : tabLayout.getSelectedTabPosition());
    }

    public void onRefresh(int page) {
        page = page < 0 ? 0 : page;
        if (tabLayout != null) {
            TabLayout.Tab tab = tabLayout.getTabAt(page);
            if (tab != null) {
                tab.select();
            }
        }
        if (loadListener != null) {
            updateSymbol(loadListener.getSymbol());
        }
    }

}
