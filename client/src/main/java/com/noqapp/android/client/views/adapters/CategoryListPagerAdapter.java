package com.noqapp.android.client.views.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.noqapp.android.client.views.fragments.CategoryListFragment;

import java.util.List;

public class CategoryListPagerAdapter extends FragmentStatePagerAdapter {

    private List<CategoryListFragment> mFragments;

    public CategoryListPagerAdapter(FragmentManager fm, List<CategoryListFragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragments.set(position, (CategoryListFragment) fragment);
        return fragment;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

}
