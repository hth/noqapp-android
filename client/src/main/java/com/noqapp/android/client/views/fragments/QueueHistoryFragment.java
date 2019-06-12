package com.noqapp.android.client.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.OrderQueueHistoryApiCall;
import com.noqapp.android.client.presenter.QueueHistoryPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.presenter.beans.JsonQueueHistoricalList;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.QueueHistoryDetailActivity;
import com.noqapp.android.client.views.adapters.QueueHistoryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.ArrayList;

public class QueueHistoryFragment extends BaseFragment implements
        QueueHistoryAdapter.OnItemClickListener, QueueHistoryPresenter {
    private RecyclerView rcv_order_history;
    private ArrayList<JsonQueueHistorical> listData;
    private RelativeLayout rl_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        rcv_order_history = view.findViewById(R.id.rcv_order_history);
        rl_empty = view.findViewById(R.id.rl_empty);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            if (UserUtils.isLogin()) {
                progressDialog.setMessage("Fetching Queue history...");
                progressDialog.show();
                OrderQueueHistoryApiCall orderQueueHistoryModel = new OrderQueueHistoryApiCall();
                orderQueueHistoryModel.setQueueHistoryPresenter(this);
                orderQueueHistoryModel.queues(UserUtils.getEmail(), UserUtils.getAuth());
            } else {
                new CustomToast().showToast(getActivity(), "Please login to see the details");
            }
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        listData = new ArrayList<>();
        rcv_order_history.setHasFixedSize(true);
        rcv_order_history.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rcv_order_history.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onStoreItemClick(JsonQueueHistorical item) {
        Intent intent = new Intent(getActivity(), QueueHistoryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IBConstant.KEY_DATA, item);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(getActivity());
    }


    @Override
    public void queueHistoryResponse(JsonQueueHistoricalList jsonQueueHistoricalList) {
        listData = new ArrayList<>(jsonQueueHistoricalList.getQueueHistoricals());
        //add all items
        QueueHistoryAdapter queueHistoryAdapter = new QueueHistoryAdapter(listData, getActivity(), this);
        rcv_order_history.setAdapter(queueHistoryAdapter);
        if (null != listData && listData.size() == 0 && null != getActivity()) {
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
        dismissProgress();
    }

}
