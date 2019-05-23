package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
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
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final EventDay jsonQueuedPerson = dataSet.get(listPosition);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.currentItemClick(listPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void currentItemClick(int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
