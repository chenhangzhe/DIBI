package cn.suozhi.DiBi.home.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.widget.TimerTextView;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.InviteRecordAdapter;
import cn.suozhi.DiBi.home.model.IeoDetailsEntity;
import cn.suozhi.DiBi.home.model.IeoInviteEntity;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;

public class CEDActivity extends BaseActivity implements AbsRecyclerAdapter.OnItemClickListener,
        OkHttpUtil.OnDataListener, SwipeRefreshLayout.OnRefreshListener, OnCallbackListener {

    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.sv_aced)
    public ScrollView scrollView;

    @BindView(R.id.tv_activity_rules) // 活动规则文字
    public TextView tvAR;
    @BindView(R.id.tv_invite_record) // 邀请记录文字
    public TextView tvIR;
    @BindView(R.id.tv_ieo_intro) // 项目简介文字
    public TextView tvII;
    @BindView(R.id.tv_danger_intro) // 风险说明文字
    public TextView tvDI;

    @BindView(R.id.tv_activity_rules_details) // 活动规则详细
    public TextView tvARD;
    @BindView(R.id.tv_ieo_intro_details) // 项目简介详细
    public TextView tvIID;
    @BindView(R.id.tv_ieo_danger_details) // 风险说明详情
    public TextView tvIDD;

    @BindView(R.id.toolbar_centerBack)
    public ImageView ivBack;

    @BindView(R.id.v_ar) // 活动规则下划线
    public View vAR;
    @BindView(R.id.v_ir) // 邀请记录下划线
    public View vIR;
    @BindView(R.id.v_ii) // 项目简介下划线
    public View vII;
    @BindView(R.id.v_id) // 风险说明下划线
    public View vID;

    @BindView(R.id.ll_invite_records) // 邀请列表
    public LinearLayout ll_inv;

    @BindView(R.id.tv_countdown) // 倒计时控件
//    public TimerTextView tvCountdown;
    public TextView tvCountdown;

    // 页面基本控件
    @BindView(R.id.iv_aced_coin_image) // 申购币种的Logo
    public ImageView coinLogo;
    @BindView(R.id.tv_aced_coin_name) // 申购币种的名字
    public TextView baseName;
    @BindView(R.id.progress_aced) // 进度条
    public ProgressBar pb;
    @BindView(R.id.tv_aced_invite) // 邀请好友
    public TextView invite;
    @BindView(R.id.tv_aced_percent_left) // 左侧比例
    public TextView percentLeft;
    @BindView(R.id.tv_aced_percent_right) // 右侧进度条百分比
    public TextView percentRight;
    @BindView(R.id.tv_lc_1) // 单价
    public TextView lc1;
    @BindView(R.id.tv_lc_2) // 总申购量
    public TextView lc2;
    @BindView(R.id.tv_lc_3) // 参与人数
    public TextView lc3;
    @BindView(R.id.tv_lc_4) // 单价 CSO / USDT
    public TextView lc4;
    @BindView(R.id.tv_lc_5) // 单价
    public TextView lc5;
    @BindView(R.id.tv_apply_number_coin_base) // 申购币种提示币种
    public TextView tvBaseCoin;
    @BindView(R.id.tv_apply_number_coin) // 申购数量提示币种
    public TextView tvCoin;
    @BindView(R.id.tv_aced_available) // 可用余额
    public TextView tvAvail;
    @BindView(R.id.tv_aced_min_apply) // 最小申购量
    public TextView tvMin;
    @BindView(R.id.tv_aced_personal_can) // 个人总申购量
    public TextView tvP;
    @BindView(R.id.tv_aced_surplus_can) // 剩余可申购量
    public TextView tvS;
    @BindView(R.id.tv_btn_aced_confirm) // 按钮 根据不同状态显示不同颜色、文字
    public TextView tvConfirm;

    @BindView(R.id.et_aced_etAmout_base) // 输入框1
    public EditText et1;
    @BindView(R.id.et_aced_etAmout) // 输入框2
    public EditText et2;

    //是否正在提交
    private boolean isCommiting = false;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPageGet = START_INDEX;
    private int mPageCharge = START_INDEX;

    private List<IeoInviteEntity.DataBean.InviteUserListBean.RecordsBean> inviteList;

    private InviteRecordAdapter inviteRecordAdapter;

    private int status = 0; // 0:项目简介 1:活动柜子 2:风险说明 3:邀请记录
    private int id;
    private int height;

    private String exchangeAmount; // 提交时的金额
    private String exchangeCount; // 提交时的数量
    private String onceToken; // 一次性token

    private int pm = 2, pa = 4;
    private boolean fromUser = false;//是否手动输入
    private double price; // 后台读取换算比例

    private int page, nextPage;// 当前页 // 下一页
    private final int pageSize = 15;//每页数量

    private int clickStatus = 0; // 定义并初始化按钮可点击状态

    private int noReq = 0; // 定义 邀请好友时判断，0的时候可以请求，1的时候不允许请求  否则会重复请求

    private long sDate,eDate;
    private int respStatus = 0;

    private Timer timer;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
            },0,1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        }
    };

    @Override
    protected int getViewResId() {
        return R.layout.activity_consensus_exchange_details;
    }

    @Override
    protected void init() {
        height = Util.getPhoneHeight(mContext) / 2;
        lang = SharedUtil.getLanguage4Url(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inviteRecordAdapter = new InviteRecordAdapter(this, height ,this);
        recyclerView.setAdapter(inviteRecordAdapter.setOnItemClickListener(this));

        inviteList = new ArrayList<>();

//        refreshLayout.setOnRefreshListener(this);
//        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData(){
        super.loadData();
        if (!fromUser) {
            fromUser = true;
        }

        et1.setText(null);
        et2.setText(null);

        id = getIntent().getIntExtra("ieoID",0);
        getCeDetails(id);

        showIeoIntro(); // 默认显示项目简介
    }

    // 获取共识详情
    private void getCeDetails(Integer ieoID){
        OkHttpUtil.getJsonToken(Constant.URL.ieoDetails + ieoID , SharedUtil.getToken(this) ,this);
    }

    // 进行共识兑换
    private void  goExchange(String ec , String ea , String onceToken , String projectID){
        OkHttpUtil.postJsonToken(Constant.URL.ieoExchange , SharedUtil.getToken(this) ,this
                , "exchangeAmount" , ea
                , "exchangeCount" , ec
                , "onceToken" , onceToken
                , "projectId" , projectID);
    }

    // 获取一次性token
    private void getOnceToken(){
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.oncetoken);
    }

    /**
     * 获取邀请记录
     */
    private void getInviteRecord() {
        OkHttpUtil.getJsonHeader2(Constant.URL.GetInviteList, SharedUtil.getToken(this), lang, this,
                "pageNum", page + "", "pageSize", pageSize + "");
    }

    // 邀请记录
    private void createGetAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        inviteRecordAdapter = new InviteRecordAdapter(mContext, height, this);
        recyclerView.setAdapter(inviteRecordAdapter);
    }

    // 显示项目简介
    private void showIeoIntro() {
        vII.setVisibility(View.VISIBLE); // 项目简介
        vAR.setVisibility(View.GONE); // 活动规则 tab
        vID.setVisibility(View.GONE); // 风险
        vIR.setVisibility(View.GONE); // 邀请记录

        tvIID.setVisibility(View.VISIBLE); // 项目简介内容
        tvARD.setVisibility(View.GONE); // 活动规则内容
        tvIDD.setVisibility(View.GONE); // 风险内容

        tvII.setTextColor(getResources().getColor(R.color.color_1888FE));
        tvAR.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvDI.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvIR.setTextColor(getResources().getColor(R.color.text_color_dark));

        ll_inv.setVisibility(View.GONE); // 邀请内容列表
        recyclerView.setVisibility(View.GONE);
    }

    // 显示活动规则
    private void showActivityRules() {
        vII.setVisibility(View.GONE); // 项目简介
        vAR.setVisibility(View.VISIBLE); // 活动规则 tab
        vID.setVisibility(View.GONE); // 风险
        vIR.setVisibility(View.GONE); // 邀请记录

        tvIID.setVisibility(View.GONE); // 项目简介内容
        tvARD.setVisibility(View.VISIBLE); // 活动规则内容
        tvIDD.setVisibility(View.GONE); // 风险内容

        tvII.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvAR.setTextColor(getResources().getColor(R.color.color_1888FE));
        tvDI.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvIR.setTextColor(getResources().getColor(R.color.text_color_dark));

        ll_inv.setVisibility(View.GONE); // 邀请内容列表
        recyclerView.setVisibility(View.GONE);
    }

    // 显示风险说明
    private void showDangerIntro() {
        vII.setVisibility(View.GONE); // 项目简介
        vAR.setVisibility(View.GONE); // 活动规则 tab
        vID.setVisibility(View.VISIBLE); // 风险
        vIR.setVisibility(View.GONE); // 邀请记录

        tvIID.setVisibility(View.GONE); // 项目简介内容
        tvARD.setVisibility(View.GONE); // 活动规则内容
        tvIDD.setVisibility(View.VISIBLE); // 风险内容

        tvII.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvAR.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvDI.setTextColor(getResources().getColor(R.color.color_1888FE));
        tvIR.setTextColor(getResources().getColor(R.color.text_color_dark));

        ll_inv.setVisibility(View.GONE); // 邀请内容列表
        recyclerView.setVisibility(View.GONE);
    }

    // 显示邀请记录
    private void showInviteRecord() {
        vII.setVisibility(View.GONE); // 项目简介
        vAR.setVisibility(View.GONE); // 活动规则 tab
        vID.setVisibility(View.GONE); // 风险
        vIR.setVisibility(View.VISIBLE); // 邀请记录

        tvIID.setVisibility(View.GONE); // 项目简介内容
        tvARD.setVisibility(View.GONE); // 活动规则内容
        tvIDD.setVisibility(View.GONE); // 风险内容

        tvII.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvAR.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvDI.setTextColor(getResources().getColor(R.color.text_color_dark));
        tvIR.setTextColor(getResources().getColor(R.color.color_1888FE));

        ll_inv.setVisibility(View.VISIBLE); // 邀请内容列表
        recyclerView.setVisibility(View.VISIBLE);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                setUi();
            }
        }
    };

    @OnClick({R.id.toolbar_centerBack,R.id.tv_ieo_intro,R.id.tv_activity_rules,R.id.tv_danger_intro,R.id.tv_invite_record,R.id.tv_aced_invite,R.id.tv_countdown,R.id.tv_btn_aced_confirm})
    public void ced(View v) {
        switch (v.getId()) {
            case R.id.toolbar_centerBack:
                finish();
                break;
            case R.id.tv_ieo_intro:
                status = 0;
                noReq = 0;
                showIeoIntro();
                break;
            case R.id.tv_activity_rules:
                status = 1;
                noReq = 0;
                showActivityRules();
                break;
            case R.id.tv_danger_intro:
                status = 2;
                noReq = 0;
                showDangerIntro();
                break;
            case R.id.tv_invite_record:
                status = 3;
                showInviteRecord();
                // 避免重复请求
                if(noReq == 0){ // noReq为0时请求
                    if (inviteRecordAdapter == null) {
                        createGetAdapter();
                    }
                    getInviteRecord();
                } else {
                    noReq = 1;
                }
                break;
            case R.id.tv_aced_invite:
                Intent ii = new Intent(CEDActivity.this,InviteActivity.class);
                startActivity(ii);
                break;
            case R.id.tv_btn_aced_confirm:
                // 点击立即结算 判断当前状态是否为1（进行中）
                if(checkInfo()){
                    showDialog();
                }
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        //
    }

    private boolean checkInfo() {
        if(clickStatus == 1){
            exchangeCount = et1.getText().toString().trim();
            exchangeAmount = et2.getText().toString().trim();

            if (TextUtils.isEmpty(exchangeCount)) {
                ToastUtil.initToast(this, R.string.number_of_applications_hint); // 提示请输入申购数量
                return false;
            }
            if (TextUtils.isEmpty(exchangeAmount)) {
                ToastUtil.initToast(this, R.string.amout_of_applications_hint); // 提示请输入申购金额
                return false;
            }
        } else {
            ToastUtil.initToast(this,getString(R.string.cannotApply));
            return  false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.ieoDetails + id)) {
            IeoDetailsEntity ieoDetails = GsonUtil.fromJson(json , IeoDetailsEntity.class);
            if(ieoDetails.getCode() == Constant.Int.SUC){
                tvIID.setText(ieoDetails.getData().getIeoIntro()); // 设置项目说明
                tvARD.setText(ieoDetails.getData().getGiveIntro()); // 设置活动规则
                tvIDD.setText(ieoDetails.getData().getRiskIntro()); // 设置风险说明
                price = ieoDetails.getData().getIeoPrice();
                Glide.with(this).load(ieoDetails.getData().getLogo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(coinLogo); // Logo
                baseName.setText(ieoDetails.getData().getBaseCurrencyCode()); // 申购币种
                // 进度条
                double i=ieoDetails.getData().getSurplusAmount(),j=ieoDetails.getData().getTotalAmount();
                double isU = j-i; // 已申购
                double percent = (isU / j * 100);
                if( percent < 1){
                    pb.setProgress(1);
                } else {
                    pb.setProgress((int) percent);
                }
                BigDecimal a = new BigDecimal(j);
                BigDecimal b = new BigDecimal(isU);
                BigDecimal c = new BigDecimal(percent);
                // 进度条左下方金额
                percentLeft.setText(b.setScale(2, BigDecimal.ROUND_HALF_UP) + " "
                        + ieoDetails.getData().getQuoteCurrencyCode() + " / "
                        + a.setScale(2, BigDecimal.ROUND_HALF_UP) + " "
                        + ieoDetails.getData().getQuoteCurrencyCode());
                if ( percent > 0 && percent < 0.01){
                    percentRight.setText("0.01%"); // 进度条右下方百分比
                } else if ( percent >= 0.01) {
                    percentRight.setText(c.setScale(2,BigDecimal.ROUND_HALF_UP) + "%"); // 进度条右下方百分比
                } else {
                    percentRight.setText("0.00%"); // 进度条右下方百分比
                }
                percentRight.setTextColor(getResources().getColor(R.color.yellowD9));
                if(ieoDetails.getData().getStatus() == 0){
                    tvConfirm.setText(getString(R.string.cel_2));
                    clickStatus = 0;
                    et1.setEnabled(false);
                    et2.setEnabled(false);
                } else if (ieoDetails.getData().getStatus() == 1){
                    tvConfirm.setText(getString(R.string.cel_12));
                    clickStatus = 1;
                    et1.setEnabled(true);
                    et2.setEnabled(true);
                } else if (ieoDetails.getData().getStatus() == 2){
                    tvConfirm.setText(getString(R.string.cel_7));
                    clickStatus = 0;
                    et1.setEnabled(false);
                    et2.setEnabled(false);
                } else if (ieoDetails.getData().getStatus() == 3){
                    tvConfirm.setText(getString(R.string.cel_9));
                    clickStatus = 0;
                    et1.setEnabled(false);
                    et2.setEnabled(false);
                }

                tvBaseCoin.setText(ieoDetails.getData().getBaseCurrencyCode()); // 申购的币
                tvCoin.setText(ieoDetails.getData().getQuoteCurrencyCode()); // 使用的币

                BigDecimal i1 = new BigDecimal(ieoDetails.getData().getAvailableAmount());
                tvAvail.setText(getString(R.string.available) + "：" + i1.setScale(2, BigDecimal.ROUND_HALF_UP) + " " + ieoDetails.getData().getQuoteCurrencyCode());// 当前用户可用币种
                BigDecimal i2 = new BigDecimal(ieoDetails.getData().getMinAmount());
                tvMin.setText(getString(R.string.min_apply) + "：" + i2.setScale(2, BigDecimal.ROUND_HALF_UP) + " " + ieoDetails.getData().getQuoteCurrencyCode()); // 最小申购量
                BigDecimal i3 = new BigDecimal(ieoDetails.getData().getExchangeAmount());
                tvP.setText(getString(R.string.total_amount_of_personal) + "：" + i3.setScale(2, BigDecimal.ROUND_HALF_UP) + " " + ieoDetails.getData().getQuoteCurrencyCode()); // 个人申购总量
                BigDecimal i4 = new BigDecimal(ieoDetails.getData().getSurplusAmount());
                tvS.setText(getString(R.string.quantity_available_for_purchase) + "：" + i4.setScale(2, BigDecimal.ROUND_HALF_UP) + " " + ieoDetails.getData().getQuoteCurrencyCode()); // 剩余可申购量

//                BigDecimal ieoPrice = new BigDecimal(ieoDetails.getData().getIeoPrice());
//                lc1.setText("" + ieoPrice.setScale(2, BigDecimal.ROUND_HALF_UP)); // 单价，保留小数点后2位
                lc1.setText("" + price);
                lc1.setTextColor(getResources().getColor(R.color.color_1888FE));
                DecimalFormat ieoNum = new DecimalFormat("#,##0"); // 每三位隔逗号，保留小数点后2位
                lc2.setText("" + ieoNum.format(ieoDetails.getData().getIeoNum())); // 申购总量
                lc2.setTextColor(getResources().getColor(R.color.color_1888FE));
                lc3.setText("" + ieoDetails.getData().getPeopleNum()); // 总参与人数
                lc3.setTextColor(getResources().getColor(R.color.color_1888FE));
                lc4.setText(getString(R.string.unit_price) + " " + ieoDetails.getData().getBaseCurrencyCode() + "/" + ieoDetails.getData().getQuoteCurrencyCode()); // 单价下方文字
                lc5.setText(getString(R.string.purchase_amount) + " " + ieoDetails.getData().getBaseCurrencyCode()); // 申购总量下方文字

                sDate = getStringToDate(ieoDetails.getData().getStartDate());
                eDate = getStringToDate(ieoDetails.getData().getEndDate());
                respStatus = ieoDetails.getData().getStatus();

                mHandler.post(runnable);
            }
        }
        else if (url.equals(Constant.URL.onceToken)){
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                BigDecimal sr1 = new BigDecimal(exchangeCount);
                BigDecimal sr2 = new BigDecimal(exchangeAmount);
                String eC = sr1.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                String eA = sr2.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                goExchange(eC , eA  , onceToken , String.valueOf(id));
            } else {
                isCommiting = false;
            }
        }
        else if (url.equals(Constant.URL.ieoExchange)){
            ObjectEntity objectEntity = GsonUtil.fromJson(json, ObjectEntity.class);
            if (objectEntity.getCode() == Constant.Int.SUC) {
                ToastUtil.initToast(this, getString(R.string.apply_success), Toast.LENGTH_SHORT);
                et1.setText(null);
                et2.setText(null);
                loadData();
            } else {
                ToastUtil.initToast(this, Util.getCodeText(this, objectEntity.getCode(), objectEntity.getMsg()));
                Util.checkLogin(this, objectEntity.getCode());
            }
        }
        else if (url.equals(Constant.URL.GetInviteList)){
//            Log.e("邀请记录","邀请列表" + json);
            IeoInviteEntity i1 = GsonUtil.fromJson(json,IeoInviteEntity.class);
            if(i1.getCode() == Constant.Int.SUC){
                IeoInviteEntity iie = GsonUtil.fromJson(json, IeoInviteEntity.class);
                dismissLoading();
                if (inviteList == null) {
                    inviteList = new ArrayList<>();
                } else {
                    inviteList.clear();
                }
                if (Constant.Int.SUC == iie.getCode()) {
                    inviteList.addAll(iie.getData().getInviteUserList().getRecords());
                    inviteList.add(new IeoInviteEntity.DataBean.InviteUserListBean.RecordsBean());//这里是把正在加载显示出来
                }
                inviteRecordAdapter.setData(inviteList);
                noReq = 1;
            } else {
                ToastUtil.initToast(CEDActivity.this,"Error");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUi(){
        long sysTime = System.currentTimeMillis();
        String a = getFormatTime(sDate - sysTime);
        String b = getFormatTime(eDate - sysTime);
        if (respStatus == 0) {
            tvCountdown.setText(getString(R.string.cel_13) + a); // 显示距离开始时间
        } else if(respStatus == 1) {
            tvCountdown.setText(getString(R.string.cel_14) + b); // 显示距离结束时间
        } else if(respStatus == 2) {
            tvCountdown.setText(getString(R.string.cel_7)); // 结算中
        } else {
            tvCountdown.setText(getString(R.string.cel_6)); // 活动已结束
        }
    }

    // 日期转时间戳
    public static long getStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(CEDActivity.this, url, error);
        isCommiting = false;
    }

    @Override
    public void onRefresh() {
        dismissLoading();
        loadData();
    }

    @Override
    protected void dismissLoading() {
        super.dismissLoading();
    }

    @Override
    public void onCallback() {
        if(status == 3){
            // 如果可以请求
            if (noReq == 0){
                getInviteRecord();
                noReq = 1;
            }
        }
    }

    // 输入框跟随变化 price = 获取到的返回ieo的单价
    @OnTextChanged(value = R.id.et_aced_etAmout_base, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etMoneyChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + pm + 1;
                if (dotIndex != dotLastIndex) {
                    et1.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    et1.setSelection(et1.length());
                } else {
                    if (text.length() > maxIndex) {
                        et1.setText(text.toString().substring(0, maxIndex));
                        et1.setSelection(et1.length());
                    }
                }
            }
            // 若有换算比例
            if (fromUser && price > 0) {
                fromUser = false;
                double m = Util.parseDouble(et1.getText().toString());
                et2.setText(AppUtil.floorRemoveZero(m * price, pa));
                fromUser = true;
            }
        } else {
            if (et2.length() > 0) {
                et2.setText(null);
            }
        }
    }

    @OnTextChanged(value = R.id.et_aced_etAmout, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void etAmountChanged(CharSequence text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + pa + 1;
                if (dotIndex != dotLastIndex) {
                    et2.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    et2.setSelection(et2.length());
                } else {
                    if (text.length() > maxIndex) {
                        et2.setText(text.toString().substring(0, maxIndex));
                        et2.setSelection(et2.length());
                    }
                }
            }
            if (fromUser && price > 0) {
                fromUser = false;
                double a = Util.parseDouble(et2.getText().toString());
                et1.setText(AppUtil.floorRemoveZero(a / price, pm));
                fromUser = true;
            }
        } else {
            if (et1.length() > 0) {
                et1.setText(null);
            }
        }
    }

    // 点击确定弹窗
    private void showDialog() {
        AlertDialog.Builder alertDB = new AlertDialog.Builder(CEDActivity.this);
        alertDB.setTitle(getString(R.string.showTips));
        alertDB.setMessage(getString(R.string.goApply));
        alertDB.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        alertDB.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getOnceToken();
            }
        });
        alertDB.show();
    }

    private String getFormatTime(long distanceMillis) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = distanceMillis / dd;
        Long hour = (distanceMillis - day * dd) / hh;
        Long minute = (distanceMillis - day * dd - hour * hh) / mi;
        Long second = (distanceMillis - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }

        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        // 将线程销毁掉
        mHandler.removeCallbacks(runnable);
        timer.cancel();
        super.onDestroy();
    }

}

