package cn.suozhi.DiBi.c2c.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.adapter.ComplaintPhotoAdapter;
import cn.suozhi.DiBi.c2c.model.CompalintDetailEnity;
import cn.suozhi.DiBi.c2c.model.OrderDetailEnity;
import cn.suozhi.DiBi.c2c.model.OrderEnity;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 申诉订单
 */
public class ComplaintOrderActivity extends BaseActivity implements OkHttpUtil.OnDataListener {
    @BindView(R.id.rv_photo)
    RecyclerView rvPhonto;

    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;


    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_sumary)
    TextView tvContent;

    @BindView(R.id.tv_result)
    TextView tvResult;


    @BindView(R.id.tv_order_number)
    TextView tvOrderNo;


    @BindView(R.id.tv_money)
    TextView tvMoney;   //交易金额

    @BindView(R.id.tv_pay_type)
    TextView tvPayType;   //支付方式

    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;   //订单时间


    private List<String> dataPic;
    private ComplaintPhotoAdapter adapter;
    private LoadingDialog loadingDialog;
    private String oderNo;

    private OrderDetailEnity.DataBean oderEnity;
    private OrderEnity.DataBean.RecordsBean oderEnityTwo;
    private boolean isFromList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_complaint_order;
    }

    @Override
    protected void init() {
        super.init();
        oderNo = getIntent().getStringExtra("oderNo");

        oderEnity = (OrderDetailEnity.DataBean) getIntent().getSerializableExtra("oderEnity");
        oderEnityTwo = (OrderEnity.DataBean.RecordsBean) getIntent().getSerializableExtra("oderEnityTwo");
        ToolbarUtil.initToolbar(toolbar, getString(R.string.str_compalint_order), v -> onBackPressed());
        rvPhonto.setLayoutManager(new GridLayoutManager(mContext, 3));
        adapter = new ComplaintPhotoAdapter(mContext);
        rvPhonto.setAdapter(adapter);

        if (rvPhonto.getItemDecorationCount() == 0) {
            rvPhonto.addItemDecoration(new DecoRecycler(mContext, R.drawable.deco_8_trans,
                    DecoRecycler.Edge_NONE));
        }
        dataPic = new ArrayList<>();

        //从待确认界面过来的
        if (oderEnity != null) {
            showOrderInfo();
        }
        //从订单列表过来的
        if (oderEnityTwo != null) {
            showOrderInfoTwo();
        }
    }

    private void showOrderInfoTwo() {
        tvOrderNo.setText(oderEnityTwo.getOrderNo());
        tvMoney.setText(Util.formatTinyDecimal(oderEnityTwo.getTotalPrice(), false) + "  " + oderEnityTwo.getLegalCurrencyCode());

        switch (oderEnityTwo.getPayMode()) {
            case 1:
                tvPayType.setText(getString(R.string.str_alipay));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.alipay, 0, 0, 0);
                break;
            case 2:
                tvPayType.setText(getString(R.string.str_wechat));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.wechat, 0, 0, 0);
                break;
            case 3:
                tvPayType.setText(getString(R.string.str_bank));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.bank, 0, 0, 0);
                break;
        }

        tvOrderTime.setText(oderEnityTwo.getCreateTime());
    }

    private void showOrderInfo() {
        tvOrderNo.setText(oderEnity.getOrderNo());
        tvMoney.setText(Util.formatTinyDecimal(oderEnity.getTotalPrice(), false) + "  " + oderEnity.getLegalCurrencyCode());

        switch (oderEnity.getPayMode()) {
            case 1:
                tvPayType.setText(getString(R.string.str_alipay));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.alipay, 0, 0, 0);
                break;
            case 2:
                tvPayType.setText(getString(R.string.str_wechat));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.wechat, 0, 0, 0);
                break;
            case 3:
                tvPayType.setText(getString(R.string.str_bank));
                tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.bank, 0, 0, 0);
                break;
        }

        tvOrderTime.setText(oderEnity.getTime());
    }

    @Override
    protected void loadData() {
        super.loadData();
        showLoading();
        OkHttpUtil.getJsonToken(Constant.URL.complaintDetail, SharedUtil.getToken(mContext), this,
                "orderNo", oderNo);//申诉订单id

    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        if (url.equals(Constant.URL.complaintDetail)) {
            CompalintDetailEnity complaint = GsonUtil.fromJson(json, CompalintDetailEnity.class);
            if (Constant.Int.SUC == complaint.getCode()) {
                //跳转到申诉详情界面
                CompalintDetailEnity.DataBean data = complaint.getData();
                setUI(data);
            } else {
                ToastUtil.initToast(ComplaintOrderActivity.this, Util.getCodeText(ComplaintOrderActivity.this,
                        complaint.getCode(), complaint.getMsg()));

            }
        }
    }

    private void setUI(CompalintDetailEnity.DataBean data) {
        if (data == null) return;
        tvTitle.setText(data.getTitle());
        tvContent.setText(data.getDescr());
        //状态[1处理中|2买家胜诉|3卖家胜诉]
        switch (data.getStatus()) {
            case 1:
                tvStatus.setText(getString(R.string.str_compalinting));
                tvStatus.setTextColor(ResUtils.getColor(R.color.color_e89940));

                tvResult.setText(getString(R.string.str_compalinting_info));
                tvResult.setTextColor(ResUtils.getColor(R.color.color_e89940));
                break;
            case 2:
                tvStatus.setText(getString(R.string.str_buy_win));
                tvStatus.setTextColor(ResUtils.getColor(R.color.green3F));

                tvResult.setText(getString(R.string.str_buy_win_info));
                tvResult.setTextColor(ResUtils.getColor(R.color.green3F));
                break;
            case 3:
                tvStatus.setText(getString(R.string.str_sell_win));
                tvStatus.setTextColor(ResUtils.getColor(R.color.green3F));

                tvResult.setText(getString(R.string.str_sell_win_info));
                tvResult.setTextColor(ResUtils.getColor(R.color.text_color_dark));
                break;
        }

        if (TextUtils.isEmpty(data.getMaterials())) {
            rvPhonto.setVisibility(View.GONE);
        } else {
            rvPhonto.setVisibility(View.VISIBLE);
            String[] photos = data.getMaterials().split(",");
            for (int i = 0; i < photos.length; i++) {
                dataPic.add(photos[i]);
            }
            adapter.setData(dataPic);

        }
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
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
    }
}
