package com.noqapp.android.merchant.views.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.DrawableUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.OnFlingGestureListener;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.EventListAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppointmentActivity extends BaseActivity implements AppointmentPresenter {
    private FixedHeightListView fh_list_view;
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
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.menu_appointments));

        setProgressMessage("Fetching appointments...");
        setProgressCancel(false);
        codeRQ = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        Log.e("CODE_QR", codeRQ);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setSwipeEnabled(false);
        fh_list_view = findViewById(R.id.fh_list_view);
        scroll_view = findViewById(R.id.scroll_view);
        if (LaunchActivity.isTablet) {
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
                        m2.addUpdateListener(animation -> {
                            ((LinearLayout.LayoutParams) ll_right.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                            ll_right.requestLayout();
                        });
                        m2.start();
                        ValueAnimator m1 = ValueAnimator.ofFloat(4, 0); //fromWeight, toWeight
                        m1.setDuration(100);
                        m1.setStartDelay(100); //Optional Delay
                        m1.setInterpolator(new LinearInterpolator());
                        m1.addUpdateListener(animation -> {
                            ((LinearLayout.LayoutParams) calendarView.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                            calendarView.requestLayout();
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
                        m2.addUpdateListener(animation -> {
                            ((LinearLayout.LayoutParams) ll_right.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                            ll_right.requestLayout();
                        });
                        m2.start();

                        ValueAnimator m1 = ValueAnimator.ofFloat(0, 4); //fromWeight, toWeight
                        m1.setDuration(200);
                        m1.setStartDelay(100); //Optional Delay
                        m1.setInterpolator(new LinearInterpolator());
                        m1.addUpdateListener(animation -> {
                            ((LinearLayout.LayoutParams) calendarView.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                            calendarView.requestLayout();
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
        fab_add_image.setOnClickListener(v -> {
            Intent in = new Intent(AppointmentActivity.this, BookAppointmentActivity.class);
            in.putExtra("jsonScheduleList", (Serializable) jsonScheduleList);
            in.putExtra(IBConstant.KEY_CODE_QR, codeRQ);
            in.putExtra("bizCategoryId", getIntent().getStringExtra("bizCategoryId"));
            startActivityForResult(in, BOOKING_SUCCESS);
        });
        FloatingActionButton fab_decrease = findViewById(R.id.fab_decrease);
        FloatingActionButton fab_increase = findViewById(R.id.fab_increase);
        fab_decrease.setOnClickListener(v -> {
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
        });

        fab_increase.setOnClickListener(v -> {
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
        });
        fab_increase.hide();
        fab_decrease.hide();

        fh_list_view.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, id) -> {
            Intent in = new Intent(AppointmentActivity.this, AppointmentActivityNew.class);
            in.putExtra("selectedDate", ((JsonSchedule) adapter.getEventDayList().get(position).getEventObject()).getScheduleDate());
            in.putExtra(IBConstant.KEY_CODE_QR, codeRQ);
            in.putExtra("appointmentDuration", jsonScheduleList.getAppointmentDuration());
            in.putExtra("displayName", getIntent().getStringExtra("displayName"));
            in.putExtra("bizCategoryId", getIntent().getStringExtra("bizCategoryId"));
            in.putExtra("jsonScheduleList", (Serializable) jsonScheduleList);
            startActivity(in);
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

        calendarView.setOnPreviousPageChangeListener(() -> fetchEvents(calendarView.getCurrentPageDate()));

        calendarView.setOnForwardPageChangeListener(() -> fetchEvents(calendarView.getCurrentPageDate()));

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            fetchEvents(Calendar.getInstance());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
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
        adapter = new EventListAdapter(AppointmentActivity.this, new ArrayList<EventDay>());
        fh_list_view.setAdapter(adapter);
        showProgress();
        ScheduleApiCalls scheduleApiCalls = new ScheduleApiCalls();
        scheduleApiCalls.setAppointmentPresenter(this);
        scheduleApiCalls.scheduleForMonth(
                BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(),
                AppUtils.dateFormatAsYYYY_MM_DD(calendar), codeRQ);
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        this.jsonScheduleList = jsonScheduleList;
        Collections.sort(jsonScheduleList.getJsonSchedules(), (o1, o2) -> {
            try {
                return CommonHelper.SDF_YYYY_MM_DD.parse(o2.getScheduleDate()).compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(o1.getScheduleDate()));
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
        List<EventDay> events = parseEventList(jsonScheduleList);

        calendarView.setEvents(events);
        adapter = new EventListAdapter(AppointmentActivity.this, events);
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
}
