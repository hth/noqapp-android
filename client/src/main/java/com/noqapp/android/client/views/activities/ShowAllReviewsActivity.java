package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ReviewModel;
import com.noqapp.android.client.presenter.AllReviewPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.ShowAllReviewsAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ShowAllReviewsActivity extends BaseActivity implements AllReviewPresenter {

    private RecyclerView rv_all_review;
    private TextView tv_empty;
    private List<JsonReview> jsonReviews = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_review);
        initActionsViews(true);
        tv_toolbar_title.setText("");
        rv_all_review = findViewById(R.id.rv_all_review);
        tv_empty = findViewById(R.id.tv_empty);
        if (jsonReviews.size() <= 0) {
            rv_all_review.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            rv_all_review.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_all_review.setLayoutManager(horizontalLayoutManagaer);
        rv_all_review.setItemAnimator(new DefaultItemAnimator());
        Intent bundle = getIntent();
        if (null != bundle) {
            String codeQR = bundle.getStringExtra(NoQueueBaseActivity.KEY_CODE_QR);
            String storeName = bundle.getStringExtra("storeName");
            String storeAddress = bundle.getStringExtra("storeAddress");
            TextView tv_store_name = findViewById(R.id.tv_store_name);
            TextView tv_address = findViewById(R.id.tv_address);
            tv_store_name.setText(storeName);
            tv_address.setText(storeAddress);
            if (NetworkUtils.isConnectingToInternet(ShowAllReviewsActivity.this)) {
                    ReviewModel reviewModel = new ReviewModel();
                    reviewModel.setAllReviewPresenter(this);
                    reviewModel.review(UserUtils.getDeviceId(), codeQR);
                    progressDialog.setMessage("Getting Reviews...");
                    progressDialog.show();

            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }

    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void allReviewResponse(JsonReviewList jsonReviewList) {
        dismissProgress();
        if (null != jsonReviewList && jsonReviewList.getJsonReviews().size() > 0)
            jsonReviews = jsonReviewList.getJsonReviews();
        ShowAllReviewsAdapter showAllReviewsAdapter = new ShowAllReviewsAdapter(jsonReviews, this);
        rv_all_review.setAdapter(showAllReviewsAdapter);
    }
}
