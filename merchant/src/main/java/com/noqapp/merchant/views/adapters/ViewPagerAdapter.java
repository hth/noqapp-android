package com.noqapp.merchant.views.adapters;

/**
 * Created by chandra on 4/16/17.
 */
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.noqapp.merchant.R;
import com.noqapp.merchant.presenter.beans.ListQueue;

import java.util.ArrayList;


public class ViewPagerAdapter  extends PagerAdapter {

    private Context context;
    private ArrayList<ListQueue> arrayList;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context, ArrayList<ListQueue> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        TextView tv_current_value;
        TextView tv_total_value;
        TextView tv_title;


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        tv_current_value = (TextView) itemView.findViewById(R.id.tv_current_value);
        tv_total_value = (TextView) itemView.findViewById(R.id.tv_total_value);
        tv_title= (TextView) itemView.findViewById(R.id.tv_title);
        ListQueue lq = arrayList.get(position);
        tv_current_value.setText(String.valueOf(lq.getServingNumber()));
        tv_total_value.setText(String.valueOf(lq.getToken()));
        tv_title.setText(lq.getDisplayName());



        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((View) object);

    }
}