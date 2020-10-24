package com.noqapp.android.merchant.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonReviewBucket;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.QReviewListActivity;
import com.noqapp.android.merchant.views.adapters.QueueReviewCardAdapter;
import com.noqapp.android.merchant.views.interfaces.AllReviewPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantReviewQListFragment extends BaseFragment implements QueueReviewCardAdapter.OnItemClickListener, AllReviewPresenter {
    private RecyclerView rcv_review;
    private List<JsonReviewList> jsonReviewLists = new ArrayList<>();

    public MerchantReviewQListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_merchant_qreview, container, false);
        rcv_review = view.findViewById(R.id.rcv_review);
        rcv_review.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rcv_review.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    public void updateUI(JsonProfessionalProfilePersonal temp) {
        if (new NetworkUtil(getActivity()).isOnline()) {
            showProgress();
            setProgressMessage("Getting reviews...");
            MerchantProfileApiCalls merchantProfileApiCalls = new MerchantProfileApiCalls();
            merchantProfileApiCalls.setAllReviewPresenter(this);
            merchantProfileApiCalls.allReviews(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void currentItemClick(JsonReviewList jsonReviewList) {
        Intent in = new Intent(getActivity(), QReviewListActivity.class);
        in.putExtra("data", jsonReviewList);
        startActivity(in);
    }

    @Override
    public void allReviewResponse(JsonReviewBucket jsonReviewBucket) {
        dismissProgress();
        ArrayList<JsonTopic> topics = LaunchActivity.merchantListFragment.getTopics();
        Map<String, String> nameList = new HashMap<>();
        for (int j = 0; j < topics.size(); j++) {
            nameList.put(topics.get(j).getCodeQR(), topics.get(j).getDisplayName());
        }
        jsonReviewLists = jsonReviewBucket.getJsonReviewLists();
        for (int i = 0; i < jsonReviewLists.size(); i++) {
            jsonReviewLists.get(i).setDisplayName(nameList.get(jsonReviewLists.get(i).getCodeQR()));
        }
        QueueReviewCardAdapter queueReviewCardAdapter = new QueueReviewCardAdapter(jsonReviewLists, getActivity(), this);
        rcv_review.setAdapter(queueReviewCardAdapter);
        Log.e("data", jsonReviewLists.toString());
    }
}
