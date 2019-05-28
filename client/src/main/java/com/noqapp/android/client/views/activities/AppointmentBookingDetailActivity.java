package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AppointmentApiCalls;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonQueueDisplay;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;

public class AppointmentBookingDetailActivity extends BaseActivity implements AppointmentPresenter {
    private boolean isNavigateHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        initActionsViews(true);
        tv_toolbar_title.setText("Booking Detail");
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        try {
            JsonSchedule jsonSchedule = (JsonSchedule) getIntent().getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
            Log.e("data", jsonSchedule.toString());
            JsonQueueDisplay jsonQueueDisplay = jsonSchedule.getJsonQueueDisplay();
            ImageView iv_main = findViewById(R.id.iv_main);
            TextView tv_title = findViewById(R.id.tv_title);
            TextView tv_degree = findViewById(R.id.tv_degree);
            TextView tv_schedule_time = findViewById(R.id.tv_schedule_time);
            tv_title.setText(jsonQueueDisplay.getDisplayName());
            tv_degree.setText(AppUtilities.getStoreAddress(jsonQueueDisplay.getTown(), jsonQueueDisplay.getArea()));
            try {
                String date = CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.getScheduleDate()));
                tv_schedule_time.setText(date + " at " + Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getStartTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            AppointmentApiCalls appointmentApiCalls = new AppointmentApiCalls();
            appointmentApiCalls.setAppointmentPresenter(this);
            if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
                isNavigateHome = false;
                iv_main.setVisibility(View.GONE);
                Button btn_cancel = findViewById(R.id.btn_cancel);
                btn_cancel.setVisibility(View.VISIBLE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ShowCustomDialog showDialog = new ShowCustomDialog(AppointmentBookingDetailActivity.this, true);
                        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                if (LaunchActivity.getLaunchActivity().isOnline()) {
                                    progressDialog.setMessage("Canceling appointment...");
                                    progressDialog.show();
                                    appointmentApiCalls.cancelAppointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonSchedule);
                                } else {
                                    ShowAlertInformation.showNetworkDialog(AppointmentBookingDetailActivity.this);
                                }
                            }

                            @Override
                            public void btnNegativeClick() {
                                //Do nothing
                            }
                        });
                        showDialog.displayDialog("Cancel Appointment", "Do you want to cancel the appointment?");
                    }
                });
            } else {
                isNavigateHome = true;
                iv_main.setVisibility(View.VISIBLE);
                AppUtilities.loadProfilePic(iv_main, getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavigateHome) {
            iv_home.performClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
    }

    @Override
    public void appointmentCancelResponse(JsonResponse jsonResponse) {
        Log.v("appointmentCancelResp", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Appointment cancelled successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to cancel appointment", Toast.LENGTH_LONG).show();
        }
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
        if (null != eej)
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
}
