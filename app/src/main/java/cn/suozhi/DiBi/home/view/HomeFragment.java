package cn.suozhi.DiBi.home.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.MainActivity;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.custom.ADTextView;
import cn.suozhi.DiBi.common.custom.GravitySnapHelper;
import cn.suozhi.DiBi.common.custom.IndicatorView;
import cn.suozhi.DiBi.common.custom.ScrollViewPager;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.contract.view.ContractActivity;
import cn.suozhi.DiBi.hide.view.VoteActivity;
import cn.suozhi.DiBi.home.adapter.HomeRVAdapter;
import cn.suozhi.DiBi.home.adapter.HomeSnapRVAdapter;
import cn.suozhi.DiBi.home.model.BannerEntity;
import cn.suozhi.DiBi.home.model.NotifyEntity;
import cn.suozhi.DiBi.home.model.QuoteEntity;
import cn.suozhi.DiBi.home.model.SingleWordEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.login.LoginActivity;

/**
 * 首页 - Fragment
 */
public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, ViewPager.OnPageChangeListener, ADTextView.OnItemClickListener,
        View.OnClickListener, AbsRecyclerAdapter.OnItemClickListener, TabLayout.OnTabSelectedListener,
        BaseDialog.OnItemClickListener {

    @BindView(R.id.v_homeDot)
    public View vDot;
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    private ScrollViewPager viewPager;
    private IndicatorView ivBanner;
    private ADTextView atv;
    private RecyclerView recyclerTop;
    private IndicatorView ivTop;
    private TabLayout tabLayout;
    private ImageView ivRate;

    private RadioGroup rgHome;

    private String[] TAB;//Tab文字
    private BannerAdapter pagerAdapter;
    private List<BannerEntity.DataEntity> bannerList;
    private HomeRVAdapter recyclerAdapter;
    private HomeSnapRVAdapter topAdapter;
    private List<QuoteEntity> topList;
    private List<QuoteEntity> quoteList;

    private int[] NEW_SORT = {R.mipmap.triangle_double_gray, R.mipmap.triangle_double_gray_blue,
            R.mipmap.triangle_double_blue_gray};//新币榜排序方式
    private int sort_index = 0;//排序方式
    private long mills;

    private boolean hasLoading = true;
    private LoadingDialog loadingDialog;

    @BindView(R.id.tv_language_choose)
    public TextView tvLC;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(View v) {
        lang = SharedUtil.getLanguage4Url(getActivity());

        if(lang == "zh_CN" || lang.equals("zh_CN")){
            tvLC.setText("简");
            tvLC.setTextSize(15);
        } else if (lang == "zh_TW" || lang.equals("zh_TW")){
            tvLC.setText("繁");
            tvLC.setTextSize(15);
        } else if (lang == "en_US" || lang.equals("en_US")){
            tvLC.setText("EN");
            tvLC.setTextSize(11);
        }

        View head = LayoutInflater.from(getActivity()).inflate(R.layout.head_home, null);
        findWidget(head);
        atv.setSize(12, 10)
                .setColor(Util.getColor(getActivity(), R.color.black), Util.getColor(getActivity(), R.color.black))
                .setOnItemClickListener(this);
        initPager();
        initSymbol();

        TAB = new String[]{getString(R.string.newRank), getString(R.string.riseRank),
                getString(R.string.fallRank)};
        Util.tabInit(tabLayout, TAB);
        tabLayout.addOnTabSelectedListener(this);

        int height = Util.getPhoneHeight(getActivity()) / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new HomeRVAdapter(getActivity(), height);
        recyclerView.setAdapter(recyclerAdapter.addHeaderView(head)
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    private void findWidget(View head) {
            viewPager = head.findViewById(R.id.svp_homeHeadBanner);
        ivBanner = head.findViewById(R.id.iv_homeHeadIndBan);
        atv = head.findViewById(R.id.atv_homeHeadNotify);
        head.findViewById(R.id.tv_homeHeadNotifyMore).setOnClickListener(this);
        recyclerTop = head.findViewById(R.id.rv_homeHeadTop);
        ivTop = head.findViewById(R.id.iv_homeHeadIndTop);
        head.findViewById(R.id.cl_homeHeadContract).setOnClickListener(this);
//        head.findViewById(R.id.cl_homeHeadGame).setOnClickListener(this);
//        head.findViewById(R.id.tv_consensus_exchange).setOnClickListener(this);
//        head.findViewById(R.id.tv_consensus_exchange_text).setOnClickListener(this);
        head.findViewById(R.id.tv_help_center).setOnClickListener(this);
        tabLayout = head.findViewById(R.id.tl_homeHeadRank);
        head.findViewById(R.id.tv_thRate).setOnClickListener(this);
        ivRate = head.findViewById(R.id.iv_thTriangle);
        ivRate.setOnClickListener(this);

        rgHome = head.findViewById(R.id.rg_week_vote);
//        rgHome.setOnCheckedChangeListener(this);
        head.findViewById(R.id.rb_home_1).setOnClickListener(this);
        head.findViewById(R.id.rb_home_2).setOnClickListener(this);
        head.findViewById(R.id.rb_home_3).setOnClickListener(this);

    }

    /**
     * 初始化头部交易对
     */
    private void initSymbol() {
        recyclerTop.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        new GravitySnapHelper(Gravity.START).attachToRecyclerView(recyclerTop);
        topAdapter = new HomeSnapRVAdapter(getActivity());
        recyclerTop.setAdapter(topAdapter.setOnItemClickListener(this));
        recyclerTop.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (manager == null) {
                        return;
                    }
                    int firstItem = manager.findFirstVisibleItemPosition();
                    ivTop.setCurrentItem(firstItem);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPager() {
        pagerAdapter = new BannerAdapter();
        viewPager.setSlideBorderMode(ScrollViewPager.SLIDE_BORDER_MODE_CYCLE)
                .setInterval(2500)
                .setAutoScrollDurationFactor(4)
                .setAdapter(pagerAdapter);
        viewPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    refreshLayout.setEnabled(false);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    refreshLayout.setEnabled(true);
                    break;
            }
            return false;
        });
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJson(Constant.URL.GetBanner, this, "category", "1",
                "lang", lang, "platform", "2");

        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang,
                "pageNum", "1", "pageSize", "6", "typeCode", "announcementCenter");

        String token = SharedUtil.getToken(getActivity());
        if (SharedUtil.isLogin(token)) {
            OkHttpUtil.getJsonToken(Constant.URL.GetUnread, token, this);
        }

        loadSymbol();
    }

    public void loadSymbol() {
        if (loadListener == null) {
            return;
        }
        loadListener.onLoad(Constant.Code.Type_Home_Top, Constant.Code.Home_Top,
                hasLoading ? recyclerView : null);
        loadTab();
    }

    private void loadTab() {
        if (loadListener == null || tabLayout == null) {
            return;
        }
        int position = tabLayout.getSelectedTabPosition();
        if (position < 0 || position >= TAB.length) {
            return;
        }
        int type;
        long id;
        switch (position) {
            case 1:
                type = Constant.Code.Type_Home_Rise;
                id = Constant.Code.Home_Rise;
                break;
            case 2:
                type = Constant.Code.Type_Home_Fall;
                id = Constant.Code.Home_Fall;
                break;
            case 0:
            default:
                type = Constant.Code.Type_Home_New;
                id = Constant.Code.Home_New;
                break;
        }
        String[] order = getOrder(position);
        loadListener.onLoad(type, id, hasLoading ? tabLayout : null, order[0], order[1]);
    }

    /**
     * 获取排序方式
     */
    private String[] getOrder(int position) {
        if (position == 1 || position == 2) {
            return new String[]{null, null};
        }
        if (sort_index == 1) {
            return new String[]{"RATE", "DESC"};
        }
        if (sort_index == 2) {
            return new String[]{"RATE", "ASC"};
        }
        return new String[]{null, null};
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Home: " + json + "|session:" + session);
        switch (url) {
            case Constant.URL.GetBanner:
                BannerEntity banner = JsonUtil.fromJsonO(json, BannerEntity.class);
                if (banner == null) {
                    break;
                }
                if (Constant.Int.SUC == banner.getCode()) {
                    bannerList = banner.getData();
//                    Log.e("loge", "bannerList: " + bannerList);
                    pagerAdapter.setData(bannerList);
                    if (bannerList.size() > 1) {
                        viewPager.setCurrentItem(1, false);
                        viewPager.startAutoScroll();
                    }
                }
                break;
            case Constant.URL.GetNotify:
                NotifyEntity notify = JsonUtil.fromJsonO(json, NotifyEntity.class);
//                Log.e("loge", "notify: " + notify);
                if (notify == null) {
                    break;
                }
                if (Constant.Int.SUC == notify.getCode()) {
                    if (notify.getData() != null) {
                        List<NotifyEntity.DataEntity.RecordsEntity> list = notify.getData().getRecords();
                        if (list != null && list.size() > 0) {
                            long d3 = Util.Day_1 * 3;
                            for (int i = 0; i < list.size(); i++) {
                                long time = Util.parseTime(list.get(i).getCreateTime());
                                list.get(i).setNew(System.currentTimeMillis() - time < d3);
                            }
                            atv.setTexts(list);
                        }
                    }
                }
                break;
            case Constant.URL.GetUnread:
                SingleWordEntity unread = JsonUtil.fromJsonO(json, SingleWordEntity.class);
                if (unread == null) {
                    break;
                }
                if (Constant.Int.SUC == unread.getCode()) {
                    vDot.setVisibility(unread.getData().getCount() > 0 ? View.VISIBLE : View.GONE);
                } else {
                    vDot.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, String error) {
        if (Constant.URL.GetUnread.equals(url)) {
            vDot.setVisibility(View.GONE);
        }
        dismissLoading();
        Util.saveLog(getActivity(), url, error);
    }

    @Override
    protected void showLoading() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getActivity());
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

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgEgnConfirm) {
            String alias = (String) v.getTag();
            showLoading();
            OkHttpUtil.putJsonToken(Constant.URL.UpdateGameName, SharedUtil.getToken(getActivity()),
                    this, "nickName", alias);
        }
    }

    private void getGame() {
        showLoading();
        OkHttpUtil.getJsonToken(Constant.URL.GetGame, SharedUtil.getToken(getActivity()), this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int position) {
        if (ivBanner == null) {
            return;
        }
        if (position == 0) {
            ivBanner.setCurrentItem(bannerList.size() - 1);
        } else if (position == bannerList.size() + 1) {
            ivBanner.setCurrentItem(0);
        } else {
            ivBanner.setCurrentItem(position - 1);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (viewPager == null) {
            return;
        }
        if (state == 0) {
            if (viewPager.getCurrentItem() == bannerList.size() + 1) {
                viewPager.setCurrentItem(1, false);
            } else if (viewPager.getCurrentItem() == 0) {
                viewPager.setCurrentItem(bannerList.size(), false);
            }
        }
    }

    public void updateData(long id, List<Messages.DmQuote> list, Map<String, Symbol> map) {
        if (id == Constant.Code.Home_Top) {
            initTop(list, map);
            ivTop.setCount((topList.size() + 2) / 3);
        } else {
            int position = tabLayout.getSelectedTabPosition();
            if (id == Constant.Code.Home_New) {
                if (position == 0) {
                    setData(list, map);
                }
            } else if (id == Constant.Code.Home_Rise) {
                if (position == 1) {
                    setData(list, map);
                }
            } else if (id == Constant.Code.Home_Fall) {
                if (position == 2) {
                    setData(list, map);
                }
            }
        }
    }

    private void initTop(List<Messages.DmQuote> list, Map<String, Symbol> map) {
        if (topList == null) {
            topList = new ArrayList<>();
        } else {
            topList.clear();
        }
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            topList.add(AppUtil.getQuote(list.get(i), map));
        }
        topAdapter.setDataList(topList);
        mills = System.currentTimeMillis();
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

    public void updateTop(Messages.DmQuote q) {
        if (q == null || topList == null || topList.size() == 0) {
            return;
        }
        int index = -1;
        for (int i = 0; i < topList.size(); i++) {
            if (topList.get(i).getSymbol().equals(q.getSymbol())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            topList.get(index).setValue(q.getPrice(), AppUtil.getQuoteRate(q), q.getVolume(), q.getCloseCny());
            topAdapter.setDataList(topList);
        }
    }

    public void updateQuote(Messages.DmQuote q) {
        if (q == null || quoteList == null || quoteList.size() == 0) {
            return;
        }
//        updateTop(q);
        if (mills > 0 && System.currentTimeMillis() - mills > 30L * 1000) {
            loadListener.onLoad(Constant.Code.Type_Home_Top, Constant.Code.Home_Top, null);
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            ivRate.setVisibility(View.VISIBLE);
            ivRate.setImageResource(NEW_SORT[sort_index]);
        } else {
            ivRate.setVisibility(View.GONE);
        }
        hasLoading = true;
        loadTab();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    @OnClick({R.id.iv_homeMessage, R.id.iv_homeUser , R.id.tv_language_choose,R.id.iv_homeLogo})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.iv_homeMessage:
                if (SharedUtil.isLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), MessageActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.iv_homeUser:
                if (loadListener != null) {
                    loadListener.onLoad(0, 0);
                }
                break;
            case R.id.tv_language_choose:
                if (loadListener != null) {
                    loadListener.onLoad(4, 0);
                }
                break;
            case R.id.iv_homeLogo:
                break;
        }
    }

    @Override
    public void onClick(long id, String title) {
        startActivity(new Intent(getActivity(), NotifyDetailActivity.class)
                .putExtra("id", id));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_homeHeadNotifyMore://公告列表
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                break;
            case R.id.iv_homeBanner://Banner
                int index = (int) v.getTag(R.id.tag_relation);
                String link = bannerList.get(index).getBannerImgHref();
                if (TextUtils.isEmpty(link)) {
                    break;
                }
                if (Util.isLink(link)) {
                    if (bannerList.get(index).getBannerImgHrefType() == 2) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    } else {
                        startActivity(new Intent(getActivity(), H5Activity.class)
                                .putExtra("url", link)
                                .putExtra("title", bannerList.get(index).getTitle()));
                    }
                } else if (link.startsWith(Constant.Strings.Banner_Notify)) {
                    long nid = AppUtil.getNotifyId(link, "#");
                    if (nid > 0) {
                        startActivity(new Intent(getActivity(), NotifyDetailActivity.class)
                                .putExtra("id", nid));
                    }
                }
                break;
            case R.id.cl_homeHeadContract://预测合约
                startActivity(new Intent(getActivity(), ContractActivity.class));
                break;
            case R.id.tv_thRate:
            case R.id.iv_thTriangle://新币排序
                sort_index = (sort_index + 1) % NEW_SORT.length;
                ivRate.setImageResource(NEW_SORT[sort_index]);
                hasLoading = true;
                loadTab();
                break;
            case R.id.tv_help_center: // 帮助中心
                startActivity(new Intent(getActivity(), HelpCenterActivity.class));
                break;
            case R.id.rb_home_1:
            case R.id.rb_home_2:
                ToastUtil.initToast(getActivity(),getString(R.string.consensus_exchange_not_open));
//                startActivity(new Intent(getActivity(), VoteActivity.class));
                break;
            case R.id.rb_home_3:
                startActivity(new Intent(getActivity(), ConsensusExchangeActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.cv_htItem:
            case R.id.cl_homeItem:
                if (loadListener != null) {
                    loadListener.onLoad(Constant.Code.Type_Trade, 0, v);
                }
                break;
        }
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

    class BannerAdapter extends PagerAdapter {

        private List<View> imgList;

        public void setData(List<BannerEntity.DataEntity> data) {
            if (imgList == null) {
                imgList = new ArrayList<>();
            } else {
                imgList.clear();
            }
            int size = data.size();
            if (ivBanner != null) {
                ivBanner.setCount(size);
            }
            if (size > 1) {
                imgList.add(initImage(size - 1, data.get(size - 1).getBannerImgUrl()));
            }
            for (int i = 0; i < size; i++) {
                imgList.add(initImage(i, data.get(i).getBannerImgUrl()));
            }
            if (size > 1) {
                imgList.add(initImage(0, data.get(0).getBannerImgUrl()));
            }
            this.notifyDataSetChanged();
        }

        public View initImage(int index, String url) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.iv_home, null, false);
            ImageView iv = v.findViewById(R.id.iv_homeBanner);
            if (!TextUtils.isEmpty(url)) {
                GlideUtil.glidePE(getActivity(), iv, url, R.mipmap.img_loading, R.mipmap.img_fail,
                        Util.dp2px(getActivity(), 5));
            }
            iv.setTag(R.id.tag_relation, index);
            iv.setOnClickListener(HomeFragment.this);
            return v;
        }

        @Override
        public int getCount() {
            return imgList == null ? 0 : imgList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(imgList.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(imgList.get(position));
            return imgList.get(position);
        }
    }


}
