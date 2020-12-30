package cn.suozhi.DiBi.wallet.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.wallet.model.SingleCoinInfoEnity;

/**
 * 充币提示适配器
 */
public class ChargeCoinHintAdapter extends AbsRecyclerAdapter<SingleCoinInfoEnity.DataBean.DepositTipsBean> {

    public ChargeCoinHintAdapter(Context context) {
        super(context, R.layout.item_charge_coin_hint);

    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, SingleCoinInfoEnity.DataBean.DepositTipsBean data, int position) {
        holder.bindTextView(R.id.tv_hint, data.getContent());
    }
}
