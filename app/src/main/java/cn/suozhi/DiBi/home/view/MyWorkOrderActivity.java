package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseFragment;
import cn.suozhi.DiBi.home.adapter.PromotionPageAdapter;

/**
 * 功能描述：我的工单
 */
public class MyWorkOrderActivity extends BaseActivity {


    @BindView(R.id.iv_mineBack)
    ImageView ivMineBack;
    @BindView(R.id.iv_edit)
    ImageView ivEdit;
    @BindView(R.id.tv_msg_title)
    TextView tvMsgTitle;
    @BindView(R.id.smarttablayout)
    SmartTabLayout smarttablayout;
    @BindView(R.id.viewpager)
    ExtendedViewPager viewpager;
    ArrayList<BaseFragment> fragmentList;
    ArrayList<String> titleDatas;
    private PromotionPageAdapter promotionPageAdapter;

    @Override
    protected int getViewResId() {

        return R.layout.activity_my_work_order;

    }

    @Override
    protected void init() {
        super.init();
        if (titleDatas == null) {
            titleDatas = new ArrayList<>();
        } else {
            titleDatas.clear();
        }

        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        } else {
            fragmentList.clear();
        }
        titleDatas.add(getString(R.string.replyWait));
        titleDatas.add(getString(R.string.replyDone));

        fragmentList.add(RepliyFragment.create(Constant.Code.Type_Waited_Reply));
        fragmentList.add(RepliyFragment.create(Constant.Code.Type_Replied));

        promotionPageAdapter=new PromotionPageAdapter(getSupportFragmentManager(),titleDatas,fragmentList);
        viewpager.setAdapter(promotionPageAdapter);
        smarttablayout.setViewPager(viewpager);

    }


    @Override
    protected void loadData() {
        super.loadData();


    }


    @OnClick({R.id.iv_mineBack, R.id.iv_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_mineBack:
                finish();
                break;
            case R.id.iv_edit://新建工单
                startActivity(new Intent(this, SubmitOrderWorkActivity.class));
                break;
        }
    }
}
