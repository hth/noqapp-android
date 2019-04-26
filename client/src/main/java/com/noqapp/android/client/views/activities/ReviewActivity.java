package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ReviewApiAuthenticCalls;
import com.noqapp.android.client.model.ReviewApiUnAuthenticCall;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.OrderReview;
import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewActivity extends AppCompatActivity implements ReviewPresenter {

    private TextView tv_rating_value;
    private RatingBar ratingBar;
    private TextView tv_hr_saved;
    private EditText edt_review;
    private JsonTokenAndQueue jtk;
    private ProgressDialog progressDialog;
    private RadioButton rb_1, rb_2, rb_3, rb_4, rb_5;
    private RadioGroup rg_save_time;
    private int selectedRadio = 1;
    private long lastPress;
    private Toast backPressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        TextView tv_store_name = findViewById(R.id.tv_store_name);
        TextView tv_queue_name = findViewById(R.id.tv_queue_name);
        TextView tv_address = findViewById(R.id.tv_address);
        TextView tv_mobile = findViewById(R.id.tv_mobile);
        TextView tv_review_msg = findViewById(R.id.tv_review_msg);
        tv_rating_value = findViewById(R.id.tv_rating_value);
        Button btn_submit = findViewById(R.id.btn_submit);
        ratingBar = findViewById(R.id.ratingBar);
        tv_hr_saved = findViewById(R.id.tv_hr_saved);
        TextView tv_details = findViewById(R.id.tv_details);
        edt_review = findViewById(R.id.edt_review);

        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        rb_4 = findViewById(R.id.rb_4);
        rb_5 = findViewById(R.id.rb_5);

        rg_save_time = findViewById(R.id.rg_save_time);
        rg_save_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                for (int i = 0; i < rg_save_time.getChildCount(); i++) {
                    View o = rg_save_time.getChildAt(i);
                    if (o instanceof RadioButton) {
                        o.setBackground(ContextCompat.getDrawable(ReviewActivity.this, R.drawable.time_save_unselect));
                        ((RadioButton) o).setTextColor(Color.BLACK);
                    }
                }
                RadioButton rb = findViewById(checkedId);
                rb.setBackground(ContextCompat.getDrawable(ReviewActivity.this, R.drawable.time_save_select));
                rb.setTextColor(Color.WHITE);
                tv_hr_saved.setText(getSeekbarLabel(Integer.parseInt(rb.getTag().toString())));
            }
        });
        rb_4.setChecked(true);
        final Bundle extras = getIntent().getExtras();

        if (null != extras) {
            jtk = (JsonTokenAndQueue) extras.getSerializable(IBConstant.KEY_DATA_OBJECT);
            if (null != jtk) {
                tv_store_name.setText(jtk.getBusinessName());
                tv_queue_name.setText(jtk.getDisplayName());
                tv_address.setText(jtk.getStoreAddress());
                String datetime = DateFormat.getDateTimeInstance().format(new Date());
                tv_mobile.setText(datetime);
                edt_review.setHint("Please provide review for " + jtk.getDisplayName());
                if (UserUtils.isLogin()) {
                    List<JsonProfile> profileList = new ArrayList<>();
                    if (null != NoQueueBaseActivity.getUserProfile().getDependents()) {
                        profileList = NoQueueBaseActivity.getUserProfile().getDependents();
                    }
                    profileList.add(0, NoQueueBaseActivity.getUserProfile());
                    tv_details.setText(AppUtilities.getNameFromQueueUserID(jtk.getQueueUserId(), profileList) + " with token #" + jtk.getToken());
                } else {
                    tv_details.setText("Guest user with token #" + jtk.getToken());
                }

                switch (jtk.getBusinessType()) {
                    case DO:
                        tv_review_msg.setText(getString(R.string.review_msg_checkup_done));
                        break;
                    case RS:
                        tv_review_msg.setText(getString(R.string.review_msg_order_done));
                        break;
                    default:
                        tv_review_msg.setText(getString(R.string.review_msg_queue_done));

                }
                if (AppUtilities.isRelease()) {
                    Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_REVIEW_SCREEN)
                            .putCustomAttribute("Business Type", jtk.getBusinessType().getName()));
                }
            }
        } else {
            //Do nothing as of now
        }
        //actionbarBack.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ratingBar.setRating(4.0f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv_rating_value.setText(rating + "");
            }
        });
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
                        /* New instance of progressbar because it is a new activity. */
                        progressDialog = new ProgressDialog(ReviewActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Submitting review...");
                        progressDialog.show();
                        if (UserUtils.isLogin()) {
                            if (jtk.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.O) {
                                OrderReview orderReview = new OrderReview();
                                orderReview.setCodeQR(jtk.getCodeQR());
                                orderReview.setToken(jtk.getToken());
                                orderReview.setRatingCount(Math.round(ratingBar.getRating()));
                                orderReview.setReview(TextUtils.isEmpty(edt_review.getText().toString()) ? null : edt_review.getText().toString());
                                new ReviewApiAuthenticCalls(ReviewActivity.this).order(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), orderReview);
                            } else {
                                QueueReview rr = new QueueReview()
                                        .setCodeQR(jtk.getCodeQR())
                                        .setToken(jtk.getToken())
                                        .setHoursSaved(selectedRadio) // update according select radio
                                        .setRatingCount(Math.round(ratingBar.getRating()))
                                        .setReview(TextUtils.isEmpty(edt_review.getText().toString()) ? null : edt_review.getText().toString())
                                        .setQueueUserId(jtk.getQueueUserId());
                                new ReviewApiAuthenticCalls(ReviewActivity.this).queue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), rr);
                            }
                        } else {
                            QueueReview rr = new QueueReview()
                                    .setCodeQR(jtk.getCodeQR())
                                    .setToken(jtk.getToken())
                                    .setHoursSaved(selectedRadio) // update according select radio
                                    .setRatingCount(Math.round(ratingBar.getRating()))
                                    .setReview(TextUtils.isEmpty(edt_review.getText().toString()) ? null : edt_review.getText().toString());

                            ReviewApiUnAuthenticCall reviewApiUnAuthenticCall = new ReviewApiUnAuthenticCall();
                            reviewApiUnAuthenticCall.setReviewPresenter(ReviewActivity.this);
                            reviewApiUnAuthenticCall.queue(UserUtils.getDeviceId(), rr);
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = Toast.makeText(this, "Please review the service, It is valuable to us.", Toast.LENGTH_LONG);
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            returnResultBack();
            finish();
        }
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
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        progressDialog.dismiss();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        progressDialog.dismiss();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        progressDialog.dismiss();
        AppUtilities.authenticationProcessing(this);
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

    private String getSeekbarLabel(int pos) {
        selectedRadio = pos;
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
    }
}
