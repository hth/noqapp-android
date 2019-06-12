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
import com.noqapp.android.client.presenter.OrderHistoryPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistoricalList;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.OrderHistoryDetailActivity;
import com.noqapp.android.client.views.adapters.OrderHistoryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.ArrayList;


public class OrderHistoryFragment extends BaseFragment implements
        OrderHistoryAdapter.OnItemClickListener, OrderHistoryPresenter {
    private RecyclerView rcv_order_history;
    private ArrayList<JsonPurchaseOrderHistorical> listData;
    private RelativeLayout rl_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        rcv_order_history = view.findViewById(R.id.rcv_order_history);
        rl_empty = view.findViewById(R.id.rl_empty);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            if (UserUtils.isLogin()) {
                progressDialog.setMessage("Fetching order history...");
                progressDialog.show();
                OrderQueueHistoryApiCall orderQueueHistoryModel = new OrderQueueHistoryApiCall();
                orderQueueHistoryModel.setOrderHistoryPresenter(this);
                orderQueueHistoryModel.orders(UserUtils.getEmail(), UserUtils.getAuth());
            } else {
                new CustomToast().showToast(getActivity(), "Please login to see the details");
            }
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        listData = new ArrayList<>();
        rcv_order_history.setHasFixedSize(true);
        rcv_order_history.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        rcv_order_history.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onStoreItemClick(JsonPurchaseOrderHistorical item, View view, int pos) {
        Intent intent = new Intent(getActivity(), OrderHistoryDetailActivity.class);
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
    public void orderHistoryResponse(JsonPurchaseOrderHistoricalList jsonPurchaseOrderHistoricalList) {
        listData = new ArrayList<>(jsonPurchaseOrderHistoricalList.getJsonPurchaseOrderHistoricals());
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(listData, getActivity(), this);
        rcv_order_history.setAdapter(orderHistoryAdapter);
        if (null != listData && listData.size() == 0 && null != getActivity()) {
            rl_empty.setVisibility(View.VISIBLE);
        }else{
            rl_empty.setVisibility(View.GONE);
        }
        dismissProgress();
    }
}
