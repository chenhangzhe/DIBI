package cn.suozhi.DiBi.common.base;

import android.app.Dialog;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * 所有Dialog的基类 -- 继承DialogFragment
 */
public abstract class BaseDialog extends DialogFragment {

    protected OnItemClickListener onItemClickListener;
    protected OnShowListener onShowListener;
    protected OnDismissListener onDismissListener;

    public BaseDialog setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public interface OnItemClickListener {
        void onItemClick(View v);
    }

    public BaseDialog setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
        return this;
    }

    public interface OnShowListener {
        void onShow();
    }

    public BaseDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public BaseDialog show(FragmentActivity context) {
        return show(context.getSupportFragmentManager());
    }

    public BaseDialog show(FragmentManager manager) {
        try {
            Dialog dialog = getDialog();
            if (dialog == null || !dialog.isShowing()) {
                show(manager, "dialog");
            }
            if (onShowListener != null) {
                onShowListener.onShow();
            }
        } catch (Exception e){}
        return this;
    }

    @Override
    public void dismiss() {
        try {
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
            super.dismiss();
        } catch (Exception e){}
    }
}
