package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.JsonScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.AppointmentStateEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.pojos.AppointmentSlot;
import com.noqapp.android.common.presenter.AppointmentPresenter;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.model.ScheduleApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.body.merchant.BookSchedule;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.AppointmentSlotAdapter;
import com.noqapp.android.merchant.views.adapters.JsonProfileAdapter;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookAppointmentActivity extends BaseActivity implements
        AppointmentSlotAdapter.OnItemClickListener, AppointmentPresenter, FindCustomerPresenter
        , RegistrationActivity.RegisterCallBack, LoginActivity.LoginCallBack {
    private TextView tv_empty_slots;
    private RecyclerView rv_available_date;
    private List<JsonHour> jsonHours;
    private Calendar selectedDate;
    private AppointmentSlotAdapter appointmentSlotAdapter;
    private int selectedPos = -1;
    private ScheduleApiCalls scheduleApiCalls;
    private Button btn_create_token;
    private Spinner sp_patient_list;
    private LinearLayout ll_mobile;
    private EditText edt_mobile;
    private Spinner sp_start_time, sp_end_time;
    private TextView tv_select_patient;
    private AutoCompleteTextView actv_chief_complaints;
    private String countryCode = "";
    private String cid = "";
    private CountryCodePicker ccp;
    private long mLastClickTime = 0;
    private Button btn_create_order;
    private BusinessCustomerApiCalls businessCustomerApiCalls;
    private String codeQR = "";
    private ArrayList<String> times = new ArrayList<>();

    private int appointmentDuration;
    private int appointmentOpenHowFar;
    private int count = 3;
    private boolean isEdit = false;
    private JsonSchedule jsonScheduleTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LaunchActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            count = 6;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            count = 3;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initActionsViews(false);
        tv_toolbar_title.setText("Book Appointment");
        tv_empty_slots = findViewById(R.id.tv_empty_slots);
        rv_available_date = findViewById(R.id.rv_available_date);
        rv_available_date.setLayoutManager(new GridLayoutManager(this, count));
        rv_available_date.setItemAnimator(new DefaultItemAnimator());
        scheduleApiCalls = new ScheduleApiCalls();
        scheduleApiCalls.setAppointmentPresenter(this);
        JsonScheduleList jsonScheduleList = (JsonScheduleList) getIntent().getExtras().getSerializable("jsonScheduleList");
        appointmentDuration = jsonScheduleList.getAppointmentDuration();
        appointmentOpenHowFar = jsonScheduleList.getAppointmentOpenHowFar();
        codeQR = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        jsonHours = jsonScheduleList.getJsonHours();
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            jsonScheduleTemp = (JsonSchedule) getIntent().getExtras().getSerializable("jsonSchedule");
        }
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, appointmentOpenHowFar * 7); // end date of appointment
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
        horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                selectedDate = date;
                fetchAppointments(AppUtils.dateFormatAsYYYY_MM_DD(selectedDate));
            }
        });
        horizontalCalendarView.refresh();
        Button btn_book_appointment = findViewById(R.id.btn_book_appointment);
        btn_book_appointment.setOnClickListener(v -> {
            if (selectedPos == -1) {
                new CustomToast().showToast(BookAppointmentActivity.this, "Please select appointment date & time");
            } else {
                if (isEdit) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        setProgressMessage("Booking appointment...");
                        showProgress();
                        setProgressCancel(false);

                        long diffInMinutes = calculateAppointmentSlot(AppUtils.getTimeFourDigitWithColon(jsonScheduleTemp.getMultipleSlotStartTiming()),
                                AppUtils.getTimeFourDigitWithColon(jsonScheduleTemp.getMultipleSlotEndTiming()));
                        String[] temp = appointmentSlotAdapter.getDataSet().get(selectedPos).getTimeSlot().split("-");
                        String endTime = getEndTime((int) diffInMinutes, temp[0].trim());
                        jsonScheduleTemp.setStartTime(AppUtils.removeColon(temp[0].trim()));
                        jsonScheduleTemp.setEndTime(AppUtils.removeColon(endTime));
                        jsonScheduleTemp.setScheduleDate(AppUtils.dateFormatAsYYYY_MM_DD(selectedDate));
                        BookSchedule bookSchedule = new BookSchedule()
                                .setBusinessCustomer(null)
                                .setJsonSchedule(jsonScheduleTemp)
                                .setBookActionType(ActionTypeEnum.EDIT);
                        scheduleApiCalls.bookSchedule(BaseLaunchActivity.getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                bookSchedule);
                    } else {
                        ShowAlertInformation.showNetworkDialog(BookAppointmentActivity.this);
                    }
                } else {
                    searchPatientWithMobileNoORCustomerId();
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

    private void setAppointmentSlots(JsonHour jsonHour, ArrayList<String> filledTimes) {
        if (new AppUtils().checkStoreClosedWithTime(jsonHour) || new AppUtils().checkStoreClosedWithAppointmentTime(jsonHour)) {
            tv_empty_slots.setVisibility(View.VISIBLE);
        } else {
            tv_empty_slots.setVisibility(View.GONE);
            List<AppointmentSlot> listData = new ArrayList<>();
            String from = Formatter.convertMilitaryTo24HourFormat(jsonHour.getAppointmentStartHour());
            String to = Formatter.convertMilitaryTo24HourFormat(jsonHour.getAppointmentEndHour());
            List<String> timeSlot = AppUtils.getTimeSlots(appointmentDuration, from, to, true);
            for (int i = 0; i < timeSlot.size() - 1; i++) {
                listData.add(
                    new AppointmentSlot()
                        .setTimeSlot(timeSlot.get(i) + " - " + timeSlot.get(i + 1))
                        .setBooked(filledTimes.contains(timeSlot.get(i)) && filledTimes.contains(timeSlot.get(i+1))));
                times.add(timeSlot.get(i));
            }
            appointmentSlotAdapter = new AppointmentSlotAdapter(listData, this, this);
            rv_available_date.setAdapter(appointmentSlotAdapter);
            appointmentSlotAdapter.notifyDataSetChanged();
            if (listData.size() == 0) {
                tv_empty_slots.setVisibility(View.VISIBLE);
            } else {
                tv_empty_slots.setVisibility(View.GONE);
            }
        }
        selectedPos = -1;
    }

    @Override
    public void appointmentResponse(JsonScheduleList jsonScheduleList) {
        Log.e("appointments", jsonScheduleList.toString());
        ArrayList<String> filledTimes = new ArrayList<>();
        if (null != jsonScheduleList.getJsonSchedules() && jsonScheduleList.getJsonSchedules().size() > 0) {
            for (int i = 0; i < jsonScheduleList.getJsonSchedules().size(); i++) {
                filledTimes.addAll(AppUtils.getTimeSlots(appointmentDuration,
                        AppUtils.getTimeFourDigitWithColon(jsonScheduleList.getJsonSchedules().get(i).getStartTime()),
                        AppUtils.getTimeFourDigitWithColon(jsonScheduleList.getJsonSchedules().get(i).getEndTime()), true));
            }
        }
        int dayOfWeek = AppUtils.getDayOfWeek(selectedDate);
        JsonHour storeHourElastic = getStoreHourElastic(jsonHours, dayOfWeek);
        if (null != jsonScheduleList.getAppointmentState() && AppointmentStateEnum.O == jsonScheduleList.getAppointmentState()) {
            /* When schedule closed for a specific duration. */
            JsonHour notAcceptingAppointment = new JsonHour()
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
        if (null != jsonSchedule) {
            Log.e("Booking status", jsonSchedule.toString());
            new CustomToast().showToast(this, "Appointment booked successfully!!!");
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            // Do nothing
            new CustomToast().showToast(this, "Appointment booking failed");
        }
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
        dismissProgress();
        if (null != eej) {
            if (eej.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
                new CustomToast().showToast(this, eej.getReason());
                Intent in = new Intent(this, LoginActivity.class);
                in.putExtra("phone_no", edt_mobile.getText().toString());
                startActivity(in);
                RegistrationActivity.registerCallBack = this;
                LoginActivity.loginCallBack = this;
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }

    private void fetchAppointments(String day) {
        times.clear();
        appointmentSlotAdapter = new AppointmentSlotAdapter(new ArrayList<AppointmentSlot>(), this, this);
        rv_available_date.setAdapter(appointmentSlotAdapter);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Fetching appointments...");
            setProgressCancel(false);
            showProgress();
            scheduleApiCalls = new ScheduleApiCalls();
            scheduleApiCalls.setAppointmentPresenter(this);
            scheduleApiCalls.scheduleForDay(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(),
                    day,
                    codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void searchPatientWithMobileNoORCustomerId() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.FullScreenDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_search_patient_for_appointment, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        ll_mobile = customDialogView.findViewById(R.id.ll_mobile);
        edt_mobile = customDialogView.findViewById(R.id.edt_mobile);
        sp_end_time = customDialogView.findViewById(R.id.sp_end_time);
        sp_start_time = customDialogView.findViewById(R.id.sp_start_time);
        sp_patient_list = customDialogView.findViewById(R.id.sp_patient_list);
        tv_select_patient = customDialogView.findViewById(R.id.tv_select_patient);
        actv_chief_complaints = customDialogView.findViewById(R.id.actv_chief_complaints);
        final ArrayList<String> data = new ArrayList<>();
        ArrayList<DataObj> temp = MedicalDataStatic.getSymptomsOnCategoryType(getIntent().getStringExtra("bizCategoryId"));
        if (temp.size() > 0) {
            for (int i = 0; i < temp.size(); i++) {
                data.add(temp.get(i).getShortName());
            }
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        actv_chief_complaints.setAdapter(adapter1);
        actv_chief_complaints.setThreshold(1);
        actv_chief_complaints.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(BookAppointmentActivity.this,
                R.layout.spinner_item, times);
        sp_end_time.setAdapter(sp_adapter);
        sp_start_time.setAdapter(sp_adapter);
        String[] split = appointmentSlotAdapter.getDataSet().get(selectedPos).getTimeSlot().split("-");
        int selectStart = times.indexOf(split[0].trim());
        int selectEnd = times.indexOf(split[1].trim());
        if (selectStart != -1) {
            sp_start_time.setSelection(selectStart);
        }
        if (selectEnd != -1) {
            sp_end_time.setSelection(selectEnd);
        }
        final EditText edt_id = customDialogView.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = customDialogView.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = customDialogView.findViewById(R.id.rb_mobile);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_mobile) {
                ll_mobile.setVisibility(View.VISIBLE);
                edt_id.setVisibility(View.GONE);
                edt_id.setText("");
            } else {
                edt_id.setVisibility(View.VISIBLE);
                ll_mobile.setVisibility(View.GONE);
                edt_mobile.setText("");
            }
        });
        cid = "";
        ccp = customDialogView.findViewById(R.id.ccp);
        String c_codeValue = LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        btn_create_order = customDialogView.findViewById(R.id.btn_create_order);
        btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setText("Search Patient");
        btn_create_token.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            boolean isValid = true;
            edt_mobile.setError(null);
            edt_id.setError(null);
            AppUtils.hideKeyBoard(BookAppointmentActivity.this);
            // get selected radio button from radioGroup
            int selectedId = rg_user_id.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_mobile) {
                if (TextUtils.isEmpty(edt_mobile.getText())) {
                    edt_mobile.setError(getString(R.string.error_mobile_blank));
                    isValid = false;
                }
            } else {
                if (TextUtils.isEmpty(edt_id.getText())) {
                    edt_id.setError(getString(R.string.error_customer_id));
                    isValid = false;
                }
            }

            if (isValid) {
                setProgressMessage("Searching patient...");
                showProgress();
                setProgressCancel(false);
                String phone = "";
                cid = "";
                if (rb_mobile.isChecked()) {
                    edt_id.setText("");
                    countryCode = ccp.getSelectedCountryCode();
                    phone = countryCode + edt_mobile.getText().toString();
                    cid = "";
                } else {
                    cid = edt_id.getText().toString();
                    edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                }
                businessCustomerApiCalls = new BusinessCustomerApiCalls();
                businessCustomerApiCalls.setFindCustomerPresenter(BookAppointmentActivity.this);
                businessCustomerApiCalls.findCustomer(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                btn_create_token.setClickable(false);
                //  mAlertDialog.dismiss();
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }


    @Override
    public void findCustomerResponse(JsonProfile jsonProfile) {
        dismissProgress();
        if (null != jsonProfile) {
            List<JsonProfile> jsonProfileList = new ArrayList<>();
            jsonProfileList.add(jsonProfile);
            if (jsonProfile.getDependents().size() > 0) {
                jsonProfileList.addAll(jsonProfile.getDependents());
            }
            JsonProfileAdapter adapter = new JsonProfileAdapter(this, jsonProfileList);
            sp_patient_list.setAdapter(adapter);
            sp_patient_list.setVisibility(View.VISIBLE);
            edt_mobile.setEnabled(false);
            tv_select_patient.setVisibility(View.VISIBLE);
            btn_create_order.setText("Book Appointment");
            btn_create_order.setVisibility(View.VISIBLE);
            btn_create_token.setVisibility(View.GONE);
            btn_create_order.setOnClickListener(v -> {
                try {
                    int start = AppUtils.removeColon((String) sp_start_time.getSelectedItem());
                    int end = AppUtils.removeColon((String) sp_end_time.getSelectedItem());
                    if (start < end) {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            btn_create_order.setEnabled(false);
                            setProgressMessage("Booking appointment...");
                            showProgress();
                            setProgressCancel(false);
                            String phoneNoWithCode = "";
                            if (TextUtils.isEmpty(cid)) {
                                phoneNoWithCode = PhoneFormatterUtil.phoneNumberWithCountryCode(jsonProfile.getPhoneRaw(), jsonProfile.getCountryShortName());
                            }

                            JsonBusinessCustomer jsonBusinessCustomer = new JsonBusinessCustomer().
                                    setQueueUserId(jsonProfileList.get(sp_patient_list.getSelectedItemPosition()).getQueueUserId());
                            jsonBusinessCustomer
                                    .setCodeQR(getIntent().getStringExtra(IBConstant.KEY_CODE_QR))
                                    .setCustomerPhone(phoneNoWithCode)
                                    .setBusinessCustomerId(cid);
                            String[] temp = appointmentSlotAdapter.getDataSet().get(selectedPos).getTimeSlot().split("-");
                            JsonSchedule jsonSchedule = new JsonSchedule()
                                    .setCodeQR(codeQR)
                                    .setStartTime(start)
                                    .setEndTime(end)
                                    .setChiefComplain(actv_chief_complaints.getText().toString())
                                    .setScheduleDate(AppUtils.dateFormatAsYYYY_MM_DD(selectedDate)).
                                            setQueueUserId(jsonProfileList.get(sp_patient_list.getSelectedItemPosition()).getQueueUserId());
                            BookSchedule bookSchedule = new BookSchedule()
                                    .setBusinessCustomer(jsonBusinessCustomer)
                                    .setJsonSchedule(jsonSchedule)
                                    .setBookActionType(ActionTypeEnum.ADD);
                            scheduleApiCalls.bookSchedule(BaseLaunchActivity.getDeviceID(),
                                    LaunchActivity.getLaunchActivity().getEmail(),
                                    LaunchActivity.getLaunchActivity().getAuth(),
                                    bookSchedule);
                        } else {
                            ShowAlertInformation.showNetworkDialog(BookAppointmentActivity.this);
                        }
                    } else {
                        new CustomToast().showToast(BookAppointmentActivity.this, "Booking start time to be less than end time");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @Override
    public void userFound(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        findCustomerResponse(jsonProfile);
    }

    private long calculateAppointmentSlot(String strFromTime, String strToTime) {
        try {
            int fromHour, fromMinute, toHour, toMinute;
            fromHour = Integer.parseInt(strFromTime.split(":")[0]);
            fromMinute = Integer.parseInt(strFromTime.split(":")[1]);

            toHour = Integer.parseInt(strToTime.split(":")[0]);
            toMinute = Integer.parseInt(strToTime.split(":")[1]);


            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, fromHour);
            calendar2.set(Calendar.MINUTE, fromMinute);

            long startTime = calendar2.getTimeInMillis();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, toHour);
            calendar1.set(Calendar.MINUTE, toMinute);
            long endTime = calendar1.getTimeInMillis();
            long duration = endTime - startTime;
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            Log.e("Booked Slots in min : ", String.valueOf(diffInMinutes));
            return diffInMinutes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private String getEndTime(int slotMinute, String strFromTime) {
        try {
            int fromHour, fromMinute;
            fromHour = Integer.parseInt(strFromTime.split(":")[0]);
            fromMinute = Integer.parseInt(strFromTime.split(":")[1]);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, fromHour);
            calendar2.set(Calendar.MINUTE, fromMinute + slotMinute);
            long currentTime = calendar2.getTimeInMillis();

            DateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String str = sdfTime.format(new Date(currentTime));
            Log.e("Booked Slots end time :", str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}