package cn.suozhi.DiBi.common.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.Util;

public class GoogleVerifyDialog extends BaseDialog implements View.OnClickListener,
        TextView.OnEditorActionListener {

    private EditText etCode;
    private TextView tveCode;

    public static GoogleVerifyDialog newInstance() {
        return new GoogleVerifyDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        getDialog().setCanceledOnTouchOutside(false);
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_google_verify, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画

        v.findViewById(R.id.iv_dgGvClose).setOnClickListener(this);
        etCode = v.findViewById(R.id.et_dgGvCode);
        tveCode = v.findViewById(R.id.et_dgGvCodeError);
        v.findViewById(R.id.tv_dgGvConfirm).setOnClickListener(this);
        Util.editListenerError(etCode, tveCode);
        etCode.setOnEditorActionListener(this);
        etCode.requestFocus();
        Util.showKeyboard(etCode);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dgGvClose:
                dismiss();
                break;
            case R.id.tv_dgGvConfirm:
                if (etCode.length() < 6) {
                    tveCode.setText(R.string.inputNum6);
                    etCode.requestFocus();
                    etCode.setSelection(etCode.length());
                } else {
                    if (onItemClickListener != null) {
                        etCode.setTag(etCode.getText().toString());
                        onItemClickListener.onItemClick(etCode);
                    }
                    dismiss();
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (etCode.length() >= 6) {
                if (onItemClickListener != null) {
                    v.setTag(etCode.getText().toString());
                    onItemClickListener.onItemClick(v);
                }
                dismiss();
            } else {
                Util.hideKeyboard(etCode);
            }
            return true;
        }
        return false;
    }

    @Override
    public void dismiss() {
        Util.hideKeyboard(etCode);
        super.dismiss();
    }
}
