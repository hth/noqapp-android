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
import com.noqapp.client.presenter.TokenPresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.beans.JsonToken;
import com.noqapp.client.views.activities.LaunchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListQueueFragment extends Fragment implements TokenPresenter {

    public  String codeQR ;
    private static String TAG = ListQueueFragment.class.getSimpleName();
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
        Log.i(TAG,"onResume: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume: ");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
    }




    public void callQueue()
    {
        if (codeQR != null){
            Log.d("code qr ::",codeQR);
            QueueModel.tokenPresenter = ListQueueFragment.this;
            QueueModel.joinQueue(LaunchActivity.DID,codeQR);}
    }

    @Override
    public void queueResponse(JsonToken token) {

        Log.d(TAG,token.toString());

    }

    @Override
    public void queueError() {
        Log.d(TAG,"Error");

    }
}
