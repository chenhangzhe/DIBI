package cn.suozhi.DiBi.c2c.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.BitMapUtil;

public class UpLoadImagAdapter extends AbsRecyclerAdapter<String> {
    //最多上传三张
    private int maxNums = 3;


    public UpLoadImagAdapter(Context context) {
        super(context, R.layout.item_img);
    }


    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size() < maxNums ? data.size() : maxNums;
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, String d, int position) {
        LinearLayout llyAddPic = (LinearLayout) holder.getView(R.id.lly_add_photo);
        RelativeLayout rlyPic = (RelativeLayout) holder.getView(R.id.rly_photo);

        holder.setOnClickListener(R.id.lly_add_photo);
        holder.setOnClickListener(R.id.iv_delete);

        if (TextUtils.isEmpty(d)) {
            llyAddPic.setVisibility(View.VISIBLE);
            rlyPic.setVisibility(View.GONE);
        } else {
            llyAddPic.setVisibility(View.GONE);
            rlyPic.setVisibility(View.VISIBLE);
        }
        holder.bindImageView(R.id.iv_upload_pic,BitMapUtil.decodeBitmap(d, BitMapUtil.K512));
    }
}
