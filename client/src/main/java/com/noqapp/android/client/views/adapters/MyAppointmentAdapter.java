package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.utils.Formatter;

import java.util.List;


public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonSchedule> dataSet;


    public MyAppointmentAdapter(List<JsonSchedule> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_appointment_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        JsonSchedule jsonSchedule = dataSet.get(position);
        holder.tv_title.setText(jsonSchedule.getJsonProfile().getName());
        holder.tv_gender_age.setText(new AppUtilities().calculateAge(jsonSchedule.getJsonProfile().
                getBirthday()) + ", " + jsonSchedule.getJsonProfile().getGender().name());
        holder.tv_customer_mobile.setText("");
        holder.tv_appointment_date.setText(jsonSchedule.getScheduleDate());
        holder.tv_appointment_time.setText(Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getStartTime()));
        holder.tv_appointment_status.setText(jsonSchedule.getAppointmentStatus().getDescription());
        AppUtilities.loadProfilePic(holder.iv_main,jsonSchedule.getJsonProfile().getProfileImage(),context);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void appointmentDetails(JsonSchedule jsonSchedule);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_gender_age;
        private TextView tv_customer_mobile;
        private TextView tv_appointment_time;
        private TextView tv_appointment_date;
        private TextView tv_appointment_status;
        private ImageView iv_main;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_gender_age = itemView.findViewById(R.id.tv_gender_age);
            this.tv_appointment_time = itemView.findViewById(R.id.tv_appointment_time);
            this.tv_appointment_date = itemView.findViewById(R.id.tv_appointment_date);
            this.tv_appointment_status = itemView.findViewById(R.id.tv_appointment_status);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
