package cn.suozhi.DiBi.wallet.adapter;

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
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;
import cn.suozhi.DiBi.wallet.model.TransferRecordEntity;

/**
 * 充提记录的适配器
 */
public class TransferRecordAdapter extends AbsRecyclerAdapter<TransferRecordEntity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private int type;
    private OnCallbackListener callbackListener;

    public TransferRecordAdapter(Context context, int height, int type, OnCallbackListener callbackListener) {
        super(context, R.layout.item_transfer_record_item, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.type = type;
        this.callbackListener = callbackListener;
    }


    @Override
    public int getItemType(TransferRecordEntity.DataBean.RecordsBean d) {
        return d.getLoadType();
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
    public void onBindHolder(RecyclerHolder holder, TransferRecordEntity.DataBean.RecordsBean d, int position) {
        switch (getItemType(d)) {
            case 0:
                if(d.getTransferType().equals("W")){
                    holder.bindTextView(R.id.tv_buy,getString(R.string.transfer_w));
                } else if(d.getTransferType().equals("D")){
                    holder.bindTextView(R.id.tv_buy,getString(R.string.transfer_d));
                }
                holder.bindTextView(R.id.tv_coin_name,d.getCurrencyCode());
                holder.bindTextView(R.id.tv_uid,"UID：" + d.getTransferUid());
                holder.bindTextView(R.id.tv_account_name,getString(R.string.tra_ada_1) + "：" + d.getPhoneOrEmail());
                holder.bindTextView(R.id.tv_transfer_accounts,getString(R.string.tra_ada_2) + "：" + d.getAmount());
                holder.bindTextView(R.id.tv_transfer_date,d.getCreatedDate());
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }

}
