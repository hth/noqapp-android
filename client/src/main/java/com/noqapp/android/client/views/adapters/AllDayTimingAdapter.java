package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.common.beans.JsonHour;

import java.util.List;

public class AllDayTimingAdapter extends BaseAdapter {
    private String NA = "NA";
    private Context context;
    private List<JsonHour> jsonHours;

    public AllDayTimingAdapter(Context context, List<JsonHour> jsonHours) {
        this.context = context;
        this.jsonHours = jsonHours;
    }

    public int getCount() {
        return this.jsonHours.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.listitem_all_day_timing, parent, false);
            recordHolder.tv_time_value = view.findViewById(R.id.tv_time_value);
            recordHolder.tv_lunch_time_value = view.findViewById(R.id.tv_lunch_time_value);
            recordHolder.tv_token_time_value = view.findViewById(R.id.tv_token_time_value);
            recordHolder.tv_appointment_time_value = view.findViewById(R.id.tv_appointment_time_value);
            recordHolder.tv_day = view.findViewById(R.id.tv_day);
            recordHolder.ll_day = view.findViewById(R.id.ll_day);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        
        JsonHour jsonHour = jsonHours.get(position);
        recordHolder.tv_day.setText(AppUtils.getDayName(jsonHour.getDayOfWeek()));
        String storeTime = new AppUtils().formatTodayStoreTiming(context, jsonHour.isDayClosed(),
                jsonHour.getStartHour(), jsonHour.getEndHour());
        recordHolder.tv_time_value.setText(storeTime);

        String lunchTime = new AppUtils().storeLunchTiming(jsonHour.getLunchTimeStart(),
                jsonHour.getLunchTimeEnd());
        recordHolder.tv_lunch_time_value.setText(TextUtils.isEmpty(lunchTime) ? NA: lunchTime);


        String tokenTime = new AppUtils().storeLunchTiming(jsonHour.getTokenAvailableFrom(),
                jsonHour.getTokenNotAvailableFrom());
        recordHolder.tv_token_time_value.setText(TextUtils.isEmpty(tokenTime) ? NA: tokenTime);


        String appointmentTime = new AppUtils().storeLunchTiming(jsonHour.getAppointmentStartHour(),
                jsonHour.getAppointmentStartHour());
        recordHolder.tv_appointment_time_value.setText(TextUtils.isEmpty(appointmentTime) ? NA : appointmentTime);


        return view;
    }

    static class RecordHolder {
        TextView tv_time_value;
        TextView tv_lunch_time_value;
        TextView tv_token_time_value;
        TextView tv_appointment_time_value;
        TextView tv_day;
        LinearLayout ll_day;
        CardView cardview;
        
        RecordHolder() {
        }
    }
    
}
