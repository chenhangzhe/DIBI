package cn.suozhi.DiBi.c2c.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.netease.nim.uikit.impl.preference.UserPreferences;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.model.C2cCoinEnity;
import cn.suozhi.DiBi.c2c.model.C2cLCoinEntity;
import cn.suozhi.DiBi.c2c.view.chat.ChatListActivity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.dialog.C2cPickDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.home.view.BindPhoneActivity;

/**
 * 法币 - Fragment
 */
public class C2cFragment extends BaseFragment implements TabLayout.OnTabSelectedListener, C2cPickDialog.OnPostCoinAndPayLinsenter,
        OkHttpUtil.OnDataListener, ViewPager.OnPageChangeListener, BaseDialog.OnItemClickListener {
    @BindView(R.id.tl_marketCoin)
    public TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.ll_tip)
    public LinearLayout llTip;

    @BindView(R.id.iv_add_advance)
    public ImageView ivAddAdvance;

    @BindView(R.id.tv_msg_hint)
    TextView tvMsgHint;

    @BindView(R.id.iv_pick)
    public ImageView ivPick;

    private FragmentStatePagerAdapter fragmentStatePagerAdapter;

    private String[] TABUser;//Tab文字
    private String[] TABBusiness;//Tab文字
    private boolean hasLoading = true;
    private int currentPos;

    private UserEntity mine;
    //用户类型 1 普通用户  2 商家
    private int userType;
    //每次刷新后选择显示的位置
    private int selectPos = 0;
    private List<Fragment> fragmentsUser;
    private List<Fragment> fragmentsBussiness;
    private boolean isBindPhone;
    private int verifiedLevel;

    private String payMode = "";//筛选条件  支付方式
    private String legalCurrencyCode = "";//筛选条件  法币类型
    private BuyUsdtFragment buyUsdtFragment;
    private BuyUsdtFragment sellAndSellUsdtFragment;
    boolean isFirstLoad = false;
    private int totalUnreadCount; //未读消息数
    private int verifiedstatus;
    private int idType;
    private C2cPickDialog c2cPickDialog;

    private int payType = 0;

    @BindView(R.id.tl_c2c_tab)
    public TabLayout tab2;
    private List<String> lTitles;
    private String[] TITLE;
    private int currentPos_c,selectPos_c;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_c2c;
    }

    @Override
    protected void init(View v) {
        super.init(v);
        llTip.setVisibility(View.GONE);
        llTip.bringToFront();

        TABBusiness = new String[]{getString(R.string.c2c_buy), getString(R.string.c2c_sell), getString(R.string.c2c_orders), getString(R.string.c2c_advert)};
        TABUser = new String[]{getString(R.string.c2c_buy), getString(R.string.c2c_sell), getString(R.string.c2c_orders)};

        getCoin();
        registerNotify(true);
        initData();
    }

    private void initData() {
        fragmentsUser = new ArrayList<>();
        fragmentsBussiness = new ArrayList<>();
    }

    @Override
    protected void loadData() {
        super.loadData();
        getUnreadMsg();
        lang = SharedUtil.getLanguage4Url(getActivity());
        getUserInfo();
        OkHttpUtil.getJsonToken(Constant.URL.getPayType, SharedUtil.getToken(getContext()), this);
    }

    private void getUnreadMsg() {
        totalUnreadCount = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        String count = "";
        if (tvMsgHint != null) {
            if (totalUnreadCount > 99) {
                tvMsgHint.setVisibility(View.VISIBLE);
                count = "99+";
                tvMsgHint.setText(count);
            } else if (totalUnreadCount <= 0) {
                tvMsgHint.setVisibility(View.GONE);
            } else {
                tvMsgHint.setVisibility(View.VISIBLE);
                count = totalUnreadCount + "";
                tvMsgHint.setText(count);
            }
        }


    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        OkHttpUtil.getJsonToken(Constant.URL.GetInfo, SharedUtil.getToken(getContext()), this);
    }


    public void refresh() {
        loadData();
    }

    private void registerNotify(boolean isRegister) {
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        getUnreadMsg();
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, isRegister);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentPos = tab.getPosition();
        selectPos = tab.getPosition();
        viewPager.setCurrentItem(tab.getPosition());
        tab.select();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
//        onTabSelected(tab);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerNotify(false);
    }

    @OnClick({R.id.iv_pick, R.id.iv_add_advance, R.id.iv_messge,R.id.iv_tip})
    public void c2cClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pick:
                c2cPickDialog = C2cPickDialog.newInstance(legalCurrencyCode, payMode);
                c2cPickDialog.setOnPostCoinAndPayLinsenter(this);
                c2cPickDialog.show(getActivity());
                break;
            case R.id.iv_add_advance:
                if (isBindPhone){
                    startActivity(new Intent(getContext(), PublishAdvanceActivity.class));
                }else {
                    showAuthDialog(getString(R.string.str_c2c_trade_hint), getString(R.string.str_go_bind), getString(R.string.str_my_think_argin));
                }
                break;
            case R.id.iv_messge:
                startActivity(new Intent(getContext(), ChatListActivity.class));

                break;
            case R.id.iv_tip:
                int is_v = llTip.getVisibility();
                // 0：可见 VISIBLE    4：不可见，但是占用布局空间 INVISIBLE   8：不可见也不占用布局空间 GONE
                if(is_v == 0){
                    llTip.setVisibility(View.GONE);
                } else {
                    llTip.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void showAuthDialog(String content, String comfirm, String cancel) {
        AuthDialog.newInstance(content,
                comfirm,
                cancel).setOnItemClickListener(this)
                .show(getActivity());
    }

    /**
     * 筛选框点击确定
     */
    @Override
    public void onClickComfirm(String currentCoin, int payType) {
        payMode = payType + "";
        legalCurrencyCode = currentCoin;

        if (buyUsdtFragment == null || sellAndSellUsdtFragment == null) {
            return;
        }

        if (currentPos == 0) {
            buyUsdtFragment.update(legalCurrencyCode, payMode.equals("0") ? "" : payMode, currentPos == 0 ? 1 : 2);
            sellAndSellUsdtFragment.setParam(legalCurrencyCode, payMode.equals("0") ? "" : payMode,true);
        } else {
            sellAndSellUsdtFragment.update(legalCurrencyCode, payMode, currentPos == 0 ? 1 : 2);
            buyUsdtFragment.setParam(legalCurrencyCode, payMode.equals("0") ? "" : payMode,true);
        }
    }


    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.GetInfo)) {
            mine = GsonUtil.fromJson(json, UserEntity.class);
            if (Constant.Int.SUC == mine.getCode()) {
                UserEntity.DataEntity.InfoEntity data = mine.getData().getInfo();
                if (data == null) {
                    return;
                }
                setUi(data);
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        mine.getCode(), mine.getMsg()));
                Util.checkLogin(getContext(), mine.getCode());
            }
        } else if (url.equals(Constant.URL.currencies)){
            C2cLCoinEntity entity = GsonUtil.fromJson(json,C2cLCoinEntity.class);
            if (Constant.Int.SUC == entity.getCode()){
                lTitles = new ArrayList<>();
                int n = entity.getData().size();
                for(int i = 0 ; i < n ; i++){
                    lTitles.add(entity.getData().get(i).getCode());
                }
                TITLE = lTitles.toArray(new String[lTitles.size()]);

                Util.tabC2cInit(tab2, TITLE, R.layout.tab_type_coin_selected, R.id.tv_tabTypePBName, 0, selectPos, R.id.v_tabTypePBIndicator, 0.1F);
                tab2.setTabMode(TabLayout.MODE_SCROLLABLE);
                tab2.addOnTabSelectedListener(new myListener());
                onClickComfirm(lTitles.get(0),payType);
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        entity.getCode(), entity.getMsg()));
                Util.checkLogin(getContext(), entity.getCode());
            }
        }
    }


    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(getActivity(), url, error);
    }

    /**
     * 更新界面
     */
    private void setUi(UserEntity.DataEntity.InfoEntity data) {
        if (data == null) return;
        userType = data.getUserType();
        isBindPhone = data.getPhoneEnabled() == 1;
        verifiedLevel = data.getVerifiedLevel();
        verifiedstatus = data.getVerifiedStatus();
        idType = data.getIdType();
        int userId = data.getUserId();
        //保存图像
        UserPreferences.saveAvastar(UserPreferences.Avastar, data.getPic());

        fragmentsUser.clear();
        fragmentsBussiness.clear();

        buyUsdtFragment = BuyUsdtFragment.getInstance(1, isBindPhone, verifiedLevel, legalCurrencyCode, payMode,
                userType == Constant.Int.BUSSINESS, verifiedstatus, idType,userId);
        sellAndSellUsdtFragment = BuyUsdtFragment.getInstance(2, isBindPhone, verifiedLevel, legalCurrencyCode, payMode,
                userType == Constant.Int.BUSSINESS, verifiedstatus, idType,userId);


        fragmentsUser.add(buyUsdtFragment);
        fragmentsUser.add(sellAndSellUsdtFragment);
        fragmentsUser.add(MyOrderFragment.getInstance(userType == Constant.Int.BUSSINESS));


        fragmentsBussiness.add(buyUsdtFragment);
        fragmentsBussiness.add(sellAndSellUsdtFragment);
        fragmentsBussiness.add(MyOrderFragment.getInstance(userType == Constant.Int.BUSSINESS));
        fragmentsBussiness.add(MyAdvanceFragment.getInstance(userType == Constant.Int.BUSSINESS));

        if (userType == Constant.Int.BUSSINESS) {
            viewPager.setOffscreenPageLimit(4);
            Util.tabC2cInit(tabLayout, TABBusiness, R.layout.tab_type_purple_black_normal, R.id.tv_tabTypePBName, 0,
                    selectPos, R.id.v_tabTypePBIndicator, 0.5F);
            ivAddAdvance.setVisibility(View.VISIBLE);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            setViewPager(TABBusiness, fragmentsBussiness);
        } else {
            viewPager.setOffscreenPageLimit(3);
            Util.tabC2cInit(tabLayout, TABUser, R.layout.tab_type_purple_black_normal, R.id.tv_tabTypePBName, 0,
                    selectPos, R.id.v_tabTypePBIndicator, 0.5F);
            ivAddAdvance.setVisibility(View.GONE);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            setViewPager(TABUser, fragmentsUser);
        }

        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(selectPos);
    }

    private void setViewPager(String[] tab, List<Fragment> fragments) {
        viewPager.setAdapter(fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            //此方法用来显示tab上的名字
            @Override
            public CharSequence getPageTitle(int position) {
                return tab[position];
            }

            @Override
            public Fragment getItem(int position) {
                //创建Fragment并返回
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return tab == null ? 0 : tab.length;
            }

        });
    }

    class myListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            currentPos_c = tab.getPosition();
            selectPos_c = tab.getPosition();
            tab.select();
            onClickComfirm(lTitles.get(currentPos_c),payType);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPos = position;
        selectPos = position;
        tabLayout.getTabAt(position).select();
        viewPager.setCurrentItem(position);
        if (position == 3 || position == 2) {
            ivPick.setVisibility(View.GONE);
            tab2.setVisibility(View.GONE);
        } else {
            ivPick.setVisibility(View.VISIBLE);
            tab2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_auth) {
            //去绑定手机
            startActivity(new Intent(getContext(), BindPhoneActivity.class));
        }
    }

    // 获取法币币种
    private void getCoin() {
        OkHttpUtil.getJson(Constant.URL.currencies, this);
    }

}
