package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.merchant.R;

import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<EventDay> dataSet;


    public AppointmentListAdapter(List<EventDay> data, Context context, OnItemClickListener listener ) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_appointment_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        JsonSchedule jsonSchedule = (JsonSchedule) dataSet.get(position).getEventObject();
        holder.tv_title.setText(jsonSchedule.getJsonProfile().getName());
        //holder.tv_gender_age.setText("");
        holder.iv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.appointmentReject(dataSet.get(position),position);
                }
            }
        });
        holder.iv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.appointmentAccept(dataSet.get(position),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void appointmentAccept(EventDay item, int pos);
        void appointmentReject(EventDay item, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_gender_age;
        private CardView card_view;
        private ImageView iv_accept;
        private ImageView iv_reject;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_gender_age = itemView.findViewById(R.id.tv_gender_age);
            this.iv_accept = itemView.findViewById(R.id.iv_accept);
            this.iv_reject = itemView.findViewById(R.id.iv_reject);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
