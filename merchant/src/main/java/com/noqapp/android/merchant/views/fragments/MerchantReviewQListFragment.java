package com.noqapp.android.merchant.views.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.QReviewListActivity;
import com.noqapp.android.merchant.views.adapters.QueueReviewCardAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MerchantReviewQListFragment extends Fragment implements QueueReviewCardAdapter.OnItemClickListener{
    private RecyclerView rcv_review;
    private ProgressDialog progressDialog;
    private List<JsonReviewList> wholeList = new ArrayList<>();

    public MerchantReviewQListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_qreview, container, false);
        rcv_review = view.findViewById(R.id.rcv_review);
        initProgress();
        rcv_review.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rcv_review.setItemAnimator(new DefaultItemAnimator());

//        if (isFirstTime) {
//            if (LaunchActivity.getLaunchActivity().isOnline()) {
//                progressDialog.show();
//                merchantStatsApiCalls.healthCare(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
//                isFirstTime = false;
//            } else {
//                ShowAlertInformation.showNetworkDialog(getActivity());
//            }
//        }
        return view;
    }

    public void updateUI(JsonProfessionalProfilePersonal temp) {
        Map<String, JsonReviewList> data = temp.getReviews();
        for (Map.Entry<String, JsonReviewList> entry : data.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
            wholeList.add(entry.getValue());
        }
        QueueReviewCardAdapter queueReviewCardAdapter = new QueueReviewCardAdapter(wholeList, getActivity(), this);
        rcv_review.setAdapter(queueReviewCardAdapter);
    }


//
//    @Override
//    public void authenticationFailure() {
//        dismissProgress();
//        AppUtils.authenticationProcessing();
//    }
//
//    @Override
//    public void responseErrorPresenter(ErrorEncounteredJson eej) {
//        dismissProgress();
//        new ErrorResponseHandler().processError(getActivity(), eej);
//    }
//
//    @Override
//    public void responseErrorPresenter(int errorCode) {
//        dismissProgress();
//        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
//    }


    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void currentItemClick(JsonReviewList jsonReviewList) {
        Intent in = new Intent(getActivity(), QReviewListActivity.class);
        in.putExtra("data",jsonReviewList);
        startActivity(in);
    }
}
