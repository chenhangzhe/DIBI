package cn.suozhi.DiBi.c2c.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.text.DecimalFormat;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import cn.suozhi.DiBi.R;

/**
 * 倒计时TextView
 * Created by weijing on 2017-08-21 14:43.
 */

public class TimerTextView extends AppCompatTextView {


    /**
     * 最后时间
     */
    private long mLastMillis;
    /**
     * 间隔时间差(两次发送handler)
     */
    private long mIntervalMillis = 1_000;
    /**
     * 开始倒计时code
     */
    private final int MSG_WHAT_START = 10_010;
    private DecimalFormat decimalFormat;
    /**
     * 前拼接字符串
     */
    private String mContentBefore = "";
    /**
     * 后拼接字符串
     */
    private String mContentAfter = "";
    private String formatTime = "";
    /**
     * 倒计时结束后显示的内容
     */
    private String mOutOfDateText;
    /**
     * 倒计时是否完成
     */
    private boolean isFinished;
    private Context context;


    public TimerTextView(Context context) {
        super(context);
        this.context = context;
    }


    public TimerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public TimerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_WHAT_START:
                    TimerTextView.this.handleMessage();
                    break;
            }
        }
    };

    private void handleMessage() {
        long currentTimeMillis = System.currentTimeMillis();
        if (mLastMillis > currentTimeMillis + 1000) {
            //1000ms以内就认为倒计时结束
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_START, mIntervalMillis);
            formatTime = getFormatTime(mLastMillis - currentTimeMillis);

            setText(Html.fromHtml(getTextContent(formatTime)));

            Log.e("ll", currentTimeMillis + "--" + formatTime);

        } else {
            isFinished = true;
            if (TextUtils.isEmpty(mOutOfDateText)) {
                setText(Html.fromHtml(getTextContent("<font color='#F79773'>--</font>")));
            } else {
                setText(mOutOfDateText);
            }
        }
    }

    /**
     * 获取格式后的
     *
     * @param formatTime
     * @return
     */
    private String getTextContent(String formatTime) {

        return mContentBefore + formatTime + mContentAfter;
    }

    private String getFormatTime(long distanceMillis) {
        StringBuffer stringBuffer = new StringBuffer();

        final long seconds = distanceMillis / 1000;
        final long minutes = seconds / 60;
        final long hours = minutes / 60;
        final long days = hours / 24;
        if (days > 0) {
            stringBuffer.append(days).append(context.getString(R.string.str_day));
        }
        if (hours > 0) {
            stringBuffer.append(format(hours % 24)).append(context.getString(R.string.str_hour));
        }
        if (minutes > 0) {
            stringBuffer.append(format(minutes % 24)).append(context.getString(R.string.str_minutes));
        }
        if (seconds > 0) {
            stringBuffer.append(format(seconds % 60)).append(context.getString(R.string.str_seconds));
        }

//        return "<font color='#F79773'>" + stringBuffer.toString() + "</font>";
        return "<font color='#FFFFFF'>" + stringBuffer.toString() + "</font>";
    }

    private String format(long number) {
        if (decimalFormat == null)
            decimalFormat = new DecimalFormat("00");

        return decimalFormat.format(number);
    }


    /**
     * 设置倒计时时间
     *
     * @param millis 毫秒值
     */
    public void setLastMillis(long millis) {
        mLastMillis = millis;
        if (mLastMillis < System.currentTimeMillis()) {
            Log.e("e", "lastTimeMillis must bigger ran currentTimeMillis:" + System.currentTimeMillis());
        }
    }

    /**
     * 设置前后拼接字符串，before + formatTime + after
     *
     * @param before
     * @param after
     */
    public void setContentBeforAfter(String before, String after) {
        mContentBefore = before;
        mContentAfter = after;
    }

    public void setOutOfDateText(String outOfDateText) {
        mOutOfDateText = outOfDateText;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        mHandler.sendEmptyMessage(MSG_WHAT_START);
    }

    /**
     * 是否完成
     *
     * @return
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * 设置是否完成
     *
     * @param isFinished
     */
    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }


    /**
     * 停止倒计时
     */
    public void stop() {
        mHandler.removeMessages(MSG_WHAT_START);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_WHAT_START);
    }
}

