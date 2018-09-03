package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.interfaces.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.JoinQueueUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import java.util.List;

public class JoinActivity extends BaseActivity implements QueuePresenter {
    private final String TAG = JoinActivity.class.getSimpleName();
    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;

    @BindView(R.id.tv_queue_name)
    protected TextView tv_queue_name;

    @BindView(R.id.tv_address)
    protected TextView tv_address;

    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;

    @BindView(R.id.tv_serving_no)
    protected TextView tv_serving_no;

    @BindView(R.id.tv_people_in_q)
    protected TextView tv_people_in_q;

    @BindView(R.id.tv_hour_saved)
    protected TextView tv_hour_saved;

    @BindView(R.id.tv_skip_msg)
    protected TextView tv_skip_msg;

    @BindView(R.id.tv_rating_review)
    protected TextView tv_rating_review;

    @BindView(R.id.btn_joinQueue)
    protected Button btn_joinQueue;

    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;

    @BindView(R.id.tv_rating)
    protected TextView tv_rating;

    @BindView(R.id.tv_add)
    protected TextView tv_add;

    @BindView(R.id.btn_no)
    protected Button btn_no;

    @BindView(R.id.sp_name_list)
    protected Spinner sp_name_list;

    @BindView(R.id.ll_patient_name)
    protected LinearLayout ll_patient_name;

    private String codeQR;
    private JsonQueue jsonQueue;
    private boolean isJoinNotPossible = false;
    private String joinErrorMsg = "";
    private boolean isCategoryData = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        ButterKnife.bind(this);
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.screen_join));
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
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
            codeQR = bundle.getStringExtra(NoQueueBaseActivity.KEY_CODE_QR);
            isCategoryData = bundle.getBooleanExtra("isCategoryData", true);
            JsonQueue jsonQueue = (JsonQueue) bundle.getExtras().getSerializable("object");

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
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        AppUtilities.authenticationProcessing(this,errorCode);
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
            String red = "<font color='#e92270'><b>Late " + jsonQueue.getDelayedInMinutes() + " minutes.</b></font>";
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
                ll_patient_name.setVisibility(View.VISIBLE);
                break;
            default:
                ll_patient_name.setVisibility(View.GONE);
        }
        dismissProgress();
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        dismissProgress();
    }


    @OnClick(R.id.btn_joinQueue)
    public void joinQueue() {
        sp_name_list.setBackground(ContextCompat.getDrawable(this,R.drawable.sp_background));
        if (isJoinNotPossible) {
            Toast.makeText(this, joinErrorMsg, Toast.LENGTH_LONG).show();
            if (joinErrorMsg.startsWith("Please login to join")) {
                // login required
                Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        } else {
            if (getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false)) {
                String errorMsg = "";
                boolean isValid = true;
                if (!UserUtils.isLogin()) {
                    errorMsg += getString(R.string.bullet) + getString(R.string.error_login) + "\n";
                    isValid = false;
                    Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
                if (!jsonQueue.isRemoteJoinAvailable()) {
                    errorMsg += getString(R.string.bullet) + getString(R.string.error_remote_join_not_available) + "\n";
                    isValid = false;
                }



                if (isValid) {
                    if (sp_name_list.getSelectedItemPosition() == 0) {
                        Toast.makeText(this, getString(R.string.error_patient_name_missing),Toast.LENGTH_LONG ).show();
                        sp_name_list.setBackground(ContextCompat.getDrawable(this,R.drawable.sp_background_red));
                    }else {
                        Intent in = new Intent(this, AfterJoinActivity.class);
                        in.putExtra(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
                        in.putExtra(NoQueueBaseActivity.KEY_FROM_LIST, false);
                        in.putExtra(NoQueueBaseActivity.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
                        in.putExtra(NoQueueBaseActivity.KEY_IS_HISTORY, getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false));
                        in.putExtra(Constants.FROM_JOIN_SCREEN, true);
                        in.putExtra("profile_pos", sp_name_list.getSelectedItemPosition());
                        startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);
                    }
                } else {
                    ShowAlertInformation.showThemeDialog(this, getString(R.string.error_join), errorMsg, true);
                }
            } else {
                if (jsonQueue.isAllowLoggedInUser()) {//Only login user to be allowed for join
                    if (UserUtils.isLogin()) {
                        if (sp_name_list.getSelectedItemPosition() == 0) {
                            Toast.makeText(this, getString(R.string.error_patient_name_missing),Toast.LENGTH_LONG ).show();
                            sp_name_list.setBackground(ContextCompat.getDrawable(this,R.drawable.sp_background_red));
                        }else {
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


            }
        }
    }

    private void callAfterJoin() {

        //TODO(chandra) make sure jsonQueue is not null. Prevent action on join button.
        Intent in = new Intent(this, AfterJoinActivity.class);
        in.putExtra(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
        //TODO // previously KEY_FROM_LIST  was false need to verify
        in.putExtra(NoQueueBaseActivity.KEY_FROM_LIST, false);//getArguments().getBoolean(KEY_FROM_LIST, false));
        in.putExtra(NoQueueBaseActivity.KEY_IS_HISTORY, getIntent().getBooleanExtra(NoQueueBaseActivity.KEY_IS_HISTORY, false));
        in.putExtra(NoQueueBaseActivity.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
        in.putExtra(Constants.FROM_JOIN_SCREEN, true);
        in.putExtra("profile_pos", sp_name_list.getSelectedItemPosition());
        startActivityForResult(in, Constants.requestCodeAfterJoinQActivity);
    }

    @OnClick(R.id.btn_no)
    public void click() {
        finish();
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
