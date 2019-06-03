package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.pojos.AppointmentModel;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.AppointmentDateAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookAppointmentActivity extends AppCompatActivity implements
        AppointmentDateAdapter.OnItemClickListener, AppointmentPresenter {
    private Spinner sp_name_list;
    private TextView tv_empty_slots;
    private RecyclerView rv_available_date;
    private List<JsonHour> jsonHours;
    private Calendar selectedDate;
    private AppointmentDateAdapter appointmentDateAdapter;
    private int selectedPos = -1;
    private ScheduleApiCalls scheduleApiCalls;
    private ProgressDialog progressDialog;
    private JsonScheduleList jsonScheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initProgress();
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Book Appointment");
        scheduleApiCalls = new ScheduleApiCalls();
        scheduleApiCalls.setAppointmentPresenter(this);
//
        jsonScheduleList = (JsonScheduleList) getIntent().getExtras().getSerializable("jsonScheduleList");
        jsonHours = jsonScheduleList.getJsonHours();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, jsonScheduleList.getAppointmentOpenHowFar() * 7); // end date of appointment
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
//        tv_doctor_name.setText(bizStoreElastic.getDisplayName());
//        tv_doctor_category.setText(MedicalDepartmentEnum.valueOf(bizStoreElastic.getBizCategoryId()).getDescription());
//
        horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                selectedDate = date;
                fetchAppointments(new AppUtils().getDateWithFormat(selectedDate));
            }
        });
        horizontalCalendarView.refresh();
        tv_empty_slots = findViewById(R.id.tv_empty_slots);
        rv_available_date = findViewById(R.id.rv_available_date);
        rv_available_date.setLayoutManager(new GridLayoutManager(this, 3));
        rv_available_date.setItemAnimator(new DefaultItemAnimator());

        sp_name_list = findViewById(R.id.sp_name_list);

//        List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
//        profileList.add(0, NoQueueBaseActivity.getUserProfile());
//        profileList.add(0, new JsonProfile().setName("Select Patient"));
//        DependentAdapter adapter = new DependentAdapter(this, profileList);
//        sp_name_list.setAdapter(adapter);

        Button btn_book_appointment = findViewById(R.id.btn_book_appointment);
        btn_book_appointment.setOnClickListener(v -> {
            sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background));
            if (sp_name_list.getSelectedItemPosition() == 0) {
                //Toast.makeText(BookAppointmentActivity.this, getString(R.string.error_patient_name_missing), Toast.LENGTH_LONG).show();
                // sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background_red));
            } else if (selectedPos == -1) {
                Toast.makeText(BookAppointmentActivity.this, "Please select appointment date & time", Toast.LENGTH_LONG).show();
            } else {
                // Process
                if (LaunchActivity.getLaunchActivity().isOnline()) {
//                    progressDialog.setMessage("Booking appointment...");
//                    progressDialog.show();
//                    String[] temp = appointmentDateAdapter.getDataSet().get(selectedPos).getTime().split("-");
//                    JsonSchedule jsonSchedule = new JsonSchedule()
//                            .setCodeQR(bizStoreElastic.getCodeQR())
//                            .setStartTime(removeColon(temp[0].trim()))
//                            .setEndTime(removeColon(temp[1].trim()))
//                            .setScheduleDate(new AppUtilities().getDateWithFormat(selectedDate))
//                            .setQueueUserId(((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId());
//                    appointmentApiCalls.bookAppointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonSchedule);
                } else {
                    ShowAlertInformation.showNetworkDialog(BookAppointmentActivity.this);
                }
            }
        });
        selectedDate = startDate;
        fetchAppointments(new AppUtils().getTomorrowDateWithFormat());

    }

    @Override
    public void onAppointmentSelected(AppointmentModel item, int pos) {
        selectedPos = pos;
    }

    @Override
    public void onBookedAppointmentSelected() {
        selectedPos = -1;
    }

    private JsonHour getStoreHourElastic(List<JsonHour> jsonHourList, int day) {
        if (null != jsonHourList && jsonHourList.size() > 0) {
            for (int i = 0; i < jsonHourList.size(); i++) {
                if (jsonHourList.get(i).getDayOfWeek() == day) {
                    return jsonHourList.get(i);
                }
            }
        }
        return null;
    }

    private void setAppointmentSlots(JsonHour storeHourElastic, ArrayList<String> filledTimes) {
        List<AppointmentModel> listData = new ArrayList<>();
        String from = Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getAppointmentStartHour());
        String to = Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getAppointmentEndHour());
        ArrayList<String> timeSlot = getTimeSlots(jsonScheduleList.getAppointmentDuration(), from, to);
        for (int i = 0; i < timeSlot.size() - 1; i++) {
            listData.add(new AppointmentModel().setTime(timeSlot.get(i) + " - " + timeSlot.get(i + 1)).setBooked(filledTimes.contains(timeSlot.get(i))));
        }
        appointmentDateAdapter = new AppointmentDateAdapter(listData, this, this);
        rv_available_date.setAdapter(appointmentDateAdapter);
        if (listData.size() == 0) {
            tv_empty_slots.setVisibility(View.VISIBLE);
        } else {
            tv_empty_slots.setVisibility(View.GONE);
        }
    }

    public ArrayList<String> getTimeSlots(int slotMinute, String strFromTime, String strToTime) {
        ArrayList<String> timeSlot = new ArrayList<String>();
        try {
            int fromHour, fromMinute, toHour, toMinute;
            fromHour = Integer.parseInt(strFromTime.split(":")[0]);
            fromMinute = Integer.parseInt(strFromTime.split(":")[1]);

            toHour = Integer.parseInt(strToTime.split(":")[0]);
            toMinute = Integer.parseInt(strToTime.split(":")[1]);

            long slot = slotMinute * 60 * 1000;
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, fromHour);
            calendar2.set(Calendar.MINUTE, fromMinute);

            long currentTime = calendar2.getTimeInMillis();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, toHour);
            calendar1.set(Calendar.MINUTE, toMinute);
            long endTime = calendar1.getTimeInMillis();
            while (currentTime <= endTime) {
                DateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeSlot.add(sdfTime.format(new Date(currentTime)));
                currentTime = currentTime + slot;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlot;
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        ArrayList<String> filledTimes = new ArrayList<>();
        if (null != jsonScheduleList.getJsonSchedules() && jsonScheduleList.getJsonSchedules().size() > 0) {
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                String str = String.valueOf(jsonScheduleList.getJsonSchedules().get(i).getStartTime());
                String input = String.format("%4s", str).replace(' ', '0');
                int index = 1;
                String outPut = input.substring(0, index + 1) + ":" + input.substring(index + 1);
                Log.e("Check string----- ", input + "----------- " + outPut);
                filledTimes.add(outPut);
            }
        }
        int dayOfWeek = AppUtils.getDayOfWeek(selectedDate);
        JsonHour storeHourElastic = getStoreHourElastic(jsonHours, dayOfWeek);
        setAppointmentSlots(storeHourElastic, filledTimes);
        dismissProgress();
    }

    @Override
    public void appointmentBookingResponse(JsonSchedule jsonSchedule) {
        Log.e("Booking status", jsonSchedule.toString());
        //  Intent intent = new Intent(this, AppointmentDetailActivity.class);
//        intent.putExtra(IBConstant.KEY_DATA_OBJECT, jsonSchedule);
//        intent.putExtra(IBConstant.KEY_IMAGE_URL, bizStoreElastic.getDisplayImage());
//        startActivity(intent);
//        finish();
        dismissProgress();
    }

    @Override
    public void appointmentCancelResponse(JsonResponse jsonResponse) {
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);

    }


    private void fetchAppointments(String day) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setMessage("Fetching appointments...");
            progressDialog.show();
            scheduleApiCalls = new ScheduleApiCalls();
            scheduleApiCalls.setAppointmentPresenter(this);
            scheduleApiCalls.scheduleForDay(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(),
                    day,
                    getIntent().getStringExtra(IBConstant.KEY_CODE_QR));
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }


    }

    private int removeColon(String input) {
        try {
            if (input.contains(":"))
                return Integer.parseInt(input.replace(":", ""));
            else return Integer.parseInt(input);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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