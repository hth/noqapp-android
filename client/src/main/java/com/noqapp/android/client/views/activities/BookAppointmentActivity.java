package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.noqapp.android.client.R;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookAppointmentActivity extends BaseActivity implements DatePickerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initActionsViews(true);
        tv_toolbar_title.setText("Book an Appointment");
        HorizontalPicker picker = findViewById(R.id.datePicker);
        picker.setListener(this).init();
        RecyclerView rv_available_date = findViewById(R.id.rv_available_date);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_available_date.setLayoutManager(horizontalLayoutManagaer);
        rv_available_date.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDateSelected(@NonNull final DateTime dateSelected) {
        Log.i("HorizontalPicker", "Selected date is " + dateSelected.toString());
    }

}

