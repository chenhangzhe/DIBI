package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import cn.suozhi.DiBi.R;


/**
 * Author: wangli
 * Date  : 2018/7/24
 * Description :
 */

public class TimerUtils {

    private TextView tvCode;
    private SendPhoneCodeTimer sendPhoneCodeTimer;

    private Context context;

    public TimerUtils(TextView tvCode, Context context) {
        this.tvCode = tvCode;
        this.context = context;
    }


    private class SendPhoneCodeTimer extends CountDownTimer {
        public SendPhoneCodeTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int remainingTime = (int) millisUntilFinished / 1000;
            if (remainingTime > 0) {
                tvCode.setText( String.format(context.getString(R.string.str_code_resend),remainingTime+ ""));
            }
        }

        @Override
        public void onFinish() {
            tvCode.setEnabled(true);
            tvCode.setText(context.getString(R.string.str_resend));
            reSetCodeBtnStatus();
        }
    }


    /**
     * 开始发送验证码定时器
     */
    public void startSendPhoneCodeTimer() {
        cancelSendPhoneCodeTimer();
        tvCode.setTextColor(ResUtils.getColor(R.color.gy8A));
        tvCode.setEnabled(false);
        sendPhoneCodeTimer = new SendPhoneCodeTimer(120 * 1000, 1000);
        sendPhoneCodeTimer.start();

    }


    /**
     * 取消发送验证码定时器
     */
    public void cancelSendPhoneCodeTimer() {
        if (null != sendPhoneCodeTimer) {
            sendPhoneCodeTimer.cancel();
            sendPhoneCodeTimer = null;
        }
    }


    /**
     * 当接口访问失败时重置按钮状态
     */
    public void reSetCodeBtnStatus() {
        tvCode.setEnabled(true);
        tvCode.setText(context.getString(R.string.str_resend));
//        tvCode.setTextColor(ResUtils.getColor(R.color.purple77));
        tvCode.setTextColor(ResUtils.getColor(R.color.color_1888FE));
        if (sendPhoneCodeTimer != null) {
            sendPhoneCodeTimer.cancel();
        }
    }
}
