package com.noqapp.android.merchant.views.activities;


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
import com.google.android.material.datepicker.Month;
import com.noqapp.android.merchant.R;

import java.util.Calendar;

public class DatePickerActivity extends AppCompatActivity {

    private static final long TODAY;
    private static final long NEXT_MONTH;
    private static final Month JAN_THIS_YEAR;
    private static final Month DEC_THIS_YEAR;
    private static final Month ONE_YEAR_FORWARD;
    private static final Pair<Long, Long> TODAY_PAIR;
    private static final Pair<Long, Long> NEXT_MONTH_PAIR;

    private static int SELECTION_MODE_DEFAULT = 0;
    private static int SELECTION_MODE_TODAY = 1;
    private static int SELECTION_MODE_NEXT = 2;

    private static int TITLE_DEFAULT = 0;
    private static int TITLE_CUSTOM = 1;

    private static int THEME_DIALOG = 0;
    private static int THEME_FULLSCREEN = 1;

    private static int BOUND_THIS_YEAR_ONLY = 0;
    private static int BOUND_NEXT_ONE_YEAR = 1;

    private static int OPENING_MONTH_CURRENT = 0;
    private static int OPENING_MONTH_NEXT = 1;


    static {
        Calendar calToday = Calendar.getInstance();
        TODAY = calToday.getTimeInMillis();
        Calendar calNextMonth = Calendar.getInstance();
        calNextMonth.roll(Calendar.MONTH, 1);
        NEXT_MONTH = calNextMonth.getTimeInMillis();

        Calendar calJanThisYear = Calendar.getInstance();
        calJanThisYear.set(Calendar.MONTH, Calendar.JANUARY);
        JAN_THIS_YEAR = Month.create(calJanThisYear.getTimeInMillis());
        Calendar calDecThisYear = Calendar.getInstance();
        calDecThisYear.set(Calendar.MONTH, Calendar.DECEMBER);
        DEC_THIS_YEAR = Month.create(calDecThisYear.getTimeInMillis());
        Calendar calOneYearForward = Calendar.getInstance();
        calOneYearForward.roll(Calendar.YEAR, 1);
        ONE_YEAR_FORWARD = Month.create(calOneYearForward.getTimeInMillis());

        TODAY_PAIR = new Pair<>(TODAY, TODAY);
        NEXT_MONTH_PAIR = new Pair<>(NEXT_MONTH, NEXT_MONTH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_lay);
        openCalender();
    }

    private void openCalender() {
        MaterialDatePicker.Builder<?> builder =
                setupDateSelectorBuilder(SELECTION_MODE_DEFAULT, SELECTION_MODE_TODAY);
        CalendarConstraints.Builder constraintsBuilder =
                setupConstraintsBuilder(BOUND_THIS_YEAR_ONLY, OPENING_MONTH_CURRENT, 0);
        int dialogTheme = resolveOrThrow(this, R.attr.materialCalendarTheme);
        int fullscreenTheme = resolveOrThrow(this, R.attr.materialCalendarFullscreenTheme);
        int themeChoice = THEME_FULLSCREEN;
        if (themeChoice == THEME_FULLSCREEN) {
            builder.setTheme(dialogTheme);
        } else if (themeChoice == THEME_FULLSCREEN) {
            builder.setTheme(fullscreenTheme);
        }
        int titleChoice = 0;
        if (titleChoice == TITLE_CUSTOM) {
            builder.setTitleTextResId(R.string.cat_picker_title_custom);
        }

        try {
            // builder.setCalendarConstraints(constraintsBuilder.build());
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

    private static int resolveOrThrow(Context context, @AttrRes int attributeResId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attributeResId, typedValue, true)) {
            return typedValue.data;
        }
        throw new IllegalArgumentException(context.getResources().getResourceName(attributeResId));
    }

    private static MaterialDatePicker.Builder<?> setupDateSelectorBuilder(
            int selectionModeChoice, int selectionChoice) {
        if (selectionModeChoice == SELECTION_MODE_DEFAULT) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            if (selectionChoice == SELECTION_MODE_TODAY) {
                builder.setSelection(TODAY);
            } else if (selectionChoice == SELECTION_MODE_NEXT) {
                builder.setSelection(NEXT_MONTH);
            }
            return builder;
        } else {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                    MaterialDatePicker.Builder.dateRangePicker();
//            if (selectionChoice == R.id.cat_picker_selection_today) {
//                builder.setSelection(TODAY_PAIR);
//            } else if (selectionChoice == R.id.cat_picker_selection_next_month) {
//                builder.setSelection(NEXT_MONTH_PAIR);
//            }
            return builder;
        }
    }

    private static CalendarConstraints.Builder setupConstraintsBuilder(
            int boundsChoice, int openingChoice, int validationChoice) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        if (boundsChoice == BOUND_THIS_YEAR_ONLY) {
            constraintsBuilder.setStart(JAN_THIS_YEAR);
            constraintsBuilder.setEnd(DEC_THIS_YEAR);
        } else if (boundsChoice == BOUND_NEXT_ONE_YEAR) {
            constraintsBuilder.setEnd(ONE_YEAR_FORWARD);
        }

        if (openingChoice == OPENING_MONTH_CURRENT) {
            constraintsBuilder.setOpening(Month.today());
        } else if (openingChoice == OPENING_MONTH_NEXT) {
            constraintsBuilder.setOpening(Month.create(NEXT_MONTH));
        }

//        if (validationChoice == R.id.cat_picker_validation_future) {
//            constraintsBuilder.setValidator(new DateValidatorPointForward());
//        } else if (validationChoice == R.id.cat_picker_validation_weekdays) {
//            constraintsBuilder.setValidator(new DateValidatorWeekdays());
//        }
        return constraintsBuilder;
    }

    private void resultBack(String result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
