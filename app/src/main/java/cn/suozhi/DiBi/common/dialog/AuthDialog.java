package cn.suozhi.DiBi.common.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.ResUtils;

/**
 * 钱包页面 认证的dialog
 */
public class AuthDialog extends BaseDialog implements View.OnClickListener {

    private String content;
    private String top;
    private String bottom;

    public static AuthDialog newInstance(String content, String top, String bottom) {
        AuthDialog dialog = new AuthDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        bundle.putString("top", top);
        bundle.putString("bottom", bottom);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_is_auth, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(ResUtils.getDimensionPixelSize(R.dimen.auth_dialog_width), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画
        initData();
        initView(v);
        return v;
    }

    private void initData() {
        Bundle arg = getArguments();
        content = arg.getString("content");
        top = arg.getString("top");
        bottom = arg.getString("bottom");
    }

    private void initView(View v) {
        TextView tvContent = v.findViewById(R.id.content);
        TextView tvAuth = v.findViewById(R.id.tv_auth);
        TextView tvThink = v.findViewById(R.id.tv_think);

        tvContent.setText(content);
        tvAuth.setText(top);
        tvThink.setText(bottom);

        tvAuth.setOnClickListener(this);
        tvThink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_auth:
                onItemClickListener.onItemClick(v);
                dismiss();
                break;
            case R.id.tv_think:
                dismiss();
                break;
        }
    }
}
