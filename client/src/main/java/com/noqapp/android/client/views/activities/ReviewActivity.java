package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.model.ReviewModel;
import com.noqapp.android.client.model.database.utils.NoQueueDB;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.ReviewRating;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.UserUtils;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity implements ReviewPresenter {
    private final String TAG = ReviewActivity.class.getSimpleName();

    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;
    @BindView(R.id.tv_queue_name)
    protected TextView tv_queue_name;
    @BindView(R.id.tv_address)
    protected TextView tv_address;
    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;
    @BindView(R.id.btn_submit)
    protected Button btn_submit;
    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;
    @BindView(R.id.radioSave)
    protected RadioGroup radioSave;
    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    private JsonTokenAndQueue jtk;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        final Bundle extras = getIntent().getExtras();

        if (null != extras) {
            jtk = (JsonTokenAndQueue) extras.getSerializable("object");
            tv_store_name.setText(jtk.getBusinessName());
            tv_queue_name.setText(jtk.getDisplayName());
            tv_address.setText(Formatter.getFormattedAddress(jtk.getStoreAddress()));
            String datetime = DateFormat.getDateTimeInstance().format(new Date());
            tv_mobile.setText("Date of service : " + datetime);
        } else {
            //Do nothing as of now
        }
       // actionbarBack.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResultBack();
                finish();
            }
        });
        tv_toolbar_title.setText("Review");
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(ReviewActivity.this, "Please rate the service", Toast.LENGTH_LONG).show();
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        ReviewRating rr = new ReviewRating();
                        rr.setCodeQR(jtk.getCodeQR());
                        rr.setToken(jtk.getToken());
                        rr.setHoursSaved(String.valueOf(radioSave.indexOfChild(findViewById(radioSave.getCheckedRadioButtonId()))));
                        rr.setRatingCount(String.valueOf(Math.round(ratingBar.getRating())));
                        /* New instance of progressbar because it is a new activity. */
                        progressDialog = new ProgressDialog(ReviewActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Updating...");
                        progressDialog.show();
                        ReviewModel.reviewPresenter = ReviewActivity.this;
                        ReviewModel.review(UserUtils.getDeviceId(), rr);
                    } else {
                        ShowAlertInformation.showNetworkDialog(ReviewActivity.this);
                    }
                }
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        Toast.makeText(this, "Please review the service, It is valuable to us.", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void reviewResponse(JsonResponse jsonResponse) {
        if (null != jsonResponse) {
            //success
            Log.v("Review response", jsonResponse.toString());
            Toast.makeText(this, "Thanks for feedback.", Toast.LENGTH_LONG).show();
            returnResultBack();

        }
        NoQueueDB.deleteTokenQueue(jtk.getCodeQR());
        finish();
        progressDialog.dismiss();
    }

    @Override
    public void reviewError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


    private void returnResultBack(){
        Intent intent = new Intent();
        intent.putExtra("CODEQR", jtk.getCodeQR());
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }
}
