package cn.suozhi.DiBi.c2c.adapter;

import android.content.Context;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;

/**
 * 首页钱包的适配器
 */
public class SellUsdtAdapter extends AbsRecyclerAdapter<String> {

    private int height;//空页面高度

    public SellUsdtAdapter(Context context, int height) {
        super(context, R.layout.item_buy_usdt);
        this.height = height;
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
    public void onBindHolder(RecyclerHolder holder, String data, int position) {
       holder.bindTextView(R.id.tv_buy,getString(R.string.str_sell));

    }
}
