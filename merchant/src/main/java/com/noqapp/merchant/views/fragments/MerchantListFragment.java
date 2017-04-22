package com.noqapp.merchant.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.noqapp.merchant.R;
import com.noqapp.merchant.presenter.beans.ListQueue;
import com.noqapp.merchant.views.activities.LaunchActivity;
import com.noqapp.merchant.views.adapters.MerchantListAdapter;


import java.util.ArrayList;


public class MerchantListFragment extends Fragment {



    private MerchantListAdapter adapter;
    private ArrayList<ListQueue> items;
    public MerchantListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantlist, container, false);
        ListView listview =(ListView) view.findViewById(R.id.listview);
        items =new ArrayList<ListQueue>();
        for (int i=0;i<5;i++){

            ListQueue lq1 =new ListQueue("","Cat","Now 23",22,11);
            ListQueue lq2 =new ListQueue("","Snake","Now 11",24,13);
            ListQueue lq3 =new ListQueue("","Goldfish","Now 2",19,15);
            items.add(lq1);
            items.add(lq2);
            items.add(lq3);
        }
        adapter = new MerchantListAdapter(getActivity(), items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object listItem = list.getItemAtPosition(position);
               LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout,
                       MerchantViewPagerFragment.getInstance(position,items),"MerchantViewPagerFragment");
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("List");

    }

}
