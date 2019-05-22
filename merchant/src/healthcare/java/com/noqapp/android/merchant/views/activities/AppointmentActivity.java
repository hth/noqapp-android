package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.EventListAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AppointmentActivity extends AppCompatActivity implements AppointmentPresenter {
    private FixedHeightListView fh_list_view;
    private ProgressDialog progressDialog;
    private CalendarView calendarView;

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
        // List<EventDay> events = new ArrayList<>();

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

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, 0);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 12);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);
        fetchEvents(Calendar.getInstance().get(Calendar.MONTH));

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
            progressDialog.show();
            ScheduleApiCalls scheduleApiCalls = new ScheduleApiCalls();
            scheduleApiCalls.setAppointmentPresenter(this);
            scheduleApiCalls.showSchedule(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), "month","codeQR");

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


    private List<EventDay> getMonthWiseEvents(int month) {
        List<EventDay> events = new ArrayList<>();
        switch (month) {
            case 4: {
                Calendar calendar = Calendar.getInstance();
                JsonSchedule appointmentInfo = new JsonSchedule();
                appointmentInfo.setTotalAppointments(11);
                appointmentInfo.setStartTime("11:45 pm");
                appointmentInfo.setDay("21-May-19");
                events.add(new EventDay(calendar, DrawableUtils.getCircleDrawableWithText(this, "Chand"), appointmentInfo));
                Calendar calendar4 = Calendar.getInstance();
                calendar4.add(Calendar.DAY_OF_MONTH, 2);

                JsonSchedule appointmentInfo1 = new JsonSchedule();
                appointmentInfo1.setTotalAppointments(12);
                appointmentInfo1.setStartTime("09:30 pm");
                appointmentInfo1.setDay("23-May-19");
                events.add(new EventDay(calendar4, DrawableUtils.getThreeDots(this), appointmentInfo1));

                Calendar calendar3 = Calendar.getInstance();
                calendar3.add(Calendar.DAY_OF_MONTH, 5);

                JsonSchedule appointmentInfo3 = new JsonSchedule();
                appointmentInfo3.setTotalAppointments(6);
                appointmentInfo3.setStartTime("09:30 pm");
                appointmentInfo3.setDay("26-May-19");
                events.add(new EventDay(calendar3, DrawableUtils.getThreeDots(this), appointmentInfo3));

                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.DAY_OF_MONTH, 7);

                JsonSchedule appointmentInfo2 = new JsonSchedule();
                appointmentInfo2.setTotalAppointments(9);
                appointmentInfo2.setStartTime("09:30 pm");
                appointmentInfo2.setDay("28-May-19");
                events.add(new EventDay(calendar1, DrawableUtils.getThreeDots(this), appointmentInfo2));
            }
            return events;
            case 5: {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.SECOND, 12);
                cal.set(Calendar.MINUTE, 11);
                cal.set(Calendar.HOUR, 12);
                cal.set(Calendar.AM_PM, Calendar.AM);
                cal.set(Calendar.MONTH, Calendar.JUNE);
                cal.set(Calendar.DAY_OF_MONTH, 15);
                cal.set(Calendar.YEAR, 2019);

                JsonSchedule appointmentInfo1 = new JsonSchedule();
                appointmentInfo1.setTotalAppointments(21);
                appointmentInfo1.setStartTime("23:30 am");
                appointmentInfo1.setDay("15-June-19");
                events.add(new EventDay(cal, DrawableUtils.getThreeDots(this), appointmentInfo1));

                Calendar cal1 = Calendar.getInstance();
                cal1.set(Calendar.SECOND, 12);
                cal1.set(Calendar.MINUTE, 11);
                cal1.set(Calendar.HOUR, 12);
                cal1.set(Calendar.AM_PM, Calendar.AM);
                cal1.set(Calendar.MONTH, Calendar.JUNE);
                cal1.set(Calendar.DAY_OF_MONTH, 27);
                cal1.set(Calendar.YEAR, 2019);
                JsonSchedule appointmentInfo = new JsonSchedule();
                appointmentInfo.setTotalAppointments(5);
                appointmentInfo.setStartTime("9:00 pm");
                appointmentInfo.setDay("27-June-19");
                events.add(new EventDay(cal1, DrawableUtils.getCircleDrawableWithText(this, "Wow"), appointmentInfo));
            }
            return events;
            default:
                return events;
        }

    }

    private void fetchEvents(int month) {
        progressDialog.show();
        EventListAdapter adapter = new EventListAdapter(AppointmentActivity.this, new ArrayList<EventDay>());
        fh_list_view.setAdapter(adapter);
        FetchEvents fetchEvents = new FetchEvents(month);
        fetchEvents.execute();
    }

    private void eventResponse(int month) {
        List<EventDay> events = getMonthWiseEvents(month);
        calendarView.setEvents(events);
        EventListAdapter adapter = new EventListAdapter(AppointmentActivity.this, events);
        fh_list_view.setAdapter(adapter);
        dismissProgress();
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
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


    private class FetchEvents extends AsyncTask<String, Void, String> {
        private int month;

        public FetchEvents(int month) {
            this.month = month;
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            eventResponse(month);
        }
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
}
