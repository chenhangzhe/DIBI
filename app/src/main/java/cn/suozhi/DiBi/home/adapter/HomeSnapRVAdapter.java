package cn.suozhi.DiBi.home.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.home.model.QuoteEntity;

public class HomeSnapRVAdapter extends AbsRecyclerAdapter<List<QuoteEntity>> implements
        AbsRecyclerAdapter.OnItemClickListener {

    private int pageSize = 3;

    public HomeSnapRVAdapter(Context context) {
        super(context, R.layout.recycler_recycler);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, List<QuoteEntity> d, int position) {
        RecyclerView rv = (RecyclerView) holder.getView(R.id.rv_recyclerItem);
        RecyclerView.Adapter ada = rv.getAdapter();
        if (ada instanceof HomeTopRVAdapter) {
            ((HomeTopRVAdapter) ada).setData(d);
        } else {
            rv.setLayoutManager(new GridLayoutManager(context, pageSize));
            HomeTopRVAdapter adapter = new HomeTopRVAdapter(context, d);
            rv.setAdapter(adapter.setOnItemClickListener(this));
            rv.addItemDecoration(new DecoRecycler(context, R.drawable.deco_10_trans,
                    DecoRecycler.Edge_All));
            rv.addItemDecoration(new DecoRecycler(context, R.drawable.deco_5_trans,
                    DecoRecycler.Edge_Except_Top));
        }
    }

    public void setDataList(List<QuoteEntity> list) {
        if (data == null) {
            data = new ArrayList<>();
        } else {
            data.clear();
        }
        int page = (list.size() - 1) / pageSize + 1;
        for (int i = 0; i < page; i++) {
            int endIndex = Math.min((i + 1) * pageSize, list.size());
            data.add(list.subList(i * pageSize, endIndex));
        }
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View v, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, position);
        }
    }
}
