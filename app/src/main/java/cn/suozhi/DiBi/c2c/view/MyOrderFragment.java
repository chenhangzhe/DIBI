package cn.suozhi.DiBi.c2c.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.AppContext;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.adapter.MyOrderAdapter;
import cn.suozhi.DiBi.c2c.model.OrderEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;

public class MyOrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener, OnCallbackListener {
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_order)
    public RecyclerView rvOrder;
    private MyOrderAdapter orderAdapter;
    private String category = "U";

    @BindView(R.id.tv_current_order)
    TextView tvCurrent;

    @BindView(R.id.tv_history_order)
    TextView tvHistory;

    @BindView(R.id.tv_report_order)
    TextView tvReport;
    private boolean isBussiness;

    private static final int START_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private int mPage = START_INDEX;
    private LoadingDialog loadingDialog;

    private List<OrderEnity.DataBean.RecordsBean> dataList;

    public static MyOrderFragment getInstance(boolean isBussiness) {
        MyOrderFragment fragment = new MyOrderFragment();
        Bundle args = new Bundle();
        args.putBoolean("isBussiness", isBussiness);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getViewResId() {
        return R.layout.fragment_my_order;
    }

    @Override
    protected void init(View v) {
        super.init(v);
        isBussiness = getArguments().getBoolean("isBussiness");
        int height = Util.getPhoneHeight(getActivity()) / 2;
        rvOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        orderAdapter = new MyOrderAdapter(getActivity(), height, this);
        rvOrder.setAdapter(orderAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));

        tvCurrent.setSelected(true);
//        initNotify();

    }

    /**
     * 注册/注销自定义通知接收观察者
     * //     * @param observer 观察者，参数为收到的自定义通知
     * //     * @param register true为注册，false为注销
     */
    private void initNotify() {
        registerNotify(true);

    }

    private void registerNotify(boolean isRegister) {
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        Log.d("inmwangyi", "自定义消息过啊里的    " + messages.get(0).getSessionId() +
                                "     " + messages.get(0).getContent() +   "   消息类型  " + messages.get(0).getSessionType().getValue() +   "  附件吗 " + ((messages.get(0).getAttachment()) == null?"附件为空" :((messages.get(0).getAttachment())).toJson(false)))
                        ;
                        mPage = START_INDEX;
                        OkHttpUtil.getJsonToken(Constant.URL.myOrder, SharedUtil.getToken(AppContext.appContext), MyOrderFragment.this,
                                "category", category,//订单类别[U-当前订单|S-历史订单|A申诉订单]
                                "orderNo", "",
                                "pageNum", mPage + "",//当前页
                                "pageSize", DEFAULT_PAGE_SIZE + "",//每页的条数
                                "type", ""//买卖类型[1-买|2-卖]
                        );

                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, isRegister);
    }

    @Override
    protected void loadData() {
        super.loadData();
        mPage = START_INDEX;
        showLoading();
        getMyOrder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        registerNotify(false);
    }

    @OnClick({R.id.tv_current_order, R.id.tv_history_order, R.id.tv_report_order})
    public void advanceClick(View v) {
        switch (v.getId()) {
            case R.id.tv_current_order:
//                mPage = START_INDEX;
                category = "U";
                showSelectedTag(true, false, false);
                loadData();
                break;
            case R.id.tv_history_order:
//                mPage = START_INDEX;
                category = "S";
                showSelectedTag(false, true, false);
                loadData();
                break;
            case R.id.tv_report_order:
//                mPage = START_INDEX;
                category = "A";
                showSelectedTag(false, false, true);
                loadData();
                break;
        }
    }

    private void showSelectedTag(boolean isCurrent, boolean isHistory, boolean isReport) {
        tvCurrent.setSelected(isCurrent);
        tvHistory.setSelected(isHistory);
        tvReport.setSelected(isReport);
    }


    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
//        mPage = START_INDEX;
        loadData();
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
    public void onItemClick(View v, int position) {
        String orderNo = orderAdapter.getData().get(position).getOrderNo();
        int status = orderAdapter.getData().get(position).getStatus();
        switch (status) {
            case 1:
                //待支付
                startActivity(new Intent(getContext(), WaitPayActivity.class).putExtra("oderNo", orderNo).putExtra("isBussiness", isBussiness));
                break;
            case 2:
                //待确认
                startActivity(new Intent(getContext(), WaitComfirmActivity.class).putExtra("oderNo", orderNo).putExtra("isBussiness", isBussiness));
                break;
            case 3:
                //已取消
                startActivity(new Intent(getContext(), OrderCompleteActivity.class).putExtra("oderNo", orderNo).putExtra("orderType", status));
                break;
            case 4:
                //申诉中
                startActivity(new Intent(getContext(), ComplaintOrderActivity.class).putExtra("oderNo", orderNo).putExtra("oderEnityTwo", orderAdapter.getData().get(position)));
                break;
            case 5:
                //已完成
                startActivity(new Intent(getContext(), OrderCompleteActivity.class).putExtra("oderNo", orderNo).putExtra("orderType", status));
                break;
        }
    }

    /**
     * 获取我的订单
     */
    private void getMyOrder() {
//        OkHttpUtil.getJsonToken(Constant.URL.myOrder, SharedUtil.getToken(getContext()), this,
//                "category", category,//订单类别[U-当前订单|S-历史订单|A申诉订单]
//                "orderNo", "",
//                "pageNum", mPage + "",//当前页
//                "pageSize", DEFAULT_PAGE_SIZE + "",//每页的条数
//                "type", ""//买卖类型[1-买|2-卖]
//        );
        getOrderGet();
    }

    @Override
    public void onResponse(String url, String json, String session) {
        loadingDialog.dismiss();
//        Log.e("我的订单：","json" + json);
        if (url.equals(Constant.URL.myOrder)) {
            dismissLoading();
            OrderEnity enity = GsonUtil.fromJson(json, OrderEnity.class);
            //第一次加载的时候
            if (mPage == 1) {
                dismissLoading();
                if (dataList == null) {
                    dataList = new ArrayList<>();
                } else {
                    dataList.clear();
                }
            }

            removeLoading();

            if (Constant.Int.SUC == enity.getCode()) {
                dataList.addAll(enity.getData().getRecords());
                if (enity.getData().getCurrent() < enity.getData().getPages()) {//还有下一页
                    dataList.add(new OrderEnity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                    mPage++;
                } else {
                    addBaseLine();
                }
            } else {
                addBaseLine();
            }
            orderAdapter.setData(dataList);
        }
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(getActivity(), url, error);
    }


    /**
     * 加载更多
     */
    private void removeLoading() {
        if (dataList.size() > 0) {
            if (dataList.get(dataList.size() - 1).getLoadType() == 1) {
                dataList.remove(dataList.size() - 1);
            }
        }

    }

    private void addBaseLine() {
        if (mPage != 1) {
            dataList.add(new OrderEnity.DataBean.RecordsBean(2));
        }
    }

    @Override
    public void onCallback() {
        getMyOrder();
    }

    // 测试
    private void getOrderGet(){
        String url = Constant.URL.myOrder + "?category=" + category + "&pageNum=" + mPage + "&pageSize=" + DEFAULT_PAGE_SIZE;
        Thread mTh = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization",SharedUtil.getToken(getContext()));
                    conn.setConnectTimeout(60 * 1000);
                    conn.setReadTimeout(60 * 1000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        //得到响应流
                        InputStream is = conn.getInputStream();
                        //将响应流转换成字符串
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuffer sb = new StringBuffer();
                        String line = null;
                        while ((line = reader.readLine()) != null){
                            sb.append(line);
                        }
                        String reponseData = sb.toString(); // 获取数据
                        // 回到主线程
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                OrderEnity enity = GsonUtil.fromJson(reponseData, OrderEnity.class);
                                //第一次加载的时候
                                if (mPage == 1) {
                                    dismissLoading();
                                    if (dataList == null) {
                                        dataList = new ArrayList<>();
                                    } else {
                                        dataList.clear();
                                    }
                                }
                                removeLoading();
                                if (Constant.Int.SUC == enity.getCode()) {
                                    dataList.addAll(enity.getData().getRecords());
                                    if (enity.getData().getCurrent() < enity.getData().getPages()) {//还有下一页
                                        dataList.add(new OrderEnity.DataBean.RecordsBean(1));//这里是把正在加载显示出来
                                        mPage++;
                                    } else {
                                        addBaseLine();
                                    }
                                } else {
                                    addBaseLine();
                                }
                                orderAdapter.setData(dataList);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mTh.start();
    }

}
