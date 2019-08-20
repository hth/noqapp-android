package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueReviewListAdapter;
import com.noqapp.android.merchant.views.interfaces.ReviewPresenter;


public class QReviewListActivity extends BaseActivity implements
        QueueReviewListAdapter.OnItemClickListener, ReviewPresenter {

    private MerchantProfileApiCalls merchantProfileApiCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review_list);
        merchantProfileApiCalls = new MerchantProfileApiCalls();
        merchantProfileApiCalls.setReviewPresenter(this);
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        RecyclerView rcv_review = findViewById(R.id.rcv_review);
        RelativeLayout rl_empty = findViewById(R.id.rl_empty);
        rcv_review.setHasFixedSize(true);
        rcv_review.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rcv_review.setItemAnimator(new DefaultItemAnimator());
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_all_review));

        JsonReviewList jsonReviewList = (JsonReviewList) getIntent().getSerializableExtra("data");
        tv_queue_name.setText(TextUtils.isEmpty(jsonReviewList.getDisplayName()) ? "N/A" : jsonReviewList.getDisplayName());
        QueueReviewListAdapter queueReviewCardAdapter = new QueueReviewListAdapter(jsonReviewList, this);
        rcv_review.setAdapter(queueReviewCardAdapter);
        if (null == jsonReviewList.getJsonReviews() || jsonReviewList.getJsonReviews().size() <= 0) {
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
    }


    @Override
    public void reviewItemListClick(String codeQR, JsonReview jsonReview) {
        showProgress();
        setProgressMessage("Updating data...");
        merchantProfileApiCalls.flagReview(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR, jsonReview);
    }

    @Override
    public void reviewResponse(JsonReview jsonReview) {
        dismissProgress();
        if (null != jsonReview) {
            // call api
            Log.e("jsonReview", jsonReview.toString());
        }
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
        finish();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                new CustomToast().showToast(this, getString(R.string.error_account_block));
                LaunchActivity.getLaunchActivity().clearLoginData(false);
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }
}
