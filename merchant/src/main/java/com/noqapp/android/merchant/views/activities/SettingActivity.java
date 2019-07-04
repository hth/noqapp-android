package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.StoreSettingApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.StoreSetting;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.utils.ViewAnimationUtils;
import com.noqapp.android.merchant.views.interfaces.StoreSettingPresenter;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class SettingActivity extends BaseActivity implements StoreSettingPresenter, View.OnClickListener {
    protected ImageView actionbarBack, iv_delete_scheduling;
    private String codeQR;
    private TextView tv_store_close, tv_store_start, tv_token_available, tv_token_not_available, tv_limited_label, tv_delay_in_minute;
    private TextView tv_scheduling_from, tv_scheduling_ending, tv_scheduling_status;
    private CheckBox cb_limit, cb_enable_payment, cb_enable_appointment;
    private EditText edt_token_no;
    private boolean arrivalTextChange = false;
    private StoreSettingApiCalls storeSettingApiCalls;
    private StoreSetting storeSettingTemp;
    private String YES = "Yes";
    private String NO = "No";
    private EditText edt_deduction_amount, edt_fees;
    private EditText edt_follow_up_in_days, edt_discounted_followup_price, edt_limited_followup_days;
    private EditText edt_appointment_accepting_week, edt_appointment_duration;


    private SegmentedControl sc_prevent_join, sc_today_closed, sc_day_closed, sc_store_offline;

    private List<String> yes_no_list = new ArrayList<>();
    private LinearLayout ll_payment, ll_follow_up;
    private TextView tv_fee_after_discounted_followup;
    private CardView cv_payment,cv_appointment;
    private boolean isFollowUpAllow = false;
    private ImageView iv_today_settings, iv_token_timing, iv_store_closing, iv_permanent_setting, iv_payment_setting, iv_appointment_setting;
    private LinearLayout ll_today_settings, ll_token_timing, ll_store_closing, ll_permanent_setting, ll_payment_setting, ll_appointment_setting;
    boolean is_today_settings_expand = true;
    boolean is_token_timing = true;
    boolean is_store_closing = true;
    boolean is_permanent_setting = true;
    boolean is_payment_setting = true;
    boolean is_appointment_setting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        storeSettingApiCalls = new StoreSettingApiCalls(this);
        setContentView(R.layout.activity_setting);

        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        iv_delete_scheduling = findViewById(R.id.iv_delete_scheduling);
        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.setScrollBarFadeDuration(0);
        scroll_view.setScrollbarFadingEnabled(false);
        setProgressMessage("Loading Queue Settings...");

        if (!new AppUtils().isTablet(getApplicationContext())) {

            iv_today_settings = findViewById(R.id.iv_today_settings);
            ll_today_settings = findViewById(R.id.ll_today_settings);

            iv_token_timing = findViewById(R.id.iv_token_timing);
            ll_token_timing = findViewById(R.id.ll_token_timing);

            iv_store_closing = findViewById(R.id.iv_store_closing);
            ll_store_closing = findViewById(R.id.ll_store_closing);

            iv_permanent_setting = findViewById(R.id.iv_permanent_setting);
            ll_permanent_setting = findViewById(R.id.ll_permanent_setting);

            iv_payment_setting = findViewById(R.id.iv_payment_setting);
            ll_payment_setting = findViewById(R.id.ll_payment_setting);

            iv_appointment_setting = findViewById(R.id.iv_appointment_setting);
            ll_appointment_setting = findViewById(R.id.ll_appointment_setting);


            iv_today_settings.setOnClickListener(this);
            iv_token_timing.setOnClickListener(this);
            iv_store_closing.setOnClickListener(this);
            iv_permanent_setting.setOnClickListener(this);
            iv_payment_setting.setOnClickListener(this);
            iv_appointment_setting.setOnClickListener(this);

            iv_today_settings.performClick();
            iv_token_timing.performClick();
            iv_store_closing.performClick();
            iv_permanent_setting.performClick();
            iv_payment_setting.performClick();
            iv_appointment_setting.performClick();
        }


        sc_day_closed = findViewById(R.id.sc_day_closed);
        sc_today_closed = findViewById(R.id.sc_today_closed);
        sc_store_offline = findViewById(R.id.sc_store_offline);
        sc_prevent_join = findViewById(R.id.sc_prevent_join);

        View view_prevent_click = findViewById(R.id.view_prevent_click);
        TextView tv_no_permission = findViewById(R.id.tv_no_permission);

        edt_deduction_amount = findViewById(R.id.edt_deduction_amount);
        edt_fees = findViewById(R.id.edt_fees);
        tv_fee_after_discounted_followup = findViewById(R.id.tv_fee_after_discounted_followup);
        edt_follow_up_in_days = findViewById(R.id.edt_follow_up_in_days);
        edt_discounted_followup_price = findViewById(R.id.edt_discounted_followup_price);
        edt_limited_followup_days = findViewById(R.id.edt_limited_followup_days);

        edt_appointment_accepting_week = findViewById(R.id.edt_appointment_accepting_week);
        edt_appointment_duration = findViewById(R.id.edt_appointment_duration);
        ll_payment = findViewById(R.id.ll_payment);
        ll_follow_up = findViewById(R.id.ll_follow_up);
        cv_payment = findViewById(R.id.cv_payment);
        cv_appointment = findViewById(R.id.cv_appointment);
        codeQR = getIntent().getStringExtra("codeQR");

        if (null != LaunchActivity.getLaunchActivity().getUserProfile() && BusinessTypeEnum.DO ==
                BaseLaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()) {
            ll_follow_up.setVisibility(View.VISIBLE);
            cv_payment.setVisibility(View.VISIBLE);
            isFollowUpAllow = true;
        } else {
            ll_follow_up.setVisibility(View.GONE);
            cv_payment.setVisibility(View.GONE);
            isFollowUpAllow = false;
        }

        if (null != LaunchActivity.getLaunchActivity().getUserProfile() &&( BusinessTypeEnum.DO ==
                BaseLaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()||BusinessTypeEnum.HS ==
                BaseLaunchActivity.getLaunchActivity().getUserProfile().getBusinessType())) {
            cv_appointment.setVisibility(View.VISIBLE);
        } else {
            cv_appointment.setVisibility(View.GONE);
        }

        yes_no_list.clear();
        yes_no_list.add(YES);
        yes_no_list.add(NO);
        sc_prevent_join.addSegments(yes_no_list);
        sc_today_closed.addSegments(yes_no_list);
        sc_day_closed.addSegments(yes_no_list);
        sc_store_offline.addSegments(yes_no_list);

        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_delete_scheduling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {

                    ShowCustomDialog showDialog = new ShowCustomDialog(SettingActivity.this);
                    showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                        @Override
                        public void btnPositiveClick() {
                            showProgress();
                            storeSettingApiCalls.removeSchedule(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                        }

                        @Override
                        public void btnNegativeClick() {
                            //Do nothing
                        }
                    });
                    showDialog.displayDialog("Remove Schedule", "Do you want to remove schedule?");
                } else {
                    ShowAlertInformation.showNetworkDialog(SettingActivity.this);
                }
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_settings));

        cb_enable_appointment = findViewById(R.id.cb_enable_appointment);
        cb_enable_payment = findViewById(R.id.cb_enable_payment);
        cb_enable_payment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_payment.setVisibility(View.VISIBLE);
                } else {
                    ll_payment.setVisibility(View.GONE);
                }
            }
        });
        cb_limit = findViewById(R.id.cb_limit);
        cb_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edt_token_no.setVisibility(View.INVISIBLE);
                    tv_limited_label.setText(getString(R.string.unlimited_token));
                    new AppUtils().hideKeyBoard(SettingActivity.this);
                } else {
                    edt_token_no.setVisibility(View.VISIBLE);
                    // edt_token_no.setText("1");
                    tv_limited_label.setText(getString(R.string.limited_token));
                }
            }
        });
        edt_token_no = findViewById(R.id.edt_token_no);
        edt_token_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /* Write your logic here that will be executed when user taps next button */
                    if (!edt_token_no.getText().toString().equals("")) {
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            showProgress();
                            updateQueueSettings();
                        } else {
                            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
                        }
                    } else {
                        new CustomToast().showToast(SettingActivity.this, "Empty field is not allowed. For Un-Limited Tokens set value to '0'");
                    }
                }
                return false;
            }
        });
        tv_token_available = findViewById(R.id.tv_token_available);
        tv_store_start = findViewById(R.id.tv_store_start);
        tv_token_not_available = findViewById(R.id.tv_token_not_available);
        tv_store_close = findViewById(R.id.tv_store_close);
        tv_token_available.setOnClickListener(new TextViewClick(tv_token_available));
        tv_store_start.setOnClickListener(new TextViewClick(tv_store_start));
        tv_token_not_available.setOnClickListener(new TextViewClick(tv_token_not_available));
        tv_store_close.setOnClickListener(new TextViewClick(tv_store_close));
        tv_limited_label = findViewById(R.id.tv_limited_label);
        tv_delay_in_minute = findViewById(R.id.tv_delay_in_minute);
        TextView tv_close_day_of_week = findViewById(R.id.tv_close_day_of_week);
        tv_scheduling_from = findViewById(R.id.tv_scheduleing_from);
        tv_scheduling_ending = findViewById(R.id.tv_scheduleing_ending);
        tv_scheduling_status = findViewById(R.id.tv_scheduling_status);
        tv_scheduling_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(tv_scheduling_from);
            }
        });
        tv_scheduling_ending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(tv_scheduling_ending);
            }
        });
        String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        tv_close_day_of_week.setText(getResources().getString(R.string.dayclosed, dayLongName));
        tv_delay_in_minute.setOnClickListener(new TextViewClickDelay(tv_delay_in_minute));

        if (!isSpecificSettingEditAllowed()) {
            //disable the fields for unauthorized user
            tv_store_start.setEnabled(false);
            tv_store_close.setEnabled(false);
            tv_token_available.setEnabled(false);
            tv_token_not_available.setEnabled(false);
        }


        Button btn_update_deduction = findViewById(R.id.btn_update_deduction);
        Button btn_update_time = findViewById(R.id.btn_update_time);
        Button btn_update_delay = findViewById(R.id.btn_update_delay);
        Button btn_update_scheduling = findViewById(R.id.btn_update_scheduling);
        Button btn_update_appointment = findViewById(R.id.btn_update_appointment);
        Button btn_update_permanent_setting = findViewById(R.id.btn_update_permanent_setting);
        btn_update_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_enable_appointment.isChecked()) {
                    if (validateAppointmentSetting()) {
                        updateAppointmentSettings();
                    }
                } else {
                    edt_appointment_accepting_week.setText("0");
                    edt_appointment_duration.setText("0");
                    updateAppointmentSettings();
                }

            }
        });

        btn_update_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSpecificSettingEditAllowed()) {
                    if (isEndTimeBeforeStartTime(tv_store_start, tv_store_close)) {
                        ShowAlertInformation.showThemeDialog(SettingActivity.this, "Alert", "'Queue start time' should be before 'Queue close time'.");
                    } else if (isEndTimeBeforeStartTime(tv_token_available, tv_token_not_available)) {
                        ShowAlertInformation.showThemeDialog(SettingActivity.this, "Alert", "'Issue token from' should be before 'Stop issuing token after'.");
                    } else if (isEndTimeBeforeStartTime(tv_token_not_available, tv_store_close)) {
                        ShowAlertInformation.showThemeDialog(SettingActivity.this, "Alert", "'Stop issuing token after' should be before 'Queue close time'.");
                    } else {
                        callUpdate();
                    }
                } else {
                    ShowAlertInformation.showThemeDialog(SettingActivity.this, "Permission denied", "You don't have permission to change this settings");
                }
            }
        });
        btn_update_scheduling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(tv_scheduling_from.getText().toString()) || TextUtils.isEmpty(tv_scheduling_ending.getText().toString())) {
                    new CustomToast().showToast(SettingActivity.this, "Both scheduling dates are required");
                } else if (isEndDateNotAfterStartDate()) {
                    new CustomToast().showToast(SettingActivity.this, "Until Date should be after From Date");
                } else {
                    if (isSpecificSettingEditAllowed()) {
                        callUpdate();
                    } else {
                        ShowAlertInformation.showThemeDialog(SettingActivity.this, "Permission denied", "You don't have permission to change this settings");
                    }
                }
            }
        });

        btn_update_delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUpdate();
            }
        });
        btn_update_permanent_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUpdate();
            }
        });

        if ((LaunchActivity.getLaunchActivity().getUserLevel() != UserLevelEnum.S_MANAGER)) {
            view_prevent_click.setVisibility(View.VISIBLE);
            tv_no_permission.setVisibility(View.VISIBLE);
            btn_update_permanent_setting.setVisibility(View.GONE);
        } else {
            view_prevent_click.setVisibility(View.GONE);
            tv_no_permission.setVisibility(View.GONE);
            btn_update_permanent_setting.setVisibility(View.VISIBLE);
        }

        edt_discounted_followup_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    updateDiscountLabel();
                } else {
                    tv_fee_after_discounted_followup.setText("");
                    tv_fee_after_discounted_followup.setVisibility(View.GONE);
                }

            }
        });
        btn_update_deduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_enable_payment.isChecked()) {
                    if (TextUtils.isEmpty(edt_fees.getText().toString()) || Integer.parseInt(edt_fees.getText().toString()) == 0) {
                        new CustomToast().showToast(SettingActivity.this, "Service Charge cannot be zero");
                    } else {
                        if (!TextUtils.isEmpty(edt_deduction_amount.getText().toString()) && TextUtils.isEmpty(edt_fees.getText().toString())) {
                            new CustomToast().showToast(SettingActivity.this, "Cancellation Charge is not allowed without Service Charge");
                        } else {
                            if (!TextUtils.isEmpty(edt_deduction_amount.getText().toString()) && !TextUtils.isEmpty(edt_fees.getText().toString())) {
                                if (Integer.parseInt(edt_deduction_amount.getText().toString()) <= Integer.parseInt(edt_fees.getText().toString())) {
                                    if (isFollowUpAllow) {
                                        if (TextUtils.isEmpty(edt_discounted_followup_price.getText().toString())) {
                                            if (!TextUtils.isEmpty(edt_follow_up_in_days.getText().toString())) {
                                                if (Integer.parseInt(edt_follow_up_in_days.getText().toString()) < (Integer.parseInt(edt_limited_followup_days.getText().toString()))) {
                                                    updatePaymentSettings();
                                                } else {
                                                    new CustomToast().showToast(SettingActivity.this, "Limited follow-up days cannot be less than free follow-up days");
                                                }
                                            } else {
                                                updatePaymentSettings();
                                            }
                                        } else {
                                            if (Integer.parseInt(edt_discounted_followup_price.getText().toString()) >= 0 && (Integer.parseInt(edt_discounted_followup_price.getText().toString()) <= Integer.parseInt(edt_fees.getText().toString()))) {
                                                if (!TextUtils.isEmpty(edt_follow_up_in_days.getText().toString())) {
                                                    if (Integer.parseInt(edt_follow_up_in_days.getText().toString()) < (Integer.parseInt(edt_limited_followup_days.getText().toString()))) {
                                                        updatePaymentSettings();
                                                    } else {
                                                        new CustomToast().showToast(SettingActivity.this, "Limited follow-up days cannot be less than free follow up days");
                                                    }
                                                } else {
                                                    updatePaymentSettings();
                                                }
                                            } else {
                                                new CustomToast().showToast(SettingActivity.this, "Discounted follow-up price cannot be greater than Service Charge");
                                            }
                                        }
                                    } else {
                                        updatePaymentSettings();
                                    }
                                } else {
                                    new CustomToast().showToast(SettingActivity.this, "Cancellation charge cannot be greater than Service Charge");
                                }
                            } else {
                                updatePaymentSettings();
                            }
                        }
                    }
                } else {
                    edt_fees.setText("0");
                    edt_deduction_amount.setText("0");
                    edt_discounted_followup_price.setText("0");
                    edt_follow_up_in_days.setText("0");
                    edt_limited_followup_days.setText("0");
                    updatePaymentSettings();
                }
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            storeSettingApiCalls.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }
    }


    private boolean validateAppointmentSetting() {
        boolean isValid = true;
        if (TextUtils.isEmpty(edt_appointment_duration.getText().toString())) {
            new CustomToast().showToast(SettingActivity.this, "Appointment duration cannot be empty or 0");
            isValid = false;
        } else {
            if (Integer.parseInt(edt_appointment_duration.getText().toString()) > 60 || Integer.parseInt(edt_appointment_duration.getText().toString()) < 10) {
                new CustomToast().showToast(SettingActivity.this, "Appointment duration should be greater than 10 & less than 60");
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(edt_appointment_accepting_week.getText().toString())) {
            new CustomToast().showToast(SettingActivity.this, "Appointment week cannot be empty or 0");
            isValid = false;
        } else {
            if (Integer.parseInt(edt_appointment_accepting_week.getText().toString()) > 52 || Integer.parseInt(edt_appointment_accepting_week.getText().toString()) < 1) {
                new CustomToast().showToast(SettingActivity.this, "Appointment week should be greater than 0 & less than or equal to 52 weeks");
                isValid = false;
            }
        }
        return isValid;
    }


    private void updateDiscountLabel() {
        if (!TextUtils.isEmpty(edt_fees.getText().toString())) {
            tv_fee_after_discounted_followup.setVisibility(View.VISIBLE);
            try {
                tv_fee_after_discounted_followup.setText("Service charge for limited follow-up until " +
                        edt_limited_followup_days.getText().toString() + " days is " +
                        (Integer.parseInt(edt_fees.getText().toString()) - Integer.parseInt(edt_discounted_followup_price.getText().toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tv_fee_after_discounted_followup.setText("");
            tv_fee_after_discounted_followup.setVisibility(View.GONE);
        }
    }

    private boolean isEndDateNotAfterStartDate() {
        try {
            Date date1 = CommonHelper.SDF_YYYY_MM_DD.parse(tv_scheduling_from.getText().toString());
            Date date2 = CommonHelper.SDF_YYYY_MM_DD.parse(tv_scheduling_ending.getText().toString());
            return date1.after(date2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        if (null != BaseLaunchActivity.merchantListFragment) {
            BaseLaunchActivity.merchantListFragment.onRefresh();
        }
    }


    @Override
    public void queueSettingResponse(StoreSetting storeSetting) {
        if (null != storeSetting) {
            storeSettingTemp = storeSetting;
            sc_day_closed.setSelectedSegment(storeSetting.isDayClosed() ? 0 : 1);
            sc_prevent_join.setSelectedSegment(storeSetting.isPreventJoining() ? 0 : 1);
            sc_today_closed.setSelectedSegment(storeSetting.isTempDayClosed() ? 0 : 1);
            sc_store_offline.setSelectedSegment(storeSetting.getStoreActionType() == ActionTypeEnum.INACTIVE ? 0 : 1);
            cb_enable_payment.setChecked(storeSetting.isEnabledPayment());

            cb_enable_appointment.setChecked(storeSetting.isAppointmentEnable());
            ll_payment.setVisibility(storeSetting.isEnabledPayment() ? View.VISIBLE : View.GONE);
            tv_token_available.setText(Formatter.convertMilitaryTo24HourFormat(storeSetting.getTokenAvailableFrom()));
            tv_store_start.setText(Formatter.convertMilitaryTo24HourFormat(storeSetting.getStartHour()));
            tv_token_not_available.setText(Formatter.convertMilitaryTo24HourFormat(storeSetting.getTokenNotAvailableFrom()));
            tv_store_close.setText(Formatter.convertMilitaryTo24HourFormat(storeSetting.getEndHour()));
            LocalTime localTime = Formatter.parseLocalTime(String.format(Locale.US, "%04d", storeSetting.getStartHour()));
            localTime = localTime.plusMinutes(storeSetting.getDelayedInMinutes());
            tv_delay_in_minute.setText(Formatter.convertMilitaryTo24HourFormat(localTime));
            if (TextUtils.isEmpty(storeSetting.getScheduledFromDay())) {
                tv_scheduling_status.setText("");
                tv_scheduling_status.setVisibility(View.GONE);
                iv_delete_scheduling.setVisibility(View.GONE);
            } else {
                tv_scheduling_status.setText("Scheduled to close from " + storeSetting.getScheduledFromDay() + " to " + storeSetting.getScheduledUntilDay());
                tv_scheduling_status.setVisibility(View.VISIBLE);
                iv_delete_scheduling.setVisibility(View.VISIBLE);
            }

            if (storeSetting.getAvailableTokenCount() <= 0) {
                cb_limit.setChecked(true);
                tv_limited_label.setText(getString(R.string.unlimited_token));
                edt_token_no.setVisibility(View.INVISIBLE);
            } else {
                cb_limit.setChecked(false);
                edt_token_no.setText(String.valueOf(storeSetting.getAvailableTokenCount()));
                edt_token_no.setVisibility(View.VISIBLE);
                tv_limited_label.setText(getString(R.string.limited_token));
                if (edt_token_no != null) {
                    edt_token_no.clearFocus();
                    new AppUtils().hideKeyBoard(this);
                }
            }
            edt_appointment_duration.setText(String.valueOf(storeSetting.getAppointmentDuration()));
            edt_appointment_accepting_week.setText(String.valueOf(storeSetting.getAppointmentOpenHowFar()));

            if (isFollowUpAllow) {
                edt_deduction_amount.setText(String.valueOf(storeSetting.getCancellationPrice() / 100));
                edt_fees.setText(String.valueOf(storeSetting.getProductPrice() / 100));

                edt_discounted_followup_price.setText(String.valueOf(storeSetting.getDiscountedFollowupProductPrice() / 100));
                edt_follow_up_in_days.setText(String.valueOf(storeSetting.getFreeFollowupDays()));
                edt_limited_followup_days.setText(String.valueOf(storeSetting.getDiscountedFollowupDays()));
                updateDiscountLabel();
            }
        }
        dismissProgress();
    }

    @Override
    public void queueSettingModifyResponse(StoreSetting storeSetting) {
        new CustomToast().showToast(this, "Settings updated successfully!!!");
        queueSettingResponse(storeSetting);

    }

    @Override
    public void queueSettingError() {
        dismissProgress();
        // to make sure the data is not changed in case of error
        if (null != storeSettingTemp) {
            queueSettingResponse(storeSettingTemp);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != storeSettingTemp) {
            queueSettingResponse(storeSettingTemp);
        }
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
        Intent intent = new Intent();
        intent.putExtra(Constants.CLEAR_DATA, true);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        finish();

    }

    private void expandCollapseViewWithArrow(boolean isExpand, LinearLayout linearLayout, ImageView imageView) {
        if (isExpand) {
            ViewAnimationUtils.expand(linearLayout);
            imageView.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.arrow_up));
        } else {
            ViewAnimationUtils.collapse(linearLayout);
            imageView.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.arrow_down));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_today_settings: {
                is_today_settings_expand = !is_today_settings_expand;
                expandCollapseViewWithArrow(is_today_settings_expand, ll_today_settings, iv_today_settings);
            }
            break;
            case R.id.iv_token_timing: {
                is_token_timing = !is_token_timing;
                expandCollapseViewWithArrow(is_token_timing, ll_token_timing, iv_token_timing);
            }
            break;
            case R.id.iv_store_closing: {
                is_store_closing = !is_store_closing;
                expandCollapseViewWithArrow(is_store_closing, ll_store_closing, iv_store_closing);
            }
            break;
            case R.id.iv_permanent_setting: {
                is_permanent_setting = !is_permanent_setting;
                expandCollapseViewWithArrow(is_permanent_setting, ll_permanent_setting, iv_permanent_setting);
            }
            break;
            case R.id.iv_payment_setting: {
                is_payment_setting = !is_payment_setting;
                expandCollapseViewWithArrow(is_payment_setting, ll_payment_setting, iv_payment_setting);
            }
            break;
            case R.id.iv_appointment_setting: {
                is_appointment_setting = !is_appointment_setting;
                expandCollapseViewWithArrow(is_appointment_setting, ll_appointment_setting, iv_appointment_setting);
            }
            break;
            default:
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showProgress();
                    updateQueueSettings();
                } else {
                    ShowAlertInformation.showNetworkDialog(SettingActivity.this);
                }
        }
    }

    private void updateQueueSettings() {
        setProgressMessage("Updating Queue Settings...");
        StoreSetting storeSetting = new StoreSetting();
        storeSetting.setCodeQR(codeQR);
        storeSetting.setDayClosed(sc_day_closed.getSelectedAbsolutePosition() == 0 ? true : false);
        storeSetting.setPreventJoining(sc_prevent_join.getSelectedAbsolutePosition() == 0 ? true : false);
        storeSetting.setTempDayClosed(sc_today_closed.getSelectedAbsolutePosition() == 0 ? true : false);
        storeSetting.setStoreActionType(sc_store_offline.getSelectedAbsolutePosition() == 0 ? ActionTypeEnum.INACTIVE : ActionTypeEnum.ACTIVE);
        storeSetting.setEnabledPayment(cb_enable_payment.isChecked());
        if (StringUtils.isNotBlank(tv_token_available.getText().toString())) {
            storeSetting.setTokenAvailableFrom(Integer.parseInt(tv_token_available.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_store_start.getText().toString())) {
            storeSetting.setStartHour(Integer.parseInt(tv_store_start.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_token_not_available.getText().toString())) {
            storeSetting.setTokenNotAvailableFrom(Integer.parseInt(tv_token_not_available.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_store_close.getText().toString())) {
            storeSetting.setEndHour(Integer.parseInt(tv_store_close.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_scheduling_from.getText().toString())) {
            storeSetting.setFromDay(tv_scheduling_from.getText().toString());
        }

        if (StringUtils.isNotBlank(tv_scheduling_ending.getText().toString())) {
            storeSetting.setUntilDay(tv_scheduling_ending.getText().toString());
        }

        if (StringUtils.isBlank(edt_token_no.getText().toString())) {
            storeSetting.setAvailableTokenCount(0);
        } else {
            storeSetting.setAvailableTokenCount(Integer.parseInt(edt_token_no.getText().toString()));
        }

        if (arrivalTextChange) {
            DateTime start = DateTime.now().withTimeAtStartOfDay();
            DateTime delay = DateTime.now().withTimeAtStartOfDay();

            int delayHour = Integer.valueOf(tv_delay_in_minute.getText().toString().split(":")[0]);
            int delayMinutes = Integer.valueOf(tv_delay_in_minute.getText().toString().split(":")[1]);
            int startHour = Integer.valueOf(tv_store_start.getText().toString().split(":")[0]);
            int startMinutes = Integer.valueOf(tv_store_start.getText().toString().split(":")[1]);

            Duration duration = new Duration(delay.plusHours(startHour).plusMinutes(startMinutes), start.plusHours(delayHour).plusMinutes(delayMinutes));
            storeSetting.setDelayedInMinutes((int) duration.getStandardMinutes());
        } else {
            storeSetting.setDelayedInMinutes(0);
        }
        storeSettingApiCalls.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), storeSetting);
    }


    private void updatePaymentSettings() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Updating payment settings...");
            showProgress();
            StoreSetting storeSetting = SerializationUtils.clone(this.storeSettingTemp);
            if (TextUtils.isEmpty(edt_deduction_amount.getText().toString())) {
                storeSetting.setCancellationPrice(0);
            } else {
                storeSetting.setCancellationPrice(Integer.parseInt(edt_deduction_amount.getText().toString()) * 100);
            }
            if (TextUtils.isEmpty(edt_fees.getText().toString())) {
                storeSetting.setProductPrice(0);
            } else {
                storeSetting.setProductPrice(Integer.parseInt(edt_fees.getText().toString()) * 100);
            }

            if (TextUtils.isEmpty(edt_follow_up_in_days.getText().toString())) {
                storeSetting.setFreeFollowupDays(0);
            } else {
                storeSetting.setFreeFollowupDays(Integer.parseInt(edt_follow_up_in_days.getText().toString()));
            }
            if (TextUtils.isEmpty(edt_limited_followup_days.getText().toString())) {
                storeSetting.setDiscountedFollowupDays(0);
            } else {
                storeSetting.setDiscountedFollowupDays(Integer.parseInt(edt_limited_followup_days.getText().toString()));
            }
            if (TextUtils.isEmpty(edt_discounted_followup_price.getText().toString())) {
                storeSetting.setDiscountedFollowupProductPrice(0);
            } else {
                storeSetting.setDiscountedFollowupProductPrice(Integer.parseInt(edt_discounted_followup_price.getText().toString()) * 100);
            }
            storeSetting.setEnabledPayment(cb_enable_payment.isChecked());
            storeSettingApiCalls.serviceCost(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), storeSetting);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }
    }

    private void updateAppointmentSettings() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            setProgressMessage("Updating appointment settings...");
            showProgress();
            StoreSetting storeSetting = SerializationUtils.clone(this.storeSettingTemp);
            if (TextUtils.isEmpty(edt_appointment_duration.getText().toString())) {
                storeSetting.setAppointmentDuration(0);
            } else {
                storeSetting.setAppointmentDuration(Integer.parseInt(edt_appointment_duration.getText().toString()));
            }
            if (TextUtils.isEmpty(edt_appointment_accepting_week.getText().toString())) {
                storeSetting.setAppointmentOpenHowFar(0);
            } else {
                storeSetting.setAppointmentOpenHowFar(Integer.parseInt(edt_appointment_accepting_week.getText().toString()));
            }

            storeSetting.setAppointmentEnable(cb_enable_appointment.isChecked());
            storeSettingApiCalls.appointment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), storeSetting);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }
    }

    private class TextViewClick implements View.OnClickListener {
        private TextView textView;

        private TextViewClick(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onClick(View view) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SettingActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if (selectedHour == 0 && selectedMinute == 0) {
                        new CustomToast().showToast(SettingActivity.this, getString(R.string.error_time));
                    } else {
                        textView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }
            }, hour, minute, false);//Yes 24 hour time
            //mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    private class TextViewClickDelay implements View.OnClickListener {
        private TextView textView;

        private TextViewClickDelay(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onClick(View view) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(SettingActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if (selectedHour == 0 && selectedMinute == 0) {
                        new CustomToast().showToast(SettingActivity.this, getString(R.string.error_time));
                    } else {
                        LocalTime startTime = Formatter.parseLocalTime(tv_store_start.getText().toString().replace(":", ""));
                        LocalTime closeTime = Formatter.parseLocalTime(tv_store_close.getText().toString().replace(":", ""));
                        LocalTime arrivalTime = Formatter.parseLocalTime(String.format("%02d%02d", selectedHour, selectedMinute));
                        if (arrivalTime.isBefore(startTime)) {
                            new CustomToast().showToast(SettingActivity.this, getString(R.string.error_delay_time));
                        } else if (closeTime.isBefore(arrivalTime)) {
                            new CustomToast().showToast(SettingActivity.this, getString(R.string.error_delay_time));
                        } else {
                            textView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                            arrivalTextChange = true;
                        }
                    }
                }
            }, hour, minute, false);//No 24 hour time
            //mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    private void callUpdate() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            updateQueueSettings();
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }
    }

    private void openDatePicker(final TextView tv) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    tv.setText(CommonHelper.SDF_YYYY_MM_DD.format(newDate.getTime()));
                } else {
                    new CustomToast().showToast(SettingActivity.this, getString(R.string.error_past_date));
                    tv.setText("");
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean isEndTimeBeforeStartTime(TextView tv_start_time, TextView tv_end_time) {
        LocalTime startTime = Formatter.parseLocalTime(tv_start_time.getText().toString().replace(":", ""));
        LocalTime endTime = Formatter.parseLocalTime(tv_end_time.getText().toString().replace(":", ""));
        return endTime.isBefore(startTime);
    }

    private boolean isSpecificSettingEditAllowed() {
        return (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) || (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.Q_SUPERVISOR);
    }
}
