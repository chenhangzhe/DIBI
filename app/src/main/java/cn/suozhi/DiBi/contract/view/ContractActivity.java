package cn.suozhi.DiBi.contract.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Transformer;
import com.google.android.material.tabs.TabLayout;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.chart.ChartFingerTouchListener;
import cn.suozhi.DiBi.common.chart.ChartListener;
import cn.suozhi.DiBi.common.chart.CoupleChartGestureListener;
import cn.suozhi.DiBi.common.chart.CoupleChartValueSelectedListener;
import cn.suozhi.DiBi.common.chart.HighlightCandleRenderer;
import cn.suozhi.DiBi.common.chart.HighlightCombinedRenderer;
import cn.suozhi.DiBi.common.chart.InBoundXAxisRenderer;
import cn.suozhi.DiBi.common.chart.InBoundYAxisRenderer;
import cn.suozhi.DiBi.common.custom.PieView;
import cn.suozhi.DiBi.common.custom.RingProgressBar;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.SocketUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.contract.adapter.ContractRVAdapter;
import cn.suozhi.DiBi.contract.model.AnalysisEntity;
import cn.suozhi.DiBi.contract.model.ContractEntity;
import cn.suozhi.DiBi.contract.model.DeliverEntity;
import cn.suozhi.DiBi.contract.model.FlowEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.home.view.HelpSearchActivity;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.market.model.Bar;

/**
 * 预测合约
 */
public class ContractActivity extends BaseActivity implements TabLayout.BaseOnTabSelectedListener,
        CoupleChartGestureListener.OnEdgeListener, CoupleChartValueSelectedListener.ValueSelectedListener,
        ChartFingerTouchListener.HighlightListener, View.OnClickListener, OkHttpUtil.OnDataListener,
        TextWatcher, AbsRecyclerAdapter.OnItemClickListener, RingProgressBar.OnChangeListener {

    @BindView(R.id.tv_conSymbol)
    public TextView tvSymbol;
    @BindView(R.id.tv_conTime)
    public TextView tvTime;
    @BindView(R.id.rv_contract)
    public RecyclerView recyclerView;

    private TextView tvPre, tvPreCoin, tvPrice, tvPriceCoin;
    private RingProgressBar rpb;
    private TabLayout tlKline, tlContract;
    private CombinedChart cp, cv;
    private TextView tvMa5p, tvMa5v, tvMa10p, tvMa10v, tvMa20p, tvMa20v;

    private View headLogout, headEmpty, headTrade, headAnalysis;
    private View headDeliver, headEntrust, headFlow, headHold;

    private EditText etAmount;
    private TextView tvAvail, tvAvailCoin, tvCommission, tvCommissionCoin, tvExpect;
    private List<View> tvsPercent;

    private List<TextView> tvsType;
    private TextView tvDraw, tvWin, tvLose;
    private TextView tvIn, tvInCoin, tvOut, tvOutCoin, tvAll, tvAllCoin;
    private PieView pie;
//    private PieChart pc;
//    private PieDataSet pieSet;

    private ContractRVAdapter recyclerAdapter;
    private List<ContractEntity> conList;

    private List<Bar> dataList;
    private Map<Integer, Long> xValues;
    private LineDataSet lineMin;//分时线
    private LineDataSet linePrice5;
    private LineDataSet linePrice10;
    private LineDataSet linePrice20;
    private CandleDataSet candleSet;
    private CombinedData combinedPrice;
    private LineDataSet lineVol5;
    private LineDataSet lineVol10;
    private LineDataSet lineVol20;
    private BarDataSet barSet;
    private CombinedData combinedVol;
    private CoupleChartGestureListener cpGesture;
    private CoupleChartGestureListener cvGesture;
    private final float barOffset = -0.5F;//BarChart偏移量
    private final float firstOffset = -0.5F;//第一个数据偏移量
    private final int range = 52;// 一屏显示Candle个数
    private int pp = 4, tp = 2;

    private String token, symbol = Constant.Strings.Default_Symbol;
    private Symbol sym;
    private long loginTime;
    private int socketType = -1;//获取币种状态 0 - 建立链接中 、 1 - 获取中 、 2 - 获取失败 、 3 - 获取后本地未登录
    // 4 - 获取后正在登录 、 5 - 获取后登录失败 、 6 - 获取后登录成功
    private WebSocketClient client;

    private boolean fromUser = true;//是否手动输入
    private double avail;
    private int page;//当前页

    private String[] TAB_KLINE, TAB_CONTRACT;
    private String[] KLINE = {"MN1", "MN1", "MN15", "H1", "H4", "D1"};
    private String[] ANALYSIS = {"1", "2", "3", "0"};
    private int index_kline = 0, index_contract = 0;
    private long[] INTERVAL = {1, 1, 15, 60, 240, 1440};//单位: Min

    private LoadingDialog loadingDialog;
    private Handler handler = new Handler();
    public static SimpleDateFormat sdf_Mm = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    @Override
    protected int getViewResId() {
        openRestartInResume();
        setNavigationColor(R.color.bk1A);
        return R.layout.activity_contract;
    }

    @Override
    protected void init() {
        lang = SharedUtil.getLanguage4Socket(this);
        TAB_KLINE = new String[]{getString(R.string.divideMin), getString(R.string.min1),
                getString(R.string.min15), getString(R.string.hour1), getString(R.string.hour4),
                getString(R.string.day1)};
        TAB_CONTRACT = new String[]{getString(R.string.trade), getString(R.string.deliverAlready),
                getString(R.string.deliverWaited), getString(R.string.currentEntrust),
                getString(R.string.historyEntrust), getString(R.string.flow), getString(R.string.analysis)};

        View headKline = LayoutInflater.from(this).inflate(R.layout.contract_kline, null);
        findWidget(headKline);
        initChart();

        conList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new ContractRVAdapter(this, conList);
        recyclerView.setAdapter(recyclerAdapter.addHeaderView(headKline)
                .setOnItemClickListener(this));

        showLoading();
        socket();
    }

    private void findWidget(View head) {
        //K线
        tvPre = head.findViewById(R.id.tv_klConPrePrice);
        tvPreCoin = head.findViewById(R.id.tv_klConPreCoin);
        tvPrice = head.findViewById(R.id.tv_klConPrice);
        tvPriceCoin = head.findViewById(R.id.tv_klConPriceCoin);
        rpb = head.findViewById(R.id.rpb_klCon);
        rpb.setOnChangeListener(this);

        tlKline = head.findViewById(R.id.tl_klConKline);
        cp = head.findViewById(R.id.cc_klConPrice);
        cv = head.findViewById(R.id.cc_klConVol);
        tvMa5p = head.findViewById(R.id.tv_klConMa5p);
        tvMa10p = head.findViewById(R.id.tv_klConMa10p);
        tvMa20p = head.findViewById(R.id.tv_klConMa20p);
        tvMa5v = head.findViewById(R.id.tv_klConMa5v);
        tvMa10v = head.findViewById(R.id.tv_klConMa10v);
        tvMa20v = head.findViewById(R.id.tv_klConMa20v);
        tlContract = head.findViewById(R.id.tl_klConContract);

        headLogout = head.findViewById(R.id.cl_logoutCon);
        headTrade = head.findViewById(R.id.cl_tradeCon);
        headDeliver = head.findViewById(R.id.cl_deliverHead);
        headEntrust = head.findViewById(R.id.cl_entCon);
        headFlow = head.findViewById(R.id.cl_flowCon);
        headAnalysis = head.findViewById(R.id.cl_anaCon);
        headHold = head.findViewById(R.id.v_klConHold);
        headEmpty = head.findViewById(R.id.cl_emptyCon);

        head.findViewById(R.id.tv_logoutConGo).setOnClickListener(this);

        //交易
        etAmount = head.findViewById(R.id.et_tradeConAmount);
        etAmount.addTextChangedListener(this);
        head.findViewById(R.id.tv_tradeConMinus).setOnClickListener(this);
        head.findViewById(R.id.tv_tradeConPlus).setOnClickListener(this);
        tvAvail = head.findViewById(R.id.tv_tradeConAvail);
        tvAvailCoin = head.findViewById(R.id.tv_tradeConAvailCoin);
        tvCommission = head.findViewById(R.id.tv_tradeConCommission);
        tvCommissionCoin = head.findViewById(R.id.tv_tradeConCommissionCoin);
        tvCommissionCoin = head.findViewById(R.id.tv_tradeConCommissionCoin);
        tvsPercent = new ArrayList<>();
        tvsPercent.add(head.findViewById(R.id.tv_tradeConPercent25));
        tvsPercent.add(head.findViewById(R.id.tv_tradeConPercent50));
        tvsPercent.add(head.findViewById(R.id.tv_tradeConPercent75));
        tvsPercent.add(head.findViewById(R.id.tv_tradeConPercent100));
        for (int i = 0; i < tvsPercent.size(); i++) {
            tvsPercent.get(i).setOnClickListener(this);
        }
        tvExpect = head.findViewById(R.id.tv_tradeConExpect);
        head.findViewById(R.id.tv_tradeConRise).setOnClickListener(this);
        head.findViewById(R.id.tv_tradeConFall).setOnClickListener(this);

        //分析
        tvsType = new ArrayList<>();
        tvsType.add(head.findViewById(R.id.tv_anaConDay));
        tvsType.add(head.findViewById(R.id.tv_anaConWeek));
        tvsType.add(head.findViewById(R.id.tv_anaConMonth));
        tvsType.add(head.findViewById(R.id.tv_anaConAll));
        tvDraw = head.findViewById(R.id.tv_anaConDraw);
        tvWin = head.findViewById(R.id.tv_anaConWin);
        tvLose = head.findViewById(R.id.tv_anaConLose);
        tvIn = head.findViewById(R.id.tv_anaConIncome);
        tvInCoin = head.findViewById(R.id.tv_anaConIncomeCoin);
        tvOut = head.findViewById(R.id.tv_anaConOutcome);
        tvOutCoin = head.findViewById(R.id.tv_anaConOutcomeCoin);
        tvAll = head.findViewById(R.id.tv_anaConTotal);
        tvAllCoin = head.findViewById(R.id.tv_anaConTotalCoin);
        pie = head.findViewById(R.id.pv_anaCon);
        tvsType.get(tvsType.size() - 1).setSelected(true);
        for (int i = 0; i < tvsType.size(); i++) {
            tvsType.get(i).setOnClickListener(this);
        }
        pie.setColors(Util.getColor(this, R.color.blue11, R.color.green25, R.color.yellowD9));
//        initPie();

        int rf6 = Util.getColor(this, R.color.redF6);
        int re6 = Util.getColor(this, R.color.redE6);
        int w_t66 = Util.getColor(this, R.color.white_t66);
        rpb.setDivide(11, 4)
                .setColorBack(rf6, re6)
                .setColorRing(w_t66, w_t66);

        Util.tabInit(tlKline, TAB_KLINE, R.layout.tab_12_purple_77_gy_5a, R.id.tv_tab12Pg5Name,
                0, index_kline, R.id.v_tipIndicator, 0.4F);
        tlKline.addOnTabSelectedListener(new KlineListener());
        int dp40 = Util.dp2px(this, 40);
        Util.tabInit(tlContract, TAB_CONTRACT, R.layout.tab_12_purple_77_gy_5a, R.id.tv_tab12Pg5Name,
                dp40, index_contract, R.id.v_tipIndicator, 0.16F);
        tlContract.addOnTabSelectedListener(this);
    }

    /*private void initPie() {
        //饼状图
        pc.setNoDataTextColor(Color.TRANSPARENT);
        pc.setDescription(null);
        pc.getLegend().setEnabled(false);
        pc.setMinOffset(0);

        pc.setDrawHoleEnabled(false);
        pc.setRotationEnabled(false);
//        pc.setHighlightPerTapEnabled(false);

        pieSet = new PieDataSet(new ArrayList<>(), "Pie");
        pieSet.setDrawValues(false);
        pieSet.setColors(Util.getColor(this, R.color.blue11, R.color.green25, R.color.yellowD9));
    }*/

    @SuppressLint("ClickableViewAccessibility")
    private void initChart() {
        int gy5a = Util.getColor(this, R.color.gy5A);//文字
        int gy7a = Util.getColor(this, R.color.gy7A_t80);//网格线
        int green = Util.getColor(this, R.color.green0E);
        int red = Util.getColor(this, R.color.redF6);
        float sp8 = Util.sp2px(this, 8);
        int highColor = Util.getColor(this, R.color.white);
        float highWidth = 0.8F;
        //K线-价格
        cp.setNoDataTextColor(gy5a);
        cp.setDescription(null);//取消描述
        cp.getLegend().setEnabled(false);//取消图例
        cp.setDragDecelerationEnabled(false);//不允许甩动惯性滑动  和moveView方法有冲突 设置为false
        cp.setMinOffset(0);//设置外边缘偏移量

        cp.setScaleEnabled(false);//不可缩放
        cp.setAutoScaleMinMaxEnabled(true);//自适应最大最小值
        cp.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE});
        //自定义Y轴标签位置
//        cp.setRendererRightYAxis(new InBoundYAxisRenderer(cp.getViewPortHandler(), cp.getAxisRight(),
//                cp.getTransformer(YAxis.AxisDependency.RIGHT)));
        cp.setRendererLeftYAxis(new InBoundYAxisRenderer(cp.getViewPortHandler(), cp.getAxisLeft(),
                cp.getTransformer(YAxis.AxisDependency.LEFT)));
        //设置渲染器控制颜色、偏移，以及高亮
        cp.setRenderer(new HighlightCombinedRenderer(cp, cp.getAnimator(), cp.getViewPortHandler(), 0)
                .setHighWidthSize(highWidth, sp8)
                .setExtremeColorSize(HighlightCandleRenderer.Extreme.EX_MAX_MIN, highColor, sp8)
                .setChartListener(new ChartListener(){
                    @Override
                    public int getPoint() {
                        return tp;
                    }
                }));

        //X轴
//        cp.getXAxis().setEnabled(false);
        XAxis xp = cp.getXAxis();
        xp.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xp.setDrawGridLines(false);
        xp.setDrawLabels(false);
        xp.setAxisLineColor(gy7a);//轴线颜色
        xp.setAxisLineWidth(0.5F);
        xp.setGridColor(gy7a);
        xp.setGridLineWidth(0.5F);
        xp.setTextColor(gy5a);//标签颜色
        xp.setTextSize(8);//标签字体大小
        xp.setAvoidFirstLastClipping(true);
        xp.setLabelCount(4, true);
        //左Y轴
//        cp.getAxisLeft().setEnabled(false);
        YAxis ylp = cp.getAxisLeft();
        ylp.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        ylp.setDrawAxisLine(false);
        ylp.setGridColor(gy7a);
        ylp.setGridLineWidth(0.5F);
        ylp.setTextColor(gy5a);
        ylp.setTextSize(8);
        ylp.setLabelCount(4, true);
        ylp.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return AppUtil.getYLabel(value, cp.getAxisLeft().getAxisMaximum());
            }
        });
        //右Y轴
        cp.getAxisRight().setEnabled(false);
        /*YAxis yrp = cp.getAxisRight();
        yrp.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yrp.setDrawAxisLine(false);
        yrp.setGridColor(gy7a);
        yrp.setGridLineWidth(0.5F);
        yrp.setTextColor(gy5a);
        yrp.setTextSize(8);
        yrp.setLabelCount(4, true);
        yrp.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return AppUtil.getYLabel(value, cp.getAxisRight().getAxisMaximum());
            }
        });*/

        //蜡烛图
        candleSet = new CandleDataSet(new ArrayList<>(), "Kline");
        candleSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleSet.setDrawHorizontalHighlightIndicator(false);
        candleSet.setHighlightLineWidth(highWidth);
        candleSet.setHighLightColor(highColor);
        candleSet.setShadowWidth(0.7f);
        candleSet.setIncreasingColor(green);
        candleSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleSet.setDecreasingColor(red);
        candleSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleSet.setNeutralColor(green);
        candleSet.setShadowColorSameAsCandle(true);
        candleSet.setDrawValues(false);
        candleSet.setHighlightEnabled(false);
        //均线-MA5
        linePrice5 = new LineDataSet(new ArrayList<>(), "MA5P");
        linePrice5.setAxisDependency(YAxis.AxisDependency.LEFT);
        linePrice5.setColor(Util.getColor(this, R.color.yellowAB));
        linePrice5.setDrawCircles(false);
        linePrice5.setDrawValues(false);
        linePrice5.setHighlightEnabled(false);
        //均线-MA10
        linePrice10 = new LineDataSet(new ArrayList<>(), "MA10P");
        linePrice10.setAxisDependency(YAxis.AxisDependency.LEFT);
        linePrice10.setColor(Util.getColor(this, R.color.blue64));
        linePrice10.setDrawCircles(false);
        linePrice10.setDrawValues(false);
        linePrice10.setHighlightEnabled(false);
        //均线-MA20
        linePrice20 = new LineDataSet(new ArrayList<>(), "MA20P");
        linePrice20.setAxisDependency(YAxis.AxisDependency.LEFT);
        linePrice20.setColor(Util.getColor(this, R.color.purple8D));
        linePrice20.setDrawCircles(false);
        linePrice20.setDrawValues(false);
        linePrice20.setHighlightEnabled(false);
        //分时线
        lineMin = new LineDataSet(new ArrayList<>(), "Min");
        lineMin.setAxisDependency(YAxis.AxisDependency.LEFT);
//        lineMin.setColor(Util.getColor(this, R.color.purple77));
        lineMin.setColor(Util.getColor(this, R.color.color_1888FE));
        lineMin.setHighlightLineWidth(highWidth);
        lineMin.setHighLightColor(highColor);
        lineMin.setDrawCircles(false);
        lineMin.setDrawValues(false);
        lineMin.setHighlightEnabled(false);
        lineMin.setDrawFilled(true);
        lineMin.setFillDrawable(Util.getDrawable(this, R.drawable.spn_grad270_kline));

        //K线-成交量
        cv.setNoDataTextColor(gy5a);
        cv.setDescription(null);
        cv.getLegend().setEnabled(false);
        cv.setDragDecelerationEnabled(false);//不允许甩动惯性滑动
        cv.setMinOffset(0);//设置外边缘偏移量
        cv.setExtraBottomOffset(5);//设置底部外边缘偏移量 便于显示X轴

        cv.setScaleEnabled(false);//不可缩放
        cv.setAutoScaleMinMaxEnabled(true);//自适应最大最小值
        cv.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        Transformer trans = cv.getTransformer(YAxis.AxisDependency.LEFT);
        //自定义X轴标签位置
        cv.setXAxisRenderer(new InBoundXAxisRenderer(cv.getViewPortHandler(), cv.getXAxis(), trans, 4));
        //自定义Y轴标签位置
//        cv.setRendererRightYAxis(new InBoundYAxisRenderer(cv.getViewPortHandler(), cv.getAxisRight(), trans));
        cv.setRendererLeftYAxis(new InBoundYAxisRenderer(cv.getViewPortHandler(), cv.getAxisLeft(), trans));
        //设置渲染器控制颜色、偏移，以及高亮
        cv.setRenderer(new HighlightCombinedRenderer(cv, cv.getAnimator(), cv.getViewPortHandler(), barOffset)
                .setHighWidthSize(highWidth, sp8)
                .setChartListener(new ChartListener(){
                    @Override
                    public int getPoint() {
                        return pp;
                    }
                }));

        //X轴
        XAxis xv = cv.getXAxis();
        xv.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xv.setDrawGridLines(false);
        xv.setAxisLineColor(gy7a);//轴线颜色
        xv.setAxisLineWidth(0.5F);
        xv.setGridColor(gy7a);
        xv.setGridLineWidth(0.5F);
        xv.setTextColor(gy5a);//标签颜色
        xv.setTextSize(8);//标签字体大小
        xv.setAvoidFirstLastClipping(true);
        xv.setLabelCount(4, true);
        xv.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int x = (int) value + 1;
                if (!xValues.containsKey(x) && xValues.containsKey(x + 1)) {
                    x += 1;
                }
                if (!xValues.containsKey(x) && xValues.containsKey(x - 1)) {
                    x -= 1;
                }
                if (!xValues.containsKey(x)) {
                    return "";
                }
                return sdf_Mm.format(xValues.get(x));
            }
        });
        //左Y轴
//        cv.getAxisLeft().setEnabled(false);
        YAxis ylv = cv.getAxisLeft();
        ylv.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        ylv.setDrawAxisLine(false);
        ylv.setDrawGridLines(false);
        ylv.setTextColor(gy5a);
        ylv.setTextSize(8);
        ylv.setLabelCount(2, true);
        ylv.setAxisMinimum(0);
        ylv.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return AppUtil.getYLabel(value, cv.getAxisLeft().getAxisMaximum());
            }
        });
        //右Y轴
        cv.getAxisRight().setEnabled(false);
        /*YAxis yrv = cv.getAxisRight();
        yrv.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yrv.setDrawAxisLine(false);
        yrv.setDrawGridLines(false);
        yrv.setTextColor(gy5a);
        yrv.setTextSize(8);
        yrv.setLabelCount(2, true);
        yrv.setAxisMinimum(0);
        yrv.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return AppUtil.getYLabel(value, cv.getAxisRight().getAxisMaximum());
            }
        });*/

        barSet = new BarDataSet(new ArrayList<>(), "VOL");
        barSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barSet.setHighLightColor(highColor);
        barSet.setColors(green, red);
        barSet.setDrawValues(false);
        barSet.setHighlightEnabled(false);
        //均线-MA5
        lineVol5 = new LineDataSet(new ArrayList<>(), "MA5V");
        lineVol5.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineVol5.setColor(Util.getColor(this, R.color.yellowAB));
        lineVol5.setDrawCircles(false);
        lineVol5.setDrawValues(false);
        lineVol5.setHighlightEnabled(false);
        //均线-MA10
        lineVol10 = new LineDataSet(new ArrayList<>(), "MA10V");
        lineVol10.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineVol10.setColor(Util.getColor(this, R.color.blue64));
        lineVol10.setDrawCircles(false);
        lineVol10.setDrawValues(false);
        lineVol10.setHighlightEnabled(false);
        //均线-MA20
        lineVol20 = new LineDataSet(new ArrayList<>(), "MA20V");
        lineVol20.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineVol20.setColor(Util.getColor(this, R.color.purple8D));
        lineVol20.setDrawCircles(false);
        lineVol20.setDrawValues(false);
        lineVol20.setHighlightEnabled(false);

        cpGesture = new CoupleChartGestureListener(this, cp, cv);//设置成全局变量，后续要用到
        cp.setOnChartGestureListener(cpGesture);//设置手势联动监听
        cvGesture = new CoupleChartGestureListener(this, cv, cp);
        cv.setOnChartGestureListener(cvGesture);

        cp.setOnChartValueSelectedListener(new CoupleChartValueSelectedListener(this, cp, cv));//设置高亮联动监听
        cv.setOnChartValueSelectedListener(new CoupleChartValueSelectedListener(this, cv, cp));
        cp.setOnTouchListener(new ChartFingerTouchListener(cp, this));//手指长按滑动高亮
        cv.setOnTouchListener(new ChartFingerTouchListener(cv, this));
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

    private void socket() {
        initUI();
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
                dismissLoading();
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
            loginAccount(Constant.Code.Login);
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
        clearChart();
        getBar(-1, 100, index_kline <= 1);
        getDataWithoutBar();
    }

    private void getDataWithoutBar() {
        getQuote();
        switch (index_contract) {
            case 0:
                dismissLoading();
                break;
            case 1:
                getDeliver(true);
                break;
            case 2:
                getDeliver(false);
                break;
            case 3:
                getEntrust(true);
                break;
            case 4:
                getEntrust(false);
                break;
            case 5:
                getFlow();
                break;
            case 6:
                getAnalysis();
                break;
        }
    }

    private void getDeliver(boolean filled) {
        page = 1;
        getDeliverData(filled);
    }

    private void getDeliverData(boolean filled) {
        OkHttpUtil.postJsonToken(Constant.URL.GetDeliver, SharedUtil.getToken(this),
                this, "filled", filled + "", "pageNum", page + "", "pageSize", "20",
                "symbol", symbol);
    }

    private void getEntrust(boolean active) {
        page = 1;
        getOrder(active);
    }

    private void getFlow() {
        page = 1;
        getFlowData();
    }

    private void getFlowData() {
        OkHttpUtil.postJsonToken(Constant.URL.GetFlow, SharedUtil.getToken(this),
                this, "fundFlowType", "F", "pageNum", page + "", "pageSize", "20");
    }

    private void getAnalysis() {
        int index = getTypeIndex();
        OkHttpUtil.postJsonToken(Constant.URL.GetAnalysis, SharedUtil.getToken(this),
                this, "pairSymbol", symbol, "timePeriod", ANALYSIS[index]);
    }

    private int getTypeIndex() {
        if (tvsType == null || tvsType.size() == 0) {
            return ANALYSIS.length - 1;
        }
        int index = ANALYSIS.length - 1;
        for (int i = 0; i < tvsType.size(); i++) {
            if (tvsType.get(i).isSelected()) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Socket返回
     */
    private void load(ByteBuffer bytes) throws Exception {
        Messages.ResponseMessage symbol = Messages.ResponseMessage.parseFrom(bytes.array());
        Log.e(TAG, "Contract: " + symbol.getMsgCase().getNumber());
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
                String cl = login.getCode();
                if (Constant.Code.Login == login.getRequestId()) {
                    if (Constant.Int.SUC_S.equals(cl)) {
                        updateAccount(getAccount(login.getAccountsList()));
                        socketType = 6;
                    } else {
                        noLogin(cl);
                        socketType = 5;
                    }
                    getCoin();
                } else {
                    if (Constant.Int.SUC_S.equals(cl)) {
                        updateAccount(getAccount(login.getAccountsList()));
                        getDataWithoutBar();
                    } else {
                        dismissLoading();
                        noLogin(cl);
                    }
                }
                break;
            case 24://行情
                Messages.FutureQuoteResponse quote = symbol.getFutureQuote();
                if (Constant.Int.SUC_S.equals(quote.getCode())) {
                    updateQuote(quote.getQuote());
                }
                break;
            case 30://行情推送
                updateQuote(symbol.getFutureQuoteUpdate().getQuote());
                break;
            case 25://bar数据
                Messages.FutureBarResponse bar = symbol.getFutureBar();
                if (Constant.Int.SUC_S.equals(bar.getCode()) && KLINE[index_kline].equals(bar.getType())) {
                    updateBar(bar.getBarsList());
                }
                break;
            case 31://bar更新
                Messages.FutureBarUpdateResponse barUpdate = symbol.getFutureBarUpdate();
                if (KLINE[index_kline].equals(barUpdate.getType())) {
                    updateBar(barUpdate.getBar());
                }
                break;
            case 27://下单
                Messages.FuturePlaceOrderResponse place = symbol.getFuturePlaceOrder();
                dismissLoading();
                ToastUtil.initToast(this, Util.getCodeTextString(this,
                        place.getCode(), place.getMessage()));
                if (Constant.Int.SUC_S.equals(place.getCode())) {
                    etAmount.setText(null);
                }
                break;
            case 26://委托单
                Messages.FutureOrderResponse order = symbol.getFutureOrder();
                if (index_contract < 3 || index_contract > 4) {
                    break;
                }
                if (page == 1) {
                    dismissLoading();
                    conList.clear();
                }
                if (Constant.Int.SUC_S.equals(order.getCode())) {
                    changeLoading(true, null);
                    List<Messages.FutureDmOrder> list = order.getOrdersList();
                    for (int i = 0; i < list.size(); i++) {
                        Messages.FutureDmOrder l = list.get(i);
                        conList.add(new ContractEntity(l.getCreatedTime(), l.getOrderStatus(),
                                l.getPrice(), l.getFilledPrice(), l.getRoundClose(), l.getRoundPreClose(),
                                l.getAction(), 3));
                    }

                    if (order.getCurrentPage() < order.getTotalPage()) {//还有下一页
                        conList.add(new ContractEntity(ContractRVAdapter.MORE, 0));
                    } else if (page != 1) {
                        conList.add(new ContractEntity(1));
                    }
                    if (headHold.getVisibility() != View.GONE) {
                        headHold.setVisibility(View.GONE);
                    }
                    recyclerAdapter.setData(conList);
                    checkEmpty();
                } else {
                    changeLoading(false, ContractRVAdapter.MORE);
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            order.getCode(), order.getMessage()));
                    noLogin(order.getCode());
                }
                break;
        }
    }

    private void noLogin(String code) {
        if (Constant.Int.Socket_No_Login.equals(code) || Constant.Int.Socket_Please_Login.equals(code)) {
            clearLogin();
        }
    }

    private void clearLogin() {
        socketType = 5;
        SharedUtil.clearData(this);
        initUI();
        avail = 0;
        tvAvail.setText("--");
        tvCommission.setText(Util.formatFloor(0, tp));
    }

    private void initSymbol(List<Messages.DmCurrency> coinList, List<Messages.DmPair> pairList) {
        if (TextUtils.isEmpty(symbol) || pairList == null || pairList.size() == 0) {
            return;
        }
        //找出sym
        int index = -1;
        for (int i = 0; i < pairList.size(); i++) {
            if (symbol.equals(pairList.get(i).getSymbol())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            Messages.DmPair pair = pairList.get(index);
            int pid = pair.getBaseCurrencyId();
            int tid = pair.getQuoteCurrencyId();
            String p = findCoin(pid, coinList);
            String t = findCoin(tid, coinList);
            sym = new Symbol(symbol, p, t, pair.getSizeDisplayDp(), pair.getPriceDisplayDp(), pid, tid, pair.getFutRate());

            updateSymbol();
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

    public void updateSymbol() {
        if (sym != null) {
            String p = sym.getPCoin(), t = sym.getTCoin();
            pp = sym.getPPoint();
            tp = Math.min(sym.getTPoint(), 2);
            tvSymbol.setText(p + "/" + t);
            tvPreCoin.setText(t);
            tvPriceCoin.setText(t);
            recyclerAdapter.setTp(tp);

            String tc = "(" + t + ")";
            etAmount.setHint(getString(R.string.amount) + tc);
            tvAvailCoin.setText(t);
            tvCommissionCoin.setText(t);
            tvExpect.setHint(getString(R.string.expectIncome) + tc);

            tvInCoin.setText(t);
            tvOutCoin.setText(t);
            tvAllCoin.setText(t);
        }
    }

    private void loginAccount(long id) {
        if (!isLogin()) {
            dismissLoading();
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

    private void getQuote() {
        Messages.FutureQuoteRequest quote = Messages.FutureQuoteRequest.newBuilder()
                .setRequestId(Constant.Code.Contract_Quote)
                .setSymbol(symbol)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setFutureQuote(quote)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getBar(long from, int number, boolean watch) {
        if (index_kline < 0 || index_kline >= KLINE.length) {
            return;
        }
        Messages.FutureBarRequest.Builder bar = Messages.FutureBarRequest.newBuilder()
                .setRequestId(Constant.Code.Contract_Bar)
                .setSymbol(symbol)
                .setType(KLINE[index_kline])
                .setNumber(number)
                .setWatch(watch)
                .setNoFill(false);
        if (from > 0) {
            bar.setFrom(from);
        }
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setFutureBar(bar)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void placeOrder(String action, String price) {
        showLoading();
        Messages.FuturePlaceOrderRequest place = Messages.FuturePlaceOrderRequest.newBuilder()
                .setRequestId(Constant.Code.Contract_Place)
                .setSymbol(symbol)
                .setAction(action)
                .setPrice(price)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setFuturePlaceOrder(place)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getOrder(boolean active) {
        Messages.FutureOrderRequest order = Messages.FutureOrderRequest.newBuilder()
                .setRequestId(Constant.Code.Contract_Order)
                .setSymbol(symbol)
                .setActiveOnly(active)
                .setPage(page)
                .setSize(15)
                .setWatch(false)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setFutureOrder(order)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void stopUpdate() {
        Messages.StopUpdateRequest stop = Messages.StopUpdateRequest.newBuilder()
                .setRequestId(Constant.Code.Stop)
                .setFutureQuote(true)
                .setFutureBar(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setStopUpdate(stop)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    public void updateQuote(Messages.FutureDmQuote q) {
        startClock(q.getTime());

        String time = getString(q.getStatus() == 1 ? R.string.tradeGoing : R.string.tradeStop) + " " +
                sdf_Mm.format(q.getTime());
        tvTime.setText(time);
        double pre = q.getPrevClose();
        tvPre.setText(Util.formatFloor(pre, tp));
        tvPre.setSelected(pre >= q.getPopClose());
        double price = q.getPrice();
        tvPrice.setText(Util.formatFloor(price, tp));
        tvPrice.setSelected(price >= pre);
    }

    private void startClock(long time) {
        if (time <= 0) {
            return;
        }
        long s = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
        calendar.set(Calendar.SECOND, 0);
        long target = calendar.getTimeInMillis();
        long e = System.currentTimeMillis();
        long count = target - time - (e - s);
        if (count < 0) {
            return;
        }
        rpb.setProgress(count / 1000F).start();
    }

    private Messages.DmUserAccount getAccount(List<Messages.DmUserAccount> list) {
        if (list == null || sym == null) {
            return null;
        }
        int id = sym.getTId();
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getCurrencyId()) {
                index = i;
                break;
            }
        }
        return index < 0 ? null : list.get(index);
    }

    public void updateAccount(Messages.DmUserAccount a) {
        avail = a == null ? 0 : Util.parseDouble(a.getAvailable());
        String rebate = a == null ? null : a.getRebate();
        tvAvail.setText(Util.formatFloor(avail, tp));
        tvCommission.setText(TextUtils.isEmpty(rebate) ? Util.formatDecimal(0, tp) :
                Util.formatDecimal(rebate, tp));
    }

    private void clearChart() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        } else {
            dataList.clear();
        }
        if (xValues == null) {
            xValues = new HashMap<>();
        } else {
            xValues.clear();
        }
        cp.setNoDataText(getString(R.string.loading));
        cp.clear();
        cv.setNoDataText(getString(R.string.loading));
        cv.clear();
    }

    private void updateBar(List<Messages.FutureDmBar> barList) {
        if (barList == null) {
            return;
        }
        List<Bar> list = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            Messages.FutureDmBar b = barList.get(i);
            list.add(new Bar(b.getTime(), b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume()));
        }
        Collections.sort(list);
        handleData(list, xValues.size());
    }

    private void updateBar(Messages.FutureDmBar b) {
        List<Bar> list = new ArrayList<>();
        list.add(new Bar(b.getTime(), b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume()));
        handleData(list, xValues.size());
    }

    private void handleData(List<Bar> list, int size) {
        boolean right;
        float highVisX = dataList.size() == 0 ? 0 : cp.getHighestVisibleX();
        if (dataList.size() == 0) {
            right = true;
            dataList.addAll(list);
        } else if (list.get(list.size() - 1).getTime() <= dataList.get(0).getTime()) {
            right = false;
            dataList.addAll(0, list);//添加到左侧
        } else {
            right = true;
            removeRightRepeat(list);
            dataList.addAll(list);
        }

        configData();
        if (xValues.size() > 0) {
            float x;
            boolean moveAnim = right && Math.abs(size - highVisX) <= 2;
            if (moveAnim) {
                x = xValues.size();
            } else {
                x = right ? highVisX - range : xValues.size() - size;
            }
            //如果设置了惯性甩动 move方法将会无效
            if (right) {
                cp.moveViewToAnimated(x, 0, YAxis.AxisDependency.LEFT, 200);
                cv.moveViewToAnimated(x + barOffset, 0, YAxis.AxisDependency.LEFT, 200);
            } else {
                cp.moveViewToX(x);
                cv.moveViewToX(x + barOffset);
            }
            cp.notifyDataSetChanged();
            cv.notifyDataSetChanged();
        }
    }

    private void removeRightRepeat(List<Bar> list) {
        for (int i = 0; i < list.size(); i++) {
            long t = list.get(i).getTime();
            for (int j = dataList.size() - 1; j >= 0; j--) {
                if (t == dataList.get(j).getTime()) {
                    dataList.remove(j);
                }
            }
        }
    }

    private void configData() {
        if (dataList == null) {
            return;
        }
        if (dataList.size() == 0) {
            cp.setNoDataText(getString(R.string.emptyData));
            cp.clear();
            cv.setNoDataText(getString(R.string.emptyData));
            cv.clear();
        } else {
            Collections.sort(dataList);
            if (combinedPrice == null) {
                combinedPrice = new CombinedData();
            }
            if (combinedVol == null) {
                combinedVol = new CombinedData();
            }
            xValues.clear();
            List<CandleEntry> candle = candleSet.getValues();
            candle.clear();
            List<Entry> ma5p= linePrice5.getValues();
            ma5p.clear();
            List<Entry> ma10p= linePrice10.getValues();
            ma10p.clear();
            List<Entry> ma20p= linePrice20.getValues();
            ma20p.clear();
            List<Entry> min = lineMin.getValues();
            min.clear();
            List<BarEntry> bar = barSet.getValues();
            bar.clear();
            List<Entry> ma5v= lineVol5.getValues();
            ma5v.clear();
            List<Entry> ma10v= lineVol10.getValues();
            ma10v.clear();
            List<Entry> ma20v= lineVol20.getValues();
            ma20v.clear();
            for (int i = 0; i < dataList.size(); i++) {
                Bar b = dataList.get(i);
                long x = b.getTime();
                if (xValues.containsValue(x)) {//重复
                    dataList.remove(i);
                    i--;
                } else {
                    xValues.put(i, x);
                    float open = b.getOpen();
                    float close = b.getClose();
                    String xTime = sdf_Mm.format(x);
                    candle.add(new CandleEntry(i, b.getHigh(), b.getLow(), open, close, xTime));
                    min.add(new Entry(i, close, xTime));
                    String barData = close >= open ? "0" : "1";
                    bar.add(new BarEntry(i, b.getVolume(), barData + "," + xTime));
                    if (i >= 4) {
                        ma5p.add(new Entry(i, getMA(i, 5, true)));
                        ma5v.add(new Entry(i + barOffset, getMA(i, 5, false)));//使和Bar的X对齐
                        if (i >= 9) {
                            ma10p.add(new Entry(i, getMA(i, 10, true)));
                            ma10v.add(new Entry(i + barOffset, getMA(i, 10, false)));
                        }
                        if (i >= 19) {
                            ma20p.add(new Entry(i, getMA(i, 20, true)));
                            ma20v.add(new Entry(i + barOffset, getMA(i, 20, false)));
                        }
                    }
                }
            }
//            addLimit(dataList.get(dataList.size() - 1).getClose());

            candleSet.setValues(candle);
            linePrice5.setValues(ma5p);
            linePrice10.setValues(ma10p);
            linePrice20.setValues(ma20p);
            lineMin.setValues(min);
            barSet.setValues(bar);
            lineVol5.setValues(ma5v);
            lineVol10.setValues(ma10v);
            lineVol20.setValues(ma20v);
            if (index_kline == 0) {
                combinedPrice.removeDataSet(candleSet);
                combinedPrice.setData(new LineData(lineMin));
            } else {
                combinedPrice.setData(new CandleData(candleSet));
                if (linePrice5.getEntryCount() > 0) {
                    combinedPrice.setData(new LineData(linePrice5, linePrice10, linePrice20));
                } else {
                    combinedPrice.removeDataSet(linePrice5);
                    combinedPrice.removeDataSet(linePrice10);
                    combinedPrice.removeDataSet(linePrice20);
                }
            }
            if (lineVol5.getEntryCount() > 0) {
                combinedVol.setData(new LineData(lineVol5, lineVol10, lineVol20));
            } else {
                combinedVol.removeDataSet(lineVol5);
                combinedVol.removeDataSet(lineVol10);
                combinedVol.removeDataSet(lineVol20);
            }

            cp.setData(combinedPrice);
            float xMax = Math.max(xValues.size(), range) - 0.5F;//默认X轴最大值是 xValues.size() - 1
            cp.getXAxis().setAxisMaximum(xMax);//使最后一个显示完整
            cp.getXAxis().setAxisMinimum(firstOffset);//使第一个显示完整

            BarData barData = new BarData(barSet);
            barData.setBarWidth(1 - candleSet.getBarSpace() * 2);//使Candle和Bar宽度一致
            combinedVol.setData(barData);
            cv.setData(combinedVol);
            cv.getXAxis().setAxisMaximum(xMax + barOffset);//保持边缘对齐
            cv.getXAxis().setAxisMinimum(firstOffset + barOffset);//使第一个显示完整

            cp.setVisibleXRange(range, range);//设置显示X轴个数的上下限，竖屏固定52个
            cv.setVisibleXRange(range, range);
        }
    }

    private void addLimit(float limit) {
        cp.getAxisRight().removeAllLimitLines();
        LimitLine ll = new LimitLine(limit);
        ll.setLineWidth(0.8F);
        ll.setLineColor(Util.getColor(this, R.color.green51));
        ll.setTextColor(Color.WHITE);
        ll.setTextSize(8);
        ll.enableDashedLine(11, 5, 0);
        cp.getAxisRight().addLimitLine(ll);
    }

    private float getMA(int index, int maxCount, boolean isClose) {
        int count = 1;
        float sum;
        if (isClose) {
            sum = dataList.get(index).getClose();
        } else {
            sum = dataList.get(index).getVolume();
        }
        while (count < maxCount) {
            if (--index < 0) {
                break;
            }
            if (isClose) {
                sum += dataList.get(index).getClose();
            } else {
                sum += dataList.get(index).getVolume();
            }
            count++;
        }
        return sum / count;
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e("loge", "Contract: " + json);
        switch (url) {
            case Constant.URL.GetDeliver:
                if (index_contract < 1 || index_contract > 2) {
                    break;
                }
                if (page == 1) {
                    dismissLoading();
                    conList.clear();
                }
                DeliverEntity deliver = JsonUtil.fromJsonO(json, DeliverEntity.class);
                if (deliver == null) {
                    changeLoading(false, ContractRVAdapter.MORE);
                    break;
                }
                if (Constant.Int.SUC == deliver.getCode()) {
                    changeLoading(true, null);
                    DeliverEntity.DataEntity d = deliver.getData();
                    for (int i = 0; i < d.getList().size(); i++) {
                        DeliverEntity.DataEntity.ListEntity l = d.getList().get(i);
                        conList.add(new ContractEntity(l.getCreatedDate(), l.getOrderStatus(),
                                l.getOrderAmount(), l.getFilledAmount(), 2));
                    }

                    if (d.getCurrentPage() < d.getTotalPage()) {//还有下一页
                        conList.add(new ContractEntity(ContractRVAdapter.MORE, 0));
                    } else if (page != 1) {
                        conList.add(new ContractEntity(1));
                    }
                    if (headHold.getVisibility() != View.GONE) {
                        headHold.setVisibility(View.GONE);
                    }
                    recyclerAdapter.setData(conList);
                    checkEmpty();
                } else {
                    changeLoading(false, ContractRVAdapter.MORE);
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            deliver.getCode(), deliver.getMsg()));
                }
                break;
            case Constant.URL.GetFlow:
                if (index_contract != 5) {
                    break;
                }
                if (page == 1) {
                    dismissLoading();
                    conList.clear();
                }
                FlowEntity flow = JsonUtil.fromJsonO(json, FlowEntity.class);
                if (flow == null) {
                    changeLoading(false, ContractRVAdapter.MORE);
                    break;
                }
                if (Constant.Int.SUC == flow.getCode()) {
                    changeLoading(true, null);
                    FlowEntity.DataEntity d = flow.getData();
                    for (int i = 0; i < d.getList().size(); i++) {
                        FlowEntity.DataEntity.ListEntity l = d.getList().get(i);
                        conList.add(new ContractEntity(l.getUpdatedDate(), l.getReasonCode(),
                                Util.formatDecimal(l.getAmount(), tp), 4));
                    }

                    if (d.getCurrentPage() < d.getTotalPage()) {//还有下一页
                        conList.add(new ContractEntity(ContractRVAdapter.MORE, 0));
                    } else if (page != 1) {
                        conList.add(new ContractEntity(1));
                    }
                    if (headHold.getVisibility() != View.GONE) {
                        headHold.setVisibility(View.GONE);
                    }
                    recyclerAdapter.setData(conList);
                    checkEmpty();
                } else {
                    changeLoading(false, ContractRVAdapter.MORE);
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            flow.getCode(), flow.getMsg()));
                }
                break;
            case Constant.URL.GetAnalysis:
                if (index_contract != 6) {
                    break;
                }
                dismissLoading();
                AnalysisEntity ana = JsonUtil.fromJsonO(json, AnalysisEntity.class);
                if (ana == null) {
                    break;
                }
                if (Constant.Int.SUC == ana.getCode()) {
                    AnalysisEntity.DataEntity d = ana.getData();
                    int draw = d.getDrawCount();
                    int win = d.getWinCount();
                    int lose = d.getLoseCount();
                    tvDraw.setText(String.format(getString(R.string.xTimes), draw));
                    tvWin.setText(String.format(getString(R.string.xTimes), win));
                    tvLose.setText(String.format(getString(R.string.xTimes), lose));
                    tvIn.setText(Util.formatFloor(d.getIncome(), tp));
                    tvOut.setText(Util.formatFloor(d.getExpenditure(), tp));
                    tvAll.setText(Util.formatFloor(d.getTotal(), tp));
                    pie.setValues(draw, win, lose);

                    /*List<PieEntry> pie = pieSet.getValues();
                    pie.clear();
                    pie.add(new PieEntry(draw));
                    pie.add(new PieEntry(win));
                    pie.add(new PieEntry(lose));
                    pieSet.setValues(pie);
                    pc.setData(new PieData(pieSet));
                    pc.notifyDataSetChanged();*/
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            ana.getCode(), ana.getMsg()));
                    noLogin(ana.getCode());
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, String error) {
        switch (url) {
            case Constant.URL.GetDeliver:
            case Constant.URL.GetFlow:
                changeLoading(false, ContractRVAdapter.MORE);
                break;
        }
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    private void noLogin(long code) {
        if (Constant.Int.Please_Login == code) {
            clearLogin();
        }
    }

    private void changeLoading(boolean remove, String status) {
        if (conList.size() > 0) {
            int index = conList.size() - 1;
            if (conList.get(index).getType() == 0) {
                if (remove) {
                    conList.remove(index);
                } else {
                    conList.get(index).setStatus(status);
                    recyclerAdapter.notifyItemChange2(index);
                }
            }
        }
    }

    private void checkEmpty() {
        headEmpty.setVisibility(conList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRingFinish() {
        handler.postDelayed(() -> {
            TabLayout.Tab tab = tlContract.getTabAt(index_contract);
            if (tab != null) {
                tab.select();
            }
        }, 3000);
    }


    class KlineListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            index_kline = tab.getPosition();
            getCoin();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        showLoading();
        index_contract = tab.getPosition();
        initUI();
        loginAccount(Constant.Code.Login_Account);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    private void initUI() {
        conList.clear();
        recyclerAdapter.notifyDataSetChanged();
        headLogout.setVisibility(View.GONE);
        headTrade.setVisibility(View.GONE);
        headDeliver.setVisibility(View.GONE);
        headEntrust.setVisibility(View.GONE);
        headFlow.setVisibility(View.GONE);
        headAnalysis.setVisibility(View.GONE);
        headHold.setVisibility(View.GONE);
        headEmpty.setVisibility(View.GONE);

        if (index_contract == 0) {
            headTrade.setVisibility(View.VISIBLE);
            return;
        }
        if (!isLogin()) {
            headLogout.setVisibility(View.VISIBLE);
            return;
        }
        switch (index_contract) {
            case 1:
            case 2:
                headDeliver.setVisibility(View.VISIBLE);
                headHold.setVisibility(View.VISIBLE);
                break;
            case 3:
            case 4:
                headEntrust.setVisibility(View.VISIBLE);
                headHold.setVisibility(View.VISIBLE);
                break;
            case 5:
                headFlow.setVisibility(View.VISIBLE);
                headHold.setVisibility(View.VISIBLE);
                break;
            case 6:
                headAnalysis.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable text) {
        if (text.length() > 0) {
            if (text.toString().contains(".")) {
                int dotIndex = text.toString().indexOf(".");
                int dotLastIndex = text.toString().lastIndexOf(".");
                int maxIndex = dotIndex + tp + 1;
                if (dotIndex != dotLastIndex) {
                    etAmount.setText(text.toString().substring(0,
                            maxIndex < dotLastIndex ? maxIndex : dotLastIndex));
                    etAmount.setSelection(etAmount.length());
                } else {
                    if (text.length() > maxIndex) {
                        etAmount.setText(text.toString().substring(0, maxIndex));
                        etAmount.setSelection(etAmount.length());
                    }
                }
            }
            double a = Util.parseDouble(etAmount.getText().toString());
            if (fromUser) {
                updatePercent(a);
            }
            if (sym != null) {
                tvExpect.setText(Util.formatDecimal(a * sym.getRate(), tp));
            }
        } else {
            clearPercent();
            if (tvExpect.length() > 0) {
                tvExpect.setText(null);
            }
        }
    }

    private void updatePercent(double amount) {
        clearPercent();
        if (avail > 0) {
            if (isEqual(amount, avail * 0.25)) {
                tvsPercent.get(0).setSelected(true);
            } else if (isEqual(amount, avail * 0.5)) {
                tvsPercent.get(1).setSelected(true);
            } else if (isEqual(amount, avail * 0.75)) {
                tvsPercent.get(2).setSelected(true);
            } else if (isEqual(amount, avail)) {
                tvsPercent.get(3).setSelected(true);
            }
        }
    }

    private boolean isEqual(double amount, double target) {
        return Math.abs(amount - target) <= Math.pow(10, -tp);
    }

    private void clearPercent() {
        for (int i = 0; i < tvsPercent.size(); i++) {
            if (tvsPercent.get(i).isSelected()) {
                tvsPercent.get(i).setSelected(false);
                break;
            }
        }
    }

    @Override
    protected void showLoading() {
        dismissLoading();
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(this);
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void edgeLoad(float x, boolean left) {
        if (xValues == null) {
            return;
        }
        if (!SocketUtil.isConnect(client)) {
            socket();
            return;
        }
        int i = (int) x;
        if (x < 0) {
            i = 0;
        }
        if (!left && !xValues.containsKey(i) && xValues.containsKey(i - 1)) {
            i -= 1;
        }
        Long time = xValues.get(i);
        if (time != null && time > 0) {
            int num = 100;
            if (!left) {//向右获取数据时判断时间间隔
                long interval = INTERVAL[index_kline] * Util.MIN_1;
                long inter = System.currentTimeMillis() - time;
                if (inter < interval) {//不会有新数据
                    return;
                } else {
                    num = (int) (inter / interval) + 1;
                }
            }
            getBar(left ? time : -1, num, !left);
        }
    }

    /**
     * 图表选中
     */
    @Override
    public void valueSelected(Entry e) {
        float x = e.getX();
        if (index_kline != 0) {
            Entry lp5 = linePrice5.getEntryForXValue(x, 0);
            if (lp5 != null && x >= 5) {
                tvMa5p.setVisibility(View.VISIBLE);
                tvMa5p.setText("MA5: " + AppUtil.roundRemoveZero(lp5.getY(), tp));
            } else {
                tvMa5p.setVisibility(View.GONE);
            }
            Entry lp10 = linePrice10.getEntryForXValue(x, 0);
            if (lp10 != null && x >= 10) {
                tvMa10p.setVisibility(View.VISIBLE);
                tvMa10p.setText("MA10: " + AppUtil.roundRemoveZero(lp10.getY(), tp));
            } else {
                tvMa10p.setVisibility(View.GONE);
            }
            Entry lp20 = linePrice20.getEntryForXValue(x, 0);
            if (lp20 != null && x >= 20) {
                tvMa20p.setVisibility(View.VISIBLE);
                tvMa20p.setText("MA20: " + AppUtil.roundRemoveZero(lp20.getY(), tp));
            } else {
                tvMa20p.setVisibility(View.GONE);
            }
        }

        Entry lv5 = lineVol5.getEntryForXValue(x, 0);
        if (lv5 != null && x >= 5) {
            tvMa5v.setVisibility(View.VISIBLE);
            tvMa5v.setText("MA5: " + AppUtil.roundRemoveZero(lv5.getY(), tp));
        } else {
            tvMa5v.setVisibility(View.GONE);
        }
        Entry lv10 = lineVol10.getEntryForXValue(x, 0);
        if (lv10 != null && x >= 10) {
            tvMa10v.setVisibility(View.VISIBLE);
            tvMa10v.setText("MA10: " + AppUtil.roundRemoveZero(lv10.getY(), tp));
        } else {
            tvMa10v.setVisibility(View.GONE);
        }
        Entry lv20 = lineVol20.getEntryForXValue(x, 0);
        if (lv20 != null && x >= 20) {
            tvMa20v.setVisibility(View.VISIBLE);
            tvMa20v.setText("MA20: " + AppUtil.roundRemoveZero(lv20.getY(), tp));
        } else {
            tvMa20v.setVisibility(View.GONE);
        }
    }

    @Override
    public void nothingSelected() {
        tvMa5p.setVisibility(View.GONE);
        tvMa10p.setVisibility(View.GONE);
        tvMa20p.setVisibility(View.GONE);
        tvMa5v.setVisibility(View.GONE);
        tvMa10v.setVisibility(View.GONE);
        tvMa20v.setVisibility(View.GONE);
    }

    @Override
    public void enableHighlight() {
        if (!barSet.isHighlightEnabled()) {
            candleSet.setHighlightEnabled(true);
            lineMin.setHighlightEnabled(true);
            barSet.setHighlightEnabled(true);
        }
    }

    @Override
    public void disableHighlight() {
        if (barSet.isHighlightEnabled()) {
            candleSet.setHighlightEnabled(false);
            lineMin.setHighlightEnabled(false);
            barSet.setHighlightEnabled(false);
            if (cpGesture != null) {
                cpGesture.setHighlight(true);
            }
            if (cvGesture != null) {
                cvGesture.setHighlight(true);
            }
        }
    }

    @OnClick({R.id.iv_conBack, R.id.tv_conQuestion})
    public void back(View v) {
        switch (v.getId()) {
            case R.id.iv_conBack:
                onBackPressed();
                break;
            case R.id.tv_conQuestion:
                startActivity(new Intent(this, HelpSearchActivity.class)
                        .putExtra(Constant.Strings.Intent_Key, getString(R.string.contractHelpTitle)));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_logoutConGo:
                isLoginAndJump();
                break;
            case R.id.tv_tradeConMinus://数量减
                AppUtil.addOrSub(etAmount, false, 0);
                break;
            case R.id.tv_tradeConPlus://数量加
                AppUtil.addOrSub(etAmount, true, 0);
                break;
            case R.id.tv_tradeConPercent25:
                clearPercent();
                tvsPercent.get(0).setSelected(true);
                updateAmount(0.25);
                break;
            case R.id.tv_tradeConPercent50:
                clearPercent();
                tvsPercent.get(1).setSelected(true);
                updateAmount(0.5);
                break;
            case R.id.tv_tradeConPercent75:
                clearPercent();
                tvsPercent.get(2).setSelected(true);
                updateAmount(0.75);
                break;
            case R.id.tv_tradeConPercent100:
                clearPercent();
                tvsPercent.get(3).setSelected(true);
                updateAmount(1);
                break;
            case R.id.tv_tradeConRise://看涨
                contract("R");
                break;
            case R.id.tv_tradeConFall://看跌
                contract("F");
                break;
            case R.id.tv_anaConDay://一天
                getAna(0);
                break;
            case R.id.tv_anaConWeek://一周
                getAna(1);
                break;
            case R.id.tv_anaConMonth://一个月
                getAna(2);
                break;
            case R.id.tv_anaConAll://全部
                getAna(3);
                break;
        }
    }

    private void updateAmount(double percent) {
        if (avail > 0) {
            fromUser = false;
            etAmount.setText(Util.formatFloor(avail * percent, tp));
            fromUser = true;
        }
    }

    private void contract(String action) {
        if (isLoginAndJump()) {
            if (etAmount.length() == 0 || Util.parseDouble(etAmount.getText().toString()) <= 0) {
                ToastUtil.initToast(this, R.string.inputAmo);
                etAmount.requestFocus();
            } else {
                Util.hideKeyboard(etAmount);
                double a = Util.parseDouble(etAmount.getText().toString());
                if (a > avail) {
                    ToastUtil.initToast(this, R.string.availLess);
                    etAmount.requestFocus();
                } else {
                    etAmount.clearFocus();
                    placeOrder(action, AppUtil.floorRemoveZero(a, tp));
                }
            }
        }
    }

    private void getAna(int index) {
        for (int i = 0; i < tvsType.size(); i++) {
            if (tvsType.get(i).isSelected()) {
                tvsType.get(i).setSelected(false);
                break;
            }
        }
        showLoading();
        tvsType.get(index).setSelected(true);
        getAnalysis();
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.tv_conItemMore:
                if (ContractRVAdapter.MORE.equals(conList.get(position).getStatus())) {
                    conList.get(position).setStatus(ContractRVAdapter.LOADING);
                    recyclerAdapter.notifyItemChange2(position);

                    if (index_contract == 1) {
                        page++;
                        getDeliverData(true);
                    } else if (index_contract == 2) {
                        page++;
                        getDeliverData(false);
                    } else if (index_contract == 3) {
                        page++;
                        getOrder(true);
                    } else if (index_contract == 4) {
                        page++;
                        getOrder(false);
                    } else if (index_contract == 5) {
                        page++;
                        getFlowData();
                    } else if (index_contract == 6) {
                        getAnalysis();
                    }
                }
                break;
        }
    }

    @Override
    protected void restart() {
        if (canLoad()) {
            initUI();
            getDataWithoutBar();
        }
        float rightX = cp.getHighestVisibleX();
        if (rightX == cp.getXChartMax()) {//停留在最右端
            edgeLoad(rightX, false);
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
                initUI();
                login();
            }
        }
        return true;
    }

    @Override
    protected void onStop() {
        stopUpdate();
        SocketUtil.close(client);
        if (rpb != null) {
            rpb.destroy();
        }
        super.onStop();
    }

    @Override
    public void finish() {
        stopUpdate();
        SocketUtil.close(client);
        if (rpb != null) {
            rpb.destroy();
        }
        super.finish();
    }
}
