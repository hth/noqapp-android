package com.noqapp.android.client.views.activities;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ReviewApiAuthenticCalls;
import com.noqapp.android.client.model.ReviewApiUnAuthenticCall;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.ReviewPresenter;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.OrderReview;
import com.noqapp.android.client.presenter.beans.body.QueueReview;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.Q;

public class ReviewActivity extends BaseActivity implements ReviewPresenter {

    private TextView tv_rating_value;
    private RatingBar ratingBar;
    private TextView tv_hr_saved;
    private EditText edt_review;
    private JsonTokenAndQueue jtk;
    private RadioButton rb_1, rb_2, rb_3, rb_4, rb_5;
    private RadioGroup rg_save_time;
    private int selectedRadio = 1;
    private long lastPress;
    private Toast backPressToast;
    private LinearLayout ll_fill_review, ll_thank_u;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initActionsViews(false);
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
        TextView tv_title = findViewById(R.id.tv_title);
        edt_review = findViewById(R.id.edt_review);

        ll_fill_review = findViewById(R.id.ll_fill_review);
        ll_thank_u = findViewById(R.id.ll_thank_u);

        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        rb_4 = findViewById(R.id.rb_4);
        rb_5 = findViewById(R.id.rb_5);

        rg_save_time = findViewById(R.id.rg_save_time);
//        rg_save_time.setOnCheckedChangeListener((group, checkedId) -> {
//            for (int i = 0; i < rg_save_time.getChildCount(); i++) {
//                View o = rg_save_time.getChildAt(i);
//                if (o instanceof RadioButton) {
//                    o.setBackground(ContextCompat.getDrawable(ReviewActivity.this, R.drawable.time_save_unselect));
//                    ((RadioButton) o).setTextColor(Color.BLACK);
//                }
//            }
//            RadioButton rb = findViewById(checkedId);
//            rb.setBackground(ContextCompat.getDrawable(ReviewActivity.this, R.drawable.time_save_select));
//            rb.setTextColor(Color.WHITE);
//            tv_hr_saved.setText(getSeekbarLabel(Integer.parseInt(rb.getTag().toString())));
//        });
        RadioButton rb = findViewById(R.id.rb_5);
        rb.setBackground(ContextCompat.getDrawable(ReviewActivity.this, R.drawable.time_save_select));
        rb.setTextColor(Color.WHITE);
        tv_hr_saved.setText(getSeekbarLabel(Integer.parseInt(rb.getTag().toString())));
        rb_5.setChecked(true);
        final Bundle extras = getIntent().getExtras();

        if (null != extras) {
            jtk = (JsonTokenAndQueue) extras.getSerializable(IBConstant.KEY_DATA_OBJECT);
            if (null != jtk) {
                if (jtk.getBusinessType() == BusinessTypeEnum.DO) {
                    ll_thank_u.setVisibility(View.VISIBLE);
                    ll_fill_review.setVisibility(View.GONE);
                    btn_submit.setText(getString(R.string.go_back));
                } else {
                    ll_thank_u.setVisibility(View.GONE);
                    ll_fill_review.setVisibility(View.VISIBLE);
                }
                tv_store_name.setText(Html.fromHtml("<u>" + jtk.getBusinessName() + "</u>"));
                tv_queue_name.setText(jtk.getDisplayName());
                tv_address.setText(jtk.getStoreAddress());
                String datetime = DateFormat.getDateTimeInstance().format(new Date());
                tv_mobile.setText(datetime);
                edt_review.setHint("Please provide review for " + jtk.getDisplayName());
                String queueOrderType = jtk.getBusinessType().getQueueOrderType() == Q ? "queue" : "order";
                tv_title.setText(StringUtils.capitalize(queueOrderType + " Detail"));

                if (UserUtils.isLogin()) {
                    List<JsonProfile> profileList = AppInitialize.getAllProfileList();
                    tv_details.setText(AppUtils.getNameFromQueueUserID(jtk.getQueueUserId(), profileList) + " with " + queueOrderType + " #" + jtk.getDisplayToken());
                } else {
                    tv_details.setText("Guest user with " + queueOrderType + " #" + jtk.getDisplayToken());
                }

                try {
                    switch (jtk.getBusinessType()) {
                        case DO:
                            tv_review_msg.setText(getString(R.string.review_msg_checkup_done));
                            break;
                        case RS:
                        case FT:
                            tv_review_msg.setText(getString(R.string.review_msg_order_done));
                            break;
                        default:
                            tv_review_msg.setText(getString(R.string.review_msg_queue_done));

                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

                if (AppUtils.isRelease()) {
                    Bundle params = new Bundle();
                    params.putString("Business_Type", jtk.getBusinessType().getName());
                    AppInitialize.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_REVIEW_SCREEN, params);
                }
            }
        } else {
            //Do nothing as of now
        }
        //actionbarBack.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener((View v) -> onBackPressed());
        ratingBar.setRating(5.0f);
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> tv_rating_value.setText(rating + ""));
        tv_toolbar_title.setText(getString(R.string.screen_review));
        btn_submit.setOnClickListener((View v) -> {
            if (null == jtk || jtk.getBusinessType() == BusinessTypeEnum.DO) {
                onBackPressed();
            } else {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(ReviewActivity.this, getString(R.string.error_rateservice), Toast.LENGTH_LONG).show();
                }
//                else if (seekBar.getProgress() / 20 == 0) {
//                    Toast.makeText(ReviewActivity.this, getString(R.string.error_timesaved), Toast.LENGTH_LONG).show();
//                }
                else {
                    if (isOnline()) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        /* New instance of progressbar because it is a new activity. */
                        setProgressCancel(true);
                        setProgressMessage("Submitting review...");
                        showProgress();
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
        //@TODO revert this changes after corona issue
        /*//super.onBackPressed();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = new CustomToast().getToast(this, "Please review the service, It is valuable to us.");
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            returnResultBack();
            finish();
        }*/
        // @TODO revert all 3 below line
        AppInitialize.setReviewShown(true);
        ReviewDB.deleteReview(jtk.getCodeQR(), String.valueOf(jtk.getToken()));
        TokenAndQueueDB.deleteTokenQueue(jtk.getCodeQR(), String.valueOf(jtk.getToken()));
        super.onBackPressed();
        // @TODO revert all 3 above line
    }

    @Override
    public void reviewResponse(JsonResponse jsonResponse) {
        mLastClickTime = 0;
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
        dismissProgress();
    }

    private void returnResultBack() {
        AppInitialize.setReviewShown(true);
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
