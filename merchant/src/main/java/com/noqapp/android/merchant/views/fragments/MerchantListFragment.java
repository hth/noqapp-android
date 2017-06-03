package com.noqapp.android.merchant.views.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.MerchantListAdapter;
import com.noqapp.android.merchant.views.adapters.ViewPagerAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;
import com.noqapp.android.merchant.views.interfaces.TopicPresenter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class MerchantListFragment extends Fragment implements TopicPresenter, FragmentCommunicator, AdapterCallback {


    public static int selected_pos = -1;
    private MerchantListAdapter adapter;
    private ArrayList<JsonTopic> topics;
    private ListView listview;
    private RelativeLayout rl_empty_screen;
    private MerchantViewPagerFragment merchantViewPagerFragment;
    private Context context;

    public MerchantListFragment() {

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        ((LaunchActivity) context).fragmentCommunicator = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantlist, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        Bundle bundle = getArguments();
        ViewPagerAdapter.setAdapterCallBack(this);
        if (null != bundle) {
            JsonMerchant jsonMerchant = (JsonMerchant) bundle.getSerializable("jsonMerchant");
            updateListData(jsonMerchant.getTopics());
            subscribeTopics();
            initListView();
        } else {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
                ManageQueueModel.topicPresenter = this;
                ManageQueueModel.getQueues(
                        LaunchActivity.getLaunchActivity().getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth());
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);

    }

    @Override
    public void queueResponse(JsonTopicList topiclist) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        // To cancel
        if (null != topiclist) {
            updateListData(topiclist.getTopics());
            subscribeTopics();
            initListView();
        } else {
            //Show error
            rl_empty_screen.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }
    }

    @Override
    public void queueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


    private void initListView() {
        rl_empty_screen.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        adapter = new MerchantListAdapter(getActivity(), topics);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                merchantViewPagerFragment = new MerchantViewPagerFragment();
                Bundle b = new Bundle();
                b.putSerializable("jsonMerchant", topics);
                b.putInt("position", position);
                merchantViewPagerFragment.setArguments(b);
                LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout,
                        merchantViewPagerFragment, "MerchantViewPagerFragment");
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(ContextCompat.getColor(
                        getActivity(), R.color.pressed_color));
                selected_pos = position;

            }
        });
    }

    private void subscribeTopics() {
        if (null != topics && topics.size() > 0) {
            for (int i = 0; i < topics.size(); i++) {
                FirebaseMessaging.getInstance().subscribeToTopic(topics.get(i).getTopic()+ "_A");
                FirebaseMessaging.getInstance().subscribeToTopic(topics.get(i).getTopic() + "_M_A");
            }
        }
    }

    public void unSubscribeTopics() {
        if (null != topics && topics.size() > 0) {
            for (int i = 0; i < topics.size(); i++) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topics.get(i).getTopic()+"_A");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topics.get(i).getTopic() + "_M_A");
            }
        }
    }

    @Override
    public void passDataToFragment(String qrcodeValue, String current_serving, String status, String lastno, String payload) {
        for (int i = 0; i < topics.size(); i++) {
            JsonTopic jt = topics.get(i);
            if (jt.getCodeQR().equalsIgnoreCase(qrcodeValue)) {

                if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.M.getName())) {
                    if(QueueStatusEnum.valueOf(status).equals(QueueStatusEnum.S)){
                        jt.setToken(Integer.parseInt(lastno));
                    }else{
                        if(Integer.parseInt(lastno)>=jt.getToken()){
                            jt.setToken(Integer.parseInt(lastno));
                        }
                    }
                }else{
                    if(Integer.parseInt(lastno)>=jt.getToken()){
                        jt.setToken(Integer.parseInt(lastno));
                    }
                }
                jt.setQueueStatus(QueueStatusEnum.valueOf(status));
                //jt.setToken(Integer.parseInt(lastno));
                topics.set(i, jt);
                adapter.notifyDataSetChanged();
                if (null != merchantViewPagerFragment) {
                    merchantViewPagerFragment.updateListData(topics);
                }
                break;
            }
        }
    }

    @Override
    public void onMethodCallback(JsonToken token) {
        if (null != token) {
            for (int i = 0; i < topics.size(); i++) {
                JsonTopic jt = topics.get(i);
                if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                    jt.setToken(token.getToken());
                    jt.setQueueStatus(token.getQueueStatus());
                    jt.setServingNumber(token.getServingNumber());
                    topics.set(i, jt);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void updateListData(List<JsonTopic> jsonTopics) {
        topics = new ArrayList<JsonTopic>();
        topics.addAll(jsonTopics);
    }
}
