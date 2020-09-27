package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;

import java.util.Date;
import java.util.List;


public class MyAppointmentAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private List<JsonSchedule> dataSet;


    public MyAppointmentAdapter(List<JsonSchedule> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_appointment_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int position) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        JsonSchedule jsonSchedule = dataSet.get(position);
        holder.tv_title.setText(jsonSchedule.getJsonQueueDisplay().getDisplayName());
        holder.tv_address.setText(AppUtils.getStoreAddress(jsonSchedule.getJsonQueueDisplay().getTown(), jsonSchedule.getJsonQueueDisplay().getArea()));
       try {
           holder.tv_category.setText(MedicalDepartmentEnum.valueOf(jsonSchedule.getJsonQueueDisplay().getBizCategoryId()).getDescription());
       }catch (IllegalArgumentException exp){
           holder.tv_category.setText("");
           Log.e("MyAppointmentAdapter", exp.toString());
       }

       try {
            Date date = CommonHelper.SDF_YYYY_MM_DD.parse(jsonSchedule.getScheduleDate());
            holder.tv_appointment_date.setText(CommonHelper.SDF_DOB_FROM_UI.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_appointment_time.setText(Formatter.convertMilitaryTo12HourFormat(jsonSchedule.getStartTime()));
        holder.tv_appointment_status.setText(jsonSchedule.getAppointmentStatus().getDescription());
        //  AppUtilities.loadProfilePic(holder.iv_main,jsonSchedule.getJsonProfile().getProfileImage(),context);
        holder.card_view.setOnClickListener((View v) -> {
            if (null != listener)
                listener.appointmentDetails(dataSet.get(position));
        });
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
        private TextView tv_category;
        private TextView tv_address;
        private TextView tv_appointment_time;
        private TextView tv_appointment_date;
        private TextView tv_appointment_status;
        private ImageView iv_main;
        private CardView card_view;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_category = itemView.findViewById(R.id.tv_category);
            this.tv_appointment_time = itemView.findViewById(R.id.tv_appointment_time);
            this.tv_appointment_date = itemView.findViewById(R.id.tv_appointment_date);
            this.tv_appointment_status = itemView.findViewById(R.id.tv_appointment_status);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.iv_main = itemView.findViewById(R.id.iv_main);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
