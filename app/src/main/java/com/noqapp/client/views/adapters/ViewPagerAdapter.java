package com.noqapp.client.views.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;

/**
 * Created by omkar on 3/31/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

    private final String[] pageTitles;
    private static Activity context;
    private FragmentManager fragmentManager;
    private static int NUM_ITEM = 3;
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    public ViewPagerAdapter(Activity context, FragmentManager fragmentManager, String[] pageTitles) {
        super(fragmentManager);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.pageTitles = pageTitles;
    }


    @Override
    public int getCount() {

        return pageTitles.length;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ScanQueueFragment();
                break;

            case 1:
                fragment = ListQueueFragment.getInstance();
                break;

            case 2:
                fragment = MeFragment.getInstance();
                break;
            default:
                return null;

        }
        return fragment;

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "onPageSelected :::" + String.valueOf(position));
        if (position == 1) {
            notifyDataSetChanged();
            ListQueueFragment fragment = new ListQueueFragment();
            fragment.callQueue();

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        Log.i(TAG, "onPageScrollStateChanged :::" + String.valueOf(state));
    }


}
