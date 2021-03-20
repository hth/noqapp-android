package com.noqapp.android.common.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.noqapp.android.common.R;

import java.util.Calendar;
import java.util.TimeZone;

public class DatePickerActivity extends AppCompatActivity {
    private long today;
    private long lastHundredYear;
    private long nextMonth;
    private long janThisYear;
    private long decThisYear;
    private long oneYearForward;
    private Pair<Long, Long> todayPair;
    private Pair<Long, Long> nextMonthPair;

    private final int SELECTION_MODE_DEFAULT = 0;
    private final int SELECTION_MODE_TODAY = 1;
    private final int SELECTION_MODE_NEXT = 2;

    private static Calendar getClearedUtc() {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        return utc;
    }

    private void init() {
        today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = getClearedUtc();
        calendar.setTimeInMillis(today);
        calendar.roll(Calendar.MONTH, 1);
        nextMonth = calendar.getTimeInMillis();

        calendar.setTimeInMillis(today);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        janThisYear = calendar.getTimeInMillis();
        calendar.setTimeInMillis(today);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        decThisYear = calendar.getTimeInMillis();

        calendar.setTimeInMillis(today);
        calendar.roll(Calendar.YEAR, 1);
        oneYearForward = calendar.getTimeInMillis();
        Calendar calendar1 = getClearedUtc();
        calendar1.set(Calendar.YEAR, -100);
        lastHundredYear =  calendar1.getTimeInMillis();

        todayPair = new Pair<>(today, today);
        nextMonthPair = new Pair<>(nextMonth, nextMonth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_lay);
        init();
        openCalender();
    }

    private void openCalender() {
        MaterialDatePicker.Builder<?> builder = setupDateSelectorBuilder(SELECTION_MODE_DEFAULT, SELECTION_MODE_TODAY);
        CalendarConstraints.Builder constraintsBuilder = setupConstraintsBuilder(false);
        int dialogTheme = resolveOrThrow(this, R.attr.materialCalendarTheme);
        int fullscreenTheme = resolveOrThrow(this, R.attr.materialCalendarFullscreenTheme);

        builder.setTheme(dialogTheme);
       // builder.setTheme(fullscreenTheme);
        builder.setTitleText(R.string.cat_picker_title_custom);

        try {
             builder.setCalendarConstraints(constraintsBuilder.build());
            MaterialDatePicker<?> picker = builder.build();
            picker.show(DatePickerActivity.this.getSupportFragmentManager(), picker.toString());
            picker.addOnNegativeButtonClickListener(v -> {
                picker.dismiss();
                resultBack("");
            });

            picker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Object>) selection -> {
                String output = picker.getHeaderText();
                picker.dismiss();
                resultBack(output);
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private int resolveOrThrow(Context context, @AttrRes int attributeResId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attributeResId, typedValue, true)) {
            return typedValue.data;
        }
        throw new IllegalArgumentException(context.getResources().getResourceName(attributeResId));
    }

    private MaterialDatePicker.Builder<?> setupDateSelectorBuilder(int selectionModeChoice, int selectionChoice) {
        if (selectionModeChoice == SELECTION_MODE_DEFAULT) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            if (selectionChoice == SELECTION_MODE_TODAY) {
                builder.setSelection(today);
            } else if (selectionChoice == SELECTION_MODE_NEXT) {
                builder.setSelection(nextMonth);
            }
            return builder;
        } else {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
//            if (selectionChoice == R.id.cat_picker_selection_today) {
//                builder.setSelection(TODAY_PAIR);
//            } else if (selectionChoice == R.id.cat_picker_selection_next_month) {
//                builder.setSelection(NEXT_MONTH_PAIR);
//            }
            return builder;
        }
    }

    private CalendarConstraints.Builder setupConstraintsBuilder(boolean isFuture) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if (isFuture) {
            constraintsBuilder.setStart(today);
            constraintsBuilder.setEnd(oneYearForward);
        } else  {
            constraintsBuilder.setStart(lastHundredYear);
            constraintsBuilder.setEnd(today);
        }
        constraintsBuilder.setOpenAt(today);
        return constraintsBuilder;
    }

    private void resultBack(String result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
