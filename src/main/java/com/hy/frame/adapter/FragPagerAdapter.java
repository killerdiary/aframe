package com.hy.frame.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 自定义主页Pager适配器
 * 
 * @author HeYan
 * @time 2014-7-22 下午4:49:03
 */
public class FragPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public FragPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> fragments) {
        super(supportFragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

}
