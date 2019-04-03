package com.noqapp.android.client.views.activities;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import java.util.List;

public class JoinActivity extends BaseActivity implements QueuePresenter {
    private final String TAG = JoinActivity.class.getSimpleName();
    private TextView tv_store_name;
    private TextView tv_queue_name;
    private TextView tv_address;
    private TextView tv_mobile, tv_consult_fees, tv_cancelation_fees;
    private TextView tv_serving_no;
    private TextView tv_people_in_q;
    private TextView tv_hour_saved;
    private TextView tv_rating_review;
    private TextView tv_add, add_person;
    private TextView tv_rating;
    private TextView tv_delay_in_time;
    private Spinner sp_name_list;
    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private Button btn_pay_and_joinQueue, btn_joinQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        tv_delay_in_time = findViewById(R.id.tv_delay_in_time);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_consult_fees = findViewById(R.id.tv_consult_fees);
        tv_cancelation_fees = findViewById(R.id.tv_cancelation_fees);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_people_in_q = findViewById(R.id.tv_people_in_q);
        tv_hour_saved = findViewById(R.id.tv_hour_saved);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        TextView tv_skip_msg = findViewById(R.id.tv_skip_msg);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        btn_pay_and_joinQueue = findViewById(R.id.btn_pay_and_joinQueue);
        btn_joinQueue = findViewById(R.id.btn_joinQueue);
        btn_joinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != jsonQueue)
                    joinQueue(false);
            }
        });
        btn_pay_and_joinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != jsonQueue)
                    joinQueue(true);
            }
        });
        tv_rating = findViewById(R.id.tv_rating);
        tv_add = findViewById(R.id.tv_add);
        add_person = findViewById(R.id.add_person);
        tv_add.setPaintFlags(tv_add.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Button btn_no = findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sp_name_list = findViewById(R.id.sp_name_list);

        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_join));
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall(JoinActivity.this, tv_mobile.getText().toString());
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openAddressInMap(JoinActivity.this, tv_address.getText().toString());
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.isLogin()) {
                    Intent loginIntent = new Intent(JoinActivity.this, UserProfileActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(JoinActivity.this, "Please login to add dependents", Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent bundle = getIntent();
        if (null != bundle) {
            codeQR = bundle.getStringExtra(IBConstant.KEY_CODE_QR);
            boolean isCategoryData = bundle.getBooleanExtra(IBConstant.KEY_IS_CATEGORY, true);
            String imageUrl = bundle.getStringExtra(IBConstant.KEY_IMAGE_URL);
            JsonQueue jsonQueue = (JsonQueue) bundle.getExtras().getSerializable(IBConstant.KEY_DATA_OBJECT);
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).
                        placeholder(getResources().getDrawable(R.drawable.profile_theme)).
                        error(getResources().getDrawable(R.drawable.profile_theme)).into(iv_profile);
            } else {
                Picasso.get().load(R.drawable.profile_theme).into(iv_profile);
            }
            if (bundle.getBooleanExtra(IBConstant.KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.yes));
                tv_skip_msg.setVisibility(View.VISIBLE);
                btn_no.setVisibility(View.VISIBLE);
            }

            if (isCategoryData) {
                queueResponse(jsonQueue);
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.setMessage("Loading queue details...");
                    progressDialog.show();
                    if (UserUtils.isLogin()) {
                        QueueApiAuthenticCall queueApiAuthenticCall = new QueueApiAuthenticCall();
                        queueApiAuthenticCall.setQueuePresenter(this);
                        queueApiAuthenticCall.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);

                    } else {
                        QueueApiUnAuthenticCall queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
                        queueApiUnAuthenticCall.setQueuePresenter(this);
                        queueApiUnAuthenticCall.getQueueState(UserUtils.getDeviceId(), codeQR);
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }
        }
    }


    @Override
    public void queueError() {
        Log.d(TAG, "Queue=Error");
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        } else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    @Override
    public void queueResponse(JsonQueue jsonQueueTemp) {
        if (null != jsonQueueTemp) {
            Log.d(TAG, "Queue=" + jsonQueueTemp.toString());
            this.jsonQueue = jsonQueueTemp;
            tv_store_name.setText(jsonQueue.getBusinessName());
            tv_queue_name.setText(jsonQueue.getDisplayName());
            tv_address.setText(jsonQueue.getStoreAddress());
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
            tv_serving_no.setText(String.valueOf(jsonQueue.getServingNumber()));
            tv_people_in_q.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
            if (jsonQueue.getDelayedInMinutes() > 0) {
                int hours = jsonQueue.getDelayedInMinutes() / 60;
                int minutes = jsonQueue.getDelayedInMinutes() % 60;
                String red = "<b>Delayed by " + hours + " Hrs " + minutes + " minutes.</b>";
                tv_delay_in_time.setText(Html.fromHtml(red));
            }else{
                tv_delay_in_time.setVisibility(View.GONE);
            }
            String time = new AppUtilities().formatTodayStoreTiming(this, jsonQueue.getStartHour(), jsonQueue.getEndHour());
            tv_hour_saved.setText(time);
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_rating_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (null != jsonQueue && jsonQueue.getReviewCount() > 0) {
                        Intent in = new Intent(JoinActivity.this, ShowAllReviewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                        bundle.putString(IBConstant.KEY_STORE_NAME, jsonQueue.getDisplayName());
                        bundle.putString(IBConstant.KEY_STORE_ADDRESS, AppUtilities.getStoreAddress(jsonQueue.getTown(), jsonQueue.getArea()));
                        in.putExtras(bundle);
                        startActivity(in);
                    }
                }
            });
            tv_rating.setText(String.valueOf(AppUtilities.round(jsonQueue.getRating())));
            if (tv_rating.getText().toString().equals("0.0")) {
                tv_rating.setVisibility(View.INVISIBLE);
            } else {
                tv_rating.setVisibility(View.VISIBLE);
            }
            String reviewText;
            if (jsonQueue.getReviewCount() == 0) {
                reviewText = "No Review";
                tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            } else if (jsonQueue.getReviewCount() == 1) {
                reviewText = "1 Review";
            } else {
                reviewText = String.valueOf(jsonQueue.getReviewCount()) + " Reviews";
            }
            tv_rating_review.setText(reviewText);
            codeQR = jsonQueue.getCodeQR();
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, JoinActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            }
            switch (jsonQueue.getBusinessType()) {
                case DO:
                case PH:
                case HS:
                    String feeString = "<b>" + AppUtilities.getCurrencySymbol(jsonQueue.getCountryShortName()) + String.valueOf(jsonQueue.getProductPrice() / 100) + "</b>  Consultation fee";
                    tv_consult_fees.setText(Html.fromHtml(feeString));
                    String cancelFeeString = "<b>" + AppUtilities.getCurrencySymbol(jsonQueue.getCountryShortName()) + String.valueOf(jsonQueue.getCancellationPrice() / 100) + "</b>  Cancellation fee";
                    tv_cancelation_fees.setText(Html.fromHtml(cancelFeeString));
                    tv_consult_fees.setVisibility(View.VISIBLE);
                    tv_cancelation_fees.setVisibility(View.VISIBLE);
                    tv_add.setVisibility(View.VISIBLE);
                    add_person.setVisibility(View.VISIBLE);
                    sp_name_list.setVisibility(View.VISIBLE);
                    break;
                default:
                    tv_consult_fees.setVisibility(View.GONE);
                    tv_cancelation_fees.setVisibility(View.GONE);
                    tv_add.setVisibility(View.GONE);
                    add_person.setVisibility(View.GONE);
                    sp_name_list.setVisibility(View.GONE);
            }

            switch (jsonQueue.getServicePayment()) {
                case R:
                    btn_joinQueue.setVisibility(View.GONE);
                    btn_pay_and_joinQueue.setVisibility(View.VISIBLE);
                    break;
                case O:
                    btn_joinQueue.setVisibility(View.VISIBLE);
                    btn_pay_and_joinQueue.setVisibility(View.VISIBLE);
                    break;
            }
            if(!jsonQueue.isEnabledPayment()){
                    btn_joinQueue.setVisibility(View.VISIBLE);
                    btn_pay_and_joinQueue.setVisibility(View.GONE);
            }
        }
        dismissProgress();
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        dismissProgress();
    }


    private void joinQueue(boolean isPayBeforeJoin) {
        sp_name_list.setBackground(ContextCompat.getDrawable(this, R.drawable.sp_background));
        if (isJoinNotPossible) {
            Toast.makeText(this, joinErrorMsg, Toast.LENGTH_LONG).show();
            if (joinErrorMsg.startsWith("Please login to join")) {
                // login required
                Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        } else {
            if (jsonQueue.isRemoteJoinAvailable()) {
                if (jsonQueue.isAllowLoggedInUser()) {//Only login user to be allowed for join
                    if (UserUtils.isLogin()) {
                        if (sp_name_list.getSelectedItemPosition() == 0) {
                            Toast.makeText(this, getString(R.string.error_patient_name_missing), Toast.LENGTH_LONG).show();
                            sp_name_list.setBackground(ContextCompat.getDrawable(this, R.drawable.sp_background_red));
                        } else {
                            callAfterJoin(isPayBeforeJoin);
                        }
                    } else {
                        // please login to avail this feature
                        Toast.makeText(JoinActivity.this, "please login to avail this feature", Toast.LENGTH_LONG).show();
                        // Navigate to login screen
                        Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                } else {
                    // any user can join
                    callAfterJoin(isPayBeforeJoin);
                }
            } else {
                ShowAlertInformation.showThemeDialog(this, getString(R.string.error_join), getString(R.string.error_remote_join_not_available), true);
            }
        }
    }

    private void callAfterJoin(boolean isPayBeforeJoin) {
        if(isPayBeforeJoin && !NoQueueBaseActivity.isEmailVerified()){
            Toast.makeText(this, "Email is mandatory. Please add and verify it", Toast.LENGTH_SHORT).show();
        }else {
            Intent in = new Intent(this, AfterJoinActivity.class);
            in.putExtra(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
            //TODO // previously KEY_FROM_LIST  was false need to verify
            in.putExtra(IBConstant.KEY_FROM_LIST, false);//getArguments().getBoolean(KEY_FROM_LIST, false));
            in.putExtra(IBConstant.KEY_JSON_QUEUE, jsonQueue);
            in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            in.putExtra(Constants.ACTIVITY_TO_CLOSE, true);
            in.putExtra("profile_pos", sp_name_list.getSelectedItemPosition());
            in.putExtra("imageUrl", getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL));
            in.putExtra("isPayBeforeJoin", isPayBeforeJoin);
            startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);

            if (AppUtilities.isRelease()) {
                Answers.getInstance().logCustom(new CustomEvent(FabricEvents.EVENT_JOIN_SCREEN)
                        .putCustomAttribute("Queue Name", jsonQueue.getDisplayName()));
            }
        }
    }


    /*
     *If user navigate to AfterJoinActivity screen from here &
     * he press back from that screen Join screen should removed from activity stack
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.requestCodeAfterJoinQActivity) {
            if (resultCode == RESULT_OK) {
                boolean toclose = data.getExtras().getBoolean(Constants.ACTIVITY_TO_CLOSE, false);
                if (toclose) {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Added to re-initialised the value if user is logged in again and comeback to join screen
        if (null != jsonQueue) {
            /* Check weather join is possible or not today due to some reason */
            JoinQueueState joinQueueState = JoinQueueUtil.canJoinQueue(jsonQueue, JoinActivity.this);
            if (joinQueueState.isJoinNotPossible()) {
                isJoinNotPossible = joinQueueState.isJoinNotPossible();
                joinErrorMsg = joinQueueState.getJoinErrorMsg();
            } else {
                isJoinNotPossible = false;
                joinErrorMsg = "";
            }
        }
        if (UserUtils.isLogin()) {
            List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
            profileList.add(0, NoQueueBaseActivity.getUserProfile());
            profileList.add(0, new JsonProfile().setName("Select Patient"));
            DependentAdapter adapter = new DependentAdapter(this, profileList);
            sp_name_list.setAdapter(adapter);
            if (profileList.size() == 2) {
                sp_name_list.setSelection(1);
            }
        }
    }
}
