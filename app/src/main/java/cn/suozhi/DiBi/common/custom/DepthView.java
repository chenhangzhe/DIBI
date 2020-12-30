package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 委托的背景深度
 */
public class DepthView extends View {

    private float depth;
    private int color;
    private boolean startLeft;
    private Paint paint;
    private RectF rect;

    public DepthView(Context context) {
        this(context, null);
    }

    public DepthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        depth = 0;
        color = Color.WHITE;
        startLeft = false;

        rect = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
    }

    public DepthView setColor(int color) {
        this.color = color;
        paint.setColor(color);
        return this;
    }

    public DepthView setStartLeft(boolean startLeft) {
        this.startLeft = startLeft;
        return this;
    }

    public void setDepth(double depth) {
        if (depth < 0) {
            this.depth = 0;
        } else if (depth > 1) {
            this.depth = 1;
        } else {
            this.depth = (float) depth;
        }
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int w = getWidth();
        int h = getHeight();
        float dw = w * depth;
        if (startLeft) {
            rect.set(0, 0, dw, h);
        } else {
            rect.set(w - dw, 0, w, h);
        }
        canvas.drawRect(rect, paint);
    }
}
