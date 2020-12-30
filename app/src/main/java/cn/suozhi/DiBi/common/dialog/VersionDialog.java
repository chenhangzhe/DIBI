package cn.suozhi.DiBi.common.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;

public class VersionDialog extends BaseDialog implements DialogInterface.OnKeyListener,
        View.OnClickListener, View.OnLongClickListener {

    private int clickCount = 0;//标题点击数量
    private boolean isExit = false;//退出标识
    private Handler handler = new Handler();

    public static VersionDialog newInstance(String title, String content, String confirm, boolean force) {
        VersionDialog dialog = new VersionDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("confirm", confirm);
        bundle.putBoolean("force", force);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_version, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        dialog.setCanceledOnTouchOutside(false);//对话框外不可取消
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        dialog.getWindow().setWindowAnimations(R.style.animScaleCenter);//进入退出动画

        ImageView ivClose = v.findViewById(R.id.iv_dgVerClose);
        TextView tvTitle = v.findViewById(R.id.tv_dgVerTitle);
        TextView tvContent = v.findViewById(R.id.tv_dgVerContent);
        TextView tvConfirm = v.findViewById(R.id.tv_dgVerConfirm);

        Bundle arg = getArguments();
        tvTitle.setText(arg.getString("title"));
        tvContent.setText(arg.getString("content"));
        tvConfirm.setText(arg.getString("confirm"));

        //强制情况下隐藏关闭按钮
        if (arg.getBoolean("force")) {
            dialog.setOnKeyListener(this);
            ivClose.setVisibility(View.GONE);
        } else {
            ivClose.setVisibility(View.VISIBLE);
        }

        tvTitle.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvConfirm.setOnLongClickListener(this);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dgVerClose:
                dismiss();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v);
                }
                break;
            case R.id.tv_dgVerTitle:
                clickCount++;
                break;
            case R.id.tv_dgVerConfirm:
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (clickCount == 3) {
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                System.exit(0);
            } else {
                ToastUtil.initToast(getActivity(), getString(R.string.pressExit) + " " +
                        Util.getAppName(getActivity()));
                isExit = true;
                handler.postDelayed(() -> isExit = false, 5000);//5秒内再按后退键真正退出
            }
            return true;
        }
        return false;
    }
}
