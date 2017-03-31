package com.noqapp.client.views.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.views.activities.LaunchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListQueueFragment extends Fragment implements QueuePresenter {

    public  String codeQR ;
    public ListQueueFragment() {
        // Required empty public constructor
    }
    public static Fragment getInstance()
    {
        return new ListQueueFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_queue, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ListQueueFragment.class.getSimpleName(), "onResume: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ListQueueFragment.class.getSimpleName(), "onResume: ");
//        if (codeQR != null){
//        QueueModel.queuePresenter = ListQueueFragment.this;
//        QueueModel.joinQueue(LaunchActivity.DID,codeQR);}
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(ListQueueFragment.class.getSimpleName(), "onActivityCreated: ");
    }

    @Override
    public void queueResponse(JsonQueue queue) {

        Log.d(ListQueueFragment.class.getSimpleName(),queue.toString());
    }

    @Override
    public void queueError() {
        Log.d(ListQueueFragment.class.getSimpleName(),"Error");
    }

    public void callQueue()
    {
        if (codeQR != null){
            Log.d("code qr ::",codeQR);
            QueueModel.queuePresenter = ListQueueFragment.this;
            QueueModel.joinQueue(LaunchActivity.DID,codeQR);}
    }
}
