package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.AppointmentDateAdapter;
import com.noqapp.android.client.views.adapters.DependentAdapter;
import com.noqapp.android.client.views.pojos.AppointmentModel;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.Formatter;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookAppointmentActivity extends BaseActivity implements DatePickerListener, AppointmentDateAdapter.OnItemClickListener {
    private Spinner sp_name_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initActionsViews(true);
        tv_toolbar_title.setText("Book an Appointment");
        HorizontalPicker picker = findViewById(R.id.datePicker);
        RecyclerView rv_available_date = findViewById(R.id.rv_available_date);
        sp_name_list = findViewById(R.id.sp_name_list);
        List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
        profileList.add(0, NoQueueBaseActivity.getUserProfile());
        profileList.add(0, new JsonProfile().setName("Select Patient"));
        DependentAdapter adapter = new DependentAdapter(this, profileList);
        sp_name_list.setAdapter(adapter);
        picker.setListener(this).init();
        picker.setDate(new DateTime());

        rv_available_date.setLayoutManager(new GridLayoutManager(this, 3));
        rv_available_date.setItemAnimator(new DefaultItemAnimator());

        List<AppointmentModel> listData = new ArrayList<>();
        BizStoreElastic bizStoreElastic = (BizStoreElastic) getIntent().getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
        if (null != bizStoreElastic) {
            StoreHourElastic storeHourElastic = AppUtilities.getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
            String from = Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getStartHour());
            String to = Formatter.convertMilitaryTo24HourFormat(storeHourElastic.getEndHour());
            ArrayList<String> timeSlot = getTimeSlot(10, from, to);
            boolean temp = true;
            for (int i = 0; i < timeSlot.size(); i++) {
                listData.add(new AppointmentModel().setTime(timeSlot.get(i)).setBooked(temp));
                temp = !temp;
            }

        } else {
            listData.add(new AppointmentModel().setTime("10:00 am").setBooked(true));
            listData.add(new AppointmentModel().setTime("10:05 am").setBooked(false));
            listData.add(new AppointmentModel().setTime("10:10 am").setBooked(true));
            listData.add(new AppointmentModel().setTime("10:15 am").setBooked(false));
            listData.add(new AppointmentModel().setTime("10:20 am").setBooked(false));
        }
        AppointmentDateAdapter appointmentDateAdapter = new AppointmentDateAdapter(listData, this, this);
        rv_available_date.setAdapter(appointmentDateAdapter);

        Button btn_book_appointment = findViewById(R.id.btn_book_appointment);
        btn_book_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background));
                if (sp_name_list.getSelectedItemPosition() == 0) {
                    Toast.makeText(BookAppointmentActivity.this, getString(R.string.error_patient_name_missing), Toast.LENGTH_LONG).show();
                    sp_name_list.setBackground(ContextCompat.getDrawable(BookAppointmentActivity.this, R.drawable.sp_background_red));
                } else {
                    // Process
                }
            }
        });

    }

    @Override
    public void onDateSelected(@NonNull final DateTime dateSelected) {
        Log.i("HorizontalPicker", "Selected date is " + dateSelected.toString());
    }

    @Override
    public void onEventItemClick(AppointmentModel item, View view, int pos) {
        //
    }

    public ArrayList<String> getTimeSlot(int slotMinute, String strFromTime, String strToTime) {
        ArrayList<String> timeSlot = new ArrayList<String>();
        try {
            int fromHour, fromMinute, toHour, toMinute;
            fromHour = Integer.parseInt(strFromTime.split(":")[0]);
            fromMinute = Integer.parseInt(strFromTime.split(":")[1]);

            toHour = Integer.parseInt(strToTime.split(":")[0]);
            toMinute = Integer.parseInt(strToTime.split(":")[1]);

            long slot = slotMinute * 60 * 1000;
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.HOUR_OF_DAY, fromHour);
            calendar2.set(Calendar.MINUTE, fromMinute);

            long currentTime = calendar2.getTimeInMillis();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, toHour);
            calendar1.set(Calendar.MINUTE, toMinute);
            long endTime = calendar1.getTimeInMillis();
            while (currentTime < endTime) {
                DateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeSlot.add(sdfTime.format(new Date(currentTime)));
                currentTime = currentTime + slot;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlot;
    }

}

