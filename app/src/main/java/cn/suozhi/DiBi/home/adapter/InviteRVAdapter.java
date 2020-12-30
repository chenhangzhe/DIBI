package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.InviteEntity;

public class InviteRVAdapter extends AbsRecyclerAdapter<InviteEntity> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public InviteRVAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_invite, R.layout.recycler_commission,
                R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(InviteEntity d) {
        return d.getType();
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, InviteEntity d, int position) {
        switch (getItemType(d)) {
            case 0:
                holder.bindImageViewGlideCirclePE(R.id.iv_inviteItemAvatar, d.getPic(),
                            R.mipmap.img_loading, R.mipmap.img_default_avatar)
                        .bindTextView(R.id.tv_inviteItemUser, Util.addStarInMiddle(d.getUserCode()))
                        .bindTextView(R.id.tv_inviteItemTime, AppUtil.formatDate2y2d(d.getDate()));
                break;
            case 1:
                holder.bindTextView(R.id.tv_comItemCoin, d.getCoin())
                        .bindTextView(R.id.tv_comItemAmount, AppUtil.roundRemoveZero(d.getAmount(), 8))
                        .bindTextView(R.id.tv_comItemCategory,
                                getStringInResId("comType_" + d.getCategory(), "--"))
                        .bindTextView(R.id.tv_comItemTime, AppUtil.formatDate2M2d(d.getDate()));
                break;
            case 2://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
