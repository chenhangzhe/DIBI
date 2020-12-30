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

/**
 * 充提记录的适配器
 */
public class AccountOrderAdapter extends AbsRecyclerAdapter<AccountRecordEnity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private int type;
    private OnCallbackListener callbackListener;

    public AccountOrderAdapter(Context context, int height, int type, OnCallbackListener callbackListener) {
        super(context, R.layout.item_account_order, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.type = type;
        this.callbackListener = callbackListener;
    }


    @Override
    public int getItemType(AccountRecordEnity.DataBean.RecordsBean d) {
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
    public void onBindHolder(RecyclerHolder holder, AccountRecordEnity.DataBean.RecordsBean data, int position) {

        switch (getItemType(data)) {
            case 0:
                holder.bindTextView(R.id.tv_time, data.getCreatedDate());
                holder.bindTextView(R.id.tv_coin, data.getCode());
                holder.bindTextView(R.id.tv_fee, getString(R.string.str_fee) + ": " + AppUtil.roundRemoveZero(data.getFee(), 8));
                holder.bindTextView(R.id.tv_address, "Txid : " + (TextUtils.isEmpty(data.getBlockchainTxId())? "" : data.getBlockchainTxId()));
                holder.bindTextView(R.id.tv_detail, AppUtil.roundRemoveZero(data.getAmount(), 8));

                String stutas = getStatusStr(data.getApprovalStatus());
                String stutasExecut = getExecutStatusStr(data.getExecutionStatus());

                if (type == 1) {
                    holder.bindTextView(R.id.tv_status, stutas);
                } else {
                    if (data.getApprovalStatus().equals("R")){
                        holder.bindTextView(R.id.tv_status, getString(R.string.str_not_check));
                    }else {
                        holder.bindTextView(R.id.tv_status, stutasExecut);
                    }

                }

                TextView tvTxid = (TextView) holder.getView(R.id.tv_address);
                TextView tvLook = (TextView) holder.getView(R.id.tv_look);

                if (TextUtils.isEmpty(data.getBlockchainTxId())){
                    tvLook.setTextColor(ResUtils.getColor(R.color.gy8A));
                }else {
//                    tvLook.setTextColor(ResUtils.getColor(R.color.purple77));
                    tvLook.setTextColor(ResUtils.getColor(R.color.color_1888FE));
                }
                holder.setOnClickListener(R.id.tv_detail);
                TextView address = (TextView) holder.getView(R.id.tv_address);
                TextView look = (TextView) holder.getView(R.id.tv_look);
                RelativeLayout rlyTixd = (RelativeLayout) holder.getView(R.id.rly_tixd);

                holder.setOnClickListener(R.id.tv_look);
                if (data.isExpand()) {
                    rlyTixd.setVisibility(View.VISIBLE);
                } else {
                    rlyTixd.setVisibility(View.GONE);
                }
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;


        }
    }


    //P 待审核 A 成功 R 审核不通过
    private String getStatusStr(String approvalStatus) {
        switch (approvalStatus) {
            case "P":
                return getString(R.string.str_dist_comfirm);
            case "A":
                return getString(R.string.str_suc);
            case "R":
                return getString(R.string.str_not_check);
            case "W":
                return getString(R.string.str_wait_check);
        }
        return "";
    }


    //N 待审核 W P 区块确认中 S 区块确认成功 F 区块确认失败
    private String getExecutStatusStr(String approvalStatus) {
        switch (approvalStatus) {
            case "N":
                return getString(R.string.str_wait_check);
            case "W":
            case "P":
                return getString(R.string.str_dist_comfirm);
            case "S":
                return getString(R.string.str_dist_comfirm_suc);
            case "F":
                return getString(R.string.str_dist_comfirm_fail);
        }
        return "";
    }
}
