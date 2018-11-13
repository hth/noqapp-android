package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.GetTimeAgoUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.AutocompleteAdapter;
import com.noqapp.android.merchant.views.adapters.MerchantListAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;
import com.noqapp.android.merchant.views.interfaces.TopicPresenter;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MerchantListFragment extends Fragment implements TopicPresenter, FragmentCommunicator, AdapterCallback, SwipeRefreshLayout.OnRefreshListener {

    public static int selected_pos = 0;
    public MerchantDetailFragment merchantDetailFragment;
    private Handler timerHandler;
    private MerchantListAdapter adapter;
    private AutocompleteAdapter temp_adapter;
    private HashMap<String, String> mHashmap = new HashMap<>();

    public ArrayList<JsonTopic> getTopics() {
        return topics;
    }

    private ArrayList<JsonTopic> topics = new ArrayList<>();
    private ListView listview;
    private RelativeLayout rl_empty_screen;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Runnable updater, run;
    private Snackbar snackbar;
    private boolean isFragmentVisible = false;
    private AutoCompleteTextView auto_complete_search;
    private ManageQueueModel manageQueueModel;

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
        manageQueueModel = new ManageQueueModel();
        manageQueueModel.setTopicPresenter(this);
        String strOutput = LaunchActivity.getLaunchActivity().getCounterName();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        if (StringUtils.isBlank(strOutput)) {
            mHashmap.clear();
        } else {
            try {
                mHashmap = gson.fromJson(strOutput, type);
            } catch (Exception e) {
                e.printStackTrace();
                mHashmap = new HashMap<>();
            }
        }
        if (mHashmap.size() == 0) {
            for (int i = 0; i < topics.size(); i++) {
                mHashmap.put(topics.get(i).getCodeQR(), "");
            }
        }
        listview = view.findViewById(R.id.listview);
        rl_empty_screen = view.findViewById(R.id.rl_empty_screen);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        auto_complete_search = view.findViewById(R.id.auto_complete_search);
        auto_complete_search.setThreshold(1);
        auto_complete_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (auto_complete_search.getRight() - auto_complete_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        hideAndReset();
                        return true;
                    }
                }
                return false;
            }
        });

        auto_complete_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (null != temp_adapter) {
                    String selectedQRcode = temp_adapter.getQRCode(pos);
                    for (int j = 0; j < topics.size(); j++) {
                        JsonTopic jt = topics.get(j);
                        if (selectedQRcode.equalsIgnoreCase(jt.getCodeQR())) {
                            hideAndReset();
                            listview.performItemClick(
                                    listview.getAdapter().getView(j, null, null),
                                    j,
                                    listview.getAdapter().getItemId(j));
                            break;
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        Bundle bundle = getArguments();
        MerchantDetailFragment.setAdapterCallBack(this);
        snackbar = Snackbar.make(listview, "", Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundResource(R.drawable.red_gredient);
        final View v = snackbar.getView();
        final TextView tv = v.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
        tv.setTextColor(Color.BLACK);

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
        run = new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
                listview.invalidateViews();
                listview.refreshDrawableState();
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
                manageQueueModel.getQueues(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth());
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
        return view;
    }

    private void hideAndReset() {
        auto_complete_search.setText("");
        new AppUtils().hideKeyBoard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
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
    public void topicPresenterResponse(JsonTopicList topiclist) {
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
            //header.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void topicPresenterError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
        AppUtils.authenticationProcessing();
    }


    private void initListView() {
        rl_empty_screen.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        Collections.sort(topics, Collections.reverseOrder(new CustomComparator()));
        adapter = new MerchantListAdapter(getActivity(), topics);
        listview.setAdapter(adapter);
        if (null != getActivity()) {
            temp_adapter = new AutocompleteAdapter(getActivity(),
                    R.layout.auto_text_item, topics);
            auto_complete_search.setAdapter(temp_adapter);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (new AppUtils().isTablet(getActivity())) {
                    merchantDetailFragment.setPage(position);
                } else {
                    merchantDetailFragment = new MerchantDetailFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("jsonMerchant", topics);
                    b.putInt("position", position);
                    merchantDetailFragment.setArguments(b);
                    LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout, merchantDetailFragment, "MerchantViewPagerFragment");
                }
                selected_pos = position;
                // to set the selected cell color
                getActivity().runOnUiThread(run);


            }
        });
        LaunchActivity.getLaunchActivity().setLastUpdateTime(System.currentTimeMillis());
        updateSnackbarTxt();
        snackbar.show();
        if (new AppUtils().isTablet(getActivity()) && topics.size() > 0) {
            merchantDetailFragment = new MerchantDetailFragment();
            Bundle b = new Bundle();
            b.putSerializable("jsonMerchant", topics);
            b.putInt("position", selected_pos);
            merchantDetailFragment.setArguments(b);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.list_detail_fragment, merchantDetailFragment);
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
    public void passDataToFragment(final String codeQR, final String current_serving, final String status, final String lastNumber, final String payload) {
        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //stuff that updates ui
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
                            temp_adapter = null;
                            if(null != getActivity()) {
                                temp_adapter = new AutocompleteAdapter(getActivity(), R.layout.auto_text_item, topics);
                                auto_complete_search.setAdapter(temp_adapter);
                            }
                            if (null != merchantDetailFragment) {
                                merchantDetailFragment.updateListData(topics);
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
        });
    }

    @Override
    public void acquireCustomer(JsonToken token) {
        try {
            if (null != token) {
                JsonTopic jt = topics.get(selected_pos);
                if (token.getCodeQR().equalsIgnoreCase(jt.getCodeQR())) {
                    jt.setServingNumber(token.getServingNumber());
                    topics.set(selected_pos, jt);
                    adapter.notifyDataSetChanged();
                    if (null != merchantDetailFragment) {
                        merchantDetailFragment.updateListData(topics);
                    }
                    temp_adapter = null;
                    if(null != getActivity()) {
                        temp_adapter = new AutocompleteAdapter(getActivity(), R.layout.auto_text_item, topics);
                        auto_complete_search.setAdapter(temp_adapter);
                    }
                    Toast.makeText(context, "Customer is acquired ", Toast.LENGTH_LONG).show();
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
                    temp_adapter = null;
                    if(null != getActivity()) {
                        temp_adapter = new AutocompleteAdapter(getActivity(), R.layout.auto_text_item, topics);
                        auto_complete_search.setAdapter(temp_adapter);
                    }
                    break;
                }
            }
        }
    }

    public void updateListData(List<JsonTopic> jsonTopics) {
        topics = new ArrayList<>();
        if (null != jsonTopics && jsonTopics.size() > 0)
            topics.addAll(jsonTopics);
    }

    @Override
    public void onRefresh() {
        //Refresh the ListView after pull
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            swipeRefreshLayout.setRefreshing(true);
            manageQueueModel.getQueues(
                    BaseLaunchActivity.getDeviceID(),
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

    public void saveCounterNames(String codeQR, String name) {
        mHashmap.put(codeQR, name);
        LaunchActivity.getLaunchActivity().setCounterName(mHashmap);
    }

    @Override
    public HashMap<String, String> getNameList() {
        return mHashmap;
    }

    public void clearData() {
        topics.clear();
        adapter.notifyDataSetChanged();
    }

    public class CustomComparator implements Comparator<JsonTopic> {
        @Override
        public int compare(JsonTopic o1, JsonTopic o2) {
            return o1.getRemaining() - o2.getRemaining();
        }
    }
}
