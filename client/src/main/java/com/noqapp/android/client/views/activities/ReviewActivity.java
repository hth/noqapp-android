package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ReviewApiModel;
import com.noqapp.android.client.model.ReviewModel;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.interfaces.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.ReviewRating;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.customviews.SeekbarWithIntervals;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @BindView(R.id.tv_rating_value)
    protected TextView tv_rating_value;

    @BindView(R.id.btn_submit)
    protected Button btn_submit;

    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;

    @BindView(R.id.tv_hr_saved)
    protected TextView tv_hr_saved;

    @BindView(R.id.tv_details)
    protected TextView tv_details;

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;

    @BindView(R.id.tv_badge)
    protected TextView tv_badge;

    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    @BindView(R.id.edt_review)
    protected EditText edt_review;

    @BindView(R.id.seekbarWithIntervals)
    SeekbarWithIntervals seekbarWithIntervals;

    @BindView(R.id.seekbarAppCompact)
    AppCompatSeekBar seekbarAppCompact;


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
            if(null != jtk) {
                tv_store_name.setText(jtk.getBusinessName());
                tv_queue_name.setText(jtk.getDisplayName());
                tv_address.setText(jtk.getStoreAddress());
                String datetime = DateFormat.getDateTimeInstance().format(new Date());
                tv_mobile.setText(datetime);
                List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
                profileList.add(0, NoQueueBaseActivity.getUserProfile());
                if (BuildConfig.BUILD_TYPE.equals("debug")) {
                    tv_details.setText("Token: " + jtk.getToken() + " : " + jtk.getQueueUserId());
                } else {
                    tv_details.setText("Token: " + jtk.getToken() + " : " + AppUtilities.getNameFromQueueUserID(jtk.getQueueUserId(), profileList));
                }
            }
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
        ratingBar.setRating(4.0f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                tv_rating_value.setText(rating + "");

            }
        });
        seekbarAppCompact.setProgress(Constants.DEFAULT_REVIEW_TIME_SAVED);
        seekbarAppCompact.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(ReviewActivity.this, "onStopTrackingTouch", Toast.LENGTH_SHORT).show();
                tv_hr_saved.setText(getSeekbarLabel(seekBar.getProgress() + 1));
            }
        });

        seekbarWithIntervals.setIntervals(getIntervals());
        seekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(ReviewActivity.this, "onStopTrackingTouch", Toast.LENGTH_SHORT).show();
                tv_hr_saved.setText(getSeekbarLabel(seekBar.getProgress() + 1));
            }
        });
        seekbarWithIntervals.setProgress(Constants.DEFAULT_REVIEW_TIME_SAVED);
        tv_hr_saved.setText(getSeekbarLabel(Constants.DEFAULT_REVIEW_TIME_SAVED + 1));
        tv_toolbar_title.setText(getString(R.string.screen_review));
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(ReviewActivity.this, getString(R.string.error_rateservice), Toast.LENGTH_LONG).show();
                }
//                else if (seekBar.getProgress() / 20 == 0) {
//                    Toast.makeText(ReviewActivity.this, getString(R.string.error_timesaved), Toast.LENGTH_LONG).show();
//                }
                else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        ReviewRating rr = new ReviewRating();
                        rr.setCodeQR(jtk.getCodeQR());
                        rr.setToken(jtk.getToken());
                        rr.setHoursSaved(seekbarAppCompact.getProgress() + 1);
                        rr.setRatingCount(Math.round(ratingBar.getRating()));
                        rr.setReview(TextUtils.isEmpty(edt_review.getText().toString()) ? null : edt_review.getText().toString());
                        /* New instance of progressbar because it is a new activity. */
                        progressDialog = new ProgressDialog(ReviewActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Updating...");
                        progressDialog.show();
                        if (UserUtils.isLogin()) {
                            new ReviewApiModel(ReviewActivity.this).review(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), rr);
                        } else {
                            new ReviewModel(ReviewActivity.this).review(UserUtils.getDeviceId(), rr);
                        }
                    } else {
                        ShowAlertInformation.showNetworkDialog(ReviewActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(this, "Please review the service, It is valuable to us.", Toast.LENGTH_LONG).show();
        returnResultBack();
        finish();
    }

    @Override
    public void reviewResponse(JsonResponse jsonResponse) {
        if (null != jsonResponse) {
            //success
            Log.v("Review response", jsonResponse.toString());
            Toast.makeText(this, getString(R.string.review_thanks), Toast.LENGTH_LONG).show();
            returnResultBack();
        }
        //Delete the value in ReviewDB
        ReviewDB.deleteReview(jtk.getCodeQR(), String.valueOf(jtk.getToken()));
        TokenAndQueueDB.deleteTokenQueue(jtk.getCodeQR(), String.valueOf(jtk.getToken()));
        finish();
        progressDialog.dismiss();
    }

    @Override
    public void reviewError() {
        progressDialog.dismiss();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        progressDialog.dismiss();
        AppUtilities.authenticationProcessing(this,errorCode);
    }


    private void returnResultBack() {
        NoQueueBaseActivity.setReviewShown(true);
        Intent intent = new Intent();
        intent.putExtra(Constants.QRCODE, jtk.getCodeQR());
        intent.putExtra(Constants.TOKEN, String.valueOf(jtk.getToken()));
        // if (getParent() == null) {
        setResult(Activity.RESULT_OK, intent);
//        } else {
//            getParent().setResult(Activity.RESULT_OK, intent);
//        }
    }

    private List<String> getIntervals() {
        return new ArrayList<String>() {{
            add(getString(R.string.radio_save_30min));
            add(getString(R.string.radio_save_1hr));
            add(getString(R.string.radio_save_2hr));
            add(getString(R.string.radio_save_3hr));
            add(getString(R.string.radio_save_4hr));
        }};
    }

    private String getSeekbarLabel(int pos) {
        switch (pos) {

            case 1:
                return getString(R.string.time_saved) + getString(R.string.radio_save_30min_f);
            case 2:
                return getString(R.string.time_saved) + getString(R.string.radio_save_1hr_f);
            case 3:
                return getString(R.string.time_saved) + getString(R.string.radio_save_2hr_f);
            case 4:
                return getString(R.string.time_saved) + getString(R.string.radio_save_3hr_f);
            case 5:
                return getString(R.string.time_saved) + getString(R.string.radio_save_4hr_f);
            default:
                return "";

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int notify_count = NotificationDB.getNotificationCount();
        tv_badge.setText(String.valueOf(notify_count));
        if (notify_count > 0) {
            tv_badge.setVisibility(View.VISIBLE);
        } else {
            tv_badge.setVisibility(View.INVISIBLE);
        }

    }
}
