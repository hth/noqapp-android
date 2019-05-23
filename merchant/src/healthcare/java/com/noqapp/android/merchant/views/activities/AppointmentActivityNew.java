package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.AppointmentListAdapter;

public class AppointmentActivityNew extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_new);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText("Appointment List");
        RecyclerView rcv_appointments = findViewById(R.id.rcv_appointments);
        rcv_appointments.setHasFixedSize(true);
        int count = 2;
        if (new AppUtils().isTablet(getApplicationContext())) {
            count = 4;
        } else {
            count = 2;
        }
        rcv_appointments.setLayoutManager(new GridLayoutManager(this, count));
        rcv_appointments.setItemAnimator(new DefaultItemAnimator());
        TextView tv_header = findViewById(R.id.tv_header);
        tv_header.setText("Today ("+AppointmentActivity.adapter.getEventDayList().size()+" appointments)");
        AppointmentListAdapter followupAllListAdapter = new AppointmentListAdapter(AppointmentActivity.adapter.getEventDayList(), this, null);
        rcv_appointments.setAdapter(followupAllListAdapter);
    }
}
