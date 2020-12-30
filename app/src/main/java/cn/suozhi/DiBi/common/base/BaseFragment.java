package cn.suozhi.DiBi.common.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.suozhi.DiBi.home.model.Symbol;

/**
 * 所有Fragment的基类
 */
public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;

    protected OnObjectListener objectListener;
    protected OnLoadListener loadListener;

    protected String lang;//请求接口时的语言，免去多次定义
    public final String TAG = "loge";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getViewResId(), container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        try {
            getData(getArguments());
            init(v);
            loadData();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        super.onViewCreated(v, savedInstanceState);
    }

    protected abstract int getViewResId();

    protected void getData(Bundle args){}

    protected void init(View v){}

    protected  void loadData(){}

    protected void showLoading(){}

    protected void dismissLoading(){}

    public void setOnObjectListener(OnObjectListener objectListener) {
        this.objectListener = objectListener;
    }

    public interface OnObjectListener {
        void onObject(Object o);
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface OnLoadListener {
        void onLoad(int type, long id);
        void onLoad(int type, long id, View v);
        void onLoad(int type, long id, View v, String orderBy, String order);
        void onLoad(int type, long id, View v, String zone, String orderBy, String order);
        Symbol getSymbol();
    }

    public void transmit(String info){}

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
