package cn.suozhi.DiBi.wallet.adapter;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.wallet.model.CoinSliderEnity;

public class CoinSideBar extends View {
	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
//	按住改变背景色
	private boolean showBkg;
	//右侧内容
//	public static String[] b = { "热", "A", "B", "C", "D", "E", "F", "G", "H",
//		"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
//		"V", "W", "X", "Y", "Z" };
//	/**被选中位置*/
	private List<CoinSliderEnity> list = new ArrayList<>();

	int choose = -1;
	private Paint paint = new Paint();

	public CoinSideBar(Context context) {
		super(context);
	}

	public CoinSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CoinSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public void setData(List<CoinSliderEnity> list){
		this.list = list;
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (list.size()!=0&&list != null){
			if(showBkg){
				canvas.drawColor(ResUtils.getColor(R.color.gyF9));
			}

			float height = getHeight();
			float width = getWidth();
//		计算单个字母高度
			float singleHeight = height / (float)(list.size());
			for(int i = 0; i < list.size(); i++){
				paint.setColor(ResUtils.getColor(R.color.text_color_dark));
				paint.setTextSize(30);
				paint.setAntiAlias(true);
				//加粗
				paint.setFakeBoldText(true);
				if (i == choose) {
//				选中的颜色
//					paint.setColor(ResUtils.getColor(R.color.purple77));
					paint.setColor(ResUtils.getColor(R.color.color_1888FE));
				}
//			设置文本坐标
				float xPos = width / 2 - paint.measureText(list.get(i).getSort()) / 2;
				float yPos = singleHeight * i + singleHeight;
				canvas.drawText(list.get(i).getSort(), xPos, yPos, paint);
				paint.reset();
			}
		}

	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * list.size());
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < list.size()) {
					listener.onTouchingLetterChanged(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < list.size()) {
					listener.onTouchingLetterChanged(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}
	//接口 触摸监听
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(int s);
	}
}
