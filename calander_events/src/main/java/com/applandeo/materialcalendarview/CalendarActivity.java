package com.applandeo.materialcalendarview;

import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.applandeo.materialcalendarview.utils.DrawableUtils;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalendarActivity extends AppCompatActivity {
    private FixedHeightListView fh_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

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

        CalendarView calendarView = findViewById(R.id.calendarView);
        fh_list_view = findViewById(R.id.fh_list_view);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -2);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 12);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);
        List<EventDay> events = getAllEvents();
        calendarView.setEvents(events);

        // calendarView.setDisabledDays(getDisabledDays());

        calendarView.setOnDayClickListener(eventDay ->
                Toast.makeText(getApplicationContext(),
                        null != eventDay.getAppointmentInfo() ? (eventDay.getCalendar().getTime().toString() + " "
                                + eventDay.isEnabled() + "\n" + eventDay.getAppointmentInfo().toString()) : (eventDay.getCalendar().getTime().toString() + " "
                                + eventDay.isEnabled()),
                        Toast.LENGTH_SHORT).show());

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                Toast.makeText(CalendarActivity.this, "Previous button click :"+calendarView.getCurrentPageDate().getTime().toString(), Toast.LENGTH_SHORT).show();
                //calendarView.setEvents(getAllJuneEvents());
                calendarView.invalidate();
            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                Toast.makeText(CalendarActivity.this, "Next button click :"+calendarView.getCurrentPageDate().getTime().toString(), Toast.LENGTH_SHORT).show();
                calendarView.setEvents(getAllJuneEvents());
            }
        });
        //calendarView.setOn
        ArrayList<String> temp_list = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {

            temp_list.add(events.get(i).getAppointmentInfo().toString());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                temp_list);
        fh_list_view.setAdapter(arrayAdapter);
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

    private List<EventDay> getAllEvents() {
        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        AppointmentInfo appointmentInfo = new AppointmentInfo();
        appointmentInfo.setPatientName("Rohan");
        appointmentInfo.setAppointmentTime("11:45 pm");
        appointmentInfo.setAppointmentDate("17-May-19");
        appointmentInfo.setInfo("Very urgent");
        events.add(new EventDay(calendar, DrawableUtils.getCircleDrawableWithText(this, "Chand"), appointmentInfo));
        Calendar calendar4 = Calendar.getInstance();
        calendar4.add(Calendar.DAY_OF_MONTH, 13);

        AppointmentInfo appointmentInfo1 = new AppointmentInfo();
        appointmentInfo1.setPatientName("Simaroo LTD");
        appointmentInfo1.setAppointmentTime("09:30 pm");
        appointmentInfo1.setAppointmentDate("10-June-19");
        appointmentInfo1.setInfo("Need diagnosis ASAP");
        events.add(new EventDay(calendar4, DrawableUtils.getThreeDots(this), appointmentInfo1));
        return events;
    }

    private List<EventDay> getAllJuneEvents() {
        List<EventDay> events = new ArrayList<>();
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.SECOND, 12);
        cal1.set(Calendar.MINUTE, 11);
        cal1.set(Calendar.HOUR, 12);
        cal1.set(Calendar.AM_PM, Calendar.AM);
        cal1.set(Calendar.MONTH, Calendar.JUNE);
        cal1.set(Calendar.DAY_OF_MONTH, 27);
        cal1.set(Calendar.YEAR, 2019);
        AppointmentInfo appointmentInfo = new AppointmentInfo();
        appointmentInfo.setPatientName("Samrat");
        appointmentInfo.setAppointmentTime("9:00 pm");
        appointmentInfo.setAppointmentDate("1-June-19");
        appointmentInfo.setInfo("Hello doctor, want to visit for diagnosis once");
        events.add(new EventDay(cal1, DrawableUtils.getCircleDrawableWithText(this, "Wow"), appointmentInfo));




        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 12);
        cal.set(Calendar.MINUTE, 11);
        cal.set(Calendar.HOUR, 12);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 15);
        cal.set(Calendar.YEAR, 2019);


        AppointmentInfo appointmentInfo1 = new AppointmentInfo();
        appointmentInfo1.setPatientName("Chaabar LTD");
        appointmentInfo1.setAppointmentTime("23:30 am");
        appointmentInfo1.setAppointmentDate("19-June-19");
        appointmentInfo1.setInfo("Lal Lab path");
        events.add(new EventDay(cal, DrawableUtils.getThreeDots(this), appointmentInfo1));
        ArrayList<String> temp_list = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {

            temp_list.add(events.get(i).getAppointmentInfo().toString());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                temp_list);
        fh_list_view.setAdapter(arrayAdapter);
        return events;
    }
}
