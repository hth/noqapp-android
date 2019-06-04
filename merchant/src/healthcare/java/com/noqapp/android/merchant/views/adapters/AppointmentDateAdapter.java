package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.pojos.AppointmentModel;
import com.noqapp.android.merchant.R;

import java.util.List;

public class AppointmentDateAdapter extends RecyclerView.Adapter<AppointmentDateAdapter.MyViewHolder> {
    private final OnItemClickListener listener;
    private List<AppointmentModel> dataSet;
    private Context context;
    private int selectPos = -1;

    public List<AppointmentModel> getDataSet() {
        return dataSet;
    }

    public AppointmentDateAdapter(List<AppointmentModel> data, OnItemClickListener listener, Context context) {
        this.dataSet = data;
        this.listener = listener;
        this.context = context;
        selectPos = -1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_appointment_date, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        AppointmentModel item = dataSet.get(listPosition);
        holder.tv_time.setText(item.getTime());
        if (item.isBooked()) {
            holder.tv_time.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_appointment_booked));
            holder.tv_time.setTextColor(Color.parseColor("#474747"));
        } else {
            if (selectPos != -1 && selectPos == listPosition) {
                holder.tv_time.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_appointment_select));
                holder.tv_time.setTextColor(Color.WHITE);
            } else {
                holder.tv_time.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_appointment_available));
                holder.tv_time.setTextColor(Color.BLACK);
            }

        }

        holder.tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    if (item.isBooked()) {
                        selectPos = -1;
                        listener.onBookedAppointmentSelected();
                        new CustomToast().showToast(context, "This slot is already booked");
                    } else {
                        selectPos = listPosition;
                        listener.onAppointmentSelected(dataSet.get(listPosition), listPosition);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onAppointmentSelected(AppointmentModel item, int pos);
        
        void onBookedAppointmentSelected();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_time;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}

