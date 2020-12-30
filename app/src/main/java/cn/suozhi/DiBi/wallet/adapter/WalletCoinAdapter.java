package cn.suozhi.DiBi.wallet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.wallet.model.CoinEnity;

/**
 * 首页钱包的适配器
 */
public class WalletCoinAdapter extends AbsRecyclerAdapter<CoinEnity.DataBean> {

    private int height;//空页面高度
    public TextView blunt;
    public TextView mention;
    public TextView zhang;
    public int selectposition = -1;
    public WalletCoinAdapter(Context context, int height) {
        super(context, R.layout.item_wallet);
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
    public void onBindHolder(RecyclerHolder holder, CoinEnity.DataBean d, int position) {
         blunt = holder.itemView.findViewById(R.id.tv_blunt);
        mention = holder.itemView.findViewById(R.id.tv_mention);
        zhang = holder.itemView.findViewById(R.id.tv_zhang);
        double all = Double.parseDouble(d.getAvailableAmount()) + Double.parseDouble(d.getFrozenAmount());
        boolean canAdmin = d.getIsDepositable() == 0 && d.getIsWithdrawable() == 0;
        boolean forbid = AppUtil.hasForbidReason(d.getForbiddenReason());

        if(d.getIsSpecialCurrency()==0){
            holder.setViewAlpha(R.id.cl_walletItem, canAdmin && !forbid ? 0.4F : 1)
                    .bindTextView(R.id.tv_coin, d.getShowCode());


        } else {
            holder.setViewAlpha(R.id.cl_walletItem, canAdmin && !forbid ? 0.4F : 1)
                    .bindTextView(R.id.tv_coin, d.getShowCode() + " (" + getString(R.string.mto) + ")");

        }
        holder.bindTextView(R.id.tv_all,getTwoPointNums(all + "",7));
        holder.bindTextView(R.id.tv_cny, "≈" + getTwoPointNums(d.getCnyValuation(),2) + " CNY");
        holder.bindTextView(R.id.tv_avalible, getTwoPointNums(d.getAvailableAmount(),7));
        holder.bindTextView(R.id.tv_frozen,getTwoPointNums(d.getFrozenAmount(),7));
        holder.bindImageViewGlide(R.id.iv_coin, d.getIcon());

        holder.bindImageViewGlideCircle(R.id.iv_coin, d.getIcon());
        holder.setViewVisibleWithClickListener(R.id.iv_item_help, canAdmin && forbid);

        holder.setViewVisible(R.id.toplayout,selectposition == position);

    }

    private String getTwoPointNums(String nums, int flag) {
        return Util.formatFloor(Double.parseDouble(nums), flag);
    }
}
