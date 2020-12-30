package cn.suozhi.DiBi.common.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;

public class ChartUtil {

    /**
     * 返回0.0000
     */
    public static String getDecimalFormat(int pointNum) {
        StringBuffer format = new StringBuffer("0");
        for (int i = 0; i < pointNum; i++) {
            if (i == 0) {
                format.append(".");
            }
            format.append("0");
        }
        return format.toString();
    }

    public static DecimalFormat getFormat(ChartListener listener, DecimalFormat defaultFormat) {
        if (listener != null && listener.getPoint() >= 0) {
            return new DecimalFormat(getDecimalFormat(listener.getPoint()));
        }
        return defaultFormat;
    }

    /**
     * 绘制虚线横线 -- 从 (startX, startY) 到 (stopX, stopY)
     * interval 各间隔长度 高亮色、透明色交替进行 默认从高亮色开始
     */
    public static void drawDashLineHor(Canvas c, float startX, float stopX, float y, Paint paint, int highColor,
                                 int... interval) {
        if (interval == null || interval.length == 0) {
            c.drawLine(startX, y, stopX, y, paint);
            return;
        }
        float dp = Utils.convertDpToPixel(1);
        float draw = 0;//已绘制长度
        while (draw < stopX) {
            for (int i = 0; i < interval.length; i++) {
                if (draw >= stopX) {
                    break;
                }
                paint.setColor(i % 2 == 0 ? highColor : Color.TRANSPARENT);
                float endX = Math.min(stopX, draw + interval[i] * dp);
                c.drawLine(draw, y, endX, y, paint);
                draw = endX;
            }
        }
    }

    /**
     * 绘制虚线竖线 -- 从 (startX, startY) 到 (stopX, stopY)
     * interval 各间隔长度 高亮色、透明色交替进行 默认从高亮色开始
     */
    public static void drawDashLineVer(Canvas c, float x, float startY, float stopY, Paint paint, int highColor,
                                 int... interval) {
        if (interval == null || interval.length == 0) {
            c.drawLine(x, startY, x, stopY, paint);
            return;
        }
        float dp = Utils.convertDpToPixel(1);
        float draw = 0;//已绘制长度
        while (draw < stopY) {
            for (int i = 0; i < interval.length; i++) {
                if (draw >= stopY) {
                    break;
                }
                paint.setColor(i % 2 == 0 ? highColor : Color.TRANSPARENT);
                float endY = Math.min(stopY, draw + interval[i] * dp);
                c.drawLine(x, draw, x, endY, paint);
                draw = endY;
            }
        }
    }
}
