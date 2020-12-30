package cn.suozhi.DiBi.common.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * 自定义BarChart渲染器 使Bar的颜色根据取值来实现 自定义高亮
 * 只修改 {@link #drawDataSet(Canvas, IBarDataSet, int)} 中设置多种颜色的情况
 * 使用方法: 1.先设置渲染器 {@link BarChart#setRenderer(DataRenderer)} 传入此渲染器
 *           2.再调用 {@link BarDataSet#setColors(int...)} 设置多种颜色;
 *           3.设置数据时 调用 {@link BarEntry#BarEntry(float, float, Object)} 传入Integer类型的data指明第几种颜色.
 */
public class OffsetBarRenderer extends BarChartRenderer {

    protected float barOffset;//BarChart绘制时偏移多少个单位 --小于0时向左偏移
    protected float highWidth, highSize;//高亮线宽度 单位:dp  /  高亮文字大小 单位:px
    private RectF mBarShadowRectBuffer = new RectF();

    private Highlight[] indices;
    private String colorRect = "#8AA0FF", colorText = "#323761";
    private DecimalFormat format = new DecimalFormat("0.0000");//保留小数点后四位

    private ChartListener chartListener;

    public OffsetBarRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        this(chart, animator, viewPortHandler, 0);
    }

    public OffsetBarRenderer(BarDataProvider chart, ChartAnimator animator,
                             ViewPortHandler viewPortHandler, float barOffset) {
        super(chart, animator, viewPortHandler);
        this.barOffset = barOffset;
    }

    public OffsetBarRenderer setHighWidthSize(float width, float textSize) {
        highWidth = Utils.convertDpToPixel(width);
        highSize = textSize;
        return this;
    }

    public OffsetBarRenderer setChartListener(ChartListener chartListener) {
        this.chartListener = chartListener;
        return this;
    }

    @Override
    public void initBuffers() {
        BarData barData = mChart.getBarData();
        mBarBuffers = new OffsetBarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new OffsetBarBuffer(set.getEntryCount() * 4 *
                    (set.isStacked() ? set.getStackSize() : 1), barData.getDataSetCount(),
                    set.isStacked(), barOffset);
        }
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;
        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();
            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int)(Math.ceil((float)(dataSet.getEntryCount()) * phaseX)),
                    dataSet.getEntryCount()); i < count; i++) {

                BarEntry e = dataSet.getEntryForIndex(i);
                x = e.getX();
                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;
                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();
                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);
        trans.pointValuesToPixel(buffer.buffer);

        int size = dataSet.getColors().size();
        final boolean isSingleColor = size == 1;
        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                BarEntry entry = dataSet.getEntryForIndex(j / 4);
                Object data = entry.getData();
                if (data instanceof String && ((String) data).contains(",")) {
                    String d = ((String) data).split(",")[0];
                    int i = Integer.parseInt(d);
                    mRenderPaint.setColor(size > 1 ? dataSet.getColors().get(i % size) : Color.BLACK);
                } else {
                    mRenderPaint.setColor(dataSet.getColor(j / 4));
                }
            }

            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
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

        BarData barData = mChart.getBarData();
        for (Highlight high : indices) {
            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set))
                continue;

            //获取接触点的X
            float barWidth = barData.getBarWidth();
            Transformer trans = mChart.getTransformer(set.getAxisDependency());
            prepareBarHighlight(e.getX() + barOffset, 0, 0, barWidth / 2, trans);
            float xp = mBarRect.centerX();

            mHighlightPaint.setStrokeWidth(highWidth);
            mHighlightPaint.setTextSize(highSize);
            mHighlightPaint.setStyle(Paint.Style.FILL);

            float yMaxValue = mChart.getYChartMax();
            float yMin = getYPixelForValues(xp, yMaxValue);
            float yMax = getYPixelForValues(xp, 0);
            //绘制竖线
            ChartUtil.drawDashLineVer(c, xp, yMin, yMax, mHighlightPaint, set.getHighLightColor(),
                    4, 2, 3, 2);

            //文字外围padding大小
            int halfPaddingVer = 8;
            int halfPaddingHor = 10;
            String textX;//高亮点的X显示文字
            Object data = e.getData();
            if (data instanceof String && ((String) data).contains(",")) {
                textX = ((String) data).split(",")[1];
            } else {
                textX = e.getX() + "";
            }

            float contentLeft = mViewPortHandler.contentLeft();
            float contentRight = mViewPortHandler.contentRight();
            if (!TextUtils.isEmpty(textX)) {//绘制x的值
                int width = Utils.calcTextWidth(mHighlightPaint, textX);
                int height = Utils.calcTextHeight(mHighlightPaint, textX);
                float bottom = yMax + height + halfPaddingVer * 2;
                float halfX = width / 2F + halfPaddingHor;
                float left = xp - halfX;
                float right = xp + halfX;
                if (left < contentLeft) {
                    left = 0;
                    right = left + width + halfPaddingHor * 2;
                } else if (right > contentRight) {
                    right = contentRight;
                    left = right - width - halfPaddingHor * 2;
                }
                mHighlightPaint.setColor(Color.parseColor(colorRect));
                c.drawRect(left, yMax, right, bottom, mHighlightPaint);
                //绘制文字
                Paint.FontMetrics metrics = mHighlightPaint.getFontMetrics();
                float baseY = (yMax + bottom - metrics.top - metrics.bottom) / 2;
                mHighlightPaint.setColor(Color.parseColor(colorText));
                c.drawText(textX, left + halfPaddingHor, baseY, mHighlightPaint);
            }

            float y = high.getDrawY();
            if (y >= 0 && y <= yMax) {//在区域内即绘制横线
                //绘制横线
                ChartUtil.drawDashLineHor(c, contentLeft, contentRight, y, mHighlightPaint, set.getHighLightColor(),
                        4, 2, 3, 2);
                halfPaddingVer = 6;
                //绘制文字框
                float yValue = (yMax - y) / (yMax - yMin) * yMaxValue;
                String text = ChartUtil.getFormat(chartListener, format).format(yValue);
                int width = Utils.calcTextWidth(mHighlightPaint, text);
                int height = Utils.calcTextHeight(mHighlightPaint, text);
                float top = Math.max(0, y - height / 2F - halfPaddingVer);//考虑间隙
                float bottom = top + height + halfPaddingVer * 2;
                if (bottom > yMax) {
                    bottom = yMax;
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
