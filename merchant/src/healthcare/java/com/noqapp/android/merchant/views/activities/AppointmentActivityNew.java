package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.utils.DrawableUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.AppointmentStatusEnum;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.BookSchedule;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.views.adapters.AppointmentListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class AppointmentActivityNew extends AppCompatActivity implements AppointmentListAdapter.OnItemClickListener, AppointmentPresenter {

    private ProgressDialog progressDialog;
    private TextView tv_header;
    private RecyclerView rcv_appointments;
    private TextView tv_appointment_accepted, tv_total_appointment,
            tv_appointment_cancelled, tv_appointment_pending;
    private ScheduleApiCalls scheduleApiCalls;
    private SegmentedControl sc_filter;
    private ArrayList<String> filter_data = new ArrayList<>();
    private List<EventDay> events;
    List<EventDay> eventsAccepted = new ArrayList<>();
    List<EventDay> eventsPending = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_new);
        initProgress();
        tv_header = findViewById(R.id.tv_header);

        tv_appointment_accepted = findViewById(R.id.tv_appointment_accepted);
        tv_total_appointment = findViewById(R.id.tv_total_appointment);
        tv_appointment_cancelled = findViewById(R.id.tv_appointment_cancelled);
        tv_appointment_pending = findViewById(R.id.tv_appointment_pending);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText("Appointment List");
        int count = 2;
        if (new AppUtils().isTablet(getApplicationContext())) {
            count = 3;
        } else {
            count = 1;
        }
        rcv_appointments = findViewById(R.id.rcv_appointments);
        rcv_appointments.setHasFixedSize(true);
        rcv_appointments.setLayoutManager(new GridLayoutManager(this, count));
        rcv_appointments.setItemAnimator(new DefaultItemAnimator());


        sc_filter = findViewById(R.id.sc_filter);
        filter_data.clear();
        filter_data.add("All");
        filter_data.add("Accepted");
        filter_data.add("Pending");
        sc_filter.addSegments(filter_data);
        sc_filter.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                int position = segmentViewHolder.getAbsolutePosition();
                switch (position) {
                    case 0:
                        rcv_appointments.setAdapter(new AppointmentListAdapter(events, AppointmentActivityNew.this, AppointmentActivityNew.this));
                        break;
                    case 1:
                        rcv_appointments.setAdapter(new AppointmentListAdapter(eventsAccepted, AppointmentActivityNew.this, AppointmentActivityNew.this));
                        break;
                    case 2:
                        rcv_appointments.setAdapter(new AppointmentListAdapter(eventsPending, AppointmentActivityNew.this, AppointmentActivityNew.this));
                        break;
                }
            }
        });

      fetchData();

    }

    private void fetchData() {
        progressDialog.show();
        scheduleApiCalls = new ScheduleApiCalls();
        scheduleApiCalls.setAppointmentPresenter(this);
        scheduleApiCalls.scheduleForDay(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(),
                getIntent().getStringExtra("selectedDate"),
                getIntent().getStringExtra(IBConstant.KEY_CODE_QR));
    }

    @Override
    public void appointmentAccept(JsonSchedule jsonSchedule, int pos) {
        progressDialog.setMessage("Accepting appointment...");
        progressDialog.show();
        jsonSchedule.setAppointmentStatus(AppointmentStatusEnum.A);
        scheduleApiCalls.scheduleAction(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), jsonSchedule);

    }

    @Override
    public void appointmentReject(JsonSchedule jsonSchedule, int pos) {
        progressDialog.setMessage("Rejecting appointment...");
        progressDialog.show();
        jsonSchedule.setAppointmentStatus(AppointmentStatusEnum.R);
        scheduleApiCalls.scheduleAction(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), jsonSchedule);
    }

    @Override
    public void appointmentEdit(JsonSchedule jsonSchedule, int pos) {
        progressDialog.setMessage("Editing appointment...");
        progressDialog.show();
        BookSchedule bookSchedule = new BookSchedule()
                .setBusinessCustomer(null)
                .setJsonSchedule(jsonSchedule)
                .setBookActionType(ActionTypeEnum.EDIT);
        scheduleApiCalls.bookSchedule(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(),
                bookSchedule);
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        events = parseEventList(jsonScheduleList);
        tv_header.setText("Date: " + getIntent().getStringExtra("selectedDate"));
        int cancel = 0;
        int pending = 0;
        int accept = 0;
        eventsAccepted.clear();
        eventsPending.clear();
        if (null != jsonScheduleList && jsonScheduleList.getJsonSchedules().size() > 0)
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                JsonSchedule jsonSchedule = jsonScheduleList.getJsonSchedules().get(i);
                switch (jsonSchedule.getAppointmentStatus()) {
                    case U:
                        ++pending;
                        eventsPending.add(events.get(i));
                        break;
                    case A:
                        ++accept;
                        eventsAccepted.add(events.get(i));
                        break;
                    case R:
                        ++cancel;
                        break;
                    case S:
                        break;
                }
            }
        tv_appointment_accepted.setText(String.valueOf(accept));
        tv_total_appointment.setText(String.valueOf(events.size()));
        tv_appointment_cancelled.setText(String.valueOf(cancel));
        tv_appointment_pending.setText(String.valueOf(pending));
        if (null == events || events.size() == 0) {
            sc_filter.setVisibility(View.GONE);
        } else {
            sc_filter.setVisibility(View.VISIBLE);
        }
        sc_filter.setSelectedSegment(0);
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
    }

    @Override
    public void appointmentAcceptRejectResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
        fetchData();
    }

    @Override
    public void appointmentCancelResponse(JsonResponse jsonResponse) {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(this, eej);
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
        dismissProgress();
    }


    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching appointments...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private List<EventDay> parseEventList(JsonScheduleList jsonScheduleList) {
        List<EventDay> events = new ArrayList<>();
        if (null == jsonScheduleList.getJsonSchedules() || jsonScheduleList.getJsonSchedules().size() == 0) {
            return events;
        } else {
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                try {
                    JsonSchedule jsonSchedule = jsonScheduleList.getJsonSchedules().get(i);
                    String[] dd = jsonSchedule.getScheduleDate().split("-");
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.SECOND, 12);
                    cal.set(Calendar.MINUTE, 11);
                    cal.set(Calendar.HOUR, 12);
                    cal.set(Calendar.AM_PM, Calendar.AM);
                    cal.set(Calendar.MONTH, Integer.parseInt(dd[1]) - 1);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd[2]));
                    cal.set(Calendar.YEAR, Integer.parseInt(dd[0]));
                    events.add(new EventDay(cal, DrawableUtils.getThreeDots(this), jsonSchedule));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return events;
        }
    }
}
