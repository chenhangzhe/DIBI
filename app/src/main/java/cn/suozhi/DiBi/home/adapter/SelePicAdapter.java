package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;

/**
 * 创建时间：2019-07-26 17:21
 * 作者：Lich_Cool
 * 邮箱：licheng@ld.chainsdir.com
 * 功能描述：
 */
public class SelePicAdapter extends AbsRecyclerAdapter<String> {

    public SelePicAdapter(Context context) {
        super(context, R.layout.item_sele_pic);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, String d, int position) {
        if (Util.hasText(d)) {
            holder.bindImageViewGlide(R.id.img_q,d);
            holder.setViewVisible(R.id.img_close, true);
        } else {
            holder.bindImageViewGlide(R.id.img_q, R.mipmap.icon_img_add);
            holder.setViewVisible(R.id.img_close, false);
        }
        holder.setOnClickListener(R.id.img_close);
    }
}
