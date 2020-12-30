package cn.suozhi.DiBi.home.adapter;

import android.content.Context;
import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.home.model.IeoEntity;

public class ConsensusExchangeEnAdapter extends AbsRecyclerAdapter<IeoEntity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    private DecimalFormat decimalFormat;

    public ConsensusExchangeEnAdapter(Context context, OnCallbackListener callbackListener) {
        this(context, 0, callbackListener);
    }

    public ConsensusExchangeEnAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_consensus_exchange_en, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getItemType(IeoEntity.DataBean.RecordsBean d) {
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
    public void onBindHolder(RecyclerHolder holder, IeoEntity.DataBean.RecordsBean d, int position) {
        switch (getItemType(d)) {
            case 0:
                // 已使用
                double used = (double)d.getTotalAmount() - (double)d.getSurplusAmount();
                double percent = used / (double)d.getTotalAmount() * 100; // 计算百分比进度

                BigDecimal a = new BigDecimal(percent);
                holder.bindImageViewGlideCircle(R.id.iv_rce_coin_image,d.getLogo()); // Logo
                holder.bindTextViewWithColorId(R.id.tv_helpItemTitle, d.getBaseCurrencyCode() + getString(R.string.cel_1) ,R.color.color_1B0167); // XX申购

                // 状态[0未开始,UI显示即将开始|1进行中,UI显示进行中|2已结束,UI显示结算中|3已结算,UI显示已结束]
                if(d.getStatus()==0)
                {
//                    holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_2) ,R.color.color_1B0167);
                    holder.bindTextViewWithColorId(R.id.tv_rce_progress,  getString(R.string.apply_for_progress) +"：" + a.setScale(2,BigDecimal.ROUND_HALF_UP) + "%",R.color.color_1B0167); // 申购进度
//                    holder.bindTextViewWithColorId(R.id.tv_rce_time, getString(R.string.cel_3) + "：" , R.color.color_1B0167);
                    long aaa = getStringToDate(d.getStartDate()) - System.currentTimeMillis();
                    String str = getFormatTime(aaa);
                    if(aaa<=0){
                        // 如果活动开始 更新按钮
                        holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_4),R.color.redE0);
                        holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_12),R.color.white);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time, getString(R.string.cel_5) + "...", R.color.color_1B0167);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time_text , "" , R.color.yellowD9);
                    } else {
                        // 继续倒计时直至活动结算
                        holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_2) ,R.color.color_1B0167);
                        holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_2),R.color.white);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time, getString(R.string.cel_13) + "：", R.color.color_1B0167);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time_text  , str , R.color.color_1B0167);
                    }
                }
                else if (d.getStatus()==1)
                {
//                    holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_4),R.color.redE0);
//                    holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_12),R.color.white);
                    holder.bindTextViewWithColorId(R.id.tv_rce_progress, getString(R.string.apply_for_progress) +"：" + a.setScale(2,BigDecimal.ROUND_HALF_UP) + "%",R.color.redE0);
//                    holder.bindTextViewWithColorId(R.id.tv_rce_time, getString(R.string.cel_5) + "：", R.color.color_1B0167);
                    long bbb = getStringToDate(d.getEndDate()) - System.currentTimeMillis();
                    String str2 = getFormatTime(bbb);
                    if (bbb<=0){
                        // 活动已结束
                        holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_6) ,R.color.color_1B0167);
                        holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_11),R.color.white);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time,getString(R.string.cel_7) + "..." , R.color.color_1B0167);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time_text , "" , R.color.yellowD9);
                    } else {
                        // 继续倒计时直至活动结算
                        holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_4),R.color.redE0);
                        holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_12),R.color.white);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time, getString(R.string.cel_5) + "：", R.color.color_1B0167);
                        holder.bindTextViewWithColorId(R.id.tv_rce_time_text , str2 , R.color.yellowD9);
                    }
                }
                else if (d.getStatus()==2)
                {
                    holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_6) ,R.color.color_1B0167);
                    holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_11),R.color.white);
                    holder.bindTextViewWithColorId(R.id.tv_rce_progress,  getString(R.string.apply_for_progress) +"：" + a.setScale(2,BigDecimal.ROUND_HALF_UP) + "%",R.color.color_1B0167); // 申购进度
                    holder.bindTextViewWithColorId(R.id.tv_rce_time,getString(R.string.cel_7) + "..." , R.color.color_1B0167);
                    holder.bindTextViewWithColorId(R.id.tv_rce_time_text , "" , R.color.yellowD9);
                }
                else if (d.getStatus()==3)
                {
                    holder.bindTextViewWithColorId(R.id.tv_helpItemTitle1, getString(R.string.cel_8) ,R.color.color_1B0167);
                    holder.bindTextViewWithColorId(R.id.tv_rce_button,getString(R.string.cel_11),R.color.white);
                    holder.bindTextViewWithColorId(R.id.tv_rce_progress,  getString(R.string.apply_for_progress) +"：" + a.setScale(2,BigDecimal.ROUND_HALF_UP) + "%",R.color.color_1B0167); // 申购进度
                    holder.bindTextViewWithColorId(R.id.tv_rce_time,getString(R.string.cel_9) + "..." , R.color.color_1B0167);
                    holder.bindTextViewWithColorId(R.id.tv_rce_time_text , "" , R.color.yellowD9);
                }

                holder.bindTextViewWithColorId(R.id.tv_rce_total,  getString(R.string.cel_10) + "：" + new DecimalFormat("###.00").format(d.getTotalAmount()) + " " + d.getQuoteCurrencyCode(),
                        R.color.color_1B0167); // 总申购资金

                // 百分比小于1时，进度条设置为1
                if( percent < 1){
                    holder.bindProgressBar(R.id.progress_rce, 1); // 进度条
                } else {
                    holder.bindProgressBar(R.id.progress_rce, (int) percent); // 进度条
                }

                holder.setOnClickListener(R.id.tv_rce_button);
                holder.setOnClickListener(R.id.tv_rce_time);



                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }

    private String getFormatTime(long distanceMillis) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = distanceMillis / dd;
        Long hour = (distanceMillis - day * dd) / hh;
        Long minute = (distanceMillis - day * dd - hour * hh) / mi;
        Long second = (distanceMillis - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }

        return sb.toString();
    }

    // 日期转时间戳
    public static long getStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }


}
