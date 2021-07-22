package com.noqapp.android.client.views.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AppointmentSlotAdapter;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.AppointmentStateEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.CanteenStoreDepartmentEnum;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.pojos.AppointmentSlot;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookAppointmentActivity
    extends BaseActivity
    implements AppointmentSlotAdapter.OnItemClickListener, AppointmentPresenter {
    private TextView tv_name;
    private Spinner sp_name_list;
    private TextView tv_empty_slots;
    private RecyclerView rv_available_date;
    private List<StoreHourElastic> storeHourElastics;
    private BizStoreElastic bizStoreElastic;
    private Calendar selectedDate;
    private AppointmentSlotAdapter appointmentSlotAdapter;
    private int selectedPos = -1;
    private AppointmentApiCalls appointmentApiCalls;
    private AppointmentSlot firstAvailableAppointment = null;
    private int totalAvailableCount = 0;
    private boolean isAppointmentBooking = false;
    private TextView tv_slot_count, tv_slot_count_empty;
    private Button btn_book_appointment;
    private LinearLayout ll_sector, ll_slots;
    private View view_available, view_full;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initActionsViews(true);
        tv_toolbar_title.setText(R.string.txt_title_book_appointment);
        appointmentApiCalls = new AppointmentApiCalls();
        appointmentApiCalls.setAppointmentPresenter(this);

        bizStoreElastic = (BizStoreElastic) getIntent().getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
        if (null != bizStoreElastic) {
            storeHourElastics = bizStoreElastic.getStoreHourElasticList();
            switch (bizStoreElastic.getAppointmentState()) {
                case O:
                    //do nothing
                    break;
                case A:
                    isAppointmentBooking = true;
                    break;
                case S:
                case F:
                    isAppointmentBooking = false;
                    break;
            }
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
        ll_sector = findViewById(R.id.ll_sector);
        ll_slots = findViewById(R.id.ll_slots);
        view_available = findViewById(R.id.view_available);
        view_full = findViewById(R.id.view_full);
        tv_slot_count = findViewById(R.id.tv_slot_count);
        tv_slot_count_empty = findViewById(R.id.tv_slot_count_empty);
        tv_doctor_name.setText(bizStoreElastic.getDisplayName());
        switch (bizStoreElastic.getBusinessType()) {
            case DO:
                tv_doctor_category.setText(MedicalDepartmentEnum.valueOf(bizStoreElastic.getBizCategoryId()).getDescription());
                break;
            case CDQ:
                tv_doctor_category.setText(CanteenStoreDepartmentEnum.valueOf(bizStoreElastic.getBizCategoryId()).getDescription());
                break;
            default:
                tv_doctor_category.setVisibility(View.GONE);
        }

        TextView tv_title = findViewById(R.id.tv_title);
        btn_book_appointment = findViewById(R.id.btn_book_appointment);
        tv_empty_slots = findViewById(R.id.tv_empty_slots);
        String styledText = "<big><b><font color='#d41717'>Closed on this day</font></b></big><br/><small><b><font color='#d41717'>Not accepting appointment</font></b></small>";
        tv_empty_slots.setText(Html.fromHtml(styledText));
        rv_available_date = findViewById(R.id.rv_available_date);
        rv_available_date.setLayoutManager(new GridLayoutManager(this, 3));
        rv_available_date.setItemAnimator(new DefaultItemAnimator());
        tv_name = findViewById(R.id.tv_name);
        sp_name_list = findViewById(R.id.sp_name_list);
        if (isAppointmentBooking) {
            tv_title.setText(R.string.txt_available_times);
            ll_slots.setVisibility(View.GONE);
            rv_available_date.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText(R.string.txt_available_slots);
            ll_slots.setVisibility(View.VISIBLE);
            rv_available_date.setVisibility(View.GONE);
            btn_book_appointment.setText(R.string.txt_book_walkin_appointment);
        }

        horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                selectedDate = date;
                fetchAppointments(AppUtils.dateFormatAsYYYY_MM_DD(selectedDate));
            }
        });
        horizontalCalendarView.refresh();

        if (BusinessTypeEnum.DO == bizStoreElastic.getBusinessType() || BusinessTypeEnum.HS == bizStoreElastic.getBusinessType()) {
            List<JsonProfile> profileList = new LinkedList<>();
            DependentAdapter adapter = new DependentAdapter(this, profileList);
            if (AppInitialize.getUserProfile().getDependents().size() > 0) {
                profileList.add(new JsonProfile().setName("Select Patient"));
                profileList.add(AppInitialize.getUserProfile());
                profileList.addAll(AppInitialize.getUserProfile().getDependents());
            } else {
                profileList.add(new JsonProfile().setName("Select Patient"));
                profileList.add(AppInitialize.getUserProfile());
            }
            tv_name.setVisibility(View.INVISIBLE);
            sp_name_list.setAdapter(adapter);
            tv_name.setText(getText(R.string.patient_name));
        } else {
            List<JsonProfile> profileList = new ArrayList<>();
            profileList.add(new JsonProfile().setName("Select Person"));
            profileList.add(AppInitialize.getUserProfile());
            DependentAdapter adapter = new DependentAdapter(this, profileList);
            sp_name_list.setAdapter(adapter);
            tv_name.setText(getText(R.string.txt_booking_person));
            if (profileList.size() >= 2) {
                sp_name_list.setSelection(1);
                sp_name_list.setEnabled(false);
                sp_name_list.setClickable(false);
            }
        }

        btn_book_appointment.setOnClickListener(v -> {
            sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background));
            if (sp_name_list.getSelectedItemPosition() == 0) {
                if (BusinessTypeEnum.DO == bizStoreElastic.getBusinessType()) {
                    new CustomToast().showToast(BookAppointmentActivity.this, getString(R.string.error_patient_name_missing));
                } else {
                    new CustomToast().showToast(BookAppointmentActivity.this, getString(R.string.error_person_name_missing));
                }
                sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background_red));
            } else {
                if (isAppointmentBooking) {
                    if (selectedPos == -1) {
                        new CustomToast().showToast(BookAppointmentActivity.this, getString(R.string.txt_please_select_appointment_datetime));
                    } else {
                        // Process
                        if (isOnline()) {
                            // setProgressMessage("Booking appointment...");
                            //  showProgress();
                            String[] temp = appointmentSlotAdapter.getDataSet().get(selectedPos).getTimeSlot().split("-");
                            JsonSchedule jsonSchedule = new JsonSchedule()
                                .setCodeQR(bizStoreElastic.getCodeQR())
                                .setStartTime(AppUtils.removeColon(temp[0].trim()))
                                .setEndTime(AppUtils.removeColon(temp[1].trim()))
                                .setScheduleDate(AppUtils.dateFormatAsYYYY_MM_DD(selectedDate))
                                .setQueueUserId(((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId());
                            // appointmentApiCalls.bookAppointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonSchedule);
                            showConfirmationDialog(
                                BookAppointmentActivity.this,
                                ((JsonProfile) sp_name_list.getSelectedItem()).getName(),
                                AppUtils.dateFormatAsYYYY_MM_DD(selectedDate),
                                appointmentSlotAdapter.getDataSet().get(selectedPos).getTimeSlot(),
                                jsonSchedule);
                        } else {
                            ShowAlertInformation.showNetworkDialog(BookAppointmentActivity.this);
                        }
                    }
                } else {
                    if (null == firstAvailableAppointment || totalAvailableCount == 0) {
                        new CustomToast().showToast(BookAppointmentActivity.this, getString(R.string.txt_no_walkin_appointment_available));
                    } else {
                        if (isOnline()) {
                            //  setProgressMessage("Booking appointment...");
                            // showProgress();
                            String[] temp = firstAvailableAppointment.getTimeSlot().split("-");
                            JsonSchedule jsonSchedule = new JsonSchedule()
                                .setCodeQR(bizStoreElastic.getCodeQR())
                                .setStartTime(AppUtils.removeColon(temp[0].trim()))
                                .setEndTime(AppUtils.removeColon(temp[1].trim()))
                                .setScheduleDate(AppUtils.dateFormatAsYYYY_MM_DD(selectedDate))
                                .setQueueUserId(((JsonProfile) sp_name_list.getSelectedItem()).getQueueUserId());
                            //appointmentApiCalls.bookAppointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonSchedule);

                            showConfirmationDialog(
                                BookAppointmentActivity.this,
                                ((JsonProfile) sp_name_list.getSelectedItem()).getName(),
                                AppUtils.dateFormatAsYYYY_MM_DD(selectedDate),
                                "",
                                jsonSchedule);
                        } else {
                            ShowAlertInformation.showNetworkDialog(BookAppointmentActivity.this);
                        }
                    }
                }
            }
        });
        selectedDate = startDate;
        fetchAppointments(AppUtils.tomorrowAsDateFormat());
    }

    @Override
    public void onAppointmentSelected(AppointmentSlot item, int pos) {
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
        if (new AppUtils().checkStoreClosedWithTime(storeHourElastic) || new AppUtils().checkStoreClosedWithAppointmentTime(storeHourElastic)) {
            tv_empty_slots.setVisibility(View.VISIBLE);
            enableDisableBtn(false);
        } else {
            tv_empty_slots.setVisibility(View.GONE);
            enableDisableBtn(true);

            /* Number of appointment slots available. */
            List<String> timeSlot = AppUtils.computeTimeSlot(
                bizStoreElastic.getAppointmentDuration(),
                Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getAppointmentStartHour()),
                Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getAppointmentEndHour()),
                CommonHelper.AppointmentComputationEnum.TOTAL_SLOTS);

            List<AppointmentSlot> listData = new ArrayList<>();
            for (int i = 0; i < timeSlot.size() - 1; i++) {
                listData.add(
                    new AppointmentSlot()
                        .setTimeSlot(timeSlot.get(i) + " - " + timeSlot.get(i + 1))
                        .setBooked(filledTimes.contains(timeSlot.get(i))));

                if (!filledTimes.contains(timeSlot.get(i)) && null == firstAvailableAppointment) {
                    firstAvailableAppointment = listData.get(i);
                }

                if (!filledTimes.contains(timeSlot.get(i))) {
                    ++totalAvailableCount;
                }
            }
            appointmentSlotAdapter = new AppointmentSlotAdapter(listData, this, this);
            rv_available_date.setAdapter(appointmentSlotAdapter);
            appointmentSlotAdapter.notifyDataSetChanged();
            selectedPos = -1;
            if (listData.size() == 0) {
                tv_empty_slots.setVisibility(View.VISIBLE);
            } else {
                tv_empty_slots.setVisibility(View.GONE);
                if (isAppointmentBooking) {
                    // do nothing
                } else {

                    if (0 == totalAvailableCount) {
                        tv_slot_count.setText("No more walk-in appointment available");
                        tv_slot_count_empty.setVisibility(View.VISIBLE);
                        tv_slot_count.setVisibility(View.GONE);
                        ll_sector.setVisibility(View.GONE);
                        // btn_book_appointment.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bg_inactive));
                        // btn_book_appointment.setTextColor(ContextCompat.getColor(this, R.color.btn_color));
                        enableDisableBtn(false);
                    } else {
                        ll_sector.setVisibility(View.VISIBLE);
                        tv_slot_count.setVisibility(View.VISIBLE);
                        tv_slot_count.setText(totalAvailableCount + " out of " + appointmentSlotAdapter.getDataSet().size() + " walk-in appointments available");
                        float f = totalAvailableCount * 100 / appointmentSlotAdapter.getDataSet().size();
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, 80, f / 100);
                        view_available.setLayoutParams(param);
                        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(0, 80, (100 - f) / 100);
                        view_full.setLayoutParams(param1);

                        // btn_book_appointment.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bg_enable));
                        //  btn_book_appointment.setTextColor(Color.parseColor("#ffffff"));
                        enableDisableBtn(true);
                        tv_slot_count_empty.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void enableDisableBtn(boolean isEnable) {
        btn_book_appointment.setEnabled(isEnable);
        if (isEnable) {
            btn_book_appointment.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bg_enable));
            btn_book_appointment.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btn_book_appointment.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bg_inactive));
            btn_book_appointment.setTextColor(ContextCompat.getColor(this, R.color.btn_color));
        }

    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        ArrayList<String> filledTimes = new ArrayList<>();
        if (null != jsonScheduleList.getJsonSchedules() && jsonScheduleList.getJsonSchedules().size() > 0) {
            List<String> appointmentSlots = new ArrayList<>();
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                appointmentSlots.addAll(
                    AppUtils.computeTimeSlot(
                        bizStoreElastic.getAppointmentDuration(),
                        AppUtils.getTimeFourDigitWithColon(jsonScheduleList.getJsonSchedules().get(i).getStartTime()),
                        AppUtils.getTimeFourDigitWithColon(jsonScheduleList.getJsonSchedules().get(i).getEndTime()),
                        CommonHelper.AppointmentComputationEnum.FILLED));
            }
            filledTimes.addAll(appointmentSlots);
        }
        int dayOfWeek = AppUtils.getDayOfWeek(selectedDate);
        StoreHourElastic storeHourElastic = getStoreHourElastic(storeHourElastics, dayOfWeek);
        if (null != jsonScheduleList.getAppointmentState() && AppointmentStateEnum.O == jsonScheduleList.getAppointmentState()) {
            /* When schedule closed for a specific duration. */
            StoreHourElastic notAcceptingAppointment = new StoreHourElastic()
                .setDayClosed(true)
                .setAppointmentStartHour(0)
                .setAppointmentEndHour(0);
            setAppointmentSlots(notAcceptingAppointment, filledTimes);
        } else {
            setAppointmentSlots(storeHourElastic, filledTimes);
        }
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
        firstAvailableAppointment = null;
        totalAvailableCount = 0;
        tv_slot_count.setVisibility(View.GONE);
        tv_slot_count_empty.setVisibility(View.GONE);
        ll_sector.setVisibility(View.GONE);
        appointmentSlotAdapter = new AppointmentSlotAdapter(new ArrayList<>(), this, this);
        rv_available_date.setAdapter(appointmentSlotAdapter);
        if (isOnline()) {
            setProgressMessage("Fetching appointments...");
            showProgress();
            appointmentApiCalls.scheduleForDay(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth(),
                day,
                bizStoreElastic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    public void showConfirmationDialog(
        Context context,
        String patientName,
        String bookingDate,
        String bookingSlot,
        JsonSchedule jsonSchedule
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.booking_confirm, null, false);

        TextView tv_patient_name = customDialogView.findViewById(R.id.tv_patient_name);
        TextView tv_booking_date = customDialogView.findViewById(R.id.tv_booking_date);
        TextView tv_booking_slot = customDialogView.findViewById(R.id.tv_booking_slot);
        tv_patient_name.setText(patientName);
        tv_booking_date.setText(bookingDate);
        tv_booking_slot.setText(bookingSlot);
        if (TextUtils.isEmpty(bookingSlot)) {
            LinearLayout ll_booking_slot = customDialogView.findViewById(R.id.ll_booking_slot);
            ll_booking_slot.setVisibility(View.GONE);
        }
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        btn_no.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v -> {
            setProgressMessage("Booking appointment...");
            showProgress();
            appointmentApiCalls.bookAppointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonSchedule);
            mAlertDialog.dismiss();
        });
        mAlertDialog.show();
    }
}
