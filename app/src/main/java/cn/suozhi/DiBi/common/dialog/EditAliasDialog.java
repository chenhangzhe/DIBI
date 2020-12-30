package cn.suozhi.DiBi.common.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 编辑昵称
 */
public class EditAliasDialog extends BaseDialog implements View.OnClickListener, TextWatcher {

    private EditText etAlias;
    private TextView tvConfirm;
    private String alias;

    public static EditAliasDialog newInstance(String alias) {
        EditAliasDialog dialog = new EditAliasDialog();
        Bundle bundle = new Bundle();
        bundle.putString("alias", alias);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_alias, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        dialog.getWindow().setWindowAnimations(R.style.animScaleCenter);//进入退出动画
        dialog.setCanceledOnTouchOutside(false);

        v.findViewById(R.id.iv_dgEaClose).setOnClickListener(this);
        etAlias = v.findViewById(R.id.et_dgEaAlias);
        tvConfirm = v.findViewById(R.id.tv_dgEaConfirm);
        tvConfirm.setOnClickListener(this);

        alias = getArguments().getString("alias");
        etAlias.addTextChangedListener(this);

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
        tvConfirm.setSelected(s.length() > 0 && !s.toString().equals(alias));
    }
}
