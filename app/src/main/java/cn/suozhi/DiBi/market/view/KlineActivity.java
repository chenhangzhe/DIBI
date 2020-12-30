package cn.suozhi.DiBi.market.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.MainActivity;
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
import cn.suozhi.DiBi.common.custom.EntrustView;
import cn.suozhi.DiBi.common.custom.RecyclerInScroll;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.SocketUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.market.adapter.KlineFilterRVAdapter;
import cn.suozhi.DiBi.market.adapter.PopupTypeRVAdapter;
import cn.suozhi.DiBi.market.adapter.PortDealRVAdapter;
import cn.suozhi.DiBi.market.model.Bar;

/**
 * K线
 */
public class KlineActivity extends BaseActivity implements View.OnClickListener, AbsRecyclerAdapter.OnItemClickListener,
        TabLayout.OnTabSelectedListener, TextView.OnEditorActionListener, CoupleChartGestureListener.OnEdgeListener,
        CoupleChartValueSelectedListener.ValueSelectedListener, ChartFingerTouchListener.HighlightListener , OkHttpUtil.OnDataListener {

    private RelativeLayout rl;
    private TextView tvSymbol, tvPrice, tvPriceCny;
    private ImageView ivFavor, ivMore;
    private TextView tvRate, tvHigh, tvLow, tvVol, tvMore;
    private LinearLayout llPort;
    private TabLayout tlIndicator;

    private CombinedChart cp, cv;
    private TextView tvMa5p, tvMa5v, tvMa10p, tvMa10v, tvMa20p, tvMa20v;
    private TabLayout tlOrder;
    private TabLayout tlLand;

    private ConstraintLayout cl0, cl1;
    private EntrustView evBuy, evSell;
    private TextView tv1p, tv1a;
    private RecyclerInScroll recycler;
    private PortDealRVAdapter adapterDeal;
    private List<QuoteEntity> dealList;

    private PopupWindow popup;
    private PopupTypeRVAdapter adapterMore;
    private List<String> moreList;

    private ConstraintLayout clPop;//切换交易对
    private View vBack;
    private TabLayout tlPop;
    private EditText etPop;
    private RecyclerView rvPop;
    private boolean isAnim;//是否正在执行动画
    private KlineFilterRVAdapter adapterPop;
    private List<QuoteEntity> popList;
    private List<QuoteEntity> filterList;

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
    private int range = 52;// 一屏显示Candle个数
    private float highVisX;//切屏时X轴的最大值

    private String token, symbol, sym_old;
    private long loginTime;
    private int socketType = -1;//获取币种状态 0 - 建立链接中 、 1 - 获取中 、 2 - 获取失败 、 3 - 获取后本地未登录
    // 4 - 获取后正在登录 、 5 - 获取后登录失败 、 6 - 获取后登录成功
    private WebSocketClient client;

    private Map<String, Symbol> mapSymbol = new HashMap<>();//所有交易对Symbol
    private int pp = 4, tp = 4;

    private String[] TAB_MORE, TAB_ORDER, TAB_POP, TAB_LAND;
    private String[] KLINE = {"MN1", "MN1", "MN5", "MN15", "MN30", "H1", "H4", "D1", "W1", "M1"};
    private int index_port = 0, index_more = 0, index_order = 0, index_pop = 0, index_land = 0;
    private long[] INTERVAL = {1, 1, 5, 15, 30, 60, 240, 1440, 10080, 43200};//单位: Min

    private LoadingDialog loadingDialog;
    private Handler handler = new Handler();
    public static SimpleDateFormat sdf_Mm = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    private String coinIntroCode;

    @Override
    protected int getViewResId() {
        openRestartInResume();
        return R.layout.activity_kline;
    }

    @Override
    protected void init() {
        lang = SharedUtil.getLanguage4Socket(this);
        symbol = getIntent().getStringExtra("symbol");

        sym_old = symbol;
        setNavigationColor(R.color.bk1A);

        TAB_MORE = new String[]{getString(R.string.min1), getString(R.string.min5), getString(R.string.min30),
                getString(R.string.hour4), getString(R.string.week1), getString(R.string.month1)};
        TAB_ORDER = new String[]{getString(R.string.entrustOrder), getString(R.string.newDeal),getString(R.string.coinIntro)};
        TAB_LAND = new String[]{getString(R.string.divideMin), getString(R.string.min1),
                getString(R.string.min5), getString(R.string.min15), getString(R.string.min30),
                getString(R.string.hour1), getString(R.string.hour4), getString(R.string.day1),
                getString(R.string.week1), getString(R.string.month1)};

        initView();
        showLoading();
        socket();
    }

    private void initView() {
        tvSymbol = findViewById(R.id.tv_klSymbol);
        tvPrice = findViewById(R.id.tv_klPrice);
        tvPriceCny = findViewById(R.id.tv_klPriceCny);
        tvVol = findViewById(R.id.tv_klVol);
        tvHigh = findViewById(R.id.tv_klHigh);
        tvLow = findViewById(R.id.tv_klLow);
        findViewById(R.id.iv_klOrientation).setOnClickListener(this);

        cp = findViewById(R.id.cc_klPrice);
        cv = findViewById(R.id.cc_klVol);
        tvMa5p = findViewById(R.id.tv_klMa5p);
        tvMa10p = findViewById(R.id.tv_klMa10p);
        tvMa20p = findViewById(R.id.tv_klMa20p);
        tvMa5v = findViewById(R.id.tv_klMa5v);
        tvMa10v = findViewById(R.id.tv_klMa10v);
        tvMa20v = findViewById(R.id.tv_klMa20v);

        evBuy = findViewById(R.id.ev_klBuy);
        evSell = findViewById(R.id.ev_klSell);

        if (isPort()) {
            rl = findViewById(R.id.rl_kl);
            findViewById(R.id.iv_klBack).setOnClickListener(this);
            findViewById(R.id.iv_klSymbol).setOnClickListener(this);
            tvSymbol.setOnClickListener(this);
            ivFavor = findViewById(R.id.iv_klFavor);
            ivFavor.setOnClickListener(this);
            tvRate = findViewById(R.id.tv_klRate);
            llPort = findViewById(R.id.ll_klPort);
            tvMore = findViewById(R.id.tv_klPort4);
            ivMore = findViewById(R.id.iv_klTriangle4);
            tlIndicator = findViewById(R.id.tl_klIndicator);
            tlOrder = findViewById(R.id.tl_klOrder);

            cl0 = findViewById(R.id.cl_klOrder0);
            cl1 = findViewById(R.id.cl_klOrder1);
            tv1p = findViewById(R.id.tv_kl1Price);
            tv1a = findViewById(R.id.tv_kl1Amount);
            recycler = findViewById(R.id.rv_kl1Deal);
            recycler.setFocusable(false);
            findViewById(R.id.tv_klBuy).setOnClickListener(this);
            findViewById(R.id.tv_klSell).setOnClickListener(this);

            //K线间隔
            for (int i = 0; i < llPort.getChildCount(); i++) {
                llPort.getChildAt(i).setOnClickListener(this);
            }
            Util.tabInit(tlIndicator, llPort.getChildCount(), R.layout.item_indicator, index_port);
            selectPort(index_port);
            //委托订单
            Util.tabInit(tlOrder, TAB_ORDER, R.layout.tab_12_purple_77_gy_5a, R.id.tv_tab12Pg5Name,
                    0, index_order, R.id.v_tipIndicator, 0.3F);
            visibleOrder();
            tlOrder.addOnTabSelectedListener(new OrderListener());
            //最新成交
            recycler.setLayoutManager(new LinearLayoutManager(this));
            adapterDeal = new PortDealRVAdapter(this, Util.getPhoneHeight(this));
            recycler.setAdapter(adapterDeal.setEmptyView(R.layout.empty_tips));
        } else {
            tlLand = findViewById(R.id.tl_klLand);
            Util.tabInit(tlLand, TAB_LAND, R.layout.tab_13_purple_77_gy_5a, R.id.tv_tab13Pg5Name,
                    0, index_land, R.id.v_tipIndicator, 0.3F);
            tlLand.addOnTabSelectedListener(new LandListener());
        }

        initChart();
        updateSymbol();
    }

    private void selectPort(int index) {
        if (llPort == null || index < 0 || index >= llPort.getChildCount()) {
            return;
        }
        int size = llPort.getChildCount();
        for (int i = 0; i < size; i++) {
            if (llPort.getChildAt(i).isSelected()) {
                llPort.getChildAt(i).setSelected(false);
            }
        }
        llPort.getChildAt(index).setSelected(true);
        tlIndicator.getTabAt(index).select();
        if (index == size - 1) {
            tvMore.setText(TAB_MORE[index_more]);
            ivMore.setImageResource(R.drawable.sls_triangle_blue_up_down);
        } else {
            tvMore.setText(R.string.more);
            ivMore.setImageResource(R.drawable.sls_triangle_indigo_up_down);
        }
        ivMore.setSelected(false);
    }

    private void visibleOrder() {
        cl0.setVisibility(index_order == 0 ? View.VISIBLE : View.INVISIBLE);
        cl1.setVisibility(index_order == 0 ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initChart() {
        int gy5a = Util.getColor(this, R.color.gy5A);//文字
        int gy7a = Util.getColor(this, R.color.gy7A_t80);//网格线
        int green = Util.getColor(this, R.color.green0E);
        int red = Util.getColor(this, R.color.redF6);
        int yellow = Util.getColor(this,R.color.yellow);
        float sp8 = Util.sp2px(this, 8);
        int highColor = Util.getColor(this, R.color.white);
        float highWidth = 0.8F;
        //K线-价格
        cp.setNoDataTextColor(yellow);
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
        xp.setTextColor(yellow);//标签颜色
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
        ylp.setTextColor(yellow);
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
        yrp.setGridLineWidth(0.4F);
        yrp.setTextColor(gy5a);
        yrp.setTextSize(8);
        yrp.setLabelCount(5, true);
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

        cpGesture = new CoupleChartGestureListener(this, cp, cv) {//设置成全局变量，后续要用到
            @Override
            public void chartDoubleTapped(MotionEvent me) {
                doubleTapped();
            }
        };
        cp.setOnChartGestureListener(cpGesture);//设置手势联动监听
        cvGesture = new CoupleChartGestureListener(this, cv, cp) {
            @Override
            public void chartDoubleTapped(MotionEvent me) {
                doubleTapped();
            }
        };
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
            Messages.UserLoginRequest login = Messages.UserLoginRequest.newBuilder()
                    .setRequestId(Constant.Code.Login)
                    .setToken(token)
                    .build();
            Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                    .setUserLogin(login)
                    .setLang(lang)
                    .build();
            socketSend(msg.toByteArray());
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
        boolean watch_bar = index_land <= 1;
        getBar(-1, 100, watch_bar);
        getDataWithoutBar(!watch_bar);
    }

    private void getDataWithoutBar(boolean watch_bar) {
        getQuote();
        if (isPort()) {
            if (index_order == 1) {
                stopUpdate(false, watch_bar, true, false);
                getTrade();
            } else {
                stopUpdate(false, watch_bar, false, true);
                getDepth(20);
            }
        } else {
            stopUpdate(false, watch_bar, false, true);
            getDepth(6);
        }
    }

    /**
     * Socket返回
     */
    private void load(ByteBuffer bytes) throws Exception {
        Messages.ResponseMessage symbol = Messages.ResponseMessage.parseFrom(bytes.array());
//        Log.e(TAG, "Kline: " + symbol.getMsgCase().getNumber());
//        Symbol sym = AppUtil.getSymbolInMap(mapSymbol, getIntent().getStringExtra("symbol"));
//        if (sym != null) {
//            String p = sym.getPCoin();
//            getCoinIntro(p);
//        }
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
                if (Constant.Int.SUC_S.equals(login.getCode())) {
                    socketType = 6;
                } else {
                    socketType = 5;
                }
                getCoin();
                break;
            case 10://币种对最新行情
                Messages.QuoteResponse quote = symbol.getQuote();
                if (Constant.Int.SUC_S.equals(quote.getCode())) {
                    updateQuote(quote.getQuote());
                    if (isPort()) {
                        ivFavor.setSelected(quote.getWatched());
                    }
                }
                break;
            case 9://实时行情推送
                Messages.QuoteUpdateResponse quoteUpdate = symbol.getQuoteUpdate();
                updateQuote(quoteUpdate.getQuote());
                break;
            case 11://bar数据
                Messages.BarResponse bar = symbol.getBar();
                if (Constant.Int.SUC_S.equals(bar.getCode()) && KLINE[index_land].equals(bar.getType())) {
                    updateBar(bar.getBarsList());
                }
                break;
            case 12://bar更新
                Messages.BarUpdateResponse barUpdate = symbol.getBarUpdate();
                if (KLINE[index_land].equals(barUpdate.getType())) {
                    updateBar(barUpdate.getBar());
                }
                break;
            case 13://市场深度
                Messages.DepthResponse depth = symbol.getDepth();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(depth.getCode())) {
                    updateDepth(depth.getBidsList(), depth.getAsksList());
                } else {
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            depth.getCode(), depth.getMessage()));
                }
                break;
            case 14://市场深度推送
                Messages.DepthUpdateResponse depthUpdate = symbol.getDepthUpdate();
                updateDepth(depthUpdate.getBidsList(), depthUpdate.getAsksList());
                break;
            case 15://最近成交记录
                Messages.TradeResponse trade = symbol.getTrade();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(trade.getCode())) {
                    updateRecord(trade.getTradesList());
                } else {
                    updateRecord();
                }
                break;
            case 16://最近成交记录推送
                Messages.TradeUpdateResponse tradeUpdate = symbol.getTradeUpdate();
                updateRecord(tradeUpdate.getTradesList());
                break;
            case 6://编辑自选
                Messages.WatchListEditResponse watch = symbol.getWatchEdit();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(watch.getCode())) {
                    if (ivFavor != null) {
                        ivFavor.setSelected(!ivFavor.isSelected());
                    }
                } else {
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            watch.getCode(), watch.getMessage()));
                }
                break;
            case 5://自选
                Messages.WatchListResponse watchList = symbol.getWatchList();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(watchList.getCode())) {
                    updateDataPop(watchList.getRequestId(), watchList.getQuotesList());
                } else {
                    setDataPop(null);
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            watchList.getCode(), watchList.getMessage()));
                }
                break;
            case 4://币种列表
                Messages.SymbolListResponse symbolList = symbol.getSymbolList();
                dismissLoading();
                if (Constant.Int.SUC_S.equals(symbolList.getCode())) {
                    updateDataPop(symbolList.getRequestId(), symbolList.getQuotesList());
                } else {
                    setDataPop(null);
                    ToastUtil.initToast(this, Util.getCodeTextString(this,
                            symbolList.getCode(), symbolList.getMessage()));
                }
                break;
        }
    }

    /**
     * 保存交易对到map
     */
    private void initSymbol(List<Messages.DmCurrency> coinList, List<Messages.DmPair> pairList) {
        if (mapSymbol.size() > 0) {
            mapSymbol.clear();
        }
        if (pairList == null || pairList.size() == 0) {
            return;
        }
        for (int i = 0; i < pairList.size(); i++) {
            Messages.DmPair pair = pairList.get(i);
            String symbol = pair.getSymbol();
            int pid = pair.getBaseCurrencyId();
            int tid = pair.getQuoteCurrencyId();
            String p = findCoin(pid, coinList);
            String t = findCoin(tid, coinList);
            double maxR = pair.getMaxRise();
            double maxF = pair.getMaxFall();
            String currencyPairRegion = pair.getCurrencyPairRegion();
            String showS = pair.getShowSymbol();

//            mapSymbol.put(symbol, new Symbol(symbol, p, t, pair.getSizeDisplayDp(), pair.getPriceDisplayDp(), pid, tid));
            mapSymbol.put(symbol, new Symbol(symbol, p, t, pair.getSizeDisplayDp(), pair.getPriceDisplayDp(), pid, tid, maxR, maxF, currencyPairRegion, showS));
        }
        updateSymbol();
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
        Symbol sym = AppUtil.getSymbolInMap(mapSymbol, symbol);
        if (sym != null) {
            String p = sym.getPCoin(), t = sym.getTCoin(), r = sym.getCurrencyPairRegion(), lp = "", rt = "";

            if(r.equals("B")){
                lp = p.substring(0, p.indexOf("-"));
            } else {
                lp = p;
            }

            if(t.contains("-")){
                rt = t.substring(0, t.indexOf("-"));
            } else {
                rt = t;
            }

            pp = AppUtil.getTradePoint(false, t, sym.getPPoint());
            tp = AppUtil.getTradePoint(true, t, sym.getTPoint());
            evBuy.setPoint(tp, pp);
            evSell.setPoint(tp, pp);
            tvSymbol.setText(lp + "/" + rt);

            // 用于获取币种信息
            coinIntroCode = p;

            if (isPort()) {//竖屏才更新
                tv1p.setText(getString(R.string.price) + "(" + rt + ")");
                tv1a.setText(getString(R.string.amount) + "(" + lp + ")");
            }
        }
    }

    private void getQuote() {
        Messages.QuoteRequest quote = Messages.QuoteRequest.newBuilder()
                .setRequestId(Constant.Code.Kline_Quote)
                .setSymbol(symbol)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setQuote(quote)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getBar(long from, int number, boolean watch) {
        if (index_land < 0 || index_land >= KLINE.length) {
            return;
        }
        Messages.BarRequest.Builder bar = Messages.BarRequest.newBuilder()
                .setRequestId(Constant.Code.Kline_Bar)
                .setSymbol(symbol)
                .setType(KLINE[index_land])
                .setNumber(number)
                .setWatch(watch)
                .setNoFill(false);
        if (from > 0) {
            bar.setFrom(from);
        }
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setBar(bar)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getDepth(int count) {
        Messages.DepthRequest depth = Messages.DepthRequest.newBuilder()
                .setRequestId(Constant.Code.Kline_Depth)
                .setSymbol(symbol)
                .setLimit(count)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setDepth(depth)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getTrade() {
        Messages.TradeRequest trade = Messages.TradeRequest.newBuilder()
                .setRequestId(Constant.Code.Kline_New_Deal)
                .setSymbol(symbol)
                .setLimit(20)
                .setWatch(true)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setTrade(trade)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void stopUpdate(boolean quote, boolean bar, boolean depth, boolean trade) {
        Messages.StopUpdateRequest stop = Messages.StopUpdateRequest.newBuilder()
                .setRequestId(Constant.Code.Stop)
                .setQuote(quote)
                .setBar(bar)
                .setDepth(depth)
                .setTrade(trade)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setStopUpdate(stop)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void favorite(boolean b) {
        Messages.WatchListEditRequest watch = Messages.WatchListEditRequest.newBuilder()
                .setRequestId(Constant.Code.Watch_Edit)
                .setAdd(b)
                .addSymbols(symbol)
                .setWatch(false)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setWatchEdit(watch)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getFavor() {
        Messages.WatchListRequest watchList = Messages.WatchListRequest.newBuilder()
                .setRequestId(Constant.Code.Watch_Pop)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setWatchList(watchList)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getSymbolBackUp(long id, String region) {
        Messages.SymbolListRequest symbolList = Messages.SymbolListRequest.newBuilder()
                .setRequestId(id)
                .setRegion(region)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setSymbolList(symbolList)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }

    private void getSymbol(long id, String region, boolean watch, View v, String zone, String orderBy, String order) {
        if (v != null) {
            showLoading();
        }
        Messages.SymbolListRequest symbolList = Messages.SymbolListRequest.newBuilder()
                .setRequestId(id)
                .setRegion(region)
                .setWatch(watch)
                .setCurrencyRegion(TextUtils.isEmpty(zone) ? "" : zone)
                .setOrderBy(TextUtils.isEmpty(orderBy) ? "" : orderBy)
                .setOrder(TextUtils.isEmpty(order) ? "" : order)
                .build();
        Messages.RequestMessage msg = Messages.RequestMessage.newBuilder()
                .setSymbolList(symbolList)
                .setLang(lang)
                .build();
        socketSend(msg.toByteArray());
    }


    public void updateQuote(Messages.DmQuote q) {
        tvPrice.setText(Util.formatDecimal(q.getPrice(), tp));
        double rate = AppUtil.getQuoteRate(q);
        boolean positive = rate >= 0;
        tvPrice.setSelected(positive);
        tvPriceCny.setText(AppUtil.approximateCny(q.getCloseCny()));
        tvVol.setText(AppUtil.roundRemoveZero(q.getVolume(), pp));
        String high = AppUtil.roundRemoveZero(q.getHigh(), tp);
        String low = AppUtil.roundRemoveZero(q.getLow(), tp);
        String larger = Util.getLargerString(high, low);
        tvHigh.setHint(larger);
        tvHigh.setText(high);
        tvLow.setHint(larger);
        tvLow.setText(low);

        if (isPort()) {
            tvRate.setText((positive ? "+" : "") + Util.Format_Percent.format(rate));
            tvRate.setSelected(positive);
        }
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

    private void updateBar(List<Messages.DmBar> barList) throws Exception {
        if (barList == null) {
            return;
        }
        /*File file = new File(CacheUtil.TEXT_CACHE_DIR, KLINE[index_land]+".txt");
        for (int i = 0; i < barList.size(); i++) {
            Messages.DmBar b = barList.get(i);
            FileUtil.writeTextFile(file, sdf_Mm.format(b.getTime()) + "\n" + b.toString() + "\n", i > 0);
        }*/
        List<Bar> list = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            Messages.DmBar b = barList.get(i);
            list.add(new Bar(b.getTime(), b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume()));
        }
        Collections.sort(list);
        handleData(list, xValues.size());
    }

    private void updateBar(Messages.DmBar b) {
        List<Bar> list = new ArrayList<>();
        list.add(new Bar(b.getTime(), b.getOpen(), b.getHigh(), b.getLow(), b.getClose(), b.getVolume()));
        handleData(list, xValues.size());
    }

    private void handleData(List<Bar> list, int size) {
        boolean right;
        if (dataList.size() == 0) {
            right = true;
            highVisX = 0;
            dataList.addAll(list);
        } else if (list.get(list.size() - 1).getTime() <= dataList.get(0).getTime()) {
            right = false;
            dataList.addAll(0, list);//添加到左侧
        } else {
            right = true;
            highVisX = cp.getHighestVisibleX();
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
            if (index_port == 0) {
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

    public void updateDepth(List<Messages.DmDepthItem> bids, List<Messages.DmDepthItem> asks) {
        if (isPort() && index_order != 0) {
            return;
        }
        int lb = bids == null ? 0 : bids.size();
        List<QuoteEntity> bl = new ArrayList<>();
        double bm = 0;
        for (int i = 0; i < lb; i++) {
            double size = bids.get(i).getSize();
            bl.add(new QuoteEntity(bids.get(i).getPrice(), size));
            if (size > bm) {
                bm = size;
            }
        }
        int la = asks == null ? 0 : asks.size();
        List<QuoteEntity> sl = new ArrayList<>();
        double sm = 0;
        for (int i = 0; i < la; i++) {
            double size = asks.get(i).getSize();
            sl.add(new QuoteEntity(asks.get(i).getPrice(), size));
            if (size > sm) {
                sm = size;
            }
        }
        double max = Math.max(bm, sm);
        evBuy.setData(bl, max);
        evSell.setData(sl, max);
    }

    private void initDeal() {
        if (dealList == null) {
            dealList = new ArrayList<>();
        } else {
            dealList.clear();
        }
    }

    public void updateRecord(List<Messages.DmTrade> list) {
        initDeal();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                dealList.add(AppUtil.getDeal(list.get(i), pp, tp));
            }
        }
        adapterDeal.setData(dealList);
    }

    public void updateRecord() {
        initDeal();
        adapterDeal.setData(dealList);
    }

    private void loadOrder() {
        showLoading();
        visibleOrder();
        if (index_order == 0) {
            stopUpdate(false, false, false, true);
            getDepth(20);
        } else if (index_order == 1) {
            stopUpdate(false, false, true, false);
            getTrade();
        } else if (index_order == 2){
            stopUpdate(false, false, false, false);
            // 点击简介从底部弹出币种信息
            Intent intentIntro = new Intent(KlineActivity.this, CoinIntroActivity.class);
            intentIntro.putExtra("coinCode", coinIntroCode);
            startActivity(intentIntro);
            //此处弹框
            dismissLoading();
        }
    }

    class OrderListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            index_order = tab.getPosition();
            loadOrder();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }
    }

    class LandListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            index_land = tab.getPosition();
            initIndexAndLoad();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }
    }

    private void initIndexAndLoad() {
        switch (index_land) {
            case 0:
                index_port = 0;
                index_more = 0;
                break;
            case 1://1min
                index_port = 4;
                index_more = 0;
                break;
            case 2:
                index_port = 4;
                index_more = 1;
                break;
            case 3:
                index_port = 1;
                index_more = 0;
                break;
            case 4://30min
                index_port = 4;
                index_more = 2;
                break;
            case 5://1h
                index_port = 2;
                index_more = 0;
                break;
            case 6:
                index_port = 4;
                index_more = 3;
                break;
            case 7://1d
                index_port = 3;
                index_more = 0;
                break;
            case 8:
                index_port = 4;
                index_more = 4;
                break;
            case 9://1mon
                index_port = 4;
                index_more = 5;
                break;
        }
        getCoin();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_klBack:
                onBackPressed();
                break;
            case R.id.iv_klSymbol:
            case R.id.tv_klSymbol://切换交易对
                if (socketType < 3) {
                    break;
                }
                if (doNext()) {
                    if (clPop == null) {
                        initPop();
                    }
                    if (rl.getChildCount() != 2) {
                        removeChildInParent(clPop);
                        rl.addView(clPop);
                    }
                    if (index_pop < 1 || index_pop >= TAB_POP.length) {
                        index_pop = getIndex();
                    }
                    showPopup();
                }
                break;
            case R.id.iv_klFavor://编辑自选
                if (isLoginAndJump()) {
                    showLoading();
                    favorite(!v.isSelected());
                }
                break;
            case R.id.tv_klPort0:
                index_port = 0;
                index_land = 0;
                selectPortAndLoad();
                break;
            case R.id.tv_klPort1:
                index_port = 1;
                index_land = 3;
                selectPortAndLoad();
                break;
            case R.id.tv_klPort2:
                index_port = 2;
                index_land = 5;
                selectPortAndLoad();
                break;
            case R.id.tv_klPort3:
                index_port = 3;
                index_land = 7;
                selectPortAndLoad();
                break;
            case R.id.ll_klPort4:
                initMore();
                ivMore.setSelected(true);
                popup.showAsDropDown(llPort, 0, Util.dp2px(this, 2));
                break;
            case R.id.iv_klOrientation://横竖屏
                highVisX = cp.getHighestVisibleX();
                setRequestedOrientation(isPort() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.tv_klBuy:
                goMain(2, true);
                break;
            case R.id.tv_klSell:
                goMain(2, false);
                break;
            case R.id.v_popFk://Popup空白处
                dismissPopup();
                break;
        }
    }

    private void selectPortAndLoad() {
        selectPort(index_port);
        getCoin();
    }

    private void initMore() {
        if (popup == null) {
            int width = Util.getPhoneWidth(this) / 6;
            RecyclerView recycler = new RecyclerView(this);
            recycler.setBackgroundResource(R.color.bk1A);
            recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            adapterMore = new PopupTypeRVAdapter(this, width);
            recycler.setAdapter(adapterMore.setOnItemClickListener(this));

            popup = new PopupWindow(recycler, ViewGroup.LayoutParams.MATCH_PARENT,
                    Util.dp2px(this, 36), true);
            popup.setOnDismissListener(() -> ivMore.setSelected(false));
            popup.setOutsideTouchable(true);
        }

        if (moreList == null) {
            moreList = new ArrayList<>();
        } else {
            moreList.clear();
        }
        moreList.addAll(Arrays.asList(TAB_MORE));
        adapterMore.setData(moreList);
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.tv_popupType:
                popup.dismiss();
                index_port = llPort.getChildCount() - 1;
                index_more = position;
                switch (index_more) {
                    case 0:
                        index_land = 1;
                        break;
                    case 1:
                        index_land = 2;
                        break;
                    case 2:
                        index_land = 4;
                        break;
                    case 3:
                        index_land = 6;
                        break;
                    case 4:
                        index_land = 8;
                        break;
                    case 5:
                        index_land = 9;
                        break;
                }
                selectPortAndLoad();
                break;
            case R.id.cl_kfItem://Popup
                if (filterList != null) {
                    symbol = filterList.get(position).getSymbol();
                    dismissPopup();
                    showLoading();
                    updateSymbol();
                    getCoin();
                }
                break;
        }
    }

    private void goMain(int page, boolean isBuy) {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("page", page)
                .putExtra("symbol", symbol)
                .putExtra("isBuy", isBuy));
        finish();
    }

    private void initPop() {
        TAB_POP = new String[]{getString(R.string.favorite), "DIC" , "USDT", "BTC", "ETH" , getString(R.string.zoneSuperMine)};
        clPop = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.popup_filter_kline, rl, false);
        rl.addView(clPop);

        vBack = clPop.findViewById(R.id.v_popFk);
        tlPop = clPop.findViewById(R.id.tl_popFkType);
        etPop = clPop.findViewById(R.id.et_popFkSearch);
        rvPop = clPop.findViewById(R.id.rv_popFkSymbol);
        vBack.setOnClickListener(this);

        Util.tabInit(tlPop, TAB_POP, R.layout.tab_15_purple_77_gy_96, R.id.tv_tab15Pg9Name, 0,
                index_pop, R.id.v_tipIndicator, 0.2F);
        tlPop.addOnTabSelectedListener(this);

        etPop.setOnEditorActionListener(this);
        rvPop.setLayoutManager(new LinearLayoutManager(this));
        adapterPop = new KlineFilterRVAdapter(this);
        rvPop.setAdapter(adapterPop.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
    }

    private int getIndex() {
        int index = 1;
        try {
            Symbol sym = AppUtil.getSymbolInMap(mapSymbol, symbol);
            if (sym == null) {
                return index;
            }
            String t = sym.getTCoin();
            for (int i = 1; i < TAB_POP.length; i++) {
                if (TAB_POP[i].equals(t)) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {}
        return index;
    }

    private void showPopup() {
        setSoft(false);
        Animation in = AnimationUtils.loadAnimation(this, R.anim.translate_in_bottom);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnim = true;
                vBack.setSelected(false);
                clPop.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnim = false;
                setStatusColor(R.color.bk0D);
                vBack.setSelected(true);
                etPop.setText(null);
                tlPop.getTabAt(index_pop).select();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        clPop.startAnimation(in);
    }

    private void dismissPopup() {
        Util.hideKeyboard(etPop);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.translate_out_bottom);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnim = true;
                setStatusColor(R.color.bk1A);
                vBack.setSelected(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnim = false;
                clPop.setVisibility(View.GONE);
                setSoft(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        clPop.startAnimation(out);
    }

    private boolean isPopShow() {
        if (clPop != null && clPop.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    private boolean doNext() {
        if (isAnim) {
            return false;
        }
        if (isPopShow()) {
            dismissPopup();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        index_pop = tab.getPosition();
        Util.hideKeyboard(etPop);
        etPop.clearFocus();
        loadPopup();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    private void loadPopup() {
        if (index_pop < 0 || index_pop >= TAB_POP.length) {
            return;
        }
        if (index_pop == 0) {
            showLoading();
            getFavor();
            return;
        }
        long id;
        String region,popzone;
        switch (index_pop) {
            case 2:
                id = Constant.Code.Pop_USDT;
                region = "USDT";
                popzone = null;
                break;
            case 3:
                id = Constant.Code.Pop_BTC;
                region = "BTC";
                popzone = null;
                break;
            case 4:
                id = Constant.Code.Pop_ETH;
                region = "ETH";
                popzone = null;
                break;
            case 5:
                id = Constant.Code.Pop_USDTE;
                region = "USDT";
                popzone = "B";
                break;
            case 1:
            default:
                id = Constant.Code.Pop_DIC;
                region = "DIC";
                popzone = null;
                break;
        }
        showLoading();
//        getSymbol(id, region);
        getSymbol(id, region, false, tlPop, popzone, null, null);
    }

    private void updateDataPop(long id, List<Messages.DmQuote> list) {
        if (id == Constant.Code.Watch_Pop) {
            if (index_pop == 0) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_DIC) {
            if (index_pop == 1) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_USDT) {
            if (index_pop == 2) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_BTC) {
            if (index_pop == 3) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_ETH) {
            if (index_pop == 4) {
                setDataPop(list);
            }
        } else if (id == Constant.Code.Pop_USDTE) {
            if (index_pop == 5) {
                setDataPop(list);
            }
        }
    }

    private void setDataPop(List<Messages.DmQuote> list) {
        if (popList == null) {
            popList = new ArrayList<>();
        } else {
            popList.clear();
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                popList.add(AppUtil.getQuote(list.get(i), mapSymbol));
            }
        }
        filter();
        adapterPop.setData(filterList);
    }

    private void filter() {
        if (filterList == null) {
            filterList = new ArrayList<>();
        } else {
            filterList.clear();
        }
        if (popList == null) {
            return;
        }
        String key = Util.editRemoveIllegal(etPop);
        if (TextUtils.isEmpty(key)) {
            filterList.addAll(popList);
        } else {
            String K = key.toUpperCase();
            for (int i = 0; i < popList.size(); i++) {
                if (popList.get(i).getSymbol().contains(K)) {
                    filterList.add(popList.get(i));
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (popList == null) {
                return false;
            }
            Util.hideKeyboard(etPop);
            etPop.clearFocus();
            handler.postDelayed(() -> {//使收起键盘时不闪烁
                filter();
                adapterPop.setData(filterList);
            }, 50);
            return true;
        }
        return false;
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
                long interval = INTERVAL[index_land] * Util.MIN_1;
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
     * 双击图表
     */
    private void doubleTapped() {
        if (isPort()) {
            highVisX = cp.getHighestVisibleX();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * 图表选中
     */
    @Override
    public void valueSelected(Entry e) {
        float x = e.getX();
        if (index_land != 0) {
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

    @Override
    public void onBackPressed() {
        if (doNext()) {
            if (isPort()) {
                if (!TextUtils.isEmpty(sym_old) && !sym_old.equals(symbol)) {
                    goMain(0, true);
                } else {
                    super.onBackPressed();
                }
            } else {
                highVisX = cp.getHighestVisibleX();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    protected void restart() {
        load();
        float rightX = cp.getHighestVisibleX();
        if (rightX == cp.getXChartMax()) {//停留在最右端
            edgeLoad(rightX, false);
        }
    }

    private void load() {
        if (canLoad()) {
            getDataWithoutBar(false);
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
                login();
            }
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isPort = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        if (isPort) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        SharedUtil.onLanguageChange(this);
        setContentView(getViewResId());
        initView();
        range = isPort ? 52 : 68;
        if (xValues == null) {
            xValues = new HashMap<>();
        }
        load();
        configData();
        if (xValues.size() > 0) {
            cp.post(() -> {
                float x = highVisX - range;
                cp.moveViewToX(x);
                cv.moveViewToX(x + barOffset);
                cp.notifyDataSetChanged();
                cv.notifyDataSetChanged();
            });
        }
    }

    private void setSoft(boolean resize) {
        int adjust = resize ? WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE :
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        getWindow().setSoftInputMode(adjust | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 从父View中移除自己
     */
    private void removeChildInParent(View child) {
        if (child == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) child.getParent();
        if (parent != null) {
            parent.removeView(child);
        }
    }

    /**
     * 当前是否是竖屏
     */
    public boolean isPort() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onStop() {
        stopUpdate(true, true, true, true);
        SocketUtil.close(client);
        super.onStop();
    }

    @Override
    public void finish() {
        stopUpdate(true, true, true, true);
        SocketUtil.close(client);
        super.finish();
    }


    public void onResponse(String url, String json, String session) {
        //
    }

    @Override
    public void onFailure(String url, String error) {
        //
    }

}
