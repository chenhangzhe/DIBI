package cn.suozhi.DiBi.wallet.adapter;
//download by http://www.codesc.net

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.wallet.model.CoinSliderEnity;

public class AllCoinAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;
    private LayoutInflater mLayoutInflater;
    private List<Integer> letterCharList;
    private List<CoinSliderEnity> title;
    private int type;

    public AllCoinAdapter(Context context, List<String> data,
                          List<Integer> letterCharList, List<CoinSliderEnity> title, int type) {
        super();
        this.context = context;
        this.data = data;
        this.letterCharList = letterCharList;
        this.title = title;

        this.type = type;
    }

    public int getCount() {
        return data == null ? 0 : data.size();
    }

    public Object getItem(int position) {
        if (data != null) {
            return data.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.maillist_item, null, false);
        }

        TextView tv02 = (TextView) convertView.findViewById(R.id.mainlist_item_tv01);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
        //判断是否显示分类提示
        if (letterCharList.get(position) >= 0) {
            tv02.setVisibility(View.VISIBLE);
            tv02.setText(title.get(letterCharList.get(position)).getSort());
        } else {
            tv02.setVisibility(View.GONE);
        }

        if (type == 1) {
            tvPhone.setVisibility(View.GONE);
        } else {
            tvPhone.setVisibility(View.VISIBLE);
        }
        tv02.setTextColor(Color.BLACK);
        TextView tv01 = (TextView) convertView.findViewById(R.id.mainlist_item_tv02);
        //设置显示城市内容
        tv01.setText(data.get(position));
//        tv01.setTextColor(Color.BLACK);
        return convertView;
    }

}
