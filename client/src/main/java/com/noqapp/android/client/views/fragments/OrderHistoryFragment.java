package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.NearMeModel;
import com.noqapp.android.client.model.OrderQueueHistoryModel;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.OrderHistoryPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistoricalList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.OrderHistoryDetailActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.adapters.OrderHistoryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OnItemClickListener, OrderHistoryPresenter {
    private RecyclerView rcv_order_history;
    private ArrayList<JsonPurchaseOrderHistorical> listData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        rcv_order_history = view.findViewById(R.id.rcv_order_history);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            OrderQueueHistoryModel orderQueueHistoryModel = new OrderQueueHistoryModel();
            orderQueueHistoryModel.setOrderHistoryPresenter(this);
            orderQueueHistoryModel.orders(UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        listData = new ArrayList<>();
        rcv_order_history.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcv_order_history.setLayoutManager(horizontalLayoutManagaer);
        rcv_order_history.setItemAnimator(new DefaultItemAnimator());
        // rcv_order_history.addItemDecoration(new VerticalSpaceItemDecoration(2));

        return view;
    }

    @Override
    public void onStoreItemClick(JsonPurchaseOrderHistorical item, View view, int pos) {
        // open order screen
        Intent intent = new Intent(getActivity(), OrderHistoryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", item);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej)
            new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        //dismissProgress();
        AppUtilities.authenticationProcessing(getActivity());
    }

    @Override
    public void orderHistoryResponse(JsonPurchaseOrderHistoricalList jsonPurchaseOrderHistoricalList) {
        listData = new ArrayList<>(jsonPurchaseOrderHistoricalList.getJsonPurchaseOrderHistoricals());
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(listData, getActivity(), this);
        rcv_order_history.setAdapter(orderHistoryAdapter);
    }
}
