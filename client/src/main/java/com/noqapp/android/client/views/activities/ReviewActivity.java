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
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ReviewModel;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.ReviewRating;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.ShowAlertInformation;
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
    @BindView(R.id.seekBar)
    protected SeekBar seekBar;
    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;

    @BindView(R.id.tv_seekbar_value)
    protected TextView tv_seekbar_value;

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
            tv_mobile.setText(datetime);
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


        seekBar.incrementProgressBy(20);
        seekBar.setProgress(80);
        tv_seekbar_value.setText(getSeekbarLebel(4));


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 20;
                //progress = progress * 20;
                tv_seekbar_value.setText(getSeekbarLebel(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tv_toolbar_title.setText("Review");
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(ReviewActivity.this, getString(R.string.error_rateservice), Toast.LENGTH_LONG).show();
                } else if (seekBar.getProgress() / 20 == 0) {
                    Toast.makeText(ReviewActivity.this, getString(R.string.error_timesaved), Toast.LENGTH_LONG).show();
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        ReviewRating rr = new ReviewRating();
                        rr.setCodeQR(jtk.getCodeQR());
                        rr.setToken(jtk.getToken());
                        rr.setHoursSaved(String.valueOf(seekBar.getProgress() / 20));
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
            Toast.makeText(this, getString(R.string.review_thanks), Toast.LENGTH_LONG).show();
            returnResultBack();
        }
        //Reset the value in ReviewDB
        ReviewDB.insert(ReviewDB.KEY_REVIEW, "", "");
        TokenAndQueueDB.deleteTokenQueue(jtk.getCodeQR());
        finish();
        progressDialog.dismiss();
    }

    @Override
    public void reviewError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


    private void returnResultBack() {
        NoQueueBaseActivity.setReviewShown(true);
        Intent intent = new Intent();
        intent.putExtra("CODEQR", jtk.getCodeQR());
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    private String getSeekbarLebel(int pos) {
        switch (pos) {

            case 1:
                return getString(R.string.radio_save_30min);
            case 2:
                return getString(R.string.radio_save_1hr);
            case 3:
                return getString(R.string.radio_save_2hr);
            case 4:
                return getString(R.string.radio_save_3hr);
            case 5:
                return getString(R.string.radio_save_4hr);
            default:
                return "";

        }

    }
}
