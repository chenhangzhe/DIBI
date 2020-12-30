package cn.suozhi.DiBi.common.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 自定义CandleStickChart渲染器 绘制高亮  -- 绘制方式和自定义LineChart渲染器相同
 * 使用方法: 1.先设置渲染器 {@link CombinedChart#setRenderer(DataRenderer)}
 *              传入自定义渲染器 将其中Candle图的渲染器替换成此渲染器
 *           2.设置数据时 调用 {@link CandleEntry#CandleEntry(float, float, float, float, float, Object)}
 *              传入String类型的data 以绘制x的值  -- 如未设置 则只绘制竖线
 */
public class HighlightCandleRenderer extends CandleStickChartRenderer {

    private float highSize;//图表高亮文字大小 单位:px

    private Highlight[] indices;
    private String colorRect = "#8AA0FF", colorText = "#323761";
    private DecimalFormat format = new DecimalFormat("0.0000");//保留小数点后四位

    private Extreme drawExtreme;
    private int extremeColor;
    private float extremeSize;
    private ChartListener chartListener;

    public enum Extreme {//极值绘制方式
        EX_NULL, EX_MAX_MIN, EX_MAX, EX_MIN
    }

    public HighlightCandleRenderer(CandleDataProvider chart, ChartAnimator animator,
                                   ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        drawExtreme = Extreme.EX_NULL;
    }

    public HighlightCandleRenderer setHighSize(float textSize) {
        highSize = textSize;
        return this;
    }

    public HighlightCandleRenderer setExtremeColorSize(Extreme extreme, int color, float textSize) {
        drawExtreme = extreme;
        extremeColor = color;
        extremeSize = textSize;
        return this;
    }

    public HighlightCandleRenderer setChartListener(ChartListener chartListener) {
        this.chartListener = chartListener;
        return this;
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        this.indices = indices;
    }

    protected float getYPixelForValues(float x, float y) {
        MPPointD pixels = mChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(x, y);
        return (float) pixels.y;
    }

    @Override
    public void drawValues(Canvas c) {
        super.drawValues(c);
        DecimalFormat ft = ChartUtil.getFormat(chartListener, format);
        drawExtreme(c, ft);
        drawHigh(c, ft);
    }

    /**
     * 绘制极值点
     */
    private void drawExtreme(Canvas c, DecimalFormat ft) {
        if (drawExtreme == Extreme.EX_NULL) {
            return;
        }
        CandleDataSet set = (CandleDataSet) mChart.getCandleData().getDataSetByIndex(0);
        if (set == null) {
            return;
        }
        List<CandleEntry> list = set.getValues();
        if (list == null || list.size() == 0) {
            return;
        }
        switch (drawExtreme) {
            case EX_MAX:
                drawMax(c, list, ft);
                break;
            case EX_MIN:
                drawMin(c, list, ft);
                break;
            case EX_MAX_MIN:
                drawMax(c, list, ft);
                drawMin(c, list, ft);
                break;
        }
    }

    private void drawMax(Canvas c, List<CandleEntry> list, DecimalFormat ft) {
        float[] max = getMaxPoint(list);
        MPPointD point = mChart.getTransformer(YAxis.AxisDependency.LEFT)
                .getPixelForValues(max[0], max[1]);
        float x = (float) point.x;
        float y = (float) point.y;

        mHighlightPaint.setTextSize(extremeSize);
        mHighlightPaint.setColor(extremeColor);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        float arrowLength = 15;
        String text = ft.format(max[1]);
        int width = Utils.calcTextWidth(mHighlightPaint, text);
        float left;//文字最左端
        float aw = arrowLength * 0.4F;//箭头拐角宽度
        if (x + (width + arrowLength * 1.4F) > mViewPortHandler.contentRight()) {//右侧位置不足
            left = x - width - arrowLength * 1.2F;
            c.drawLine(x, y, x - aw, y - aw, mHighlightPaint);
            c.drawLine(x, y, x - aw, y + aw, mHighlightPaint);
            c.drawLine(x - arrowLength, y, x, y, mHighlightPaint);
        } else {
            left = x + arrowLength * 1.2F;
            c.drawLine(x, y, x + aw, y - aw, mHighlightPaint);
            c.drawLine(x, y, x + aw, y + aw, mHighlightPaint);
            c.drawLine(x, y, x + arrowLength, y, mHighlightPaint);
        }

        //绘制文字
        Paint.FontMetrics metrics = mHighlightPaint.getFontMetrics();
        float baseY = y - (metrics.top + metrics.bottom) / 2;
        c.drawText(text, left, baseY, mHighlightPaint);
    }

    private float[] getMaxPoint(List<CandleEntry> list) {
        int min = Math.max(0, Math.round(mChart.getLowestVisibleX()));
        int max = Math.round(mChart.getHighestVisibleX());
        float[] point = {list.get(min).getX(), list.get(min).getHigh()};
        for (int i = min + 1; i <= Math.min(list.size() - 1, max); i++) {
            if (point[1] < list.get(i).getHigh()) {
                point[0] = list.get(i).getX();
                point[1] = list.get(i).getHigh();
            }
        }
        return point;
    }

    private void drawMin(Canvas c, List<CandleEntry> list, DecimalFormat ft) {
        float[] min = getMinPoint(list);
        MPPointD point = mChart.getTransformer(YAxis.AxisDependency.LEFT)
                .getPixelForValues(min[0], min[1]);
        float x = (float) point.x;
        float y = (float) point.y;

        mHighlightPaint.setTextSize(extremeSize);
        mHighlightPaint.setColor(extremeColor);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        float arrowLength = 15;
        String text = ft.format(min[1]);
        int width = Utils.calcTextWidth(mHighlightPaint, text);
        float left;//文字最左端
        float aw = arrowLength * 0.4F;//箭头拐角宽度
        if (x - (width + arrowLength * 1.4F) < mViewPortHandler.contentLeft()) {//左侧位置不足
            left = x + arrowLength * 1.2F;
            c.drawLine(x, y, x + aw, y - aw, mHighlightPaint);
            c.drawLine(x, y, x + aw, y + aw, mHighlightPaint);
            c.drawLine(x, y, x + arrowLength, y, mHighlightPaint);
        } else {
            left = x - width - arrowLength * 1.2F;
            c.drawLine(x, y, x - aw, y - aw, mHighlightPaint);
            c.drawLine(x, y, x - aw, y + aw, mHighlightPaint);
            c.drawLine(x - arrowLength, y, x, y, mHighlightPaint);
        }

        //绘制文字
        Paint.FontMetrics metrics = mHighlightPaint.getFontMetrics();
        float baseY = y - (metrics.top + metrics.bottom) / 2;
        c.drawText(text, left, baseY, mHighlightPaint);
    }

    private float[] getMinPoint(List<CandleEntry> list) {
        int min = Math.max(0, Math.round(mChart.getLowestVisibleX()));
        int max = Math.round(mChart.getHighestVisibleX());
        float[] point = {list.get(min).getX(), list.get(min).getLow()};
        for (int i = min + 1; i <= Math.min(list.size() - 1, max); i++) {
            if (point[1] > list.get(i).getLow()) {
                point[0] = list.get(i).getX();
                point[1] = list.get(i).getLow();
            }
        }
        return point;
    }

    private void drawHigh(Canvas c, DecimalFormat ft) {
        if (indices == null) {
            return;
        }

        CandleData candleData = mChart.getCandleData();
        for (Highlight high : indices) {
            ICandleDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            CandleEntry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set))
                continue;

            //获取接触点的X
            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();
            MPPointD pix = mChart.getTransformer(set.getAxisDependency())
                    .getPixelForValues(e.getX(), (lowValue + highValue) / 2f);
            float xp = (float) pix.x;

            mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());
            mHighlightPaint.setTextSize(highSize);
            mHighlightPaint.setStyle(Paint.Style.FILL);

            float contentBottom = mViewPortHandler.contentBottom();
            //绘制竖线
            ChartUtil.drawDashLineVer(c, xp, mViewPortHandler.contentTop(), contentBottom,
                    mHighlightPaint, set.getHighLightColor(), 4, 2, 3, 2);

            float y = high.getDrawY();
            float yMaxValue = mChart.getYChartMax();
            float yMinValue = mChart.getYChartMin();
            float yMin = getYPixelForValues(xp, yMaxValue);
            float yMax = getYPixelForValues(xp, yMinValue);
            if (y >= 0 && y <= contentBottom) {//在区域内即绘制横线
                float contentRight = mViewPortHandler.contentRight();
                //绘制横线
                ChartUtil.drawDashLineHor(c, mViewPortHandler.contentLeft(), contentRight, y,
                        mHighlightPaint, set.getHighLightColor(), 4, 2, 3, 2);
                //文字外围padding大小
                int halfPaddingVer = 6;
                int halfPaddingHor = 10;
                //绘制文字框
                float yValue = (yMax - y) / (yMax - yMin) * (yMaxValue - yMinValue) + yMinValue;
                String text = ft.format(yValue);
                int width = Utils.calcTextWidth(mHighlightPaint, text);
                int height = Utils.calcTextHeight(mHighlightPaint, text);
                float top = Math.max(0, y - height / 2F - halfPaddingVer);//考虑间隙
                float bottom = top + height + halfPaddingVer * 2;
                if (bottom > contentBottom) {
                    bottom = contentBottom;
                    top = bottom - height - halfPaddingVer * 2;
                }
                float left = contentRight - width - 2 * halfPaddingHor;
                Path path = new Path();
                path.moveTo(left, top);
                path.lineTo(contentRight, top);
                path.lineTo(contentRight, bottom);
                path.lineTo(left, bottom);
                path.lineTo(left - height + halfPaddingVer, y);
                path.close();
                mHighlightPaint.setColor(Color.parseColor(colorRect));
                c.drawPath(path, mHighlightPaint);
                //绘制文字
                Paint.FontMetrics metrics = mHighlightPaint.getFontMetrics();
                float baseY = (top + bottom - metrics.top - metrics.bottom) / 2;
                mHighlightPaint.setColor(Color.parseColor(colorText));
                c.drawText(text, left + halfPaddingHor, baseY, mHighlightPaint);
            }
        }
        indices = null;
    }
}
