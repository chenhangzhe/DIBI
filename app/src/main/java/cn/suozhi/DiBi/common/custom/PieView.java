package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import cn.suozhi.DiBi.R;

/**
 * 饼状图
 */
public class PieView extends View {

    private int startAngle;//起始绘制角度
    private int colorDefault, countDefault;
    private boolean drawNull;
    private int[] colors;
    private Paint paint;
    private RectF rect;

    private int[] values;

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        init();
    }

    private void getAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.PieView);
            startAngle = ta.getInteger(R.styleable.PieView_pieStartAngle, -90);
            colorDefault = ta.getColor(R.styleable.PieView_pieDefaultColor, Color.WHITE);
            countDefault = ta.getInteger(R.styleable.PieView_pieDefaultCount, 1);
            drawNull = ta.getBoolean(R.styleable.PieView_pieDrawNull, false);
            ta.recycle();
        } else {
            startAngle = -90;
            colorDefault = Color.WHITE;
            countDefault = 1;
            drawNull = false;
        }
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (values == null || values.length == 0) {
            if (drawNull) {
                drawPie(canvas, getDefaultValue());
            }
        } else {
            drawPie(canvas, isAllZero(values) ? getDefaultValue() : values);
        }
    }

    private int[] getDefaultValue() {
        int[] val = new int[countDefault];
        for (int i = 0; i < countDefault; i++) {
            val[i] = 1;
        }
        return val;
    }

    private boolean isAllZero(int[] val) {
        if (val == null || val.length == 0) {
            return true;
        }
        boolean all0 = true;
        for (int v : val) {
            if (v > 0) {
                all0 = false;
                break;
            }
        }
        return all0;
    }

    private void drawPie(Canvas canvas, int... val) {
        int sum = 0;
        for (int v : val) {
            sum += v;
        }
        rect.set(0, 0 , getWidth(), getHeight());
        float ta = 0;//已画过的扇形角度
        for (int i = 0; i < val.length; i++) {
            float angle = val[i] * 360F / sum;
            if (i == val.length - 1) {
                float last = 360 - ta;//剩余未绘制角度
                if (Math.abs(angle - last) <= 1) {
                    drawArc(canvas, startAngle + ta, last, getPieColor(i));
                    break;
                }
            }
            drawArc(canvas, startAngle + ta, angle, getPieColor(i));
            ta += angle;
        }
    }

    private void drawArc(Canvas canvas, float start, float sweep, int color) {
        paint.setColor(color);
        canvas.drawArc(rect, start, sweep, true, paint);
    }

    private int getPieColor(int index) {
        if (colors == null || index < 0 || index >= colors.length) {
            return colorDefault;
        }
        return colors[index];
    }

    public void setColors(int... colors) {
        this.colors = colors;
    }

    public void setValues(int... values) {
        this.values = values;
        postInvalidate();
    }
}
