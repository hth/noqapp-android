package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.utils.DrawableUtils;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.AppointmentStatusEnum;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.BookSchedule;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.adapters.AppointmentListAdapter;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class AppointmentActivityNew extends BaseActivity implements
        AppointmentListAdapter.OnItemClickListener, AppointmentPresenter {

    private TextView tv_header, tv_date;
    private RecyclerView rcv_appointments;
    private TextView tv_appointment_accepted, tv_total_appointment,
            tv_appointment_cancelled, tv_appointment_pending;
    private ScheduleApiCalls scheduleApiCalls;
    private SegmentedControl sc_filter;
    private ArrayList<String> filter_data = new ArrayList<>();
    private List<EventDay> events = new ArrayList<>();
    private List<EventDay> eventsAccepted = new ArrayList<>();
    private List<EventDay> eventsPending = new ArrayList<>();
    private int appointmentDuration = 0;
    private final int BOOKING_SUCCESS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_new);
        setProgressMessage("Fetching appointments...");
        setProgressCancel(false);
        tv_header = findViewById(R.id.tv_header);
        tv_date = findViewById(R.id.tv_date);
        initActionsViews(false);
        tv_appointment_accepted = findViewById(R.id.tv_appointment_accepted);
        tv_total_appointment = findViewById(R.id.tv_total_appointment);
        tv_appointment_cancelled = findViewById(R.id.tv_appointment_cancelled);
        tv_appointment_pending = findViewById(R.id.tv_appointment_pending);
        tv_toolbar_title.setText("Appointment List");
        int count = 2;
        if (LaunchActivity.isTablet) {
            count = 3;
        } else {
            count = 1;
        }
        appointmentDuration = getIntent().getIntExtra("appointmentDuration", 0);
        tv_date.setText(getIntent().getStringExtra("selectedDate"));
        tv_header.setText(getIntent().getStringExtra("displayName"));
        String bizCategoryId = getIntent().getStringExtra("bizCategoryId");

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
                        rcv_appointments.setAdapter(new AppointmentListAdapter(events, AppointmentActivityNew.this, AppointmentActivityNew.this, bizCategoryId));
                        break;
                    case 1:
                        rcv_appointments.setAdapter(new AppointmentListAdapter(eventsAccepted, AppointmentActivityNew.this, AppointmentActivityNew.this, bizCategoryId));
                        break;
                    case 2:
                        rcv_appointments.setAdapter(new AppointmentListAdapter(eventsPending, AppointmentActivityNew.this, AppointmentActivityNew.this, bizCategoryId));
                        break;
                }
            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            fetchData();
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void fetchData() {
        showProgress();
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

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            ShowCustomDialog showDialog = new ShowCustomDialog(this);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    setProgressMessage("Accepting appointment...");
                    showProgress();
                    jsonSchedule.setAppointmentStatus(AppointmentStatusEnum.A);
                    scheduleApiCalls.scheduleAction(BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(), jsonSchedule);
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Accept Appointment", "Do you want to accept appointment?");
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void appointmentReject(JsonSchedule jsonSchedule, int pos) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            ShowCustomDialog showDialog = new ShowCustomDialog(this);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    setProgressMessage("Rejecting appointment...");
                    showProgress();
                    jsonSchedule.setAppointmentStatus(AppointmentStatusEnum.R);
                    scheduleApiCalls.scheduleAction(BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(), jsonSchedule);
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Reject Appointment", "Do you want to reject appointment?");
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void appointmentEdit(JsonSchedule jsonSchedule, int pos) {
        setProgressMessage("Editing appointment...");
        showProgress();
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
    public void appointmentReschedule(JsonSchedule jsonSchedule, int pos) {
        Intent in = new Intent(AppointmentActivityNew.this, BookAppointmentActivity.class);
        in.putExtra("jsonScheduleList", getIntent().getExtras().getSerializable("jsonScheduleList"));
        in.putExtra(IBConstant.KEY_CODE_QR, getIntent().getStringExtra(IBConstant.KEY_CODE_QR));
        in.putExtra("bizCategoryId", getIntent().getStringExtra("bizCategoryId"));
        in.putExtra("displayName", getIntent().getStringExtra("displayName"));
        in.putExtra("jsonSchedule", jsonSchedule);
        in.putExtra("isEdit", true);
        startActivityForResult(in, BOOKING_SUCCESS);
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        events.clear();
        List<EventDay> temp = parseEventList(jsonScheduleList);
        for (int i = 0; i < temp.size(); i++) {
            JsonSchedule js = (JsonSchedule) temp.get(i).getEventObject();
            // set to keep track the actual assign slot in case of splitting the slot to show in appointment UI
            js.setMultipleSlotStartTiming(js.getStartTime());
            js.setMultipleSlotEndTiming(js.getEndTime());
            List<String> timeSlot = AppUtils.computeTimeSlot(
                appointmentDuration,
                AppUtils.getTimeFourDigitWithColon(js.getStartTime()),
                AppUtils.getTimeFourDigitWithColon(js.getEndTime()),
                CommonHelper.AppointmentComputationEnum.FILLED);

            Log.e("Number of time slots", "" + timeSlot.size());
            if (timeSlot.size() == 0 || timeSlot.size() == 1) {
                events.add(temp.get(i));
            } else {
                for (int j = 0; j < timeSlot.size(); j++) {
                    EventDay eventDay = new EventDay(temp.get(i).getCalendar(), DrawableUtils.getThreeDots(this), SerializationUtils.clone(js));
                    ((JsonSchedule) eventDay.getEventObject()).setStartTime(AppUtils.removeColon(timeSlot.get(j)));
                    if (j == timeSlot.size() - 1) {
                        ((JsonSchedule) eventDay.getEventObject()).setEndTime(js.getEndTime());
                    } else {
                        ((JsonSchedule) eventDay.getEventObject()).setEndTime(AppUtils.removeColon(timeSlot.get(j + 1)));
                    }
                    if (((JsonSchedule) eventDay.getEventObject()).getStartTime() != ((JsonSchedule) eventDay.getEventObject()).getEndTime()) {
                        events.add(eventDay);
                    } else {
                        Log.e("Error: ", "Start end time same, Do not add data");
                        for (int k = 0; k < timeSlot.size(); k++) {
                            Log.e("time: ", k + " " + timeSlot.get(k));
                        }
                    }
                }
            }
        }
        Collections.sort(events, new Comparator<EventDay>() {
            public int compare(EventDay o1, EventDay o2) {
                try {
                    int time1 = ((JsonSchedule) o2.getEventObject()).getStartTime();
                    int time2 = ((JsonSchedule) o1.getEventObject()).getStartTime();
                    /*For descending order*/
                    return time2 - time1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        int cancel = 0;
        int pending = 0;
        int accept = 0;
        eventsAccepted.clear();
        eventsPending.clear();
        if (null != events && events.size() > 0)
            for (int i = 0; i < events.size(); i++) {
                JsonSchedule jsonSchedule = (JsonSchedule) events.get(i).getEventObject();
                switch (jsonSchedule.getAppointmentStatus()) {
                    case U:
                        eventsPending.add(events.get(i));
                        break;
                    case A:
                        eventsAccepted.add(events.get(i));
                        break;
                    case R:
                        break;
                    case S:
                        break;
                }
            }
        if (null != jsonScheduleList && jsonScheduleList.getJsonSchedules().size() > 0)
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                JsonSchedule jsonSchedule = jsonScheduleList.getJsonSchedules().get(i);
                switch (jsonSchedule.getAppointmentStatus()) {
                    case U:
                        ++pending;
                        break;
                    case A:
                        ++accept;
                        break;
                    case R:
                        ++cancel;
                        break;
                    case S:
                        break;
                }
            }
        tv_appointment_accepted.setText(String.valueOf(accept));
        tv_total_appointment.setText(String.valueOf(jsonScheduleList.getJsonSchedules().size()));
        tv_appointment_cancelled.setText(String.valueOf(cancel));
        tv_appointment_pending.setText(String.valueOf(pending));
        if (null == events || events.size() == 0) {
            sc_filter.setVisibility(View.GONE);
        } else {
            sc_filter.setVisibility(View.VISIBLE);
        }
        sc_filter.setSelectedSegment(0); // to set adapter
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOOKING_SUCCESS) {
            if (resultCode == RESULT_OK) {
                fetchData();
            }
        }
    }
}
