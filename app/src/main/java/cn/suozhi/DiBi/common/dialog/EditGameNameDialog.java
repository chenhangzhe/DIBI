package cn.suozhi.DiBi.common.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 编辑游戏昵称
 */
public class EditGameNameDialog extends BaseDialog implements View.OnClickListener, TextWatcher {

    private EditText etAlias;
    private TextView tvConfirm;

    public static EditGameNameDialog newInstance() {
        return new EditGameNameDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_game_name, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        dialog.getWindow().setWindowAnimations(R.style.animScaleCenter);//进入退出动画
        dialog.setCanceledOnTouchOutside(false);

        v.findViewById(R.id.iv_dgEgnClose).setOnClickListener(this);
        etAlias = v.findViewById(R.id.et_dgEgnAlias);
        tvConfirm = v.findViewById(R.id.tv_dgEgnConfirm);
        tvConfirm.setOnClickListener(this);

        etAlias.addTextChangedListener(this);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dgEgnClose:
                Util.hideKeyboard(etAlias);
                dismiss();
                break;
            case R.id.tv_dgEgnConfirm:
                Util.editRemoveIllegal(etAlias);
                if (v.isSelected()) {
                    if (onItemClickListener != null) {
                        v.setTag(etAlias.getText().toString());
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
        tvConfirm.setSelected(s.length() > 0);
    }
}
