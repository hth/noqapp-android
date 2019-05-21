package com.applandeo.materialcalendarview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.R;

import java.util.List;


public class EventListAdapter extends BaseAdapter {
    private Context context;
    private List<EventDay> eventDayList;

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
            // recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        EventDay eventDay = eventDayList.get(position);
        recordHolder.tv_date.setText(eventDay.getAppointmentInfo().getAppointmentDate());
        recordHolder.tv_no_of_patient.setText(eventDay.getAppointmentInfo().getNoOfPatient());
        return view;
    }

    static class RecordHolder {
        TextView tv_date;
        TextView tv_no_of_patient;
        //CardView cardview;
        RecordHolder() {
        }
    }
}
