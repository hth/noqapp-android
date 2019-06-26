package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AppointmentApiCalls;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AppointmentDateAdapter;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.pojos.AppointmentModel;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.Formatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookAppointmentActivity extends BaseActivity implements
        AppointmentDateAdapter.OnItemClickListener, AppointmentPresenter {
    private Spinner sp_name_list;
    private TextView tv_empty_slots;
    private RecyclerView rv_available_date;
    private List<StoreHourElastic> storeHourElastics;
    private BizStoreElastic bizStoreElastic;
    private Calendar selectedDate;
    private AppointmentDateAdapter appointmentDateAdapter;
    private int selectedPos = -1;
    private AppointmentApiCalls appointmentApiCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initActionsViews(true);
        tv_toolbar_title.setText("Book Appointment");
        appointmentApiCalls = new AppointmentApiCalls();
        appointmentApiCalls.setAppointmentPresenter(this);

        bizStoreElastic = (BizStoreElastic) getIntent().getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
        if (null != bizStoreElastic) {
            storeHourElastics = bizStoreElastic.getStoreHourElasticList();
        }
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, bizStoreElastic.getAppointmentOpenHowFar() * 7); // end date of appointment
        Calendar startDate = Calendar.getInstance();
        Date dt = new Date();
        startDate.setTime(dt);
        startDate.add(Calendar.DAY_OF_MONTH, 1); // start date of appointment


        HorizontalCalendar horizontalCalendarView = new HorizontalCalendar.Builder(this, R.id.horizontalCalendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatBottomText("EEE")
                .formatMiddleText("dd")
                .formatTopText("MMM")
                .textSize(14f, 24f, 14f)
                .end()
                .build();
        TextView tv_doctor_category = findViewById(R.id.tv_doctor_category);
        TextView tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_doctor_name.setText(bizStoreElastic.getDisplayName());
        tv_doctor_category.setText(MedicalDepartmentEnum.valueOf(bizStoreElastic.getBizCategoryId()).getDescription());

        horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                selectedDate = date;
                fetchAppointments(new AppUtilities().getDateWithFormat(selectedDate));
            }
//            @Override
//            public boolean onDateLongClicked(Calendar date, int position) {
//                selectedDate = date;
//                fetchAppointments(new AppUtilities().getDateWithFormat(selectedDate));
//                return true;
//            }
        });
        horizontalCalendarView.refresh();
        tv_empty_slots = findViewById(R.id.tv_empty_slots);
        String styledText = "<big><b><font color='#d41717'>Closed on this day</font></b></big><br/><small><b><font color='#d41717'>Not accepting appointment</font></b></small>";
        tv_empty_slots.setText(Html.fromHtml(styledText));
        rv_available_date = findViewById(R.id.rv_available_date);
        rv_available_date.setLayoutManager(new GridLayoutManager(this, 3));
        rv_available_date.setItemAnimator(new DefaultItemAnimator());
        sp_name_list = findViewById(R.id.sp_name_list);

        List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
        profileList.add(0, NoQueueBaseActivity.getUserProfile());
        profileList.add(0, new JsonProfile().setName("Select Patient"));
        DependentAdapter adapter = new DependentAdapter(this, profileList);
        sp_name_list.setAdapter(adapter);

        Button btn_book_appointment = findViewById(R.id.btn_book_appointment);
        btn_book_appointment.setOnClickListener(v -> {
            sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background));
            if (sp_name_list.getSelectedItemPosition() == 0) {
                new CustomToast().showToast(BookAppointmentActivity.this, getString(R.string.error_patient_name_missing));
                sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background_red));
            } else if (selectedPos == -1) {
                new CustomToast().showToast(BookAppointmentActivity.this, "Please select appointment date & time");
            } else {
                // Process
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    setProgressMessage("Booking appointment...");
                    showProgress();
                    String[] temp = appointmentDateAdapter.getDataSet().get(selectedPos).getTime().split("-");
                    JsonSchedule jsonSchedule = new JsonSchedule()
                            .setCodeQR(bizStoreElastic.getCodeQR())
                            .setStartTime(AppUtilities.removeColon(temp[0].trim()))
                            .setEndTime(AppUtilities.removeColon(temp[1].trim()))
                            .setScheduleDate(new AppUtilities().getDateWithFormat(selectedDate))
                            .setQueueUserId(((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId());
                    appointmentApiCalls.bookAppointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonSchedule);
                } else {
                    ShowAlertInformation.showNetworkDialog(BookAppointmentActivity.this);
                }
            }
        });
        selectedDate = startDate;
        fetchAppointments(new AppUtilities().getTomorrowDateWithFormat());

    }

    @Override
    public void onAppointmentSelected(AppointmentModel item, int pos) {
        selectedPos = pos;
    }

    @Override
    public void onBookedAppointmentSelected() {
        selectedPos = -1;
    }

    private StoreHourElastic getStoreHourElastic(List<StoreHourElastic> jsonHourList, int day) {
        if (null != jsonHourList && jsonHourList.size() > 0) {
            for (int i = 0; i < jsonHourList.size(); i++) {
                if (jsonHourList.get(i).getDayOfWeek() == day) {
                    return jsonHourList.get(i);
                }
            }
        }
        return null;
    }

    private void setAppointmentSlots(StoreHourElastic storeHourElastic, ArrayList<String> filledTimes) {
        List<AppointmentModel> listData = new ArrayList<>();
        if (new AppUtilities().checkStoreClosedWithTime(storeHourElastic)) {
            tv_empty_slots.setVisibility(View.VISIBLE);
        } else {
            tv_empty_slots.setVisibility(View.GONE);
            String from = Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getAppointmentStartHour());
            String to = Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getAppointmentEndHour());
            ArrayList<String> timeSlot = AppUtilities.getTimeSlots(bizStoreElastic.getAppointmentDuration(), from, to, true);
            for (int i = 0; i < timeSlot.size() - 1; i++) {
                listData.add(new AppointmentModel().setTime(timeSlot.get(i) + " - " + timeSlot.get(i + 1)).setBooked(filledTimes.contains(timeSlot.get(i))));
            }
            appointmentDateAdapter = new AppointmentDateAdapter(listData, this, this);
            rv_available_date.setAdapter(appointmentDateAdapter);
            appointmentDateAdapter.notifyDataSetChanged();
            selectedPos = -1;
            if (listData.size() == 0) {
                tv_empty_slots.setVisibility(View.VISIBLE);
            } else {
                tv_empty_slots.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        ArrayList<String> filledTimes = new ArrayList<>();
        if (null != jsonScheduleList.getJsonSchedules() && jsonScheduleList.getJsonSchedules().size() > 0) {
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                filledTimes.addAll(AppUtilities.getTimeSlots(bizStoreElastic.getAppointmentDuration(),
                        AppUtilities.getTimeFourDigitWithColon(jsonScheduleList.getJsonSchedules().get(i).getStartTime()),
                        AppUtilities.getTimeFourDigitWithColon(jsonScheduleList.getJsonSchedules().get(i).getEndTime()), false));
            }
        }
        int dayOfWeek = AppUtilities.getDayOfWeek(selectedDate);
        StoreHourElastic storeHourElastic = getStoreHourElastic(storeHourElastics, dayOfWeek);
        setAppointmentSlots(storeHourElastic, filledTimes);
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {
        Log.e("Booking status", jsonSchedule.toString());
        Intent intent = new Intent(this, AppointmentDetailActivity.class);
        intent.putExtra(IBConstant.KEY_DATA_OBJECT, jsonSchedule);
        intent.putExtra(IBConstant.KEY_IMAGE_URL, bizStoreElastic.getDisplayImage());
        startActivity(intent);
        finish();
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


    private void fetchAppointments(String day) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Fetching appointments...");
            showProgress();
            appointmentApiCalls.scheduleForDay(UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth(), day, bizStoreElastic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }
}