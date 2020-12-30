package cn.suozhi.DiBi.wallet.adapter;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.wallet.model.TransferRecordEntity;

/**
 * 首页钱包的适配器
 */
public class TransferRecordBKAdapter extends AbsRecyclerAdapter<TransferRecordEntity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private int type;
    private OnCallbackListener callbackListener;

    public TransferRecordBKAdapter(Context context, int height, int type, OnCallbackListener callbackListener) {
        super(context, R.layout.item_transfer_record_item, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.type = type;
        this.callbackListener = callbackListener;
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
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
                holder.bindTextView(R.id.tv_account_name,"用户：" + d.getPhoneOrEmail());
                holder.bindTextView(R.id.tv_transfer_accounts,"转账数量：" + d.getAmount());
                holder.bindTextView(R.id.tv_transfer_date,d.getCreatedDate());

                String[] date = d.getCreatedDate().split(",");
                List<String> stringList = new ArrayList<>();
                for (int i = 0; i < date.length; i++) {
                    stringList.add(date[i]);
                }

                sortList(stringList);

                break;
            case 1:
                //滑到底，加载下一页
//                callbackListener.onCallback();
                break;
        }
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    /**
     * 排序
     * *@param stringList
     */
    private void sortList(List<String> stringList) {
        Collections.sort(stringList, new Comparator<String>() {
            /*
             * int compare(Person p1, Person p2) 返回一个基本类型的整型，
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(String p1, String p2) {
                // 按照Person的年龄进行升序排列
                if (Integer.valueOf(p1) > Integer.valueOf(p2)) {
                    return 1;
                }
                if (Integer.valueOf(p1) == Integer.valueOf(p2)) {
                    return 0;
                }
                return -1;
//                // 按照date降序排序
//                if (Integer.valueOf(String.valueOf(getStringToDate(p1))) < Integer.valueOf(String.valueOf(getStringToDate(p2)))) {
//                    return 1;
//                }
//                if (Integer.valueOf(String.valueOf(getStringToDate(p1))) == Integer.valueOf(String.valueOf(getStringToDate(p2)))) {
//                    return 0;
//                }
//                return -1;
            }
        });
    }

    /**
     * 将字符串转为时间戳
     *      *@param dateString
     *      * @param pattern
     *      * @return
     */
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
