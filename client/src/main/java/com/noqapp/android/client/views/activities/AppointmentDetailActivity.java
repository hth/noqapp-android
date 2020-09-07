package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AppointmentApiCalls;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonQueueDisplay;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import java.util.Objects;

public class AppointmentDetailActivity extends BaseActivity implements AppointmentPresenter {
    private boolean isNavigateHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        initActionsViews(true);
        tv_toolbar_title.setText("Appointment Detail");
        actionbarBack.setOnClickListener((View v) -> onBackPressed());
        try {
            JsonSchedule jsonSchedule = (JsonSchedule) getIntent().getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
            Log.e("data", jsonSchedule.toString());
            JsonQueueDisplay jsonQueueDisplay = jsonSchedule.getJsonQueueDisplay();
            ImageView iv_main = findViewById(R.id.iv_main);
            TextView tv_title = findViewById(R.id.tv_title);
            TextView tv_degree = findViewById(R.id.tv_degree);
            TextView tv_address = findViewById(R.id.tv_address);
            TextView tv_mobile = findViewById(R.id.tv_mobile);
            TextView tv_schedule_time = findViewById(R.id.tv_schedule_time);
            TextView tv_schedule_date = findViewById(R.id.tv_schedule_date);
            TextView tv_patient_name = findViewById(R.id.tv_patient_name);
            TextView tv_appointment_status = findViewById(R.id.tv_appointment_status);
            TextView tv_msg = findViewById(R.id.tv_msg);
            tv_title.setText(jsonQueueDisplay.getDisplayName());
            tv_address.setText(AppUtils.getStoreAddress(jsonQueueDisplay.getTown(), jsonQueueDisplay.getArea()));
            switch (jsonSchedule.getJsonQueueDisplay().getBusinessType()) {
                case DO:
                    tv_degree.setText(MedicalDepartmentEnum.valueOf(jsonSchedule.getJsonQueueDisplay().getBizCategoryId()).getDescription());
                    break;
                default:
                    tv_degree.setText(jsonSchedule.getJsonQueueDisplay().getBusinessType().getDescription());
            }
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonSchedule.getJsonQueueDisplay().getCountryShortName(), jsonSchedule.getJsonQueueDisplay().getStorePhone()));
            tv_mobile.setOnClickListener((View v) -> AppUtils.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString()));
            tv_patient_name.setText(jsonSchedule.getJsonProfile().getName());
            tv_address.setOnClickListener((View v) -> AppUtils.openAddressInMap(LaunchActivity.getLaunchActivity(), jsonQueueDisplay.getStoreAddress()));

            try {
                String date = CommonHelper.SDF_DOB_FROM_UI.format(Objects.requireNonNull(CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.getScheduleDate())));
                tv_schedule_date.setText("Date: " + date);
                tv_schedule_time.setText("Time: " + Formatter.convertMilitaryTo12HourFormat(jsonSchedule.getStartTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_appointment_status.setText(jsonSchedule.getAppointmentStatus().getDescription());
            String note = getString(R.string.asterisk)
                    + " Arrive 30 mins before schedule appointment\n"
                    + getString(R.string.asterisk) + getString(R.string.asterisk)
                    + " Cancel at least 24 hrs before scheduled time\n";
            tv_msg.setText(note);
            AppointmentApiCalls appointmentApiCalls = new AppointmentApiCalls();
            appointmentApiCalls.setAppointmentPresenter(this);
            Button btn_cancel = findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener((View v) -> {
                ShowCustomDialog showDialog = new ShowCustomDialog(AppointmentDetailActivity.this, true);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        if (isOnline()) {
                            setProgressMessage("Canceling appointment...");
                            showProgress();
                            appointmentApiCalls.cancelAppointment(
                                UserUtils.getDeviceId(),
                                UserUtils.getEmail(),
                                UserUtils.getAuth(),
                                jsonSchedule);
                        } else {
                            ShowAlertInformation.showNetworkDialog(AppointmentDetailActivity.this);
                        }
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog("Cancel Appointment", "Do you want to cancel the appointment?");
            });
            if (getIntent().getBooleanExtra(IBConstant.KEY_FROM_LIST, false)) {
                isNavigateHome = false;
                //iv_main.setVisibility(View.GONE);
                switch (jsonSchedule.getAppointmentStatus()) {
                    case U:
                    case A:
                        btn_cancel.setVisibility(View.VISIBLE);
                        break;
                    case R:
                    case S:
                        btn_cancel.setVisibility(View.GONE);
                        break;
                }
                if (getIntent().getBooleanExtra("isPast", false)) {
                    btn_cancel.setVisibility(View.GONE);
                }
            } else {
                isNavigateHome = true;
                // iv_main.setVisibility(View.VISIBLE);
                AppUtils.loadProfilePic(iv_main, getIntent().getStringExtra(IBConstant.KEY_IMAGE_URL), this);
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
    public void appointmentAcceptRejectResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
    }

    @Override
    public void appointmentCancelResponse(JsonResponse jsonResponse) {
        Log.v("appointmentCancelResp", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(this, "Appointment cancelled successfully!");
            finish();
        } else {
            new CustomToast().showToast(this, "Failed to cancel appointment");
        }
        dismissProgress();
    }
}
