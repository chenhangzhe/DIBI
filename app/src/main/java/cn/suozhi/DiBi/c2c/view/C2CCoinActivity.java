package cn.suozhi.DiBi.c2c.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.adapter.C2cAllCoinAdapter;
import cn.suozhi.DiBi.c2c.adapter.C2cCoinSideBar;
import cn.suozhi.DiBi.c2c.model.C2cLCoinEntity;
import cn.suozhi.DiBi.c2c.model.C2cSelectedEnity;
import cn.suozhi.DiBi.c2c.model.C2cSliderEnity;
import cn.suozhi.DiBi.c2c.model.CoinEnity;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;

public class C2CCoinActivity extends BaseActivity implements OkHttpUtil.OnDataListener, C2cCoinSideBar.OnTouchingLetterChangedListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    //显示城市列表
    private ListView mainList;
    //城市数据源
    private List<String> data;
    //字母位置
    private List<Integer> letterPositionList;
    //字母Char
    private List<Integer> letterCharList;
    //自定义View显示右侧字母
    private C2cCoinSideBar myView;
    //列表上方固定文字
    private TextView tv01;
    //右侧显示内容
    private int lastFirstVisibleItem;
    private TextView overlay;
    private Handler handler;
    private OverlayThread overlayThread;
//    private CoinEnity allCoins;
    private C2cLCoinEntity allCoins;
    private List<C2cSliderEnity> districtSliderEnities;
    private List<C2cSliderEnity> title = new ArrayList<>();
    private int type;
    private String currency = "";
    //排序后的集合
    List<C2cSelectedEnity> coinSort = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_c2c_selected_coin;
    }

    @Override
    protected void init() {
        super.init();

        initData();

        tvTitle.setText(getString(R.string.str_c2c_coin_selected));

        //用于显示城市列表
        mainList = (ListView) findViewById(R.id.mainlist);
        //显示右侧字母列表
        myView = (C2cCoinSideBar) findViewById(R.id.myview);
        //城市列表上方固定文字
        tv01 = (TextView) findViewById(R.id.main_tv01);

        //绑定滑动监听
        myView.setOnTouchingLetterChangedListener(this);
        data = new ArrayList<String>();

        letterCharList = new ArrayList<Integer>();
        letterPositionList = new ArrayList<Integer>();


        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result = data.get(position) + "";

                Intent intent = new Intent();
                intent.putExtra("result", result);

                setResult(RESULT_OK, intent);//设置resultCode
                finish();


            }
        });

    }

    @OnClick({R.id.iv_back})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void initData() {
        type = getIntent().getIntExtra("conuty", -1);
        districtSliderEnities = new ArrayList<>();
        districtSliderEnities.add(new C2cSliderEnity("热", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("A", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("B", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("C", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("D", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("E", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("F", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("G", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("H", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("I", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("J", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("K", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("L", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("M", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("N", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("O", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("P", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("Q", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("R", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("S", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("T", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("U", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("V", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("W", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("X", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("Y", new ArrayList<>()));
        districtSliderEnities.add(new C2cSliderEnity("Z", new ArrayList<>()));
    }

    @Override
    protected void loadData() {
        super.loadData();
//        OkHttpUtil.getJson(Constant.URL.coinList, this);

        getCoin();
    }

    private void getCoin() {
//        OkHttpUtil.getJsonToken(Constant.URL.coinListC2c, SharedUtil.getToken(mContext), this,"category","FIAT");
        OkHttpUtil.getJson(Constant.URL.currencies, this);
    }


    //加载提示选择的字母
    private void initOverlay() {
        //设置选择字母后提示信息显示位置
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        //设置提示信息显示的位置
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        //添加提示信息到布局 lp的位置
        windowManager.addView(overlay, lp);
    }

    //触摸监听
    public void onTouchingLetterChanged(int s) {
        //切换到list指定的item位置
        mainList.setSelection(letterPositionList.get(s));
        //设置提示的字母
//        overlay.setText(districtSliderEnities.get(s).getSort());
        //显示提示窗
//        overlay.setVisibility(View.VISIBLE);
        //移除以前开启的线程
        handler.removeCallbacks(overlayThread);
        //1.5秒后执行overlayThread线程 隐藏选择字母提示
        handler.postDelayed(overlayThread, 1500);
    }


    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("C2CCOIN：","json：" + json);
//        allCoins = GsonUtil.fromJson(json, CoinEnity.class);
        allCoins = GsonUtil.fromJson(json, C2cLCoinEntity.class);
        if (allCoins.getCode() == Constant.Int.SUC) {
            Log.d("wangli", json);
            //清除数据
            title.clear();

//            List<CoinEnity.DataBean> coinList = allCoins.getData();
            List<C2cLCoinEntity.DataBean> coinList = allCoins.getData();

            coinSort.clear();
            for (int i = 0; i < coinList.size(); i++) {
                C2cSelectedEnity c2cSelectedEnity = new C2cSelectedEnity();
                c2cSelectedEnity.setCoin(coinList.get(i).getCode());
                coinSort.add(c2cSelectedEnity);
            }

            for (int i = 0; i < coinSort.size(); i++) {
                //汉字转换成拼音
//               String pinyin = PinyinUtils.getPingYin(coinList.get(i).getCode());
                String pinyin = coinSort.get(i).getCoin();
                //取第一个首字母
                String letters = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
//                if (letters.matches("[A-Z]")) {
//
//                }
                coinSort.get(i).setmSortLetters(letters.toUpperCase());
            }

            //清除每一个字母集合里面的数据
            for (int i = 0; i < districtSliderEnities.size(); i++) {
                districtSliderEnities.get(i).getList().clear();

            }


            for (int i = 0; i < districtSliderEnities.size(); i++) {
                for (int j = 0; j < coinList.size(); j++) {
                    if (coinSort.get(j).getmSortLetters().equals(districtSliderEnities.get(i).getSort())) {
                        districtSliderEnities.get(i).getList().add(coinSort.get(j));
                    }
                }


            }

            for (int i = 0; i < districtSliderEnities.size(); i++) {
                if (districtSliderEnities.get(i).getList().size() != 0 && districtSliderEnities.get(i).getList() != null) {
                    title.add(districtSliderEnities.get(i));
                }
            }
            showPhoneCity(districtSliderEnities, title);

        }

    }

    @Override
    public void onFailure(String url, String error) {

    }

    //关闭提示信息线程
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            //隐藏提示信息
            overlay.setVisibility(View.GONE);
        }
    }


    public void showPhoneCity(List<C2cSliderEnity> enityList, List<C2cSliderEnity> title) {
        //清除数据
        letterPositionList.clear();
        data.clear();
        //消息通知
        handler = new Handler();
        //隐藏提示信息线程
        overlayThread = new OverlayThread();
        initOverlay();
        int index = 0, position = 0;
        //是否显示热门
//        letterCharList.add(index);
        //循环城市数据源
        for (int i = 0; i < enityList.size(); i++) {
            for (int j = 0; j < enityList.get(i).getList().size(); j++) {
                if (i == 0 && j == 0) {
                    index++;
                    letterPositionList.add(position);
                } else if (j == 0) {
                    letterCharList.add(index);
                    letterPositionList.add(position);
                    index++;
                } else {
                    letterCharList.add(-1);
                }
                position++;
                //添加数据到集合
                data.add(enityList.get(i).getList().get(j).getCoin());

            }
        }
        //声明适配器
        C2cAllCoinAdapter adapter = new C2cAllCoinAdapter(this, data, letterCharList, title, type);
        //设置适配器
        mainList.setAdapter(adapter);
        //判断listview滚动设置城市列表上方固定文字
        mainList.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            //滑动的时候触发
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (letterCharList.get(firstVisibleItem) >= 0) {
                    //设置显示文字
                    tv01.setText(title.get(letterCharList.get(firstVisibleItem)).getSort());
                    lastFirstVisibleItem = firstVisibleItem;
                } else {
                    if (lastFirstVisibleItem > firstVisibleItem) {
                        //设置显示文字
                        tv01.setText(title.get(letterCharList.get(lastFirstVisibleItem) - 1).getSort());
                    }
                }
            }
        });

        myView.setData(title);
    }

}
