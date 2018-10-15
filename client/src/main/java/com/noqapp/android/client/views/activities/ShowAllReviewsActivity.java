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
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ShowAllReviewsActivity extends BaseActivity implements AllReviewPresenter {

    private RecyclerView rv_all_review;
    private TextView tv_empty,tv_review_label;
    private List<JsonReview> jsonReviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_review);
        initActionsViews(true);
        tv_toolbar_title.setText("");
        rv_all_review = findViewById(R.id.rv_all_review);
        tv_empty = findViewById(R.id.tv_empty);
        tv_review_label = findViewById(R.id.tv_review_label);
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
            jsonReviews = (List<JsonReview>) getIntent().getExtras().getSerializable("data");
            String storeName = bundle.getStringExtra("storeName");
            String storeAddress = bundle.getStringExtra("storeAddress");
            TextView tv_store_name = findViewById(R.id.tv_store_name);
            TextView tv_address = findViewById(R.id.tv_address);
            tv_store_name.setText(storeName);
            tv_address.setText(storeAddress);
            if (null != jsonReviews) {
                updateUI();
                tv_address.setVisibility(View.GONE);
            } else {
                jsonReviews = new ArrayList<>();
                String codeQR = bundle.getStringExtra(NoQueueBaseActivity.KEY_CODE_QR);
                if (NetworkUtils.isConnectingToInternet(ShowAllReviewsActivity.this)) {
                    ReviewModel reviewModel = new ReviewModel();
                    reviewModel.setAllReviewPresenter(this);
                    if (bundle.getBooleanExtra("isLevelUp", false)) {
                        reviewModel.reviewsLevelUp(UserUtils.getDeviceId(), codeQR);
                    } else {
                        reviewModel.review(UserUtils.getDeviceId(), codeQR);
                    }
                    progressDialog.setMessage("Getting Reviews...");
                    progressDialog.show();

                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
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
        updateUI();
    }

    private void updateUI() {
        int ratingCount = 0;
        int listSize = 0;
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
        if (null != jsonReviews && jsonReviews.size() > 0) {
            List<JsonReview> temp = new ArrayList<>();
            listSize = jsonReviews.size();
            for (int i = 0; i < jsonReviews.size(); i++) {
                ratingCount += jsonReviews.get(i).getRatingCount();
                if (!TextUtils.isEmpty(jsonReviews.get(i).getReview()))
                    temp.add(jsonReviews.get(i));
            }
            jsonReviews = temp;
        }
        if (null == jsonReviews || jsonReviews.size() <= 0) {
            rv_all_review.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            rv_all_review.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
            tv_review_label.setText("" + jsonReviews.size() + " Ratings with reviews");
            tv_review_label.setPaintFlags(tv_review_label.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            try {
                float f = ratingCount * 1.0f /
                        listSize;
                ratingBar.setRating(f);
                TextView tv_rating = findViewById(R.id.tv_rating);
                tv_rating.setText(String.valueOf(AppUtilities.round(f)));
            } catch (Exception e) {
                
                e.printStackTrace();
            }
        }


        ShowAllReviewsAdapter showAllReviewsAdapter = new ShowAllReviewsAdapter(jsonReviews, this);
        rv_all_review.setAdapter(showAllReviewsAdapter);
    }
}
