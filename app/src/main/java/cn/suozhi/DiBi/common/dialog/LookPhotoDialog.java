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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.GlideUtil;

/**
 * 交易须知的dialog
 */
public class LookPhotoDialog extends BaseDialog implements View.OnClickListener {

    private String content;
    private String qrCode;


    public static LookPhotoDialog newInstance(String qrCode) {
        LookPhotoDialog dialog = new LookPhotoDialog();
        Bundle bundle = new Bundle();
        bundle.putString("qrCode",qrCode);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_look_photo, window.findViewById(android.R.id.content), false);


        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画
        initData();
        initView(v);
        return v;
    }

    private void initData() {
        Bundle arg = getArguments();
        qrCode = arg.getString("qrCode");

    }

    private void initView(View v) {

        TextView tvComfirm = v.findViewById(R.id.tv_comfirm);
        ImageView delete = v.findViewById(R.id.iv_delete);
        ImageView ivCotent = v.findViewById(R.id.tv_content);
        GlideUtil.glidePE(getActivity(),ivCotent,qrCode);

        tvComfirm.setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comfirm:
                dismiss();
                break;
            case R.id.iv_delete:
                dismiss();
                break;

        }
    }
}
