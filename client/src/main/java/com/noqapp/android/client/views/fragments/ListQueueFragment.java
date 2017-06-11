package com.noqapp.android.client.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.NoQueueDBPresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.DeviceToken;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.adapters.ListQueueAdapter;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chandra on 5/14/17.
 * <p>
 * Calls -
 * Step 1- fetch the current & history queue data from local DB first
 * Step 2- fetch the current & history queue data from server
 * Step 3- update the DB
 * Step 4- update the list
 **/
public class ListQueueFragment extends Scanner implements TokenAndQueuePresenter, TokenQueueViewInterface {

    private static final int MSG_CURRENT_QUEUE = 0;
    private static final int MSG_HISTORY_QUEUE = 1;
    private static Context context;
    private static QueueHandler mHandler;
    private static TokenQueueViewInterface tokenQueueViewInterface;
    private RelativeLayout rl_empty_screen;
    private String TAG = ListQueueFragment.class.getSimpleName();
    private ListQueueAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<JsonTokenAndQueue>> listDataChild;
    private ViewGroup header, footer;

    public ListQueueFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_listqueue, container, false);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        //ButterKnife.bind(this,view);

        header = (ViewGroup) inflater.inflate(R.layout.listview_header, expListView, false);
        Button btn_scn = (Button) header.findViewById(R.id.btnScanQRCode);
        btn_scn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanningBarcode();
            }
        });
        footer = (ViewGroup) inflater.inflate(R.layout.listview_footer, expListView, false);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        tokenQueueViewInterface = this;
        //fetch the
        fetchCurrentAndHistoryList();
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            mHandler = new QueueHandler();

            if (UserUtils.isLogin()) { // Call secure API if user is loggedIn else normal API
                //Call the current queue
                QueueApiModel.tokenAndQueuePresenter = this;
                QueueApiModel.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());

                //Call the history queue
                DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
                QueueApiModel.allHistoricalJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
            } else {
                //Call the current queue
                QueueModel.tokenAndQueuePresenter = this;
                QueueModel.getAllJoinedQueue(UserUtils.getDeviceId());

                //Call the history queue
                DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
                QueueModel.getHistoryQueueList(UserUtils.getDeviceId(), deviceToken);
            }


        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.tab_list));
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    protected void barcodeResult(String codeQR) {
        Bundle b = new Bundle();
        b.putString(KEY_CODE_QR, codeQR);
        b.putBoolean(KEY_FROM_LIST, true);
        b.putBoolean(KEY_IS_HISTORY, false);
        JoinFragment jf = new JoinFragment();
        jf.setArguments(b);
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG, LaunchActivity.tabList);
    }

    @Override
    public void currentQueueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveTokenQueue(tokenAndQueues, true);
    }

    @Override
    public void historyQueueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveTokenQueue(tokenAndQueues, false);
    }

    @Override
    public void historyQueueError() {
        Log.d(TAG, "Token and queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(false);
    }

    @Override
    public void currentQueueError() {
        Log.d(TAG, "Token and queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(true);
    }


    @Override
    public void currentQueueSaved() {
        passMsgToHandler(true);
    }

    @Override
    public void historyQueueSaved() {
        passMsgToHandler(false);
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
        listDataHeader.add("Current Queue");
        listDataHeader.add("QHistory");
        listDataChild.put(listDataHeader.get(0), currentlist); // Header, Child data
        listDataChild.put(listDataHeader.get(1), historylist);
        listAdapter = null;
        expListView.setAdapter(listAdapter);
        listAdapter = new ListQueueAdapter(getActivity(), listDataHeader, listDataChild);

        if (currentlist.size() == 0 && expListView.getHeaderViewsCount() == 0) {
            //header.setVisibility(View.VISIBLE);

            expListView.addHeaderView(header, null, false);
        } else if (currentlist.size() > 0) {
            // header.setVisibility(View.GONE);
            expListView.removeHeaderView(header);
        }
        if (historylist.size() == 0 && expListView.getFooterViewsCount() == 0) {
            //footer.setVisibility(View.VISIBLE);
            expListView.addFooterView(footer, null, false);
        } else if (historylist.size() > 0) {
            expListView.removeFooterView(footer);
        }
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);//By default expand the list first group
        expListView.expandGroup(1);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                JsonTokenAndQueue jsonQueue = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                Bundle b = new Bundle();
                b.putString(KEY_CODE_QR, jsonQueue.getCodeQR());
                b.putBoolean(KEY_FROM_LIST, true);
                if (groupPosition == 0) {
                    b.putSerializable(KEY_JSON_TOKEN_QUEUE, jsonQueue);
                    AfterJoinFragment ajf = new AfterJoinFragment();
                    ajf.setArguments(b);
                    replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, ajf, TAG, LaunchActivity.tabList);
                } else {
                    JoinFragment jf = new JoinFragment();
                    b.putBoolean(KEY_IS_HISTORY, true);
                    b.putBoolean(KEY_IS_AUTOJOIN_ELIGIBLE,false);
                    jf.setArguments(b);
                    replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG, LaunchActivity.tabList);
                }
                return false;
            }
        });
    }

    private void passMsgToHandler(boolean isCurrentQueue) {
        // pass msg to handler to load the data from DB
        if (isCurrentQueue) {
            Message msg = new Message();
            msg.what = MSG_CURRENT_QUEUE;
            mHandler.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = MSG_HISTORY_QUEUE;
            mHandler.sendMessage(msg);
        }
    }

    private static class QueueHandler extends Handler {
        private boolean isCurrentExecute = false;
        private boolean isHistoryExecute = false;

        // This method is used to handle received messages
        public void handleMessage(Message msg) {
            // switch to identify the message by its code
            switch (msg.what) {
                case MSG_CURRENT_QUEUE:
                    //doSomething();
                    isCurrentExecute = true;
                    if (isHistoryExecute && isCurrentExecute) {
                        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
                        dbPresenter.tokenQueueViewInterface = tokenQueueViewInterface;
                        dbPresenter.getCurrentAndHistoryTokenQueueListFromDB();
                    }
                    break;

                case MSG_HISTORY_QUEUE:
                    //doMoreThings();
                    isHistoryExecute = true;
                    if (isHistoryExecute && isCurrentExecute) {
                        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
                        dbPresenter.tokenQueueViewInterface = tokenQueueViewInterface;
                        dbPresenter.getCurrentAndHistoryTokenQueueListFromDB();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
