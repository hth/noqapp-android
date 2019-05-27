package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;

public class AppointmentBookingDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        initActionsViews(true);
        tv_toolbar_title.setText("Booking Detail");
        JsonSchedule jsonSchedule = (JsonSchedule) getIntent().getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
        BizStoreElastic bizStoreElastic = (BizStoreElastic) getIntent().getSerializableExtra(IBConstant.KEY_DATA);
        ImageView iv_main = findViewById(R.id.iv_main);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_degree = findViewById(R.id.tv_degree);
        TextView tv_schedule_time = findViewById(R.id.tv_schedule_time);
        tv_title.setText(bizStoreElastic.getDisplayName());
        AppUtilities.loadProfilePic(iv_main, bizStoreElastic.getDisplayImage(), this);
        tv_degree.setText(AppUtilities.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
        try {
            String date = CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.getScheduleDate()));
            tv_schedule_time.setText(date + " at " + Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getStartTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("data", jsonSchedule.toString());
    }
}
