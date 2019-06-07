package com.noqapp.android.merchant.views.activities;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.EventListAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;
import com.noqapp.android.merchant.views.utils.OnFlingGestureListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AppointmentActivity extends AppCompatActivity implements AppointmentPresenter,
        EventListAdapter.OnItemClickListener {
    private FixedHeightListView fh_list_view;
    private ProgressDialog progressDialog;
    private CalendarView calendarView;
    public EventListAdapter adapter;
    private String codeRQ = "";
    private ScrollView scroll_view;
    private JsonScheduleList jsonScheduleList;
    private final int BOOKING_SUCCESS = 101;
    private int weight = 5;
    private boolean isOpen = true;

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
        calendarView.setSwipeEnabled(false);
        fh_list_view = findViewById(R.id.fh_list_view);
        scroll_view = findViewById(R.id.scroll_view);
        if (new AppUtils().isTablet(getApplicationContext())) {
            RelativeLayout rl_parent = findViewById(R.id.rl_parent);
            LinearLayout ll_right = findViewById(R.id.ll_right);
            rl_parent.setOnTouchListener(new OnFlingGestureListener(this) {

                @Override
                public void onTopToBottom() {
                    new CustomToast().showToast(AppointmentActivity.this, "Top swipe");
                }

                @Override
                public void onRightToLeft() {
                   // new CustomToast().showToast(AppointmentActivity.this, "Right swipe");
                    //close it
                    if (isOpen) {

                        ValueAnimator m2 = ValueAnimator.ofFloat(6, 10); //fromWeight, toWeight
                        m2.setDuration(200);
                        m2.setStartDelay(100); //Optional Delay
                        m2.setInterpolator(new LinearInterpolator());
                        m2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ((LinearLayout.LayoutParams) ll_right.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                                ll_right.requestLayout();
                            }

                        });
                        m2.start();
                        ValueAnimator m1 = ValueAnimator.ofFloat(4, 0); //fromWeight, toWeight
                        m1.setDuration(100);
                        m1.setStartDelay(100); //Optional Delay
                        m1.setInterpolator(new LinearInterpolator());
                        m1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ((LinearLayout.LayoutParams) calendarView.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                                calendarView.requestLayout();
                            }

                        });
                        m1.start();
                        isOpen = false;
                    }
                }

                @Override
                public void onLeftToRight() {
                    //open it
                    if (!isOpen) {
                       // new CustomToast().showToast(AppointmentActivity.this, "Left swipe");
                        ValueAnimator m2 = ValueAnimator.ofFloat(10, 6); //fromWeight, toWeight
                        m2.setDuration(100);
                        m2.setStartDelay(100); //Optional Delay
                        m2.setInterpolator(new LinearInterpolator());
                        m2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ((LinearLayout.LayoutParams) ll_right.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                                ll_right.requestLayout();
                            }

                        });
                        m2.start();

                        ValueAnimator m1 = ValueAnimator.ofFloat(0, 4); //fromWeight, toWeight
                        m1.setDuration(200);
                        m1.setStartDelay(100); //Optional Delay
                        m1.setInterpolator(new LinearInterpolator());
                        m1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                ((LinearLayout.LayoutParams) calendarView.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                                calendarView.requestLayout();
                            }

                        });
                        m1.start();
                        isOpen = true;
                    }
                }

                @Override
                public void onBottomToTop() {
                    new CustomToast().showToast(AppointmentActivity.this, "bottom swipe");
                }
            });
        }
        FloatingActionButton fab_add_image = findViewById(R.id.fab_add_image);
        fab_add_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(AppointmentActivity.this, BookAppointmentActivity.class);
                in.putExtra("jsonScheduleList", (Serializable) jsonScheduleList);
                in.putExtra(IBConstant.KEY_CODE_QR, codeRQ);
                startActivityForResult(in, BOOKING_SUCCESS);
            }
        });
        FloatingActionButton fab_decrease = findViewById(R.id.fab_decrease);
        FloatingActionButton fab_increase = findViewById(R.id.fab_increase);
        fab_decrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //
                if (weight > 2 && weight <= 5) {
                    // --weight;


                    ValueAnimator m1 = ValueAnimator.ofFloat(weight, --weight); //fromWeight, toWeight
                    m1.setDuration(400);
                    m1.setStartDelay(100); //Optional Delay
                    m1.setInterpolator(new LinearInterpolator());
                    m1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            ((LinearLayout.LayoutParams) calendarView.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                            calendarView.requestLayout();
                        }

                    });
                    m1.start();


//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
//                    lp.weight = weight;
//                    calendarView.setLayoutParams(lp);
//
//                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
//                    lp1.weight = 10-weight;
//                    ll_right.setLayoutParams(lp1);
                    fab_increase.show();
                    if (weight == 2) {
                        fab_decrease.hide();
                    }
//
                }
            }
        });

        fab_increase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //
                if (weight >= 2 && weight < 5) {
                    //++weight;


                    ValueAnimator m1 = ValueAnimator.ofFloat(weight, ++weight); //fromWeight, toWeight
                    m1.setDuration(400);
                    m1.setStartDelay(100); //Optional Delay
                    m1.setInterpolator(new LinearInterpolator());
                    m1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            ((LinearLayout.LayoutParams) calendarView.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                            calendarView.requestLayout();
                        }

                    });
                    m1.start();
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
//                    lp.weight = weight;
//                    calendarView.setLayoutParams(lp);
//
//                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
//                    lp1.weight = 10-weight;
//                    ll_right.setLayoutParams(lp1);
                    fab_decrease.show();
                    if (weight == 5) {
                        fab_increase.hide();
                    }
//
                }
            }
        });
        fab_increase.hide();
        fab_decrease.hide();

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
        this.jsonScheduleList = jsonScheduleList;
        Collections.sort(jsonScheduleList.getJsonSchedules(), new Comparator<JsonSchedule>() {
            public int compare(JsonSchedule o1, JsonSchedule o2) {
                try {
                    return CommonHelper.SDF_YYYY_MM_DD.parse(o2.getScheduleDate()).compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(o1.getScheduleDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        List<EventDay> events = parseEventList(jsonScheduleList);

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
    public void appointmentAcceptRejectResponse(JsonSchedule jsonSchedule) {
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
