package cn.suozhi.DiBi.home.view;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.InvitePosterDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.InviteRVAdapter;
import cn.suozhi.DiBi.home.model.BannerEntity;
import cn.suozhi.DiBi.home.model.CommissionEntity;
import cn.suozhi.DiBi.home.model.CommissionListEntity;
import cn.suozhi.DiBi.home.model.FriendEntity;
import cn.suozhi.DiBi.home.model.InviteEntity;

/**
 * 邀请好友
 */
public class InviteActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, OnCallbackListener, View.OnClickListener {

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.cl_invFloat)
    public ConstraintLayout clFloat;
    @BindViews({R.id.tv_invFloatFriend, R.id.tv_invFloatCommission})
    public List<TextView> tvsFloat;

    @BindView(R.id.ll_friendFoot)
    public LinearLayout llFriend;
    @BindView(R.id.ll_commissionFoot)
    public LinearLayout llCommission;
    @BindView(R.id.tv_friendCode)
    public TextView tvCode;
    @BindView(R.id.tv_comFootToday)
    public TextView tvToday;
    @BindView(R.id.tv_comFootTotal)
    public TextView tvTotal;

    private List<TextView> tvsHead;

    private int page, nextPage;//当前页 / 下一页
    private final int pageSize = 15;//每页数量
    private InviteRVAdapter recyclerAdapter;
    private List<InviteEntity> dataList;

    private int index = 0;

    private String code, link;
    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        setSystemUI();
        return R.layout.activity_invite;
    }

    @Override
    protected void init() {
        lang = SharedUtil.getLanguage4Url(this);
        showLoading();

        View head = LayoutInflater.from(this).inflate(R.layout.float_invite, null);
        findWidget(head);
        initFloat();

        int height = Util.getPhoneHeight(this) / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new InviteRVAdapter(this, height, this);
        recyclerView.setAdapter(recyclerAdapter.addHeaderView(R.layout.head_invite)
                .addHeaderView(head)
                .addFooterView(R.layout.foot_invite)
                .setEmptyView(R.layout.empty_tips)
                .setEmptyBackgroundResource(R.color.white));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager == null) {
                    return;
                }
                int firstItem = manager.findFirstVisibleItemPosition();
                if (firstItem >= 1 && clFloat.getVisibility() != View.VISIBLE) {
                    clFloat.setVisibility(View.VISIBLE);
                } else if (firstItem == 0 && clFloat.getVisibility() != View.GONE) {
                    clFloat.setVisibility(View.GONE);
                }
            }
        });

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    private void findWidget(View head) {
        tvsHead = new ArrayList<>();
        tvsHead.add(head.findViewById(R.id.tv_invFloatFriend));
        tvsHead.add(head.findViewById(R.id.tv_invFloatCommission));
        head.findViewById(R.id.v_invFloatFriend).setOnClickListener(this);
        head.findViewById(R.id.v_invFloatCommission).setOnClickListener(this);
    }

    private void initFloat() {
        for (int i = 0; i < tvsHead.size(); i++) {
            tvsHead.get(i).setTextSize(index == i ? 16 : 14);
            tvsFloat.get(i).setTextSize(index == i ? 16 : 14);
        }

        llFriend.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        llCommission.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void loadData() {
        page = 1;
        nextPage = 1;
        getData();
    }

    private void getData() {
        String token = SharedUtil.getToken(this);
        if (index == 1) {
            OkHttpUtil.getJsonHeader2(Constant.URL.GetCommissionList, token, lang, this,
                    "pageNum", page + "", "pageSize", pageSize + "");
            OkHttpUtil.getJsonHeader2(Constant.URL.GetCommission, token, lang, this);
        } else {
            OkHttpUtil.getJsonHeader2(Constant.URL.GetInviteList, token, lang, this,
                    "pageNum", page + "", "pageSize", pageSize + "");
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e("loge", "Invite: " + json);
        switch (url) {
            case Constant.URL.GetInviteList:
                FriendEntity invite = JsonUtil.fromJsonO(json, FriendEntity.class);
                if (page == 1) {
                    dismissLoading();
                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    } else {
                        dataList.clear();
                    }
                }
                removeLoading();
                if (invite == null) {
                    break;
                }
                if (Constant.Int.SUC == invite.getCode()) {
                    FriendEntity.DataEntity d = invite.getData();
                    code = d.getInviteCode();
                    link = d.getH5InviteUrl();
                    tvCode.setText(getString(R.string.inviteCodeColon) + code);

                    FriendEntity.DataEntity.UserEntity record = d.getInviteUserList();
                    for (int i = 0; i < record.getRecords().size(); i++) {
                        FriendEntity.DataEntity.UserEntity.RecordsEntity r = record.getRecords().get(i);
                        dataList.add(new InviteEntity(r.getPic(), r.getUserCode(), r.getCreatedDate(), 0));
                    }
                    if (record.getCurrent() < record.getPages()) {//还有下一页
                        dataList.add(new InviteEntity(2));
                        nextPage = page + 1;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                recyclerAdapter.setData(dataList);
                break;
            case Constant.URL.GetBanner:
                BannerEntity banner = JsonUtil.fromJsonO(json, BannerEntity.class);
                dismissLoading();
                if (banner == null) {
                    break;
                }
                if (Constant.Int.SUC == banner.getCode()) {
                    List<BannerEntity.DataEntity> list = banner.getData();
                    if (list != null && list.size() > 0) {
                        String poster = list.get(0).getBannerImgUrl();
                        float height, qr, left, top;
                        BannerEntity.DataEntity.QrEntity qp = list.get(0).getQrPosition();
                        if (qp == null || qp.getWidth() <= 0 || qp.getHeight() <= 0 || qp.getLength() <= 0) {
                            height = 1.35F;
                            qr = 0.15F;
                            left = 0.77F;
                            top = 0.88F;
                        } else {
                            int pw = qp.getWidth();
                            int qw = qp.getLength();
                            height = qp.getHeight() * 1.0F / pw;
                            qr = qw * 1.0F / pw;
                            left = qp.getX() < 0 ? 0.77F: (qp.getX() + qw / 2.0F) / pw;
                            top = qp.getY() < 0 ? 0.88F: (qp.getY() + qw / 2.0F) / qp.getHeight();
                        }
                        InvitePosterDialog.newInstance(link, poster, height, qr, left, top)
                                .show(this);
                    }
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            banner.getCode(), banner.getMsg()));
                }
                break;
            case Constant.URL.GetCommissionList:
                CommissionListEntity comList = JsonUtil.fromJsonO(json, CommissionListEntity.class);
                if (page == 1) {
                    dismissLoading();
                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    } else {
                        dataList.clear();
                    }
                }
                removeLoading();
                if (comList == null) {
                    break;
                }
                if (Constant.Int.SUC == comList.getCode()) {
                    CommissionListEntity.DataEntity d = comList.getData();
                    for (int i = 0; i < d.getRecords().size(); i++) {
                        CommissionListEntity.DataEntity.RecordsEntity c = d.getRecords().get(i);
                        dataList.add(new InviteEntity(c.getCurrency(), c.getRakeBackAmount(), c.getType(),
                                c.getSettleTime(), 1));
                    }
                    if (d.getCurrent() < d.getPages()) {//还有下一页
                        dataList.add(new InviteEntity(2));
                        nextPage = page + 1;
                    } else {
                        addBaseLine();
                    }
                } else {
                    addBaseLine();
                }
                recyclerAdapter.setData(dataList);
                break;
            case Constant.URL.GetCommission:
                CommissionEntity com = JsonUtil.fromJsonO(json, CommissionEntity.class);
                if (com == null) {
                    break;
                }
                if (Constant.Int.SUC == com.getCode()) {
                    tvToday.setText("≈" + AppUtil.roundRemoveZero(com.getData().getTodayRakeBack(), 4));
                    tvTotal.setText("≈" + AppUtil.roundRemoveZero(com.getData().getTotalRakeBack(), 4));
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            com.getCode(), com.getMsg()));
                    Util.checkLogin(this, com.getCode());
                }
                break;
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
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void removeLoading() {
        if (dataList.size() > 0) {
            if (dataList.get(dataList.size() - 1).getType() == 2) {
                dataList.remove(dataList.size() - 1);
            }
        }
    }

    private void addBaseLine() {
        if (page != 1) {
            dataList.add(new InviteEntity(3));
        }
    }

    @Override
    public synchronized void onCallback() {
        if (nextPage == page + 1) {
            page++;
            getData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_invFloatFriend:
                index = 0;
                loadInvite();
                break;
            case R.id.v_invFloatCommission:
                index = 1;
                loadInvite();
                break;
        }
    }

    private void loadInvite() {
        showLoading();
        initFloat();
        loadData();
    }

    @OnClick({R.id.iv_inviteBack, R.id.v_invFloatFriend, R.id.v_invFloatCommission,
            R.id.tv_friendCode, R.id.tv_friendPoster})
    public void invite(View v) {
        switch (v.getId()) {
            case R.id.iv_inviteBack:
                onBackPressed();
                break;
            case R.id.v_invFloatFriend:
                index = 0;
                loadInvite();
                break;
            case R.id.v_invFloatCommission:
                index = 1;
                loadInvite();
                break;
            case R.id.tv_friendCode:
                if (!TextUtils.isEmpty(code)) {
                    Util.copyBoard(this, code);
                    ToastUtil.initToast(this, R.string.copyDone);
                }
                break;
            case R.id.tv_friendPoster:
                if (!TextUtils.isEmpty(link)) {
                    showLoading();
                    OkHttpUtil.getJson(Constant.URL.GetBanner, this, "category", "2",
                            "lang", lang, "platform", "2");
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
