package cn.suozhi.DiBi.home.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.WorkOrderDetailEntity;

/**
 * 功能描述：工单详情
 */
public class WorkOrderDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener {


    @BindView(R.id.iv_mineBack)
    ImageView ivMineBack;
    @BindView(R.id.iv_edit)
    ImageView ivEdit;
    @BindView(R.id.tv_msg_title)
    TextView tvMsgTitle;

    @BindView(R.id.srl_wod)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.wv_wod)
    public WebView wv;

    @BindView(R.id.ll_q)
    LinearLayout llQ;
    @BindView(R.id.tv_wodQTime)
    TextView tvQTime;
    @BindView(R.id.tv_q_content)
    TextView tvQContent;
    @BindView(R.id.ll_a)
    LinearLayout llA;
    @BindView(R.id.tv_wodATime)
    TextView tvATime;
//    @BindView(R.id.tv_a_content)
//    TextView tvAContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.ll_wodQImg)
    public LinearLayout llQImg;
    @BindViews({R.id.img_q_one, R.id.img_q_two, R.id.img_q_three})
    public List<ImageView> ivsQ;

    private String workOrderId;
    private List<String> qList;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_work_order_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        showLoading();
        workOrderId = getIntent().getStringExtra("workOrderId");

        wv.requestFocusFromTouch();
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJsonToken(Constant.URL.Work_Order_Detail + workOrderId, SharedUtil.getToken(this), this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e(TAG, "WorkDetail：" + json);
        dismissLoading();
        WorkOrderDetailEntity detail = gson.fromJson(json, WorkOrderDetailEntity.class);
        if (Constant.Int.SUC == detail.getCode()) {
            WorkOrderDetailEntity.DataBean d = detail.getData();
            tvTitle.setText(d.getTitle());
            tvQTime.setText("Q: " + d.getCreateTime());
            tvQContent.setText(d.getContent());
            updateQImg(d.getSysWorkSheetPicture());
            updateA(d.getSysWorkSheetsReplys());
        } else {
            ToastUtil.initToast(this, Util.getCodeText(this, detail.getCode(), detail.getMsg()));
            Util.checkLogin(this, detail.getCode());
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
    }

    private void updateQImg(List<WorkOrderDetailEntity.DataBean.SysWorkSheetPictureBean> list) {
        if (qList == null) {
            qList = new ArrayList<>();
        }else {
            qList.clear();
        }
        if (list != null && list.size() > 0) {
            llQImg.setVisibility(View.VISIBLE);
            for (int i = 0; i < ivsQ.size(); i++) {
                if (i >= list.size()) {
                    ivsQ.get(i).setVisibility(View.INVISIBLE);
                } else {
                    ivsQ.get(i).setVisibility(View.VISIBLE);
                    String img = list.get(i).getPicPath();
                    qList.add(img);
                    GlideUtil.glide(this, ivsQ.get(i), img);
                }
            }
        } else {
            llQImg.setVisibility(View.GONE);
        }
    }

    private void updateA(List<WorkOrderDetailEntity.DataBean.SysWorkSheetsReplysBean> list) {
        if (list != null && list.size() > 0) {
            llA.setVisibility(View.VISIBLE);
            tvATime.setText("A: " + list.get(0).getCreateTime());
            Util.loadHtmlData(wv, list.get(0).getReplyContent());
        } else {
            llA.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_mineBack, R.id.iv_edit, R.id.img_q_one, R.id.img_q_two, R.id.img_q_three})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_mineBack:
                finish();
                break;
            case R.id.iv_edit:
                startActivity(new Intent(this, SubmitOrderWorkActivity.class));
                break;
            case R.id.img_q_one:
                if (qList == null || qList.size() < 1) {
                    break;
                }
                lookPreviewPic(qList, 0);
                break;
            case R.id.img_q_two:
                if (qList == null || qList.size() < 2) {
                    break;
                }
                lookPreviewPic(qList, 1);
                break;
            case R.id.img_q_three:
                if (qList == null || qList.size() < 3) {
                    break;
                }
                lookPreviewPic(qList, 2);
                break;
        }
    }

    private void lookPreviewPic(List<String> list, int position) {
        Intent intent=new Intent(this, PhotoActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("list", (Serializable) list);//序列化,要注意转化(Serializable)
        bundle.putString("url", list.get(position));
        bundle.putInt("position", position);
        intent.putExtras(bundle);//发送数据
        startActivity(intent);//启动intent
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onDestroy() {
        wv.stopLoading();
        wv.removeAllViews();
        wv.clearCache(true);
        wv.clearHistory();
        wv.destroy();
        super.onDestroy();
    }
}
