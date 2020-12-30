package cn.suozhi.DiBi.home.adapter;

import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.suozhi.DiBi.common.base.BaseFragment;

/**
 * 描述 :回复工单适配器
 */
public class PromotionPageAdapter extends FragmentPagerAdapter {
    private ArrayList<String> titleList;
    private ArrayList<BaseFragment> fragmentList;

    public PromotionPageAdapter(FragmentManager fm, ArrayList<String> titleList, ArrayList<BaseFragment> fragmentList) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        try {
            ((ViewPager) container).removeView((View) object);
        } catch (Exception e) {
        }
    }
}
