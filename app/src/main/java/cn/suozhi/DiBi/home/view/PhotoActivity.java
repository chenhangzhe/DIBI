package cn.suozhi.DiBi.home.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.List;

import butterknife.BindView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.home.adapter.PhotoPagerAdapter;


/**
 * 创建时间：2019-05-22 16:57
 * 作者：Lich_Cool
 * 邮箱：lich_cool@sina.com
 * 功能描述：图片预览Activity
 */
public class PhotoActivity extends BaseActivity {
    @BindView(R.id.vp_photo_pager)
    PhotoViewPager vpPhotoPager;
    List<String> photoList;
    private String url;
    private int position;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.activity_scale_in,R.anim.activity_scale_out);
//    }
//
//    @Override
//    protected void onPause() {
//        //去除activity离开动画
//      overridePendingTransition(R.anim.activity_scale_in,R.anim.activity_scale_out);
//
//        super.onPause();
//
//    }
    @Override
    protected int getViewResId() {


        return R.layout.activity_photo;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        // 实例化一个Bundle
        Bundle bundle = intent.getExtras();
        photoList = (List<String>) bundle.getSerializable("list");//获取list方式
        position = bundle.getInt("position");
        url = bundle.getString("url");
        vpPhotoPager.setAdapter(new PhotoPagerAdapter(this, photoList));
        vpPhotoPager.setCurrentItem(position);
        ObjectAnimator moveInX = ObjectAnimator.ofFloat(vpPhotoPager, "translationX", 0, 1f);
        ObjectAnimator moveInY = ObjectAnimator.ofFloat(vpPhotoPager, "translationY", 0, 1f);
       // ObjectAnimator rotate = ObjectAnimator.ofFloat(vpPhotoPager, "rotation", 0f, 360f);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(vpPhotoPager, "alpha",  0f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(moveInX).with(moveInY).after(fadeInOut);
        animSet.setDuration(200);
        animSet.start();

    }

    @Override
    public void finish() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.finish();
    }
}
