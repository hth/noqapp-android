package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.ServicePaymentEnum;
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
import com.noqapp.android.merchant.views.interfaces.StoreSettingPresenter;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements StoreSettingPresenter, View.OnClickListener {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack, iv_delete_scheduling;
    private SwitchCompat toggleDayClosed, togglePreventJoin, toggleTodayClosed, toggleStoreOffline;
    private String codeQR;
    private TextView tv_store_close, tv_store_start, tv_token_available, tv_token_not_available, tv_limited_label, tv_delay_in_minute;
    private TextView tv_scheduling_from, tv_scheduling_ending, tv_scheduling_status;
    private CheckBox cb_limit, cb_enable_payment;
    private EditText edt_token_no;
    private boolean arrivalTextChange = false;
    private StoreSettingApiCalls storeSettingApiCalls;
    private StoreSetting storeSettingTemp;
    private TextView togglePreventJoinLabel, toggleTodayClosedLabel, toggleDayClosedLabel, toggleStoreOfflineLabel;
    private String YES = "Yes";
    private String NO = "No";
    private EditText edt_deduction_amount, edt_fees;
    private EditText edt_follow_up_in_days, edt_discounted_followup_price, edt_limited_followup_days;
    private SegmentedControl sc_paid_user;
    private List<String> pay_list = new ArrayList<>();
    private ServicePaymentEnum servicePaymentEnum;
    private LinearLayout ll_payment;
    private TextView tv_fee_after_discounted_followup;

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
        initProgress();

        toggleDayClosed = findViewById(R.id.toggleDayClosed);
        toggleTodayClosed = findViewById(R.id.toggleTodayClosed);
        togglePreventJoin = findViewById(R.id.togglePreventJoin);
        toggleStoreOffline = findViewById(R.id.toggleStoreOffline);
        sc_paid_user = findViewById(R.id.sc_paid_user);
        togglePreventJoinLabel = findViewById(R.id.togglePreventJoinLabel);
        toggleTodayClosedLabel = findViewById(R.id.toggleTodayClosedLabel);
        toggleDayClosedLabel = findViewById(R.id.toggleDayClosedLabel);
        toggleStoreOfflineLabel = findViewById(R.id.toggleStoreOfflineLabel);
        edt_deduction_amount = findViewById(R.id.edt_deduction_amount);
        edt_fees = findViewById(R.id.edt_fees);
        tv_fee_after_discounted_followup = findViewById(R.id.tv_fee_after_discounted_followup);
        edt_follow_up_in_days = findViewById(R.id.edt_follow_up_in_days);
        edt_discounted_followup_price = findViewById(R.id.edt_discounted_followup_price);
        edt_limited_followup_days = findViewById(R.id.edt_limited_followup_days);
        ll_payment = findViewById(R.id.ll_payment);
        codeQR = getIntent().getStringExtra("codeQR");
        toggleDayClosed.setOnClickListener(this);
        toggleTodayClosed.setOnClickListener(this);
        togglePreventJoin.setOnClickListener(this);
        toggleStoreOffline.setOnClickListener(this);

        pay_list.clear();
        pay_list.addAll(ServicePaymentEnum.asListOfDescription());
        sc_paid_user.addSegments(pay_list);
        sc_paid_user.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    try {
                        int selection = segmentViewHolder.getAbsolutePosition();
                        servicePaymentEnum = ServicePaymentEnum.getEnum(pay_list.get(selection));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
                            progressDialog.show();
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
                            progressDialog.show();
                            updateQueueSettings();
                        } else {
                            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, "Empty field is not allowed. For Un-Limited Tokens set value to '0'", Toast.LENGTH_LONG).show();
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

        if ((LaunchActivity.getLaunchActivity().getUserLevel() != UserLevelEnum.S_MANAGER)) {
            toggleDayClosed.setClickable(false);
            toggleDayClosed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ShowAlertInformation.showThemeDialog(SettingActivity.this, "Permission denied", "You don't have permission to change this settings");
                    return false;
                }
            });
            toggleStoreOffline.setClickable(false);
            toggleStoreOffline.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ShowAlertInformation.showThemeDialog(SettingActivity.this, "Permission denied", "You don't have permission to change this settings");
                    return false;
                }
            });
        }

        Button btn_update_deduction = findViewById(R.id.btn_update_deduction);
        Button btn_update_time = findViewById(R.id.btn_update_time);
        Button btn_update_delay = findViewById(R.id.btn_update_delay);
        Button btn_update_scheduling = findViewById(R.id.btn_update_scheduling);
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
                    Toast.makeText(SettingActivity.this, "Both scheduling dates are required", Toast.LENGTH_LONG).show();
                } else if (isEndDateNotAfterStartDate()) {
                    Toast.makeText(SettingActivity.this, "Until Date should be after From Date", Toast.LENGTH_LONG).show();
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

        edt_discounted_followup_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    if (!TextUtils.isEmpty(edt_fees.getText().toString())) {
                        tv_fee_after_discounted_followup.setVisibility(View.VISIBLE);
                        try {
                            tv_fee_after_discounted_followup.setText("Your Service Charges in followup will be " + (Integer.parseInt(edt_fees.getText().toString()) - Integer.parseInt(edt_discounted_followup_price.getText().toString())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        tv_fee_after_discounted_followup.setText("");
                        tv_fee_after_discounted_followup.setVisibility(View.GONE);
                    }
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
                        Toast.makeText(SettingActivity.this, "Service Charge cannot be zero", Toast.LENGTH_LONG).show();
                    } else {
                        if (!TextUtils.isEmpty(edt_deduction_amount.getText().toString()) && TextUtils.isEmpty(edt_fees.getText().toString())) {
                            Toast.makeText(SettingActivity.this, "Cancellation Charge is not allowed without Service Charge", Toast.LENGTH_LONG).show();
                        } else {
                            if (!TextUtils.isEmpty(edt_deduction_amount.getText().toString()) && !TextUtils.isEmpty(edt_fees.getText().toString())) {
                                if (Integer.parseInt(edt_deduction_amount.getText().toString()) <= Integer.parseInt(edt_fees.getText().toString())) {
                                    if (TextUtils.isEmpty(edt_discounted_followup_price.getText().toString())) {
                                        if (!TextUtils.isEmpty(edt_follow_up_in_days.getText().toString())) {
                                            if (Integer.parseInt(edt_follow_up_in_days.getText().toString()) >=
                                                    (Integer.parseInt(edt_limited_followup_days.getText().toString()))) {
                                                updatePaymentSettings();
                                            } else {
                                                Toast.makeText(SettingActivity.this, "Limited follow up days cannot be greater than follow up days", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            updatePaymentSettings();
                                        }
                                    } else {
                                        if (Integer.parseInt(edt_discounted_followup_price.getText().toString()) >= 0 && (Integer.parseInt(edt_discounted_followup_price.getText().toString()) < Integer.parseInt(edt_fees.getText().toString()))) {
                                            if (!TextUtils.isEmpty(edt_follow_up_in_days.getText().toString())) {
                                                if (Integer.parseInt(edt_follow_up_in_days.getText().toString()) >= (Integer.parseInt(edt_limited_followup_days.getText().toString()))) {
                                                    updatePaymentSettings();
                                                } else {
                                                    Toast.makeText(SettingActivity.this, "Limited follow up days cannot be greater than follow up days", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                updatePaymentSettings();
                                            }
                                        } else {
                                            Toast.makeText(SettingActivity.this, "Discounted follow up price cannot be greater than Service Charges", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(SettingActivity.this, "Cancellation charges cannot be greater than Service Charges", Toast.LENGTH_LONG).show();
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
            progressDialog.show();
            storeSettingApiCalls.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }
    }

    private void updatePaymentSettings() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            StoreSetting storeSetting = storeSettingTemp;
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
            storeSetting.setServicePayment(servicePaymentEnum);
            storeSetting.setEnabledPayment(cb_enable_payment.isChecked());
            storeSettingApiCalls.serviceCost(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), storeSetting);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
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

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Queue Settings...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
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
            toggleDayClosed.setChecked(storeSetting.isDayClosed());
            toggleDayClosedLabel.setText(storeSetting.isDayClosed() ? YES : NO);
            togglePreventJoin.setChecked(storeSetting.isPreventJoining());
            togglePreventJoinLabel.setText(storeSetting.isPreventJoining() ? YES : NO);
            toggleTodayClosed.setChecked(storeSetting.isTempDayClosed());
            toggleTodayClosedLabel.setText(storeSetting.isTempDayClosed() ? YES : NO);
            toggleStoreOffline.setChecked(storeSetting.getStoreActionType() == ActionTypeEnum.INACTIVE);
            toggleStoreOfflineLabel.setText(storeSetting.getStoreActionType() == ActionTypeEnum.INACTIVE ? YES : NO);
            cb_enable_payment.setChecked(storeSetting.isEnabledPayment());
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
            ServicePaymentEnum servicePaymentEnum = storeSetting.getServicePayment();
            sc_paid_user.setSelectedSegment(pay_list.indexOf(servicePaymentEnum.getDescription()));
            edt_deduction_amount.setText(CommonHelper.displayPrice(storeSetting.getCancellationPrice()));
            edt_fees.setText(CommonHelper.displayPrice(storeSetting.getProductPrice()));

            edt_discounted_followup_price.setText(String.valueOf(storeSetting.getDiscountedFollowupProductPrice() / 100));
            edt_follow_up_in_days.setText(String.valueOf(storeSetting.getFreeFollowupDays()));
            edt_limited_followup_days.setText(String.valueOf(storeSetting.getDiscountedFollowupDays()));
        }
        dismissProgress();
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
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        LaunchActivity.getLaunchActivity().dismissProgress();
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

    @Override
    public void onClick(View v) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            updateQueueSettings();
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
            if (v.getId() == R.id.toggleDayClosed) {
                toggleDayClosed.setChecked(!toggleDayClosed.isChecked());
                toggleDayClosedLabel.setText(toggleDayClosed.isChecked() ? YES : NO);
            } else if (v.getId() == R.id.togglePreventJoin) {
                togglePreventJoin.setChecked(!togglePreventJoin.isChecked());
                togglePreventJoinLabel.setText(togglePreventJoin.isChecked() ? YES : NO);
            } else if (v.getId() == R.id.toggleTodayClosed) {
                toggleTodayClosed.setChecked(!toggleTodayClosed.isChecked());
                toggleTodayClosedLabel.setText(toggleTodayClosed.isChecked() ? YES : NO);
            } else if (v.getId() == R.id.toggleStoreOffline) {
                toggleStoreOffline.setChecked(!toggleStoreOffline.isChecked());
                toggleStoreOfflineLabel.setText(toggleStoreOffline.isChecked() ? YES : NO);
            }
        }
    }

    private void updateQueueSettings() {
        progressDialog.setMessage("Updating Queue Settings...");
        StoreSetting storeSetting = new StoreSetting();
        storeSetting.setCodeQR(codeQR);
        storeSetting.setDayClosed(toggleDayClosed.isChecked());
        storeSetting.setPreventJoining(togglePreventJoin.isChecked());
        storeSetting.setTempDayClosed(toggleTodayClosed.isChecked());
        storeSetting.setStoreActionType(toggleStoreOffline.isChecked() ? ActionTypeEnum.INACTIVE : ActionTypeEnum.ACTIVE);
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
                        Toast.makeText(SettingActivity.this, getString(R.string.error_time), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(SettingActivity.this, getString(R.string.error_time), Toast.LENGTH_LONG).show();
                    } else {
                        LocalTime startTime = Formatter.parseLocalTime(tv_store_start.getText().toString().replace(":", ""));
                        LocalTime closeTime = Formatter.parseLocalTime(tv_store_close.getText().toString().replace(":", ""));
                        LocalTime arrivalTime = Formatter.parseLocalTime(String.format("%02d%02d", selectedHour, selectedMinute));
                        if (arrivalTime.isBefore(startTime)) {
                            Toast.makeText(SettingActivity.this, getString(R.string.error_delay_time), Toast.LENGTH_LONG).show();
                        } else if (closeTime.isBefore(arrivalTime)) {
                            Toast.makeText(SettingActivity.this, getString(R.string.error_delay_time), Toast.LENGTH_LONG).show();
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
            progressDialog.show();
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
                    Toast.makeText(SettingActivity.this, getString(R.string.error_past_date), Toast.LENGTH_LONG).show();
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
