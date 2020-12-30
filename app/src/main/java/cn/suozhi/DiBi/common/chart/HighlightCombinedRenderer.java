package cn.suozhi.DiBi.common.chart;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.renderer.BubbleChartRenderer;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 自定义CombinedChartRenderer 把Candle图、Line图 的渲染器替换成自定义渲染器
 */
public class HighlightCombinedRenderer extends CombinedChartRenderer {

    protected float barOffset;//BarChart绘制时偏移多少个单位 --小于0时向左偏移
    protected float highWidth, highSize;//高亮线宽度 单位:dp  /  高亮文字大小 单位:px

    private HighlightCandleRenderer.Extreme drawExtreme;
    private int extremeColor;
    private float extremeSize;
    private ChartListener chartListener;

    public HighlightCombinedRenderer(CombinedChart chart, ChartAnimator animator,
                                     ViewPortHandler viewPortHandler, float barOffset) {
        super(chart, animator, viewPortHandler);
        this.barOffset = barOffset;
    }

    public HighlightCombinedRenderer setHighWidthSize(float width, float textSize) {
        highWidth = width;
        highSize = textSize;
        return this;
    }

    public HighlightCombinedRenderer setExtremeColorSize(HighlightCandleRenderer.Extreme extreme, int color, float textSize) {
        drawExtreme = extreme;
        extremeColor = color;
        extremeSize = textSize;
        return this;
    }

    public HighlightCombinedRenderer setChartListener(ChartListener chartListener) {
        this.chartListener = chartListener;
        return this;
    }

    @Override
    public void createRenderers() {
        mRenderers.clear();
        CombinedChart chart = (CombinedChart)mChart.get();
        if (chart == null)
            return;
        DrawOrder[] orders = chart.getDrawOrder();
        for (DrawOrder order : orders) {
            switch (order) {
                case BAR:
                    if (chart.getBarData() != null)
                        mRenderers.add(new OffsetBarRenderer(chart, mAnimator, mViewPortHandler, barOffset)
                                .setHighWidthSize(highWidth, highSize)
                                .setChartListener(chartListener));
                    break;
                case BUBBLE:
                    if (chart.getBubbleData() != null)
                        mRenderers.add(new BubbleChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case LINE:
                    if (chart.getLineData() != null)
                        mRenderers.add(new HighlightLineRenderer(chart, mAnimator, mViewPortHandler)
                                .setHighSize(highSize)
                                .setChartListener(chartListener));
                    break;
                case CANDLE:
                    if (chart.getCandleData() != null)
                        mRenderers.add(new HighlightCandleRenderer(chart, mAnimator, mViewPortHandler)
                                .setHighSize(highSize)
                                .setExtremeColorSize(drawExtreme, extremeColor, extremeSize)
                                .setChartListener(chartListener));
                    break;
                case SCATTER:
                    if (chart.getScatterData() != null)
                        mRenderers.add(new ScatterChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
            }
        }
    }
}
