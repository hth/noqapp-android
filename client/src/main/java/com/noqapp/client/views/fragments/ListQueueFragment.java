package com.noqapp.client.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.NoQueueDBPresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.adapters.ListQueueAdapter;
import com.noqapp.client.views.interfaces.Token_QueueViewInterface;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListQueueFragment extends NoQueueBaseFragment implements TokenAndQueuePresenter, Token_QueueViewInterface {


    private RecyclerView listViewQueue;
    private RelativeLayout rl_empty_screen;
    public static boolean isCurrentQueueCall = false;
    private String TAG = ListQueueFragment.class.getSimpleName();
    private FragmentActivity context;


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
        QueueModel.getAllJoinedQueue(LaunchActivity.DID);
        isCurrentQueueCall = true;
    }

    public void callQueueHistory() {

        QueueModel.tokenAndQueuePresenter = this;
        //Todo Check the flow of history queue
        //QueueModel.getAllHistoricalJoinedQueue(LaunchActivity.DID);
        DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
        QueueModel.getAllHistoricalJoinedQueue("123", deviceToken);
        //QueueModel.getAllJoinedQueue(LaunchActivity.DID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_list_queue, container, false);
        listViewQueue = (RecyclerView) view.findViewById(R.id.listView_quequList);
        rl_empty_screen = (RelativeLayout) view.findViewById(R.id.rl_empty_screen);
        //ButterKnife.bind(this,view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (networkHelper.isOnline()) {
            callQueue();
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Queues");

    }


    @Override
    public void queueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        Log.d(TAG, "Tokent and Queue Response::" + tokenAndQueues.size());
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveToken_Queue(tokenAndQueues, isCurrentQueueCall);

    }

    @Override
    public void queueError() {
        Log.d(TAG, "Token and queue Error");

    }

    @Override
    public void noCurentQueue() {
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
        dbPresenter.currentandHistoryTokenQueueListFromDB();
    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Current Queue Count : " + String.valueOf(currentlist.size()) + "::" + String.valueOf(historylist.size()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        listViewQueue.setLayoutManager(layoutManager);
        listViewQueue.setHasFixedSize(true);
        listViewQueue.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        listViewQueue.setItemAnimator(new DefaultItemAnimator());
        ListQueueAdapter adapter = new ListQueueAdapter(context, currentlist, historylist);
        listViewQueue.setAdapter(adapter);
        rl_empty_screen.setVisibility(View.GONE);
        listViewQueue.setVisibility(View.VISIBLE);

    }


}
