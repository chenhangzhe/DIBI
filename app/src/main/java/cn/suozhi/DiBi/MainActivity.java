package cn.suozhi.DiBi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.tabs.TabLayout;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.suozhi.DiBi.c2c.view.C2cFragment;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.dialog.DownloadDialog;
import cn.suozhi.DiBi.common.dialog.EditAliasDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.dialog.VersionDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Base64Util;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.DataBaseUtil;
import cn.suozhi.DiBi.common.util.FileUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.SocketUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.home.model.QuoteEntity;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.home.model.VersionEntity;
import cn.suozhi.DiBi.home.view.C1Activity;
import cn.suozhi.DiBi.home.view.CollectMoneyActivity;
import cn.suozhi.DiBi.home.view.ContactActivity;
import cn.suozhi.DiBi.home.view.HomeFragment;
import cn.suozhi.DiBi.home.view.IdentityResultActivity;
import cn.suozhi.DiBi.home.view.InviteActivity;
import cn.suozhi.DiBi.home.view.SecurityActivity;
import cn.suozhi.DiBi.home.view.SettingActivity;
import cn.suozhi.DiBi.home.view.Thefingerprint;
import cn.suozhi.DiBi.home.view.Yestures;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.market.adapter.SearchRVAdapter;
import cn.suozhi.DiBi.market.adapter.TradeFilterRVAdapter;
import cn.suozhi.DiBi.market.view.KlineActivity;
import cn.suozhi.DiBi.market.view.MarketFragment;
import cn.suozhi.DiBi.market.view.TradeFragment;
import cn.suozhi.DiBi.wallet.view.WalletFragment;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        BaseFragment.OnLoadListener, SwipeRefreshLayout.OnRefreshListener, OkHttpUtil.OnDataListener,
        BaseDialog.OnItemClickListener, AbsRecyclerAdapter.OnItemClickListener, View.OnClickListener,
        TabLayout.OnTabSelectedListener, TextView.OnEditorActionListener, OkHttpUtil.OnProgressListener, Utils.OnAppStatusChangedListener {

    @BindView(R.id.dl_main)
    public DrawerLayout drawerLayout;
    @BindView(R.id.rg_mainTab)
    public RadioGroup radioGroup;
    @BindView(R.id.rl_main)
    public RelativeLayout rl;

    @BindView(R.id.ll_mine)
    public LinearLayout llMine;
    @BindView(R.id.srl_mine)
    public SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tv_mineAlias)
    public TextView tvAlias;
    @BindView(R.id.tv_mineUid)
    public TextView tvUid;
    @BindView(R.id.tv_mineIdLevel)
    public TextView tvLevel;
    @BindView(R.id.iv_mineAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tv_mineAvatar)
    public TextView tvAvatar;
    @BindView(R.id.cl_mineTips)
    public ConstraintLayout clTips;
    @BindView(R.id.tv_mineIdentifyState)
    public TextView tvIdentify;

    @BindView(R.id.ll_search)
    public LinearLayout llSearch;
    @BindView(R.id.et_search)
    public EditText etSearch;
    @BindView(R.id.rv_search)
    public RecyclerView rvSearch;

    private ConstraintLayout clPop;//切换交易对
    private View vBack;
    private TabLayout tlPop;
    private EditText etPop;
    private RecyclerView rvPop;

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private MarketFragment marketFragment;
    private C2cFragment c2cFragment;
    private TradeFragment tradeFragment;
    private WalletFragment walletFragment;

    private int page, page_trade;// 底部tab / 币币页码
    private boolean isExit = false;//退出标识
    private UserEntity.DataEntity.InfoEntity d;
    private String reason;//C2认证失败提示
    private boolean isUpdating;//头像是否正在上传
    private String path;//拍照储存路径

    private final int LIMIT_SEARCH = 8;//搜索历史最多显示个数
    private SearchRVAdapter adapterSearch;
    private List<QuoteEntity> searchList;

    private String token, favor;// token / socket语言 / 编辑自选的交易对
    private long loginTime;
    private int socketType = -1;//获取币种状态 0 - 建立链接中 、 1 - 获取中 、 2 - 获取失败 、 3 - 获取后本地未登录
    // 4 - 获取后正在登录 、 5 - 获取后登录失败 、 6 - 获取后登录成功
    private WebSocketClient client;

    private Map<String, Symbol> mapSymbol = new HashMap<>();//所有交易对Symbol
    private Set<String> setSymbol = new HashSet<>();//添加自选的交易对

    private String symbol;// 币币页展示的交易对
    private int pId = -1, tId = -1;//币种id

    private boolean isAnim;//是否正在执行动画
    private String[] TAB_POP;
    private int indexPop;
    private TradeFilterRVAdapter adapterPop;
    private List<QuoteEntity> popList;
    private List<QuoteEntity> filterList;

    private String versionName;
    private String downloadLink;
    private String filePath;//安装包存放路径
    private BaseDialog versionDialog;
    private DownloadDialog downloadDialog;

    private LoadingDialog loadingDialog;
    private Handler handler = new Handler();

    private ArrayList<String> langList;
    private String[] langString = {"chs", "cht", "en"};
    private int langDefaultIndex;
    private long useTime;
    private SharedPreferences time;
    private long enter;
    private UserEntity mine;
    private long enter1;

    @Override
    protected int getViewResId() {
        openRestartInResume();
        setNavigationColor(R.color.white);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        return R.layout.activity_main;
    }

    @Override
    protected void init() {


        AppUtils.registerAppStatusChangedListener(this);
        lang = SharedUtil.getLanguage4Socket(this);
        time = getSharedPreferences("time", Context.MODE_PRIVATE);
        langList = new ArrayList<>();
        langList.add("简体中文");
        langList.add("繁體中文");
        langList.add("English");
        String language = SharedUtil.getLanguage(this);
        switch (language) {
            case "chs":
                langDefaultIndex = 0;
                break;
            case "cht":
                langDefaultIndex = 1;
                break;
            case "en"://英文
                langDefaultIndex = 2;
                break;
            default:
                langDefaultIndex = 0;
                break;
        }

        Intent intent = getIntent();
        String s = intent.getStringExtra("symbol");
        symbol = TextUtils.isEmpty(s) ? Constant.Strings.Default_Symbol : s;
        boolean isBuy = intent.getBooleanExtra("isBuy", true);
        page_trade = isBuy ? 0 : 1;

        page = intent.getIntExtra("page", 0);//需要打开的底部tab
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.getChildAt(page).performClick();

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.setFocusableInTouchMode(false);
        int pw = Util.getPhoneWidth(this);
        ViewGroup.LayoutParams lpm = llMine.getLayoutParams();
        lpm.width = pw;
        llMine.setLayoutParams(lpm);
        ViewGroup.LayoutParams lps = llSearch.getLayoutParams();
        lps.width = pw;
        llSearch.setLayoutParams(lps);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);

        checkStorage();
        checkVersion();
        socket();

        isNologin();

    }

    private boolean isNologin() {
        long usetime1 = time.getLong("usetime", loginTime);
        enter1 = (System.currentTimeMillis() - usetime1);
        token = SharedUtil.getToken(this);
        if (!SharedUtil.isLogin(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            /// finish();
            return false;

        } else {
            if (enter > 1200000) {


                    SharedPreferences password = getSharedPreferences("password", Context.MODE_PRIVATE);
                    String zhiwen = password.getString("zhiwen", "");
                    String shoushi = password.getString("shoushi", "");
                    Log.i("TAG", "intoNextPage: " + "1111111111111" + zhiwen);
                    if (zhiwen.equals("zhiwen")) {
                        Log.i("TAG", "intoNextPage: " + "1111111111111");
                        Thefingerprint.start(this);
                        finish();

                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    if (shoushi.equals("shoushi")) {
                        Yestures.start(this);
                        finish();
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }


                }


        }
        return true;
    }

    private boolean isLogin() {
        token = SharedUtil.getToken(this);
        return SharedUtil.isLogin(token);
    }

    private boolean isLoginAndJump() {
        token = SharedUtil.getToken(this);
        if (!SharedUtil.isLogin(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        stopUpdate(true, true, true, true);
        switch (checkedId) {
            case R.id.rb_mainTab0://首页
                page = 0;
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    homeFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_main, homeFragment, "0");
                } else {
                    transaction.show(homeFragment);
                    homeFragment.onRefresh();
                }
                break;
            case R.id.rb_mainTab1://行情
                page = 1;
                if (marketFragment == null) {
                    marketFragment = new MarketFragment();
                    marketFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_main, marketFragment, "1");
                } else {
                    transaction.show(marketFragment);
                    marketFragment.onRefresh();
                }
                break;
            case R.id.rb_mainTab2://法币
                page = 3;
                if (isLoginAndJump()) {
                    if (c2cFragment == null) {
                        c2cFragment = new C2cFragment();
                        transaction.add(R.id.fl_main, c2cFragment, "3");
                    } else {
                        transaction.show(c2cFragment);
                        c2cFragment.refresh();
                    }
                }
                break;
            case R.id.rb_mainTab3://币币
                page = 2;
                if (tradeFragment == null) {
                    tradeFragment = TradeFragment.newInstance(page_trade);
                    tradeFragment.setOnLoadListener(this);
                    transaction.add(R.id.fl_main, tradeFragment, "2");
                    page_trade = 0;
                } else {
                    transaction.show(tradeFragment);
                    tradeFragment.onRefresh();
                }
                break;
            case R.id.rb_mainTab4://钱包
                page = 4;
                if (isLoginAndJump()) {
                    if (walletFragment == null) {
                        walletFragment = new WalletFragment();
                        transaction.add(R.id.fl_main, walletFragment, "4");
                    } else {
                        transaction.show(walletFragment);
                        walletFragment.onRefresh();
                    }
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 先隐藏所有Fragment
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (marketFragment != null) {
            transaction.hide(marketFragment);
        }
        if (c2cFragment != null) {
            transaction.hide(c2cFragment);
        }
        if (tradeFragment != null) {
            transaction.hide(tradeFragment);
        }
        if (walletFragment != null) {
            transaction.hide(walletFragment);
        }
    }

    private boolean checkStorage() {
        return Util.permCheckReq(this, Constant.Code.PermStorageCode,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!Util.permCheck(grantResults)) {
            ToastUtil.initToast(this, R.string.permDeny);
        } else {
            if (requestCode == Constant.Code.CameraCode) {
                openCamera();
            }
        }
    }

    private void checkVersion() {
        if (SharedUtil.getBool(this, "User", "isVerFirst", false)) {
            SharedUtil.putBool(this, "User", "isVerFirst", false);
            OkHttpUtil.getJson(Constant.URL.GetAppVersion, this);
        }
    }

    private void socket() {
        loginTime = 0;
        client = SocketUtil.client(Constant.URL.Socket, new SocketUtil.OnSocketListener() {
            @Override
            public void onOpen(ServerHandshake handshake) {
                getMaster();
            }

            @Override
            public void onMessage(ByteBuffer bytes) throws Exception {
                load(bytes);
            }

            @Override
            public void onClose(String message) {
                if (socketType == 1) {
                    socketType = 2;
                } else if (socketType == 4) {
                    socketType = 5;
                } else {
                    socketType = -1;
                }
                socketClose(message);
            }
        });
        socketType = 0;
        client.connect();
    }

    private void socketSend(byte[] bytes) {
        try {
            client.send(bytes);
        } catch (Exception e) {
            dismissLoading();
        }
    }

    /**
     * 获取币种及交易对
     */
    private void getMaster() {
        socketType = 1;
        Messages.MasterDataRequest master = Messages.MasterDataRequest.newBuilder()
                .setRequestId(Constant.Code.Master)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setMasterData(master)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void login() {
        if (isLogin()) {
            loginTime = System.currentTimeMillis();
            socketType = 4;
            Messages.UserLoginRequest login = Messages.UserLoginRequest.newBuilder()
                    .setRequestId(Constant.Code.Login)
                    .setToken(token)
                    .build();
            Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                    .setUserLogin(login)
                    .setLang(lang)
                    .build();
            socketSend(msg.toByteArray());
        } else {//未登录，直接获取行情
            socketType = 3;
            getCoin();
        }
    }

    /**
     * 获取页面数据
     */
    public void getCoin() {
        if (!SocketUtil.isConnect(client)) {
            handler.postDelayed(this::socket, 5 * 1000L);
            return;
        }
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_mainTab0:
                homeFragment.onRefresh();
                break;
            case R.id.rb_mainTab1:
                marketFragment.onRefresh();
                break;
            case R.id.rb_mainTab3:
                tradeFragment.onRefresh();
                break;
        }
    }

    /**
     * Socket返回
     */
    private void load(ByteBuffer bytes) throws Exception {
        Messages.ResponseMessage symbol = Messages.ResponseMessage.parseFrom(bytes.array());
//        Log.e(TAG, "Main: " + symbol.getMsgCase().getNumber());
        switch (symbol.getMsgCase().getNumber()) {
            case 21://币种，交易对
                Messages.MasterDataResponse master = symbol.getMasterData();
                if (Constant.Int.SUC_S.equals(master.getCode())) {
                    initSymbol(master.getCurrenciesList(), master.getPairsList());
                    login();
                } else {
                    socketType = 2;
                }
                break;
            case 1://用户登录
                Messages.UserLoginResponse login = symbol.getUserLogin();
                long lid = login.getRequestId();
                if (Constant.Code.Login_Account == lid) {
                    if (Constant.Int.SUC_S.equals(login.getCode())) {
                        if (tradeFragment != null) {
                            tradeFragment.updateAccount(getAccount(login.getAccountsList(), pId), true);
                            tradeFragment.updateAccount(getAccount(login.getAccountsList(), tId), false);
                        }
                    } /*else {
                        ToastUtil.initToast(this, Util.getCodeTextString(this,
                                login.getCode(), login.getMessage()));
                    }*/
                } else {
                    if (Constant.Int.SUC_S.equals(login.getCode())) {
                        socketType = 6;
                    } else {
                        socketType = 5;
                    }
                    getCoin();
                }
                break;
            case 3://帐户数据更新
                Messages.DmUserAccount account = symbol.getAccountUpdate().getAccount();
                if (account.getCurrencyId() == pId) {
                    if (tradeFragment != null) {
                        tradeFragment.updateAccount(account, true);
                    }
                } else if (account.getCurrencyId() == tId) {
                    if (tradeFragment != null) {
                        tradeFragment.updateAccount(account, false);
                    }
                }
                break;
            case 20://首页交易对
                Messages.HomeSymbolListResponse homeList = symbol.getHomeSymbolList();
                if (Constant.Int.SUC_S.equals(homeList.getCode())) {
                    dismissLoading();
                    if (homeFragment != null) {
                        homeFragment.updateData(homeList.getRequestId(), homeList.getQuotesList(), mapSymbol);
                    }
                } else {
                    socketClose(null);
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            homeList.getCode(), homeList.getMessage()));
                }
                break;
            case 4://币种列表
                Messages.SymbolListResponse symbolList = symbol.getSymbolList();
                long sid = symbolList.getRequestId();
                boolean isPopS = sid >= Constant.Code.Pop_BTC && sid <= Constant.Code.Pop_USDTE;
                if (Constant.Int.SUC_S.equals(symbolList.getCode())) {
                    dismissLoading();
                    if (isPopS) {
                        updateDataPop(sid, symbolList.getQuotesList());
                    } else {
                        if (marketFragment != null) {
                            marketFragment.updateData(sid, symbolList.getQuotesList(), mapSymbol);
                        }
                    }
                } else {
                    if (isPopS) {
                        dismissLoading();
                        setDataPop(null);
                    } else {
                        socketClose(null);
                    }
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            symbolList.getCode(), symbolList.getMessage()));
                }
                break;
            case 5://自选
                Messages.WatchListResponse watchList = symbol.getWatchList();
                long wid = watchList.getRequestId();
                boolean isPopW = wid == Constant.Code.Watch_Pop;
                if (Constant.Int.SUC_S.equals(watchList.getCode())) {
                    dismissLoading();
                    List<Messages.DmQuote> list = watchList.getQuotesList();
                    if (wid != Constant.Code.Watch) {
                        initFavor(list);
                        if (marketFragment != null) {
                            marketFragment.upFavor(setSymbol);
                        }
                        if (adapterSearch != null) {
                            adapterSearch.setFavor(setSymbol);
                        }
                    } else {
                        if (marketFragment != null) {
                            marketFragment.updateData(wid, list, mapSymbol);
                        }
                    }
                    if (isPopW) {//搜索弹窗
                        updateDataPop(wid, list);
                    }
                } else {
                    if (isPopW) {
                        dismissLoading();
                        setDataPop(null);
                    } else {
                        socketClose(null);
                    }
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            watchList.getCode(), watchList.getMessage()));
                }
                break;
            case 6://编辑自选
                Messages.WatchListEditResponse watch = symbol.getWatchEdit();
                if (Constant.Int.SUC_S.equals(watch.getCode())) {
                    dismissLoading();
                    upFavor();
                    if (marketFragment != null) {
                        marketFragment.upFavor(setSymbol);
                    }
                    if (adapterSearch != null) {
                        adapterSearch.setFavor(setSymbol);
                    }
                } else {
                    socketClose(null);
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            watch.getCode(), watch.getMessage()));
                }
                break;
            case 8://搜索币种对
                Messages.SymbolSearchResponse search = symbol.getSymbolSearch();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(search.getCode())) {
                    setSearch(search.getQuotesList());
                } else {
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            search.getCode(), search.getMessage()));
                }
                break;
            case 10://币种对最新行情
                Messages.QuoteResponse quote = symbol.getQuote();
                if (Constant.Int.SUC_S.equals(quote.getCode())) {
                    if (tradeFragment != null) {
                        tradeFragment.updateQuote(quote.getQuote(), true);
                    }
                }
                break;
            case 9://实时行情推送
                Messages.QuoteUpdateResponse quoteUpdate = symbol.getQuoteUpdate();
                long uid = quoteUpdate.getRequestId();
                if (uid == Constant.Code.Trade_Quote) {//币币推送
                    if (tradeFragment != null) {
                        tradeFragment.updateQuote(quoteUpdate.getQuote(), false);
                    }
                } else if (uid >= Constant.Code.Market_BTC && uid <= Constant.Code.Market_DIC) {//行情推送
                    if (marketFragment != null) {
                        marketFragment.updateQuote(quoteUpdate.getQuote());
                    }
                } /*else if (uid == Constant.Code.Home_Top) {//首页顶部推送
                    if (homeFragment != null) {
                        homeFragment.updateTop(quoteUpdate.getQuote());
                    }
                }*/ else if (uid >= Constant.Code.Home_New && uid <= Constant.Code.Home_Fall) {//首页推送
                    if (homeFragment != null) {
                        homeFragment.updateQuote(quoteUpdate.getQuote());
                    }
                } else if (uid == Constant.Code.Watch) {//自选推送
                    if (marketFragment != null) {
                        marketFragment.updateQuote(quoteUpdate.getQuote());
                    }
                }
                break;
            case 13://市场深度
                Messages.DepthResponse depth = symbol.getDepth();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(depth.getCode())) {
                    if (tradeFragment != null) {
                        tradeFragment.updateDepth(depth.getBidsList(), depth.getAsksList());
                    }
                } else {
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            depth.getCode(), depth.getMessage()));
                }
                break;
            case 14://市场深度推送
                Messages.DepthUpdateResponse depthUpdate = symbol.getDepthUpdate();
                if (tradeFragment != null) {
                    tradeFragment.updateDepth(depthUpdate.getBidsList(), depthUpdate.getAsksList());
                }
                break;
            case 22://下单
                Messages.PlaceOrderResponse place = symbol.getPlaceOrder();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(place.getCode())) {
                    View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                            R.mipmap.tick_white_circle, getString(R.string.placeOrderSuc));
                    ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                    if (tradeFragment != null) {
                        tradeFragment.updatePlace(place.getRequestId());
                    }
                } else if (place.getCode().equals("1260009")) {
                    ToastUtil.initToast(this, getString(R.string.code1260009));
                } else if (place.getCode().equals("2010028")) {
                    ToastUtil.initToast(this, getString(R.string.code2010028));
                } else {
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            place.getCode(), place.getMessage()));
                    Util.checkLoginSocket(this, place.getCode());
                }
                break;
            case 15://最近成交记录
                Messages.TradeResponse trade = symbol.getTrade();
                if (Constant.Int.SUC_S.equals(trade.getCode())) {
                    if (tradeFragment != null) {
                        tradeFragment.updateRecord(trade.getTradesList());
                    }
                } else {
                    if (tradeFragment != null) {
                        tradeFragment.updateRecord();
                    }
                }
                break;
            case 16://最近成交记录推送
                Messages.TradeUpdateResponse tradeUpdate = symbol.getTradeUpdate();
                if (tradeFragment != null) {
                    tradeFragment.updateRecord(tradeUpdate.getTradesList());
                }
                break;
            case 18://委托单
                Messages.OrderResponse order = symbol.getOrder();
                dismissLoading();
                long oid = order.getRequestId();
                if (Constant.Int.SUC_S.equals(order.getCode())) {
                    if (tradeFragment != null) {
                        tradeFragment.updateOrder(oid, order);
                    }
                } else {
                    if (tradeFragment != null) {
                        tradeFragment.updateOrder(oid);
                    }
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            order.getCode(), order.getMessage()));
                    Util.checkLoginSocket(this, order.getCode());
                }
                break;
            case 19://委托单推送
                Messages.OrderUpdateResponse orderUpdate = symbol.getOrderUpdate();
                if (tradeFragment != null) {
                    tradeFragment.updateOrder(orderUpdate.getOrder());
                }
                break;
            case 23://取消挂单
                Messages.CancelOrderResponse cancel = symbol.getCancelOrder();
                dismissLoading();
                ToastUtil.initToast(this, Util.getCodeTextString(this,
                        cancel.getCode(), cancel.getMessage()));
                if (Constant.Int.SUC_S.equals(cancel.getCode())) {
                    if (tradeFragment != null) {
                        tradeFragment.updateCancel();
                    }
                } else {
                    Util.checkLoginSocket(this, cancel.getCode());
                }
                break;
            case 17://成交记录
                Messages.ExecutionResponse execution = symbol.getExecution();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(execution.getCode())) {
                    if (tradeFragment != null) {
                        tradeFragment.updateExecution(execution);
                    }
                } else {
                    if (tradeFragment != null) {
                        tradeFragment.updateExecution();
                    }
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            execution.getCode(), execution.getMessage()));
                    Util.checkLoginSocket(this, execution.getCode());
                }
                break;
        }
    }

    /**
     * 通知Fragment Socket已关闭或异常
     */
    private void socketClose(String info) {
        dismissLoading();
        if (homeFragment != null) {
            homeFragment.transmit(info);
        }
        if (marketFragment != null) {
            marketFragment.transmit(info);
        }
        if (tradeFragment != null) {
            tradeFragment.transmit(info);
        }
    }

    /**
     * 保存交易对到map
     */
    private void initSymbol(List<Messages.DmCurrency> coinList, List<Messages.DmPair> pairList) {
        if (mapSymbol.size() > 0) {
            mapSymbol.clear();
        }
        if (pairList == null || pairList.size() == 0) {
            return;
        }
        for (int i = 0; i < pairList.size(); i++) {
            Messages.DmPair pair = pairList.get(i);
            String symbol = pair.getSymbol();
            int pid = pair.getBaseCurrencyId();
            int tid = pair.getQuoteCurrencyId();
            String p = findCoin(pid, coinList);
            String t = findCoin(tid, coinList);
            double maxR = pair.getMaxRise();
            double maxF = pair.getMaxFall();
            String currencyPairRegion = pair.getCurrencyPairRegion();
            String showS = pair.getShowSymbol();

            mapSymbol.put(symbol, new Symbol(symbol, p, t, pair.getSizeDisplayDp(), pair.getPriceDisplayDp(), pid, tid, maxR, maxF, currencyPairRegion, showS));
        }

        if (TextUtils.isEmpty(symbol)) {
            return;
        }

        Symbol s = getSymbol();

        if (s != null) {
            pId = s.getPId();
            tId = s.getTId();
        } else {
            pId = tId = -1;
        }
    }

    private String findCoin(int id, List<Messages.DmCurrency> coinList) {
        int index = -1;
        for (int i = 0; i < coinList.size(); i++) {
            if (id == coinList.get(i).getCurrencyId()) {
                index = i;
                break;
            }
        }
        return index < 0 ? null : coinList.get(index).getCode();
    }

    private Messages.DmUserAccount getAccount(List<Messages.DmUserAccount> list, int id) {
        if (list == null || id < 0) {
            return null;
        }
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getCurrencyId()) {
                index = i;
                break;
            }
        }
        return index < 0 ? null : list.get(index);
    }

    /**
     * 初始化币币相关
     */
    private void initTrade() {
        if (TextUtils.isEmpty(symbol)) {
            return;
        }
        indexPop = 0;
        Symbol s = getSymbol();

        if (s != null) {
            pId = s.getPId();
            tId = s.getTId();
        } else {
            pId = tId = -1;
        }
        if (page == 2) {
            onCheckedChanged(radioGroup, radioGroup.getCheckedRadioButtonId());
        } else {
//            radioGroup.getChildAt(2).performClick();
            startActivity(new Intent(this, KlineActivity.class)
                    .putExtra("symbol", symbol));
        }
    }

    /**
     * 保存自选交易对到set
     */
    private void initFavor(List<Messages.DmQuote> list) {
        if (setSymbol.size() > 0) {
            setSymbol.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            setSymbol.add(list.get(i).getSymbol());
        }
    }

    /**
     * 删减已保存的自选
     */
    private void upFavor() {
        if (TextUtils.isEmpty(favor)) {
            return;
        }
        if (setSymbol.contains(favor)) {
            setSymbol.remove(favor);
        } else {
            setSymbol.add(favor);
        }
    }

    @Override
    public void onLoad(int type, long id) {
        switch (type) {
            case 0://个人中心
                if (isLoginAndJump()) {
                    openDrawer(GravityCompat.START);
                    showLoading();
                    getInfo();
                }
                break;
            case 1://搜索
                setSoft(false);
                openDrawer(GravityCompat.END);
                etSearch.setText(null);
                initSearch();
                break;
            case 2://切换交易对
                if (socketType < 3) {
                    break;
                }
                if (doNext()) {
                    if (clPop == null) {
                        initPop();
                    }
                    if (indexPop < 1 || indexPop >= TAB_POP.length) {
//                        indexPop = getIndex();
                        indexPop = 1;
                    }
                    showPopup();
                }
                break;
            case 3://K线
                startActivity(new Intent(this, KlineActivity.class)
                        .putExtra("symbol", symbol));
                break;
            case 4:// 首页顶部切换语言
                ChooseSingleDialog.newInstance(null, langList, langDefaultIndex, false)
                        .setOnItemClickListener(vl -> {
                            int position = (int) vl.getTag();
                            SharedUtil.putLanguage(this, langString[position]);
                            SharedUtil.onLanguageChange(getApplicationContext());
                            startActivity(new Intent(this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        })
                        .show(this);
                break;
        }
    }

    @Override
    public void onLoad(int type, long id, View v) {
        if (!canLoad()) {
            return;
        }
        switch (type) {
            case Constant.Code.Type_Home_Top:
                getHome(id, "TOP", false, v, null, null);
                break;
            case Constant.Code.Type_Favor_All:
                getFavor(id, false, v, null, null, null);
                break;
            case Constant.Code.Type_Favor_Add:
                favorite(id, true, v);
                break;
            case Constant.Code.Type_Favor_Del:
                favorite(id, false, v);
                break;
            case Constant.Code.Type_Trade:
                symbol = (String) v.getTag(R.id.tag_relation);
                initTrade();
                break;
            case Constant.Code.Type_Login:
                loginAccount(id);
                break;
            case Constant.Code.Type_Quote:
                getQuote(id);
                break;
            case Constant.Code.Type_Depth:
                getDepth(id, v);
                break;
            case Constant.Code.Type_New_Deal:
                getTrade(id);
                break;
            case Constant.Code.Type_Buy_Limit:
                placeOrder(id, v, "B", "L");
                break;
            case Constant.Code.Type_Buy_Market:
                placeOrder(id, v, "B", "M");
                break;
            case Constant.Code.Type_Buy_Target:
                placeOrder(id, v, "B", "SL");
                break;
            case Constant.Code.Type_Sell_Limit:
                placeOrder(id, v, "S", "L");
                break;
            case Constant.Code.Type_Sell_Market:
                placeOrder(id, v, "S", "M");
                break;
            case Constant.Code.Type_Sell_Target:
                placeOrder(id, v, "S", "SL");
                break;
            case Constant.Code.Type_Cancel_Current:
            case Constant.Code.Type_Cancel_History:
                cancelOrder(id, v);
                break;
        }
    }

    @Override
    public void onLoad(int type, long id, View v, String orderBy, String order) {
        if (!canLoad()) {
            return;
        }
        switch (type) {
            case Constant.Code.Type_Home_New:
                getHome(id, "NEW", true, v, orderBy, order);
                break;
            case Constant.Code.Type_Home_Rise:
                getHome(id, "RISE", true, v, orderBy, order);
                break;
            case Constant.Code.Type_Home_Fall:
                getHome(id, "FALL", true, v, orderBy, order);
                break;
            case Constant.Code.Type_Current_Entrust:
                getOrder(id, v, "1", false, orderBy, order);
                break;
            case Constant.Code.Type_History_Entrust:
                getOrder(id, v, "0", true, orderBy, order);
                break;
            case Constant.Code.Type_History_Deal:
                getExecution(id, v, orderBy, order);
                break;
        }
    }

    @Override
    public void onLoad(int type, long id, View v, String zone, String orderBy, String order) {
        if (!canLoad()) {
            return;
        }
        if (zone.equals("B") && type == Constant.Code.Type_USDT) {
            getSymbol(Constant.Code.Market_USDTE, "USDT", true, v, zone, orderBy, order);
        } else {
            switch (type) {
                case Constant.Code.Type_Favor:
                    getFavor(id, true, v, zone, orderBy, order);
                    break;
                case Constant.Code.Type_USDT:
                    getSymbol(id, "USDT", true, v, zone, orderBy, order);
                    break;
                case Constant.Code.Type_BTC:
                    getSymbol(id, "BTC", true, v, zone, orderBy, order);
                    break;
                case Constant.Code.Type_ETH:
                    getSymbol(id, "ETH", true, v, zone, orderBy, order);
                    break;
                case Constant.Code.Type_DIC:
                    getSymbol(id, "DIC", true, v, zone, orderBy, order);
                    break;
            }
        }
    }

    private boolean canLoad() {
        if (socketType == 0 || socketType == 1) {
            return false;
        }
        if (!SocketUtil.isConnect(client)) {
            socket();
            return false;
        }
        if (socketType == 2) {
            getMaster();
            return false;
        }
        if (socketType == 5 || (socketType == 3 && isLogin())) {
            if (System.currentTimeMillis() - loginTime > Util.MIN_1) {//1分钟内执行一次登录
                login();
            }
        }
        return true;
    }

    @Override
    public Symbol getSymbol() {
        return AppUtil.getSymbolInMap(mapSymbol, symbol);
    }

    private void getHome(long id, String type, boolean watch, View v, String orderBy, String order) {
        if (v != null) {
            showLoading();
        }
        Messages.HomeSymbolListRequest symbolList = Messages.HomeSymbolListRequest.newBuilder()
                .setRequestId(id)
                .setType(type)
                .setWatch(watch)
                .setOrderBy(TextUtils.isEmpty(orderBy) ? "" : orderBy)
                .setOrder(TextUtils.isEmpty(order) ? "" : order)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setHomeSymbolList(symbolList)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getSymbol(long id, String region, boolean watch, View v, String zone, String orderBy, String order) {
        if (v != null) {
            showLoading();
        }
        Messages.SymbolListRequest symbolList = Messages.SymbolListRequest.newBuilder()
                .setRequestId(id)
                .setRegion(region)
                .setWatch(watch)
                .setCurrencyRegion(TextUtils.isEmpty(zone) ? "" : zone)
                .setOrderBy(TextUtils.isEmpty(orderBy) ? "" : orderBy)
                .setOrder(TextUtils.isEmpty(order) ? "" : order)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setSymbolList(symbolList)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getFavor(long id, boolean watch, View v, String zone, String orderBy, String order) {
        if (v != null) {
            showLoading();
        }
        Messages.WatchListRequest watchList = Messages.WatchListRequest.newBuilder()
                .setRequestId(id)
                .setWatch(watch)
                .setCurrencyRegion(TextUtils.isEmpty(zone) ? "" : zone)
                .setOrderBy(TextUtils.isEmpty(orderBy) ? "" : orderBy)
                .setOrder(TextUtils.isEmpty(order) ? "" : order)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setWatchList(watchList)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void favorite(long id, boolean b, View v) {
        if (!isLoginAndJump()) {
            return;
        }
        if (v == null) {
            return;
        }
        showLoading();
        favor = (String) v.getTag();

        Messages.WatchListEditRequest watch = Messages.WatchListEditRequest.newBuilder()
                .setRequestId(id)
                .setAdd(b)
                .addSymbols(favor)
                .setWatch(false)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setWatchEdit(watch)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void search(String key) {
        Messages.SymbolSearchRequest search = Messages.SymbolSearchRequest.newBuilder()
                .setRequestId(Constant.Code.Search)
                .setSymbol(key)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setSymbolSearch(search)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    /**
     * 登录获取余额
     */
    private void loginAccount(long id) {
        if (!isLogin()) {
            return;
        }
        Messages.UserLoginRequest login = Messages.UserLoginRequest.newBuilder()
                .setRequestId(id)
                .setToken(token)
                .setIncludeAccount(true)
                .setReqAccountUpdate(true)
                .setHasFundOnly(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setUserLogin(login)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getQuote(long id) {
        Messages.QuoteRequest quote = Messages.QuoteRequest.newBuilder()
                .setRequestId(id)
                .setSymbol(symbol)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setQuote(quote)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getDepth(long id, View v) {
        stopUpdate(false, false, true, true);
        if (v != null) {
            showLoading();
        }
        Messages.DepthRequest depth = Messages.DepthRequest.newBuilder()
                .setRequestId(id)
                .setSymbol(symbol)
                .setLimit(5)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setDepth(depth)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void stopUpdate(boolean quote, boolean depth, boolean trade, boolean order) {
        Messages.StopUpdateRequest stop = Messages.StopUpdateRequest.newBuilder()
                .setRequestId(Constant.Code.Stop)
                .setQuote(quote)
                .setDepth(depth)
                .setTrade(trade)
                .setOrder(order)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setStopUpdate(stop)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getTrade(long id) {
        stopUpdate(true, true, false, true);
        Messages.TradeRequest trade = Messages.TradeRequest.newBuilder()
                .setRequestId(id)
                .setSymbol(symbol)
                .setLimit(20)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setTrade(trade)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void placeOrder(long id, View v, String action, String orderType) {
        if (v == null) {
            return;
        }
        showLoading();
        String p = (String) v.getTag(R.id.tag_relation);
        String t2 = (String) v.getTag(R.id.tag_relation2);
        String ta = (String) v.getTag(R.id.tag_relation3);
        boolean bm = "B".equals(action) && "M".equals(orderType);
        String a = bm ? "" : t2;
        String t = bm ? t2 : "";
        Messages.PlaceOrderRequest place = Messages.PlaceOrderRequest.newBuilder()
                .setRequestId(id)
                .setSymbol(symbol)
                .setAction(action)
                .setOrderType(orderType)
                .setPrice(TextUtils.isEmpty(p) ? "" : p)
                .setSize(TextUtils.isEmpty(a) ? "" : a)
                .setTriggerPrice(TextUtils.isEmpty(ta) ? "" : ta)
                .setAmount(TextUtils.isEmpty(t) ? "" : t)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setPlaceOrder(place)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getOrder(long id, View v, String status, boolean watch, String action, String loading) {
        stopUpdate(true, true, true, false);
        if ("L".equals(loading)) {
            showLoading();
        }
        int p = (int) v.getTag(R.id.tag_relation);
        Messages.OrderRequest order = Messages.OrderRequest.newBuilder()
                .setRequestId(id)
                .setSymbol(symbol)
                .setOrderStatus(status)
                .setPage(p)
                .setSize(15)
                .setWatch(watch)
                .setAction(action)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setOrder(order)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void cancelOrder(long id, View v) {
        if (v == null) {
            return;
        }
        showLoading();
        long orderId = (long) v.getTag(R.id.tag_relation);
        Messages.CancelOrderRequest cancel = Messages.CancelOrderRequest.newBuilder()
                .setRequestId(id)
                .setSymbol(symbol)
                .setOrderId(orderId)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setCancelOrder(cancel)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getExecution(long id, View v, String action, String loading) {
        stopUpdate(true, true, true, true);
        if ("L".equals(loading)) {
            showLoading();
        }
        int p = (int) v.getTag(R.id.tag_relation);
        Messages.ExecutionRequest execution = Messages.ExecutionRequest.newBuilder()
                .setRequestId(id)
                .setPage(p)
                .setSize(15)
                .setSymbol(symbol)
                .setAction(action)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setExecution(execution)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    @Override
    protected void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(this);
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void openDrawer(int gravity) {
        if (!drawerLayout.isDrawerOpen(gravity)) {
            drawerLayout.openDrawer(gravity);
        }
    }

    @OnClick({R.id.iv_mineBack, R.id.iv_mineTipsClose, R.id.tv_mineSecurity, R.id.tv_mineSetting,
            R.id.tv_searchClose})
    public void back(View v) {
        switch (v.getId()) {
            case R.id.iv_mineBack:
                drawerLayout.closeDrawer(GravityCompat.START);
                restart();
                break;
            case R.id.iv_mineTipsClose:
                if (clTips.getVisibility() != View.GONE) {
                    clTips.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_mineSecurity://安全中心
                startActivity(new Intent(this, SecurityActivity.class));
                break;
            case R.id.tv_mineSetting://设置
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.tv_searchClose:
                drawerLayout.closeDrawer(GravityCompat.END);
                setSoft(true);
                restart();
                break;
        }
    }

    private void getInfo() {
        OkHttpUtil.getJsonHeader2(Constant.URL.GetInfo, SharedUtil.getToken(this),
                SharedUtil.getLanguage4Url(this), this);
    }

    @Override
    public void onResponse(String url, String json, String session) {

        switch (url) {
            case Constant.URL.GetInfo:
                mine = JsonUtil.fromJsonO(json, UserEntity.class);
                dismissLoading();
                if (mine == null) {
                    break;
                }
                if (Constant.Int.SUC == mine.getCode()) {
                    reason = mine.getData().getC2VerifyFailureReason();
                    d = mine.getData().getInfo();
                    updateMine();
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            mine.getCode(), mine.getMsg()));
                    Util.checkLogin(this, mine.getCode());
                }
                break;
            case Constant.URL.Upload:
                StringEntity upload = JsonUtil.fromJson(json, StringEntity.class);
                if (upload == null) {
                    break;
                }
                if (Constant.Int.SUC == upload.getCode()) {
                    OkHttpUtil.putJsonToken(Constant.URL.UpdateInfo, SharedUtil.getToken(this),
                            this, "nickName", "", "pic", upload.getData());
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            upload.getCode(), upload.getMsg()));
                }
                break;
            case Constant.URL.UpdateInfo:
                ObjectEntity avatar = JsonUtil.fromJson(json, ObjectEntity.class);
                dismissLoading();
                isUpdating = false;
                if (avatar == null) {
                    break;
                }
                ToastUtil.initToast(this, Util.getCodeText(this,
                        avatar.getCode(), avatar.getMsg()));
                if (Constant.Int.SUC == avatar.getCode()) {
                    tvAvatar.setVisibility(View.GONE);
                    getInfo();
                } else {
                    tvAvatar.setText(R.string.uploadFail);
                    Util.checkLogin(this, avatar.getCode());
                }
                break;
            case Constant.URL.GetAppVersion:
//                Log.e("loge", "Version: " + json);
                VersionEntity ver = JsonUtil.fromJsonO(json, VersionEntity.class);
                if (ver == null) {
                    break;
                }
                if (Constant.Int.SUC == ver.getCode()) {
                    VersionEntity.DataEntity d = ver.getData();
                    if (d == null || TextUtils.isEmpty(d.getVersion()) || !d.getVersion().contains(".")
                            || !Util.isLink(d.getDownloadUrl())) {
                        break;
                    }
                    versionName = d.getVersion();
                    int need = Util.isNeedUpdate(Util.getVersion(this), versionName);
                    if (need > 0) {
                        downloadLink = d.getDownloadUrl();
                        boolean isForce = d.getIsMandatoryUpdate() == 1;
                        String title = String.format(getString(R.string.versionTitle), versionName);
                        String content = d.getDescription().replace("</br>", "\n");
                        if (isForce) {
                            showVersion(title, content, getString(R.string.updateImm), true);
                        } else {
                            String vo = SharedUtil.getString(this, "Constant",
                                    "versionName", "");
                            long time = SharedUtil.getLong(this, "Constant",
                                    "versionTime", 0);
                            if (!versionName.equals(vo) ||//非上次取消更新的版本
                                    System.currentTimeMillis() - time > SharedUtil.DAY_1) {
                                showVersion(title, content, getString(R.string.updateImm), false);
                            }
                        }
                    }
                }
                break;
            case Constant.URL.Logout:
                SharedUtil.clearData(this);
                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;
        }
    }

    private void showVersion(String title, String content, String confirm, boolean force) {
        versionDialog = VersionDialog.newInstance(title, content, confirm, force)
                .setOnItemClickListener(this)
                .show(this);
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        if (Constant.URL.Upload.equals(url) || Constant.URL.UpdateInfo.equals(url)) {
            isUpdating = false;
            tvAvatar.setText(R.string.dataAbnormal);
        }
    }

    private void updateMine() {
        if (d == null) {
            return;
        }
        tvAlias.setText(AppUtil.getAlias(d,this));
        tvUid.setText("UID: " + d.getUserCode());
        GlideUtil.glide(this, ivAvatar, d.getPic(),
                GlideUtil.getOptionCircle(0, R.mipmap.img_default_avatar));
        tvLevel.setVisibility(View.VISIBLE);
        int id = AppUtil.getIdentifyState(d.getVerifiedLevel(), d.getVerifiedStatus(), d.getIdType());
        String state = id > 0 ? getString(id) : null;
        tvLevel.setText(state);
        tvIdentify.setText(state);
    }

    @OnClick({R.id.tv_mineAlias, R.id.iv_mineAvatar, R.id.tv_mineIdLevel, R.id.tv_mineIdentify,
            R.id.tv_mineInvite, R.id.tv_mineGather, R.id.tv_mineContact, R.id.ll_logout})
    public void mine(View v) {
        if (!isLoginAndJump()) {
            return;
        }
        if (d == null) {
            ToastUtil.initToast(this, R.string.retryAfterRefresh);
            return;
        }
        switch (v.getId()) {
            case R.id.tv_mineAlias:
                EditAliasDialog.newInstance(d.getUserName())
                        .setOnItemClickListener(this)
                        .show(this);
                break;
            case R.id.iv_mineAvatar:
                if (!isUpdating) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(getString(R.string.takePicture));
                    list.add(getString(R.string.chooseFromAlbum));
                    ChooseSingleDialog.newInstance(list)
                            .setOnItemClickListener(this)
                            .show(this);
                }
                break;
            case R.id.tv_mineIdLevel:
            case R.id.tv_mineIdentify://身份认证
                int level = d.getVerifiedLevel();
                int status = d.getVerifiedStatus();
                int ls = AppUtil.getIdentify(level, status);
                if (ls == 1) {
                    startActivity(new Intent(this, C1Activity.class));
                } else if (ls == 2) {//C1成功
                    startActivity(new Intent(this, IdentityResultActivity.class)
                            .putExtra("level", level)
                            .putExtra("status", status)
                            .putExtra("type", d.getIdType()));
                } else if (ls >= 3 && ls <= 5) {//C2状态
                    startActivity(new Intent(this, IdentityResultActivity.class)
                            .putExtra("level", level)
                            .putExtra("status", status)
                            .putExtra("reason", reason)
                            .putExtra("type", d.getIdType()));
                } else if (ls == 6) {//C3成功
                    startActivity(new Intent(this, IdentityResultActivity.class)
                            .putExtra("level", level)
                            .putExtra("status", status)
                            .putExtra("type", d.getIdType()));
                }
                break;
            case R.id.tv_mineInvite://邀请好友
                startActivity(new Intent(this, InviteActivity.class));
                break;
            case R.id.tv_mineGather://收款账号
                startActivity(new Intent(this, CollectMoneyActivity.class));
                break;
            case R.id.tv_mineContact://提交工单
                startActivity(new Intent(this, ContactActivity.class));
                break;
//            case R.id.tv_mineHelp://帮助中心
//                startActivity(new Intent(this, HelpCenterActivity.class));
//                break;
            case R.id.ll_logout://在线交谈
//                startActivity(new Intent(this, QIYuChatActivity.class));
                logout();
                break;
        }
    }

    private void logout() {
        AlertDialog.Builder alertDB = new AlertDialog.Builder(MainActivity.this);
        alertDB.setTitle(getString(R.string.showTips));
        alertDB.setMessage(R.string.logoutMark);
        alertDB.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        alertDB.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String token = SharedUtil.getToken(MainActivity.this);
                if (SharedUtil.isLogin(token)) {
                    showLoading();
                    OkHttpUtil.deleteJsonToken(Constant.URL.Logout, token, MainActivity.this);
                } else {
                    SharedUtil.clearData(MainActivity.this);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    restart();
//                    startActivity(new Intent(MainActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        });
        alertDB.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_searchHeadDelete://搜索记录清空
                if (searchList.size() > 0 && searchList.get(0).getType() == 1) {
                    ConfirmDialog.newInstance(getString(R.string.clearHistoryQ),
                            getString(R.string.cancel), getString(R.string.confirm))
                            .setOnItemClickListener(this)
                            .show(this);
                } else {
                    ToastUtil.initToast(this, R.string.emptyHistory);
                }
                break;
            case R.id.v_popFs://Popup空白处
                dismissPopup();
                break;
        }
    }

    @OnEditorAction(R.id.et_search)
    public boolean onAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            Util.editRemoveIllegal(etSearch);
            if (etSearch.length() == 0) {
                ToastUtil.initToast(this, R.string.searchByKey);
                return true;
            }
            Util.hideKeyboard(v);
            etSearch.clearFocus();
            goSearch(etSearch.getText().toString());
            return true;
        }
        return false;
    }

    /**
     * 执行搜索
     */
    private void goSearch(String key) {
        showLoading();
        DataBaseUtil.addSearch(key);
        initRecord();
        search(key);
    }

    /**
     * 展示搜索结果
     */
    private void setSearch(List<Messages.DmQuote> list) {
        clearByType(0, 2, 4);
        searchList.add(new QuoteEntity(2));
        if (list == null || list.size() == 0) {
            searchList.add(new QuoteEntity(4));
            adapterSearch.setData(searchList);
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            searchList.add(AppUtil.getQuote(list.get(i), mapSymbol));
        }
        adapterSearch.setData(searchList);
    }

    @Override
    public void onRefresh() {
        getInfo();
    }

    private void initSearch() {
        if (adapterSearch == null) {
            View head = LayoutInflater.from(this).inflate(R.layout.head_search, null);
            head.findViewById(R.id.iv_searchHeadDelete).setOnClickListener(this);

            rvSearch.setLayoutManager(new GridLayoutManager(this, 4));
            adapterSearch = new SearchRVAdapter(this, setSymbol);
            rvSearch.setAdapter(adapterSearch.addHeaderView(head)
                    .setOnItemClickListener(this));
        }
        if (searchList == null) {
            searchList = new ArrayList<>();
        } else {
            searchList.clear();
        }
        searchList.addAll(0, getRecord());
        adapterSearch.setData(searchList);
    }

    /**
     * 获取数据库保存的搜索记录 - 如无则返回type3空页面
     */
    private List<QuoteEntity> getRecord() {
        List<QuoteEntity> l = new ArrayList<>();
        List<QuoteEntity> list = DataBaseUtil.select(DataBaseUtil.clsQuote);
        if (list.size() == 0) {
            l.add(new QuoteEntity(3));
        } else {
            int min = Math.max(0, list.size() - LIMIT_SEARCH);
            for (int i = list.size() - 1; i >= min; i--) {
                l.add(new QuoteEntity(list.get(i).getSymbol(), 1));
            }
        }
        return l;
    }

    private void initRecord() {
        clearByType(1, 3);
        searchList.addAll(0, getRecord());
        adapterSearch.notifyDataSetChanged();
    }

    /**
     * 根据type删除searchList中item
     */
    private void clearByType(Integer... type) {
        if (type == null || type.length == 0) {
            searchList.clear();
            return;
        }
        List<Integer> list = new ArrayList<>(Arrays.asList(type));
        int t = list.get(0);
        for (int i = searchList.size() - 1; i >= 0; i--) {
            if (t == searchList.get(i).getType()) {
                searchList.remove(i);
            }
        }
        int size = list.size();
        if (size == 1) {
            return;
        }
        Integer[] a = new Integer[size - 1];
        clearByType(list.subList(1, size).toArray(a));
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.tv_shItem://历史记录
                String key = searchList.get(position).getSymbol();
                etSearch.setText(key);
                Util.hideKeyboard(v);
                etSearch.clearFocus();
                goSearch(key);
                break;
            case R.id.cl_searchItem://搜索结果
                symbol = searchList.get(position).getSymbol();
                drawerLayout.closeDrawer(GravityCompat.END);
                setSoft(true);
                initTrade();
                break;
            case R.id.iv_searchItemStar://搜索结果编辑自选
                etSearch.setTag(searchList.get(position).getSymbol());
                favorite(Constant.Code.Watch_Edit, !v.isSelected(), etSearch);
                break;
            case R.id.cl_tfItem://Popup
                if (filterList != null) {
                    symbol = filterList.get(position).getSymbol();
                    dismissPopup();
                    initTrade();
                }
                break;
        }
    }

    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dgEaConfirm:
                String alias = (String) v.getTag();
                showLoading();
                OkHttpUtil.putJsonToken(Constant.URL.UpdateInfo, SharedUtil.getToken(this),
                        new OkHttpUtil.OnDataListener() {
                            @Override
                            public void onResponse(String url, String json, String session) {
                                ObjectEntity ali = JsonUtil.fromJson(json, ObjectEntity.class);
                                dismissLoading();
                                if (ali == null) {
                                    return;
                                }
                                if (Constant.Int.SUC == ali.getCode()) {
                                    d.setUserName(alias);
                                    tvAlias.setText(alias);
                                } else {
                                    ToastUtil.initToast(MainActivity.this,
                                            Util.getCodeText(MainActivity.this,
                                                    ali.getCode(), ali.getMsg()));
                                    Util.checkLogin(MainActivity.this, ali.getCode());
                                }
                            }

                            @Override
                            public void onFailure(String url, String error) {
                                dismissLoading();
                            }
                        }, "nickName", alias, "pic", "");
                break;
            case R.id.tv_stItem:
                int position = (int) v.getTag();
                if (position == 0) {
                    if (Util.permCheckReq(this, Constant.Code.CameraCode,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                        openCamera();
                    }
                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"),
                            Constant.Code.AlbumCode);
                }
                break;
            case R.id.tv_dgcConfirm:
                DataBaseUtil.clear();
                if (searchList.size() == 0) {
                    return;
                }
                clearByType(1, 3);
                searchList.add(0, new QuoteEntity(3));
                adapterSearch.notifyDataSetChanged();
                break;
            case R.id.iv_dgVerClose:
                SharedUtil.putString(this, "Constant", "versionName",
                        versionName);
                SharedUtil.putLong(this, "Constant", "versionTime",
                        System.currentTimeMillis());
                break;
            case R.id.tv_dgVerConfirm:
                if (checkStorage()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!getPackageManager().canRequestPackageInstalls()) {
                            ToastUtil.initToast(this, R.string.needInstallPerm);
                            startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                    Uri.parse("package:" + getPackageName())));
                            break;
                        }
                    }
                    versionDialog.dismiss();

                    downloadDialog = DownloadDialog.newInstance(Util.getAppName(this) + " V" + versionName);
                    downloadDialog.setOnItemClickListener(this).show(this);
                    filePath = Environment.getExternalStorageDirectory() + "/Download/" +
                            Util.getAppName(this) + "_V" + versionName + ".apk";
//                    Log.e("loge", "Download: " + filePath);

                    OkHttpUtil.fileDownload(downloadLink, filePath, this, new OkHttpUtil.OnDataListener() {
                        @Override
                        public void onResponse(String url, String json, String session) {
                            downloadDialog.notifyDone();
                            jumpInstall();
                        }

                        @Override
                        public void onFailure(String url, String error) {
                        }
                    });
                }
                break;
        }
    }


    @Override
    public void onProgress(int index, int rate) {
        ProgressBar pb = downloadDialog.getProgressBar();
        TextView tvInstall = downloadDialog.getViewInstall();
        if (pb != null) {
            pb.setProgress(rate);
        }
        handler.post(() -> {
            if (tvInstall != null) {
                tvInstall.setText(getString(R.string.downloading) + "(" + rate + "%)");
                if (rate >= 100) {
                    tvInstall.setText(R.string.install);
                    tvInstall.setSelected(true);
                }
            }
        });
    }

    /**
     * 跳转到安装页面
     */
    private void jumpInstall() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri;
            File file = new File(filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            startActivity(intent.setDataAndType(uri, "application/vnd.android.package-archive"));
            handler.postDelayed(() -> android.os.Process.killProcess(android.os.Process.myPid()), 1000);
        } catch (Exception e) {
            Log.e(TAG, "Install: " + e.getMessage());
        }
    }

    private void openCamera() {
        FileUtil.delFile(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Util.getDCIMPath() + "/avatar" + System.currentTimeMillis() + ".jpg";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        } else {//7.0
            ContentValues cv = new ContentValues(1);
            cv.put(MediaStore.Images.Media.DATA, path);
            Uri uri = getApplication().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, Constant.Code.CameraCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.Code.CameraCode:
                    setAndUpload(BitMapUtil.decodeBitmap(path, BitMapUtil.K512));
                    break;
                case Constant.Code.AlbumCode:
                    if (data == null) {
                        break;
                    }
                    setAndUpload(BitMapUtil.Uri2Bitmap(this, data.getData(), BitMapUtil.K512));
                    break;
            }
        }
    }

    private void setAndUpload(Bitmap bm) {
        if (bm == null) {
            return;
        }
        GlideUtil.glide(this, ivAvatar, bm, GlideUtil.getOptionCircle());

        isUpdating = true;
        tvAvatar.setVisibility(View.VISIBLE);
        tvAvatar.setText(R.string.uploading);
        String img = Base64Util.encodeByte(BitMapUtil.Bitmap2Bytes(bm));
        OkHttpUtil.postJsonToken(Constant.URL.Upload, SharedUtil.getToken(this),
                this, "file", img);
    }

    private void initPop() {
        TAB_POP = new String[]{getString(R.string.favorite), "USDT", "BTC", "ETH", getString(R.string.zoneSuperMine)};
        clPop = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.popup_filter_symbol, rl, false);
        rl.addView(clPop);

        vBack = clPop.findViewById(R.id.v_popFs);
        tlPop = clPop.findViewById(R.id.tl_popFsType); // tab
        etPop = clPop.findViewById(R.id.et_popFsSearch);
        rvPop = clPop.findViewById(R.id.rv_popFsSymbol);
        vBack.setOnClickListener(this);

        Util.tabInit(tlPop, TAB_POP, R.layout.tab_b_purple_77_bk_2c, R.id.tv_tabBPb2Name, 0,
                indexPop, R.id.v_tipIndicator, 0.2F);
        tlPop.addOnTabSelectedListener(this);

        etPop.setOnEditorActionListener(this);
        rvPop.setLayoutManager(new LinearLayoutManager(this));
        adapterPop = new TradeFilterRVAdapter(this);
        rvPop.setAdapter(adapterPop.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    private int getIndex() {
        int index = 1;
        try {
            Symbol symbol = getSymbol();
            if (symbol == null) {
                return index;
            }
            String t = symbol.getTCoin();
            for (int i = 1; i < TAB_POP.length; i++) {
                if (TAB_POP[i].equals(t)) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {
        }
        return index;
    }

    private void showPopup() {
        setSoft(false);
        Animation in = AnimationUtils.loadAnimation(this, R.anim.translate_in_bottom);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnim = true;
                vBack.setSelected(false);
                clPop.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setStatusColor(R.color.black_t80);
                vBack.setSelected(true);
                isAnim = false;
                etPop.setText(null);
                tlPop.getTabAt(indexPop).select();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        clPop.startAnimation(in);
    }

    private void dismissPopup() {
        Util.hideKeyboard(etPop);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.translate_out_bottom);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnim = true;
                setStatusColor(R.color.white);
                vBack.setSelected(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clPop.setVisibility(View.GONE);
                isAnim = false;
                setSoft(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        clPop.startAnimation(out);
    }

    private boolean isPopShow() {
        if (clPop != null && clPop.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    private boolean doNext() {
        if (isAnim) {
            return false;
        }
        if (isPopShow()) {
            dismissPopup();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        indexPop = tab.getPosition();
        Util.hideKeyboard(etPop);
        etPop.clearFocus();
        loadPopup();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    private void loadPopup() {
        if (indexPop < 0 || indexPop >= TAB_POP.length) {
            return;
        }
        if (indexPop == 0) {
            getFavor(Constant.Code.Watch_Pop, false, tlPop, null, null, null);
            return;
        }
        long id;
        String region, popzone;
        switch (indexPop) {
            case 1:
                id = Constant.Code.Pop_USDT;
                region = "USDT";
                popzone = null;
                break;
            case 2:
                id = Constant.Code.Pop_BTC;
                region = "BTC";
                popzone = null;
                break;
            case 3:
                id = Constant.Code.Pop_ETH;
                region = "ETH";
                popzone = null;
                break;
            case 4:
                id = Constant.Code.Pop_USDTE;
                region = "USDT";
                popzone = "B";
                break;
//            case 1:
            default:
                id = Constant.Code.Pop_DIC;
                region = "DIC";
                popzone = null;
                break;
        }
        getSymbol(id, region, false, tlPop, popzone, null, null);
    }

    private void updateDataPop(long id, List<Messages.DmQuote> list) {
//        Log.e("pop","id = " + id);
        if (id == Constant.Code.Watch_Pop) {
            if (indexPop == 0) {
                setDataPop(list);
            }
        }
//        else if (id == Constant.Code.Pop_DIC) {
//            if (indexPop == 1) {
//                setDataPop(list);
//            }
//        }
        else if (id == Constant.Code.Pop_USDT) {
            if (indexPop == 1) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_BTC) {
            if (indexPop == 2) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_ETH) {
            if (indexPop == 3) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_USDTE) {
            if (indexPop == 4) {
                setDataPop(list);
            }
        }
    }

    private void setDataPop(List<Messages.DmQuote> list) {
        if (popList == null) {
            popList = new ArrayList<>();
        } else {
            popList.clear();
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                popList.add(AppUtil.getQuote(list.get(i), mapSymbol));
            }
        }
        filter();
        adapterPop.setData(filterList);
    }

    private void filter() {
        if (filterList == null) {
            filterList = new ArrayList<>();
        } else {
            filterList.clear();
        }
        if (popList == null) {
            return;
        }
        String key = Util.editRemoveIllegal(etPop);
        if (TextUtils.isEmpty(key)) {
            filterList.addAll(popList);
        } else {
            String K = key.toUpperCase();
            for (int i = 0; i < popList.size(); i++) {
                if (popList.get(i).getSymbol().contains(K)) {
                    filterList.add(popList.get(i));
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (popList == null) {
                return false;
            }
            Util.hideKeyboard(etPop);
            etPop.clearFocus();
            handler.postDelayed(() -> {//使收起键盘时不闪烁
                filter();
                adapterPop.setData(filterList);
            }, 50);
            return true;
        }
        return false;
    }

    @Override
    protected void restart() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getInfo();
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            initRecord();
        } else {
            if (!isLogin() && (page >= 2)) {
                radioGroup.getChildAt(0).performClick();
            } else {
                int checkedId = radioGroup.getCheckedRadioButtonId();
                int position = radioGroup.indexOfChild(radioGroup.findViewById(checkedId));
                if (page == position) {
                    onCheckedChanged(radioGroup, checkedId);
                } else {
                    radioGroup.getChildAt(page).performClick();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doNext()) {
            if (isExit) {
                finish();
                System.exit(0);
            } else {
                ToastUtil.initToast(this, getString(R.string.pressExit) + " " +
                        Util.getAppName(this));
                isExit = true;
                handler.postDelayed(() -> isExit = false, 5000);//5秒内再按后退键真正退出
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                restart();
                return true;
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
                setSoft(true);
                restart();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setSoft(boolean resize) {
        int adjust = resize ? WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE :
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        getWindow().setSoftInputMode(adjust | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onStop() {
        stopUpdate(true, true, true, true);
        SocketUtil.close(client);
        super.onStop();
    }

    @Override
    public void finish() {
        stopUpdate(true, true, true, true);
        SocketUtil.close(client);
        super.finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        // NoLogin();
    }

    private void NoLogin() {
        //Util.checkLogin(this,mine.getCode());
        if (enter > 1200000) {
            if (!SharedUtil.isLogin(token)) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            } else {
                SharedPreferences password = getSharedPreferences("password", Context.MODE_PRIVATE);
                String zhiwen = password.getString("zhiwen", "");
                String shoushi = password.getString("shoushi", "");
                Log.i("TAG", "intoNextPage: " + "1111111111111" + zhiwen);
                if (zhiwen.equals("zhiwen")) {
                    Log.i("TAG", "intoNextPage: " + "1111111111111");
                    Thefingerprint.start(this);
                    finish();
                    return;
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                if (shoushi.equals("shoushi")) {
                    Yestures.start(this);
                    finish();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }


            }

        }
    }


    @Override
    public void onForeground(Activity activity) {
        enter = (System.currentTimeMillis() - useTime);
        Log.i("11111", "onForeground: " + enter);


        NoLogin();

    }

    @Override
    public void onBackground(Activity activity) {
        useTime = System.currentTimeMillis();
        time.edit().putLong("usetime", useTime).commit();
    }
}
