package cn.suozhi.DiBi.wallet.adapter;

import android.content.Context;
import android.view.View;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.wallet.model.CoinAddressEnity;

/**
 * 首页钱包的适配器
 */
public class AddressAdapter extends AbsRecyclerAdapter<CoinAddressEnity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public AddressAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.item_address, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public int getItemType(CoinAddressEnity.DataBean.RecordsBean d) {
        return d.getLoadType();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, CoinAddressEnity.DataBean.RecordsBean data, int position) {

        switch (getItemType(data)) {
            case 0:

                String coin = data.getCode();
                holder.bindTextView(R.id.tv_coin, coin);
                holder.bindTextView(R.id.tv_name, data.getTag());
                holder.bindTextView(R.id.tv_address,  data.getAddress());

                if (!data.isTransactionNeedTag()) {
                    holder.bindTextView(R.id.tv_memo_title, getString(R.string.str_other_none));
                    holder.setViewVisible(R.id.tv_memo, View.GONE);
                } else {
                    holder.setViewVisible(R.id.tv_memo, View.VISIBLE);


//                    if (coin.equals("EOS")
//                            || coin.equals("XLM")
//                            || coin.equals("XRP")
//                            || coin.equals("XMP") || coin.equals("WCG") || coin.equals("WEN") || coin.equals("ORA") || coin.equals("IPSO") || coin.equals("HCC")|| coin.equals("GDM")) {
//
//                        if (coin.equals("EOS")
//                                || coin.equals("XLM") || coin.equals("WCG") || coin.equals("WEN") || coin.equals("ORA") || coin.equals("IPSO") || coin.equals("HCC")|| coin.equals("GDM")) {
//                            holder.bindTextView(R.id.tv_memo, data.getRemark());
//                            holder.bindTextView(R.id.tv_memo_title, "Memo :");
//                        }
//                    } else if (coin.equals("XRP")) {
//                        holder.bindTextView(R.id.tv_memo, data.getRemark());
//                        holder.bindTextView(R.id.tv_memo_title, "Tag :");
//                    } else {
//                        holder.bindTextView(R.id.tv_memo, data.getRemark());
//                        holder.bindTextView(R.id.tv_memo_title, "Payment ID :");
//
//                    }

                    holder.bindTextView(R.id.tv_memo, data.getRemark());
                    holder.bindTextView(R.id.tv_memo_title, data.getTagDescribe() +" :");
                }
                holder.setOnClickListener(R.id.tv_item_delete);
                holder.setOnClickListener(R.id.lly_item_view);
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }

    }
}
