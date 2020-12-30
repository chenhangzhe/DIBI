package cn.suozhi.DiBi.common.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * 自定义LineChart渲染器 绘制高亮  -- 绘制方式和自定义CandleStickChart渲染器相同
 * 使用方法: 1.先设置渲染器 {@link CombinedChart#setRenderer(DataRenderer)}
 *              传入自定义渲染器 将其中Line图的渲染器替换成此渲染器
 *           2.设置数据时 调用 {@link Entry#Entry(float, float, Object)}
 *              传入String类型的data 以绘制x的值  -- 如未设置 则只绘制竖线
 */
public class HighlightLineRenderer extends LineChartRenderer {

    private float highSize;//图表高亮文字大小 单位:px

    private Highlight[] indices;
    private String colorRect = "#8AA0FF", colorText = "#323761";
    private DecimalFormat format = new DecimalFormat("0.0000");//保留小数点后四位

    private ChartListener chartListener;

    public HighlightLineRenderer(LineDataProvider chart, ChartAnimator animator,
                                 ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public HighlightLineRenderer setHighSize(float textSize) {
        highSize = textSize;
        return this;
    }

    public HighlightLineRenderer setChartListener(ChartListener chartListener) {
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
        drawHigh(c);
    }

    private void drawHigh(Canvas c) {
        if (indices == null) {
            return;
        }

        LineData lineData = mChart.getLineData();
        for (Highlight high : indices) {
            ILineDataSet set = lineData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            Entry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set))
                continue;

            //获取接触点的X
            MPPointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(),
                    e.getY() * mAnimator.getPhaseY());
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
                String text = ChartUtil.getFormat(chartListener, format).format(yValue);
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
