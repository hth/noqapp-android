package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.applandeo.materialcalendarview.EventDay;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;

import java.util.Date;
import java.util.List;


public class EventListAdapter extends BaseAdapter {
    private Context context;
    private List<EventDay> eventDayList;
    public List<EventDay> getEventDayList() {
        return eventDayList;
    }

    public EventListAdapter(Context context, List<EventDay> eventDayList) {
        this.context = context;
        this.eventDayList = eventDayList;
    }

    public int getCount() {
        return this.eventDayList.size();
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
            view = layoutInflater.inflate(R.layout.list_item_event, parent, false);
            recordHolder.tv_no_of_patient = view.findViewById(R.id.tv_no_of_patient);
            recordHolder.tv_date = view.findViewById(R.id.tv_date);
            recordHolder.card_view = view.findViewById(R.id.card_view);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        JsonSchedule jsonSchedule = (JsonSchedule) eventDayList.get(position).getEventObject();
        recordHolder.tv_date.setText(jsonSchedule.getScheduleDate());
        recordHolder.tv_no_of_patient.setText(String.valueOf(jsonSchedule.getTotalAppointments()));

        try {
            if (new Date().compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.getScheduleDate())) < 0) {
                recordHolder.card_view.setCardBackgroundColor(Color.WHITE);
            } else {
                recordHolder.card_view.setCardBackgroundColor(Color.LTGRAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_date;
        TextView tv_no_of_patient;
        CardView card_view;

        RecordHolder() {
        }
    }
}
