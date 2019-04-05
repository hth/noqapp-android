package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueReviewListAdapter;
import com.noqapp.android.merchant.views.interfaces.ReviewPresenter;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class QReviewListActivity extends AppCompatActivity implements QueueReviewListAdapter.OnItemClickListener, ReviewPresenter {

    private ProgressDialog progressDialog;
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
        initProgress();
        rcv_review.setHasFixedSize(true);
        rcv_review.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rcv_review.setItemAnimator(new DefaultItemAnimator());
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_all_review));
        initProgress();

        JsonReviewList jsonReviewList = (JsonReviewList) getIntent().getSerializableExtra("data");
        tv_queue_name.setText(TextUtils.isEmpty(jsonReviewList.getDisplayName()) ? "N/A" : jsonReviewList.getDisplayName());
        QueueReviewListAdapter queueReviewCardAdapter = new QueueReviewListAdapter(jsonReviewList, this, this);
        rcv_review.setAdapter(queueReviewCardAdapter);
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void reviewItemListClick(String codeQR, JsonReview jsonReview) {
        progressDialog.show();
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
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                Toast.makeText(this, getString(R.string.error_account_block), Toast.LENGTH_LONG).show();
                LaunchActivity.getLaunchActivity().clearLoginData(false);
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }
}
