package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.OrderQueueHistoryModel;
import com.noqapp.android.client.presenter.QueueHistoryPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.presenter.beans.JsonQueueHistoricalList;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.adapters.QueueHistoryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class QueueHistoryFragment extends Fragment implements QueueHistoryAdapter.OnItemClickListener, QueueHistoryPresenter {
    private RecyclerView rcv_order_history;
    private ArrayList<JsonQueueHistorical> listData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        rcv_order_history = view.findViewById(R.id.rcv_order_history);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            OrderQueueHistoryModel orderQueueHistoryModel = new OrderQueueHistoryModel();
            orderQueueHistoryModel.setQueueHistoryPresenter(this);
            orderQueueHistoryModel.queues(UserUtils.getEmail(), UserUtils.getAuth());
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
    public void onStoreItemClick(JsonQueueHistorical item) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                b.putSerializable("BizStoreElastic", null);
                Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", null);
                intent.putExtras(bundle);
                startActivity(intent);
        }
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
    public void queueHistoryResponse(JsonQueueHistoricalList jsonQueueHistoricalList) {
        listData = new ArrayList<>(jsonQueueHistoricalList.getQueueHistoricals());
        //add all items
        QueueHistoryAdapter queueHistoryAdapter = new QueueHistoryAdapter(listData, getActivity(), this);
        rcv_order_history.setAdapter(queueHistoryAdapter);
        if (null != listData && listData.size() == 0)
            Toast.makeText(getActivity(), "You havn't join any Queue yet :(", Toast.LENGTH_LONG).show();
    }


}
