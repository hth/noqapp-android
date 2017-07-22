package com.noqapp.android.merchant.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.GetTimeAgoUtils;
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

public class MerchantListFragment extends Fragment implements TopicPresenter, FragmentCommunicator, AdapterCallback, SwipeRefreshLayout.OnRefreshListener {

    public static int selected_pos = -1;
    public MerchantViewPagerFragment merchantViewPagerFragment;
    private Handler timerHandler;
    private MerchantListAdapter adapter;
    private ArrayList<JsonTopic> topics;
    private ListView listview;
    private RelativeLayout rl_empty_screen;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Runnable updater;
    private Snackbar snackbar;
    private boolean isFragmentVisible = false;

    public MerchantListFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
        ((LaunchActivity) context).fragmentCommunicator = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantlist, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        Bundle bundle = getArguments();
        ViewPagerAdapter.setAdapterCallBack(this);

        snackbar = Snackbar.make(listview, "", Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundResource(R.drawable.red_gredient);

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                // recursively call this method again when the snackbar was dismissed through a swipe
                if (event == DISMISS_EVENT_SWIPE) {
                }
            }
        });

        timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                updateSnackbarTxt();// update the snakebar after every minute
                timerHandler.postDelayed(updater, 60000);
            }
        };
        timerHandler.post(updater);
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
        LaunchActivity.getLaunchActivity().enableLogout();
        isFragmentVisible = true;
        updateSnackbarTxt();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // timerHandler.removeCallbacks(updater);
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
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void queueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            LaunchActivity.getLaunchActivity().clearLoginData();
        }
    }


    private void initListView() {
        rl_empty_screen.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        adapter = new MerchantListAdapter(getActivity(), topics);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!new AppUtils().isTablet(getActivity())) {
                    merchantViewPagerFragment = new MerchantViewPagerFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("jsonMerchant", topics);
                    b.putInt("position", position);
                    merchantViewPagerFragment.setArguments(b);
                    LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout, merchantViewPagerFragment, "MerchantViewPagerFragment");
                } else {
                    merchantViewPagerFragment.setPage(position);
                }
                for (int j = 0; j < parent.getChildCount(); j++) {
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                }
                // change the background color of the selected element
                view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pressed_color));
                selected_pos = position;
            }
        });
        LaunchActivity.getLaunchActivity().setLastUpdateTime(System.currentTimeMillis());
        updateSnackbarTxt();
        snackbar.show();


        if (new AppUtils().isTablet(getActivity())) {
            merchantViewPagerFragment = new MerchantViewPagerFragment();
            Bundle b = new Bundle();
            b.putSerializable("jsonMerchant", topics);
            b.putInt("position", 0);
            merchantViewPagerFragment.setArguments(b);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.list_detail_fragment, merchantViewPagerFragment);
            //  fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void subscribeTopics() {
        if (null != topics && topics.size() > 0) {
            for (int i = 0; i < topics.size(); i++) {
                FirebaseMessaging.getInstance().subscribeToTopic(topics.get(i).getTopic() + "_A");
                FirebaseMessaging.getInstance().subscribeToTopic(topics.get(i).getTopic() + "_M_A");
            }
        }
    }

    public void unSubscribeTopics() {
        if (null != topics && topics.size() > 0) {
            for (int i = 0; i < topics.size(); i++) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topics.get(i).getTopic() + "_A");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topics.get(i).getTopic() + "_M_A");
            }
        }
    }

    @Override
    public void passDataToFragment(String codeQR, String current_serving, String status, String lastNumber, String payload) {
        try {
            for (int i = 0; i < topics.size(); i++) {
                JsonTopic jt = topics.get(i);
                if (jt.getCodeQR().equalsIgnoreCase(codeQR)) {

                    if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.M.getName())) {
                        if (QueueStatusEnum.valueOf(status).equals(QueueStatusEnum.S)) {
                            jt.setToken(Integer.parseInt(lastNumber));
                            jt.setServingNumber(Integer.parseInt(current_serving));
                        } else {
                            if (Integer.parseInt(lastNumber) >= jt.getToken()) {
                                jt.setToken(Integer.parseInt(lastNumber));
                            }
                        }
                        /* Update only from merchant msg. */
                        jt.setQueueStatus(QueueStatusEnum.valueOf(status));
                    } else {
                        if (Integer.parseInt(lastNumber) >= jt.getToken()) {
                            jt.setToken(Integer.parseInt(lastNumber));
                        }
                    }
                    //jt.setToken(Integer.parseInt(lastno));
                    topics.set(i, jt);
                    adapter.notifyDataSetChanged();
                    if (null != merchantViewPagerFragment) {
                        merchantViewPagerFragment.updateListData(topics);
                    }
                    LaunchActivity.getLaunchActivity().setLastUpdateTime(System.currentTimeMillis());
                    updateSnackbarTxt();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onRefresh() {
        //Refresh the ListView after pull
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            swipeRefreshLayout.setRefreshing(true);
            ManageQueueModel.topicPresenter = this;
            ManageQueueModel.getQueues(
                    LaunchActivity.getLaunchActivity().getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth());
        } else {
            Toast.makeText(getActivity(), getString(R.string.networkerror), Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateSnackbarTxt() {
        if (isFragmentVisible)
            snackbar.setText(getString(R.string.last_update) + " " + GetTimeAgoUtils.getTimeAgo(LaunchActivity.getLaunchActivity().getLastUpdateTime()));
    }

}
