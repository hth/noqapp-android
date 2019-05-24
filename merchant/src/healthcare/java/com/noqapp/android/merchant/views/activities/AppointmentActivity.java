package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.applandeo.materialcalendarview.utils.DrawableUtils;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AppointmentActivity extends AppCompatActivity implements AppointmentPresenter, EventListAdapter.OnItemClickListener {
    private FixedHeightListView fh_list_view;
    private ProgressDialog progressDialog;
    private CalendarView calendarView;
    public static EventListAdapter adapter;
    private String codeRQ = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        // Calendar calendar = Calendar.getInstance();
        // events.add(new EventDay(calendar, DrawableUtils.getCircleDrawableWithText(this, "Chand")));

//        Calendar calendar1 = Calendar.getInstance();
//        calendar1.add(Calendar.DAY_OF_MONTH, 2);
//        events.add(new EventDay(calendar1, R.drawable.sample_icon_2));

//        Calendar calendar2 = Calendar.getInstance();
//        calendar2.add(Calendar.DAY_OF_MONTH, 5);
//        events.add(new EventDay(calendar2, R.drawable.sample_icon_3));

//        Calendar calendar3 = Calendar.getInstance();
//        calendar3.add(Calendar.DAY_OF_MONTH, 7);
//        events.add(new EventDay(calendar3, R.drawable.sample_four_icons));

//        Calendar calendar4 = Calendar.getInstance();
//        calendar4.add(Calendar.DAY_OF_MONTH, 13);
//        events.add(new EventDay(calendar4, DrawableUtils.getThreeDots(this)));

        calendarView = findViewById(R.id.calendarView);
        fh_list_view = findViewById(R.id.fh_list_view);
        fh_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(AppointmentActivity.this, AppointmentActivityNew.class);
                // in.putParcelableArrayListExtra(IBConstant.KEY_OBJECT_LIST,new ArrayList<EventDay>(adapter.getEventDayList()));
                startActivity(in);
            }
        });

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, 0);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 12);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);


        // calendarView.setDisabledDays(getDisabledDays());

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                if (eventDay.isEnabled()) {
                    Toast.makeText(getApplicationContext(),
                            (eventDay.getCalendar().getTime().toString()),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                fetchEvents(calendarView.getCurrentPageDate().getTime().getMonth());
            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                fetchEvents(calendarView.getCurrentPageDate().getTime().getMonth());
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            fetchEvents(Calendar.getInstance().get(Calendar.MONTH));
        } else {
            ShowAlertInformation.showNetworkDialog(this);
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

    private void fetchEvents(int month) {
        progressDialog.show();
        adapter = new EventListAdapter(AppointmentActivity.this, new ArrayList<EventDay>(), this);
        fh_list_view.setAdapter(adapter);
        progressDialog.show();
        ScheduleApiCalls scheduleApiCalls = new ScheduleApiCalls();
        scheduleApiCalls.setAppointmentPresenter(this);
        scheduleApiCalls.scheduleForMonth(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), "2019-05-24", codeRQ);
    }


    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        List<EventDay> events = parseEventList(jsonScheduleList);
        calendarView.setEvents(events);
        adapter = new EventListAdapter(AppointmentActivity.this, events, this);
        fh_list_view.setAdapter(adapter);
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {

    }

    @Override
    public void appointmentCancelResponse(JsonResponse jsonResponse) {

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
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
