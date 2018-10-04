package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.NearMeModel;
import com.noqapp.android.client.model.OrderQueueHistoryModel;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.OrderHistoryPresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
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
    private ArrayList<JsonPurchaseOrder> listData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        rcv_order_history = view.findViewById(R.id.rcv_order_history);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            OrderQueueHistoryModel orderQueueHistoryModel = new OrderQueueHistoryModel();
            orderQueueHistoryModel.setOrderHistoryPresenter(this);
            orderQueueHistoryModel.orders(UserUtils.getEmail(),UserUtils.getAuth());
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
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                b.putSerializable("BizStoreElastic", item);
                Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                intent.putExtras(bundle);
                startActivity(intent);
        }
    }




    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
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
    public void orderHistoryResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        listData = new ArrayList<>(jsonPurchaseOrderList.getPurchaseOrders());
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(listData, getActivity(), this);
        rcv_order_history.setAdapter(orderHistoryAdapter);
    }

    @Override
    public void orderHistoryError() {

    }
}
