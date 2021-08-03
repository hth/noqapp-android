package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.open.ReviewImpl;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AllReviewsAdapter;
import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.presenter.AllReviewPresenter;

import java.util.ArrayList;
import java.util.List;

public class AllReviewsActivity extends BaseActivity implements AllReviewPresenter {

    private RecyclerView rv_all_review;
    private TextView tv_review_label;
    private List<JsonReview> jsonReviews = new ArrayList<>();
    private List<JsonReview> jsonReviewsOnlyText = new ArrayList<>();
    private RelativeLayout rl_empty;
    private SwitchCompat toggleShowAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_review);
        initActionsViews(true);
        tv_toolbar_title.setText("");
        rv_all_review = findViewById(R.id.rv_all_review);
        rl_empty = findViewById(R.id.rl_empty);
        tv_review_label = findViewById(R.id.tv_review_label);
        toggleShowAll = findViewById(R.id.toggleShowAll);
        toggleShowAll.setVisibility(View.INVISIBLE);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_all_review.setLayoutManager(horizontalLayoutManagaer);
        rv_all_review.setItemAnimator(new DefaultItemAnimator());
        Intent bundle = getIntent();
        if (null != bundle) {
            jsonReviews = (List<JsonReview>) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
            String storeName = bundle.getStringExtra(IBConstant.KEY_STORE_NAME);
            String storeAddress = bundle.getStringExtra(IBConstant.KEY_STORE_ADDRESS);
            TextView tv_store_name = findViewById(R.id.tv_store_name);
            TextView tv_address = findViewById(R.id.tv_address);
            tv_store_name.setText(storeName);
            tv_address.setText(storeAddress);
            if (null != jsonReviews) {
                updateUI();
                tv_address.setVisibility(View.GONE);
            } else {
                jsonReviews = new ArrayList<>();
                String codeQR = bundle.getStringExtra(IBConstant.KEY_CODE_QR);
                if (NetworkUtils.isConnectingToInternet(AllReviewsActivity.this)) {
                    ReviewImpl reviewImpl = new ReviewImpl();
                    reviewImpl.setAllReviewPresenter(this);
                    if (bundle.getBooleanExtra("isLevelUp", false)) {
                        reviewImpl.reviewsLevelUp(UserUtils.getDeviceId(), codeQR);
                    } else {
                        reviewImpl.review(UserUtils.getDeviceId(), codeQR);
                    }
                    setProgressMessage("Getting Reviews...");
                    showProgress();

                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }
        }

    }

    @Override
    public void allReviewResponse(JsonReviewList jsonReviewList) {
        dismissProgress();
        if (null != jsonReviewList && jsonReviewList.getJsonReviews().size() > 0) {
            jsonReviews = jsonReviewList.getJsonReviews();
        }
        updateUI();
    }

    private void updateUI() {
        int ratingCount = 0;
        int listSize = 0;
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtils.setRatingBarColor(stars, this);
        if (null != jsonReviews && jsonReviews.size() > 0) {
            listSize = jsonReviews.size();
            jsonReviewsOnlyText.clear();
            for (int i = 0; i < jsonReviews.size(); i++) {
                ratingCount += jsonReviews.get(i).getRatingCount();
                if (!TextUtils.isEmpty(jsonReviews.get(i).getReview()))
                    jsonReviewsOnlyText.add(jsonReviews.get(i));
            }
            // jsonReviews = temp;
        }
        if (null == jsonReviews || jsonReviews.size() <= 0) {
            rv_all_review.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
            toggleShowAll.setVisibility(View.INVISIBLE);
        } else {
            toggleShowAll.setVisibility(View.VISIBLE);
            rv_all_review.setVisibility(View.VISIBLE);
            toggleShowAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    AllReviewsAdapter showAllReviewsAdapter = new AllReviewsAdapter(jsonReviewsOnlyText, AllReviewsActivity.this);
                    rv_all_review.setAdapter(showAllReviewsAdapter);
                } else {
                    AllReviewsAdapter showAllReviewsAdapter = new AllReviewsAdapter(jsonReviews, AllReviewsActivity.this);
                    rv_all_review.setAdapter(showAllReviewsAdapter);
                }
            });
            rl_empty.setVisibility(View.GONE);
            tv_review_label.setText(jsonReviews.size() + " Ratings");
            try {
                float f = ratingCount * 1.0f / listSize;
                ratingBar.setRating(f);
                TextView tv_rating = findViewById(R.id.tv_rating);
                tv_rating.setText(String.valueOf(AppUtils.round(f)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jsonReviewsOnlyText.size() > 0) {
            toggleShowAll.setChecked(true);
            AllReviewsAdapter showAllReviewsAdapter = new AllReviewsAdapter(jsonReviewsOnlyText, AllReviewsActivity.this);
            rv_all_review.setAdapter(showAllReviewsAdapter);
        } else {
            toggleShowAll.setChecked(false);
            AllReviewsAdapter showAllReviewsAdapter = new AllReviewsAdapter(jsonReviews, AllReviewsActivity.this);
            rv_all_review.setAdapter(showAllReviewsAdapter);
        }
    }
}
