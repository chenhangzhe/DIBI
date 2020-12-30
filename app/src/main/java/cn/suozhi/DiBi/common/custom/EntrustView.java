package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.QuoteEntity;

/**
 * 委托单 -- 一定要设置layoutId、一定要设置小数位
 */
public class EntrustView extends LinearLayout implements View.OnClickListener {

    private int layoutId, count;
    private int start, sort, direction;//起始位置 / 排序方式 / 底色方向
    private boolean leftPrice, removeZero;//价格显示在左侧 / 移除末尾的0
    private int colorPrice, colorAmount, colorBack;

    private int itemHeight;
    private int pp = 4, ap = 4;//小数位
    private List<TextView> tvp;
    private List<TextView> tva;
    private List<DepthView> tvb;

    private List<QuoteEntity> list;
    private double max;//数量最大值 用于绘制底部颜色长度

    private OnItemClickListener onItemClickListener;

    public EntrustView(Context context) {
        this(context, null);
    }

    public EntrustView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
    }

    private void getAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.EntrustView);
            layoutId = ta.getResourceId(R.styleable.EntrustView_enLayout, 0);
            count = ta.getInteger(R.styleable.EntrustView_enCount, 1);
            start = ta.getInt(R.styleable.EntrustView_enStart, 0);
            sort = ta.getInt(R.styleable.EntrustView_enSort, 0);
            direction = ta.getInt(R.styleable.EntrustView_enBackDirection, 0);
            leftPrice = ta.getBoolean(R.styleable.EntrustView_enLeftPrice, true);
            removeZero = ta.getBoolean(R.styleable.EntrustView_enRemoveZero, true);
            colorPrice = ta.getColor(R.styleable.EntrustView_enPriceColor, Color.BLACK);
            colorAmount = ta.getColor(R.styleable.EntrustView_enAmountColor, Color.BLACK);
            colorBack = ta.getColor(R.styleable.EntrustView_enBackColor, Color.WHITE);
            ta.recycle();
        } else {
            layoutId = 0;
            count = 1;
            start = sort = direction = 0;
            leftPrice = true;
            removeZero = true;
            colorPrice = colorAmount = Color.BLACK;
            colorBack = Color.WHITE;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec) / count;
        } else {
            height = 0;
        }
        if (itemHeight != height) {
            itemHeight = height;
            init();
        }
    }

    public void setPoint(int pp, int ap) {
        if (pp < 0) {
            pp = 0;
        }
        this.pp = pp;
        if (ap < 0) {
            ap = 0;
        }
        this.ap = ap;
        if (tvp == null) {
            init();
        }
    }

    private void init() {
//        Log.e("loge", "init: ");
        if (tvp == null) {
            tvp = new ArrayList<>();
        } else {
            tvp.clear();
        }
        if (tva == null) {
            tva = new ArrayList<>();
        } else {
            tva.clear();
        }
        if (tvb == null) {
            tvb = new ArrayList<>();
        } else {
            tvb.clear();
        }
        removeAllViews();

        if (layoutId == 0) {
            return;
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                itemHeight > 0 ? itemHeight : LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < count; i++) {
            View child = LayoutInflater.from(getContext()).inflate(layoutId, null);
            DepthView dv = getBackView(child);
            if (dv != null) {
                dv.setOnClickListener(this);
                tvb.add(dv.setColor(colorBack).setStartLeft(direction == 0));
            }
            TextView tp = getTextView(child, leftPrice ? 0 : 1);
            if (tp != null) {
                tp.setTextColor(colorPrice);
                tvp.add(tp);
            }
            TextView ta = getTextView(child, leftPrice ? 1 : 0);
            if (ta != null) {
                ta.setTextColor(colorAmount);
                tva.add(ta);
            }

            if (itemHeight > 0) {
                addView(child, lp);
            } else {
                addView(child);
            }
        }
    }

    private DepthView getBackView(View child) {
        if (child instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) child;
            DepthView dv = null;
            for (int i = 0; i < g.getChildCount(); i++) {
                if (g.getChildAt(i) instanceof DepthView) {
                    dv = (DepthView) g.getChildAt(i);
                    break;
                }
            }
            return dv;
        }
        return null;
    }

    private TextView getTextView(View child, int index) {
        if (child instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) child;
            int in = -1;
            TextView tv = null;
            for (int i = 0; i < g.getChildCount(); i++) {
                if (g.getChildAt(i) instanceof TextView) {
                    if (++in == index) {
                        tv = (TextView) g.getChildAt(i);
                        break;
                    }
                }
            }
            return tv;
        }
        return null;
    }

    public void setData(List<QuoteEntity> list) {
        if (list != null) {
            this.list = list;
        }
        update();
    }

    public void setData(List<QuoteEntity> list, double max) {
        if (list != null) {
            this.list = list;
        }
        if (max > 0) {
            this.max = max;
        }
        update();
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
        update();
    }

    private void update() {
//        Log.e("loge", "update: ");
        if (list == null) {
            init();
            return;
        }
        if (tvp == null) {
            init();
        }
        if (sort == 0) {
            Collections.sort(list, (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
        } else {
            Collections.sort(list, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
        }
        double max = getMaxAmount();
        for (int i = 0; i < count; i++) {
            boolean isShow = i < list.size();
            int index = start == 0 ? i : count - i - 1;
            DepthView dv = tvb.get(index);
            if (isShow) {
                double vol = list.get(i).getVolume();
                dv.setTag(i);
                dv.setDepth(vol / max);
                tvp.get(index).setText(getNumber(list.get(i).getPrice(), pp));
                tva.get(index).setText(getNumber(vol, ap));
            } else {
                dv.setTag(null);
                dv.setDepth(0);
                tvp.get(index).setText(null);
                tva.get(index).setText(null);
            }
        }
    }

    private String getNumber(double num, int point) {
        return removeZero ? AppUtil.roundRemoveZero(num, point) : Util.formatDecimal(num, point);
    }

    private double getMaxAmount() {
        if (max > 0) {
            return max;
        }
        double m = 0;
        for (int i = 0; i < Math.min(count, list.size()); i++) {
            if (list.get(i).getVolume() > m) {
                m = list.get(i).getVolume();
            }
        }
        return m;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null || onItemClickListener == null) {
            return;
        }
        int i = (int) tag;
        boolean hasValue = i >= 0 && i < list.size();
        double price = hasValue ? list.get(i).getPrice() : -1;
        double amount = hasValue ? list.get(i).getVolume() : -1;
        onItemClickListener.onItemClick((View) v.getParent(), hasValue, price, amount);
    }

    public EntrustView setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public interface OnItemClickListener {
        void onItemClick(View layout, boolean hasValue, double price, double amount);
    }
}
