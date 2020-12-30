package cn.suozhi.DiBi.common.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.QRCodeUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;

public class InvitePosterDialog extends BaseDialog implements View.OnClickListener, View.OnLongClickListener {

    private ConstraintLayout cl;
    private ImageView ivPoster;
    private Bitmap qrCode;
    private String link;

    public static InvitePosterDialog newInstance(String link, String poster, float height, float qr,
                                                 float left, float top) {
        InvitePosterDialog dialog = new InvitePosterDialog();
        Bundle bundle = new Bundle();
        bundle.putString("link", link);//二维码
        bundle.putString("poster", poster);//海报链接
        bundle.putFloat("height", height);//海报高度比例
        bundle.putFloat("qr", qr);//二维码宽度比例
        bundle.putFloat("left", left);//二维码X轴距离
        bundle.putFloat("top", top);//二维码Y轴距离
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_invite_poster, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画

        v.findViewById(R.id.tv_dgIpSave).setOnClickListener(this);
        cl = v.findViewById(R.id.cl_dgIpPoster);
        ivPoster = v.findViewById(R.id.iv_dgIpPoster);
        View vd = v.findViewById(R.id.d_dgIp);
        ImageView ivQr = v.findViewById(R.id.iv_dgIpQr);
        ivPoster.setOnLongClickListener(this);

        Bundle arg = getArguments();
        float height = arg.getFloat("height", 1.35F);
        float qr = arg.getFloat("qr", 0.15F);
        float left = arg.getFloat("left", 0.77F);
        float top = arg.getFloat("top", 0.88F);
        int pw = Util.getPhoneWidth(getActivity());

        //设置海报宽高
        ConstraintLayout.LayoutParams lpp = (ConstraintLayout.LayoutParams) ivPoster.getLayoutParams();
        int h = Math.round(pw * height);
        lpp.dimensionRatio = "H," + pw + ":" + h;
        ivPoster.setLayoutParams(lpp);

        //设置二维码中心点
        ConstraintLayout.LayoutParams lpd = (ConstraintLayout.LayoutParams) vd.getLayoutParams();
        lpd.horizontalBias = left;
        lpd.verticalBias = top;
        vd.setLayoutParams(lpd);

        //设置二维码宽度
        int qw = Math.round((pw - Util.dp2px(getActivity(), 50)) * qr);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) ivQr.getLayoutParams();
        lp.width = qw;
        lp.height = qw;
        ivQr.setLayoutParams(lp);

        link = getArguments().getString("link");
        String poster = getArguments().getString("poster");
        GlideUtil.glide(getActivity(), ivPoster, poster);
        ivQr.post(() -> {
            qrCode = QRCodeUtil.syncEncodeQRCode(link, pw / 3);
            ivQr.setImageBitmap(qrCode);
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        if (qrCode == null) {
            ToastUtil.initToast(getActivity(), R.string.dataAbnormal);
            return;
        }
        try {
            String filePath = Util.getDCIMPath() + "/invite" + ".jpg";
            if (BitMapUtil.saveView2File(cl, qrCode, filePath)) {
                BitMapUtil.insertGallery(getActivity(), filePath);
                ToastUtil.initToast(getActivity(), R.string.saveSuc);
            } else {
                ToastUtil.initToast(getActivity(), R.string.saveFail);
            }
        } catch (Exception e) {}
    }

    @Override
    public boolean onLongClick(View v) {
        if (!TextUtils.isEmpty(link)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            return true;
        }
        return false;
    }
}
