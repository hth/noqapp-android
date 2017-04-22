package com.noqapp.merchant.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.noqapp.merchant.R;
import com.noqapp.merchant.model.ManageQueueModel;
import com.noqapp.merchant.presenter.beans.JsonTopic;
import com.noqapp.merchant.presenter.beans.JsonTopicList;
import com.noqapp.merchant.views.activities.LaunchActivity;
import com.noqapp.merchant.views.adapters.MerchantListAdapter;
import com.noqapp.merchant.views.interfaces.TopicPresenter;


import java.util.List;


public class MerchantListFragment extends Fragment implements TopicPresenter{



    private MerchantListAdapter adapter;
    private List<JsonTopic> topics;
    private ListView listview;
    private RelativeLayout rl_empty_screen;
    public MerchantListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantlist, container, false);
        listview =(ListView) view.findViewById(R.id.listview);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        LaunchActivity.getLaunchActivity().progressDialog.show();
        ManageQueueModel.topicPresenter = this;
        ManageQueueModel.getQueues("123213","b@r.com",
                                "$2a$15$ed3VSsc5x367CNiwQ3fKsemHSZUr.D3EVjHVjZ2cBTySc/l7gwPua");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("List");

    }

    @Override
    public void queueResponse(JsonTopicList topiclist) {
        // To cancel
        if(null!=topiclist){
            topics=topiclist.getTopics();
            rl_empty_screen.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            adapter = new MerchantListAdapter(getActivity(), topics);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout,
                            MerchantViewPagerFragment.getInstance(position,topics),"MerchantViewPagerFragment");
                }
            });
        }else{
            //Show error
            rl_empty_screen.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }
    }

    @Override
    public void queueError() {

    }
}
