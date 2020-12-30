package cn.suozhi.DiBi.common.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.custom.Quadrangle;

/**
 * 加载中对话框
 */
public class LoadingDialog extends BaseDialog implements DialogInterface.OnKeyListener {

    private AnimatorSet set;

    public static LoadingDialog newInstance() {
        return newInstance(false);
    }

    public static LoadingDialog newInstance(boolean isForce) {
        LoadingDialog dialog = new LoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isForce", isForce);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);//对话框外不可取消
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_loading, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.2F;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        Quadrangle q0 = v.findViewById(R.id.q_loading0);
        Quadrangle q1 = v.findViewById(R.id.q_loading1);
        Quadrangle q2 = v.findViewById(R.id.q_loading2);

        ObjectAnimator a0 = ObjectAnimator.ofFloat(q0, "alpha", 1, 0.4F, 0.1F, 0.4F, 1);
        ObjectAnimator a1 = ObjectAnimator.ofFloat(q1, "alpha", 0.4F, 1, 0.4F, 0.1F, 0.4F);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(q2, "alpha", 0.1F, 0.4F, 1, 0.4F, 0.1F);
        a0.setRepeatCount(ValueAnimator.INFINITE);
        a1.setRepeatCount(ValueAnimator.INFINITE);
        a2.setRepeatCount(ValueAnimator.INFINITE);
        set = new AnimatorSet().setDuration(2500);
        set.setInterpolator(new LinearInterpolator());
        set.playTogether(a0, a1, a2);
        set.start();

        if (getArguments().getBoolean("isForce")) {
            getDialog().setOnKeyListener(this);
        }

        return v;
    }

    @Override
    public void dismiss() {
        if (set != null) {
            set.cancel();
        }
        super.dismiss();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
