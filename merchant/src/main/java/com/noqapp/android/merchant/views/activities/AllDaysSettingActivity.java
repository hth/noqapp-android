package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.StoreSettingApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.StoreHours;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.utils.ViewAnimationUtils;
import com.noqapp.android.merchant.views.interfaces.StoreHoursSettingPresenter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllDaysSettingActivity extends BaseActivity implements StoreHoursSettingPresenter, View.OnClickListener {
    protected ImageView actionbarBack;
    private String codeQR;
    private StoreSettingApiCalls storeSettingApiCalls;
    private StoreHours updatedStoreHours;
    boolean is_token_timing[];
    private TextView tvStoreClose[], tvStoreStart[], tvStoreLunchStart[], tvStoreLunchClose[],
            tvTokenAvailable[], tvTokenNotAvailable[];
    private CheckBox[] cbLunch;
    private ImageView[] imageViewsOpenClose;
    private LinearLayout[] llCollapse;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        storeSettingApiCalls = new StoreSettingApiCalls(this);
        setContentView(R.layout.activity_all_days_setting);
        cbLunch = new CheckBox[7];
        imageViewsOpenClose = new ImageView[7];
        llCollapse = new LinearLayout[7];
        tvStoreStart = new TextView[7];
        tvStoreClose = new TextView[7];
        tvStoreLunchStart = new TextView[7];
        tvStoreLunchClose = new TextView[7];
        tvTokenAvailable = new TextView[7];
        tvTokenNotAvailable = new TextView[7];
        btnUpdate = findViewById(R.id.btn_update_time);
        btnUpdate.setOnClickListener(this);
        is_token_timing = new boolean[7];

        for (int i = 0; i < 7; i++) {
            is_token_timing[i] = true;
            Resources res = getResources();
            int resCount = i + 1;
            int id0 = res.getIdentifier("tv_token_available" + resCount, "id", getPackageName());
            int id1 = res.getIdentifier("tv_token_not_available" + resCount, "id", getPackageName());
            tvTokenAvailable[i] = findViewById(id0);
            tvTokenNotAvailable[i] = findViewById(id1);

            int id2 = res.getIdentifier("tv_store_start" + resCount, "id", getPackageName());
            int id3 = res.getIdentifier("tv_store_close" + resCount, "id", getPackageName());
            tvStoreStart[i] = findViewById(id2);
            tvStoreClose[i] = findViewById(id3);

            int id4 = res.getIdentifier("tv_store_lunch_start" + resCount, "id", getPackageName());
            int id5 = res.getIdentifier("tv_store_lunch_close" + resCount, "id", getPackageName());
            tvStoreLunchStart[i] = findViewById(id4);
            tvStoreLunchClose[i] = findViewById(id5);

            int id6 = res.getIdentifier("cb_lunch" + resCount, "id", getPackageName());
            cbLunch[i] = findViewById(id6);

            int id7 = res.getIdentifier("iv_token_timing" + resCount, "id", getPackageName());
            imageViewsOpenClose[i] = findViewById(id7);
            imageViewsOpenClose[i].setTag(String.valueOf(i));
            imageViewsOpenClose[i].setOnClickListener(this::onClick);

            int id8 = res.getIdentifier("ll_token_timing" + resCount, "id", getPackageName());
            llCollapse[i] = findViewById(id8);

            final int pos = i;
            tvTokenAvailable[i].setOnClickListener(new TextViewClick(tvTokenAvailable[i]));
            tvTokenNotAvailable[i].setOnClickListener(new TextViewClick(tvTokenNotAvailable[i]));
            tvStoreStart[i].setOnClickListener(new TextViewClick(tvStoreStart[i]));
            tvStoreClose[i].setOnClickListener(new TextViewClick(tvStoreClose[i]));
            tvStoreLunchStart[i].setOnClickListener(new TextViewClick(tvStoreLunchStart[i]));
            tvStoreLunchClose[i].setOnClickListener(new TextViewClick(tvStoreLunchClose[i]));

            cbLunch[i].setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    tvStoreLunchStart[pos].setOnClickListener(new TextViewClick(tvStoreLunchStart[pos]));
                    tvStoreLunchStart[pos].setVisibility(View.VISIBLE);
                    tvStoreLunchClose[pos].setOnClickListener(new TextViewClick(tvStoreLunchClose[pos]));
                    tvStoreLunchClose[pos].setVisibility(View.VISIBLE);
                } else {
                    tvStoreLunchStart[pos].setOnClickListener(disableClick);
                    tvStoreLunchStart[pos].setVisibility(View.GONE);
                    tvStoreLunchClose[pos].setOnClickListener(disableClick);
                    tvStoreLunchClose[pos].setVisibility(View.GONE);
                }
            });
            cbLunch[i].setChecked(true);
            if (!LaunchActivity.isTablet) {
                imageViewsOpenClose[i].setOnClickListener(this);
                imageViewsOpenClose[i].performClick();
            }
        }

        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.setScrollBarFadeDuration(0);
        scroll_view.setScrollbarFadingEnabled(false);
        setProgressMessage("Loading Queue Settings...");
        codeQR = getIntent().getStringExtra("codeQR");
        actionbarBack.setOnClickListener(v -> onBackPressed());
        tv_toolbar_title.setText(getString(R.string.screen_settings_all_days));

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            storeSettingApiCalls.storeHours(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(AllDaysSettingActivity.this);
        }
    }

    View.OnClickListener disableClick = v -> ShowAlertInformation.showThemeDialog(
            AllDaysSettingActivity.this,
            "Alert",
            "Lunch time disabled for the day. Select checkbox to enable the lunch time.");

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
    public void queueStoreHoursSettingResponse(StoreHours storeHours) {
        if (null != storeHours) {
            updatedStoreHours = storeHours;
            for (int i = 0; i < 7; i++) {
                //  The int value follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday).
                JsonHour jsonHour = getStoreHour(updatedStoreHours.getJsonHours(), i + 1);
                tvTokenAvailable[i].setText(Formatter.convertMilitaryTo24HourFormat(jsonHour.getTokenAvailableFrom()));
                tvStoreStart[i].setText(Formatter.convertMilitaryTo24HourFormat(jsonHour.getStartHour()));
                tvTokenNotAvailable[i].setText(Formatter.convertMilitaryTo24HourFormat(jsonHour.getTokenNotAvailableFrom()));
                tvStoreClose[i].setText(Formatter.convertMilitaryTo24HourFormat(jsonHour.getEndHour()));
                tvStoreLunchStart[i].setText(Formatter.convertMilitaryTo24HourFormat(jsonHour.getLunchTimeStart()));
                tvStoreLunchClose[i].setText(Formatter.convertMilitaryTo24HourFormat(jsonHour.getLunchTimeEnd()));
                cbLunch[i].setChecked(jsonHour.getLunchTimeStart() != jsonHour.getLunchTimeEnd());
            }
        }
        dismissProgress();
    }

    @Override
    public void queueStoreHoursSettingModifyResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (null != jsonResponse) {
            if (jsonResponse.getResponse() == Constants.SUCCESS) {
                new CustomToast().showToast(this, "Settings updated successfully!!!");
            } else {
                new CustomToast().showToast(this, "Settings updated failed!!!" + "\n" + jsonResponse.getError().getReason());
            }
        } else {
            new CustomToast().showToast(this, "Settings updated failed!!!");
        }
    }

    @Override
    public void queueStoreHoursSettingError() {
        dismissProgress();
        // to make sure the data is not changed in case of error
        if (null != updatedStoreHours) {
            queueStoreHoursSettingResponse(updatedStoreHours);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != updatedStoreHours) {
            queueStoreHoursSettingResponse(updatedStoreHours);
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
            imageView.setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.arrow_up));
        } else {
            ViewAnimationUtils.collapse(linearLayout);
            imageView.setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.arrow_down));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_time: {
                boolean isValid = true;
                for (int pos = 0; pos < 7; pos++) {
                    tvStoreStart[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
                    //tvStoreClose[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
                    tvTokenAvailable[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
                    tvTokenNotAvailable[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
                    tvStoreStart[pos].setError(null);
                    // tvStoreClose[pos].setError(null);
                    tvTokenAvailable[pos].setError(null);
                    tvTokenNotAvailable[pos].setError(null);
                    if (isEndTimeBeforeStartTime(tvStoreStart[pos], tvStoreClose[pos])) {
                        tvStoreStart[pos].setError("'Queue start time' should be before 'Queue close time'.");
                        tvStoreStart[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_red_bg_drawable));
                        if (isValid) {
                            isValid = false;
                        }
                    } else if (isEndTimeBeforeStartTime(tvTokenAvailable[pos], tvTokenNotAvailable[pos])) {
                        tvTokenAvailable[pos].setError("'Issue token from' should be before 'Stop issuing token after'.");
                        tvTokenAvailable[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_red_bg_drawable));
                        if (isValid) {
                            isValid = false;
                        }
                    } else if (isEndTimeBeforeStartTime(tvTokenNotAvailable[pos], tvStoreClose[pos])) {
                        tvTokenNotAvailable[pos].setError("'Stop issuing token after' should be before 'Queue close time'.");
                        tvTokenNotAvailable[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_red_bg_drawable));
                        if (isValid) {
                            isValid = false;
                        }
                    } else {
                        boolean isLunchTimeValid = validateLunchTime(pos);
                        if (!isLunchTimeValid && isValid) {
                            isValid = false;
                        }
                    }
                }
                if (isValid) {
                    callUpdate(getString(R.string.setting_token_q_timing));
                }
            }
            break;
            default: {
                int tag = Integer.parseInt((String) v.getTag());
                is_token_timing[tag] = !is_token_timing[tag];
                expandCollapseViewWithArrow(is_token_timing[tag], llCollapse[tag], imageViewsOpenClose[tag]);
            }
        }
    }

    private void updateQueueSettings(String alertMsg) {
        ShowCustomDialog showDialog = new ShowCustomDialog(AllDaysSettingActivity.this);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                showProgress();
                setProgressMessage("Updating Queue Settings...");
                updatedStoreHours.setCodeQR(codeQR);
                List<JsonHour> jsonHours = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    JsonHour jsonHour = getStoreHour(updatedStoreHours.getJsonHours(), i + 1);
                    if (StringUtils.isNotBlank(tvTokenAvailable[i].getText().toString())) {
                        jsonHour.setTokenAvailableFrom(Integer.parseInt(tvTokenAvailable[i].getText().toString().replace(":", "")));
                    }

                    if (StringUtils.isNotBlank(tvStoreStart[i].getText().toString())) {
                        jsonHour.setStartHour(Integer.parseInt(tvStoreStart[i].getText().toString().replace(":", "")));
                    }

                    if (StringUtils.isNotBlank(tvTokenNotAvailable[i].getText().toString())) {
                        jsonHour.setTokenNotAvailableFrom(Integer.parseInt(tvTokenNotAvailable[i].getText().toString().replace(":", "")));
                    }

                    if (StringUtils.isNotBlank(tvStoreClose[i].getText().toString())) {
                        jsonHour.setEndHour(Integer.parseInt(tvStoreClose[i].getText().toString().replace(":", "")));
                    }

                    if (cbLunch[i].isChecked() && StringUtils.isNotBlank(tvStoreLunchStart[i].getText().toString())) {
                        jsonHour.setLunchTimeStart(Integer.parseInt(tvStoreLunchStart[i].getText().toString().replace(":", "")));
                    } else {
                        jsonHour.setLunchTimeStart(0);
                    }

                    if (cbLunch[i].isChecked() && StringUtils.isNotBlank(tvStoreLunchClose[i].getText().toString())) {
                        jsonHour.setLunchTimeEnd(Integer.parseInt(tvStoreLunchClose[i].getText().toString().replace(":", "")));
                    } else {
                        jsonHour.setLunchTimeEnd(0);
                    }
                    jsonHours.add(jsonHour);
                }
                updatedStoreHours.setJsonHours(jsonHours);
                storeSettingApiCalls.storeHoursUpdate(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), updatedStoreHours);
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog(getString(R.string.setting_title), TextUtils.isEmpty(alertMsg) ? getString(R.string.setting_msg) : alertMsg);
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
            mTimePicker = new TimePickerDialog(AllDaysSettingActivity.this, R.style.TimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
                if (selectedHour == 0 && selectedMinute == 0) {
                    new CustomToast().showToast(AllDaysSettingActivity.this, getString(R.string.error_time));
                } else {
                    textView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }
            }, hour, minute, false);//Yes 24 hour time
            //mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    private void callUpdate(String alertMsg) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            updateQueueSettings(alertMsg);
        } else {
            ShowAlertInformation.showNetworkDialog(AllDaysSettingActivity.this);
        }
    }

    private boolean isEndTimeBeforeStartTime(TextView tv_start_time, TextView tv_end_time) {
        LocalTime startTime = Formatter.parseLocalTime(tv_start_time.getText().toString().replace(":", ""));
        LocalTime endTime = Formatter.parseLocalTime(tv_end_time.getText().toString().replace(":", ""));
        return endTime.isBefore(startTime);
    }

    private boolean validateLunchTime(int pos) {
        CheckBox cb_lunch = cbLunch[pos];
        tvStoreLunchStart[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
        tvStoreLunchClose[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
        tvStoreClose[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
        tvStoreStart[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_white_bg_drawable));
        tvStoreStart[pos].setError(null);
        tvStoreClose[pos].setError(null);
        tvStoreLunchStart[pos].setError(null);
        tvStoreLunchClose[pos].setError(null);
        if (cb_lunch.isChecked()) {
            boolean isValid = true;
            if (isEndTimeBeforeStartTime(tvStoreLunchStart[pos], tvStoreLunchClose[pos])) {
                tvStoreLunchStart[pos].setError("'Lunch start time' should be before 'Lunch close time'.");
                tvStoreLunchStart[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_red_bg_drawable));
                isValid = false;
            } else if (isEndTimeBeforeStartTime(tvStoreStart[pos], tvStoreLunchStart[pos])) {
                tvStoreLunchStart[pos].setError("'Lunch start time' should be after 'Queue start time'.");
                tvStoreLunchStart[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_red_bg_drawable));
                isValid = false;
            } else if (isEndTimeBeforeStartTime(tvStoreLunchClose[pos], tvStoreClose[pos])) {
                tvStoreLunchClose[pos].setError("'Lunch close time' should be before 'Queue close time'.");
                tvStoreLunchClose[pos].setBackground(ContextCompat.getDrawable(AllDaysSettingActivity.this, R.drawable.square_red_bg_drawable));
                isValid = false;
            }
            return isValid;
        } else {
            return true;
        }
    }

    private JsonHour getStoreHour(List<JsonHour> jsonHourList, int day) {
        if (null != jsonHourList && jsonHourList.size() > 0) {
            for (int i = 0; i < jsonHourList.size(); i++) {
                if (jsonHourList.get(i).getDayOfWeek() == day) {
                    return jsonHourList.get(i);
                }
            }
        }
        return null;
    }
}
