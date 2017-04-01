package com.noqapp.client.views.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.NoQueueDBPresenter;
import com.noqapp.client.presenter.TokenAndQueuePresenter;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.interfaces.Token_QueueViewInterface;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListQueueFragment extends Fragment implements TokenAndQueuePresenter, Token_QueueViewInterface {

    public String codeQR;
    private static String TAG = ListQueueFragment.class.getSimpleName();
    private static FragmentActivity context;

    public ListQueueFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance() {
        return new ListQueueFragment();
    }

    public void callQueue() {
        QueueModel.tokenAndQueuePresenter = this;
        QueueModel.getAllJoinedQueue(LaunchActivity.DID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        return inflater.inflate(R.layout.fragment_list_queue, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
    }


    @Override
    public void queueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        Log.d(TAG, "Tokent and Queue Response::" + tokenAndQueues.toString());
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(ListQueueFragment.context);
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveToken_Queue(tokenAndQueues);

    }

    @Override
    public void queueError() {
        Log.d(TAG, "Token and queue Error");

    }

    @Override
    public void dataSavedStatus(String msg) {

        Log.d(TAG, msg);

    }
}
