package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import cn.suozhi.DiBi.R;

/**
 * 指示器 -- 如果显示异常 请不要放在ConstraintLayout布局中
 */
public class IndicatorView extends View {

    private int count, item;//总数量 / 当前选中(0 ~ count-1)
    private int singleWidth, colorBack, colorInd, corner;//单个宽度 / 底色 / 指示器颜色 / 圆角
    private boolean showOne;
    private float offset;//偏移量
    private Paint paint;
    private RectF rect;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        init();
    }

    private void getAttrs(AttributeSet attrs) {
        int tw = Color.parseColor("#33FFFFFF");
        if (attrs != null) {
            TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.IndicatorView);
            singleWidth = ta.getDimensionPixelSize(R.styleable.IndicatorView_indSingleWidth, dp(15));
            colorBack = ta.getColor(R.styleable.IndicatorView_indBackColor, tw);
            colorInd = ta.getColor(R.styleable.IndicatorView_indColor, Color.WHITE);
            corner = ta.getDimensionPixelSize(R.styleable.IndicatorView_indCorner, 0);
            showOne = ta.getBoolean(R.styleable.IndicatorView_indShowOne, true);
            ta.recycle();
        } else {
            singleWidth = dp(15);
            colorBack = tw;
            colorInd = Color.WHITE;
            corner = 0;
            showOne = true;
        }
    }

    private void init() {
        count = item = 0;
        offset = 0;
        rect = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            h = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            h = dp(4);
        }
        setMeasuredDimension(singleWidth * count, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight();
        if (count <= 0) {
            setVisibility(GONE);
        } else if (count == 1) {
            if (showOne) {
                setVisibility(VISIBLE);
                rect.set(0, 0, getWidth(), h);
                paint.setColor(colorInd);
                canvas.drawRoundRect(rect, corner, corner, paint);
            } else {
                setVisibility(GONE);
            }
        } else {
            setVisibility(VISIBLE);
            //画底
            rect.set(0, 0, getWidth(), h);
            paint.setColor(colorBack);
            canvas.drawRoundRect(rect, corner, corner, paint);
            //画指示器 - 左半边
            float left = (item + offset) * singleWidth;
            rect.set(left, 0, left + singleWidth * 0.6F, h);
            paint.setColor(colorInd);
            float cl = item == 0 ? corner * (1 - offset) : 0;
            canvas.drawRoundRect(rect, cl, cl, paint);
            //画指示器 - 右半边
            rect.set(left + singleWidth * 0.4F, 0, left + singleWidth, h);
            paint.setColor(colorInd);
            float cr;
            if (item == count - 1) {
                cr = corner;
            } else if (item == count - 2) {
                cr = corner * offset;
            } else {
                cr = 0;
            }
            canvas.drawRoundRect(rect, cr, cr, paint);
        }
    }

    public void setCount(int count) {
        if (count < 0) {
            return;
        }
        this.count = count;
        if (item < 0 || item >= count) {
            item = 0;
        }
        requestLayout();
        postInvalidate();
    }

    public int getCount() {
        return count;
    }

    public int getCurrentItem() {
        return item;
    }

    public void setCurrentItem(int position) {
        setCurrentItem(position, 0);
    }

    public void setCurrentItem(int position, float offset) {
        if (position < 0 || position >= count || offset < 0 || offset >= 1) {
            return;
        }
        item = position;
        this.offset = offset;
        postInvalidate();
    }

    public int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
