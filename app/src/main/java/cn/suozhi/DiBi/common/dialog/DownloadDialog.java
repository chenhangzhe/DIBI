package cn.suozhi.DiBi.common.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;

public class DownloadDialog extends BaseDialog implements DialogInterface.OnKeyListener, View.OnClickListener {

    private TextView tvTitle;
    private ProgressBar pb;
    private TextView tvInstall;

    public static DownloadDialog newInstance(String title) {
        DownloadDialog dialog = new DownloadDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        dialog.setCanceledOnTouchOutside(false);//对话框外不可取消
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        dialog.getWindow().setWindowAnimations(R.style.animScaleCenter);//进入退出动画

        dialog.setOnKeyListener(this);

        tvTitle = v.findViewById(R.id.tv_dgDownTitle);
        pb = v.findViewById(R.id.pb_dgDown);
        tvInstall = v.findViewById(R.id.tv_dgDownInstall);
        tvTitle.setText(getArguments().getString("title"));
        tvInstall.setOnClickListener(this);

        return dialog;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK;
    }

    public ProgressBar getProgressBar() {
        return pb;
    }

    public TextView getViewInstall() {
        return tvInstall;
    }

    /**
     * 通知已下载完成
     */
    public void notifyDone() {
        if (tvInstall != null) {
            tvInstall.setText(R.string.install);
            tvInstall.setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_dgDownInstall && v.isSelected()) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v);
            }
        }
    }
}
