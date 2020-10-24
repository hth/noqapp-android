package com.noqapp.android.merchant.views.fragments;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.presenter.AllReviewPresenter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ReviewApiUnAuthenticCalls;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.ReviewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends BaseFragment implements AllReviewPresenter {

    private RecyclerView rv_all_review;
    private TextView tv_review_label;
    private List<JsonReview> jsonReviews = new ArrayList<>();
    private List<JsonReview> jsonReviewsOnlyText = new ArrayList<>();
    private RelativeLayout rl_empty;
    private SwitchCompat toggleShowAll;
    private View view;
    private TextView tv_store_name, tv_address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        view = inflater.inflate(R.layout.frag_show_all_review, container, false);
        rv_all_review = view.findViewById(R.id.rv_all_review);
        rl_empty = view.findViewById(R.id.rl_empty);
        tv_review_label = view.findViewById(R.id.tv_review_label);
        tv_store_name = view.findViewById(R.id.tv_store_name);
        tv_address = view.findViewById(R.id.tv_address);
        toggleShowAll = view.findViewById(R.id.toggleShowAll);
        toggleShowAll.setVisibility(View.INVISIBLE);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_all_review.setLayoutManager(horizontalLayoutManagaer);
        rv_all_review.setItemAnimator(new DefaultItemAnimator());
        Bundle bundle = getArguments();
        if (null != bundle) {
            JsonTopic jt = (JsonTopic) bundle.getSerializable("jsonTopic");
            updateReviews(jt);
        }
        return view;
    }

    public void updateReviews(JsonTopic jsonTopic) {
        if (null != jsonTopic) {
            String storeName = jsonTopic.getDisplayName();
            String storeAddress = "";
            tv_store_name.setText(storeName);
            tv_address.setText(storeAddress);
            jsonReviews = new ArrayList<>();
            String codeQR = jsonTopic.getCodeQR();
            if (new NetworkUtil(getActivity()).isOnline()) {
                ReviewApiUnAuthenticCalls reviewApiUnAuthenticCall = new ReviewApiUnAuthenticCalls();
                reviewApiUnAuthenticCall.setAllReviewPresenter(this);
                reviewApiUnAuthenticCall.review(UserUtils.getDeviceId(), codeQR);
                setProgressMessage("Getting Reviews...");
                showProgress();
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
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
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtils.setRatingBarColor(stars, getActivity());
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
            toggleShowAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ReviewsAdapter ReviewsAdapter = new ReviewsAdapter(jsonReviewsOnlyText, getActivity());
                        rv_all_review.setAdapter(ReviewsAdapter);
                    } else {
                        ReviewsAdapter ReviewsAdapter = new ReviewsAdapter(jsonReviews, getActivity());
                        rv_all_review.setAdapter(ReviewsAdapter);
                    }
                }
            });
            rl_empty.setVisibility(View.GONE);
            tv_review_label.setText("" + jsonReviews.size() + " Ratings");
            try {
                float f = ratingCount * 1.0f /
                        listSize;
                ratingBar.setRating(f);
                TextView tv_rating = view.findViewById(R.id.tv_rating);
                tv_rating.setText(String.valueOf(AppUtils.round(f)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jsonReviewsOnlyText.size() > 0) {
            toggleShowAll.setChecked(true);
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(jsonReviewsOnlyText, getActivity());
            rv_all_review.setAdapter(reviewsAdapter);
        } else {
            toggleShowAll.setChecked(false);
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(jsonReviews, getActivity());
            rv_all_review.setAdapter(reviewsAdapter);
        }
    }
}
