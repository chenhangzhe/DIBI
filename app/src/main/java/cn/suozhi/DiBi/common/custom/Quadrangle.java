package cn.suozhi.DiBi.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import cn.suozhi.DiBi.R;

/**
 * 平行四边形
 */
public class Quadrangle extends View {

    private int color;
    private Paint paint;
    private Path path;

    public Quadrangle(Context context) {
        this(context, null);
    }

    public Quadrangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        init();
    }

    private void getAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.Quadrangle);
            color = ta.getColor(R.styleable.Quadrangle_quadColor, Color.WHITE);
            ta.recycle();
        } else {
            color = Color.WHITE;
        }
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        path.reset();
        path.moveTo(w * 0.4F, 0);
        path.lineTo(w, 0);
        path.lineTo(w * 0.6F, h);
        path.lineTo(0, h);
        path.close();
        canvas.drawPath(path, paint);
    }
}
