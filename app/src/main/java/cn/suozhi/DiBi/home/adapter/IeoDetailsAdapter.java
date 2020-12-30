package cn.suozhi.DiBi.home.adapter;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.home.model.IeoDetailsEntity;
import cn.suozhi.DiBi.home.model.IeoEntity;

public class IeoDetailsAdapter extends AbsRecyclerAdapter<IeoDetailsEntity.DataBean> {

    private int height;//空页面高度
    private OnCallbackListener callbackListener;

    public IeoDetailsAdapter(Context context, OnCallbackListener callbackListener) {
        this(context, 0, callbackListener);
    }

    public IeoDetailsAdapter(Context context, int height, OnCallbackListener callbackListener) {
        super(context, R.layout.recycler_consensus_exchange, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.callbackListener = callbackListener;
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
    public void onBindHolder(RecyclerHolder holder, IeoDetailsEntity.DataBean d, int position) {
        //
    }

}
