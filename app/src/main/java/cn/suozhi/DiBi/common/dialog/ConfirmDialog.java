package cn.suozhi.DiBi.common.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;

/**
 * 确认对话框
 */
public class ConfirmDialog extends BaseDialog implements View.OnClickListener {

    public static ConfirmDialog newInstance(String title, String cancel, String confirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("cancel", cancel);
        bundle.putString("confirm", confirm);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        dialog.getWindow().setWindowAnimations(R.style.animScaleCenter);//进入退出动画

        TextView tvTitle = v.findViewById(R.id.tv_dgcTitle);
        TextView tvCancel = v.findViewById(R.id.tv_dgcCancel);
        TextView tvConfirm = v.findViewById(R.id.tv_dgcConfirm);

        Bundle arg = getArguments();
        tvTitle.setText(arg.getString("title"));
        tvCancel.setText(arg.getString("cancel"));
        tvConfirm.setText(arg.getString("confirm"));
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v);
        }
        dismiss();
    }
}
