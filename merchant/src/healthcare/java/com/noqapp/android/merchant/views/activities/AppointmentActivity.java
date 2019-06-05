package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.applandeo.materialcalendarview.utils.DrawableUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.EventListAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AppointmentActivity extends AppCompatActivity implements AppointmentPresenter, EventListAdapter.OnItemClickListener {
    private FixedHeightListView fh_list_view;
    private ProgressDialog progressDialog;
    private CalendarView calendarView;
    public EventListAdapter adapter;
    private String codeRQ = "";
    private ScrollView scroll_view;
    private JsonScheduleList jsonScheduleList;
    private final int BOOKING_SUCCESS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText(getString(R.string.menu_appointments));
        initProgress();
        codeRQ = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        Log.e("CODE_QR", codeRQ);
        calendarView = findViewById(R.id.calendarView);
        fh_list_view = findViewById(R.id.fh_list_view);
        scroll_view = findViewById(R.id.scroll_view);
        FloatingActionButton fab_add_image = findViewById(R.id.fab_add_image);
        fab_add_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(AppointmentActivity.this, BookAppointmentActivity.class);
                in.putExtra("jsonScheduleList", (Serializable) jsonScheduleList);
                in.putExtra(IBConstant.KEY_CODE_QR, codeRQ);
                startActivityForResult(in,BOOKING_SUCCESS);
            }
        });
        fh_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(AppointmentActivity.this, AppointmentActivityNew.class);
                in.putExtra("selectedDate", ((JsonSchedule) adapter.getEventDayList().get(position).getEventObject()).getScheduleDate());
                in.putExtra(IBConstant.KEY_CODE_QR, codeRQ);
                startActivity(in);
            }
        });

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -1);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 12);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);
        try {
            calendarView.setDate(Calendar.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // calendarView.setDisabledDays(getDisabledDays());
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                if (eventDay.isEnabled() && null != eventDay.getEventObject()) {
                    scrollInList((JsonSchedule) eventDay.getEventObject());
                }
            }
        });

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                fetchEvents(calendarView.getCurrentPageDate());
            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                fetchEvents(calendarView.getCurrentPageDate());
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            fetchEvents(Calendar.getInstance());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOOKING_SUCCESS) {
            if (resultCode == RESULT_OK) {
                fetchEvents(calendarView.getCurrentPageDate());
            }
        }
    }

    private void scrollInList(JsonSchedule jsonSchedule) {
        List<EventDay> temp = adapter.getEventDayList();
        for (int i = 0; i < temp.size(); i++) {
            JsonSchedule jsonSchedule1 = (JsonSchedule) temp.get(i).getEventObject();
            if (jsonSchedule1.getScheduleDate().equals(jsonSchedule.getScheduleDate())) {
                View c = fh_list_view.getChildAt(i);
                int scrolly = -c.getTop() + fh_list_view.getFirstVisiblePosition() * c.getHeight();
                if (null == scroll_view) {
                    //in case of tablet
                    fh_list_view.smoothScrollToPosition(i);
                } else {
                    scroll_view.scrollTo(0, calendarView.getBottom() + scrolly);
                }
                break;
            }
        }
    }

    private List<Calendar> getDisabledDays() {
        Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, 2);

        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, 1);

        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);

        List<Calendar> calendars = new ArrayList<>();
        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);
        return calendars;
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

    private void fetchEvents(Calendar calendar) {
        progressDialog.show();
        adapter = new EventListAdapter(AppointmentActivity.this, new ArrayList<EventDay>(), this);
        fh_list_view.setAdapter(adapter);
        progressDialog.show();

        ScheduleApiCalls scheduleApiCalls = new ScheduleApiCalls();
        scheduleApiCalls.setAppointmentPresenter(this);
        scheduleApiCalls.scheduleForMonth(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), new AppUtils().getDateWithFormat(calendar), codeRQ);
    }


    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        List<EventDay> events = parseEventList(jsonScheduleList);
        this.jsonScheduleList =jsonScheduleList;
        calendarView.setEvents(events);
        adapter = new EventListAdapter(AppointmentActivity.this, events, this);
        fh_list_view.setAdapter(adapter);
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {
        dismissProgress();
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

    @Override
    public void appointmentAccept(EventDay item, View view, int pos) {

    }

    @Override
    public void appointmentReject(EventDay item, View view, int pos) {

    }
}
