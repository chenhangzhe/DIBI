package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.suozhi.DiBi.R;

/**
 * 环形进度条
 */
public class RingProgressBar extends View {

    private int max, textSize;
    private int colorShadow, colorBackground, colorRing, colorText;
    private float progress, ringWidthRate;//progress自动递减
    private boolean startAfterFinish;
    private Paint paintText, paintDraw;
    private RectF rect;

    //不同分割下显示不同颜色
    private List<Float> divideList;
    private List<Integer> backList;
    private List<Integer> ringList;

    private boolean ongoing = false;
    private Handler handler;
    private OnChangeListener changeListener;

    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        init();
    }

    private void getAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RingProgressBar);
            max = ta.getInteger(R.styleable.RingProgressBar_ringMax, -1);
            colorShadow = ta.getColor(R.styleable.RingProgressBar_ringShadow, Color.BLACK);
            colorBackground = ta.getColor(R.styleable.RingProgressBar_ringBackground, Color.WHITE);
            colorRing = ta.getColor(R.styleable.RingProgressBar_ringColor, Color.RED);
            colorText = ta.getColor(R.styleable.RingProgressBar_ringTextColor, Color.RED);
            textSize = (int) ta.getDimension(R.styleable.RingProgressBar_ringTextSize, sp(16));
            float rate = ta.getFloat(R.styleable.RingProgressBar_ringWidthRate, 0.1F);
            ringWidthRate = Math.max(Math.min(rate, 0.3F), 0.02F);
            startAfterFinish = ta.getBoolean(R.styleable.RingProgressBar_ringStartAfterFinish, false);
            ta.recycle();
        } else {
            max = -1;
            colorShadow = Color.BLACK;
            colorBackground = Color.WHITE;
            colorRing = Color.RED;
            colorText = Color.RED;
            textSize = sp(16);
            ringWidthRate = 0.1F;
            startAfterFinish = false;
        }
    }

    private void init() {
        progress = -1;
        rect = new RectF();
        divideList = new ArrayList<>();
        backList = new ArrayList<>();
        ringList = new ArrayList<>();

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setDither(true);
        paintText.setTextAlign(Paint.Align.CENTER);

        paintDraw = new Paint();
        paintDraw.setAntiAlias(true);
        paintDraw.setDither(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//阴影效果
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float rw = getWidth() / 2.0F;
        float rh = getHeight() / 2.0F;
        float radius = Math.min(rw, rh);
        if (progress <= -1) {//无进度，只绘制底色
            paintDraw.setColor(colorBackground);
            paintDraw.setShadowLayer(10, 0, 0, colorShadow);
            canvas.drawCircle(rw, rh, radius, paintDraw);
            return;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress > max) {
            progress = max;
        }

        int index = getIndexDivide();
        int back = getColorBack(index);
        //画底圆
        paintDraw.setColor(back);
        paintDraw.setShadowLayer(10, 0, 0, colorShadow);
        canvas.drawCircle(rw, rh, radius, paintDraw);
        //画进度
        paintDraw.setColor(getColorRing(index));
        paintDraw.clearShadowLayer();
        rect.set(0, 0, getWidth(), getHeight());
        float angle = progress * 360 / max;
        canvas.drawArc(rect, 270 - angle, angle, true, paintDraw);
        //遮盖
        float rc = rw * (1 - ringWidthRate);//减去进度的宽度
        paintDraw.setColor(back);
        canvas.drawCircle(rw, rh, rc, paintDraw);
        //显示文字
        if (max > 0) {
            paintText.setColor(colorText);
            paintText.setTextSize(textSize);
            Paint.FontMetrics metrics = paintText.getFontMetrics();
            float baseY = (getHeight() - metrics.top - metrics.bottom) / 2;
            int text = (int) Math.floor(progress);
            canvas.drawText(text + "s", rw, baseY, paintText);
        }
    }

    private int getIndexDivide() {
        if (divideList.size() == 0) {
            return -1;
        }
        int index = -1;
        for (int i = 0; i < divideList.size(); i++) {
            if (progress > divideList.get(i)) {
                index = i - 1;
                break;
            }
            if (i == divideList.size() -1 && progress <= divideList.get(i)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getColorBack(int index) {
        if (index < 0 || index >= backList.size()) {
            return colorBackground;
        }
        return backList.get(index);
    }

    private int getColorRing(int index) {
        if (index < 0 || index >= ringList.size()) {
            return colorRing;
        }
        return ringList.get(index);
    }

    public RingProgressBar setDivide(float... div) {
        if (div.length == 0) {
            return this;
        }
        if (divideList.size() > 0) {
            divideList.clear();
        }
        for (float d : div) {
            divideList.add(d);
        }
        return this;
    }

    public RingProgressBar setColorBack(int... back) {
        if (back.length == 0) {
            return this;
        }
        if (backList.size() > 0) {
            backList.clear();
        }
        for (int b : back) {
            backList.add(b);
        }
        return this;
    }

    public RingProgressBar setColorRing(int... ring) {
        if (ring.length == 0) {
            return this;
        }
        if (ringList.size() > 0) {
            ringList.clear();
        }
        for (int r : ring) {
            ringList.add(r);
        }
        return this;
    }

    public int getMax() {
        return max;
    }

    public RingProgressBar setMax(int max) {
        this.max = max;
        return this;
    }

    public float getProgress() {
        return progress;
    }

    public RingProgressBar setProgress(float pro) {
        if (Math.abs(progress - pro) < 1.5F) {
            return this;
        }
        destroy();
        this.progress = pro;
        postInvalidate();
        return this;
    }

    public void start() {
        if (handler != null) {
            return;
        }
        ongoing = true;
        handler = new Handler();
        progress += 0.1F;
        handler.post(run);
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            if (handler == null) {
                return;
            }
            progress -= 0.1F;
            postInvalidate();
            if (progress <= 0) {
                if (changeListener != null) {
                    changeListener.onRingFinish();
                }
                if (startAfterFinish) {
                    progress = max;
                    postInvalidate();
                    handler.postDelayed(this, 100);
                } else {
                    handler = null;
                    ongoing = false;
                }
                return;
            }
            handler.postDelayed(this, 100);
        }
    };

    public boolean isOngoing() {
        return ongoing;
    }

    public boolean isFinish() {
        return progress >= max && max > 0;
    }

    public RingProgressBar setOnChangeListener(OnChangeListener finishListener) {
        this.changeListener = finishListener;
        return this;
    }

    public interface OnChangeListener {
        void onRingFinish();
    }

    public void destroy() {
        if (handler != null) {
            handler.removeCallbacks(run);
            handler = null;
        }
        ongoing = false;
    }

    public int sp(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
