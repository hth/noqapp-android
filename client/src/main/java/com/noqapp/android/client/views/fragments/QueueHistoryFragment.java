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
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.QueueHistoryDetailActivity;
import com.noqapp.android.client.views.adapters.QueueHistoryAdapter;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

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
        if (new NetworkUtil(getActivity()).isOnline()) {
            if (UserUtils.isLogin()) {
                setProgressMessage("Fetching Queue history...");
                showProgress();
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
    public void queueHistoryResponse(JsonQueueHistoricalList jsonQueueHistoricalList) {
        listData = new ArrayList<>(jsonQueueHistoricalList.getQueueHistoricals());
        Collections.sort(listData, (o1, o2) -> {
            try {
                return Objects.requireNonNull(CommonHelper.SDF_ISO8601_FMT.parse(o2.getCreated())).
                    compareTo(CommonHelper.SDF_ISO8601_FMT.parse(o1.getCreated()));
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
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
