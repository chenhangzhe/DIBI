package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.custom.SideBar;
import cn.suozhi.DiBi.common.custom.TopLinearManager;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.CharacterParser;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.PinyinComparator;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.NationRVAdapter;
import cn.suozhi.DiBi.home.model.SelectEntity;

/**
 * 选择国家或区号
 */
public class NationActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        OkHttpUtil.OnDataListener, AbsRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;
    @BindView(R.id.srl_item)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_item)
    public RecyclerView recyclerView;

    @BindView(R.id.tv_nation)
    public TextView tv;
    @BindView(R.id.sb_nation)
    public SideBar sidebar;

    private String cate;//类型
    private CharacterParser parser;
    private PinyinComparator pyComparator;
    private NationRVAdapter recyclerAdapter;
    private List<SelectEntity.DataEntity> dataList;

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_nation;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        ToolbarUtil.initToolbar(toolbar, title, v -> onBackPressed());
        showLoading();
        lang = SharedUtil.getLanguage4Url(this);

        cate = intent.getStringExtra("cate");
        boolean showValue = intent.getBooleanExtra("showValue", true);//显示值(cn、+86等)
        parser = CharacterParser.getInstance();
        pyComparator = new PinyinComparator();
        sidebar.setTextView(tv);
        initRecycler(showValue);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    private void initRecycler(boolean showValue) {
        recyclerView.setLayoutManager(new TopLinearManager(this));
        recyclerAdapter = new NationRVAdapter(this, showValue);
        recyclerView.setAdapter(recyclerAdapter.setOnItemClickListener(this)
                .setEmptyView(R.layout.empty_tips));
        DecoRecycler deco = new DecoRecycler(this, 0, true)
                .setStickyBackground(Util.getColor(this, R.color.gyF9))
//                .setStickyColor(Util.getColor(this, R.color.purple77))
                .setStickyColor(Util.getColor(this, R.color.color_1888FE))
                .setStickySize(Util.sp2px(this, 16))
                .setStickyPaddingLeft(Util.dp2px(this, 15));
        recyclerView.addItemDecoration(deco);

        sidebar.setOnTouchingListener(s -> {
            int position = recyclerAdapter.getPositionForSection(s);
            if (position > -1) {
                recyclerView.scrollToPosition(position);
            }
        });
    }

    @Override
    protected void loadData() {
        OkHttpUtil.getJson(Constant.URL.GetSelect, this, "category", cate, "language", lang);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "Nation: " + json);
        SelectEntity nation = gson.fromJson(json, SelectEntity.class);
        dismissLoading();
        if (Constant.Int.SUC == nation.getCode()) {
            dataList = nation.getData();
            transPinyin();
            Collections.sort(dataList, pyComparator);
            recyclerAdapter.setData(dataList);
        } else {
            ToastUtil.initToast(this, Util.getCodeText(this,
                    nation.getCode(), nation.getMsg()));
        }
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    @Override
    protected void showLoading() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(this);
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void transPinyin() {
        for (int i = 0; i < dataList.size(); i++) {
            String pinyin = Chinese2Pinyin(dataList.get(i).getName());
            //判断首字母是否是英文字母
            if (!TextUtils.isEmpty(pinyin) && pinyin.substring(0, 1).toUpperCase().matches("[A-Z]")) {
                dataList.get(i).setSortLetter(pinyin);
            } else {
                dataList.get(i).setSortLetter("#");
            }
        }
    }

    private String Chinese2Pinyin(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        return parser.getSelling(name);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (v.getId() == R.id.tv_nationItemName) {
            Intent data = new Intent()
                    .putExtra("name", dataList.get(position).getName())
                    .putExtra("value", dataList.get(position).getValue());
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
