package cn.suozhi.DiBi.wallet.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.custom.ExampleCardPopup;
import cn.suozhi.DiBi.common.custom.RelativePopupWindow;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.home.view.C1Activity;
import cn.suozhi.DiBi.home.view.C2Activity;
import cn.suozhi.DiBi.home.view.IdentityResultActivity;
import cn.suozhi.DiBi.market.view.IeoTransferActivity;
import cn.suozhi.DiBi.wallet.adapter.WalletCoinAdapter;
import cn.suozhi.DiBi.wallet.model.CoinAssertEnity;
import cn.suozhi.DiBi.wallet.model.CoinEnity;

/**
 * 钱包 - Fragment
 */
public class WalletFragment extends BaseFragment implements AbsRecyclerAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OkHttpUtil.OnDataListener, BaseDialog.OnItemClickListener {

    @BindView(R.id.rv_item)
    RecyclerView rvCoin;

    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;

    private WalletCoinAdapter walletAdapter;
    private View header;
    private List<CoinEnity.DataBean> coinList,lanucnList,normalList;
    private TextView tvAccountOrder;
    private TextView tvChargeCoin;
    private TextView tvGetCoin;
    private TextView tvManageAddress;
    private CheckBox cbCoin;
    private TextView tvSmallCoin;
    private TextView tvTransferAccounts;
    private TextView tvIeoTransfer;
    private boolean isSelectHideSmall;
    //搜索的币种
    private String searchCoin = "";
    private EditText etSearchCoin;
    private boolean isDone;
    private UserEntity.DataEntity.InfoEntity data;
    private boolean isBindPhone;
    private boolean isBindEmail;
    private boolean isBindGoogle;
    private UserEntity mine;
    private String area;
    private String account;
    //认证等级
    private int verifiedLevel;
    private String allBtc;
    private String allCny;

    TextView tvCoinAmount;

    TextView tvCnyAmount;
    private ImageView ivIsEyes;
    private boolean isEyeVisible = false;
    private ImageView ivHelp;
    private View contentView;
    private ExampleCardPopup popup;
    private int vertPos;
    private int horizPos;
    private ExampleCardPopup popup2;
    private int horizPos2;
    private int vertPos2;
    private int verifiedstatus;
    private int idType;
    private TextView tvTitle,tvWSL,tvWSR;
    private int ctwlr = 0;

    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        return R.layout.fragment_wallet;
    }

    @Override
    protected void init(View v) {
        showLoading();

        initData();
        header = LayoutInflater.from(getContext()).inflate(R.layout.header_wallet, null);
        initHeader(header);

        int height = Util.getPhoneHeight(getActivity()) / 2;
        rvCoin.setLayoutManager(new LinearLayoutManager(getActivity()));
        walletAdapter = new WalletCoinAdapter(getActivity(), height);
        rvCoin.setAdapter(walletAdapter.addHeaderView(header)
                .setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
//        rvCoin.addItemDecoration(new DecoRecycler(getActivity(), R.drawable.deco_15_trans_f5f5f8,
//                DecoRecycler.Edge_Except_Left,false));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);

//        isEyeVisible = SharedUtil.getBool(getContext(), "isVisibleAssert", "user", true);
    }

    private void initData() {
        coinList = new ArrayList<>();
        lanucnList = new ArrayList<>();
        normalList = new ArrayList<>();
    }

    private void initHeader(View header) {

        tvTitle = header.findViewById(R.id.tv_title);
        tvIeoTransfer = header.findViewById(R.id.tv_wallet_ieo_transfer);
        tvManageAddress = header.findViewById(R.id.tv_manage_address);
        if (SharedUtil.getLanguage(getContext()).equals("en")) {
            tvTitle.setTextSize(22);
            tvIeoTransfer.setTextSize(11);
            tvManageAddress.setTextSize(12);
        } else {
            tvTitle.setTextSize(28);
            tvIeoTransfer.setTextSize(14);
            tvManageAddress.setTextSize(14);
        }
        ivIsEyes = header.findViewById(R.id.iv_is_visible);
        tvCoinAmount = header.findViewById(R.id.tv_coin_amount);
        tvCnyAmount = header.findViewById(R.id.tv_cny_amount);
        tvAccountOrder = header.findViewById(R.id.tv_account_order);
        tvChargeCoin = header.findViewById(R.id.tv_charge_coin);
        tvGetCoin = header.findViewById(R.id.tv_get_coin);
        tvTransferAccounts = header.findViewById(R.id.tv_transfer_accounts); // 转账
        cbCoin = header.findViewById(R.id.cb_protocol);
        tvSmallCoin = header.findViewById(R.id.tv_small_coin);
        etSearchCoin = header.findViewById(R.id.et_search_coin);
        ivHelp = header.findViewById(R.id.iv_help);

        tvWSL = header.findViewById(R.id.tv_wsl);
        tvWSR = header.findViewById(R.id.tv_wsr);

        tvAccountOrder.setOnClickListener(this);
        tvChargeCoin.setOnClickListener(this);
        tvGetCoin.setOnClickListener(this);
        tvTransferAccounts.setOnClickListener(this);
        tvManageAddress.setOnClickListener(this);
        tvSmallCoin.setOnClickListener(this);
        ivIsEyes.setOnClickListener(this);
        ivHelp.setOnClickListener(this);
        tvIeoTransfer.setOnClickListener(this);
        tvWSL.setOnClickListener(this);
        tvWSR.setOnClickListener(this);
        tvWSL.setBackground(getResources().getDrawable(R.drawable.order_slected_bg));
        tvWSR.setBackground(null);

        etSearchCoin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCoin = etSearchCoin.getText().toString().trim();
                    isSmalCoin();
                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });


        cbCoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSelectHideSmall = isChecked;
                isSmalCoin();
            }
        });
    }


    /**
     * 是否显示小额币种
     */
    private void isSmalCoin() {
        if (isSelectHideSmall) {
            getCoin("true", searchCoin);
        } else {
            getCoin("false", searchCoin);
        }
    }

    @Override
    protected void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(getChildFragmentManager());
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
    protected void loadData() {
        isEyeVisible = false;
        isSmalCoin();
        isDone = false;
        OkHttpUtil.getJsonToken(Constant.URL.GetInfo, SharedUtil.getToken(getContext()), this);
//        OkHttpUtil.getJsonToken(Constant.URL.coinAssert, SharedUtil.getToken(getContext()), this);
    }


    /**
     * @param isSmall  是否隐藏小额资产 true 隐藏 false 不隐藏
     * @param currency 搜索的币种
     */
    private void getCoin(String isSmall, String currency) {
        OkHttpUtil.getJsonToken(Constant.URL.walletCoin, SharedUtil.getToken(getContext()), this,
                "amountSort", "0",
                "currency", currency,
                "currencySort", "0",
                "isHide", isSmall);
    }

    @Override
    public void onItemClick(View v, int position) {
        walletAdapter.selectposition = position;
        walletAdapter.notifyDataSetChanged();
        Log.i(TAG, "onItemClick: "+coinList.get(position).getCode());
     //   coinList.get(position).getCode()
        if (v.getId() == R.id.iv_item_help) {
            String content = coinList.get(position).getForbiddenReason().get(0).getContent();
            showPopItem(v, content);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onClick(View v) {
        if (!isDone) {
            ToastUtil.initToast(getActivity(), R.string.retryAfterRefresh);
            return;
        }
        switch (v.getId()) {
            case R.id.tv_account_order:
                startActivity(new Intent(getContext(), AccountOrderActivity.class));
                break;
            case R.id.tv_charge_coin:
                Intent intent = new Intent(getContext(), SelectedCoinActivity.class);
                intent.putExtra("type", Constant.Strings.Intent_Choose_Recharge);
                startActivity(intent);
//                startActivity(new Intent(getContext(), ChargeCoinActivity.class));
                break;
            case R.id.tv_get_coin:
                //提币先认证
                int ls = AppUtil.getIdentify(verifiedLevel, verifiedstatus);
                if (idType == 1 && ls < 6) {
                    AuthDialog.newInstance(getString(R.string.withdrawNeedSenior), getString(R.string.str_go_auth),
                            getString(R.string.str_my_think_argin))
                            .setOnItemClickListener(this)
                            .show(getActivity());
                    return;
                } else if (idType != 1 && ls < 5) {
                    AuthDialog.newInstance(getString(R.string.str_auth_hint), getString(R.string.str_go_auth),
                            getString(R.string.str_my_think_argin))
                            .setOnItemClickListener(this)
                            .show(getActivity());
                    return;
                }

                if (isDone) {
                    int gs = getStatus();
                    Intent intent1 = new Intent(getContext(), SelectedCoinActivity.class);
                    intent1.putExtra("type", Constant.Strings.Intent_Choose_Withdraw);
                    intent1.putExtra("state", gs);
                    intent1.putExtra("area", area);
                    intent1.putExtra("account", account);
                    startActivity(intent1);
                }
                break;
            case R.id.tv_manage_address:
                if (isDone) {
                    int gs = getStatus();
                    startActivity(new Intent(getContext(), AdreessListActivity.class)
                            .putExtra("state", gs)
                            .putExtra("area", area)
                            .putExtra("account", account));
                }

                break;
            case R.id.tv_small_coin:
                isSelectHideSmall = !isSelectHideSmall;
                cbCoin.setChecked(isSelectHideSmall);
                isSmalCoin();
                break;
            case R.id.iv_is_visible:
                isEyeVisible = !isEyeVisible;
                if (isEyeVisible) {
                    tvCoinAmount.setText(allBtc + " BTC");
                    tvCnyAmount.setText("≈" + allCny + " CNY");
                    ivIsEyes.setImageResource(R.mipmap.eye_visible);

                } else {
                    ivIsEyes.setImageResource(R.mipmap.eyes_close);
                    tvCoinAmount.setText("*************");
                    tvCnyAmount.setText("≈ **********");
                }

//                SharedUtil.putBool(getContext(), "isVisibleAssert", "user", isEyeVisible);
                break;
            case R.id.iv_help:
                showPop(getString(R.string.str_small_coin_hint));
                break;
            case R.id.tv_transfer_accounts:
                // 转账前先通过初级认证
                if(verifiedLevel<1){
                    AuthDialog.newInstance(getString(R.string.transferNeedSeniorPrimary), getString(R.string.str_go_auth),
                            getString(R.string.str_my_think_argin))
                            .setOnItemClickListener(this)
                            .show(getActivity());
                    return;
                }

                if (isDone) {
                    int gs = getStatus();
                    Intent intent2 = new Intent(getContext(), TransferCoinActivity.class);
                    intent2.putExtra("state", gs);
                    intent2.putExtra("area", area);
                    intent2.putExtra("account", account);
                    startActivity(intent2);
                }
                break;
            case R.id.tv_wallet_ieo_transfer:
                startActivity(new Intent(getContext(), IeoTransferActivity.class));
                break;
            case R.id.tv_wsl:
                ctwlr = 0;
                clickTVC(ctwlr);
                isSmalCoin();
                break;
            case R.id.tv_wsr:
                ctwlr = 1;
                clickTVC(ctwlr);
                isSmalCoin();
                break;
        }
    }


    /**
     * 获取绑定状态
     */
    private int getStatus() {
        int gs = AppUtil.getBindState(data.getGaEnabled(), data.getPhoneEnabled(), data.getEmailEnabled());
        switch (gs) {
            case 1:
                area = data.getPhoneArea();
                account = data.getCellPhone();
                break;
            case 2:
                area = "";
                account = data.getEmail();
                break;
            default:
                area = "";
                account = "";
                break;
        }
        return gs;
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e(TAG, "Wallet: "+url + json);
        if (url.equals(Constant.URL.walletCoin)) {
            CoinEnity coinEnity = GsonUtil.fromJson(json, CoinEnity.class);
            if (coinEnity.getCode() == Constant.Int.SUC) {
                coinList.clear();
                lanucnList.clear();
                normalList.clear();
                coinList = coinEnity.getData();
                for (int i = 0; i < coinList.size(); i++){
                    if(coinList.get(i).getIsSpecialCurrency()==1){
                        lanucnList.add(coinList.get(i));
                    } else {
                        normalList.add(coinList.get(i));
                    }
                }
                if(ctwlr == 1){
                    walletAdapter.setData(lanucnList);
                } else {
                    walletAdapter.setData(normalList);
                }
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        coinEnity.getCode(), coinEnity.getMsg()));
                Util.checkLogin(getContext(), coinEnity.getCode());
            }
        } else if (url.equals(Constant.URL.coinAssert)) {
            CoinAssertEnity coinAssertEnity = GsonUtil.fromJson(json, CoinAssertEnity.class);
//            if (coinAssertEnity.getCode() == Constant.Int.SUC) {
//                coinList = coinAssertEnity.getData();
//                walletAdapter.setData(coinList);
//
//            }
        } else if (url.equals(Constant.URL.GetInfo)) {
            dismissLoading();
            mine = GsonUtil.fromJson(json, UserEntity.class);
            if (Constant.Int.SUC == mine.getCode()) {
                isDone = true;
                data = mine.getData().getInfo();
                if (data == null) {
                    return;
                }

                allBtc = mine.getData().getTotalBtcValuation();
                allCny = mine.getData().getTotalCnyValuation();
                if (isEyeVisible) {
                    tvCoinAmount.setText(allBtc + " BTC");
                    tvCnyAmount.setText("≈" + allCny + " CNY");
                    ivIsEyes.setImageResource(R.mipmap.eye_visible);

                } else {
                    ivIsEyes.setImageResource(R.mipmap.eyes_close);
                    tvCoinAmount.setText("*************");
                    tvCnyAmount.setText("≈ **********");
                }
                isBindPhone = data.getPhoneEnabled() == 1;
                isBindEmail = data.getEmailEnabled() == 1;
                isBindGoogle = data.getGaEnabled() == 1;
                verifiedLevel = data.getVerifiedLevel();
                verifiedstatus = data.getVerifiedStatus();
                idType = data.getIdType();
            } else {
                ToastUtil.initToast(getActivity(), Util.getCodeText(getContext(),
                        mine.getCode(), mine.getMsg()));
                Util.checkLogin(getContext(), mine.getCode());
            }
        }
    }


    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(getActivity(), url, error);
    }


    private void showPop(String string) {
        if (popup == null) {
            popup = new ExampleCardPopup(getContext(), string);
            vertPos = RelativePopupWindow.VerticalPosition.ABOVE;
            horizPos = RelativePopupWindow.HorizontalPosition.RIGHT;
        }

        popup.showOnAnchor(ivHelp, vertPos, horizPos, true);

    }

    private void showPopItem(View view, String content) {

        popup2 = new ExampleCardPopup(getContext(), content);
        vertPos2 = RelativePopupWindow.VerticalPosition.ABOVE;
        horizPos2 = RelativePopupWindow.HorizontalPosition.LEFT;

        popup2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popup2 = null;
            }
        });
        popup2.showOnAnchor(view, vertPos2, horizPos2, true);

    }

    private void clickTVC(int type){
        switch (type){
            case 1:
                tvWSL.setBackground(null);
                tvWSR.setBackground(getResources().getDrawable(R.drawable.order_slected_bg));
                break;
            case 0:
            default:
                tvWSL.setBackground(getResources().getDrawable(R.drawable.order_slected_bg));
                tvWSR.setBackground(null);
                break;
        }
    }


    //如果显示则隐藏，如果隐藏则显示
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_auth) {
            int ls = AppUtil.getIdentify(verifiedLevel, verifiedstatus);
            if (ls == 1) {
                startActivity(new Intent(getActivity(), C1Activity.class));
            } else {
                if (idType == 1) {
                    if (ls >= 2 && ls <= 5) {
                        startActivity(new Intent(getActivity(), IdentityResultActivity.class)
                                .putExtra("level", verifiedLevel)
                                .putExtra("status", verifiedstatus)
                                .putExtra("type", idType));
                    }
                } else {
                    if (ls == 2 || ls == 4) {
                        startActivity(new Intent(getActivity(), C2Activity.class));
                    } else if (ls == 3) {
                        startActivity(new Intent(getActivity(), IdentityResultActivity.class)
                                .putExtra("level", verifiedLevel)
                                .putExtra("status", verifiedstatus)
                                .putExtra("type", idType));
                    }
                }
            }
        }
    }
}
