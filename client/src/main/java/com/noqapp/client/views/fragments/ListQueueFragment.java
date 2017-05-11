package com.noqapp.client.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.network.NoQueueFirebaseInstanceServices;
import com.noqapp.client.presenter.NoQueueDBPresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.utils.UserUtils;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.adapters.ListQueueAdapter;
import com.noqapp.client.views.interfaces.TokenQueueViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListQueueFragment extends NoQueueBaseFragment implements TokenAndQueuePresenter, TokenQueueViewInterface {

    private RelativeLayout rl_empty_screen;
    public static boolean isCurrentQueueCall = false;
    private String TAG = ListQueueFragment.class.getSimpleName();
    private FragmentActivity context;
    private ListQueueAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<JsonTokenAndQueue>> listDataChild;
    private FrameLayout frame_scan;

    public ListQueueFragment() {

    }

    public static ListQueueFragment getInstance() {
        return new ListQueueFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void callQueue() {
        LaunchActivity.getLaunchActivity().progressDialog.show();
        QueueModel.tokenAndQueuePresenter = this;
        QueueModel.getAllJoinedQueue(UserUtils.getDeviceId());
        isCurrentQueueCall = true;
    }

    public void callQueueHistory() {
        QueueModel.tokenAndQueuePresenter = this;
        //Todo Check the flow of history queue
        //QueueModel.getAllHistoricalJoinedQueue(LaunchActivity.DID);
        DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
        //QueueModel.getAllHistoricalJoinedQueue("123", deviceToken);
        QueueModel.getAllHistoricalJoinedQueue(NoQueueFirebaseInstanceServices.createOrFindDeviceId(), deviceToken);
        //QueueModel.getAllJoinedQueue(LaunchActivity.DID);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_listqueue, container, false);
        frame_scan = (FrameLayout) view.findViewById(R.id.frame_scan);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        Bundle b = new Bundle();
        b.putBoolean(KEY_FROM_LIST, true);
        ScanQueueFragment sqc = new ScanQueueFragment();
        sqc.setArguments(b);
        replaceFragmentWithoutBackStack(getActivity(), R.id.frame_scan, sqc, TAG);
        //ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            callQueue();
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Queues");
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void queueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        Log.d(TAG, "TokenAndQueues size=" + tokenAndQueues.size());
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveTokenQueue(tokenAndQueues, isCurrentQueueCall);
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Token and queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void noCurrentQueue() {
        dataSavedStatus(0);
    }

    @Override
    public void noHistoryQueue() {
        dataSavedStatus(0);
    }

    @Override
    public void dataSavedStatus(int msg) {
        Log.d(TAG, String.valueOf(msg));
        if (isCurrentQueueCall) {
            isCurrentQueueCall = false;
            callQueueHistory();

        } else {
            fetchCurrentAndHistoryList();
        }

    }

    public void fetchCurrentAndHistoryList() {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.getCurrentAndHistoryTokenQueueListFromDB();
    }

    @Override
    public void tokenQueueList(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Current Queue Count : " + String.valueOf(currentlist.size()) + "::" + String.valueOf(historylist.size()));
        initListView(currentlist, historylist);
        rl_empty_screen.setVisibility(View.GONE);
        expListView.setVisibility(View.VISIBLE);
    }

    private void initListView(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<JsonTokenAndQueue>>();
        //currentlist.clear();
        //historylist.clear();
//        for (int i=0;i<15;i++){
//            currentlist.add(currentlist.get(0));
//        }
        // Adding child data
        listDataHeader.add("Current Queue");
        listDataHeader.add("History");
        listDataChild.put(listDataHeader.get(0), currentlist); // Header, Child data
        listDataChild.put(listDataHeader.get(1), historylist);

        listAdapter = new ListQueueAdapter(getActivity(), listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);//By default expand the list first group
        expListView.expandGroup(1);

        if (currentlist.size() == 0) {
            frame_scan.setVisibility(View.VISIBLE);
        } else {
            frame_scan.setVisibility(View.GONE);
        }
        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                JsonTokenAndQueue jsonQueue = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                Bundle b = new Bundle();
                b.putString(KEY_CODEQR, jsonQueue.getCodeQR());
                b.putBoolean(KEY_FROM_LIST, true);
                if (groupPosition == 0) {
                    b.putString(KEY_DISPLAYNAME, jsonQueue.getBusinessName());
                    b.putString(KEY_STOREPHONE, jsonQueue.getStorePhone());
                    b.putString(KEY_QUEUENAME, jsonQueue.getDisplayName());
                    b.putString(KEY_ADDRESS, jsonQueue.getStoreAddress());
                    b.putString(KEY_TOPIC, jsonQueue.getTopic());
                    b.putString(KEY_SERVING_NO, String.valueOf(jsonQueue.getServingNumber()));
                    b.putString(KEY_TOKEN, String.valueOf(jsonQueue.getToken()));
                    b.putString(KEY_HOW_LONG, String.valueOf(jsonQueue.afterHowLong()));
                    b.putString(KEY_COUNTRY_SHORT_NAME, jsonQueue.getCountryShortName());
                    AfterJoinFragment ajf = new AfterJoinFragment();
                    ajf.setArguments(b);
                    replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, ajf, TAG, LaunchActivity.tabList);
                } else {
                    JoinFragment jf = new JoinFragment();
                    jf.setArguments(b);
                    replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG, LaunchActivity.tabList);
                }
                return false;
            }
        });
    }
}
