package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.home.model.NotifyEntity;

/**
 * 垂直滚动广告栏
 */

public class ADTextView extends View {

    private int mSpeed; //文字出现或消失的速度 建议1~5
    private int mInterval; //文字停留在中间的时长
    private int mTextSize; //文字大小
    private int mNum; //文字段数
    private int mY = 0; //文字的Y坐标
    private int mIndex = 0; //当前的数据下标
    private Paint[] mPaints; //绘制的画笔
    private boolean isMove = true; //文字是否移动
    private boolean hasInit = false;
    private boolean isPaused = false;

    private final String NEW = "new";
    private Paint paintRect;
    private RectF rect;
    private Rect rectText, rectNew;//文字和new标签

    private List<NotifyEntity.DataEntity.RecordsEntity> mTexts; //显示文字的数据源

    private OnItemClickListener onItemClickListener;

    public ADTextView(Context context) {
        this(context, null);
    }

    public ADTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        init();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(long id, String title);
    }

    /**
     * 解析自定义属性
     */
    private void getAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ADTextView);
            mSpeed = ta.getInt(R.styleable.ADTextView_adSpeed, 2);
            mInterval = ta.getInt(R.styleable.ADTextView_adInterval, 1500);
            mNum = ta.getInt(R.styleable.ADTextView_adNum, 1);
            mTextSize = (int) ta.getDimension(R.styleable.ADTextView_adSize, sp(14));
            ta.recycle();
        } else {
            mSpeed = 2;
            mInterval = 1500;
            mNum = 1;
            mTextSize = sp(14);
        }
    }

    private void init() {
        mIndex = 0;
        rect = new RectF();
        rectText = new Rect();
        rectNew = new Rect();

        mPaints = new Paint[mNum];
        for (int i = 0; i < mNum; i++) {
            mPaints[i] = initPaint(Color.BLACK);
        }
        mPaints[0].setTextSize(mTextSize);

        paintRect = new Paint();
        paintRect.setAntiAlias(true);
        paintRect.setDither(true);
        paintRect.setColor(Color.parseColor("#E86240"));
    }

    private Paint initPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(mTextSize);
        paint.setColor(color);
        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (onItemClickListener != null && mTexts != null) {
                    onItemClickListener.onClick(mTexts.get(mIndex).getId(),
                            mTexts.get(mIndex).getTitle());
                }
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    //测量宽度
    private int measureWidth(int widthMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else { //宽度最小十个字的宽度
            String text = "我随手一打就是十个字";
//            Rect rect = new Rect();
//            mPaints[0].getTextBounds(text, 0, text.length(), rect);
//            result = rect.right - rect.left;
            result = (int) mPaints[0].measureText(text);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    //测量高度
    private int measureHeight(int heightMeasureSpec) {
        float result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {//高度至少为两倍字高
            int mTextHeight = (int) (mPaints[0].descent() - mPaints[0].ascent()); //前缀文字字高
            if (mPaints.length > 1) {
                int mContentTextHeight = (int) (mPaints[1].descent() - mPaints[1].ascent()); //内容文字字高
                result = Math.max(mTextHeight, mContentTextHeight) * 2.5F;
            } else {
                result = mTextHeight * 2.5F;
            }
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return (int) result;
    }

    //设置数据源
    public void setTexts(List<NotifyEntity.DataEntity.RecordsEntity> texts) {
        this.mTexts = texts;
        invalidate();
    }

    //设置广告文字的停顿时间
    public void setInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    //设置速度
    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public ADTextView setSize(int... size) {
        for (int i = 0; i < size.length; i++) {
            mPaints[i].setTextSize(sp(size[i]));
        }
        return this;
    }

    //文字颜色
    public ADTextView setColor(int... color) {
        for (int i = 0; i < color.length; i++) {
            mPaints[i].setColor(color[i]);
        }
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTexts != null) {
            String text = mTexts.get(mIndex).getTitle();

            rectText.setEmpty();
            mPaints[0].getTextBounds(text, 0, text.length(), rectText);

            if (mY == 0 && !hasInit) {
                mY = getMeasuredHeight() - rectText.top;
                hasInit = true;
            }
            int width = getWidth();
            if (mTexts.get(mIndex).isNew()) {
                rectNew.setEmpty();
                mPaints[1].getTextBounds(NEW, 0, NEW.length(), rectNew);
                String title = getTitle(text, width - rectNew.right - 12 - 10);
                canvas.drawText(title, 0, mY, mPaints[0]);

                float rs = mPaints[0].measureText(title) + 10;//矩形起始位置
                Paint.FontMetrics fm0 = mPaints[0].getFontMetrics();
                float center = mY + (fm0.top + fm0.bottom) / 2;
                float hl = (rectNew.bottom - rectNew.top) / 2.0F;//标签矩形一半高度
                rect.set(rs, center - hl - 4, rs + rectNew.right + 12, center + hl + 4);
                canvas.drawRoundRect(rect, 4, 4, paintRect);
                Paint.FontMetrics fm1 = mPaints[1].getFontMetrics();
                float baseY = center - (fm1.top + fm1.bottom) / 2 - 3;
                canvas.drawText(NEW, rs + 6, baseY, mPaints[1]);
            } else {
                String title = getTitle(text, width);
                canvas.drawText(title, 0, mY, mPaints[0]);
            }

            //移动到最上面
            if (mY <= 0 - rectText.bottom) {
                mY = getMeasuredHeight() - rectText.top;
                mIndex++;
                isPaused = false;
            }
            //移动到中间
            if (!isPaused && mY <= getMeasuredHeight() / 2 - (rectText.top + rectText.bottom) / 2) {
                isMove = false;
                isPaused = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        postInvalidate();
                        isMove = true;
                    }
                }, mInterval);
            }
            mY -= mSpeed;
            //循环使用数据
            if (mIndex == mTexts.size()) {
                mIndex = 0;
            }
            //如果是处于移动状态时的,则延迟绘制
            //计算公式为一个比例,一个时间间隔移动组件高度,则多少毫秒来移动1像素
            if (isMove) {
                postInvalidateDelayed(2);
            }
        }
    }

    private String getTitle(String text, int last) {
        float len = mPaints[0].measureText(text);
        if (TextUtils.isEmpty(text) || len <= last) {
            return text;
        }
        int low = 1;
        int high = text.length();
        while (low < high) {
            if (high - low == 1) {
                high = low;
            } else {
                int mid = (low + high) / 2;
                float lm = mPaints[0].measureText(text.substring(0, mid));
                if (lm <= last) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
        }
        return text.substring(0, low);
    }

    public int sp(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
