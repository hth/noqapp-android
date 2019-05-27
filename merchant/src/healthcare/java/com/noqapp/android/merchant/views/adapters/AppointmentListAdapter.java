package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.MyViewHolder> {
    private final Context context;
    private final OnItemClickListener listener;
    private List<EventDay> dataSet;


    public AppointmentListAdapter(List<EventDay> data, Context context, OnItemClickListener listener) {
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
        holder.tv_gender_age.setText(new AppUtils().calculateAge(jsonSchedule.getJsonProfile().
                getBirthday()) + ", " + jsonSchedule.getJsonProfile().getGender().name());
        holder.tv_customer_mobile.setText(
                TextUtils.isEmpty(jsonSchedule.getJsonProfile().getPhoneRaw())
                        ? context.getString(R.string.unregister_user)
                        : PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(),
                        jsonSchedule.getJsonProfile().getPhoneRaw()));
        holder.tv_appointment_date.setText(jsonSchedule.getScheduleDate());
        holder.tv_appointment_time.setText(Formatter.convertMilitaryTo24HourFormat(jsonSchedule.getStartTime()));
        holder.tv_appointment_status.setText(jsonSchedule.getAppointmentStatus().getDescription());
        holder.iv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.appointmentReject(dataSet.get(position), position);
                }
            }
        });
        holder.iv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.appointmentAccept(dataSet.get(position), position);
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
        private TextView tv_customer_mobile;
        private TextView tv_appointment_time;
        private TextView tv_appointment_date;
        private TextView tv_appointment_status;
        private CardView card_view;
        private ImageView iv_accept;
        private ImageView iv_reject;


        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_gender_age = itemView.findViewById(R.id.tv_gender_age);
            this.tv_appointment_time = itemView.findViewById(R.id.tv_appointment_time);
            this.tv_appointment_date = itemView.findViewById(R.id.tv_appointment_date);
            this.tv_appointment_status = itemView.findViewById(R.id.tv_appointment_status);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.iv_accept = itemView.findViewById(R.id.iv_accept);
            this.iv_reject = itemView.findViewById(R.id.iv_reject);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
