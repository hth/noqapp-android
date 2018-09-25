package com.noqapp.android.client.views.activities;


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class JoinActivity extends BaseActivity implements QueuePresenter {
    private final String TAG = JoinActivity.class.getSimpleName();
    private TextView tv_store_name;
    private TextView tv_queue_name;
    private TextView tv_address;
    private TextView tv_mobile;
    private TextView tv_serving_no;
    private TextView tv_people_in_q;
    private TextView tv_hour_saved;
    private TextView tv_rating_review;
    private TextView tv_add ,add_person;
    private RatingBar ratingBar;
    private Spinner sp_name_list;

    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        tv_store_name = findViewById(R.id.tv_store_name);
        tv_queue_name = findViewById(R.id.tv_queue_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_serving_no = findViewById(R.id.tv_serving_no);
        tv_people_in_q = findViewById(R.id.tv_people_in_q);
        tv_hour_saved = findViewById(R.id.tv_hour_saved);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        TextView tv_skip_msg = findViewById(R.id.tv_skip_msg);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        Button btn_joinQueue = findViewById(R.id.btn_joinQueue);
        btn_joinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != jsonQueue)
                    joinQueue();
            }
        });
        ratingBar = findViewById(R.id.ratingBar);
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
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColorBlue(stars, this);
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
            boolean isCategoryData = true;
            codeQR = bundle.getStringExtra(NoQueueBaseActivity.KEY_CODE_QR);
            isCategoryData = bundle.getBooleanExtra("isCategoryData", true);
            String imageUrl = bundle.getStringExtra("imageUrl");
            JsonQueue jsonQueue = (JsonQueue) bundle.getExtras().getSerializable("object");
            if (!TextUtils.isEmpty(imageUrl)) {

                Picasso.with(this).load(imageUrl).
                        placeholder(getResources().getDrawable(R.drawable.profile_red)).
                        error(getResources().getDrawable(R.drawable.profile_red)).into(iv_profile);
            } else {
                Picasso.with(this).load(R.drawable.profile_red).into(iv_profile);
            }
            if (bundle.getBooleanExtra(NoQueueBaseActivity.KEY_IS_REJOIN, false)) {
                btn_joinQueue.setText(getString(R.string.yes));
                tv_skip_msg.setVisibility(View.VISIBLE);
                btn_no.setVisibility(View.VISIBLE);
            }

            if (isCategoryData) {
                queueResponse(jsonQueue);
            } else {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.setMessage("fetching queue details...");
                    progressDialog.show();
                    if (UserUtils.isLogin()) {
                        QueueApiModel queueApiModel = new QueueApiModel();
                        queueApiModel.setQueuePresenter(this);
                        queueApiModel.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);

                    } else {
                        QueueModel queueModel = new QueueModel();
                        queueModel.setQueuePresenter(this);
                        queueModel.getQueueState(UserUtils.getDeviceId(), codeQR);
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
        new ErrorResponseHandler().processError(this,eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE){
            ShowAlertInformation.showBarcodeErrorDialog(this);
        }else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    @Override
    public void queueResponse(JsonQueue jsonQueue) {

        Log.d(TAG, "Queue=" + jsonQueue.toString());
        this.jsonQueue = jsonQueue;
        tv_store_name.setText(jsonQueue.getBusinessName());
        tv_queue_name.setText(jsonQueue.getDisplayName());
        tv_address.setText(jsonQueue.getStoreAddress());
        tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
        tv_serving_no.setText(String.valueOf(jsonQueue.getServingNumber()));
        tv_people_in_q.setText(String.valueOf(jsonQueue.getPeopleInQueue()));
        String time = getString(R.string.store_hour) + " " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()) +
                " - " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour());
        if (jsonQueue.getDelayedInMinutes() > 0) {
            int hours = jsonQueue.getDelayedInMinutes() / 60;
            int minutes = jsonQueue.getDelayedInMinutes() % 60;
            System.out.printf("%d:%02d", hours, minutes);
            String red = "<font color='#e92270'><b>Delayed by " + hours+" Hrs " + minutes+" minutes.</b></font>";
            time = time + " " + red;
        }
        tv_hour_saved.setText(Html.fromHtml(time));
        ratingBar.setRating(jsonQueue.getRating());
        String reviewText = String.valueOf(jsonQueue.getRatingCount() == 0 ? "No" : jsonQueue.getRatingCount()) + " Reviews";
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
                tv_add.setVisibility(View.VISIBLE);
                add_person.setVisibility(View.VISIBLE);
                sp_name_list.setVisibility(View.VISIBLE);
                break;
            default:
                tv_add.setVisibility(View.GONE);
                add_person.setVisibility(View.GONE);
                sp_name_list.setVisibility(View.GONE);
        }
        dismissProgress();
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        dismissProgress();
    }


    private void joinQueue() {
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
                            callAfterJoin();
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
                    callAfterJoin();
                }
            } else {
                ShowAlertInformation.showThemeDialog(this, getString(R.string.error_join),  getString(R.string.error_remote_join_not_available), true);
            }
        }
    }

    private void callAfterJoin() {
        Intent in = new Intent(this, AfterJoinActivity.class);
        in.putExtra(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
        //TODO // previously KEY_FROM_LIST  was false need to verify
        in.putExtra(NoQueueBaseActivity.KEY_FROM_LIST, false);//getArguments().getBoolean(KEY_FROM_LIST, false));
        in.putExtra(NoQueueBaseActivity.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
        in.putExtra(Constants.FROM_JOIN_SCREEN, true);
        in.putExtra("profile_pos", sp_name_list.getSelectedItemPosition());
        in.putExtra("imageUrl",getIntent().getStringExtra("imageUrl"));
        startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);
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
