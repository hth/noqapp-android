package com.noqapp.merchant.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.noqapp.merchant.R;
import com.noqapp.merchant.views.activities.LaunchActivity;
import com.noqapp.merchant.views.adapters.ViewPagerAdapter;


public class MerchantViewPagerFragment extends Fragment {



    public ViewPagerAdapter adapter;
    private ViewPager        viewPager ;
    private static int pos=0;
    private ImageView leftNav,rightNav;


    public static MerchantViewPagerFragment merchantViewPagerFragment;

    public static  MerchantViewPagerFragment getInstance(int position) {
        pos=position;
        return  merchantViewPagerFragment =new MerchantViewPagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantviewpager, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);
        leftNav = (ImageView) view.findViewById(R.id.left_nav);
        rightNav = (ImageView) view.findViewById(R.id.right_nav);
        if(MerchantListFragment.topics.size()>1){
            leftNav.setVisibility(View.VISIBLE);
            rightNav.setVisibility(View.VISIBLE);
        }else{
            leftNav.setVisibility(View.INVISIBLE);
            rightNav.setVisibility(View.INVISIBLE);
        }
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if(tab<viewPager.getAdapter().getCount()) {
                    tab++;
                    viewPager.setCurrentItem(tab);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Queue Detail");
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
    }

}
