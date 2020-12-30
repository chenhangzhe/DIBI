package cn.suozhi.DiBi.common.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 自定义Y轴标签渲染器，使其不出界
 * 只添加一个偏移 使最后一个标签处于刻度下方 第一个标签处于刻度上方 其他标签居中
 */
public class InBoundYAxisRenderer extends YAxisRenderer {

    private DecimalFormat format = new DecimalFormat("0.0000");//保留小数点后四位

    public InBoundYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled() ? mYAxis.mEntryCount : (mYAxis.mEntryCount - 1);

        // draw
        int labelHeight = Utils.calcTextHeight(mAxisLabelPaint, "A");
        for (int i = from; i < to; i++) {
            String text = mYAxis.getFormattedLabel(i);
            float os = i == mYAxis.mEntryCount - 1 ? -0.9F * labelHeight : 0.7F * labelHeight;
            /*float os;
            if (i == from) {
                os = 0.7F * labelHeight;
            } else if (i == mYAxis.mEntryCount - 1) {
                os = -0.9F * labelHeight;
            } else {
                os = 0;
            }*/
            c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset - os, mAxisLabelPaint);
        }
    }

    @Override
    public void renderAxisLabels(Canvas c) {
        super.renderAxisLabels(c);
        drawLimit(c);
    }

    /**
     * 绘制完标签后再绘制限制线
     */
    private void drawLimit(Canvas c) {
        List<LimitLine> limitLines = mYAxis.getLimitLines();
        if (limitLines == null || limitLines.size() <= 0) {
            return;
        }

        float[] pts = {0, 0};
        Path limitLinePath = mRenderLimitLines;
        limitLinePath.reset();

        for (int i = 0; i < limitLines.size(); i++) {
            LimitLine l = limitLines.get(i);
            if (!l.isEnabled()) {
                continue;
            }

            pts[1] = l.getLimit();
            mTrans.pointValuesToPixel(pts);

            //文字外围padding大小
            int halfPaddingVer = 5;
            int halfPaddingHor = 5;
            //确认文字框位置
            String text = format.format(l.getLimit());
            int width = Utils.calcTextWidth(mLimitLinePaint, text);
            int height = Utils.calcTextHeight(mLimitLinePaint, text);
            float contentBottom = mViewPortHandler.contentBottom();
//            float halfHeight = height / 2F + halfPaddingVer;
//            float top = pts[1] - halfHeight;
//            float bottom = pts[1] + halfHeight;
            if (pts[1] <= 0 || pts[1] >= contentBottom) {
                continue;
            }

            float right = mViewPortHandler.contentRight();
            //绘制虚线
            mLimitLinePaint.setStyle(Paint.Style.STROKE);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());

            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            limitLinePath.lineTo(right, pts[1]);
            c.drawPath(limitLinePath, mLimitLinePaint);
            limitLinePath.reset();

            //绘制文字
            mLimitLinePaint.setStyle(Paint.Style.FILL);
            mLimitLinePaint.setPathEffect(null);
            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setTextSize(l.getTextSize());
            //绘制文字框
            /*if (top < 0) {
                top = 0;
                bottom = top + height + halfPaddingVer * 2;
            } else if (bottom > contentBottom) {
                bottom = contentBottom;
                top = bottom - height - halfPaddingVer * 2;
            }
            c.drawRect(right - width - 2 * halfPaddingHor, top, right, bottom, mLimitLinePaint);*/
            float top = pts[1] + halfPaddingVer;
            float bottom = top + height;
            if (pts[1] + height + 2 * halfPaddingVer >= contentBottom) {
                bottom = pts[1] - halfPaddingVer;
                top = bottom - height;
            }
            //绘制文字
            mLimitLinePaint.setColor(l.getTextColor());
            Paint.FontMetrics metrics = mLimitLinePaint.getFontMetrics();
            float baseY = (top + bottom - metrics.top - metrics.bottom) / 2;
            c.drawText(text, halfPaddingHor, baseY, mLimitLinePaint);
        }
    }

    /*@Override
    public void renderLimitLines(Canvas c) {
        //什么都不做 避免先绘制限制线再绘制标签
    }*/
}
