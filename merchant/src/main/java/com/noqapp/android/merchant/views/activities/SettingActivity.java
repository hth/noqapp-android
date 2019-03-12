package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.QueueSettingApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.QueueSettingPresenter;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements QueueSettingPresenter, View.OnClickListener {
    private ProgressDialog progressDialog;
    protected ImageView actionbarBack, iv_delete_scheduling;
    private SwitchCompat toggleDayClosed, togglePreventJoin, toggleTodayClosed, toggleStoreOffline;
    private String codeQR;
    protected boolean isDialog = false;
    private TextView tv_store_close, tv_store_start, tv_token_available, tv_token_not_available, tv_limited_label, tv_delay_in_minute, tv_close_day_of_week;
    private TextView tv_scheduling_from, tv_scheduling_ending, tv_scheduling_status;
    private CheckBox cb_limit;
    private EditText edt_token_no;
    private boolean arrivalTextChange = false;
    private QueueSettingApiCalls queueSettingApiCalls;
    private QueueSetting queueSettingTemp;
    private TextView togglePreventJoinLabel, toggleTodayClosedLabel, toggleDayClosedLabel, toggleStoreOfflineLabel;
    private String YES = "Yes";
    private String NO = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isDialog) {
            if (new AppUtils().isTablet(getApplicationContext())) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        super.onCreate(savedInstanceState);
        queueSettingApiCalls = new QueueSettingApiCalls(this);
        setContentView(R.layout.activity_setting);
        if (isDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.60);
            int height = (int) (metrics.heightPixels * 0.70);
            getWindow().setLayout(screenWidth, height);
        }
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        iv_delete_scheduling = findViewById(R.id.iv_delete_scheduling);
        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.setScrollBarFadeDuration(0);
        scroll_view.setScrollbarFadingEnabled(false);
        initProgress();
        TextView tv_title = findViewById(R.id.tv_title);
        toggleDayClosed = findViewById(R.id.toggleDayClosed);
        toggleTodayClosed = findViewById(R.id.toggleTodayClosed);
        togglePreventJoin = findViewById(R.id.togglePreventJoin);
        toggleStoreOffline = findViewById(R.id.toggleStoreOffline);
        togglePreventJoinLabel = findViewById(R.id.togglePreventJoinLabel);
        toggleTodayClosedLabel = findViewById(R.id.toggleTodayClosedLabel);
        toggleDayClosedLabel = findViewById(R.id.toggleDayClosedLabel);
        toggleStoreOfflineLabel = findViewById(R.id.toggleStoreOfflineLabel);
        String title = getIntent().getStringExtra("title");
        codeQR = getIntent().getStringExtra("codeQR");
        if (null != title) {
            tv_title.setText(title);
        }
        toggleDayClosed.setOnClickListener(this);
        toggleTodayClosed.setOnClickListener(this);
        togglePreventJoin.setOnClickListener(this);
        toggleStoreOffline.setOnClickListener(this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Remove Schedule");
                    tv_msg.setText("Do you want to remove schedule?");
                    Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                    Button btn_no = customDialogView.findViewById(R.id.btn_no);
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_yes.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            queueSettingApiCalls.removeSchedule(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();
                } else {
                    ShowAlertInformation.showNetworkDialog(SettingActivity.this);
                }
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_settings));

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
        tv_close_day_of_week = findViewById(R.id.tv_close_day_of_week);
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

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            queueSettingApiCalls.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
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
    public void queueSettingResponse(QueueSetting queueSetting) {
        if (null != queueSetting) {
            queueSettingTemp = queueSetting;
            toggleDayClosed.setChecked(queueSetting.isDayClosed());
            toggleDayClosedLabel.setText(queueSetting.isDayClosed() ? YES : NO);
            togglePreventJoin.setChecked(queueSetting.isPreventJoining());
            togglePreventJoinLabel.setText(queueSetting.isPreventJoining() ? YES : NO);
            toggleTodayClosed.setChecked(queueSetting.isTempDayClosed());
            toggleTodayClosedLabel.setText(queueSetting.isTempDayClosed() ? YES : NO);
            toggleStoreOffline.setChecked(queueSetting.getStoreActionType() == ActionTypeEnum.INACTIVE);
            toggleStoreOfflineLabel.setText(queueSetting.getStoreActionType() == ActionTypeEnum.INACTIVE ? YES : NO);
            tv_token_available.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getTokenAvailableFrom()));
            tv_store_start.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getStartHour()));
            tv_token_not_available.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getTokenNotAvailableFrom()));
            tv_store_close.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getEndHour()));
            LocalTime localTime = Formatter.parseLocalTime(String.format(Locale.US, "%04d", queueSetting.getStartHour()));
            localTime = localTime.plusMinutes(queueSetting.getDelayedInMinutes());
            tv_delay_in_minute.setText(Formatter.convertMilitaryTo24HourFormat(localTime));
            if (TextUtils.isEmpty(queueSetting.getScheduledFromDay())) {
                tv_scheduling_status.setText("");
                tv_scheduling_status.setVisibility(View.GONE);
                iv_delete_scheduling.setVisibility(View.GONE);
            } else {
                tv_scheduling_status.setText("Scheduled to close from " + queueSetting.getScheduledFromDay() + " to " + queueSetting.getScheduledUntilDay());
                tv_scheduling_status.setVisibility(View.VISIBLE);
                iv_delete_scheduling.setVisibility(View.VISIBLE);
            }

            if (queueSetting.getAvailableTokenCount() <= 0) {
                cb_limit.setChecked(true);
                tv_limited_label.setText(getString(R.string.unlimited_token));
                edt_token_no.setVisibility(View.INVISIBLE);
            } else {
                cb_limit.setChecked(false);
                edt_token_no.setText(String.valueOf(queueSetting.getAvailableTokenCount()));
                edt_token_no.setVisibility(View.VISIBLE);
                tv_limited_label.setText(getString(R.string.limited_token));
                if (edt_token_no != null) {
                    edt_token_no.clearFocus();
                    new AppUtils().hideKeyBoard(this);
                }
            }
        }
        dismissProgress();
    }

    @Override
    public void queueSettingError() {
        dismissProgress();
        // to make sure the data is not changed in case of error
        if (null != queueSettingTemp) {
            queueSettingResponse(queueSettingTemp);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != queueSettingTemp) {
            queueSettingResponse(queueSettingTemp);
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
            } else {
                toggleStoreOffline.setChecked(!toggleStoreOffline.isChecked());
                toggleStoreOfflineLabel.setText(toggleStoreOffline.isChecked() ? YES : NO);
            }
        }
    }

    private void updateQueueSettings() {
        progressDialog.setMessage("Updating Queue Settings...");
        QueueSetting queueSetting = new QueueSetting();
        queueSetting.setCodeQR(codeQR);
        queueSetting.setDayClosed(toggleDayClosed.isChecked());
        queueSetting.setPreventJoining(togglePreventJoin.isChecked());
        queueSetting.setTempDayClosed(toggleTodayClosed.isChecked());
        queueSetting.setStoreActionType(toggleStoreOffline.isChecked() ? ActionTypeEnum.INACTIVE : ActionTypeEnum.ACTIVE);
        if (StringUtils.isNotBlank(tv_token_available.getText().toString())) {
            queueSetting.setTokenAvailableFrom(Integer.parseInt(tv_token_available.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_store_start.getText().toString())) {
            queueSetting.setStartHour(Integer.parseInt(tv_store_start.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_token_not_available.getText().toString())) {
            queueSetting.setTokenNotAvailableFrom(Integer.parseInt(tv_token_not_available.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_store_close.getText().toString())) {
            queueSetting.setEndHour(Integer.parseInt(tv_store_close.getText().toString().replace(":", "")));
        }

        if (StringUtils.isNotBlank(tv_scheduling_from.getText().toString())) {
            queueSetting.setFromDay(tv_scheduling_from.getText().toString());
        }

        if (StringUtils.isNotBlank(tv_scheduling_ending.getText().toString())) {
            queueSetting.setUntilDay(tv_scheduling_ending.getText().toString());
        }

        if (StringUtils.isBlank(edt_token_no.getText().toString())) {
            queueSetting.setAvailableTokenCount(0);
        } else {
            queueSetting.setAvailableTokenCount(Integer.parseInt(edt_token_no.getText().toString()));
        }

        if (arrivalTextChange) {
            DateTime start = DateTime.now().withTimeAtStartOfDay();
            DateTime delay = DateTime.now().withTimeAtStartOfDay();

            int delayHour = Integer.valueOf(tv_delay_in_minute.getText().toString().split(":")[0]);
            int delayMinutes = Integer.valueOf(tv_delay_in_minute.getText().toString().split(":")[1]);
            int startHour = Integer.valueOf(tv_store_start.getText().toString().split(":")[0]);
            int startMinutes = Integer.valueOf(tv_store_start.getText().toString().split(":")[1]);

            Duration duration = new Duration(delay.plusHours(startHour).plusMinutes(startMinutes), start.plusHours(delayHour).plusMinutes(delayMinutes));
            queueSetting.setDelayedInMinutes((int) duration.getStandardMinutes());
        } else {
            queueSetting.setDelayedInMinutes(0);
        }
        queueSettingApiCalls.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), queueSetting);
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
