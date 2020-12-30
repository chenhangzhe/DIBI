package cn.suozhi.DiBi.market.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.KeyboardChangeListener;
import cn.suozhi.DiBi.common.custom.EntrustView;
import cn.suozhi.DiBi.common.custom.TickSeekBar;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.home.view.HelpSearchActivity;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.market.adapter.NewDealRVAdapter;

/**
 * 币币-卖出
 */
public class TradeSellFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        BaseDialog.OnItemClickListener, SeekBar.OnSeekBarChangeListener, EntrustView.OnItemClickListener,
        KeyboardChangeListener.KeyBoardListener,BaseFragment.OnLoadListener,TabLayout.OnTabSelectedListener {

    private FragmentManager fragmentManager;

    @BindView(R.id.ll_ts)
    public LinearLayout llSell;
    @BindView(R.id.srl_ts)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.ll_newDeal)
    public LinearLayout llDeal;

    @BindView(R.id.tv_tsPrice)
    public TextView tvPrice;
    @BindView(R.id.tv_tsPriceCny)
    public TextView tvPriceCny;
    @BindView(R.id.tv_ttBuyPrice)
    public TextView tvBP;
    @BindView(R.id.ev_tsBuy)
    public EntrustView evBuy;
    @BindView(R.id.ev_tsSell)
    public EntrustView evSell;

    @BindViews({R.id.tv_ptTabLimit, R.id.tv_ptTabMarket, R.id.fl_ptTabTarget})
    public List<View> tabs;
    @BindView(R.id.cl_ptTarget)
    public ConstraintLayout clTarget;
    @BindView(R.id.cl_plLimit)
    public ConstraintLayout clLimit;
    @BindView(R.id.cl_smMarket)
    public ConstraintLayout clMarket;

    @BindView(R.id.et_ptPrice)
    public EditText etTarget;
    @BindView(R.id.tv_ptPriceError)
    public TextView tveTarget;
    @BindView(R.id.et_plPrice)
    public EditText etPrice;
    @BindView(R.id.tv_plPriceError)
    public TextView tvePrice;
    @BindView(R.id.et_plAmount)
    public EditText etAmount;
    @BindView(R.id.tv_plAmountError)
    public TextView tveAmount;

    @BindView(R.id.tv_ptPriceCny)
    public TextView tvtCny;
    @BindView(R.id.tv_plPriceCny)
    public TextView tvlCny;
    @BindView(R.id.tv_plaCoin)
    public TextView tvlCoin;
    @BindView(R.id.tv_plaAvail)
    public TextView tvlAvail;

    @BindView(R.id.et_smAmount)
    public EditText etmAmount;
    @BindView(R.id.tv_smAmountError)
    public TextView tvmAmount;
    @BindView(R.id.tv_smPriceCny)
    public TextView tvmCny;
    @BindView(R.id.tv_smCoin)
    public TextView tvmCoin;
    @BindView(R.id.tv_smAvail)
    public TextView tvmAvail;

    @BindView(R.id.tsb_pl)
    public TickSeekBar tsbLimit;
    @BindView(R.id.tv_plProgress)
    public TextView tvpLimit;
    @BindView(R.id.tsb_sm)
    public TickSeekBar tsbMarket;
    @BindView(R.id.tv_smProgress)
    public TextView tvpMarket;

    @BindView(R.id.tv_tsTotalName)
    public TextView tvTotalName;
    @BindView(R.id.tv_tslTotal)
    public TextView tvlTotal;
    @BindView(R.id.tv_tsmTotal)
    public TextView tvmTotal;
    @BindView(R.id.tv_tsSell)
    public TextView tvSell;

    @BindView(R.id.cv_tsNewDeal)
    public View vNewDeal;

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshDeal;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.tv_tndPrice)
    public TextView tvdPrice;
    @BindView(R.id.tv_tndAmount)
    public TextView tvdAmount;

    private List<QuoteEntity> dataList;
    private NewDealRVAdapter recyclerAdapter;

    private int pp = 4, tp = 4;
    private boolean fromUser = false;//是否手动输入
    private Messages.DmQuote quote;
    private double avail;

    private boolean seekChange = false;
    private int[] TICK = {0, 25, 50, 75, 100};

    private int page;
    private boolean hasLoading = true;

    @BindView(R.id.tv_max_r)
    public TextView tvMaxR;
    @BindView(R.id.tv_max_f)
    public TextView tvMaxF;
    @BindView(R.id.ll_maxRF)
    public LinearLayout llMaxRF;

    private double avgP,maxR,maxF,tempR,tempF;
    private int marketType = 0; // 市场类型 B为多解一

    @Override
    protected int getViewResId() {
        return R.layout.fragment_trade_sell;
    }

    @Override
    protected void init(View v) {

        clMarket.setVisibility(View.GONE); // 市价

        if (loadListener != null) {
            updateSymbol(loadListener.getSymbol());
        }
        page = 0;
        tabs.get(0).setSelected(true);
        evBuy.setOnItemClickListener(this);
        evSell.setOnItemClickListener(this);

        tsbLimit.addTick(25, 50, 75);
        tsbLimit.setOnSeekBarChangeListener(this);
        tsbMarket.addTick(25, 50, 75);
        tsbMarket.setOnSeekBarChangeListener(this);

        Util.editListenerEnterNext(etTarget, etPrice);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new NewDealRVAdapter(getActivity());
        recyclerView.setAdapter(recyclerAdapter.setEmptyView(R.layout.empty_tips));

        new KeyboardChangeListener(getActivity())
                .setLimit(Util.getPhoneWidth(getActivity()) / 2)
                .setKeyBoardListener(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshDeal.setOnRefreshListener(this);
        refreshDeal.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        if (loadListener != null) {
            View v = hasLoading ? tvPrice : null;
            if (page == 0) {
                loadListener.onLoad(Constant.Code.Type_Login, Constant.Code.Login_Account, v);
                loadListener.onLoad(Constant.Code.Type_Quote, Constant.Code.Trade_Quote, v);
                loadListener.onLoad(Constant.Code.Type_Depth, Constant.Code.Trade_Depth, v);
            } else {
                loadListener.onLoad(Constant.Code.Type_New_Deal, Constant.Code.Trade_New_Deal, v);
            }
        }
    }

    public void updateSymbol(Symbol s) {
        if (s == null) {
            return;
        }
        etTarget.setText(null);
        etPrice.setText(null);
        etAmount.setText(null);

        String p = s.getPCoin(), t = s.getTCoin(), r = s.getCurrencyPairRegion(), lp = "", rt = "" , ac = s.getShowSymbol();
        pp = AppUtil.getTradePoint(false, t, s.getPPoint());
        tp = AppUtil.getTradePoint(true, t, s.getTPoint());
        evBuy.setPoint(tp, pp);
        evSell.setPoint(tp, pp);

        if(r.equals("B")){
            marketType = 1; // 超级矿区
            // 只允许限价
            tabs.get(0).setVisibility(View.VISIBLE);
            tabs.get(1).setVisibility(View.GONE);
            tabs.get(2).setVisibility(View.GONE);
            llMaxRF.setVisibility(View.VISIBLE);
            lp = ac.substring(0 , ac.length() - t.length());
        } else {
            marketType = 0;
            tabs.get(0).setVisibility(View.VISIBLE);
            tabs.get(1).setVisibility(View.VISIBLE);
            tabs.get(2).setVisibility(View.VISIBLE);
            llMaxRF.setVisibility(View.GONE);
            lp = p;
        }
        rt = t;

        maxR = s.getMaxRise();
        maxF = s.getMaxFall();

        String pn = getString(R.string.price) + "(" + rt + ")";
        String an = getString(R.string.amount) + "(" + lp + ")";
        tvBP.setText(pn);
//        tvBA.setText(an);
//        tvSP.setText(pn);
//        tvSA.setText(an);
        etPrice.setHint(pn);
        etAmount.setHint(an);
        tvlCoin.setText(lp);
        tvmCoin.setText(lp);
        etmAmount.setHint(an);
        etTarget.setHint(getString(R.string.touchPrice) + "(" + rt + ")");
        tvTotalName.setText(getString(R.string.total) + "(" + rt + ")");

        if (SharedUtil.isLogin(getActivity())) {
            tvSell.setText(getString(R.string.selling) + " " + lp);
        } else {
            tvSell.setText(R.string.sellingAfterLogin);
        }

        tvdPrice.setText(pn);
        tvdAmount.setText(an);
    }

    public void updateAccount(Messages.DmUserAccount a) {
        avail = a == null ? 0 : Util.parseDouble(a.getAvailable());
        String av = Util.addNumSeparate(AppUtil.floorRemoveZero(avail, pp));
        tvlAvail.setText(av);
        tvmAvail.setText(av);
        if (!fromUser) {
            fromUser = true;
        }
    }

    public void updateQuote(Messages.DmQuote q, boolean first) {
        quote = q;

        avgP = quote.getAvgPrice();
        tempR = avgP * (1 + maxR);
        tempF = avgP * (1 - maxF);

        DecimalFormat df = new DecimalFormat("0.00000000");

        tvMaxR.setText(getString(R.string.maxR) + dfSaveTp(df.format(tempR)));
        tvMaxF.setText(getString(R.string.maxF) + dfSaveTp(df.format(tempF)));

        String price = Util.formatDecimal(quote.getPrice(), tp);
        tvPrice.setText(price);
        tvPrice.setSelected(AppUtil.getQuoteRate(quote) >= 0);
        String cny = AppUtil.approximateCny(quote.getCloseCny());
        tvPriceCny.setText(cny);
        tvmCny.setText(cny);
        if (first && etPrice.length() == 0) {
            etPrice.setText(Util.removePointZero(price, false));
        }
    }

    public void updateDepth(List<Messages.DmDepthItem> bids, List<Messages.DmDepthItem> asks) {
        dismissLoading();
        int lb = bids == null ? 0 : bids.size();
        List<QuoteEntity> bl = new ArrayList<>();
        double bm = 0;
        for (int i = 0; i < lb; i++) {
            double size = bids.get(i).getSize();
            bl.add(new QuoteEntity(bids.get(i).getPrice(), size));
            if (size > bm) {
                bm = size;
            }
        }
        int la = asks == null ? 0 : asks.size();
        List<QuoteEntity> sl = new ArrayList<>();
        double sm = 0;
        for (int i = 0; i < la; i++) {
            double size = asks.get(i).getSize();
            sl.add(new QuoteEntity(asks.get(i).getPrice(), size));
            if (size > sm) {
                sm = size;
            }
        }
        double max = Math.max(bm, sm);
        evBuy.setData(bl, max);
        evSell.setData(sl, max);
    }

    /**
     * 下单成功后清空数量
     */
    public void updatePlace(long id) {
        if (id == Constant.Code.Sell_Limit || id == Constant.Code.Sell_Target) {
            fromUser = false;
            etAmount.setText(null);
            etAmount.clearFocus();
            tsbLimit.setProgress(0);
            tvlTotal.setText(null);
            fromUser = true;
        } else if (id == Constant.Code.Sell_Market) {
            fromUser = false;
            tsbMarket.setProgress(0);
            etmAmount.setText(null);
            fromUser = true;
        }
    }

    /**
     * 是否在价格区间
     */
    private boolean isBetween(int index) {
        if (index == 1) {
            return true;
        }
        if (marketType == 1){
            double p = Util.parseDouble(etPrice.getText().toString());
            if(p > tempR || p < tempF){ // 价格必须在成交价的最小~最大区间内
                ToastUtil.initToast(getActivity(),getString(R.string.RFHint));
                return false;
            }
        }
        return true;
    }

    private void initData() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        } else {
            dataList.clear();
        }
    }

    public void updateRecord(List<Messages.DmTrade> list) {
        dismissLoading();
        initData();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                dataList.add(AppUtil.getDeal(list.get(i), pp, tp));
            }
        }
        recyclerAdapter.setData(dataList);
    }

    public void updateRecord() {
        dismissLoading();
        initData();
        recyclerAdapter.setData(dataList);
    }

    @OnTextChanged(value = R.id.et_ptPrice, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etTargetChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + tp + 1;
                if (dotIndex != dotLastIndex) {
                    etTarget.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etTarget.setSelection(etTarget.length());
                } else {
                    if (text.length() > maxIndex) {
                        etTarget.setText(text.toString().substring(0, maxIndex));
                        etTarget.setSelection(etTarget.length());
                    }
                }
            }
            double p = Util.parseDouble(etTarget.getText().toString());
            if (quote != null) {//输入价格约值
                tvtCny.setText(AppUtil.calculateCny(p, quote.getCloseCny(), quote.getPrice()));
            }
            if (tveTarget.length() > 0) {
                tveTarget.setText(null);
            }
            if (tvePrice.length() > 0) {
                tvePrice.setText(null);
            }
        } else {
            if (tvtCny.length() > 0) {
                tvtCny.setText(null);
            }
        }
    }

    @OnTextChanged(value = R.id.et_plPrice, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etPriceChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + tp + 1;
                if (dotIndex != dotLastIndex) {
                    etPrice.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etPrice.setSelection(etPrice.length());
                } else {
                    if (text.length() > maxIndex) {
                        etPrice.setText(text.toString().substring(0, maxIndex));
                        etPrice.setSelection(etPrice.length());
                    }
                }
            }
            double p = Util.parseDouble(etPrice.getText().toString());
            if (quote != null) {//输入价格约值
                tvlCny.setText(AppUtil.calculateCny(p, quote.getCloseCny(), quote.getPrice()));
            }
            if (etAmount.length() > 0) {
                double a = Util.parseDouble(etAmount.getText().toString());
                tvlTotal.setText(AppUtil.roundRemoveZero(p * a, tp));
            }
            if (tvePrice.length() > 0) {
                tvePrice.setText(null);
            }
            if (tveAmount.length() > 0) {
                tveAmount.setText(null);
            }
        } else {
            if (tvlCny.length() > 0) {
                tvlCny.setText(null);
            }
            if (tvlTotal.length() > 0) {
                tvlTotal.setText(null);
            }
        }
    }

    @OnTextChanged(value = R.id.et_plAmount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etAmountChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + pp + 1;
                if (dotIndex != dotLastIndex) {
                    etAmount.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etAmount.setSelection(etAmount.length());
                } else {
                    if (text.length() > maxIndex) {
                        etAmount.setText(text.toString().substring(0, maxIndex));
                        etAmount.setSelection(etAmount.length());
                    }
                }
            }
            double a = Util.parseDouble(etAmount.getText().toString());
            tsbLimit.setProgress(AppUtil.calculateProgress(a, avail));
            if (etPrice.length() > 0) {
                double p = Util.parseDouble(etPrice.getText().toString());
                tvlTotal.setText(AppUtil.roundRemoveZero(p * a, tp));
            }
            if (tveAmount.length() > 0) {
                tveAmount.setText(null);
            }
        } else {
            if (tsbLimit.getProgress() > 0) {
                tsbLimit.setProgress(0);
            }
            if (tvlTotal.length() > 0) {
                tvlTotal.setText(null);
            }
        }
    }

    @OnTextChanged(value = R.id.et_smAmount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etMarketChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + pp + 1;
                if (dotIndex != dotLastIndex) {
                    etmAmount.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etmAmount.setSelection(etmAmount.length());
                } else {
                    if (text.length() > maxIndex) {
                        etmAmount.setText(text.toString().substring(0, maxIndex));
                        etmAmount.setSelection(etmAmount.length());
                    }
                }
            }
            double a = Util.parseDouble(etmAmount.getText().toString());
            tsbMarket.setProgress(AppUtil.calculateProgress(a, avail));
        } else {
            if (tsbLimit.getProgress() > 0) {
                tsbLimit.setProgress(0);
            }
        }
    }

    @OnClick({R.id.tv_ptTabLimit, R.id.tv_ptTabMarket, R.id.fl_ptTabTarget, R.id.iv_ptTabTarget,
            R.id.tv_ptpMinus, R.id.tv_ptpPlus, R.id.tv_plpMinus, R.id.tv_plpPlus,
            R.id.tv_plaMinus, R.id.tv_plaPlus, R.id.tv_smMinus, R.id.tv_smPlus, R.id.tv_tsSell,
            R.id.tv_tsUnfold, R.id.tv_newDealUnfold})
    public void sell(View v) {
        switch (v.getId()) {
            case R.id.tv_ptTabLimit://限价
                if (!v.isSelected()) {
                    tabSelect(0);
                }
                break;
            case R.id.tv_ptTabMarket://市价
                if (!v.isSelected()) {
                    tabSelect(1);
                }
                break;
            case R.id.fl_ptTabTarget://止盈止损
                if (!v.isSelected()) {
                    tabSelect(2);
                }
                break;
            case R.id.iv_ptTabTarget://止盈止损
                ConfirmDialog.newInstance(getString(R.string.targetTips), getString(R.string.cancel),
                        getString(R.string.check))
                        .setOnItemClickListener(this)
                        .show(getActivity());
                break;
            case R.id.tv_ptpMinus://触发减
                AppUtil.addOrSub(etTarget, false);
                break;
            case R.id.tv_ptpPlus://触发加
                AppUtil.addOrSub(etTarget, true);
                break;
            case R.id.tv_plpMinus://价格减
                AppUtil.addOrSub(etPrice, false);
                break;
            case R.id.tv_plpPlus://价格加
                AppUtil.addOrSub(etPrice, true);
                break;
            case R.id.tv_plaMinus://数量减
                AppUtil.addOrSub(etAmount, false);
                break;
            case R.id.tv_plaPlus://数量加
                AppUtil.addOrSub(etAmount, true);
                break;
            case R.id.tv_smMinus://市价数量减
                AppUtil.addOrSub(etmAmount, false);
                break;
            case R.id.tv_smPlus://市价数量加
                AppUtil.addOrSub(etmAmount, true);
                break;
            case R.id.tv_tsSell://卖出
                if (!SharedUtil.isLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    int index = getTabIndex();
                    if (index < 0 || index > 2) {
                        break;
                    }
                    Util.hideKeyboard(etPrice);
                    if (isTargetLegal(index) && isLimitLegal(index) && isPriceLegal(index) &&
                            isMarketLegal(index) && isAvailLegal(index) && isBetween(index)) {
                        if (loadListener != null) {
                            int type;
                            long id;
                            String p = Util.removePointZero(etPrice.getText().toString(), false);
                            String a = Util.removePointZero(etAmount.getText().toString(), false);
                            String ta = Util.removePointZero(etTarget.getText().toString(), false);
                            String am = Util.removePointZero(etmAmount.getText().toString(), false);
                            switch (index) {
                                case 0:
                                    type = Constant.Code.Type_Sell_Limit;
                                    id = Constant.Code.Sell_Limit;
                                    v.setTag(R.id.tag_relation, p);
                                    v.setTag(R.id.tag_relation2, a);
                                    v.setTag(R.id.tag_relation3, "");
                                    break;
                                case 1:
                                    type = Constant.Code.Type_Sell_Market;
                                    id = Constant.Code.Sell_Market;
                                    v.setTag(R.id.tag_relation, "");
                                    v.setTag(R.id.tag_relation2, am);
                                    v.setTag(R.id.tag_relation3, "");
                                    break;
                                case 2:
                                    type = Constant.Code.Type_Sell_Target;
                                    id = Constant.Code.Sell_Target;
                                    v.setTag(R.id.tag_relation, p);
                                    v.setTag(R.id.tag_relation2, a);
                                    v.setTag(R.id.tag_relation3, ta);
                                    break;
                                default:
                                    id = type = 0;
                                    break;
                            }
                            loadListener.onLoad(type, id, v);
                        }
                    }
                }
                break;
            case R.id.tv_tsUnfold:
                page = 1;
                llDeal.setVisibility(View.VISIBLE);
                animMove(0, llSell.getHeight());
                break;
            case R.id.tv_newDealUnfold:
                page = 0;
                animMove(-llSell.getHeight(), 0);
                break;
        }
    }

    /**
     * 触发价是否输入合理
     */
    private boolean isTargetLegal(int index) {
        if (index != 2) {
            return true;
        }
        if (etTarget.length() == 0 || Util.parseDouble(etTarget.getText().toString()) <= 0) {
            tveTarget.setText(R.string.inputTouchPrice);
            etTarget.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 价格数量是否输入合理
     */
    private boolean isLimitLegal(int index) {
        if (index == 1) {
            return true;
        }
        if (etPrice.length() == 0 || Util.parseDouble(etPrice.getText().toString()) <= 0) {
            tvePrice.setText(R.string.inputPrice);
            etPrice.requestFocus();
            return false;
        }
        if (etAmount.length() == 0 || Util.parseDouble(etAmount.getText().toString()) <= 0) {
            tveAmount.setText(R.string.inputAmount);
            etAmount.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 触发价委托价是否输入合理
     */
    private boolean isPriceLegal(int index) {
        if (index != 2) {
            return true;
        }
        double ta = Util.parseDouble(etTarget.getText().toString());
        double p = Util.parseDouble(etPrice.getText().toString());
        if (ta * 0.9 > p) {
            tvePrice.setText(R.string.touchPriceTips90);
            etPrice.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 市价是否输入合理
     */
    private boolean isMarketLegal(int index) {
        if (index != 1) {
            return true;
        }
        double a = Util.parseDouble(etmAmount.getText().toString());
        if (etmAmount.length() == 0 || a <= 0) {
            tvmAmount.setText(R.string.inputAmount);
            etmAmount.requestFocus();
            return false;
        }
        if (a > avail) {
            tvmAmount.setText(R.string.availLess);
            etmAmount.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 余额是否足够
     */
    private boolean isAvailLegal(int index) {
        if (index == 1) {
            return true;
        }
        double a = Util.parseDouble(etAmount.getText().toString());
        if (a > avail) {
            tveAmount.setText(R.string.availLess);
            return false;
        }
        return true;
    }

    private void tabSelect(int index) {
        clearSelect();
        Util.hideKeyboard(tvPrice);
        tabs.get(index).setSelected(true);
        tveTarget.setText(null);
        tvePrice.setText(null);
        tveAmount.setText(null);
        tvmAmount.setText(null);

        switch (index) {
            case 0:
                clTarget.setVisibility(View.GONE); // 触发价
                clLimit.setVisibility(View.VISIBLE); // 限价
                clMarket.setVisibility(View.GONE); // 市价
                tvlTotal.setVisibility(View.VISIBLE);
                tvmTotal.setVisibility(View.GONE);
                break;
            case 1:
                clTarget.setVisibility(View.GONE);
                clLimit.setVisibility(View.GONE);
                clMarket.setVisibility(View.VISIBLE);
                tvlTotal.setVisibility(View.INVISIBLE);
                tvmTotal.setVisibility(View.VISIBLE);
                break;
            case 2:
                clTarget.setVisibility(View.VISIBLE);
                clLimit.setVisibility(View.VISIBLE);
                clMarket.setVisibility(View.GONE);
                tvlTotal.setVisibility(View.VISIBLE);
                tvmTotal.setVisibility(View.GONE);
                break;
            default:
                clTarget.setVisibility(View.GONE);
                clLimit.setVisibility(View.GONE);
                clMarket.setVisibility(View.GONE);
                tvlTotal.setVisibility(View.VISIBLE);
                tvmTotal.setVisibility(View.GONE);
                break;
        }
    }

    private void clearSelect() {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).isSelected()) {
                tabs.get(i).setSelected(false);
                break;
            }
        }
    }

    private int getTabIndex() {
        int index = -1;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).isSelected()) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
        switch (seekBar.getId()) {
            case R.id.tsb_pl:
                tvpLimit.setText(progress + "%");
                if ((user || seekChange) && avail > 0) {//修改数量
                    double a = avail * progress / 100;
                    etAmount.setText(AppUtil.floorRemoveZero(a, pp));
                    seekChange = false;
                }
                break;
            case R.id.tsb_sm:
                tvpMarket.setText(progress + "%");
                if ((user || seekChange) && avail > 0) {
                    double a = avail * progress / 100;
                    etmAmount.setText(AppUtil.floorRemoveZero(a, pp));
                    seekChange = false;
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int pro = seekBar.getProgress();
        int newPro = getNewProgress(pro);
        if (pro != newPro) {
            seekChange = true;
            seekBar.setProgress(newPro);
        }
    }

    private int getNewProgress(int pro) {
        int np = pro;
        for (int i = 0; i < TICK.length; i++) {
            int t = TICK[i];
            if (Math.abs(pro - t) <= 5) {
                np = t;
                break;
            }
        }
        return np;
    }

    @Override
    public void onItemClick(View layout, boolean hasValue, double price, double amount) {
        if (hasValue) {
            fromUser = false;
            etPrice.setText(AppUtil.roundRemoveZero(price, tp));
            fromUser = true;
            etAmount.setText(AppUtil.roundRemoveZero(amount, pp));
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm) {//跳转帮助
            startActivity(new Intent(getActivity(), HelpSearchActivity.class)
                    .putExtra(Constant.Strings.Intent_Key, getString(R.string.targetHelpTitle)));
        }
    }

    private void animMove(int top, int bottom) {
        ObjectAnimator cb = ObjectAnimator.ofFloat(llSell, "Y", top, -bottom);
        ObjectAnimator ld = ObjectAnimator.ofFloat(llDeal, "Y", bottom, -top);
        AnimatorSet set = new AnimatorSet().setDuration(300);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loadData();
            }
        });
        set.playTogether(cb, ld);
        set.start();
    }

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        vNewDeal.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void dismissLoading() {
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        if (refreshDeal != null && refreshDeal.isRefreshing()) {
            refreshDeal.setRefreshing(false);
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (fragmentManager == null) {
            fragmentManager = getChildFragmentManager();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    private String dfSaveTp(String str){
        return str.substring(0, str.substring(0 , str.indexOf(".")).length() + 1 + tp);
    }


}
