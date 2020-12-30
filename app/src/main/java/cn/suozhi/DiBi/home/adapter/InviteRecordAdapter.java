package cn.suozhi.DiBi.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.home.model.IeoInviteEntity;
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;

/**
 * 邀请记录适配器
 */
public class InviteRecordAdapter extends AbsRecyclerAdapter<IeoInviteEntity.DataBean.InviteUserListBean.RecordsBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public InviteRecordAdapter(Context context, int height , OnCallbackListener callbackListener) {
        super(context, R.layout.item_invite_record, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
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
    public void onBindHolder(RecyclerHolder holder, IeoInviteEntity.DataBean.InviteUserListBean.RecordsBean d, int position) {
        holder.bindTextView(R.id.tv_item_invite1 , d.getCreatedDate());
        holder.bindTextView(R.id.tv_item_invite2 , d.getUserCode());
        holder.bindTextView(R.id.tv_item_invite3 , d.getIeoExchangeAmountStr());
        callbackListener.onCallback();
    }

}
