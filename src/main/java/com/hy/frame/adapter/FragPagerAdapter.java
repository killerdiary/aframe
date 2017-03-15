package com.hy.frame.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hy.frame.common.BaseFragment;

import java.util.List;

/**
 * 自定义主页Pager适配器
 * author HeYan
 * time 2016/3/8 13:08
 */
public class FragPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;
    private String[] titles;

    public FragPagerAdapter(FragmentManager manager, List<BaseFragment> fragments) {
        this(manager, fragments, null);

    }

    public FragPagerAdapter(FragmentManager manager, List<BaseFragment> fragments, String[] titles) {
        super(manager);
        this.fragments = fragments;
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null)
            return titles[position];
        return null;
    }

}
