package cn.suozhi.DiBi.common.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.SingleTextRVAdapter;

/**
 * 选择单一选项对话框
 */
public class ChooseSingleDialog extends BaseDialog implements View.OnClickListener,
        AbsRecyclerAdapter.OnItemClickListener {

    private int index;
    private boolean choose;

    public static ChooseSingleDialog newInstance(ArrayList<String> data) {
        return newInstance(null, data, -1, true);
    }

    public static ChooseSingleDialog newInstance(String title, ArrayList<String> data) {
        return newInstance(title, data, -1, true);
    }

    public static ChooseSingleDialog newInstance(String title, ArrayList<String> data, int index,
                                                 boolean choose) {
        ChooseSingleDialog dialog = new ChooseSingleDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putStringArrayList("data", data);
        bundle.putInt("index", index);
        bundle.putBoolean("choose", choose);//已选择item能否再次选中
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_choose_single, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画

        TextView tvTitle = v.findViewById(R.id.tv_dgCsTitle);
        View line = v.findViewById(R.id.l_dgCs);
        RecyclerView recyclerView = v.findViewById(R.id.rv_dgCs);
        v.findViewById(R.id.tv_dgCsCancel).setOnClickListener(this);

        Bundle arg = getArguments();
        String title = arg.getString("title");
        ArrayList<String> list = arg.getStringArrayList("data");
        index = arg.getInt("index", -1);
        choose = arg.getBoolean("choose", true);

        if (list != null && list.size() > 5) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
            lp.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            lp.height = Util.getPhoneHeight(getActivity()) / 2;
            recyclerView.setLayoutParams(lp);
        }

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SingleTextRVAdapter adapter = new SingleTextRVAdapter(getActivity(), list, index);
        recyclerView.setAdapter(adapter.setOnItemClickListener(this));
        recyclerView.addItemDecoration(new DecoRecycler(getActivity(), R.drawable.deco_1_gy_8a__t26,
                DecoRecycler.Edge_NONE));

        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(View v, int position) {
        if (index != position || choose) {
            if (onItemClickListener != null) {
                v.setTag(position);
                onItemClickListener.onItemClick(v);
            }
            dismiss();
        }
    }
}
