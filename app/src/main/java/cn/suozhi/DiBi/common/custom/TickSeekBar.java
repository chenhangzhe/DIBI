package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatSeekBar;
import cn.suozhi.DiBi.R;

/**
 * 自定义添加节点的SeekBar
 *      - 因SeekBar自带paddingStart、paddingEnd为16dp 且有水波纹效果，因此需设定paddingStart、paddingEnd为8dp
 */
public class TickSeekBar extends AppCompatSeekBar {

    private int backHeight, backCorner, tickWidth;
    private int colorBack, colorProgress;
    private Paint paint;
    private RectF rect;

    private List<Integer> ticks;

    public TickSeekBar(Context context) {
        this(context, null);
    }

    public TickSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        init();
    }

    private void getAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.TickSeekBar);
            backHeight = ta.getDimensionPixelSize(R.styleable.TickSeekBar_tickBackHeight, dp(6));
            backCorner = ta.getDimensionPixelSize(R.styleable.TickSeekBar_tickBackCorner, dp(2));
            tickWidth = ta.getDimensionPixelSize(R.styleable.TickSeekBar_tickWidth, dp(10));
            colorBack = ta.getColor(R.styleable.TickSeekBar_tickColorBack, Color.WHITE);
            colorProgress = ta.getColor(R.styleable.TickSeekBar_tickColorProgress, Color.WHITE);
            ta.recycle();
        } else {
            backHeight = dp(8);
            backCorner = dp(2);
            tickWidth = dp(10);
            colorBack = Color.WHITE;
            colorProgress = Color.WHITE;
        }
    }

    private void init() {
        rect = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    public TickSeekBar addTick(int... tick) {
        if (ticks == null) {
            ticks = new ArrayList<>();
        }
        if (tick == null || tick.length == 0) {
            return this;
        }
        for (int t : tick) {
            ticks.add(t);
        }
        postInvalidate();
        return this;
    }

    public TickSeekBar clearTick() {
        if (ticks != null) {
            ticks.clear();
        }
        postInvalidate();
        return this;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawBack(canvas);
        drawTicks(canvas);
        drawThumb(canvas);
    }

    /**
     * 返回进度为progress的中心点长度，且考虑thumb的宽度
     */
    private float getProgressCenter(int progress) {
        float radius = getHeight() / 3.0F;
        return (getWidth() - 2 * radius) * progress / getMax() + radius;
    }

    private void drawBack(Canvas canvas) {
        float cy = getHeight() / 2.0F;
        float hb = backHeight / 2.0F;
        float radius = getHeight() / 3.0F;
        rect.set(radius, cy - hb, getWidth() - radius, cy + hb);
        paint.setColor(colorBack);
        canvas.drawRoundRect(rect, backCorner, backCorner, paint);
        float progress = getProgressCenter(getProgress());
        rect.set(radius, cy - hb, progress + radius, cy + hb);
        paint.setColor(colorProgress);
        canvas.drawRoundRect(rect, backCorner, backCorner, paint);
    }

    protected void drawTicks(Canvas canvas) {
        if (ticks == null || ticks.size() == 0) {
            return;
        }
        int progress = getProgress();
        float cy = getHeight() / 2.0F;
        float radius = tickWidth / 2.0F;
        for (int i = 0; i < ticks.size(); i++) {
            int t = ticks.get(i);
            float cx = getProgressCenter(t);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cx, cy, radius + dp(1), paint);
            paint.setColor(t > progress ? colorBack : colorProgress);
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

    private void drawThumb(Canvas canvas) {
        float cx = getProgressCenter(getProgress());
        float cy = getHeight() / 2.0F;
        float radius = getHeight() / 3.0F;
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setColor(colorProgress);
        canvas.drawCircle(cx, cy, radius - dp(1), paint);
    }

    public int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
