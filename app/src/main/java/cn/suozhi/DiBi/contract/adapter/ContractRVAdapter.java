package cn.suozhi.DiBi.contract.adapter;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.contract.model.ContractEntity;

public class ContractRVAdapter extends AbsRecyclerAdapter<ContractEntity> {

    private int tp = 2;
    public static final String MORE = "More";
    public static final String LOADING = "Load";

    public ContractRVAdapter(Context context, List<ContractEntity> data) {
        super(context, data, R.layout.contract_more, R.layout.page_bottom,
                R.layout.recycler_delivery, R.layout.recycler_entrust, R.layout.recycler_flow);
    }

    /**
     * type:
     *      0 - 点击加载更多/加载中
     *      1 - 已加载完
     *      2 - 已交割/待交割
     *      3 - 当前委托/历史委托
     *      4 - 流水
     */
    @Override
    public int getItemType(ContractEntity d) {
        return d.getType();
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, ContractEntity d, int position) {
        String s = d.getStatus();
        switch (getItemType(d)) {
            case 0:
                String more = MORE.equals(s) ? getString(R.string.clickLoadMore) :
                        (LOADING.equals(s) ? getString(R.string.loading) : "");
                holder.bindTextView(R.id.tv_conItemMore, more);
                break;
            case 2:
                holder.bindTextView(R.id.tv_deliverItemTime, Util.sdf_H2s.format(d.getTime()))
                        .bindTextView(R.id.tv_deliverItemOrder, Util.formatFloor(d.getOrderMoney(), tp))
                        .bindTextView(R.id.tv_deliverItemDone, Util.formatFloor(d.getDeliveryMoney(), tp))
                        .bindTextView(R.id.tv_deliverItemStatus,
                                getStringInResId("contractStatus" + s, s));
                break;
            case 3:
                String time = Util.sdf_M2s.format(d.getTime());
                String t0, t1;
                if (TextUtils.isEmpty(time) || !time.contains(" ")) {
                    t0 = time;
                    t1 = "";
                } else {
                    String[] split = time.split(" ");
                    t0 = split[0];
                    t1 = split[1];
                }
                double close = d.getClose();
                double pre = d.getPreClose();
                boolean rise = close >= pre;
                int cc = close <= 0 ? R.color.gy96 : (rise ? R.color.green3F : R.color.redE0);//收盘价颜色
                int dir = "R".equals(d.getDirection()) ? 0 : ("F".equals(d.getDirection()) ? 1 : 2);
                int res;
                if (dir == 2) {
                    res = 4;
                } else {
                    switch (s) {
                        case "R":
                            res = 2;
                            break;
                        case "S":
                            res = 3;
                            break;
                        case "W":
                        case "P":
                        case "F":
                        case "E":
                            res = dir == 0 ^ rise ? 1 : 0;
                            break;
                        default:
                            res = 4;
                            break;
                    }
                }
                String sign = res == 0 ? "+" : "";//交割符号
                int cr = res == 0 ? R.color.green3F : (res == 1 ? R.color.orangeE0 : R.color.gy5A);
                holder.bindTextView(R.id.tv_entItemDay, t0)
                        .bindTextView(R.id.tv_entItemTime, t1)
                        .bindTextViewWithColorId(R.id.tv_entItemClose, close <= 0 ? "--" : Util.formatFloor(close, tp), cc)
                        .bindTextView(R.id.tv_entItemPreClose, pre <= 0 ? "--" : Util.formatFloor(pre, tp))
                        .bindTextView(R.id.tv_entItemDelivery, sign + Util.formatFloor(d.getDeliveryMoney(), tp))
                        .bindTextView(R.id.tv_entItemOrder, Util.formatFloor(d.getOrderMoney(), tp))
                        .bindTextView(R.id.tv_entItemDirection, dir == 2 ? "--" :
                                getString(dir == 0 ? R.string.expectRise : R.string.expectFall))
                        .setViewVisible(R.id.iv_entItemResultIcon, res <= 1)
                        .bindImageView(R.id.iv_entItemResultIcon,
                                res == 0 ? R.mipmap.tick_green_round : R.mipmap.cross_brown_round)
                        .bindTextViewWithColorId(R.id.tv_entItemResult,
                                getStringInResId("contractResult" + res, "--"), cr)
                        .bindTextView(R.id.tv_entItemStatus,
                                getStringInResId("contractStatus" + s, "--"));
                break;
            case 4:
                int color;
                String money = d.getMoney();
                if (TextUtils.isEmpty(money)) {
                    color = R.color.gy96;
                } else if (money.startsWith("+")) {
                    color = R.color.green3F;
                } else if (money.startsWith("-")) {
                    color = R.color.redE0;
                } else {
                    color = R.color.gy96;
                }
                holder.bindTextView(R.id.tv_flowItemTime, Util.sdf_H2s.format(d.getTime()))
                        .bindTextViewWithColorId(R.id.tv_flowItemMoney, money, color)
                        .bindTextView(R.id.tv_flowItemStatus,
                                getStringInResId("flowStatus" + s, "--"));
                break;
        }
    }

    public void setTp(int tp) {
        this.tp = tp;
    }
}
