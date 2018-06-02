package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.QueueSettingModel;
import com.noqapp.android.merchant.presenter.beans.body.QueueSetting;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.QueueSettingPresenter;
import com.noqapp.library.utils.Formatter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements QueueSettingPresenter, View.OnClickListener {

    private ProgressDialog progressDialog;
    private TextView tv_toolbar_title;
    protected ImageView actionbarBack;
    private TextView tv_title;
    private ToggleButton toggleDayClosed, togglePreventJoin;
    private String codeQR;
    protected boolean isDialog = false;
    private TextView tv_store_close, tv_store_start, tv_token_available, tv_token_not_available, tv_limited_label, tv_delay_in_minute;
    private Button btn_update_time, btn_update_delay;
    private CheckBox cb_limit;
    private EditText edt_token_no;
    private boolean arrivalTextChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        QueueSettingModel.queueSettingPresenter = this;
        setContentView(R.layout.activity_setting);
        if (isDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.60);
            int height = (int) (metrics.heightPixels * 0.60);
            getWindow().setLayout(screenWidth, height);
        }
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        initProgress();
        tv_title = (TextView) findViewById(R.id.tv_title);
        toggleDayClosed = (ToggleButton) findViewById(R.id.toggleDayClosed);
        togglePreventJoin = (ToggleButton) findViewById(R.id.togglePreventJoin);
        String title = getIntent().getStringExtra("title");
        codeQR = getIntent().getStringExtra("codeQR");
        if (null != title) {
            tv_title.setText(title);
        }
        toggleDayClosed.setOnClickListener(this);
        togglePreventJoin.setOnClickListener(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_settings));

        cb_limit = (CheckBox) findViewById(R.id.cb_limit);
        cb_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edt_token_no.setVisibility(View.INVISIBLE);
                    tv_limited_label.setText("Un-Limited Tokens");
                    View view = SettingActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    edt_token_no.setVisibility(View.VISIBLE);
                    // edt_token_no.setText("1");
                    tv_limited_label.setText("Limited Tokens");
                }
            }
        });
        edt_token_no = (EditText) findViewById(R.id.edt_token_no);
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
        tv_token_available = (TextView) findViewById(R.id.tv_token_available);
        tv_store_start = (TextView) findViewById(R.id.tv_store_start);
        tv_token_not_available = (TextView) findViewById(R.id.tv_token_not_available);
        tv_store_close = (TextView) findViewById(R.id.tv_store_close);
        tv_token_available.setOnClickListener(new TextViewClick(tv_token_available));
        tv_store_start.setOnClickListener(new TextViewClick(tv_store_start));
        tv_token_not_available.setOnClickListener(new TextViewClick(tv_token_not_available));
        tv_store_close.setOnClickListener(new TextViewClick(tv_store_close));
        tv_limited_label = (TextView) findViewById(R.id.tv_limited_label);
        tv_delay_in_minute = (TextView) findViewById(R.id.tv_delay_in_minute);
        tv_delay_in_minute.setOnClickListener(new TextViewClickDelay(tv_delay_in_minute));
        btn_update_time = (Button) findViewById(R.id.btn_update_time);
        btn_update_delay = (Button) findViewById(R.id.btn_update_delay);
        btn_update_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUpdate();
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
            QueueSettingModel.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(SettingActivity.this);
        }
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
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
    }

    @Override
    public void queueSettingResponse(QueueSetting queueSetting) {
        if (null != queueSetting) {
            toggleDayClosed.setChecked(queueSetting.isDayClosed());
            togglePreventJoin.setChecked(queueSetting.isPreventJoining());
            tv_token_available.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getTokenAvailableFrom()));
            tv_store_start.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getStartHour()));
            tv_token_not_available.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getTokenNotAvailableFrom()));
            tv_store_close.setText(Formatter.convertMilitaryTo24HourFormat(queueSetting.getEndHour()));

            LocalTime localTime = Formatter.parseLocalTime(String.format(Locale.US, "%04d", queueSetting.getStartHour()));
            localTime = localTime.plusMinutes(queueSetting.getDelayedInMinutes());
            tv_delay_in_minute.setText(Formatter.convertMilitaryTo24HourFormat(localTime));

            if (queueSetting.getAvailableTokenCount() <= 0) {
                cb_limit.setChecked(true);
                tv_limited_label.setText("Un-Limited Tokens");
                edt_token_no.setVisibility(View.INVISIBLE);
            } else {
                cb_limit.setChecked(false);
                edt_token_no.setText(String.valueOf(queueSetting.getAvailableTokenCount()));
                edt_token_no.setVisibility(View.VISIBLE);
                tv_limited_label.setText("Limited Tokens");
                if (edt_token_no != null) {
                    edt_token_no.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_token_no.getWindowToken(), 0);
                }
            }
        }
        dismissProgress();
    }

    @Override
    public void queueSettingError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorcode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorcode == Constants.INVALID_CREDENTIAL) {
            Intent intent = new Intent();
            intent.putExtra(Constants.CLEAR_DATA, true);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
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
            } else {
                togglePreventJoin.setChecked(!togglePreventJoin.isChecked());
            }
        }
    }

    private void updateQueueSettings() {
        QueueSetting queueSetting = new QueueSetting();
        queueSetting.setCodeQR(codeQR);
        queueSetting.setDayClosed(toggleDayClosed.isChecked());
        queueSetting.setPreventJoining(togglePreventJoin.isChecked());

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
        QueueSettingModel.modify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), queueSetting);
    }

    private class TextViewClick implements View.OnClickListener {
        private TextView textView;

        public TextViewClick(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onClick(View view) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if (selectedHour == 0 && selectedMinute == 0) {
                        Toast.makeText(SettingActivity.this, getString(R.string.error_time), Toast.LENGTH_LONG).show();
                    } else {
                        textView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }
            }, hour, minute, true);//Yes 24 hour time
            //mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    private class TextViewClickDelay implements View.OnClickListener {
        private TextView textView;

        public TextViewClickDelay(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onClick(View view) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if (selectedHour == 0 && selectedMinute == 0) {
                        Toast.makeText(SettingActivity.this, getString(R.string.error_time), Toast.LENGTH_LONG).show();
                    } else {
                        LocalTime startTime = Formatter.parseLocalTime(tv_store_start.getText().toString().replace(":", ""));
                        LocalTime arrivalTime = Formatter.parseLocalTime(String.format("%02d%02d", selectedHour, selectedMinute));
                        if (arrivalTime.isBefore(startTime)) {
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

}
