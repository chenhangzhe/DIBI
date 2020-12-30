package cn.suozhi.DiBi.c2c.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.GlideUtil;

public class ComplaintPhotoAdapter extends AbsRecyclerAdapter<String> {

    public ComplaintPhotoAdapter(Context context) {
        super(context, R.layout.item_photo);
    }

    @Override
    public void onBindHolder(AbsRecyclerAdapter.RecyclerHolder holder, String d, int position) {
        holder.bindImageViewGlide(R.id.iv_photo,d,GlideUtil.getOption());
    }
}
