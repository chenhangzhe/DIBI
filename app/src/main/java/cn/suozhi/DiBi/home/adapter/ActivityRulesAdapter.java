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
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;

/**
 * 邀请记录适配器
 */
public class ActivityRulesAdapter extends AbsRecyclerAdapter<AccountRecordEnity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private int type;
    private OnCallbackListener callbackListener;

    public ActivityRulesAdapter(Context context, int height, int type, OnCallbackListener callbackListener) {
        super(context, R.layout.item_activity_rules, R.layout.page_more, R.layout.page_bottom);
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
                holder.bindTextView(R.id.tv_test_text,"活动规则" + data.getCreatedDate());
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }
}
