package cn.suozhi.DiBi.home.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.home.model.IeoEntity;

public class IeoListAdapter extends RecyclerView.Adapter<IeoListAdapter.MyViewHolder> {

    private RecyclerView recyclerView;
    private Context context;
    private List<IeoEntity.DataBean.RecordsBean> data;
    private View footerView;
    public static final int TYPE_FOOT = 1;
    public static final int TYPE_NOMAL = 0;
    public List<MyViewHolder> myViewHolderList = new ArrayList<>();

    public IeoListAdapter(Context context, List<IeoEntity.DataBean.RecordsBean> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public IeoListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.recycler_consensus_exchange, parent, false);
        return new IeoListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IeoListAdapter.MyViewHolder holder, int position) {
        // 用holder绑定对应的position
        holder.setDataPosition(position);
        int status = data.get(position).getStatus();
        Log.e("LogWT:","适配器a：" +data.get(position).getStartCountTime());
        Log.e("LogWT:","适配器b：" +data.get(position).getEndCountTime());
        Log.e("LogWT:","适配器c：" +data.get(position).getStartShowTime());
        Log.e("LogWT:","适配器d：" +data.get(position).getEndShowTime());
        if( status == 0){
            holder.tvCountDown.setText(data.get(position).getStartShowTime());
        } else if ( status == 1 ){
            holder.tvCountDown.setText(data.get(position).getEndShowTime());
        }
    }

    //遍历list，刷新相应holder的TextView
    public void notifyData(){
        for(int i = 0;i < myViewHolderList.size(); i++){
            int status = data.get(myViewHolderList.get(i).position).getStatus();
            if( status == 0){
                myViewHolderList.get(i).tvCountDown.setText(data.get(myViewHolderList.get(i).position).getStartShowTime());
            } else if ( status == 1 ){
                myViewHolderList.get(i).tvCountDown.setText(data.get(myViewHolderList.get(i).position).getEndShowTime());
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = (data == null ? 0 :data.size());
        if(footerView != null){
            count++;
        }
        return count;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvCountDown;
        private int position;

        private void setDataPosition(int position){
            this.position = position;
        }

        public MyViewHolder(View view)
        {
            super(view);
            tvCountDown = view.findViewById(R.id.tv_rce_time_text);
        }
    }

}
