package cn.suozhi.DiBi.wallet.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.login.model.PhoneCodeEntity;
import cn.suozhi.DiBi.wallet.adapter.AllCoinAdapter;
import cn.suozhi.DiBi.wallet.adapter.CoinSideBar;
import cn.suozhi.DiBi.wallet.model.AllCoinEnity;
import cn.suozhi.DiBi.wallet.model.CoinSliderEnity;

public class SelectedCoinActivity extends BaseActivity implements OkHttpUtil.OnDataListener, CoinSideBar.OnTouchingLetterChangedListener {


    //显示城市列表
    private ListView mainList;
    private List<ArrayList<PhoneCodeEntity.DataBean>> cotainers = new ArrayList<>();
    //城市数据源
    private List<String> data;
    private List<Integer> dataId;
    private List<Integer> precis;
    //字母位置
    private List<Integer> letterPositionList;
    //字母Char
    private List<Integer> letterCharList;
    //自定义View显示右侧字母
    private CoinSideBar myView;
    //列表上方固定文字
    private TextView tv01;
    //右侧显示内容
    private int lastFirstVisibleItem;
    private TextView overlay;
    private Handler handler;
    private OverlayThread overlayThread;
    private AllCoinEnity allCoins;
    private List<CoinSliderEnity> districtSliderEnities;
    private List<CoinSliderEnity> title = new ArrayList<>();
    private String currency = "";

    @BindView(R.id.rv_often_coin)
    RecyclerView rvOftenCoin;

    @BindView(R.id.et_search_coin)
    EditText etSearchCoin;

    private CoinAdapter coinAdapter;
    private List<AllCoinEnity.DataBean.LatestCurrencyBean> latestCurrency;
    private String type;
    private int gs;
    private String area;
    private String account;

    private String oToken;

    private List<AllCoinEnity.DataBean.CurrencyDtoBean> coinList;

    @Override
    protected int getViewResId() {
        return R.layout.activity_selected_coin;
    }

    @Override
    protected void init() {
        initData();

        rvOftenCoin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        coinAdapter = new CoinAdapter(mContext);
        rvOftenCoin.setAdapter(coinAdapter);

        if (rvOftenCoin.getItemDecorationCount() == 0) {
            rvOftenCoin.addItemDecoration(new DecoRecycler(mContext, R.drawable.deco_8_trans,
                    DecoRecycler.Edge_NONE));
        }


        coinAdapter.setOnItemClickListener((v, position) -> {
            int cid = latestCurrency.get(position).getCurrencyId();
            if (!canNext(cid)) {
                return;
            }

            String result = latestCurrency.get(position).getCode() + "";
            String coinId = cid + "";
            int preci = latestCurrency.get(position).getPrecis();

            String onceToken = oToken;

            switch (type) {
                case Constant.Strings.Intent_Choose_Recharge:
                    Intent ir = new Intent(this, ChargeCoinActivity.class)
                            .putExtra("result", result)
                            .putExtra("coinId", coinId)
                            .putExtra("precis", preci)
                            .putExtra("oToken", onceToken);
                    startActivity(ir);
                    break;
                case Constant.Strings.Intent_Choose_Withdraw:
                    Intent iw = new Intent(this, GetCoinActivity.class)
                            .putExtra("result", result)
                            .putExtra("coinId", coinId)
                            .putExtra("precis", preci)
                            .putExtra("state", gs)
                            .putExtra("area", area)
                            .putExtra("account", account);
                    startActivity(iw);
                    break;
                case Constant.Strings.Intent_Back_Recharge:
                case Constant.Strings.Intent_Back_Withdraw:
                case Constant.Strings.Intent_Choose_Coin:
                    Intent data = new Intent()
                            .putExtra("result", result)
                            .putExtra("coinId", coinId)
                            .putExtra("precis", preci)
                            .putExtra("oToken", onceToken);
                    setResult(RESULT_OK, data);
                    break;
            }
            finish();
        });

        etSearchCoin.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currency = etSearchCoin.getText().toString().trim().toUpperCase();
                getCoin();
                Util.hideKeyboard(etSearchCoin);
                return true;
            }
            return false;
        });

        //用于显示城市列表
        mainList = (ListView) findViewById(R.id.mainlist);
        //显示右侧字母列表
        myView = (CoinSideBar) findViewById(R.id.myview);
        //城市列表上方固定文字
        tv01 = (TextView) findViewById(R.id.main_tv01);

        //绑定滑动监听
        myView.setOnTouchingLetterChangedListener(this);
        data = new ArrayList<String>();
        dataId = new ArrayList();
        precis = new ArrayList();
        letterCharList = new ArrayList<Integer>();
        letterPositionList = new ArrayList<Integer>();


        mainList.setOnItemClickListener((parent, view, position, id) -> {
            int cid = dataId.get(position);
            if (!canNext(cid)) {
                return;
            }

            String result = data.get(position) + "";
            String coinId = cid + "";
            int preci = precis.get(position);

            String onceToken = oToken;

            switch (type) {
                case Constant.Strings.Intent_Choose_Recharge:
                    Intent ir = new Intent(this, ChargeCoinActivity.class)
                            .putExtra("result", result)
                            .putExtra("coinId", coinId)
                            .putExtra("precis", preci)
                            .putExtra("oToken", onceToken);
                    startActivity(ir);
                    break;
                case Constant.Strings.Intent_Choose_Withdraw:
                    Intent iw = new Intent(this, GetCoinActivity.class)
                            .putExtra("result", result)
                            .putExtra("coinId", coinId)
                            .putExtra("precis", preci)
                            .putExtra("state", gs)
                            .putExtra("area", area)
                            .putExtra("account", account);
                    startActivity(iw);
                    break;
                case Constant.Strings.Intent_Back_Recharge:
                case Constant.Strings.Intent_Back_Withdraw:
                case Constant.Strings.Intent_Choose_Coin:
                    Intent data = new Intent()
                            .putExtra("result", result)
                            .putExtra("coinId", coinId)
                            .putExtra("precis", preci)
                            .putExtra("oToken", onceToken);
                    setResult(RESULT_OK, data);
                    break;
            }
            finish();
        });
    }

    private boolean canNext(int cid) {
        if (coinList == null) {
            return false;
        }
        if (Constant.Strings.Intent_Choose_Coin.equals(type)) {
            return true;
        }
        int index = -1;
        for (int i = 0; i < coinList.size(); i++) {
            if (cid == coinList.get(i).getCurrencyId()) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            AllCoinEnity.DataBean.CurrencyDtoBean c = coinList.get(index);
            if (Constant.Strings.Intent_Choose_Recharge.equals(type) || Constant.Strings.Intent_Back_Recharge.equals(type)) {
                if (c.getIsDepositable() == 0) {
                    ToastUtil.initToast(this, R.string.coinCannotRecharge);
                    return false;
                }
                return true;
            } else if (Constant.Strings.Intent_Choose_Withdraw.equals(type) || Constant.Strings.Intent_Back_Withdraw.equals(type)) {
                if (c.getIsWithdrawable() == 0) {
                    ToastUtil.initToast(this, R.string.coinCannotWithdraw);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @OnClick({R.id.tv_cancel})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        gs = getIntent().getIntExtra("state", -1);
        area = getIntent().getStringExtra("area");
        account = getIntent().getStringExtra("account");
        if (TextUtils.isEmpty(type)) {
            type = Constant.Strings.Intent_Choose_Coin;
        }

        districtSliderEnities = new ArrayList<>();
        districtSliderEnities.add(new CoinSliderEnity("热", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("A", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("B", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("C", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("D", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("E", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("F", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("G", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("H", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("I", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("J", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("K", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("L", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("M", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("N", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("O", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("P", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("Q", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("R", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("S", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("T", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("U", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("V", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("W", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("X", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("Y", new ArrayList<>()));
        districtSliderEnities.add(new CoinSliderEnity("Z", new ArrayList<>()));
    }

    @Override
    protected void loadData() {
//        OkHttpUtil.getJson(Constant.URL.coinList, this);
        getCoin();
        // 获取一次性token
        getOnceToken();
    }

    // 测试获取一次性token
    private void getOnceToken(){
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.oncetoken);
    }

    private void getCoin() {
        OkHttpUtil.getJsonToken(Constant.URL.chargeGetCoin, SharedUtil.getToken(mContext), this, "currency", currency);
    }

    //加载提示选择的字母
    private void initOverlay() {
        //设置选择字母后提示信息显示位置
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        //设置提示信息显示的位置
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        //添加提示信息到布局 lp的位置
        windowManager.addView(overlay, lp);
    }

    //触摸监听
    public void onTouchingLetterChanged(int s) {
        //切换到list指定的item位置
        mainList.setSelection(letterPositionList.get(s));
        //设置提示的字母
//        overlay.setText(districtSliderEnities.get(s).getSort());
        //显示提示窗
//        overlay.setVisibility(View.VISIBLE);
        //移除以前开启的线程
        handler.removeCallbacks(overlayThread);
        //1.5秒后执行overlayThread线程 隐藏选择字母提示
        handler.postDelayed(overlayThread, 1500);
    }


    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e(TAG, "Choose: " + url + json);
        if (url.equals(Constant.URL.chargeGetCoin)) {
            allCoins = GsonUtil.fromJson(json, AllCoinEnity.class);
            if (allCoins.getCode() == Constant.Int.SUC) {
                //清除数据
                title.clear();
                if (coinList == null) {
                    coinList = new ArrayList<>();
                } else {
                    coinList.clear();
                }
                coinList.addAll(allCoins.getData().getCurrencyDto());
                if (coinList.size() == 0) {
                    tv01.setVisibility(View.GONE);
                } else {
                    tv01.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < coinList.size(); i++) {
                    //汉字转换成拼音
                    String pinyin = coinList.get(i).getCode();
                    //取第一个首字母
                    String letters = pinyin.substring(0, 1).toUpperCase();
                    // 正则表达式，判断首字母是否是英文字母
//                if (letters.matches("[A-Z]")) {
//
//                }
                    coinList.get(i).setmSortLetters(letters.toUpperCase());
                }

                //清除每一个字母集合里面的数据
                for (int i = 0; i < districtSliderEnities.size(); i++) {
                    districtSliderEnities.get(i).getList().clear();

                }
                for (int i = 0; i < districtSliderEnities.size(); i++) {
                    for (int j = 0; j < coinList.size(); j++) {
                        if (coinList.get(j).getmSortLetters().equals(districtSliderEnities.get(i).getSort())) {
                            districtSliderEnities.get(i).getList().add(coinList.get(j));
                        }
                    }
                }

                for (int i = 0; i < districtSliderEnities.size(); i++) {
                    if (districtSliderEnities.get(i).getList().size() != 0 && districtSliderEnities.get(i).getList() != null) {
                        title.add(districtSliderEnities.get(i));
                    }
                }
                showPhoneCity(districtSliderEnities, title);
                latestCurrency = allCoins.getData().getLatestCurrency();
                coinAdapter.setData(latestCurrency);
            }
        }
        else if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                oToken = onceToken;
            } else {
                ToastUtil.initToast(SelectedCoinActivity.this,onceTokenEnity.getMsg());
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {

    }

    //关闭提示信息线程
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            //隐藏提示信息
            overlay.setVisibility(View.GONE);
        }
    }


    public void showPhoneCity(List<CoinSliderEnity> enityList, List<CoinSliderEnity> title) {
        //清除数据
        letterPositionList.clear();
        data.clear();
        dataId.clear();
        precis.clear();
        //消息通知
        handler = new Handler();
        //隐藏提示信息线程
        overlayThread = new OverlayThread();
        initOverlay();
        int index = 0, position = 0;
        //是否显示热门
//        letterCharList.add(index);
        //循环城市数据源
        for (int i = 0; i < enityList.size(); i++) {
            for (int j = 0; j < enityList.get(i).getList().size(); j++) {
                if (i == 0 && j == 0) {
                    index++;
                    letterPositionList.add(position);
                } else if (j == 0) {
                    letterCharList.add(index);
                    letterPositionList.add(position);
                    index++;
                } else {
                    letterCharList.add(-1);
                }
                position++;
                //添加数据到集合
                data.add(enityList.get(i).getList().get(j).getCode());
                dataId.add(enityList.get(i).getList().get(j).getCurrencyId());
                precis.add(enityList.get(i).getList().get(j).getPrecis());

            }
        }
        //声明适配器
        AllCoinAdapter adapter = new AllCoinAdapter(this, data, letterCharList, title, -1);
        //设置适配器
        mainList.setAdapter(adapter);
        //判断listview滚动设置城市列表上方固定文字
        mainList.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            //滑动的时候触发
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (letterCharList.get(firstVisibleItem) >= 0) {
                    //设置显示文字
                    if (title != null && title.size() != 0) {
                        tv01.setText(title.get(letterCharList.get(firstVisibleItem)).getSort());
                    }

                    lastFirstVisibleItem = firstVisibleItem;
                } else {
                    if (lastFirstVisibleItem > firstVisibleItem) {
                        //设置显示文字
                        if (title != null && title.size() != 0) {
                            tv01.setText(title.get(letterCharList.get(lastFirstVisibleItem) - 1).getSort());
                        }
                    }
                }
            }
        });

        myView.setData(title);
    }


    public class CoinAdapter extends AbsRecyclerAdapter<AllCoinEnity.DataBean.LatestCurrencyBean> {


        public CoinAdapter(Context context) {
            super(context, R.layout.item_coin);
        }

        @Override
        public void onBindHolder(RecyclerHolder holder, AllCoinEnity.DataBean.LatestCurrencyBean d, int position) {
            holder.bindTextView(R.id.tv_coin, d.getCode());
        }
    }
}
