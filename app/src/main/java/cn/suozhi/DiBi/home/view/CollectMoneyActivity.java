package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.CollectMoneyEnity;
import cn.suozhi.DiBi.home.model.UserEntity;

public class CollectMoneyActivity extends BaseActivity implements OkHttpUtil.OnDataListener, BaseDialog.OnItemClickListener {

    @BindView(R.id.rly_bank)
    RelativeLayout rlyBank;
    @BindView(R.id.rly_alipay)
    RelativeLayout rlyAlipay;
    @BindView(R.id.rly_wechat)
    RelativeLayout rlyWechat;
    @BindView(R.id.tv_bank)
    TextView tvBank;
    @BindView(R.id.tv_alipay)
    TextView tvAlipay;
    @BindView(R.id.tv_wechat)
    TextView tvWechat;

    boolean isBindAlipay = false;
    boolean isBindweChat = false;
    boolean isBindBank = false;
    boolean isSeller = false;

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    private boolean isDone;
    private List<CollectMoneyEnity.DataBean.RecordsBean> records;
    private UserEntity.DataEntity.InfoEntity d;
    //认证级别
    private int verifiedLevel;
    private CollectMoneyEnity.DataBean.RecordsBean alipayData;
    private CollectMoneyEnity.DataBean.RecordsBean wechatData;
    private CollectMoneyEnity.DataBean.RecordsBean bankData;
    private String fullName;

    @Override
    protected int getViewResId() {
        return R.layout.activity_collect_money;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.str_collet_type), v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDone = false;
        OkHttpUtil.getJsonToken(Constant.URL.GetInfo, SharedUtil.getToken(this), this);
    }

    @OnClick({R.id.rly_bank, R.id.rly_alipay, R.id.rly_wechat})
    public void user(View v) {
        if (!isDone) {
            ToastUtil.initToast(this, R.string.retryAfterRefresh);
            return;
        }
        if (verifiedLevel == 0) {
            showAuthDialog();
            return;
        }
        switch (v.getId()) {
            case R.id.rly_bank:
                Intent intent = new Intent(mContext, BankActivity.class)
                        .putExtra("isBind", isBindBank)
                        .putExtra("data", bankData)
                        .putExtra("fullName", fullName)
                        .putExtra("isSeller", isSeller);
                startActivity(intent);
                break;
            case R.id.rly_alipay:
                toAliPayActivity(1, isBindAlipay, alipayData);

                break;
            case R.id.rly_wechat:
                toAliPayActivity(2, isBindweChat, wechatData);
                break;

        }
    }

    private void toAliPayActivity(int type, boolean isBind, CollectMoneyEnity.DataBean.RecordsBean data) {
        Intent intent = new Intent(mContext, AlipayActivity.class)
                .putExtra("type", type)
                .putExtra("isBind", isBind)
                .putExtra("data", data)
                .putExtra("fullName", fullName)
                .putExtra("isSeller", isSeller);
        startActivity(intent);
    }

    private void showAuthDialog() {
        AuthDialog.newInstance(ResUtils.getString(mContext, R.string.str_collect_money_hint),
                ResUtils.getString(mContext, R.string.str_go_auth),
                ResUtils.getString(mContext, R.string.cancel)).setOnItemClickListener(this)
                .show(this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Gather: " + json);
        dismissLoading();
        if (url.equals(Constant.URL.GetInfo)) {
            UserEntity mine = GsonUtil.fromJson(json, UserEntity.class);
            if (Constant.Int.SUC == mine.getCode()) {
                isDone = true;
                d = mine.getData().getInfo();
                verifiedLevel = d.getVerifiedLevel();
                fullName = d.getFullName();
                isSeller = d.getUserType() == 2;
                OkHttpUtil.getJsonToken(Constant.URL.collectMoneyType, SharedUtil.getToken(mContext), this
                        , "pageNum", "1",
                        "pageSize", "10");
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        mine.getCode(), mine.getMsg()));
                Util.checkLogin(this, mine.getCode());
            }
        } else if (url.equals(Constant.URL.collectMoneyType)) {
            CollectMoneyEnity collectMoneyEnity = GsonUtil.fromJson(json, CollectMoneyEnity.class);
            if (Constant.Int.SUC == collectMoneyEnity.getCode()) {
                isDone = true;
                records = collectMoneyEnity.getData().getRecords();
                updateUI(records);
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this,
                        collectMoneyEnity.getCode(), collectMoneyEnity.getMsg()));
                Util.checkLogin(this, collectMoneyEnity.getCode());
            }
        }
    }

    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(this, url, error);
    }


    private void updateUI(List<CollectMoneyEnity.DataBean.RecordsBean> records) {
        if (records == null || records.size() == 0) {
            tvBank.setText(getString(R.string.unbind));
            tvAlipay.setText(getString(R.string.unbind));
            tvWechat.setText(getString(R.string.unbind));
        } else {
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).getAccountType() == 1) {
                    tvAlipay.setText(getString(R.string.str_bind));
                    isBindAlipay = true;
                    alipayData = records.get(i);
                } else if (records.get(i).getAccountType() == 2) {
                    tvWechat.setText(getString(R.string.str_bind));
                    wechatData = records.get(i);
                    isBindweChat = true;
                } else if (records.get(i).getAccountType() == 3) {
                    tvBank.setText(getString(R.string.str_bind));
                    bankData = records.get(i);
                    isBindBank = true;
                }
            }
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_auth) {
            startActivity(new Intent(mContext, C1Activity.class));
        }
    }
}
