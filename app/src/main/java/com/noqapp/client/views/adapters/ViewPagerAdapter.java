package com.noqapp.client.views.adapters;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;

/**
 * Created by omkar on 3/31/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] pageTitles;
    private  Context context;
    private FragmentManager fragmentManager;
    private static  int NUM_ITEM = 3;

    public ViewPagerAdapter(Context context, FragmentManager fragmentManager,String[] pageTitles)
    {
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
     switch (position)
     {
         case 0:
             fragment = new ScanQueueFragment();
         break;

         case 1:
             fragment = ListQueueFragment.getInstance();
             break;

         case 2:
             fragment =  MeFragment.getInstance();
         break;
         default:
             return null;

     }
     return fragment;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

}
