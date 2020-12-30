package cn.suozhi.DiBi.common.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 编辑昵称
 */
public class DoVoteDialog extends BaseDialog implements View.OnClickListener, TextWatcher {

    private EditText etAlias;
    private TextView tvConfirm,tvTitle,tvHint;
    private String alias,avail;
    private int pid;

    public static DoVoteDialog newInstance(String alias,int pid,String avail) {
        DoVoteDialog dialog = new DoVoteDialog();
        Bundle bundle = new Bundle();
        bundle.putString("alias", alias);
        bundle.putInt("pid", pid);
        bundle.putString("avail", avail);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_do_vote, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        dialog.getWindow().setWindowAnimations(R.style.animScaleCenter);//进入退出动画
        dialog.setCanceledOnTouchOutside(false);

        v.findViewById(R.id.iv_dgEaClose).setOnClickListener(this);
        tvTitle = v.findViewById(R.id.tv_dgEaTitle);
        etAlias = v.findViewById(R.id.et_dgEaAlias);

        tvConfirm = v.findViewById(R.id.tv_dgEaConfirm);
        tvConfirm.setOnClickListener(this);

        alias = getArguments().getString("alias");
        pid = getArguments().getInt("pid");
        avail = getArguments().getString("avail");
        etAlias.addTextChangedListener(this);
        tvTitle.setText(getString(R.string.dg_vote_dialog_title) + " " + alias);
        etAlias.setHint(getString(R.string.dg_vote_dialog_tip) + avail);

        tvHint = v.findViewById(R.id.tv_ddv_hint);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dgEaClose:
                Util.hideKeyboard(etAlias);
                dismiss();
                break;
            case R.id.tv_dgEaConfirm:
                Util.editRemoveIllegal(etAlias);
                if (v.isSelected()) {
                    if (onItemClickListener != null) {
                        String et = etAlias.getText().toString();
                        if(!TextUtils.isEmpty(et)){
                            v.setTag(et);
                            tvHint.setText(null);
                        } else {
                            v.setTag("-1");
                            tvHint.setText(getString(R.string.dg_vote_dialog_edit_hint));
                        }
                        onItemClickListener.onItemClick(v);
                    }
                    Util.hideKeyboard(etAlias);
                    dismiss();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        tvConfirm.setSelected(s.length() > 0 && !s.toString().equals(alias));
    }
}
