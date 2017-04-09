package com.noqapp.client.views.fragments;


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

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.NoQueueDBPresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.adapters.ListqueueAdapter;
import com.noqapp.client.views.interfaces.Token_QueueViewInterface;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListQueueFragment extends Fragment implements TokenAndQueuePresenter, Token_QueueViewInterface {


    private RecyclerView listViewQueue;
    public static boolean isCurrentQueueCall = false;
    private String TAG = ListQueueFragment.class.getSimpleName();
    private FragmentActivity context;


    public ListQueueFragment() {
        // Required empty public constructor
    }

    public static ListQueueFragment getInstance() {
        return new ListQueueFragment();
    }

    public void callQueue() {
        QueueModel.tokenAndQueuePresenter = this;
        QueueModel.getAllJoinedQueue(LaunchActivity.DID);
        isCurrentQueueCall = true;
    }

    public void callQueueHistory() {
        QueueModel.tokenAndQueuePresenter = this;
        //Todo Check the flow of history queue
        //QueueModel.getAllHistoricalJoinedQueue(LaunchActivity.DID);
        QueueModel.getAllHistoricalJoinedQueue("123");
        //QueueModel.getAllJoinedQueue(LaunchActivity.DID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_list_queue, container, false);
        listViewQueue = (RecyclerView) view.findViewById(R.id.listView_quequList);
        //ButterKnife.bind(this,view);
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("List");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void dataSavedStatus(int msg) {
        Log.d(TAG, String.valueOf(msg));
        if (isCurrentQueueCall) {
            isCurrentQueueCall = false;
            callQueueHistory();

        } else {
            NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(context);
            dbPresenter.tokenQueueViewInterface = this;
            dbPresenter.currentandHistoryTokenQueueListFromDB();
        }


    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist) {

        Log.d(TAG, "Current Queue Count : " + String.valueOf(currentlist.size()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        listViewQueue.setLayoutManager(layoutManager);
        listViewQueue.setHasFixedSize(true);
        listViewQueue.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        listViewQueue.setItemAnimator(new DefaultItemAnimator());
        ListqueueAdapter adapter = new ListqueueAdapter(context, currentlist, historylist);
        listViewQueue.setAdapter(adapter);


    }


}
